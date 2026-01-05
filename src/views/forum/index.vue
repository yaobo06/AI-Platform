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
          <el-autocomplete
            v-model="searchQuery"
            :fetch-suggestions="querySearch"
            placeholder="搜索帖子"
            prefix-icon="el-icon-search"
            class="search-input"
            clearable
            @select="handleSearchSelect"
            @keyup.enter.native="handleSearch"
            @focus="handleSearchFocus"
            popper-class="search-suggestions"
          />
          <el-button
            type="primary"
            icon="el-icon-search"
            @click="handleSearch"
            class="search-button">
            搜索
          </el-button>
          <el-button
            type="primary"
            @click="handlePostClick"
            icon="el-icon-edit">
            {{ isLoggedIn ? '发布帖子' : '登录后发帖' }}
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
          <div class="tags-content" v-if="hotTags && hotTags.length > 0">
            <el-tag
              v-for="(tag, index) in hotTags"
              :key="tag.tagName || tag.name || index"
              :type="getTagType(index)"
              size="small"
              effect="plain"
              class="tag-item"
              @click="searchTag(tag.tagName || tag.name)"
            >
              {{ tag.tagName || tag.name }}
              <span class="tag-count">({{ tag.count || 0 }})</span>
            </el-tag>
          </div>
          <div v-else class="empty-tags">
            <p>暂无热门标签</p>
          </div>
        </div>

        <!-- 大家都在看 -->
        <div class="sidebar-card trending-posts">
          <h3>大家都在看</h3>
          <div class="trending-content">
            <div
              v-for="post in trendingPosts"
              :key="post.id"
              class="trending-item"
              @click="viewPost({ id: post.id })">
              <div class="trending-post-info">
                <div class="trending-title-wrapper">
                  <span class="trending-title-main">{{ post.mainTitle || post.title }}</span>
                  <span v-if="post.subTitle" class="trending-title-sub">{{ post.subTitle }}</span>
                </div>
                <span class="author-name">{{ post.authorName }}</span>
              </div>
            </div>
            <div v-if="trendingPosts.length === 0" class="empty-trending">
              <p>暂无热门帖子</p>
            </div>
          </div>
          <!-- 分页 -->
          <el-pagination
            v-if="trendingTotal > trendingPageSize"
            small
            layout="prev, pager, next"
            :total="trendingTotal"
            :page-size="trendingPageSize"
            :current-page="trendingCurrentPage"
            @current-change="handleTrendingPageChange"
            class="trending-pagination">
          </el-pagination>
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
import { getPostList, createPost, getStats, getTrendingPosts, getHotTags } from '@/api/forum'
import { fetchAndFormatCategories } from '@/utils/forum-utils'
import { getForumToken } from '@/utils/forum-auth'
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
      trendingPosts: [],
      trendingCurrentPage: 1,
      trendingPageSize: 5,
      trendingTotal: 0,
      stats: {
        totalPosts: 0,
        totalUsers: 0,
        totalComments: 0
      },
      searchTimer: null
    }
  },
  computed: {
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
      return !!getForumToken()
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
      await this.fetchTrendingPosts()
      await this.fetchHotTags()
    },
    async fetchCategories() {
      const { categories, categoryMap } = await fetchAndFormatCategories()
      this.categories = categories
      this.categoryMap = categoryMap
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
              id: item.id || item.postId, // 兼容两种字段名
              title: item.title,
              excerpt: content.length > 100 ? content.substring(0, 100) + '...' : content,
              author: {
                name: item.authorName || item.nickName || item.userName,
                avatar: item.authorAvatarUrl || item.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
              },
              createTime: item.createdAt || item.createTime,
              views: item.viewCount || 0,
              comments: item.commentCount || item.replyCount || 0,
              likes: item.likeCount || 0,
              categoryId: item.categoryId ? String(item.categoryId) : (item.category || undefined),
              categoryName: item.categoryName || item.category
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
        // 将 categoryId 转换为 category 名称
        let category = postData.category
        if (category && this.categoryMap[category]) {
          // 如果 category 是 ID，转换为名称
          category = this.categoryMap[category]
        }

        const submitData = {
          title: postData.title,
          content: postData.content,
          category: category || postData.category, // 优先使用 category 名称
          categoryId: postData.category ? Number(postData.category) : undefined // 兼容 categoryId
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
    async fetchTrendingPosts() {
      try {
        const response = await getTrendingPosts(this.trendingCurrentPage, this.trendingPageSize)
        if (response.code === 200) {
          const data = response.data || {}
          this.trendingPosts = data.records || []
          // 确保分页信息正确更新
          if (data.total !== undefined) {
            this.trendingTotal = data.total || 0
          }
          if (data.current !== undefined) {
            this.trendingCurrentPage = data.current || 1
          }
          if (data.size !== undefined) {
            this.trendingPageSize = data.size || 5
          }
        }
      } catch (error) {
        console.error('获取热门帖子失败:', error)
      }
    },
    handleTrendingPageChange(page) {
      this.trendingCurrentPage = page
      this.fetchTrendingPosts()
    },
    async fetchHotTags() {
      try {
        const response = await getHotTags(10)
        if (response.code === 200) {
          this.hotTags = response.data || []
        }
      } catch (error) {
        console.error('获取热门标签失败:', error)
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
        this.$router.push({ path: '/forum/login', query: { redirect: '/forum' } });
      }
    },
    handleSearch() {
      if (this.searchQuery && this.searchQuery.trim()) {
        this.$router.push({
          path: '/forum/search',
          query: {
            q: this.searchQuery.trim(),
            mode: 'hybrid'
          }
        })
      } else {
        this.$message.warning('请输入搜索关键词')
      }
    },
    handleSearchFocus() {
      // 搜索框获得焦点时，显示搜索建议
    },
    querySearch(queryString, cb) {
      // 搜索建议功能
      if (!queryString || queryString.trim() === '') {
        // 如果没有输入，显示热门标签作为建议
        const suggestions = this.hotTags.slice(0, 5).map(tag => ({
          value: tag.name || tag.tagName,
          label: `${tag.name || tag.tagName} (${tag.count || 0})`
        }))
        cb(suggestions)
        return
      }

      // 从帖子标题中搜索匹配的建议
      const query = queryString.toLowerCase()
      const suggestions = this.posts
        .filter(post => {
          const title = (post.title || '').toLowerCase()
          return title.includes(query)
        })
        .slice(0, 5)
        .map(post => ({
          value: post.title,
          label: post.title
        }))

      cb(suggestions)
    },
    handleSearchSelect(item) {
      this.searchQuery = item.value
      this.handleSearch()
    },
    searchTag(tagName) {
      if (tagName) {
        this.searchQuery = tagName
        this.handleSearch()
      }
    },
    getTagType(index) {
      const types = ['', 'success', 'warning', 'info', 'danger']
      return types[index % types.length]
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
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .tag-count {
        font-size: 12px;
        opacity: 0.7;
        margin-left: 2px;
      }
    }

    .empty-tags {
      text-align: center;
      padding: 20px;
      color: #909399;
      font-size: 14px;
    }
  }

  .trending-posts {
    .trending-content {
      display: flex;
      flex-direction: column;
      gap: 10px;
      margin-bottom: 16px;
    }

    .trending-item {
      padding: 12px 14px;
      background: #fff;
      border: 1px solid #e5e7eb;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
        background: #f5f7fa;
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
      }

      .trending-post-info {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .trending-title-wrapper {
          display: flex;
          flex-direction: column;
          gap: 4px;
        }

        .trending-title-main {
          font-size: 14px;
          font-weight: 600;
          color: #303133;
          line-height: 1.4;
        }

        .trending-title-sub {
          font-size: 12px;
          font-weight: 400;
          color: #909399;
          line-height: 1.4;
          padding-left: 8px;
          border-left: 2px solid #e5e7eb;
          opacity: 0.8;
        }

        .author-name {
          font-size: 12px;
          color: #909399;
          margin-top: 2px;
        }
      }
    }

    .empty-trending {
      text-align: center;
      padding: 20px;
      color: #909399;
      font-size: 14px;
    }

    .trending-pagination {
      margin-top: 12px;
      display: flex;
      justify-content: center;
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

        .trending-posts {
          .trending-content {
            gap: 8px;
          }

          .trending-item {
            padding: 10px 12px;

            .trending-title-main {
              font-size: 13px;
            }

            .trending-title-sub {
              font-size: 11px;
            }

            .author-name {
              font-size: 11px;
            }
          }

          .trending-pagination {
            :deep(.el-pagination) {
              .el-pager li {
                min-width: 28px;
                height: 28px;
                line-height: 28px;
                font-size: 12px;
              }

              .btn-prev,
              .btn-next {
                min-width: 28px;
                height: 28px;
                line-height: 28px;
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

<style lang="scss">
// 搜索建议下拉框样式
.search-suggestions {
  .el-autocomplete-suggestion__list {
    max-height: 300px;

    li {
      padding: 12px 20px;
      line-height: 1.5;
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f7fa;
      }

      .highlight {
        color: #409eff;
        font-weight: 600;
      }
    }
  }
}
</style>

