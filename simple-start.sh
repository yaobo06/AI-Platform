#!/bin/bash

echo "=== 若依系统启动脚本 ==="
echo "正在准备启动环境..."

# 创建必要的目录
echo "1. 创建必要的目录..."
mkdir -p logs
mkdir -p uploadPath

# 显示目录状态
echo "2. 检查目录状态..."
ls -la | grep -E "(logs|uploadPath)"

# 启动应用
echo "3. 启动应用..."
cd ruoyi-admin

# 使用Maven启动应用
echo "使用Maven启动应用..."
mvn spring-boot:run

echo "应用启动完成！"
