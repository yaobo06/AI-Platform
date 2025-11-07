# AI模型前后端对接说明

## 对接概述

本文档说明了如何将 `ai-platform-web` 中的 `src/views/ai-models` 前端页面与 `RuoYi-Vue-master` 中的 `AiModelsController` 后端接口成功对接。

## 主要修改

### 1. 前端修改

#### 1.1 创建 API 调用文件
- **文件**: `src/api/ai-models.js`
- **功能**: 定义与后端对接的所有 API 接口
- **关键接口**:
  - `getAllAiModels()`: 获取所有AI模型列表（前端展示用）
  - `listAiModels()`: 分页查询AI模型列表
  - `getAiModel(id)`: 获取单个AI模型详情
  - `addAiModel()`: 新增AI模型
  - `updateAiModel()`: 更新AI模型
  - `delAiModel()`: 删除AI模型

#### 1.2 修改视图组件
- **文件**: `src/views/ai-models/index.vue`
- **主要修改**:
  - 导入正确的 API 函数: `import { getAllAiModels } from '@/api/ai-models'`
  - 修改数据获取方法: `fetchModels()` 现在调用 `getAllAiModels()`
  - 修改数据映射: 将后端字段名正确映射到前端数据结构

### 2. 后端修改

#### 2.1 修复控制器包名
- **文件**: `RuoYi-Vue-master/ruoyi-admin/src/main/java/com/ruoyi/web/controller/aimodel/AiModelsController.java`
- **修改**: 将包名从 `com.ruoyi.system.controller` 改为 `com.ruoyi.web.controller.aimodel`

#### 2.2 新增前端专用接口
- **新增接口**: `@GetMapping("/lists")`
- **功能**: 为前端展示提供不分页的完整AI模型列表
- **返回格式**: `AjaxResult` 包装的数据列表

### 3. 数据库设计

#### 3.1 表结构
- **表名**: `ai_models`
- **主要字段**:
  - `id`: 主键ID
  - `name`: 模型名称
  - `description`: 模型描述
  - `image_url`: 模型图片URL
  - `category_type`: 分类类型（text/image/audio/video/code）
  - `status_code`: 状态码（1:热门 2:新品 3:推荐）
  - `version`: 版本号
  - `tags`: 标签（逗号分隔）
  - `experience_url`: 体验链接

#### 3.2 初始化数据
- 提供了 7 个示例AI模型数据，包含 GPT-4、Claude-3、DALL-E 3 等知名模型

## 数据流向

```
前端 ai-models/index.vue 
    ↓ 调用
API ai-models.js (getAllAiModels)
    ↓ 请求
后端 AiModelsController (/ai/models/lists)
    ↓ 调用
Service IAiModelsService
    ↓ 查询
Mapper AiModelsMapper
    ↓ 访问
数据库 ai_models 表
```

## 字段映射关系

| 前端字段 | 后端字段 | 说明 |
|---------|---------|------|
| id | id | 主键ID |
| name | name | 模型名称 |
| description | description | 模型描述 |
| image | imageUrl | 图片URL |
| category | categoryType | 分类类型 |
| status | statusCode | 状态码（需转换） |
| statusText | statusCode | 状态文本（需转换） |
| updateDate | updateTime | 更新时间（需格式化） |
| version | version | 版本号 |
| tags | tags | 标签（需分割） |
| url | experienceUrl | 体验链接 |

## 启动说明

### 前端启动
```bash
cd ai-platform-web
npm install
npm run dev
```

### 后端启动
1. 导入数据库脚本: `sql/ai_models.sql`
2. 启动后端服务: `RuoYiApplication.java`
3. 确保端口配置正确（默认8080）

### 配置检查
- 确保前端的 `VUE_APP_BASE_API` 指向正确的后端地址
- 确保后端数据库连接配置正确
- 确保跨域配置正确

## 测试验证

1. 启动前后端服务
2. 访问前端AI模型页面
3. 检查是否能正确展示模型列表
4. 验证分类筛选功能
5. 测试"立即体验"和"了解更多"按钮

## 常见问题

1. **数据不显示**: 检查后端接口是否正常响应，数据库是否有数据
2. **接口404**: 检查URL路径是否匹配，控制器映射是否正确
3. **字段为空**: 检查字段映射关系，确保前后端字段名一致
4. **跨域问题**: 确保后端已配置CORS支持

## 扩展说明

此对接方案已经为AI模型管理功能提供了完整的CRUD操作接口，可以根据实际需求进一步扩展功能，如：
- 添加搜索功能
- 添加排序功能
- 添加模型评分功能
- 添加使用统计功能 