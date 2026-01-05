#!/usr/bin/env python3
"""
本地 Embedding 服务
使用 BGE 中文模型，提供 HTTP API 接口
Java 应用可以通过 HTTP 调用，实现"本地原生"的效果
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer
import uvicorn
import sys

app = FastAPI(title="Local Embedding Service", version="1.0.0")

# 添加 CORS 支持
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 全局模型变量
model = None

class EmbeddingRequest(BaseModel):
    model: str = "BAAI/bge-small-zh"
    input: str

class EmbeddingResponse(BaseModel):
    data: list
    model: str
    usage: dict

@app.on_event("startup")
async def load_model():
    """启动时加载模型"""
    global model
    try:
        print("正在加载 BGE 中文模型...")
        print("提示：首次运行会从 Hugging Face 下载模型（约 100MB），请耐心等待...")
        model = SentenceTransformer('BAAI/bge-small-zh')
        print("✅ 模型加载成功！")
        print("📝 API 端点: http://localhost:8083/embeddings")
    except Exception as e:
        print(f"❌ 模型加载失败: {e}")
        print("提示：")
        print("  1. 检查网络连接（需要从 Hugging Face 下载模型）")
        print("  2. 确保已安装: pip install sentence-transformers")
        print("  3. 如果网络问题，可以设置镜像: export HF_ENDPOINT=https://hf-mirror.com")
        sys.exit(1)

@app.post("/embeddings", response_model=EmbeddingResponse)
async def create_embedding(request: EmbeddingRequest):
    """生成文本向量"""
    if model is None:
        raise HTTPException(status_code=503, detail="模型未加载")
    
    if not request.input:
        raise HTTPException(status_code=400, detail="输入文本不能为空")
    
    try:
        # 生成向量（自动归一化）
        embeddings = model.encode(request.input, normalize_embeddings=True)
        
        # 转换为列表
        embedding_list = embeddings.tolist()
        
        return EmbeddingResponse(
            data=[{
                "embedding": embedding_list,
                "index": 0
            }],
            model=request.model,
            usage={
                "prompt_tokens": len(request.input),
                "total_tokens": len(request.input)
            }
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"生成向量失败: {str(e)}")

@app.get("/health")
async def health():
    """健康检查"""
    return {
        "status": "healthy",
        "model_loaded": model is not None
    }

@app.get("/")
async def root():
    """根路径"""
    return {
        "service": "Local Embedding Service",
        "model": "BAAI/bge-small-zh",
        "endpoints": {
            "embeddings": "/embeddings",
            "health": "/health"
        }
    }

if __name__ == "__main__":
    print("=" * 60)
    print("🚀 启动本地 Embedding 服务")
    print("=" * 60)
    print("模型: BAAI/bge-small-zh")
    print("端口: 8083")
    print("=" * 60)
    
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8083,
        log_level="info"
    )

