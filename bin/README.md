# 脚本使用说明

## macOS/Linux 用户

在macOS或Linux系统上，请使用以下命令：

### 安装依赖
```bash
./bin/package.sh
```

### 启动开发服务器
```bash
./bin/run-web.sh
```

### 构建生产版本
```bash
./bin/build.sh
```

## Windows 用户

在Windows系统上，请使用以下命令：

### 安装依赖
```cmd
bin\package.bat
```

### 启动开发服务器
```cmd
bin\run-web.bat
```

### 构建生产版本
```cmd
bin\build.bat
```

## 直接使用npm命令

您也可以直接使用npm命令：

```bash
# 安装依赖
npm install --registry=https://registry.npmmirror.com

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build:prod
```

## 注意事项

- 确保已安装Node.js和npm
- 首次运行前请先执行安装依赖命令
- 如果遇到权限问题，请确保脚本有执行权限：`chmod +x bin/*.sh`
