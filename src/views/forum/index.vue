<template>
  <div class="forum-container has-global-navbar">
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

    <!-- 论坛头部 -->
    <div class="forum-header">
      <div class="header-content">
        <div class="header-left">
          <h1>AI论坛</h1>
          <p class="subtitle">分享AI知识，探寻技术可能</p>
        </div>
        <div class="forum-actions">
          <el-input
            v-model="searchQuery"
            placeholder="搜索帖子"
            prefix-icon="el-icon-search"
            class="search-input"
            clearable
          />
          <!-- 用户信息显示 -->
          <div v-if="isLoggedIn" class="user-info">
            <el-avatar :src="userAvatar" :size="32" class="user-avatar"></el-avatar>
            <span class="username">{{ userName }}</span>
            <el-button 
              type="primary" 
              @click="handlePostClick" 
              icon="el-icon-edit">
              发布帖子
            </el-button>
          </div>
          <el-button 
            v-else
            type="primary" 
            @click="handlePostClick" 
            icon="el-icon-edit">
            登录后发帖
          </el-button>
        </div>
      </div>
    </div>

    <!-- 论坛主体 -->
    <div class="forum-main">
      <!-- 论坛分类 -->
      <div class="forum-categories">
        <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange" class="custom-tabs">
          <el-tab-pane v-for="cat in categories" :key="cat.id" :label="cat.name" :name="cat.id">
            <!-- 帖子列表 -->
            <div class="post-list-wrapper">
              <div v-if="filteredPosts.length === 0" class="empty-state">
                <i class="el-icon-chat-dot-square"></i>
                <p>暂无相关帖子</p>
                <el-button 
                  type="primary" 
                  @click="handlePostClick"
                  :disabled="!isLoggedIn">
                  {{ isLoggedIn ? '发布第一个帖子' : '登录后发帖' }}
                </el-button>
              </div>
              <post-list v-else :posts="filteredPosts" @view-post="viewPost" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 侧边栏 -->
      <div class="forum-sidebar">
        <!-- 论坛统计 -->
        <div class="sidebar-card forum-stats">
          <h3>论坛统计</h3>
          <div class="stats-content">
            <div class="stat-item">
              <i class="el-icon-document"></i>
              <div class="stat-info">
                <span class="stat-value">{{ totalPosts }}</span>
                <span class="stat-label">帖子</span>
              </div>
            </div>
            <div class="stat-item">
              <i class="el-icon-user"></i>
              <div class="stat-info">
                <span class="stat-value">{{ totalUsers }}</span>
                <span class="stat-label">用户</span>
              </div>
            </div>
            <div class="stat-item">
              <i class="el-icon-chat-dot-round"></i>
              <div class="stat-info">
                <span class="stat-value">{{ totalComments }}</span>
                <span class="stat-label">评论</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 热门标签 -->
        <div class="sidebar-card hot-tags">
          <h3>热门标签</h3>
          <div class="tags-content">
            <el-tag
              v-for="tag in hotTags"
              :key="tag.name"
              :type="tag.type"
              size="small"
              effect="plain"
              class="tag-item"
            >
              {{ tag.name }}
              <span class="tag-count">({{ tag.count }})</span>
            </el-tag>
          </div>
        </div>

        <!-- 活跃用户 -->
        <div class="sidebar-card active-users">
          <h3>活跃用户</h3>
          <div class="users-content">
            <div v-for="user in activeUsers" :key="user.id" class="user-item">
              <el-avatar :src="user.avatar" :size="32"></el-avatar>
              <div class="user-info">
                <span class="username">{{ user.name }}</span>
                <span class="post-count">发帖 {{ user.posts }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 发帖对话框 -->
    <post-form
      v-model="showPostDialog"
      :categories="categories"
      @submit="submitPost"
    />
  </div>
</template>

<script>
import { getPostList, createPost, getCategories, getStats } from '@/api/forum'
import { getToken } from '@/utils/auth'
import { mapGetters } from 'vuex'
import PostList from './components/PostList.vue'
import PostForm from './components/PostForm.vue'
import GlobalNavbar from '@/components/GlobalNavbar'

export default {
  name: 'Forum',
  components: {
    PostList,
    PostForm,
    GlobalNavbar
  },
  data() {
    return {
      searchQuery: '',
      activeCategory: 'all',
      showPostDialog: false,
      categories: [
        { id: 'all', name: '全部' }
      ],
      categoryMap: {},
      posts: [],
      hotTags: [
        { name: 'ChatGPT', count: 128, type: '' },
        { name: 'Stable Diffusion', count: 85, type: 'success' },
        { name: 'Midjourney', count: 76, type: 'warning' },
        { name: 'Claude', count: 64, type: 'info' },
        { name: 'Gemini', count: 52, type: 'danger' }
      ],
      activeUsers: [
        { id: 1, name: '张三', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 32 },
        { id: 2, name: '李四', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 28 },
        { id: 3, name: '王五', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', posts: 25 }
      ],
      stats: {
        totalPosts: 0,
        totalUsers: 0,
        totalComments: 0
      },
      searchTimer: null
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar'
    ]),
    filteredPosts() {
      let filtered = this.posts

      // 按分类筛选
      if (this.activeCategory !== 'all') {
        filtered = filtered.filter(post => String(post.categoryId) === String(this.activeCategory))
      }

      // 按搜索关键词筛选
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(post =>
          (post.title || '').toLowerCase().includes(query) ||
          (post.excerpt || '').toLowerCase().includes(query)
        )
      }

      return filtered
    },
    totalPosts() {
      return this.stats.totalPosts
    },
    totalUsers() {
      return this.stats.totalUsers
    },
    totalComments() {
      return this.stats.totalComments
    },
    isLoggedIn() {
      return !!getToken()
    },
    userName() {
      return this.name || '用户'
    },
    userAvatar() {
      return this.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  },
  created() {
    this.initialize()
  },
  methods: {
    async initialize() {
      await this.fetchCategories()
      await this.fetchPosts()
      await this.fetchStats()
    },
    async fetchCategories() {
      try {
        const response = await getCategories()
        if (response.code === 200) {
          const fetched = (response.data || [])
            .filter(item => item.status !== '1')
            .map(item => ({
              id: String(item.categoryId),
              name: item.categoryName,
              description: item.description
            }))
          this.categories = [{ id: 'all', name: '全部' }, ...fetched]
          this.categoryMap = fetched.reduce((acc, cur) => {
            acc[cur.id] = cur.name
            return acc
          }, {})
        }
      } catch (error) {
        console.error('获取分类列表失败:', error)
        this.$message.error('获取分类列表失败')
      }
    },
    async fetchPosts() {
      try {
        const query = {
          title: this.searchQuery || undefined
        }
        if (this.activeCategory !== 'all') {
          query.categoryId = this.activeCategory
        }
        const response = await getPostList(query)
        if (response.code === 200) {
          // 适配后端返回的数据结构
          const list = Array.isArray(response.rows) ? response.rows : []
          this.posts = list.map(item => {
            const content = item.content || ''
            return {
              id: item.postId,
              title: item.title,
              excerpt: content.length > 100 ? content.substring(0, 100) + '...' : content,
              author: {
                name: item.nickName || item.userName,
                avatar: item.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
              },
              createTime: item.createTime,
              views: item.viewCount || 0,
              comments: item.replyCount || 0,
              likes: item.likeCount || 0,
              categoryId: item.categoryId ? String(item.categoryId) : undefined,
              categoryName: item.categoryName
            }
          })
        }
      } catch (error) {
        console.error('获取帖子列表失败:', error)
        this.$message.error('获取帖子列表失败')
      }
    },
    handleCategoryChange(tab) {
      this.activeCategory = tab.name || tab.paneName || 'all'
      this.fetchPosts()
    },
    viewPost(post) {
      this.$router.push(`/forum/post/${post.id}`)
    },
    async submitPost(postData) {
      try {
        const submitData = {
          title: postData.title,
          content: postData.content,
          categoryId: Number(postData.category),
          contentType: '1', // 1表示文本
          tags: postData.tags.join(',') // 将标签数组转换为字符串
        }
        
        console.log('提交发帖数据:', submitData)
        
        const response = await createPost(submitData)
        console.log('发帖响应:', response)
        
        if (response.code === 200) {
          this.$message.success('发布成功！')
          this.showPostDialog = false
          // 重新获取帖子列表
          await this.fetchPosts()
          // 重新获取统计数据
          await this.fetchStats()
        } else {
          this.$message.error(response.msg || '发布失败，请重试')
        }
      } catch (error) {
        console.error('发布帖子失败:', error)
        this.$message.error('发布失败，请重试')
      }
    },
    async fetchStats() {
      try {
        const response = await getStats()
        if (response.code === 200) {
          this.stats = {
            totalPosts: response.data?.totalPosts || 0,
            totalUsers: response.data?.totalUsers || 0,
            totalComments: response.data?.totalComments || 0
          }
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },
    goBack() {
      this.$router.push('/introduction');
    },
    handlePostClick() {
      if (this.isLoggedIn) {
        this.showPostDialog = true;
      } else {
        this.$message.warning('请先登录才能发帖！');
        this.$router.push('/login');
      }
    }
  },
  watch: {
    searchQuery: {
      handler(val) {
        if (this.searchTimer) {
          clearTimeout(this.searchTimer)
        }
        this.searchTimer = setTimeout(() => {
          this.fetchPosts()
        }, 300)
      }
    }
  }
}
</script>

<style scoped lang="scss">
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

.forum-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  &.has-global-navbar {
    padding-top: 84px; // 为导航栏预留空间
  }
}

.forum-header {
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
  margin: -20px -20px 30px;
  padding: 40px 20px;
  color: #fff;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-left {
    margin-left: 90px; // 调整左边距，与返回按钮位置协调
    
    h1 {
      font-size: 32px;
      font-weight: 600;
      margin: 0 0 8px;
    }

    .subtitle {
      font-size: 16px;
      opacity: 0.8;
      margin: 0;
    }
  }

      .forum-actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .search-input {
        width: 300px;
      }

      .user-info {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px 16px;
        background: #f8fafc;
        border-radius: 8px;
        border: 1px solid #e5e7eb;

        .user-avatar {
          border: 2px solid #fff;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .username {
          font-weight: 500;
          color: #374151;
          font-size: 14px;
        }

        .el-button {
          margin-left: 8px;
        }
      }
    }

  // 移动端响应式调整
  @media (max-width: 768px) {
    .header-left {
      margin-left: 75px; // 与移动端返回按钮位置协调
    }

    .forum-actions {
      flex-direction: column;
      gap: 12px;

      .search-input {
        width: 100%;
      }
    }
  }

  @media (max-width: 480px) {
    .header-left {
      margin-left: 70px; // 小屏幕调整边距
    }

    .header-content {
      flex-direction: column;
      align-items: flex-start;
      gap: 20px;
    }
  }
}

.forum-main {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
}

.forum-categories {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);

  .custom-tabs {
    :deep(.el-tabs__header) {
      margin-bottom: 20px;
    }

    :deep(.el-tabs__nav-wrap::after) {
      height: 1px;
      background-color: #e5e7eb;
    }

    :deep(.el-tabs__item) {
      font-size: 15px;
      padding: 0 20px;
      height: 40px;
      line-height: 40px;
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

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #6b7280;

  i {
    font-size: 48px;
    margin-bottom: 16px;
  }

  p {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.forum-sidebar {
  .sidebar-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 24px;
    box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);

    &:last-child {
      margin-bottom: 0;
    }

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #374151;
      margin: 0 0 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #e5e7eb;
    }
  }

  .forum-stats {
    .stats-content {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
    }

    .stat-item {
      text-align: center;
      padding: 12px;
      border-radius: 6px;
      background: #f3f4f6;
      transition: all 0.3s;

      &:hover {
        background: #e5e7eb;
      }

      i {
        font-size: 24px;
        color: #1e40af;
        margin-bottom: 8px;
      }

      .stat-info {
        .stat-value {
          display: block;
          font-size: 18px;
          font-weight: 600;
          color: #1f2937;
        }

        .stat-label {
          font-size: 13px;
          color: #6b7280;
        }
      }
    }
  }

  .hot-tags {
    .tags-content {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .tag-item {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-1px);
      }

      .tag-count {
        font-size: 12px;
        opacity: 0.7;
        margin-left: 2px;
      }
    }
  }

  .active-users {
    .users-content {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .user-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 8px;
      border-radius: 6px;
      transition: all 0.3s;

      &:hover {
        background: #f3f4f6;
      }

      .user-info {
        .username {
          display: block;
          font-size: 14px;
          font-weight: 500;
          color: #374151;
        }

        .post-count {
          font-size: 12px;
          color: #6b7280;
        }
      }
    }
  }
}

// 移动端适应
@media (max-width: 768px) {
  .forum-container {
    .back-button-container {
      top: 10px;
      left: 10px;
      
      .back-button {
        width: 40px;
        height: 40px;
      }
    }

    .forum-header {
      padding: 80px 15px 30px;
      
      .header-content {
        flex-direction: column;
        align-items: stretch;
        gap: 20px;
        
        .header-left {
          margin-left: 0;
          text-align: center;
          
          h1 {
            font-size: 2rem;
          }
          
          .subtitle {
            font-size: 1rem;
          }
        }
        
        .forum-actions {
          .search-input {
            margin-bottom: 10px;
          }
          
          .el-button {
            width: 100%;
          }
        }
      }
    }

    .forum-main {
      grid-template-columns: 1fr;
      gap: 20px;
      padding: 0 15px;
      
      .forum-categories {
        padding: 15px;
        
        .custom-tabs {
          :deep(.el-tabs__nav-scroll) {
            overflow-x: auto;
          }
          
          :deep(.el-tabs__item) {
            padding: 0 15px;
            font-size: 14px;
            white-space: nowrap;
          }
        }
      }
      
      .forum-sidebar {
        .sidebar-card {
          padding: 15px;
          margin-bottom: 20px;
          
          h3 {
            font-size: 15px;
            margin-bottom: 12px;
          }
        }
        
        .forum-stats {
          .stats-content {
            grid-template-columns: repeat(3, 1fr);
            gap: 8px;
          }
          
          .stat-item {
            padding: 8px;
            
            i {
              font-size: 20px;
              margin-bottom: 6px;
            }
            
            .stat-info {
              .stat-value {
                font-size: 16px;
              }
              
              .stat-label {
                font-size: 12px;
              }
            }
          }
        }
        
        .hot-tags {
          .tags-content {
            gap: 6px;
          }
          
          .tag-item {
            font-size: 12px;
            padding: 4px 8px;
          }
        }
        
        .active-users {
          .users-content {
            gap: 10px;
          }
          
          .user-item {
            padding: 6px;
            
            .user-info {
              .username {
                font-size: 13px;
              }
              
              .post-count {
                font-size: 11px;
              }
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .forum-container {
    .forum-header {
      padding: 70px 10px 20px;
      
      .header-content {
        .header-left {
          h1 {
            font-size: 1.8rem;
          }
          
          .subtitle {
            font-size: 0.9rem;
          }
        }
      }
    }
    
    .forum-main {
      padding: 0 10px;
      
      .forum-categories {
        padding: 10px;
      }
      
      .forum-sidebar {
        .sidebar-card {
          padding: 12px;
          
          h3 {
            font-size: 14px;
          }
        }
        
        .forum-stats {
          .stats-content {
            grid-template-columns: 1fr;
            gap: 6px;
          }
          
          .stat-item {
            padding: 6px;
            
            .stat-info {
              .stat-value {
                font-size: 14px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
