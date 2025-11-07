# AI模型页面匿名访问设置

## 概述

本文档说明了如何配置 AI模型页面（ai-models）实现无需登录即可访问的功能。

## 修改内容

### 1. 前端配置

#### 1.1 路由白名单配置
- **文件**: `src/permission.js`
- **配置**: `/ai-models` 已添加到白名单 `whiteList` 数组中
- **效果**: 前端路由无需登录验证即可访问

```javascript
const whiteList = ['/login', '/register', '/introduction', '/ai-models', '/forum', '/community']
```

#### 1.2 API调用配置
- **文件**: `src/api/ai-models.js`
- **修改**: 为匿名访问的接口添加 `isToken: false` 头部标识
- **涉及接口**:
  - `getAllAiModels()`: 获取AI模型列表
  - `getAiModel(id)`: 获取AI模型详情

```javascript
headers: {
  'isToken': false
}
```

### 2. 后端配置

#### 2.1 匿名访问注解
- **文件**: `RuoYi-Vue-master/ruoyi-admin/src/main/java/com/ruoyi/web/controller/aimodel/AiModelsController.java`
- **修改**: 为相关接口添加 `@Anonymous` 注解
- **涉及接口**:
  - `/ai/models/lists`: 获取AI模型列表（前端展示用）
  - `/ai/models/{id}`: 获取AI模型详情

```java
@Anonymous
@GetMapping("/lists")
public AjaxResult lists(AiModels aiModels)

@Anonymous
@GetMapping(value = "/{id}")
public AjaxResult getInfo(@PathVariable("id") Long id)
```

## 技术原理

### 前端权限控制流程

```
用户访问 /ai-models
    ↓
路由守卫检查 (permission.js)
    ↓
检查是否在白名单中
    ↓
在白名单中，允许访问
    ↓
页面加载，调用API
    ↓
请求头包含 isToken: false
    ↓
不发送认证token
```

### 后端权限控制流程

```
前端请求 /ai/models/lists
    ↓
Spring Security 过滤器
    ↓
检查 @Anonymous 注解
    ↓
匿名访问允许
    ↓
直接进入控制器方法
    ↓
返回数据
```

## 安全考虑

### 1. 接口安全性
- ✅ `/lists` 接口只提供基本的模型展示信息
- ✅ `/ai/models/{id}` 接口只提供模型详情，不包含敏感信息
- ✅ 管理类接口（增删改）仍需要权限验证

### 2. 数据保护
- ✅ 返回的数据不包含用户隐私信息
- ✅ 不暴露系统内部敏感配置
- ✅ 只读操作，不能修改数据

## 测试验证

### 1. 前端测试
```bash
# 访问以下URL应该能正常显示页面
http://localhost:80/ai-models
```

### 2. 后端接口测试
```bash
# 不带认证头部应该能正常返回数据
curl -X GET "http://localhost:8080/ai/models/lists"
curl -X GET "http://localhost:8080/ai/models/1"
```

### 3. 功能测试
- [ ] 无需登录可以访问AI模型页面
- [ ] 可以正常显示模型列表
- [ ] 可以按分类筛选模型
- [ ] 可以点击"立即体验"跳转到外部链接
- [ ] 可以点击"了解更多"查看模型详情

## 注意事项

1. **环境配置**: 确保前后端服务都已启动
2. **数据库数据**: 确保 `ai_models` 表有初始化数据
3. **跨域配置**: 确保后端已正确配置CORS
4. **缓存清理**: 修改后建议清理浏览器缓存

## 故障排除

### 常见问题

1. **页面显示空白**
   - 检查控制台是否有JavaScript错误
   - 确认API接口是否正常响应

2. **接口403错误**
   - 确认 `@Anonymous` 注解是否正确添加
   - 检查Spring Security配置是否生效

3. **数据不显示**
   - 确认数据库是否有数据
   - 检查数据字段映射是否正确

4. **路由404错误**
   - 确认前端路由配置是否正确
   - 检查组件路径是否存在

## 扩展功能

未来可以考虑添加的功能：
- 模型搜索功能（无需登录）
- 模型评分展示（无需登录）
- 模型使用统计（无需登录）
- 热门推荐功能（无需登录） 