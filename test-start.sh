#!/bin/bash

echo "开始测试应用启动..."

# 创建必要的目录
echo "创建必要的目录..."
mkdir -p logs
mkdir -p uploadPath

# 检查目录是否创建成功
echo "检查目录创建状态..."
ls -la | grep -E "(logs|uploadPath)"

# 尝试启动应用（只启动几秒钟来测试）
echo "尝试启动应用进行测试..."
cd ruoyi-admin
timeout 10s mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev" || echo "应用启动测试完成"

echo "测试完成！"
