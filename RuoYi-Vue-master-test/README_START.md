# 服务启动指南

## 🚀 一键启动所有服务

```bash
./start-all.sh
```

这个脚本会自动启动：
- ✅ Docker 服务（Milvus + Redis）
- ✅ Python Embedding 服务（RAG 功能）

## 🛑 停止所有服务

```bash
./stop-all.sh
```

## 📋 启动顺序说明

### 1. 启动基础服务（Docker）

```bash
./start-all.sh
```

这会启动：
- **Milvus** - 向量数据库（端口 19530）
- **Redis** - 缓存服务（端口 6379）
- **MinIO** - 对象存储（端口 9000, 9001）

### 2. 启动 Python Embedding 服务

`start-all.sh` 会自动启动，也可以手动启动：

```bash
python3 local-embedding-service.py
```

### 3. 启动 Java 后端

```bash
cd ruoyi-admin
mvn spring-boot:run
```

或者使用 IDE 运行 `RuoYiApplication.java`

### 4. 启动前端

```bash
cd ai-platform-web
npm run dev
```

## ⚠️ 关于 404 错误

如果启动后端后立即访问前端出现 404 错误，这是正常的：

1. **后端启动需要时间**（通常 10-30 秒）
2. **前端在页面加载时立即请求接口**，此时后端可能还未完全启动
3. **404 错误已被优化处理**，不会影响页面正常使用

### 解决方案

1. **等待后端完全启动**：查看后端日志，等待看到 "应用启动成功" 的提示
2. **刷新页面**：后端启动后刷新前端页面即可
3. **检查服务状态**：
   ```bash
   # 检查后端是否运行
   curl http://localhost:8080/forum/categories
   
   # 应该返回 JSON 数据，而不是 404
   ```

## 🔍 服务状态检查

### 检查 Docker 服务

```bash
docker ps --filter "name=milvus" --filter "name=redis"
```

### 检查 Embedding 服务

```bash
curl http://localhost:8083/health
```

应该返回：
```json
{"status":"healthy","model_loaded":true}
```

### 检查后端服务

```bash
curl http://localhost:8080/forum/categories
```

## 📝 服务端口列表

| 服务 | 端口 | 说明 |
|------|------|------|
| Java 后端 | 8080 | 主应用服务 |
| Milvus | 19530 | 向量数据库 |
| Redis | 6379 | 缓存服务 |
| MinIO API | 9000 | 对象存储 API |
| MinIO 控制台 | 9001 | 对象存储控制台 |
| Embedding | 8083 | Python Embedding 服务 |

## 🐛 常见问题

### 1. 端口被占用

```bash
# 查找占用端口的进程
lsof -i :8080
lsof -i :6379
lsof -i :8083

# 停止进程
kill -9 <PID>
```

### 2. Docker 服务启动失败

```bash
# 查看 Docker 日志
docker-compose -f docker-compose-milvus.yml logs

# 重启 Docker 服务
docker-compose -f docker-compose-milvus.yml restart
```

### 3. Embedding 服务启动失败

```bash
# 查看日志
tail -f embedding.log

# 检查依赖
pip3 install fastapi uvicorn sentence-transformers
```

### 4. 后端连接 Redis 失败

确保：
1. Redis 服务已启动：`docker ps | grep redis`
2. 配置正确：检查 `application.yml` 中的 Redis 配置
3. 密码正确：默认密码是 `20250528`

## 📚 相关文档

- [Docker Redis 配置](./DOCKER_REDIS_SETUP.md)（如果存在）
- [Embedding 服务配置](./EMBEDDING_SERVICE_SETUP.md)（如果存在）

