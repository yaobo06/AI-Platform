<template>
  <div class="forum-login">
    <div class="left">
    </div>
    <div class="right">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <h3 class="title">AI论坛登录</h3>
        <el-form-item prop="username">
          <el-input size="large" v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号">
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="large" v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码"
            @keyup.enter.native="handleLogin">
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input size="large" v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 63%"
            @keyup.enter.native="handleLogin">
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img" />
          </div>
        </el-form-item>
        <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
        <el-form-item style="width:100%;">
          <el-button :loading="loading" size="large" type="primary" style="width:100%;"
            @click.native.prevent="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
          <div style="float: right;" v-if="register">
            <router-link class="link-type" :to="'/forum/register'">立即注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { forumLogin, getForumCodeImg } from "@/api/forum-user";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { setForumToken, getForumToken } from '@/utils/forum-auth'

export default {
  name: "ForumLogin",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ]
        // code: [{ required: true, trigger: "change", message: "请输入验证码" }] // 暂时不需要验证码
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        // 如果redirect是论坛相关路径，使用它；否则默认跳转到论坛首页
        const redirect = route.query && route.query.redirect;
        if (redirect) {
          // 解码 redirect 参数（URL 编码的 %2Fforum 需要解码为 /forum）
          try {
            const decoded = decodeURIComponent(redirect);
            // 如果重定向路径是登录页本身，则跳转到论坛首页，避免循环
            if (decoded === '/forum/login' || decoded.startsWith('/forum/login?')) {
              this.redirect = '/forum';
            } else if (decoded.startsWith('/forum')) {
              this.redirect = decoded;
            } else {
              this.redirect = '/forum';
            }
          } catch (e) {
            // 如果解码失败，使用原始值
            if (redirect === '/forum/login' || redirect.startsWith('/forum/login?')) {
              this.redirect = '/forum';
            } else if (redirect.startsWith('/forum')) {
              this.redirect = redirect;
            } else {
              this.redirect = '/forum';
            }
          }
        } else {
          this.redirect = '/forum'; // 论坛登录默认跳转到论坛首页
        }
      },
      immediate: true
    }
  },
  created() {
    // 如果已经登录，直接跳转到论坛首页或重定向页面
    const token = getForumToken();
    if (token) {
      let redirectPath = '/forum';
      if (this.redirect && this.redirect !== '/forum/login') {
        redirectPath = this.redirect;
      }
      this.$router.replace(redirectPath);
      return;
    }
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getForumCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      }).catch(() => {
        this.captchaEnabled = false;
      });
    },
    getCookie() {
      const username = Cookies.get("forum_username");
      const password = Cookies.get("forum_password");
      const rememberMe = Cookies.get('forum_rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("forum_username", this.loginForm.username, { expires: 30 });
            Cookies.set("forum_password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('forum_rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("forum_username");
            Cookies.remove("forum_password");
            Cookies.remove('forum_rememberMe');
          }

          forumLogin(this.loginForm.username, this.loginForm.password, this.loginForm.code, this.loginForm.uuid)
            .then(res => {
              console.log('登录响应:', res); // 调试日志
              if (res.code === 200) {
                // /user/auth/login 返回的数据在 data 字段中
                const data = res.data || res;
                const token = data.token;
                console.log('获取到的token:', token); // 调试日志
                if (token) {
                  setForumToken(token);
                  // 保存用户信息到 localStorage（如果需要）
                  if (data.userId) {
                    localStorage.setItem('user_id', data.userId)
                  }
                  if (data.username) {
                    localStorage.setItem('user_username', data.username)
                  }
                  if (data.nickname) {
                    localStorage.setItem('user_nickname', data.nickname)
                  }
                  // 触发登录成功事件，通知 GlobalNavbar 更新
                  window.dispatchEvent(new Event('forum-login-success'))
                  this.$message.success('登录成功');
                  // 跳转到指定页面或论坛首页
                  // 解码 redirect 参数
                  let redirectPath = '/forum';
                  if (this.redirect) {
                    try {
                      redirectPath = decodeURIComponent(this.redirect);
                      // 确保路径以 / 开头
                      if (!redirectPath.startsWith('/')) {
                        redirectPath = '/' + redirectPath;
                      }
                      // 如果重定向路径还是登录页，则跳转到论坛首页
                      if (redirectPath === '/forum/login' || redirectPath.startsWith('/forum/login?')) {
                        redirectPath = '/forum';
                      }
                    } catch (e) {
                      redirectPath = this.redirect;
                      if (redirectPath === '/forum/login' || redirectPath.startsWith('/forum/login?')) {
                        redirectPath = '/forum';
                      }
                    }
                  }
                  console.log('准备跳转到:', redirectPath); // 调试日志
                  // 直接使用 window.location.href 强制刷新页面，确保 Cookie 生效
                  // 这样可以避免路由守卫或组件检查时 token 未生效的问题
                  this.loading = false;
                  window.location.href = redirectPath;
                } else {
                  console.error('未获取到token，响应数据:', res);
                  this.$message.error('登录失败：未获取到token');
                  this.loading = false;
                  if (this.captchaEnabled) {
                    this.getCode();
                  }
                }
              } else {
                this.$message.error(res.msg || '登录失败');
                this.loading = false;
                if (this.captchaEnabled) {
                  this.getCode();
                }
              }
            })
            .catch((error) => {
              console.error('登录错误:', error);
              this.$message.error('登录失败，请重试');
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.forum-login {
  display: flex;
  height: 100%;
  background-image: url("../../assets/images/login.png");
  background-size: cover;
  .left {
    background-image: url("../../assets/images/login-left.png");
    background-size: cover;
    width: 60%;
  }

  .right {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    .login-form {
      border-radius: 6px;
      background: #ffffff;
      width: 400px;
      padding: 30px 25px 5px 25px;
      .title {
        margin-bottom: 50px;
        text-align: center;
        color: #2c2b2b;
        font-weight: bold;
        font-size: 22px;
      }
      .el-input {
        height: 38px;

        input {
          height: 38px;
        }
      }

      .input-icon {
        height: 39px;
        width: 14px;
        margin-left: 2px;
      }
    }

    .login-tip {
      font-size: 13px;
      text-align: center;
      color: #bfbfbf;
    }

    .login-code {
      width: 33%;
      height: 38px;
      float: right;

      img {
        cursor: pointer;
        vertical-align: middle;
      }
    }

    .login-code-img {
      height: 38px;
    }
  }
}
</style>

