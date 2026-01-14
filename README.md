# AI Platform - 智能论坛平台

基于 RuoYi-Vue 框架开发的智能论坛平台，集成了 DeepSeek AI、RAG 向量检索、Redis 缓存等技术。

## ✨ 功能特性

- 🗨️ **论坛功能**：帖子发布、评论、点赞、收藏、分类管理
- 🔍 **智能搜索**：支持数据库搜索、DeepSeek AI 搜索、RAG 向量检索
- 🤖 **AI 集成**：DeepSeek API 用于智能搜索和标题解析
- 📊 **向量检索**：Milvus 向量数据库支持语义搜索（RAG）
- ⚡ **性能优化**：Redis 缓存加速搜索和标题解析
- 👤 **用户系统**：JWT 认证、用户个人中心、登录状态管理

## 📋 前置要求

### 必需环境

- **JDK 1.8+** - Java 开发环境
- **Maven 3.6+** - Java 项目构建工具
- **MySQL 5.7+** - 数据库
- **Redis 5.0+** - 缓存服务（可通过 Docker 启动）
- **Docker** - 用于运行 Milvus 和 Redis
- **Node.js 14+** - 前端开发环境
- **Python 3.8+** - Embedding 服务（可选，用于 RAG）

### 可选环境

- **Python 3.8+** - 用于本地 Embedding 服务（RAG 功能）

## 🚀 快速开始

### 第一步：克隆项目

### 第二步：数据库初始化

1. **创建数据库**
   ```sql
   CREATE DATABASE aidb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **执行 SQL 脚本**（如果存在）
   ```bash
   # 基础表结构（需要从 RuoYi 官方获取）
   # mysql -u用户名 -p密码 aidb < sql/ry_20250417.sql
   # mysql -u用户名 -p密码 aidb < sql/quartz.sql
   ```

### 第三步：配置文件设置

#### 1. 后端配置

复制示例配置文件并修改：

```bash
cd RuoYi-Vue-master-test/ruoyi-admin/src/main/resources
cp application.yml.example application.yml
cp application-druid.yml.example application-druid.yml
```

编辑 `application.yml`，配置以下内容：

```yaml
spring:
  datasource:
    # 数据库配置（在 application-druid.yml 中）
    # ...
  redis:
    host: localhost  # 或你的 Redis 地址
    port: 6379
    password: xxxxxxx  # 修改为你的 Redis 密码
    database: 0

# DeepSeek API 配置
deepseek:
  api:
    url: https://api.deepseek.com/v1/chat/completions
    key: your-deepseek-api-key  # 替换为你的 DeepSeek API Key
  embeddings:
    url: http://localhost:8083/embeddings  # 本地 Embedding 服务
    model: BAAI/bge-small-zh
    dimension: 512

# Token 配置（生产环境请修改）
token:
  secret: your-secret-key-here  # 修改为随机字符串
  expireTime: 30
```

编辑 `application-druid.yml`，配置数据库连接：

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/aidb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: your-username  # 修改为你的数据库用户名
        password: your-password  # 修改为你的数据库密码
```

#### 2. 获取 DeepSeek API Key

1. 访问 [DeepSeek 官网](https://www.deepseek.com/)
2. 注册账号并获取 API Key
3. 将 Key 填入 `application.yml` 中的 `deepseek.api.key`

### 第四步：启动基础服务

#### 方式1：使用一键启动脚本（推荐）

```bash
cd RuoYi-Vue-master-test
chmod +x start-all.sh
./start-all.sh
```

这个脚本会自动启动：
- ✅ Docker 服务（Milvus + Redis）
- ✅ Python Embedding 服务（RAG 功能）

#### 方式2：手动启动

**启动 Docker 服务（Milvus + Redis）**

```bash
cd RuoYi-Vue-master-test
docker-compose -f docker-compose-milvus.yml up -d
```

**启动 Python Embedding 服务（可选，用于 RAG）**

```bash
cd RuoYi-Vue-master-test

# 安装依赖（首次运行）
pip3 install fastapi uvicorn sentence-transformers

# 启动服务
python3 local-embedding-service.py
```

服务将在 `http://localhost:8083` 启动。

**注意**：首次运行会从 Hugging Face 下载 BGE 模型（约 100MB），请耐心等待。

### 第五步：启动后端服务

```bash
cd RuoYi-Vue-master-test

# 编译项目
mvn clean install -DskipTests

# 启动应用
cd ruoyi-admin
mvn spring-boot:run

# 或者使用脚本
./ry.sh
```

后端服务将在 `http://localhost:8080` 启动。

### 第六步：启动前端服务

```bash
cd ai-platform-web

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:80`（或配置的端口）启动。

## 📝 服务端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| Java 后端 | 8080 | 主应用服务 |
| 前端 | 80 | Vue 开发服务器 |
| Milvus | 19530 | 向量数据库 |
| Redis | 6379 | 缓存服务 |
| MinIO API | 9000 | 对象存储 API |
| MinIO 控制台 | 9001 | 对象存储控制台（用户名/密码：minioadmin/minioadmin） |
| Embedding | 8083 | Python Embedding 服务 |

## 🔧 功能配置

### 搜索模式

系统支持三种搜索模式：

1. **智能搜索（Hybrid）**：结合数据库搜索和 AI 关键词扩展
2. **DeepSeek 搜索**：使用 DeepSeek AI 进行语义搜索
3. **RAG 向量检索**：使用 Milvus 向量数据库进行向量相似度搜索

### Redis 缓存

系统使用 Redis 缓存以下内容：
- 搜索结果
- 标题解析结果
- 热门标签

### Milvus 向量数据库

用于 RAG 功能的向量存储：
- 自动为帖子内容生成向量
- 支持语义相似度搜索
- 需要 Embedding 服务支持

## 🛑 停止服务

### 停止所有服务

```bash
cd RuoYi-Vue-master-test
./stop-all.sh
```

### 手动停止

```bash
# 停止 Docker 服务
docker-compose -f docker-compose-milvus.yml down

# 停止 Embedding 服务
lsof -ti:8083 | xargs kill -9

# 停止后端（Ctrl+C 或）
lsof -ti:8080 | xargs kill -9

# 停止前端（Ctrl+C 或）
lsof -ti:80 | xargs kill -9
```

## 🐛 常见问题

### 1. 后端启动后前端出现 404 错误

**原因**：后端启动需要时间（10-30秒），前端在页面加载时立即请求接口。

**解决方案**：
1. 等待后端完全启动（查看后端日志）
2. 刷新前端页面
3. 检查后端是否运行：`curl http://localhost:8080/forum/categories`

### 2. Redis 连接失败

**检查项**：
- Redis 服务是否启动：`docker ps | grep redis`
- 配置是否正确：检查 `application.yml` 中的 Redis 配置
- 密码是否正确：默认密码是 `20250528`（Docker 启动时设置）

### 3. Embedding 服务启动失败

**检查项**：
- Python 版本：`python3 --version`（需要 3.8+）
- 依赖是否安装：`pip3 install fastapi uvicorn sentence-transformers`
- 端口是否被占用：`lsof -i :8083`
- 网络连接：首次运行需要从 Hugging Face 下载模型

### 4. Milvus 连接失败

**检查项**：
- Docker 服务是否启动：`docker ps | grep milvus`
- 端口是否正确：默认 19530
- 配置是否正确：检查 `application.yml` 中的 Milvus 配置

### 5. DeepSeek API 调用失败

**检查项**：
- API Key 是否正确：检查 `application.yml` 中的 `deepseek.api.key`
- 网络连接：确保可以访问 `https://api.deepseek.com`
- API 余额：检查 DeepSeek 账号是否有可用余额

## 📚 项目结构

```
BackEnd Update/
├── ai-platform-web/          # 前端项目（Vue.js）
│   ├── src/
│   │   ├── views/forum/      # 论坛相关页面
│   │   ├── components/        # 公共组件
│   │   ├── mixins/           # Mixin（登录状态管理）
│   │   └── utils/            # 工具函数
│   └── package.json
│
├── RuoYi-Vue-master-test/     # 后端项目（Spring Boot）
│   ├── ruoyi-admin/          # 主应用模块
│   ├── ruoyi-system/         # 系统模块
│   ├── ruoyi-common/         # 公共模块
│   ├── docker-compose-milvus.yml  # Docker Compose 配置
│   ├── local-embedding-service.py # Embedding 服务
│   ├── start-all.sh          # 一键启动脚本
│   └── stop-all.sh           # 停止脚本
│
└── README.md                  # 本文件
```

## 🔐 安全注意事项

1. **配置文件**：不要将包含敏感信息的配置文件提交到 Git
   - `application.yml` - 包含数据库密码、API Key
   - `application-druid.yml` - 包含数据库连接信息
   - `application-local-redis.yml` - 包含 Redis 密码

2. **生产环境**：
   - 修改所有默认密码
   - 使用强密码
   - 配置 HTTPS
   - 限制 API 访问频率

## 📄 许可证

本项目基于 RuoYi-Vue 框架开发，请遵循相应的开源协议。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

如有问题，请提交 Issue 或联系项目维护者。

---

**祝使用愉快！** 🎉

