#!/bin/bash

# 一键停止所有服务脚本

echo "=========================================="
echo "🛑 停止所有服务"
echo "=========================================="

# 停止 Embedding 服务
if [ -f embedding.pid ]; then
    EMBEDDING_PID=$(cat embedding.pid)
    if ps -p $EMBEDDING_PID > /dev/null 2>&1; then
        echo "停止 Embedding 服务 (PID: $EMBEDDING_PID)..."
        kill $EMBEDDING_PID 2>/dev/null || true
        sleep 2
        # 如果还在运行，强制杀死
        if ps -p $EMBEDDING_PID > /dev/null 2>&1; then
            kill -9 $EMBEDDING_PID 2>/dev/null || true
        fi
        echo "✅ Embedding 服务已停止"
    fi
    rm -f embedding.pid
fi

# 也尝试通过端口停止
if lsof -Pi :8083 -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "停止占用 8083 端口的进程..."
    lsof -ti:8083 | xargs kill -9 2>/dev/null || true
fi

# 停止 Docker 服务
if docker info > /dev/null 2>&1; then
    echo "停止 Docker 服务（Milvus + Redis）..."
    docker-compose -f docker-compose-milvus.yml down
    echo "✅ Docker 服务已停止"
else
    echo "⚠️  Docker 未运行，跳过"
fi

echo ""
echo "✅ 所有服务已停止！"

