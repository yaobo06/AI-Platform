import request from '@/utils/request'

// 查询海康用户列表
export function listUser(query) {
  return request({
    url: '/platform/user/list',
    method: 'get',
    params: query
  })
}

// 查询海康用户详细
export function getUser(id) {
  return request({
    url: '/platform/user/' + id,
    method: 'get'
  })
}

// 新增海康用户
export function addUser(data) {
  return request({
    url: '/platform/user',
    method: 'post',
    data: data
  })
}

// 修改海康用户
export function updateUser(data) {
  return request({
    url: '/platform/user',
    method: 'put',
    data: data
  })
}

// 删除海康用户
export function delUser(id) {
  return request({
    url: '/platform/user/' + id,
    method: 'delete'
  })
}
