<template>
  <div class="post-list">
    <div v-for="post in posts" :key="post.id" class="post-item" @click="$emit('view-post', post)">
      <div class="post-avatar">
        <el-avatar :src="post.author.avatar" :size="40"></el-avatar>
      </div>
      <div class="post-content">
        <h3 class="post-title">{{ post.title }}</h3>
        <p class="post-excerpt">{{ post.excerpt }}</p>
        <div class="post-meta">
          <div class="meta-left">
            <span class="author">
              <i class="el-icon-user"></i>
              {{ post.author.name }}
            </span>
            <span class="time">
              <i class="el-icon-time"></i>
              {{ post.createTime }}
            </span>
          </div>
          <div class="meta-right">
            <span class="views">
              <i class="el-icon-view"></i>
              {{ post.views }}
            </span>
            <span class="comments">
              <i class="el-icon-chat-dot-round"></i>
              {{ post.comments }}
            </span>
            <span class="likes">
              <i class="el-icon-star-off"></i>
              {{ post.likes }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PostList',
  props: {
    posts: {
      type: Array,
      required: true
    }
  }
}
</script>

<style scoped lang="scss">
.post-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  }

  .post-avatar {
    flex-shrink: 0;
  }

  .post-content {
    flex-grow: 1;
    min-width: 0; // 防止文本溢出

    .post-title {
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
      margin: 0 0 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      transition: color 0.3s;

      &:hover {
        color: #1e40af;
      }
    }

    .post-excerpt {
      font-size: 14px;
      color: #6b7280;
      margin: 0 0 12px;
      line-height: 1.5;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .post-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      color: #9ca3af;

      .meta-left, .meta-right {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      span {
        display: flex;
        align-items: center;
        gap: 4px;

        i {
          font-size: 14px;
        }
      }

      .author {
        color: #4b5563;
        font-weight: 500;
      }

      .views, .comments, .likes {
        transition: color 0.3s;

        &:hover {
          color: #1e40af;
        }
      }
    }
  }
}
</style> 