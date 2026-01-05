#!/bin/bash

echo ""
echo "[信息] 安装Web应用，生成node_modules文件夹。"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 切换到项目根目录
cd "$SCRIPT_DIR/.."

# 安装依赖
npm install --registry=https://registry.npmmirror.com
