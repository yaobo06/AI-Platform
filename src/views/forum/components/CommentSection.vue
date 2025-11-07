<template>
  <div class="comments-section">
    <h2>评论 ({{ comments.length }})</h2>
    
    <!-- 发表评论 -->
    <div class="comment-form">
      <el-avatar :src="currentUser.avatar" :size="40"></el-avatar>
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
      ></el-input>
      <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
        发表评论
      </el-button>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-user">
          <el-avatar :src="comment.user.avatar" :size="32"></el-avatar>
          <div class="comment-info">
            <span class="username">{{ comment.user.name }}</span>
            <span class="time">{{ comment.createTime }}</span>
          </div>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-actions">
          <el-button type="text" @click="handleLikeComment(comment)">
            <i :class="['el-icon-star-off', { 'is-liked': comment.isLiked }]"></i>
            {{ comment.likes }}
          </el-button>
          <el-button type="text" @click="handleReplyComment(comment)">
            <i class="el-icon-chat-dot-round"></i>
            回复
          </el-button>
        </div>
        
        <!-- 回复列表 -->
        <div v-if="comment.replies && comment.replies.length" class="replies-list">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <div class="comment-user">
              <el-avatar :src="reply.user.avatar" :size="24"></el-avatar>
              <div class="comment-info">
                <span class="username">{{ reply.user.name }}</span>
                <span class="time">{{ reply.createTime }}</span>
              </div>
            </div>
            <div class="comment-content">{{ reply.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { createComment, likeComment, unlikeComment, replyComment } from '@/api/forum'

export default {
  name: 'CommentSection',
  props: {
    postId: {
      type: [String, Number],
      required: true
    },
    comments: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      newComment: '',
      currentUser: {
        id: 1,
        name: '当前用户',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      }
    }
  },
  methods: {
    async submitComment() {
      if (!this.newComment.trim()) return
      
      try {
        const data = {
          content: this.newComment,
          postId: this.postId
        }
        await createComment(this.postId, data)
        this.newComment = ''
        this.$message.success('评论发表成功')
        this.$emit('comment-added')
      } catch (error) {
        console.error('发表评论失败:', error)
        this.$message.error('发表评论失败，请重试')
      }
    },
    async handleLikeComment(comment) {
      try {
        if (comment.isLiked) {
          await unlikeComment(this.postId, comment.id)
        } else {
          await likeComment(this.postId, comment.id)
        }
        comment.isLiked = !comment.isLiked
        comment.likes += comment.isLiked ? 1 : -1
        this.$message.success(comment.isLiked ? '点赞成功' : '已取消点赞')
      } catch (error) {
        console.error('操作失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    async handleReplyComment(comment) {
      this.newComment = `@${comment.user.name} `
      // 滚动到评论框
      this.$nextTick(() => {
        document.querySelector('.comment-form textarea').focus()
      })
    }
  }
}
</script>

<style scoped lang="scss">
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

  .comment-form {
    display: flex;
    gap: 16px;
    margin-bottom: 32px;

    .el-avatar {
      flex-shrink: 0;
    }

    .el-input {
      flex-grow: 1;
    }

    .el-button {
      flex-shrink: 0;
      align-self: flex-start;
    }
  }

  .comments-list {
    .comment-item {
      padding: 16px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .comment-user {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;

        .comment-info {
          .username {
            font-weight: 500;
            color: #333;
          }

          .time {
            font-size: 12px;
            color: #999;
            margin-left: 8px;
          }
        }
      }

      .comment-content {
        color: #333;
        line-height: 1.6;
        margin: 8px 0;
      }

      .comment-actions {
        display: flex;
        gap: 16px;

        .el-button {
          font-size: 12px;

          i {
            margin-right: 4px;

            &.is-liked {
              color: #409EFF;
            }
          }
        }
      }

      .replies-list {
        margin-left: 48px;
        margin-top: 16px;
        padding: 8px 16px;
        background: #f9f9f9;
        border-radius: 4px;

        .reply-item {
          padding: 8px 0;
          border-bottom: 1px solid #eee;

          &:last-child {
            border-bottom: none;
          }
        }
      }
    }
  }
}
</style> 