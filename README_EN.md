# AI Platform - Intelligent Forum Platform

An intelligent forum platform developed based on the RuoYi-Vue framework, integrating advanced technologies such as DeepSeek AI, RAG vector retrieval, and Redis caching.

## ✨ Features

- 🗨️ **Forum Features**: Post publishing, comments, likes, favorites, category management
- 🔍 **Intelligent Search**: Supports database search, DeepSeek AI search, and RAG vector retrieval
- 🤖 **AI Integration**: DeepSeek API for intelligent search and title parsing
- 📊 **Vector Retrieval**: Milvus vector database supports semantic search (RAG)
- ⚡ **Performance Optimization**: Redis caching accelerates search and title parsing
- 👤 **User System**: JWT authentication, user profile center, login status management

## 📋 Prerequisites

### Required Environment

- **JDK 1.8+** - Java development environment
- **Maven 3.6+** - Java project build tool
- **MySQL 5.7+** - Database
- **Redis 5.0+** - Cache service (can be started via Docker)
- **Docker** - For running Milvus and Redis
- **Node.js 14+** - Frontend development environment
- **Python 3.8+** - Embedding service (optional, for RAG)

### Optional Environment

- **Python 3.8+** - For local Embedding service (RAG functionality)

## 🚀 Quick Start

### Step 1: Clone the Project

```bash
git clone https://github.com/yaobo06/AI-Platform.git
cd AI-Platform
```

### Step 2: Database Initialization

1. **Create Database**
   ```sql
   CREATE DATABASE aidb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Execute SQL Scripts** (if available)
   ```bash
   # Basic table structure (need to obtain from RuoYi official)
   # mysql -uusername -ppassword aidb < sql/ry_20250417.sql
   # mysql -uusername -ppassword aidb < sql/quartz.sql
   ```

### Step 3: Configuration Setup

#### 1. Backend Configuration

Copy example configuration files and modify:

```bash
cd RuoYi-Vue-master-test/ruoyi-admin/src/main/resources
cp application.yml.example application.yml
cp application-druid.yml.example application-druid.yml
```

Edit `application.yml` with the following content:

```yaml
spring:
  datasource:
    # Database configuration (in application-druid.yml)
    # ...
  redis:
    host: localhost  # or your Redis address
    port: 6379
    password: xxxxxxx  # Change to your Redis password
    database: 0

# DeepSeek API Configuration
deepseek:
  api:
    url: https://api.deepseek.com/v1/chat/completions
    key: your-deepseek-api-key  # Replace with your DeepSeek API Key
  embeddings:
    url: http://localhost:8083/embeddings  # Local Embedding service
    model: BAAI/bge-small-zh
    dimension: 512

# Token Configuration (modify for production)
token:
  secret: your-secret-key-here  # Change to a random string
  expireTime: 30
```

Edit `application-druid.yml` to configure database connection:

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/aidb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: your-username  # Change to your database username
        password: your-password  # Change to your database password
```

#### 2. Get DeepSeek API Key

1. Visit [DeepSeek Official Website](https://www.deepseek.com/)
2. Register an account and get API Key
3. Fill the Key into `deepseek.api.key` in `application.yml`

### Step 4: Start Base Services

#### Method 1: Use One-Click Startup Script (Recommended)

```bash
cd RuoYi-Vue-master-test
chmod +x start-all.sh
./start-all.sh
```

This script will automatically start:
- ✅ Docker services (Milvus + Redis)
- ✅ Python Embedding service (RAG functionality)

#### Method 2: Manual Startup

**Start Docker Services (Milvus + Redis)**

```bash
cd RuoYi-Vue-master-test
docker-compose -f docker-compose-milvus.yml up -d
```

**Start Python Embedding Service (Optional, for RAG)**

```bash
cd RuoYi-Vue-master-test

# Install dependencies (first run)
pip3 install fastapi uvicorn sentence-transformers

# Start service
python3 local-embedding-service.py
```

The service will start at `http://localhost:8083`.

**Note**: The first run will download the BGE model from Hugging Face (about 100MB), please be patient.

### Step 5: Start Backend Service

```bash
cd RuoYi-Vue-master-test

# Build project
mvn clean install -DskipTests

# Start application
cd ruoyi-admin
mvn spring-boot:run

# Or use script
./ry.sh
```

Backend service will start at `http://localhost:8080`.

### Step 6: Start Frontend Service

```bash
cd ai-platform-web

# Install dependencies (first run)
npm install

# Start development server
npm run dev
```

Frontend service will start at `http://localhost:80` (or configured port).

## 📝 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| Java Backend | 8080 | Main application service |
| Frontend | 80 | Vue development server |
| Milvus | 19530 | Vector database |
| Redis | 6379 | Cache service |
| MinIO API | 9000 | Object storage API |
| MinIO Console | 9001 | Object storage console (username/password: minioadmin/minioadmin) |
| Embedding | 8083 | Python Embedding service |

## 🔧 Feature Configuration

### Search Modes

The system supports three search modes:

1. **Intelligent Search (Hybrid)**: Combines database search and AI keyword expansion
2. **DeepSeek Search**: Uses DeepSeek AI for semantic search
3. **RAG Vector Retrieval**: Uses Milvus vector database for vector similarity search

### Redis Cache

The system uses Redis to cache the following:
- Search results
- Title parsing results
- Popular tags

### Milvus Vector Database

Used for RAG functionality vector storage:
- Automatically generates vectors for post content
- Supports semantic similarity search
- Requires Embedding service support

## 🛑 Stop Services

### Stop All Services

```bash
cd RuoYi-Vue-master-test
./stop-all.sh
```

### Manual Stop

```bash
# Stop Docker services
docker-compose -f docker-compose-milvus.yml down

# Stop Embedding service
lsof -ti:8083 | xargs kill -9

# Stop backend (Ctrl+C or)
lsof -ti:8080 | xargs kill -9

# Stop frontend (Ctrl+C or)
lsof -ti:80 | xargs kill -9
```

## 🐛 Common Issues

### 1. Frontend shows 404 error after backend startup

**Reason**: Backend startup takes time (10-30 seconds), frontend requests API immediately on page load.

**Solution**:
1. Wait for backend to fully start (check backend logs)
2. Refresh frontend page
3. Check if backend is running: `curl http://localhost:8080/forum/categories`

### 2. Redis Connection Failed

**Check Items**:
- Is Redis service running: `docker ps | grep redis`
- Is configuration correct: Check Redis configuration in `application.yml`
- Is password correct: Default password is `20250528` (set when Docker starts)

### 3. Embedding Service Startup Failed

**Check Items**:
- Python version: `python3 --version` (requires 3.8+)
- Are dependencies installed: `pip3 install fastapi uvicorn sentence-transformers`
- Is port occupied: `lsof -i :8083`
- Network connection: First run needs to download model from Hugging Face

### 4. Milvus Connection Failed

**Check Items**:
- Is Docker service running: `docker ps | grep milvus`
- Is port correct: Default 19530
- Is configuration correct: Check Milvus configuration in `application.yml`

### 5. DeepSeek API Call Failed

**Check Items**:
- Is API Key correct: Check `deepseek.api.key` in `application.yml`
- Network connection: Ensure can access `https://api.deepseek.com`
- API balance: Check if DeepSeek account has available balance

## 📚 Project Structure

```
AI-Platform/
├── ai-platform-web/          # Frontend project (Vue.js)
│   ├── src/
│   │   ├── views/forum/      # Forum related pages
│   │   ├── components/       # Common components
│   │   ├── mixins/          # Mixin (login status management)
│   │   └── utils/           # Utility functions
│   └── package.json
│
├── RuoYi-Vue-master-test/     # Backend project (Spring Boot)
│   ├── ruoyi-admin/          # Main application module
│   ├── ruoyi-system/         # System module
│   ├── ruoyi-common/         # Common module
│   ├── docker-compose-milvus.yml  # Docker Compose configuration
│   ├── local-embedding-service.py # Embedding service
│   ├── start-all.sh          # One-click startup script
│   └── stop-all.sh           # Stop script
│
└── README.md                  # This file
```

## 🔐 Security Notes

1. **Configuration Files**: Do not commit configuration files containing sensitive information to Git
   - `application.yml` - Contains database password, API Key
   - `application-druid.yml` - Contains database connection information
   - `application-local-redis.yml` - Contains Redis password

2. **Production Environment**:
   - Change all default passwords
   - Use strong passwords
   - Configure HTTPS
   - Limit API access frequency

## 📄 License

This project is developed based on the RuoYi-Vue framework. Please follow the corresponding open source license.

## 🤝 Contributing

Welcome to submit Issues and Pull Requests!

## 📞 Contact

If you have any questions, please submit an Issue or contact the project maintainer.

---

**Enjoy using!** 🎉

