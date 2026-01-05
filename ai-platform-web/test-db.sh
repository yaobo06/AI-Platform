#!/bin/bash

# 数据库连接测试脚本

echo "=== 数据库连接测试 ==="

# 检查MySQL客户端是否安装
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL客户端未安装，请先安装MySQL客户端"
    echo "macOS: brew install mysql-client"
    echo "Ubuntu: sudo apt-get install mysql-client"
    exit 1
fi

# 数据库连接参数
DB_HOST="47.113.98.181"
DB_PORT="33130"
DB_USER="root"
DB_PASS="Hm20250528"
DB_NAME="aidb"

echo "🔗 正在连接数据库..."
echo "主机: $DB_HOST:$DB_PORT"
echo "数据库: $DB_NAME"
echo "用户: $DB_USER"

# 测试连接
if mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SELECT 'Connection successful' as status;" 2>/dev/null; then
    echo "✅ 数据库连接成功！"
    
    echo ""
    echo "📋 检查现有表..."
    mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SHOW TABLES;"
    
    echo ""
    echo "🔍 检查论坛相关表..."
    mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SHOW TABLES LIKE 'forum%';"
    
    echo ""
    echo "📊 检查用户表..."
    mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASS -D $DB_NAME -e "SHOW TABLES LIKE 'sys_user';"
    
else
    echo "❌ 数据库连接失败！"
    echo "请检查以下配置："
    echo "1. 数据库主机地址是否正确"
    echo "2. 端口号是否正确"
    echo "3. 用户名密码是否正确"
    echo "4. 数据库名称是否存在"
    echo "5. 网络连接是否正常"
    exit 1
fi

echo ""
echo "=== 测试完成 ==="
