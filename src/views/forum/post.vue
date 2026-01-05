<template>
  <div class="post-detail-container">
    <!-- 帖子内容 -->
    <div class="post-content" v-if="post">
      <div class="post-header">
        <h1>{{ post.title }}</h1>
        <div class="post-meta">
          <el-avatar :src="post.author.avatar" :size="40"></el-avatar>
          <span class="author">{{ post.author.name }}</span>
          <span class="time">{{ post.createTime }}</span>
<span class="category">{{ post.categoryName || getCategoryName(post.categoryId) }}</span>
        </div>
      </div>
      <div class="post-body">
        {{ post.content }}
      </div>
      <div class="post-actions">
        <el-button
          type="text"
          @click="handleLike"
          :disabled="!isLoggedIn">
          <i :class="['el-icon-star-off', { 'is-liked': post.isLiked }]"></i>
          {{ post.likes }} {{ isLoggedIn ? '点赞' : '登录后点赞' }}
        </el-button>
        <el-button type="text">
          <i class="el-icon-chat-dot-round"></i>
          {{ post.comments }} 评论
        </el-button>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comments-section" v-if="post">
      <h2>评论 ({{ post.comments }})</h2>

      <!-- 评论输入框 -->
      <div class="comment-input">
        <div v-if="!isLoggedIn" class="login-prompt">
          <el-alert
            title="请先登录才能发表评论"
            type="info"
            :closable="false"
            show-icon>
            <template slot="default">
              <el-button type="primary" size="small" @click="goToLogin">立即登录</el-button>
            </template>
          </el-alert>
        </div>
        <div v-else>
          <el-input
            type="textarea"
            v-model="newComment"
            :rows="3"
            placeholder="写下你的评论..."
          ></el-input>
          <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <el-avatar :src="comment.author.avatar" :size="32"></el-avatar>
            <span class="author">{{ comment.author.name }}</span>
            <span class="time">{{ comment.createTime }}</span>
          </div>
          <div class="comment-content">
            {{ comment.content }}
          </div>
          <div class="comment-actions">
            <el-button type="text" size="small" @click="handleReply(comment)">回复</el-button>
          </div>
          <!-- 回复列表 -->
          <div v-if="comment.replies && comment.replies.length" class="reply-list">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <el-avatar :src="reply.author.avatar" :size="24"></el-avatar>
                <span class="author">{{ reply.author.name }}</span>
                <span class="time">{{ reply.createTime }}</span>
              </div>
              <div class="reply-content">
                {{ reply.content }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getPostDetail, likePost, unlikePost, getComments, addComment } from '@/api/forum'
import { fetchAndFormatCategories } from '@/utils/forum-utils'
import { getForumToken } from '@/utils/forum-auth'

export default {
  name: 'PostDetail',
  data() {
    return {
      post: null,
      comments: [],
      newComment: '',
      categories: [],
      categoryMap: {}
    }
  },
  computed: {
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
      await this.fetchPostDetail()
      await this.fetchComments()
    },
    async fetchCategories() {
      const { categories, categoryMap } = await fetchAndFormatCategories({ filterDisabled: false })
      this.categories = categories.filter(cat => cat.id !== 'all') // 帖子页面不需要"全部"
      this.categoryMap = categoryMap
    },
    async fetchPostDetail() {
      try {
        const postId = this.$route.params.id
        if (!postId) {
          this.$message.error('帖子ID不能为空')
          this.$router.push('/forum')
          return
        }
        const response = await getPostDetail(postId)
        if (response.code === 200) {
          // 后端返回的数据结构：{ data: { post: {...}, comments: {...} } }
          const postData = response.data.post || response.data
          this.post = {
            id: postData.id || postData.postId,
            title: postData.title,
            content: postData.content,
            author: {
              name: postData.authorName || postData.nickName || postData.userName,
              avatar: postData.authorAvatarUrl || postData.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
            },
            createTime: postData.createdAt || postData.createTime,
            views: postData.viewCount || 0,
            comments: postData.commentCount || postData.replyCount || 0,
            likes: postData.likeCount || 0,
            isLiked: postData.remark === '1',
            categoryId: postData.categoryId ? String(postData.categoryId) : (postData.category || undefined),
            categoryName: postData.categoryName || postData.category
          }
        }
      } catch (error) {
        console.error('获取帖子详情失败:', error)
        this.$message.error('获取帖子详情失败')
      }
    },
    async fetchComments() {
      try {
        const postId = this.$route.params.id
        if (!postId) {
          return
        }
        const response = await getComments(postId)
        if (response.code === 200) {
          // 后端返回的数据结构：{ data: { records: [...], total: ... } }
          let list = []
          if (response.data && Array.isArray(response.data)) {
            list = response.data
          } else if (response.data && response.data.records && Array.isArray(response.data.records)) {
            list = response.data.records
          } else if (response.data && response.data.list && Array.isArray(response.data.list)) {
            list = response.data.list
          }

          this.comments = list.map(item => ({
            id: item.id || item.commentId,
            content: item.content,
            author: {
              name: item.authorName || item.nickName || item.userName,
              avatar: item.authorAvatarUrl || item.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
            },
            createTime: item.createdAt || item.createTime,
            replies: Array.isArray(item.replies) ? item.replies.map(child => ({
              id: child.id || child.commentId,
              content: child.content,
              author: {
                name: child.authorName || child.nickName || child.userName,
                avatar: child.authorAvatarUrl || child.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
              },
              createTime: child.createdAt || child.createTime
            })) : []
          }))
        }
      } catch (error) {
        console.error('获取评论失败:', error)
        this.$message.error('获取评论失败')
      }
    },
    getCategoryName(categoryId) {
      if (!categoryId) return ''
      return this.categoryMap[categoryId] || ''
    },
    async handleLike() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录才能点赞！')
        this.$router.push({ path: '/forum/login', query: { redirect: this.$route.fullPath } })
        return
      }

      if (!this.post || !this.post.id) {
        this.$message.error('帖子信息不完整')
        return
      }

      try {
        if (this.post.isLiked) {
          await unlikePost(this.post.id)
          this.post.likes = Math.max((this.post.likes || 1) - 1, 0)
        } else {
          await likePost(this.post.id)
          this.post.likes = (this.post.likes || 0) + 1
        }
        this.post.isLiked = !this.post.isLiked
      } catch (error) {
        console.error('操作失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    async submitComment() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录才能发表评论！')
        this.$router.push({ path: '/forum/login', query: { redirect: this.$route.fullPath } })
        return
      }

      if (!this.newComment.trim()) return

      if (!this.post || !this.post.id) {
        this.$message.error('帖子信息不完整')
        return
      }

      try {
        const commentData = {
          content: this.newComment,
          parentId: 0
        }
        await addComment(this.post.id, commentData)

        this.newComment = ''
        this.$message.success('评论成功')
        // 重新获取评论列表
        await this.fetchComments()
        // 更新帖子评论数
        if (this.post) {
          this.post.comments = (this.post.comments || 0) + 1
        }
      } catch (error) {
        console.error('评论失败:', error)
        this.$message.error('评论失败，请重试')
      }
    },
    handleReply(comment) {
      this.newComment = `@${comment.author.name} `
    },
    goToLogin() {
      this.$router.push({ path: '/forum/login', query: { redirect: this.$route.fullPath } })
    }
  }
}
</script>

<style scoped lang="scss">
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.post-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);

  .post-header {
    margin-bottom: 24px;

    h1 {
      font-size: 24px;
      color: #333;
      margin: 0 0 16px;
    }

    .post-meta {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #666;

      .author {
        font-weight: 500;
      }

      .time, .category {
        color: #999;
      }
    }
  }

  .post-body {
    font-size: 16px;
    line-height: 1.8;
    color: #333;
    margin-bottom: 24px;
    white-space: pre-wrap;
  }

  .post-actions {
    border-top: 1px solid #eee;
    padding-top: 16px;
    display: flex;
    gap: 24px;

    .el-button {
      font-size: 14px;

      i {
        margin-right: 4px;

        &.is-liked {
          color: #409EFF;
        }
      }
    }
  }
}

.comments-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);

  h2 {
    font-size: 18px;
    color: #333;
    margin: 0 0 24px;
  }

  .comment-input {
    margin-bottom: 24px;

    .login-prompt {
      margin-bottom: 16px;
    }

    .el-button {
      margin-top: 12px;
    }
  }

  .comment-list {
    .comment-item {
      padding: 16px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .comment-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        .author {
          font-weight: 500;
          color: #333;
        }

        .time {
          color: #999;
          font-size: 12px;
        }
      }

      .comment-content {
        color: #333;
        line-height: 1.6;
        margin-bottom: 8px;
      }

      .comment-actions {
        margin-bottom: 8px;
      }

      .reply-list {
        margin-left: 40px;
        padding: 8px 0;
        background: #f9fafb;
        border-radius: 4px;

        .reply-item {
          padding: 8px 16px;

          .reply-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 4px;

            .author {
              font-weight: 500;
              color: #333;
            }

            .time {
              color: #999;
              font-size: 12px;
            }
          }

          .reply-content {
            color: #333;
            line-height: 1.6;
          }
        }
      }
    }
  }
}
</style>
