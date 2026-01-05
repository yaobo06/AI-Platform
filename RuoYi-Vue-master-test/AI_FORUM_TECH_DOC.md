# AI 论坛系统技术文档

> 详细的技术实现文档，包含 DeepSeek、RAG、Redis 的使用详解和前端实现巧思

## 📚 目录

- [一、DeepSeek 的使用](#一deepseek-的使用)
  - [1.1 搜索关键词扩展（智能搜索模式）](#11-搜索关键词扩展智能搜索模式)
  - [1.2 标题解析（主标题/副标题识别）](#12-标题解析主标题副标题识别)
  - [1.3 DeepSeek 搜索模式](#13-deepseek-搜索模式)
  - [1.4 DeepSeek 配置](#14-deepseek-配置)
- [二、RAG（检索增强生成）的使用](#二rag检索增强生成的使用)
  - [2.1 RAG 架构](#21-rag-架构)
  - [2.2 向量生成（EmbeddingService）](#22-向量生成embeddingservice)
  - [2.3 向量存储（Milvus）](#23-向量存储milvus)
  - [2.4 RAG 搜索模式](#24-rag-搜索模式)
- [三、Redis 的使用](#三redis-的使用)
  - [3.1 标题解析结果缓存](#31-标题解析结果缓存)
  - [3.2 验证码缓存](#32-验证码缓存)
  - [3.3 Redis 工具类](#33-redis-工具类)
- [四、前端实现巧思](#四前端实现巧思)
  - [4.1 搜索建议（防抖 + 实时）](#41-搜索建议防抖--实时)
  - [4.2 标题显示优化（主标题/副标题分离）](#42-标题显示优化主标题副标题分离)
  - [4.3 搜索模式切换](#43-搜索模式切换)
  - [4.4 搜索结果展示](#44-搜索结果展示)
- [五、整体数据流程](#五整体数据流程)
  - [5.1 智能搜索模式完整流程](#51-智能搜索模式完整流程)
  - [5.2 标题解析流程](#52-标题解析流程)
- [六、性能优化总结](#六性能优化总结)
- [七、配置说明](#七配置说明)
- [八、使用示例](#八使用示例)

---

## 一、DeepSeek 的使用

### 1.1 搜索关键词扩展（智能搜索模式）

**位置**：`SearchServiceImpl.searchWithHybrid()`

**功能**：当直接数据库搜索结果不足时，调用 DeepSeek API 分析用户搜索意图，生成扩展关键词，提高搜索准确性。

**流程**：
```
用户搜索 "ChatGPT" 
  ↓
直接数据库搜索（找到5条结果）
  ↓
结果不足，调用 DeepSeek API
  ↓
DeepSeek 分析搜索意图，返回扩展关键词：
"ChatGPT, GPT-4, OpenAI, 大语言模型, AI对话"
  ↓
使用扩展关键词进行多关键词数据库搜索
  ↓
合并结果并去重
```

**代码实现**：
```java
// 1. 调用 DeepSeek API 扩展查询
String expandedQuery = generateExpandedQuery(query, ragPromptTemplate, 0.4, 120);
// 返回："ChatGPT, GPT-4, OpenAI, 大语言模型, AI对话"

// 2. 拆分关键词
String[] keywords = expandedQuery.split("[,，、\\s]+");
// ["ChatGPT", "GPT-4", "OpenAI", "大语言模型", "AI对话"]

// 3. 多关键词搜索（OR 查询）
for (String keyword : keywords) {
    // 搜索标题
    postMapper.selectPostList(searchTitle);
    // 搜索内容
    postMapper.selectPostList(searchContent);
}
```

**DeepSeek API 调用细节**：
```java
// 请求体
{
  "model": "deepseek-chat",
  "messages": [
    {
      "role": "user",
      "content": "用户搜索：ChatGPT\n\n请理解用户的搜索意图，并生成3-5个语义相关的搜索关键词（用逗号分隔）。这些关键词应该能够匹配论坛中相关的帖子内容。只返回关键词，不要其他解释。"
    }
  ],
  "temperature": 0.4,  // 较低温度，更确定性
  "max_tokens": 120    // 限制输出长度
}

// 响应
{
  "choices": [{
    "message": {
      "content": "ChatGPT, GPT-4, OpenAI, 大语言模型, AI对话"
    }
  }]
}
```

### 1.2 标题解析（主标题/副标题识别）

**位置**：`SearchServiceImpl.parseTitleWithDeepSeek()`

**功能**：自动识别帖子标题的主标题和副标题，用于"大家都在看"功能的优化显示。

**流程**：
```
帖子标题："ChatGPT使用指南：从入门到精通"
  ↓
检查 Redis 缓存：title:parse:ChatGPT使用指南：从入门到精通
  ↓
缓存未命中，调用 DeepSeek API
  ↓
DeepSeek 分析标题结构，返回：
"主标题：ChatGPT使用指南
副标题：从入门到精通"
  ↓
解析结果，缓存到 Redis（7天过期）
  ↓
返回：{
  "mainTitle": "ChatGPT使用指南",
  "subTitle": "从入门到精通"
}
```

**批量优化**：
```java
// 批量解析标题（用于"大家都在看"功能）
Map<String, Map<String, String>> batchParseTitleWithDeepSeek(List<String> titles) {
    // 1. 批量构建缓存 key
    List<String> cacheKeys = titles.stream()
        .map(title -> "title:parse:" + title)
        .collect(Collectors.toList());
    
    // 2. 批量查询 Redis（MGET，一次网络往返）
    List<Map<String, String>> cachedValues = redisCache.getMultiCacheObject(cacheKeys);
    
    // 3. 区分缓存命中和未命中
    for (未命中的标题) {
        // 先使用冒号检测快速返回（降级方案）
        Map<String, String> fallback = parseTitleWithColon(title);
        
        // 异步调用 DeepSeek 更新缓存（不阻塞）
        new Thread(() -> parseTitleWithDeepSeek(title)).start();
    }
}
```

**DeepSeek API 调用**：
```java
// Prompt 模板
"请分析以下帖子标题，识别出主标题和副标题。

标题：ChatGPT使用指南：从入门到精通

如果标题可以分成主标题和副标题，请按以下格式返回：
主标题：xxx
副标题：xxx

如果标题没有明显的副标题，只返回：
主标题：xxx

只返回主标题和副标题的内容，不要其他解释。"

// 响应
"主标题：ChatGPT使用指南
副标题：从入门到精通"
```

### 1.3 DeepSeek 搜索模式（纯 DeepSeek 关键词提取）

**位置**：`SearchServiceImpl.searchWithDeepSeek()`

**功能**：用户选择"DeepSeek 搜索"模式时，直接调用 DeepSeek API 提取关键词，然后进行数据库搜索。

**与智能搜索的区别**：
- **智能搜索**：先数据库搜索，结果不足才调用 DeepSeek
- **DeepSeek 搜索**：直接调用 DeepSeek，然后数据库搜索

### 1.4 DeepSeek 配置

**配置文件**：`application.yml`

```yaml
deepseek:
  api:
    key: "your-api-key"  # DeepSeek API Key
    url: "https://api.deepseek.com/v1/chat/completions"
  prompt:
    # 搜索关键词扩展提示词
    deepseek: "用户搜索关键词：%s\n\n请分析用户的搜索意图，并提取3-5个相关的搜索关键词（用逗号分隔）。关键词应该与论坛帖子相关，包括技术、设计、生活等话题。只返回关键词，不要其他解释。"
    
    # RAG 模式提示词
    rag: "用户搜索：%s\n\n请理解用户的搜索意图，并生成3-5个语义相关的搜索关键词（用逗号分隔）。这些关键词应该能够匹配论坛中相关的帖子内容。只返回关键词，不要其他解释。"
    
    # 标题解析提示词
    title-parse: |
      请分析以下帖子标题，识别出主标题和副标题。
      
      标题：%s
      
      如果标题可以分成主标题和副标题，请按以下格式返回：
      主标题：xxx
      副标题：xxx
      
      如果标题没有明显的副标题，只返回：
      主标题：xxx
      
      只返回主标题和副标题的内容，不要其他解释。
```

---

## 二、RAG（检索增强生成）的使用

### 2.1 RAG 架构

**整体流程**：
```
用户搜索 "如何优化Spring Boot性能"
  ↓
EmbeddingService 生成查询向量（512维）
  ↓
VectorDBService 在 Milvus 中搜索相似向量
  ↓
返回相似度最高的帖子ID列表
  ↓
从数据库查询帖子详情
  ↓
结合热度分数排序
  ↓
返回最终结果
```

### 2.2 向量生成（EmbeddingService）

**位置**：`EmbeddingServiceImpl.generateEmbedding()`

**功能**：将文本转换为高维向量，用于语义相似度计算。

**流程**：
```java
// 1. 调用 Embeddings API（DeepSeek 或本地 BGE 服务）
POST https://api.deepseek.com/v1/embeddings
{
  "model": "deepseek-embedding",
  "input": "如何优化Spring Boot性能"
}

// 2. 返回向量（512维或768维）
{
  "data": [{
    "embedding": [0.123, -0.456, 0.789, ...]  // 512个浮点数
  }]
}

// 3. 转换为 float[] 数组
float[] vector = new float[512];
for (int i = 0; i < 512; i++) {
    vector[i] = (float) embeddingNode.get(i).asDouble();
}
```

**配置**：
```yaml
embeddings:
  api:
    key: ""  # 本地 BGE 服务不需要 API Key
  rag:
    enabled: true  # 启用向量检索
```

### 2.3 向量存储（Milvus）

**位置**：`VectorDBServiceImpl`

**初始化流程**：
```java
@PostConstruct
public void init() {
    // 1. 连接 Milvus Standalone
    client = new MilvusServiceClient(ConnectParam.newBuilder()
        .withHost("localhost")
        .withPort(19530)
        .build());
    
    // 2. 检查集合是否存在
    if (!hasCollection()) {
        createCollection();  // 创建 posts_vectors 集合
    }
    
    // 3. 确保索引存在（IVF_FLAT 索引，用于快速搜索）
    ensureIndex();
}
```

**集合结构**：
```java
// 字段1：post_id (Int64, Primary Key)
FieldType idField = FieldType.newBuilder()
    .withName("post_id")
    .withDataType(DataType.Int64)
    .withPrimaryKey(true)
    .build();

// 字段2：vector (FloatVector, 512维)
FieldType vectorField = FieldType.newBuilder()
    .withName("vector")
    .withDataType(DataType.FloatVector)
    .withDimension(512)  // 与 BGE-small-zh 模型匹配
    .build();
```

**向量插入**：
```java
// 当帖子创建或更新时，生成向量并插入 Milvus
float[] vector = embeddingService.generateEmbedding(post.getTitle() + " " + post.getContent());
vectorDBService.insertVector(post.getId(), vector);
```

**向量搜索**：
```java
// 1. 生成查询向量
float[] queryVector = embeddingService.generateEmbedding("如何优化Spring Boot性能");

// 2. 在 Milvus 中搜索（TopK=20）
SearchParam searchParam = SearchParam.newBuilder()
    .withCollectionName("posts_vectors")
    .withMetricType(MetricType.IP)  // 内积相似度
    .withTopK(20)
    .withVectors(Collections.singletonList(queryVectorList))
    .build();

// 3. 返回相似度最高的帖子ID和分数
List<SearchResult> results = [
    {postId: 123, score: 0.95},  // 相似度 95%
    {postId: 456, score: 0.87},  // 相似度 87%
    ...
]
```

**相似度计算**：
```java
// IP（内积）相似度：值越大越相似
// 如果向量已归一化，IP 等价于余弦相似度（范围 -1 到 1）
if ("IP".equalsIgnoreCase(metricType)) {
    // 转换为 0-1 范围
    similarity = (score + 1.0f) / 2.0f;  // -1~1 映射到 0~1
}

// L2（欧氏距离）：值越小越相似
else {
    similarity = 1.0f / (1.0f + score);  // 距离转换为相似度
}
```

### 2.4 RAG 搜索模式

**位置**：`SearchServiceImpl.searchWithRAG()`

**流程**：
```java
// 1. 检查 RAG 是否可用
if (!vectorDBService.isAvailable()) {
    // 降级到关键词搜索
    return searchWithDeepSeekAPI(query, limit, ragPromptTemplate, 0.5, 150);
}

// 2. 生成查询向量
float[] queryVector = embeddingService.generateEmbedding(query, false);

// 3. 向量搜索
List<Post> rankedPosts = searchWithVector(queryVector, limit);

// 4. 如果向量搜索无结果，降级到数据库搜索
if (rankedPosts.isEmpty()) {
    return searchWithDatabase(query, limit);
}
```

**混合排序**：
```java
// 结合向量相似度和热度分数
double finalScore = similarity * 0.7 + popularityScore * 0.3;
// 70% 权重给语义相似度，30% 权重给帖子热度
```

**配置**：
```yaml
# Milvus 配置
milvus:
  lite:
    enabled: true   # 启用 Milvus（需要先启动 Docker 服务）
    host: localhost
    port: 19530
    collection-name: posts_vectors
    dimension: 512   # 向量维度（与 BGE-small-zh 匹配）
    metric-type: IP  # L2 或 IP（推荐 IP，但需要向量归一化）
```

---

## 三、Redis 的使用

### 3.1 标题解析结果缓存

**缓存键格式**：`title:parse:{原始标题}`

**缓存值**：
```json
{
  "mainTitle": "ChatGPT使用指南",
  "subTitle": "从入门到精通"
}
```

**过期时间**：7天

**实现**：
```java
// 1. 查询缓存
String cacheKey = "title:parse:" + title;
Map<String, String> cachedResult = redisCache.getCacheObject(cacheKey);

// 2. 缓存未命中，调用 DeepSeek
if (cachedResult == null) {
    Map<String, String> result = callDeepSeekAPI(title);
    
    // 3. 缓存结果
    redisCache.setCacheObject(cacheKey, result, 7, TimeUnit.DAYS);
}
```

**批量优化**：
```java
// 批量查询（MGET，一次网络往返）
List<String> cacheKeys = titles.stream()
    .map(title -> "title:parse:" + title)
    .collect(Collectors.toList());

List<Map<String, String>> cachedValues = redisCache.getMultiCacheObject(cacheKeys);
// 返回顺序与 cacheKeys 一致，不存在的 key 返回 null
```

### 3.2 验证码缓存

**位置**：`ForumController.getCaptchaImage()`

**缓存键格式**：`captcha_codes:{uuid}`

**缓存值**：验证码字符串（如 "A3B7"）

**过期时间**：2分钟

**实现**：
```java
String verifyKey = "captcha_codes:" + uuid;
redisCache.setCacheObject(verifyKey, code, 2, TimeUnit.MINUTES);
```

### 3.3 Redis 工具类

**位置**：`RedisCache.java`

**核心方法**：
```java
// 1. 设置缓存（带过期时间）
redisCache.setCacheObject(key, value, timeout, TimeUnit.DAYS);

// 2. 获取缓存
T value = redisCache.getCacheObject(key);

// 3. 批量获取缓存（MGET 优化）
List<T> values = redisCache.getMultiCacheObject(keys);

// 4. 删除缓存
redisCache.deleteObject(key);

// 5. 检查 key 是否存在
Boolean exists = redisCache.hasKey(key);
```

**底层实现**：
```java
// 使用 Spring Data Redis
@Autowired
public RedisTemplate redisTemplate;

// 设置缓存
public <T> void setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
    redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
}

// 批量获取（MGET）
public <T> List<T> getMultiCacheObject(Collection<String> keys) {
    ValueOperations<String, T> operation = redisTemplate.opsForValue();
    return operation.multiGet(keys);  // Redis MGET 命令
}
```

---

## 四、前端实现巧思

### 4.1 搜索建议（防抖 + 实时）

**位置**：`search.vue` 的 `querySearchSuggestions()`

**功能**：用户输入时实时显示搜索建议，使用防抖机制减少 API 调用。

**实现**：
```javascript
querySearchSuggestions(queryString, cb) {
  // 1. 清除之前的定时器（防抖核心）
  if (this.searchSuggestTimer) {
    clearTimeout(this.searchSuggestTimer)
  }
  
  // 2. 空输入时显示热门搜索
  if (!queryString || queryString.trim() === '') {
    const suggestions = this.hotSearches.slice(0, 5).map(tag => ({
      value: tag,
      label: tag
    }))
    cb(suggestions)
    return
  }
  
  // 3. 防抖：延迟300ms后执行搜索
  this.searchSuggestTimer = setTimeout(() => {
    // 调用搜索API获取建议（限制返回5条）
    searchPosts(query, this.searchMode, 1, 5)
      .then(response => {
        const suggestions = response.data.results.map(item => ({
          value: item.title,  // 使用帖子标题作为建议
          label: item.title
        }))
        cb(suggestions)
      })
  }, 300)  // 300ms 防抖延迟
}
```

**防抖原理**：
```
用户输入: "s" → 创建定时器（300ms）
用户输入: "sp" → 清除上一个，创建新定时器（300ms）
用户输入: "spr" → 清除上一个，创建新定时器（300ms）
用户停止输入 300ms → 执行搜索 API 调用
```

**Element UI Autocomplete 集成**：
```vue
<el-autocomplete
  v-model="searchQuery"
  :fetch-suggestions="querySearchSuggestions"
  @select="handleSearchSelect"
  @keyup.enter.native="handleSearch"
  :trigger-on-focus="true">
</el-autocomplete>
```

### 4.2 标题显示优化（主标题/副标题分离）

**位置**：`index.vue` 的 "大家都在看" 部分

**功能**：将帖子标题分为主标题和副标题，优化显示效果。

**实现**：
```vue
<div class="trending-title-wrapper">
  <span class="trending-title-main">{{ post.mainTitle || post.title }}</span>
  <span v-if="post.subTitle" class="trending-title-sub">{{ post.subTitle }}</span>
</div>
```

**CSS 样式**：
```scss
.trending-title-main {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;  // 最多显示2行
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.trending-title-sub {
  font-size: 12px;
  font-weight: 400;
  color: #909399;  // 淡灰色
  padding-left: 8px;
  border-left: 2px solid #e5e7eb;  // 左边框装饰
  opacity: 0.8;
  -webkit-line-clamp: 1;  // 最多显示1行
}
```

**数据获取**：
```javascript
// 后端返回的数据结构
{
  "id": 123,
  "title": "ChatGPT使用指南：从入门到精通",  // 原始标题
  "mainTitle": "ChatGPT使用指南",           // DeepSeek 解析的主标题
  "subTitle": "从入门到精通",              // DeepSeek 解析的副标题
  "authorName": "张三"
}
```

### 4.3 搜索模式切换

**位置**：`search.vue`

**功能**：用户可以在"智能搜索"和"DeepSeek 搜索"之间切换。

**实现**：
```vue
<el-radio-group v-model="searchMode" @change="handleModeChange">
  <el-radio-button label="hybrid">智能搜索</el-radio-button>
  <el-radio-button label="deepseek">DeepSeek 搜索</el-radio-button>
</el-radio-group>
```

**模式切换逻辑**：
```javascript
handleModeChange() {
  if (this.hasSearched) {
    // 如果已经搜索过，切换模式后自动重新搜索
    this.handleSearch()
  }
}
```

### 4.4 搜索结果展示

**位置**：`search.vue` 的 `results-list`

**功能**：美观地展示搜索结果，包括标题、作者、内容摘要、统计数据等。

**实现**：
```vue
<div class="result-item" @click="viewPost(post)">
  <div class="result-header">
    <h3 class="result-title">{{ post.title }}</h3>
    <div class="result-meta">
      <span class="author">
        <img :src="post.authorAvatarUrl || defaultAvatar" />
        {{ post.authorName || '匿名用户' }}
      </span>
      <span class="time">{{ formatTime(post.createdAt) }}</span>
    </div>
  </div>
  <div class="result-content">
    <p>{{ getExcerpt(post.content) }}</p>  <!-- 截取前150字符 -->
  </div>
  <div class="result-footer">
    <div class="result-stats">
      <span><i class="el-icon-view"></i>{{ post.viewCount }}</span>
      <span><i class="el-icon-chat-line-round"></i>{{ post.commentCount }}</span>
      <span><i class="el-icon-star-on"></i>{{ post.likeCount }}</span>
    </div>
  </div>
</div>
```

**内容截取**：
```javascript
getExcerpt(content) {
  if (!content) return ''
  const text = content.replace(/<[^>]*>/g, '')  // 去除HTML标签
  return text.length > 150 ? text.substring(0, 150) + '...' : text
}
```

---

## 五、整体数据流程

### 5.1 智能搜索模式完整流程

```
用户输入 "ChatGPT" 并点击搜索
  ↓
前端：search.vue → handleSearch()
  ↓
API 调用：GET /forum/search?q=ChatGPT&mode=hybrid&page=1&size=10
  ↓
后端：SearchController.search()
  ↓
SearchServiceImpl.searchWithHybrid()
  ↓
【步骤1】直接数据库搜索
  SELECT * FROM posts WHERE title LIKE '%ChatGPT%' OR content LIKE '%ChatGPT%'
  → 找到 5 条结果
  ↓
【步骤2】结果不足，调用 DeepSeek API 扩展查询
  POST https://api.deepseek.com/v1/chat/completions
  → 返回："ChatGPT, GPT-4, OpenAI, 大语言模型, AI对话"
  ↓
【步骤3】使用扩展关键词进行多关键词搜索
  SELECT * FROM posts WHERE title LIKE '%GPT-4%' OR ...
  → 找到 10 条额外结果
  ↓
【步骤4】向量检索（如果 RAG 启用）
  EmbeddingService.generateEmbedding("ChatGPT")
  → 生成 512维向量
  ↓
  VectorDBService.search(queryVector, 20)
  → 在 Milvus 中搜索相似向量
  → 返回相似度最高的 20 个帖子ID
  ↓
【步骤5】合并所有结果，去重并按分数排序
  - 直接搜索结果（权重高）
  - 扩展搜索结果（权重中）
  - 向量搜索结果（权重中，结合相似度和热度）
  ↓
【步骤6】分页处理
  PaginationUtils.paginateList(results, page, size)
  ↓
返回给前端
  ↓
前端渲染搜索结果列表
```

### 5.2 标题解析流程（"大家都在看"）

```
用户访问论坛首页
  ↓
前端：index.vue → fetchTrendingPosts()
  ↓
API 调用：GET /forum/trending?page=1&size=5
  ↓
后端：ForumController.getTrending()
  ↓
PostService.listTrending() → 计算热度分数，排序
  → 返回前5个热门帖子
  ↓
批量解析标题：searchService.batchParseTitleWithDeepSeek(titles)
  ↓
【步骤1】批量构建缓存 key
  ["title:parse:标题1", "title:parse:标题2", ...]
  ↓
【步骤2】批量查询 Redis（MGET，一次网络往返）
  redisCache.getMultiCacheObject(cacheKeys)
  → 返回缓存结果列表
  ↓
【步骤3】区分缓存命中和未命中
  - 缓存命中：直接使用缓存结果
  - 缓存未命中：
    a. 先使用冒号检测快速返回（降级方案）
    b. 异步调用 DeepSeek 更新缓存（不阻塞）
  ↓
【步骤4】返回解析结果
  {
    "title1": {"mainTitle": "...", "subTitle": "..."},
    "title2": {"mainTitle": "...", "subTitle": "..."}
  }
  ↓
前端显示主标题和副标题（分离显示，副标题淡色+左边框）
```

---

## 六、性能优化总结

### 6.1 Redis 缓存优化

- ✅ **标题解析结果缓存**（7天过期）
- ✅ **批量查询（MGET）**减少网络往返
- ✅ **异步更新缓存**（不阻塞主流程）

### 6.2 DeepSeek API 优化

- ✅ **防抖处理**（前端300ms）
- ✅ **批量处理**（标题解析）
- ✅ **降级方案**（冒号检测）

### 6.3 RAG 向量检索优化

- ✅ **索引优化**（IVF_FLAT）
- ✅ **混合排序**（相似度 + 热度）
- ✅ **降级机制**（RAG 不可用时降级到关键词搜索）

### 6.4 前端优化

- ✅ **防抖搜索建议**
- ✅ **懒加载分页**
- ✅ **内容截取**（避免过长）
- ✅ **响应式设计**

---

## 七、配置说明

### 7.1 完整配置示例

```yaml
# DeepSeek 配置
deepseek:
  api:
    key: "your-deepseek-api-key"
    url: "https://api.deepseek.com/v1/chat/completions"
  prompt:
    deepseek: "用户搜索关键词：%s\n\n请分析用户的搜索意图，并提取3-5个相关的搜索关键词（用逗号分隔）。关键词应该与论坛帖子相关，包括技术、设计、生活等话题。只返回关键词，不要其他解释。"
    rag: "用户搜索：%s\n\n请理解用户的搜索意图，并生成3-5个语义相关的搜索关键词（用逗号分隔）。这些关键词应该能够匹配论坛中相关的帖子内容。只返回关键词，不要其他解释。"
    title-parse: |
      请分析以下帖子标题，识别出主标题和副标题。
      
      标题：%s
      
      如果标题可以分成主标题和副标题，请按以下格式返回：
      主标题：xxx
      副标题：xxx
      
      如果标题没有明显的副标题，只返回：
      主标题：xxx
      
      只返回主标题和副标题的内容，不要其他解释。

# Embeddings 配置
embeddings:
  api:
    key: ""  # 本地 BGE 服务不需要 API Key
  rag:
    enabled: true  # 启用向量检索

# Milvus 配置
milvus:
  lite:
    enabled: true   # 启用 Milvus（需要先启动 Docker 服务）
    host: localhost
    port: 19530
    collection-name: posts_vectors
    dimension: 512   # 向量维度（与 BGE-small-zh 匹配）
    metric-type: IP  # L2 或 IP（推荐 IP，但需要向量归一化）

# 搜索排序配置
search:
  ranking:
    like-weight: 0.6    # 点赞权重
    comment-weight: 0.4  # 评论权重
```

### 7.2 环境要求

- **Java**: 17+
- **Redis**: 用于缓存
- **Milvus**: 用于向量存储（可选，如果启用 RAG）
- **DeepSeek API Key**: 用于 AI 功能

---

## 八、使用示例

### 8.1 搜索功能

**前端调用**：
```javascript
// 智能搜索
searchPosts('ChatGPT', 'hybrid', 1, 10)
  .then(response => {
    console.log('搜索结果:', response.data.results)
  })

// DeepSeek 搜索
searchPosts('ChatGPT', 'deepseek', 1, 10)
  .then(response => {
    console.log('搜索结果:', response.data.results)
  })
```

**后端处理**：
```java
// SearchController
@GetMapping
public AjaxResult search(@RequestParam("q") String q,
                        @RequestParam(defaultValue = "hybrid") String mode,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size) {
    List<Post> list = searchService.search(q, mode, size * 2);
    List<Post> paginatedList = PaginationUtils.paginateList(list, page, size);
    return success().put("data", paginatedList);
}
```

### 8.2 标题解析

**前端调用**：
```javascript
// 获取热门帖子（自动解析标题）
getTrendingPosts(1, 5)
  .then(response => {
    response.data.records.forEach(post => {
      console.log('主标题:', post.mainTitle)
      console.log('副标题:', post.subTitle)
    })
  })
```

**后端处理**：
```java
// ForumController
@GetMapping("/trending")
public AjaxResult getTrending(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size) {
    PageInfo<Post> pageInfo = postService.listTrending(page, size);
    
    // 批量解析标题
    List<String> titles = pageInfo.getList().stream()
        .map(Post::getTitle)
        .collect(Collectors.toList());
    
    Map<String, Map<String, String>> titleParseResults = 
        searchService.batchParseTitleWithDeepSeek(titles);
    
    // 构建返回结果
    // ...
}
```

### 8.3 搜索建议

**前端实现**：
```javascript
// 实时搜索建议（防抖300ms）
querySearchSuggestions(queryString, cb) {
  if (this.searchSuggestTimer) {
    clearTimeout(this.searchSuggestTimer)
  }
  
  this.searchSuggestTimer = setTimeout(() => {
    searchPosts(queryString, this.searchMode, 1, 5)
      .then(response => {
        const suggestions = response.data.results.map(item => ({
          value: item.title,
          label: item.title
        }))
        cb(suggestions)
      })
  }, 300)
}
```

---

## 📝 总结

本系统集成了 **DeepSeek AI**、**RAG 向量检索** 和 **Redis 缓存**，实现了智能搜索、标题解析和性能优化等功能。通过多层缓存、批量处理和降级机制，确保了系统的高性能和稳定性。

**核心特性**：
- 🚀 智能搜索（关键词扩展）
- 🎯 语义搜索（RAG 向量检索）
- ⚡ 性能优化（Redis 缓存）
- 🎨 前端优化（防抖、实时建议）

**学习建议**：
1. 先理解整体架构和数据流程
2. 深入学习每个模块的实现细节
3. 实践配置和调试
4. 根据需求进行扩展和优化

---

**文档版本**: v1.0  
**最后更新**: 2025-01-24

