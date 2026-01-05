<template>
  <div class="search-container has-global-navbar">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button
        class="back-button"
        type="text"
        @click="goBack"
        title="返回论坛">
        <i class="el-icon-back"></i>
        <span>返回论坛</span>
      </el-button>
    </div>

    <!-- 搜索框区域 -->
    <div class="search-box-container">
      <div class="search-box">
        <el-autocomplete
          v-model="searchQuery"
          :fetch-suggestions="querySearchSuggestions"
          placeholder="输入关键词搜索帖子..."
          prefix-icon="el-icon-search"
          size="large"
          clearable
          @select="handleSearchSelect"
          @keyup.enter.native="handleSearch"
          @clear="handleClear"
          class="search-input-large"
          popper-class="search-suggestions-popper"
          :trigger-on-focus="true">
        </el-autocomplete>
        <el-button
          type="primary"
          size="large"
          icon="el-icon-search"
          @click="handleSearch"
          :loading="loading">
          搜索
        </el-button>
      </div>

      <!-- 搜索模式选择 -->
      <div class="search-modes">
        <el-radio-group v-model="searchMode" @change="handleModeChange" size="small">
          <el-radio-button label="hybrid">智能搜索</el-radio-button>
          <el-radio-button label="deepseek">DeepSeek 搜索</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results" v-if="hasSearched">
      <div class="results-header">
        <div class="results-info">
          <span class="results-count">找到 <strong>{{ total }}</strong> 条结果</span>
          <span class="search-mode-badge" :class="searchMode">
            {{ getModeName(searchMode) }}
          </span>
        </div>
        <div class="results-sort">
          <el-select v-model="sortBy" size="small" @change="handleSortChange">
            <el-option label="相关度" value="relevance"></el-option>
            <el-option label="最新" value="latest"></el-option>
            <el-option label="最热" value="hot"></el-option>
          </el-select>
        </div>
      </div>

      <!-- 加载中 -->
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading"></i>
        <p>搜索中...</p>
      </div>

      <!-- 结果列表 -->
      <div v-else-if="results.length > 0" class="results-list">
        <div
          v-for="post in results"
          :key="post.id"
          class="result-item"
          @click="viewPost(post)">
          <div class="result-header">
            <h3 class="result-title">{{ post.title }}</h3>
            <div class="result-meta">
              <span class="author">
                <img :src="post.authorAvatarUrl || defaultAvatar" class="avatar-small" />
                {{ post.authorName || '匿名用户' }}
              </span>
              <span class="time">{{ formatTime(post.createdAt) }}</span>
            </div>
          </div>
          <div class="result-content">
            <p>{{ getExcerpt(post.content) }}</p>
          </div>
          <div class="result-footer">
            <div class="result-stats">
              <span class="stat-item">
                <i class="el-icon-view"></i>
                {{ post.viewCount || 0 }}
              </span>
              <span class="stat-item">
                <i class="el-icon-chat-line-round"></i>
                {{ post.commentCount || 0 }}
              </span>
              <span class="stat-item">
                <i class="el-icon-star-on"></i>
                {{ post.likeCount || 0 }}
              </span>
            </div>
            <div class="result-category" v-if="post.category">
              <el-tag size="mini">{{ post.category }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > pageSize">
          <el-pagination
            @current-change="handlePageChange"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next, jumper, total"
            background>
          </el-pagination>
        </div>
      </div>

      <!-- 无结果 -->
      <div v-else class="empty-results">
        <i class="el-icon-search"></i>
        <p>没有找到相关帖子</p>
        <el-button type="primary" @click="handleClear">重新搜索</el-button>
      </div>
    </div>

    <!-- 搜索提示（未搜索时显示） -->
    <div v-else class="search-tips">
      <div class="tips-content">
        <h3>搜索提示</h3>
        <ul>
          <li>支持关键词搜索，如："Spring Boot"、"Vue 3"</li>
          <li>智能搜索模式会自动扩展关键词，提高搜索准确性</li>
          <li>RAG 向量检索支持语义搜索，理解搜索意图</li>
          <li>可以尝试搜索技术问题、工具名称、概念等</li>
        </ul>
        <div class="hot-searches">
          <h4>热门搜索</h4>
          <div class="hot-tags">
            <el-tag
              v-for="tag in hotSearches"
              :key="tag"
              @click="searchTag(tag)"
              class="hot-tag">
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { searchPosts } from '@/api/forum'
import GlobalNavbar from '@/components/GlobalNavbar/index.vue'

export default {
  name: 'ForumSearch',
  components: {
    GlobalNavbar
  },
  data() {
    return {
      searchQuery: '',
      searchMode: 'hybrid',
      loading: false,
      hasSearched: false,
      results: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      sortBy: 'relevance',
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      hotSearches: ['Spring Boot', 'Vue 3', 'AI 模型', '机器学习', '深度学习', 'ChatGPT'],
      searchSuggestTimer: null
    }
  },
  created() {
    // 从路由参数获取搜索关键词
    if (this.$route.query.q) {
      this.searchQuery = this.$route.query.q
      this.handleSearch()
    }
    if (this.$route.query.mode) {
      this.searchMode = this.$route.query.mode
    }
  },
  methods: {
    handleSearch() {
      if (!this.searchQuery || !this.searchQuery.trim()) {
        this.$message.warning('请输入搜索关键词')
        return
      }

      this.loading = true
      this.hasSearched = true
      this.currentPage = 1

      searchPosts(this.searchQuery, this.searchMode, this.currentPage, this.pageSize)
        .then(response => {
          this.loading = false
          if (response.code === 200 && response.data) {
            const data = response.data
            this.results = (data.results || []).map(item => ({
              id: item.id,
              title: item.title,
              content: item.content,
              authorName: item.authorName,
              authorAvatarUrl: item.authorAvatarUrl,
              createdAt: item.createdAt,
              viewCount: item.viewCount,
              commentCount: item.commentCount,
              likeCount: item.likeCount,
              category: item.category
            }))
            this.total = data.total || 0
          } else {
            this.results = []
            this.total = 0
          }
        })
        .catch(error => {
          this.loading = false
          console.error('搜索失败:', error)
          this.$message.error('搜索失败，请重试')
          this.results = []
          this.total = 0
        })
    },
    handleClear() {
      this.searchQuery = ''
      this.hasSearched = false
      this.results = []
      this.total = 0
      this.currentPage = 1
    },
    handleModeChange() {
      if (this.hasSearched) {
        this.handleSearch()
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.handleSearch()
    },
    handleSortChange() {
      // 重新搜索并排序
      this.handleSearch()
    },
    viewPost(post) {
      this.$router.push(`/forum/post/${post.id}`)
    },
    searchTag(tag) {
      this.searchQuery = tag
      this.handleSearch()
    },
    getExcerpt(content) {
      if (!content) return ''
      const text = content.replace(/<[^>]*>/g, '')
      return text.length > 150 ? text.substring(0, 150) + '...' : text
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      return date.toLocaleDateString()
    },
    getModeName(mode) {
      const names = {
        hybrid: '智能搜索',
        deepseek: 'DeepSeek 搜索'
      }
      return names[mode] || mode
    },
    goBack() {
      this.$router.push('/forum')
    },
    querySearchSuggestions(queryString, cb) {
      // 清除之前的定时器
      if (this.searchSuggestTimer) {
        clearTimeout(this.searchSuggestTimer)
      }

      // 实时搜索建议功能
      if (!queryString || queryString.trim() === '') {
        // 如果没有输入，显示热门搜索作为建议
        const suggestions = this.hotSearches.slice(0, 5).map(tag => ({
          value: tag,
          label: tag
        }))
        cb(suggestions)
        return
      }

      // 实时搜索：输入时就开始搜索，显示相关建议（防抖处理）
      const query = queryString.trim()
      if (query.length < 1) {
        cb([])
        return
      }

      // 防抖：延迟300ms后执行搜索
      this.searchSuggestTimer = setTimeout(() => {
        // 调用搜索API获取建议（限制返回数量）
        searchPosts(query, this.searchMode, 1, 5)
          .then(response => {
            if (response.code === 200 && response.data && response.data.results) {
              const suggestions = response.data.results.map(item => ({
                value: item.title,
                label: item.title
              }))
              cb(suggestions)
            } else {
              cb([])
            }
          })
          .catch(error => {
            console.error('获取搜索建议失败:', error)
            cb([])
          })
      }, 300)
    },
    handleSearchSelect(item) {
      this.searchQuery = item.value
      this.handleSearch()
    }
  }
}
</script>

<style scoped lang="scss">
.search-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1e40af 0%, #3b82f6 50%, #60a5fa 100%);
  padding-bottom: 40px;
}

.back-button-container {
  position: fixed;
  top: 80px;
  left: 20px;
  z-index: 1000;

  .back-button {
    background: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: white;
    padding: 8px 16px;
    border-radius: 20px;
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;

    &:hover {
      background: rgba(255, 255, 255, 0.3);
      border-color: rgba(255, 255, 255, 0.5);
      transform: translateX(-2px);
      color: white;
    }

    i {
      font-size: 16px;
    }

    span {
      margin-left: 4px;
    }
  }
}

.search-header {
  padding: 80px 20px 40px;
  text-align: center;
  color: white;

  h1 {
    font-size: 32px;
    margin: 0 0 20px 0;
    font-weight: 600;
  }
}

.search-box-container {
  max-width: 900px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;

  .search-input-large {
    flex: 1;

    ::v-deep .el-input__inner {
      height: 50px;
      font-size: 16px;
      border-radius: 25px;
      padding-left: 50px;
    }

    ::v-deep .el-input__prefix {
      left: 15px;
    }
  }

  .el-button {
    height: 50px;
    padding: 0 30px;
    font-size: 16px;
  }
}

.search-modes {
  text-align: center;
  margin-top: 20px;

  .el-radio-group {
    margin-bottom: 0;
  }
}

.search-results {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .results-info {
    display: flex;
    align-items: center;
    gap: 15px;

    .results-count {
      font-size: 16px;
      color: #333;

      strong {
        color: #409EFF;
        font-size: 18px;
      }
    }

    .search-mode-badge {
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;

      &.hybrid {
        background: #E6F7FF;
        color: #1890FF;
      }

      &.db {
        background: #F6FFED;
        color: #52C41A;
      }

      &.deepseek {
        background: #FFF7E6;
        color: #FA8C16;
      }

      &.rag {
        background: #F9F0FF;
        color: #722ED1;
      }
    }
  }
}

.loading-container {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;

  i {
    font-size: 32px;
    color: #409EFF;
    animation: rotate 1s linear infinite;
  }

  p {
    margin-top: 15px;
    color: #666;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.results-list {
  .result-item {
    background: white;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }

    .result-header {
      margin-bottom: 12px;

      .result-title {
        font-size: 20px;
        font-weight: 600;
        color: #333;
        margin: 0 0 10px 0;
        line-height: 1.4;
      }

      .result-meta {
        display: flex;
        align-items: center;
        gap: 15px;
        font-size: 14px;
        color: #999;

        .author {
          display: flex;
          align-items: center;
          gap: 6px;

          .avatar-small {
            width: 24px;
            height: 24px;
            border-radius: 50%;
          }
        }
      }
    }

    .result-content {
      margin-bottom: 16px;
      color: #666;
      line-height: 1.6;
      font-size: 15px;
    }

    .result-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .result-stats {
        display: flex;
        gap: 20px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #999;
          font-size: 14px;

          i {
            font-size: 16px;
          }
        }
      }
    }
  }
}

.pagination-container {
  margin-top: 30px;
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.empty-results {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 8px;

  i {
    font-size: 64px;
    color: #ddd;
    margin-bottom: 20px;
  }

  p {
    font-size: 18px;
    color: #999;
    margin-bottom: 30px;
  }
}

.search-tips {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.tips-content {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

  h3 {
    font-size: 24px;
    color: #333;
    margin: 0 0 20px 0;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0 0 30px 0;

    li {
      padding: 10px 0;
      color: #666;
      font-size: 15px;
      line-height: 1.6;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      &:before {
        content: "✓";
        color: #409EFF;
        font-weight: bold;
        margin-right: 10px;
      }
    }
  }

  .hot-searches {
    margin-top: 30px;
    padding-top: 30px;
    border-top: 1px solid #f0f0f0;

    h4 {
      font-size: 18px;
      color: #333;
      margin: 0 0 15px 0;
    }

    .hot-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;

      .hot-tag {
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          transform: scale(1.05);
        }
      }
    }
  }
}
</style>

<style lang="scss">
// 搜索建议下拉框样式
.search-suggestions-popper {
  .el-autocomplete-suggestion__list {
    max-height: 300px;

    li {
      padding: 12px 20px;
      line-height: 1.5;
      cursor: pointer;
      transition: background-color 0.3s;
      font-size: 14px;

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

