# 日志系统初始化失败问题修复说明

## 问题描述
应用启动时出现以下错误：
```
Logging system failed to initialize using configuration from 'null'
java.lang.IllegalStateException: Logback configuration error detected: 
ERROR in ch.qos.logback.core.rolling.RollingFileAppender[file_info] - Failed to create parent directories for [/home/ruoyi/logs/sys-info.log]
```

## 问题原因
1. 日志配置文件 `logback.xml` 中硬编码了绝对路径 `/home/ruoyi/logs`
2. 该目录在开发环境中不存在，导致日志文件无法创建
3. 应用配置文件 `application.yml` 中的文件上传路径也被硬编码

## 修复方案

### 1. 修改日志配置文件
- 将 `ruoyi-admin/src/main/resources/logback.xml` 中的日志路径改为相对路径 `logs`
- 创建了 `logback-spring.xml` 配置文件，支持不同环境的配置

### 2. 修改应用配置文件
- 将 `ruoyi-admin/src/main/resources/application.yml` 中的文件上传路径改为相对路径 `uploadPath`

### 3. 修改启动脚本
- 在 `ry.sh` (Linux) 和 `ry.bat` (Windows) 中添加了目录创建逻辑
- 启动应用前自动创建 `logs` 和 `uploadPath` 目录

## 使用方法

### 开发环境
直接运行应用，日志文件会保存在项目根目录的 `logs` 文件夹中

### 生产环境
1. 设置环境变量 `SPRING_PROFILES_ACTIVE=prod`
2. 或者手动创建目录 `/home/ruoyi/logs` 和 `/home/ruoyi/uploadPath`

### 启动应用
```bash
# Linux/Mac
./ry.sh start

# Windows
ry.bat
```

## 注意事项
1. 确保应用有权限在项目目录下创建文件夹
2. 生产环境建议使用绝对路径配置
3. 如果使用Docker部署，需要在容器中创建相应的目录

## 文件修改清单
- `ruoyi-admin/src/main/resources/logback.xml` - 修改日志路径
- `ruoyi-admin/src/main/resources/logback-spring.xml` - 新增环境相关配置
- `ruoyi-admin/src/main/resources/application.yml` - 修改文件上传路径
- `ry.sh` - 添加目录创建逻辑
- `ry.bat` - 添加目录创建逻辑
