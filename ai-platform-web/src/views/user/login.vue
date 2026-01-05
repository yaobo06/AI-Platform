<template>
  <div class="user-login">
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <h2>用户登录</h2>
          <p>欢迎来到 AI Platform</p>
        </div>

        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form">

          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              size="large"
              clearable>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password
              @keyup.enter.native="handleLogin">
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <div style="float: right;">
              <router-link to="/user/register" class="link-text">立即注册</router-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click.native.prevent="handleLogin"
              style="width: 100%;">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>还没有账号？<router-link to="/user/register" class="link-text">立即注册</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { userLogin } from '@/api/user'
import Cookies from 'js-cookie'

export default {
  name: 'UserLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        rememberMe: false
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      },
      loading: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCookie()
  },
  methods: {
    getCookie() {
      const username = Cookies.get('user_username')
      const password = Cookies.get('user_password')
      const rememberMe = Cookies.get('user_rememberMe')

      if (username) {
        this.loginForm.username = username
      }
      if (password) {
        this.loginForm.password = this.$decrypt(password)
      }
      if (rememberMe) {
        this.loginForm.rememberMe = rememberMe === 'true'
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true

          // 记住密码
          if (this.loginForm.rememberMe) {
            Cookies.set('user_username', this.loginForm.username, { expires: 30 })
            Cookies.set('user_password', this.$encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('user_rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove('user_username')
            Cookies.remove('user_password')
            Cookies.remove('user_rememberMe')
          }

          userLogin(this.loginForm.username, this.loginForm.password)
            .then(res => {
              this.loading = false
              if (res.code === 200 && res.data) {
                // 存储 token
                const token = res.data.token
                if (token) {
                  localStorage.setItem('user_token', token)
                  localStorage.setItem('user_id', res.data.userId)
                  localStorage.setItem('user_username', res.data.username)
                  localStorage.setItem('user_nickname', res.data.nickname || res.data.username)

                  this.$message.success('登录成功')

                  // 跳转
                  const redirectPath = this.redirect || '/introduction'
                  this.$router.replace({ path: redirectPath }).catch(() => {
                    window.location.href = redirectPath
                  })
                } else {
                  this.$message.error('登录失败：未获取到token')
                }
              } else {
                this.$message.error(res.msg || '登录失败')
              }
            })
            .catch(error => {
              this.loading = false
              console.error('登录错误:', error)
              this.$message.error(error.msg || '登录失败，请重试')
            })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.user-login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 450px;
}

.login-box {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  h2 {
    font-size: 28px;
    color: #333;
    margin: 0 0 10px 0;
    font-weight: 600;
  }

  p {
    font-size: 14px;
    color: #999;
    margin: 0;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  .link-text {
    color: #409EFF;
    text-decoration: none;
    font-size: 14px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;

  p {
    font-size: 14px;
    color: #666;
    margin: 0;
  }

  .link-text {
    color: #409EFF;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>

