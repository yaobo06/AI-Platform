<template>
  <div class="forum-test-container has-global-navbar">
    <!-- 全局导航栏 -->
    <GlobalNavbar />
    
    <div class="test-content">
      <h1>论坛功能测试页面</h1>
      
      <!-- 用户状态测试 -->
      <div class="test-section">
        <h2>用户状态测试</h2>
        <div class="status-info">
          <p><strong>登录状态:</strong> {{ isLoggedIn ? '已登录' : '未登录' }}</p>
          <p><strong>用户Token:</strong> {{ token || '无' }}</p>
          <p><strong>用户名:</strong> {{ userName || '未获取' }}</p>
          <p><strong>用户头像:</strong> {{ userAvatar || '未获取' }}</p>
        </div>
        
        <div class="action-buttons">
          <el-button @click="checkLoginStatus">检查登录状态</el-button>
          <el-button @click="getUserInfo">获取用户信息</el-button>
          <el-button @click="goToLogin">去登录</el-button>
          <el-button @click="logout">退出登录</el-button>
        </div>
      </div>

      <!-- 发帖功能测试 -->
      <div class="test-section">
        <h2>发帖功能测试</h2>
        <div class="post-test">
          <el-form :model="testPostForm" label-width="80px">
            <el-form-item label="标题">
              <el-input v-model="testPostForm.title" placeholder="测试帖子标题"></el-input>
            </el-form-item>
            <el-form-item label="内容">
              <el-input type="textarea" v-model="testPostForm.content" :rows="4" placeholder="测试帖子内容"></el-input>
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="testPostForm.category" placeholder="选择分类">
                <el-option label="经验分享" value="share"></el-option>
                <el-option label="问题求助" value="question"></el-option>
                <el-option label="技术讨论" value="discuss"></el-option>
                <el-option label="AI资讯" value="news"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="testCreatePost" :disabled="!isLoggedIn">
                {{ isLoggedIn ? '测试发帖' : '请先登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- API测试 -->
      <div class="test-section">
        <h2>API测试</h2>
        <div class="api-test">
          <el-button @click="testGetPosts">测试获取帖子列表</el-button>
          <el-button @click="testGetCategories">测试获取分类</el-button>
          <el-button @click="testGetStats">测试获取统计</el-button>
        </div>
        <div class="api-result">
          <h3>API测试结果:</h3>
          <pre>{{ apiResult }}</pre>
        </div>
      </div>

      <!-- 权限测试 -->
      <div class="test-section">
        <h2>权限测试</h2>
        <div class="permission-test">
          <p><strong>查看帖子权限:</strong> {{ canViewPosts ? '允许' : '拒绝' }}</p>
          <p><strong>发帖权限:</strong> {{ canPost ? '允许' : '拒绝' }}</p>
          <p><strong>点赞权限:</strong> {{ canLike ? '允许' : '拒绝' }}</p>
          <p><strong>评论权限:</strong> {{ canComment ? '允许' : '拒绝' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getToken, removeToken } from '@/utils/auth'
import { mapGetters } from 'vuex'
import { getPostList, createPost, getCategories, getStats } from '@/api/forum'
import GlobalNavbar from '@/components/GlobalNavbar'

export default {
  name: 'ForumTest',
  components: {
    GlobalNavbar
  },
  data() {
    return {
      testPostForm: {
        title: '测试帖子标题',
        content: '这是一个测试帖子的内容，用于验证发帖功能是否正常工作。',
        category: 'share'
      },
      apiResult: '点击按钮测试API...'
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'token'
    ]),
    isLoggedIn() {
      return !!getToken()
    },
    userName() {
      return this.name || '未获取'
    },
    userAvatar() {
      return this.avatar || '未获取'
    },
    canViewPosts() {
      return true // 查看帖子不需要登录
    },
    canPost() {
      return this.isLoggedIn
    },
    canLike() {
      return this.isLoggedIn
    },
    canComment() {
      return this.isLoggedIn
    }
  },
  methods: {
    checkLoginStatus() {
      const token = getToken()
      this.$message.info(`当前登录状态: ${token ? '已登录' : '未登录'}`)
      console.log('Token:', token)
    },
    
    async getUserInfo() {
      try {
        await this.$store.dispatch('GetInfo')
        this.$message.success('用户信息获取成功')
      } catch (error) {
        this.$message.error('获取用户信息失败: ' + error.message)
      }
    },
    
    goToLogin() {
      this.$router.push('/login')
    },
    
    logout() {
      this.$store.dispatch('LogOut').then(() => {
        this.$message.success('退出登录成功')
        this.$router.push('/login')
      }).catch(error => {
        this.$message.error('退出登录失败: ' + error.message)
      })
    },
    
    async testCreatePost() {
      try {
        const submitData = {
          title: this.testPostForm.title,
          content: this.testPostForm.content,
          categoryId: this.getCategoryIdByName(this.testPostForm.category),
          contentType: '1',
          tags: '测试,功能验证'
        }
        
        console.log('测试发帖数据:', submitData)
        
        const response = await createPost(submitData)
        console.log('发帖响应:', response)
        
        if (response.code === 200) {
          this.$message.success('测试发帖成功！')
          this.apiResult = JSON.stringify(response, null, 2)
        } else {
          this.$message.error(response.msg || '测试发帖失败')
          this.apiResult = JSON.stringify(response, null, 2)
        }
      } catch (error) {
        console.error('测试发帖失败:', error)
        this.$message.error('测试发帖失败: ' + error.message)
        this.apiResult = '错误: ' + error.message
      }
    },
    
    getCategoryIdByName(categoryName) {
      const categoryMap = {
        'share': 1,
        'question': 2,
        'discuss': 3,
        'news': 4
      }
      return categoryMap[categoryName]
    },
    
    async testGetPosts() {
      try {
        const response = await getPostList({})
        this.apiResult = JSON.stringify(response, null, 2)
        this.$message.success('获取帖子列表成功')
      } catch (error) {
        this.$message.error('获取帖子列表失败: ' + error.message)
        this.apiResult = '错误: ' + error.message
      }
    },
    
    async testGetCategories() {
      try {
        const response = await getCategories()
        this.apiResult = JSON.stringify(response, null, 2)
        this.$message.success('获取分类成功')
      } catch (error) {
        this.$message.error('获取分类失败: ' + error.message)
        this.apiResult = '错误: ' + error.message
      }
    },
    
    async testGetStats() {
      try {
        const response = await getStats()
        this.apiResult = JSON.stringify(response, null, 2)
        this.$message.success('获取统计成功')
      } catch (error) {
        this.$message.error('获取统计失败: ' + error.message)
        this.apiResult = '错误: ' + error.message
      }
    }
  }
}
</script>

<style scoped lang="scss">
.forum-test-container {
  padding-top: 64px;
  min-height: 100vh;
  background: #f8fafc;
}

.test-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  text-align: center;
  color: #1f2937;
  margin-bottom: 30px;
}

.test-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

  h2 {
    color: #374151;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #e5e7eb;
  }
}

.status-info {
  background: #f9fafb;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 15px;

  p {
    margin: 8px 0;
    color: #4b5563;
  }
}

.action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.post-test {
  .el-form {
    max-width: 500px;
  }
}

.api-test {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.api-result {
  background: #1f2937;
  color: #f9fafb;
  padding: 15px;
  border-radius: 6px;
  overflow-x: auto;

  h3 {
    margin-top: 0;
    color: #60a5fa;
  }

  pre {
    margin: 0;
    white-space: pre-wrap;
    word-wrap: break-word;
  }
}

.permission-test {
  background: #f0f9ff;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #3b82f6;

  p {
    margin: 8px 0;
    color: #1e40af;
  }
}

@media (max-width: 768px) {
  .test-content {
    padding: 15px;
  }
  
  .action-buttons,
  .api-test {
    flex-direction: column;
  }
}
</style>
