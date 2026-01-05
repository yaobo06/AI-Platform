#!/bin/bash

echo ""
echo "[信息] 构建Web应用，生成dist文件夹。"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 切换到项目根目录
cd "$SCRIPT_DIR/.."

# 构建生产版本
npm run build:prod
