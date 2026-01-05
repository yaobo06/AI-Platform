#!/bin/bash

# 一键启动所有服务脚本（Docker + Embedding）

set -e  # 遇到错误立即退出

echo "=========================================="
echo "🚀 启动所有服务"
echo "=========================================="

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查 Docker 是否运行
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}❌ 错误: Docker 未运行，请先启动 Docker${NC}"
        exit 1
    fi
}

# 检查 Python 是否安装
check_python() {
    if ! command -v python3 &> /dev/null; then
        echo -e "${YELLOW}⚠️  警告: 未找到 python3，将跳过 Embedding 服务启动${NC}"
        return 1
    fi
    return 0
}

# 启动 Docker 服务（Milvus + Redis）
start_docker() {
    echo ""
    echo -e "${GREEN}📦 启动 Docker 服务（Milvus + Redis）...${NC}"
    
    # 创建数据目录
    mkdir -p milvus-data/etcd
    mkdir -p milvus-data/minio
    mkdir -p milvus-data/standalone
    mkdir -p redis-data
    
    # 启动服务
    docker-compose -f docker-compose-milvus.yml up -d
    
    echo "等待 Docker 服务启动..."
    sleep 15
    
    # 检查服务状态
    echo ""
    echo "Docker 服务状态:"
    docker ps --filter "name=milvus" --filter "name=redis" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" || true
}

# 启动 Embedding 服务
start_embedding() {
    if ! check_python; then
        return 1
    fi
    
    echo ""
    echo -e "${GREEN}🐍 启动 Python Embedding 服务（RAG 功能）...${NC}"
    
    # 检查端口是否被占用
    if lsof -Pi :8083 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  端口 8083 已被占用，跳过启动${NC}"
        echo "提示: 如果服务未运行，请手动停止占用端口的进程"
        return 0
    fi
    
    # 检查依赖
    if ! python3 -c "import fastapi" 2>/dev/null; then
        echo -e "${YELLOW}⚠️  检测到缺少依赖，正在安装...${NC}"
        pip3 install fastapi uvicorn sentence-transformers || {
            echo -e "${RED}❌ 依赖安装失败${NC}"
            echo "请手动执行: pip3 install fastapi uvicorn sentence-transformers"
            return 1
        }
    fi
    
    # 后台启动 Embedding 服务
    echo "启动 Embedding 服务（后台运行）..."
    nohup python3 local-embedding-service.py > embedding.log 2>&1 &
    EMBEDDING_PID=$!
    echo $EMBEDDING_PID > embedding.pid
    
    # 等待服务启动
    echo "等待 Embedding 服务启动..."
    sleep 5
    
    # 检查服务是否启动成功
    for i in {1..30}; do
        if curl -s http://localhost:8083/health > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Embedding 服务启动成功！${NC}"
            return 0
        fi
        sleep 1
    done
    
    echo -e "${YELLOW}⚠️  Embedding 服务启动超时，请检查 embedding.log${NC}"
    return 1
}

# 验证服务
verify_services() {
    echo ""
    echo "=========================================="
    echo "✅ 服务验证"
    echo "=========================================="
    
    # 检查 Redis
    if docker exec redis-server redis-cli -a 20250528 ping > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Redis: 正常${NC}"
    else
        echo -e "${RED}❌ Redis: 异常${NC}"
    fi
    
    # 检查 Milvus
    if curl -s http://localhost:9091/healthz > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Milvus: 正常${NC}"
    else
        echo -e "${YELLOW}⚠️  Milvus: 可能还在启动中${NC}"
    fi
    
    # 检查 Embedding
    if curl -s http://localhost:8083/health > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Embedding: 正常${NC}"
    else
        echo -e "${YELLOW}⚠️  Embedding: 未启动或启动中${NC}"
    fi
}

# 显示服务信息
show_info() {
    echo ""
    echo "=========================================="
    echo "📋 服务信息"
    echo "=========================================="
    echo "✅ Milvus 向量数据库:"
    echo "   - 地址: localhost:19530"
    echo "   - 健康检查: http://localhost:9091/healthz"
    echo ""
    echo "✅ MinIO 对象存储:"
    echo "   - API: http://localhost:9000"
    echo "   - 控制台: http://localhost:9001"
    echo "   - 用户名/密码: minioadmin/minioadmin"
    echo ""
    echo "✅ Redis 缓存服务:"
    echo "   - 地址: localhost:6379"
    echo "   - 密码: 20250528"
    echo ""
    echo "✅ Python Embedding 服务:"
    echo "   - 地址: http://localhost:8083/embeddings"
    echo "   - 健康检查: http://localhost:8083/health"
    echo "   - 日志: tail -f embedding.log"
    echo ""
    echo "=========================================="
    echo "📝 常用命令"
    echo "=========================================="
    echo "停止所有服务: ./stop-all.sh"
    echo "查看 Docker 日志: docker-compose -f docker-compose-milvus.yml logs -f"
    echo "查看 Embedding 日志: tail -f embedding.log"
    echo ""
}

# 主函数
main() {
    check_docker
    start_docker
    start_embedding
    verify_services
    show_info
    
    echo ""
    echo -e "${GREEN}🎉 所有服务启动完成！${NC}"
    echo ""
}

# 执行主函数
main

