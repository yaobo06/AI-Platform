import request from '@/utils/request'

// 用户登录
export function userLogin(username, password) {
  return request({
    url: '/user/auth/login',
    method: 'post',
    data: {
      username: username,
      password: password
    }
  })
}

// 用户注册
export function userRegister(data) {
  return request({
    url: '/user/auth/register',
    method: 'post',
    data: data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

