<template>
  <div class="profile-container has-global-navbar">
    <!-- 全局导航栏 -->
    <GlobalNavbar />

    <div class="profile-content">
      <div class="profile-header">
        <h1>个人中心</h1>
      </div>

      <div v-if="user" class="profile-main">
        <!-- 用户信息卡片 -->
        <div class="user-card">
          <div class="user-header">
            <div class="avatar-wrapper">
              <el-avatar
                :src="avatarUrl"
                :size="80"
                class="avatar-large">
                <i class="el-icon-user-solid"></i>
              </el-avatar>
            </div>
            <div class="user-info">
              <div class="user-name">{{ user.nickName || user.userName }}</div>
              <div class="user-id">@{{ user.userName }}</div>
            </div>
          </div>
        </div>

        <!-- 内容标签页 -->
        <div class="content-tabs">
          <el-tabs v-model="activeTab" @tab-click="handleTabChange">
            <el-tab-pane label="我的发帖" name="posts">
              <div class="content-list">
                <div v-if="loadingPosts" class="loading">
                  <i class="el-icon-loading"></i> 加载中...
                </div>
                <div v-else-if="myPosts.length === 0" class="empty">
                  <i class="el-icon-document"></i>
                  <p>还没有发过帖子</p>
                </div>
                <div v-else class="post-items">
                  <div
                    v-for="post in myPosts"
                    :key="post.id || post.postId"
                    class="post-item"
                    @click="viewPost(post)">
                    <h3 class="post-title">{{ post.title }}</h3>
                    <p class="post-excerpt">{{ getExcerpt(post.content) }}</p>
                    <div class="post-meta">
                      <span class="time">
                        <i class="el-icon-time"></i>
                        {{ formatTime(post.createdAt) }}
                      </span>
                      <div class="stats">
                        <span class="stat-item">
                          <i class="el-icon-view"></i>
                          {{ post.viewCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <i class="el-icon-chat-dot-round"></i>
                          {{ post.commentCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <i class="el-icon-star-off"></i>
                          {{ post.likeCount || 0 }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="我的点赞" name="liked">
              <div class="content-list">
                <div v-if="loadingLiked" class="loading">
                  <i class="el-icon-loading"></i> 加载中...
                </div>
                <div v-else-if="myLikedPosts.length === 0" class="empty">
                  <i class="el-icon-star-off"></i>
                  <p>还没有点赞过帖子</p>
                </div>
                <div v-else class="post-items">
                  <div
                    v-for="post in myLikedPosts"
                    :key="post.id || post.postId"
                    class="post-item"
                    @click="viewPost(post)">
                    <h3 class="post-title">{{ post.title }}</h3>
                    <p class="post-excerpt">{{ getExcerpt(post.content) }}</p>
                    <div class="post-meta">
                      <span class="time">
                        <i class="el-icon-time"></i>
                        {{ formatTime(post.createdAt) }}
                      </span>
                      <div class="stats">
                        <span class="stat-item">
                          <i class="el-icon-view"></i>
                          {{ post.viewCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <i class="el-icon-chat-dot-round"></i>
                          {{ post.commentCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <i class="el-icon-star-off"></i>
                          {{ post.likeCount || 0 }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="我的收藏" name="favorited">
              <div class="content-list">
                <div v-if="loadingFavorited" class="loading">
                  <i class="el-icon-loading"></i> 加载中...
                </div>
                <div v-else-if="myFavoritedPosts.length === 0" class="empty">
                  <i class="el-icon-collection"></i>
                  <p>还没有收藏过帖子</p>
                </div>
                <div v-else class="post-items">
                  <div
                    v-for="post in myFavoritedPosts"
                    :key="post.id || post.postId"
                    class="post-item"
                    @click="viewPost(post)">
                    <h3 class="post-title">{{ post.title }}</h3>
                    <p class="post-excerpt">{{ getExcerpt(post.content) }}</p>
                    <div class="post-meta">
                      <span class="time">
                        <i class="el-icon-time"></i>
                        {{ formatTime(post.createdAt) }}
                      </span>
                      <div class="stats">
                        <span class="stat-item">
                          <i class="el-icon-view"></i>
                          {{ post.viewCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <i class="el-icon-chat-dot-round"></i>
                          {{ post.commentCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <i class="el-icon-star-off"></i>
                          {{ post.likeCount || 0 }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane label="我的评论" name="comments">
              <div class="content-list">
                <div v-if="loadingComments" class="loading">
                  <i class="el-icon-loading"></i> 加载中...
                </div>
                <div v-else-if="myComments.length === 0" class="empty">
                  <i class="el-icon-chat-dot-round"></i>
                  <p>还没有评论过</p>
                </div>
                <div v-else class="comment-items">
                  <div
                    v-for="comment in myComments"
                    :key="comment.id"
                    class="comment-item"
                    @click="viewPostById(comment.postId)">
                    <div class="comment-content">{{ comment.content }}</div>
                    <div class="comment-meta">
                      <span class="time">
                        <i class="el-icon-time"></i>
                        {{ formatTime(comment.createdAt) }}
                      </span>
                      <span class="post-link">
                        查看原帖 <i class="el-icon-arrow-right"></i>
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <div v-else class="loading-container">
        <i class="el-icon-loading"></i>
        <p>加载中...</p>
      </div>
    </div>
  </div>
</template>

<script>
import GlobalNavbar from '@/components/GlobalNavbar'
import { getForumUserInfo } from '@/api/forum-user'
import { getMyPosts, getMyLikedPosts, getMyFavoritedPosts, getMyComments } from '@/api/forum'

export default {
  name: 'ForumProfile',
  components: {
    GlobalNavbar
  },
  data() {
    return {
      user: null,
      activeTab: 'posts',
      myPosts: [],
      myLikedPosts: [],
      myFavoritedPosts: [],
      myComments: [],
      loadingPosts: false,
      loadingLiked: false,
      loadingFavorited: false,
      loadingComments: false,
      pagePosts: 1,
      pageLiked: 1,
      pageFavorited: 1,
      pageComments: 1
    }
  },
  computed: {
    avatarUrl() {
      if (!this.user || !this.user.avatar) {
        return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      }
      return this.user.avatar.startsWith('http')
        ? this.user.avatar
        : `${process.env.VUE_APP_BASE_API}${this.user.avatar}`
    }
  },
  created() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        const response = await getForumUserInfo()
        if (response.code === 200) {
          this.user = response.data?.user || response.user || response.data
          // 默认加载我的发帖
          this.loadMyPosts()
        } else {
          this.$message.error('获取用户信息失败')
          this.$router.push('/forum/login')
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.$message.error('获取用户信息失败')
        this.$router.push('/forum/login')
      }
    },
    async loadMyPosts() {
      this.loadingPosts = true
      try {
        const response = await getMyPosts(this.pagePosts, 10)
        if (response.code === 200) {
          this.myPosts = response.data?.records || response.data?.list || []
        }
      } catch (error) {
        console.error('获取我的发帖失败:', error)
      } finally {
        this.loadingPosts = false
      }
    },
    async loadMyLikedPosts() {
      this.loadingLiked = true
      try {
        const response = await getMyLikedPosts(this.pageLiked, 10)
        if (response.code === 200) {
          this.myLikedPosts = response.data?.records || response.data?.list || []
        }
      } catch (error) {
        console.error('获取我的点赞失败:', error)
      } finally {
        this.loadingLiked = false
      }
    },
    async loadMyFavoritedPosts() {
      this.loadingFavorited = true
      try {
        const response = await getMyFavoritedPosts(this.pageFavorited, 10)
        if (response.code === 200) {
          this.myFavoritedPosts = response.data?.records || response.data?.list || []
        }
      } catch (error) {
        console.error('获取我的收藏失败:', error)
      } finally {
        this.loadingFavorited = false
      }
    },
    async loadMyComments() {
      this.loadingComments = true
      try {
        const response = await getMyComments(this.pageComments, 10)
        if (response.code === 200) {
          this.myComments = response.data?.records || response.data?.list || []
        }
      } catch (error) {
        console.error('获取我的评论失败:', error)
      } finally {
        this.loadingComments = false
      }
    },
    handleTabChange(tab) {
      const tabName = tab.name
      if (tabName === 'posts' && this.myPosts.length === 0) {
        this.loadMyPosts()
      } else if (tabName === 'liked' && this.myLikedPosts.length === 0) {
        this.loadMyLikedPosts()
      } else if (tabName === 'favorited' && this.myFavoritedPosts.length === 0) {
        this.loadMyFavoritedPosts()
      } else if (tabName === 'comments' && this.myComments.length === 0) {
        this.loadMyComments()
      }
    },
    viewPost(post) {
      const postId = post.id || post.postId
      if (postId) {
        this.$router.push(`/forum/post/${postId}`)
      }
    },
    viewPostById(postId) {
      if (postId) {
        this.$router.push(`/forum/post/${postId}`)
      }
    },
    getExcerpt(content) {
      if (!content) return ''
      return content.length > 100 ? content.substring(0, 100) + '...' : content
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
    }
  }
}
</script>

<style scoped lang="scss">
.profile-container {
  min-height: 100vh;
  background: #f5f6fa;
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  margin-bottom: 20px;

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #1f2937;
  }
}

.profile-main {
  .user-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    .user-header {
      display: flex;
      align-items: center;
      gap: 20px;

      .avatar-wrapper {
        .avatar-large {
          border: 2px solid #e5e7eb;
        }
      }

      .user-info {
        flex: 1;

        .user-name {
          font-size: 20px;
          font-weight: 600;
          color: #1f2937;
          margin-bottom: 4px;
        }

        .user-id {
          font-size: 14px;
          color: #6b7280;
        }
      }
    }
  }

  .content-tabs {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    .content-list {
      min-height: 400px;
      padding: 20px 0;

      .loading, .empty {
        text-align: center;
        padding: 60px 20px;
        color: #9ca3af;

        i {
          font-size: 48px;
          margin-bottom: 16px;
          display: block;
        }
      }

      .post-items, .comment-items {
        .post-item, .comment-item {
          padding: 16px;
          border-bottom: 1px solid #e5e7eb;
          cursor: pointer;
          transition: background 0.3s;

          &:hover {
            background: #f9fafb;
          }

          &:last-child {
            border-bottom: none;
          }
        }

        .post-item {
          .post-title {
            font-size: 16px;
            font-weight: 600;
            color: #1f2937;
            margin: 0 0 8px;
          }

          .post-excerpt {
            font-size: 14px;
            color: #6b7280;
            margin: 0 0 12px;
            line-height: 1.5;
          }

          .post-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 12px;
            color: #9ca3af;

            .time {
              display: flex;
              align-items: center;
              gap: 4px;
            }

            .stats {
              display: flex;
              gap: 16px;

              .stat-item {
                display: flex;
                align-items: center;
                gap: 4px;
              }
            }
          }
        }

        .comment-item {
          .comment-content {
            font-size: 14px;
            color: #374151;
            margin-bottom: 8px;
            line-height: 1.5;
          }

          .comment-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 12px;
            color: #9ca3af;

            .post-link {
              color: #1e40af;
              cursor: pointer;
              display: flex;
              align-items: center;
              gap: 4px;

              &:hover {
                text-decoration: underline;
              }
            }
          }
        }
      }
    }
  }
}

.loading-container {
  text-align: center;
  padding: 100px 20px;
  color: #9ca3af;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
  }
}
</style>

