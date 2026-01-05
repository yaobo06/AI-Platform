<template>
  <div class="user-register">
    <div class="register-container">
      <div class="register-box">
        <div class="register-header">
          <h2>用户注册</h2>
          <p>创建您的 AI Platform 账号</p>
        </div>

        <el-form
          ref="registerForm"
          :model="registerForm"
          :rules="registerRules"
          class="register-form">

          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              size="large"
              clearable>
            </el-input>
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
              v-model="registerForm.nickname"
              placeholder="请输入昵称（可选）"
              prefix-icon="el-icon-user-solid"
              size="large"
              clearable>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password
              clearable>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              show-password
              @keyup.enter.native="handleRegister"
              clearable>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click.native.prevent="handleRegister"
              style="width: 100%;">
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <p>已有账号？<router-link to="/forum/login" class="link-text">立即登录</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { userRegister } from '@/api/user'

export default {
  name: 'UserRegister',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      registerForm: {
        username: '',
        nickname: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        nickname: [
          { max: 20, message: '昵称长度不能超过 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true

          const registerData = {
            username: this.registerForm.username,
            password: this.registerForm.password,
            nickname: this.registerForm.nickname || this.registerForm.username
          }

          userRegister(registerData)
            .then(res => {
              this.loading = false
              if (res.code === 200) {
                this.$message.success('注册成功，请登录')
                // 跳转到登录页
                this.$router.push({ path: '/forum/login' })
              } else {
                this.$message.error(res.msg || '注册失败')
              }
            })
            .catch(error => {
              this.loading = false
              console.error('注册错误:', error)
              this.$message.error(error.msg || '注册失败，请重试')
            })
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.user-register {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 450px;
}

.register-box {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
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

.register-form {
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

.register-footer {
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

