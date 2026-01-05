#!/bin/bash

# 创建临时日志目录
mkdir -p /tmp/ruoyi/logs

# 设置日志路径环境变量
export LOG_PATH="/tmp/ruoyi/logs"

# 启动应用程序，禁用日志文件输出
java -Dlogging.file.name="" \
     -Dlogging.pattern.console="%d{HH:mm:ss.SSS} [%thread] %-5level %logger{20} - %msg%n" \
     -Dlogging.level.root=INFO \
     -Dlogging.level.com.ruoyi=INFO \
     -Dlogging.level.org.springframework=WARN \
     -jar ruoyi-admin/target/ruoyi-admin.jar
