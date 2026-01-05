import request from '@/utils/request'

// Forum用户登录（使用关联数据库的登录接口）
export function forumLogin(username, password, code, uuid) {
  const data = {
    username,
    password
    // 注意：/user/auth/login 不需要验证码，如果需要验证码可以后续添加
  }
  return request({
    url: '/user/auth/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data
  })
}

// Forum用户注册
export function forumRegister(data) {
  return request({
    url: '/forum/user/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取Forum用户详细信息
export function getForumUserInfo() {
  return request({
    url: '/forum/user/info',
    method: 'get'
  })
}

// Forum用户退出登录
export function forumLogout() {
  return request({
    url: '/forum/user/logout',
    method: 'post'
  })
}

// 获取Forum验证码
export function getForumCodeImg() {
  return request({
    url: '/forum/captchaImage',
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  })
}

