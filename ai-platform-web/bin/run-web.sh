#!/bin/bash

echo ""
echo "[信息] 使用 Vue CLI 启动 Web 应用。"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 切换到项目根目录
cd "$SCRIPT_DIR/.."

# 运行开发服务器
npm run dev
