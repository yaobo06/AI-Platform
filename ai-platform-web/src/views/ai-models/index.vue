<template>
  <div class="ai-models has-global-navbar">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button
        class="back-button"
        type="primary"
        size="medium"
        circle
        @click="goBack"
        title="返回首页">
        <i class="el-icon-back"></i>
      </el-button>
      <div class="back-tooltip">返回首页</div>
    </div>

    <!-- 页面头部 -->
    <div class="models-header">
      <div class="header-content">
        <h1>AI 应用</h1>
        <p class="subtitle">持续更新最新AI应用中······</p>
      </div>
    </div>

    <!-- 模型分类导航 -->
    <div class="models-nav">
      <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange" class="custom-tabs">
        <el-tab-pane v-for="category in categories" :key="category.id" :label="category.name" :name="category.id">
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 模型列表 -->
    <div class="models-grid">
      <el-skeleton :loading="loading" animated :count="6" v-if="loading">
        <template slot="template">
          <div class="model-card skeleton">
            <div class="skeleton-image"></div>
            <div class="skeleton-content">
              <h3 class="skeleton-title"></h3>
              <p class="skeleton-desc"></p>
              <div class="skeleton-meta"></div>
              <div class="skeleton-tags"></div>
              <div class="skeleton-actions"></div>
            </div>
          </div>
        </template>
      </el-skeleton>

      <div v-else v-for="model in filteredModels" :key="model.id" class="model-card">
        <div class="model-image">
          <img :src="model.image" :alt="model.name">
          <div class="model-badge" :class="model.status">{{ model.statusText }}</div>
        </div>
        <div class="model-content">
          <h3 class="model-name">{{ model.name }}</h3>
          <p class="model-desc">{{ model.description }}</p>
          <div class="model-meta">
            <div class="meta-item">
              <i class="el-icon-date"></i>
              <span>{{ model.updateDate }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-collection"></i>
              <span>{{ model.version }}</span>
            </div>
          </div>
          <div class="model-tags">
            <el-tag v-for="tag in model.tags"
                    :key="tag"
                    :type="getTagType(tag)"
                    size="small"
                    effect="plain">
              {{ tag }}
            </el-tag>
          </div>
          <div class="model-actions">
            <el-button type="primary" @click="tryModel(model)">立即体验</el-button>
            <el-button type="text" @click="viewDetails(model)">
              了解更多
              <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAllAiModels } from '@/api/ai-models'
import GlobalNavbar from '@/components/GlobalNavbar'

export default {
  name: 'AIModels',
  components: {
    GlobalNavbar
  },
  data() {
    return {
      activeCategory: 'all',
      categories: [
        { id: 'all', name: '全部模型' },
        { id: 'text', name: '文本生成' },
        { id: 'image', name: '图像生成' },
        { id: 'audio', name: '语音处理' },
        { id: 'video', name: '视频生成' },
        { id: 'code', name: '代码生成' }
      ],
      models: [], // 初始化为空数组
      loading: false // 添加加载状态
    }
  },
  created() {
    // 组件创建时获取数据
    this.fetchModels()
  },
  computed: {
    filteredModels() {
      if (this.activeCategory === 'all') {
        return this.models;
      }
      return this.models.filter(model => model.category === this.activeCategory);
    }
  },
  methods: {
    // 从数据库获取模型数据
    async fetchModels() {
      try {
        this.loading = true;
        // 调用API获取数据
        const response = await getAllAiModels();
        // 处理返回的数据，确保数据格式正确
        const dataList = response.data || [];
        this.models = dataList.map(item => ({
          id: item.id,
          name: item.name,
          description: item.description,
          image: "/prod-api"+item.imageUrl,
          category: item.categoryType,
          status: this.getStatusType(item.statusCode),
          statusText: this.getStatusText(item.statusCode),
          updateDate: this.formatDate(item.updateTime),
          version: item.version,
          tags: item.tags ? item.tags.split(',') : [],
          url: item.experienceUrl
        }));
      } catch (error) {
        console.error('获取模型数据失败:', error);
        this.$message.error('获取模型数据失败，请稍后重试');
      } finally {
        this.loading = false;
      }
    },

    // 状态码转换为显示状态
    getStatusType(statusCode) {
      const statusMap = {
        1: 'hot',
        2: 'new',
        3: 'popular'
      };
      return statusMap[statusCode] || 'normal';
    },

    // 状态码转换为显示文本
    getStatusText(statusCode) {
      const textMap = {
        1: '热门',
        2: '新品',
        3: '推荐'
      };
      return textMap[statusCode] || '普通';
    },

    // 格式化日期
    formatDate(dateString) {
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },

    handleCategoryChange(tab) {
      this.activeCategory = tab.name;
    },
    getTagType(tag) {
      const types = {
        '对话': '',
        'GPT-4': 'success',
        '自然语言处理': 'info',
        'Claude': 'warning',
        '学术研究': 'info',
        '图像生成': 'danger',
        '艺术创作': 'warning',
        'AI绘画': 'success',
        '精确控制': 'info',
        'OpenAI': 'success',
        '语音识别': 'warning',
        '多语言': 'info',
        '实时转录': 'success',
        '视频生成': 'danger',
        '动画制作': 'warning',
        'AI视频': 'info',
        '代码生成': 'success',
        'AI编程': 'warning',
        '智能补全': 'info'
      };
      return types[tag] || '';
    },
    tryModel(model) {
      window.open(model.url, '_blank');
    },
    viewDetails(model) {
      this.$router.push(`/ai-models/${model.id}`);
    },
    goBack() {
      this.$router.push('/introduction');
    }
  }
}
</script>

<style scoped lang="scss">
.ai-models {
  min-height: 100vh;
  background: #f8fafc;
}

// 返回按钮样式
.back-button-container {
  position: fixed;
  top: 90px; // 调整位置，避免与导航栏重叠
  left: 30px; // 稍微向右移动
  z-index: 999;

  .back-button {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
    border: none;
    box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px) scale(1.05);
      box-shadow: 0 8px 20px rgba(30, 64, 175, 0.4);
      background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
    }

    &:active {
      transform: translateY(0) scale(1);
    }

    i {
      font-size: 18px;
      font-weight: bold;
    }
  }

  .back-tooltip {
    position: absolute;
    left: 60px;
    top: 50%;
    transform: translateY(-50%);
    background: rgba(0, 0, 0, 0.8);
    color: white;
    padding: 8px 12px;
    border-radius: 6px;
    font-size: 14px;
    white-space: nowrap;
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
    pointer-events: none;

    &::before {
      content: '';
      position: absolute;
      left: -6px;
      top: 50%;
      transform: translateY(-50%);
      border: 6px solid transparent;
      border-right-color: rgba(0, 0, 0, 0.8);
    }
  }

  &:hover .back-tooltip {
    opacity: 1;
    visibility: visible;
    left: 55px;
  }

  // 移动端适配
  @media (max-width: 768px) {
    top: 75px; // 调整移动端位置，避免与导航栏重叠
    left: 20px;

    .back-button {
      width: 44px;
      height: 44px;

      i {
        font-size: 16px;
      }
    }

    .back-tooltip {
      display: none;
    }
  }
}

.models-header {
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
  padding: 100px 0 60px; // 进一步增加顶部内边距，让标题更多下移
  color: #fff;
  text-align: center;
  margin-bottom: 40px;

  .header-content {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;

    h1 {
      font-size: 2.5rem;
      font-weight: 700;
      margin: 0 0 16px;
      letter-spacing: 1px;
    }

    .subtitle {
      font-size: 1.2rem;
      opacity: 0.9;
      margin: 0;
    }
  }
}

.models-nav {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 0 20px;

  .custom-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 0;
      border-bottom: none;
    }

    :deep(.el-tabs__nav-wrap::after) {
      display: none;
    }

    :deep(.el-tabs__item) {
      font-size: 16px;
      padding: 0 24px;
      height: 48px;
      line-height: 48px;
      transition: all 0.3s;

      &.is-active {
        color: #1e40af;
        font-weight: 500;
      }

      &:hover {
        color: #1e40af;
      }
    }

    :deep(.el-tabs__active-bar) {
      background-color: #1e40af;
      height: 3px;
      border-radius: 3px;
    }
  }
}

.models-grid {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.model-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
  }

  .model-image {
    position: relative;
    height: 200px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;
    }

    &:hover img {
      transform: scale(1.05);
    }

    .model-badge {
      position: absolute;
      top: 12px;
      right: 12px;
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 500;
      color: #fff;

      &.hot {
        background: #ef4444;
      }

      &.new {
        background: #3b82f6;
      }

      &.popular {
        background: #10b981;
      }
    }
  }

  .model-content {
    padding: 20px;

    .model-name {
      font-size: 1.25rem;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 8px;
    }

    .model-desc {
      font-size: 0.875rem;
      color: #6b7280;
      margin: 0 0 16px;
      line-height: 1.5;
      height: 40px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .model-meta {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
      font-size: 0.875rem;
      color: #6b7280;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;

        i {
          font-size: 14px;
        }
      }
    }

    .model-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-bottom: 20px;

      .el-tag {
        border-radius: 4px;
      }
    }

    .model-actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .el-button--text {
        font-size: 0.875rem;

        i {
          margin-left: 4px;
          transition: transform 0.3s ease;
        }

        &:hover i {
          transform: translateX(4px);
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .models-header {
    padding: 60px 0 40px; // 移动端也增加顶部内边距

    .header-content {
      h1 {
        font-size: 2rem;
      }

      .subtitle {
        font-size: 1rem;
      }
    }
  }

  .models-grid {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 0 16px;
  }

  .model-card {
    .model-image {
      height: 180px;
    }

    .model-content {
      padding: 16px;
    }
  }
}

// 骨架屏样式
.skeleton {
  .skeleton-image {
    height: 200px;
    background: #f0f0f0;
    border-radius: 12px 12px 0 0;
  }

  .skeleton-content {
    padding: 20px;

    .skeleton-title {
      height: 24px;
      background: #f0f0f0;
      margin-bottom: 12px;
      width: 70%;
    }

    .skeleton-desc {
      height: 16px;
      background: #f0f0f0;
      margin-bottom: 16px;
      width: 100%;
    }

    .skeleton-meta {
      height: 20px;
      background: #f0f0f0;
      margin-bottom: 16px;
      width: 60%;
    }

    .skeleton-tags {
      display: flex;
      gap: 8px;
      margin-bottom: 20px;

      &::before {
        content: '';
        height: 24px;
        background: #f0f0f0;
        width: 60px;
        border-radius: 4px;
      }

      &::after {
        content: '';
        height: 24px;
        background: #f0f0f0;
        width: 80px;
        border-radius: 4px;
      }
    }

    .skeleton-actions {
      height: 32px;
      background: #f0f0f0;
      border-radius: 4px;
    }
  }
}
</style>
