import request from '@/utils/request'

// 查询Embedding向量数据库配置列表
export function listStore(query) {
  return request({
    url: '/system/store/list',
    method: 'get',
    params: query
  })
}

// 查询Embedding向量数据库配置详细
export function getStore(id) {
  return request({
    url: '/system/store/' + id,
    method: 'get'
  })
}

// 新增Embedding向量数据库配置
export function addStore(data) {
  return request({
    url: '/system/store',
    method: 'post',
    data: data
  })
}

// 修改Embedding向量数据库配置
export function updateStore(data) {
  return request({
    url: '/system/store',
    method: 'put',
    data: data
  })
}

// 删除Embedding向量数据库配置
export function delStore(id) {
  return request({
    url: '/system/store/' + id,
    method: 'delete'
  })
}
