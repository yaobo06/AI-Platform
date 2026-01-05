#!/bin/bash

echo "=== 安装Maven ==="

# 检查是否已安装Maven
if command -v mvn &> /dev/null; then
    echo "Maven已安装: $(mvn -version | head -1)"
    exit 0
fi

# 创建Maven目录
MAVEN_HOME="$HOME/maven"
mkdir -p "$MAVEN_HOME"

echo "下载Maven..."
# 下载Maven 3.9.6
curl -L "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz" -o maven.tar.gz

echo "解压Maven..."
tar -xzf maven.tar.gz
mv apache-maven-3.9.6/* "$MAVEN_HOME/"
rm -rf apache-maven-3.9.6 maven.tar.gz

# 添加到PATH
echo "配置环境变量..."
echo 'export MAVEN_HOME="$HOME/maven"' >> ~/.zshrc
echo 'export PATH="$MAVEN_HOME/bin:$PATH"' >> ~/.zshrc

# 重新加载配置
source ~/.zshrc

echo "Maven安装完成！"
echo "请重新打开终端或运行: source ~/.zshrc"
echo "然后运行: mvn -version 验证安装"
