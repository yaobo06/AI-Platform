# AI Platform 启动指南

## 📋 项目概述

AI Platform 是基于 RuoYi-Vue 框架开发的智能论坛平台，集成了：
- **论坛功能**：帖子、评论、点赞、收藏
- **智能搜索**：数据库搜索、DeepSeek AI 搜索、RAG 向量检索
- **用户系统**：JWT 认证、用户个人中心

## 🚀 快速启动

### 前置要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- Docker（用于 Milvus 向量数据库）

### 第一步：数据库初始化

1. 创建数据库 `aidb`
2. 执行基础 SQL 脚本：
   ```bash
   mysql -u用户名 -p密码 aidb < sql/ry_20250417.sql
   mysql -u用户名 -p密码 aidb < sql/quartz.sql
   mysql -u用户名 -p密码 aidb < sql/ai_models.sql
   ```

3. 执行数据迁移（从 myforum 到 aidb）：
   ```bash
   mysql -u用户名 -p密码 aidb < sql/migrate_all_in_one.sql
   ```

### 第二步：配置 DeepSeek API

编辑 `ruoyi-admin/src/main/resources/application.yml`：

```yaml
deepseek:
  api:
    url: https://api.deepseek.com/v1/chat/completions
    key: your-deepseek-api-key  # 替换为你的 DeepSeek API Key
  embeddings:
    url: http://localhost:8083/embeddings  # 本地 BGE 服务地址
    model: BAAI/bge-small-zh
    dimension: 512
```

**DeepSeek API Key 获取**：
1. 访问 [DeepSeek 官网](https://www.deepseek.com/)
2. 注册账号并获取 API Key
3. 将 Key 填入配置文件

### 第三步：启动 Milvus（RAG 向量数据库）

#### 方式1：使用启动脚本（推荐）

```bash
cd RuoYi-Vue-master-test
./milvus-start.sh
```

#### 方式2：使用 Docker Compose

```bash
docker-compose -f docker-compose-milvus.yml up -d
```

#### 验证 Milvus 运行状态

```bash
# 检查容器状态
docker ps | grep milvus

# 应该看到三个容器：
# - milvus-etcd
# - milvus-minio
# - milvus-standalone
```

**Milvus 服务信息**：
- 主服务端口：`19530`
- MinIO 控制台：http://localhost:9001
- 用户名/密码：`minioadmin/minioadmin`

### 第四步：启动 Embedding 服务（可选，用于 RAG）

#### 选项1：使用本地 BGE 服务（推荐）

启动本地 BGE Embedding 服务（端口 8083）：

```bash
# 需要先安装 Python 和依赖
pip install transformers torch sentence-transformers flask

# 启动 BGE 服务（示例）
python -m flask run --port=8083
```

#### 选项2：使用在线 API

在 `application.yml` 中配置：

```yaml
deepseek:
  embeddings:
    url: https://api.deepseek.com/v1/embeddings
    model: deepseek-embedding
    dimension: 768
```

**注意**：如果 Embedding 服务不可用，RAG 功能会自动降级到数据库搜索。

### 第五步：配置数据库和 Redis

编辑 `ruoyi-admin/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/aidb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: 你的数据库用户名
    password: 你的数据库密码

  redis:
    host: 你的Redis地址
    port: 6379
    password: 你的Redis密码
```

### 第六步：编译和启动后端

```bash
# 进入项目目录
cd RuoYi-Vue-master-test

# 编译项目
mvn clean install -DskipTests

# 启动应用
cd ruoyi-admin
mvn spring-boot:run

# 或者使用脚本
./ry.sh
```

**后端服务地址**：http://localhost:8080

### 第七步：启动前端

```bash
# 进入前端目录
cd ai-platform-web

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

**前端服务地址**：http://localhost:80（或配置的端口）

## 🔧 功能配置说明

### 1. 搜索模式配置

在 `application.yml` 中配置搜索权重：

```yaml
search:
  ranking:
    like-weight: 0.6    # 点赞权重
    comment-weight: 0.4  # 评论权重
```

### 2. Milvus 向量数据库配置

```yaml
milvus:
  lite:
    enabled: true          # 启用 Milvus
    host: localhost        # Milvus 服务地址
    port: 19530            # Milvus 服务端口
    collection-name: posts_vectors  # 集合名称
    dimension: 512         # 向量维度（与 BGE-small-zh 匹配）
    metric-type: IP        # 相似度度量方法（IP 或 L2）
```

### 3. JWT 用户认证配置

```yaml
jwt:
  secret: your-secret-key-here  # 生产环境请修改
  expire-minutes: 120           # Token 过期时间（分钟）
```

## 📝 功能说明

### 智能搜索功能

1. **数据库搜索（db）**：快速关键词匹配
2. **DeepSeek 搜索（deepseek）**：AI 关键词扩展搜索
3. **RAG 向量检索（rag）**：语义相似度搜索
4. **智能混合搜索（hybrid）**：结合多种搜索方式

### 用户系统

- **用户登录**：`/user/login`
- **管理员登录**：`/login` 或 `/admin/login`
- **用户注册**：`/user/register`

### 论坛功能

- 帖子发布、编辑、删除
- 评论和回复
- 点赞和收藏
- 个人中心（我的发帖、点赞、收藏、评论）

## 🐛 常见问题

### 1. Milvus 连接失败

**症状**：日志显示 "Milvus 初始化失败"

**解决方案**：
```bash
# 检查 Docker 是否运行
docker ps

# 检查 Milvus 容器状态
docker ps | grep milvus

# 重启 Milvus
./milvus-stop.sh
./milvus-start.sh
```

### 2. Embedding 服务不可用

**症状**：RAG 搜索降级到数据库搜索

**解决方案**：
- 检查 Embedding 服务是否启动
- 检查 `application.yml` 中的 `embeddings.url` 配置
- 访问 `/forum/embedding/status` 查看服务状态

### 3. DeepSeek API 调用失败

**症状**：DeepSeek 搜索模式不可用

**解决方案**：
- 检查 API Key 是否正确
- 检查网络连接
- 查看 DeepSeek API 余额

### 4. 数据库连接失败

**症状**：应用启动失败，提示数据库连接错误

**解决方案**：
- 检查数据库是否启动
- 检查 `application.yml` 中的数据库配置
- 确认数据库 `aidb` 已创建

## 📊 服务端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 后端服务 | 8080 | Spring Boot 应用 |
| 前端服务 | 80 | Vue 前端应用 |
| Milvus | 19530 | 向量数据库主服务 |
| Milvus MinIO | 9000, 9001 | 对象存储服务 |
| Milvus etcd | 2379 | 元数据存储 |
| Embedding 服务 | 8083 | BGE Embedding 服务（可选） |

## 🔄 停止服务

### 停止后端

```bash
# 在运行后端的终端按 Ctrl+C
# 或查找进程并杀死
ps aux | grep java
kill -9 <PID>
```

### 停止 Milvus

```bash
./milvus-stop.sh
# 或
docker-compose -f docker-compose-milvus.yml down
```

### 停止前端

```bash
# 在运行前端的终端按 Ctrl+C
```

## 📚 相关文档

- [RuoYi 官方文档](http://doc.ruoyi.vip/)
- [DeepSeek API 文档](https://platform.deepseek.com/docs)
- [Milvus 官方文档](https://milvus.io/docs)
- [BGE 模型文档](https://github.com/FlagOpen/FlagEmbedding)

## ✅ 启动检查清单

- [ ] 数据库 `aidb` 已创建并初始化
- [ ] 数据迁移已完成（`migrate_all_in_one.sql`）
- [ ] Redis 服务已启动
- [ ] Milvus 服务已启动（`docker ps | grep milvus`）
- [ ] DeepSeek API Key 已配置
- [ ] Embedding 服务已启动（可选，用于 RAG）
- [ ] 后端服务已启动（http://localhost:8080）
- [ ] 前端服务已启动（http://localhost:80）

## 🎉 启动成功

访问以下地址验证：

- **前端首页**：http://localhost:80
- **论坛页面**：http://localhost:80/forum
- **搜索页面**：http://localhost:80/forum/search
- **API 文档**：http://localhost:8080/swagger-ui.html

祝使用愉快！🚀

