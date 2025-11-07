import request from '@/utils/request'

// 查询海康事件用户关联列表
export function listEvent(query) {
  return request({
    url: '/platform/userevent/list',
    method: 'get',
    params: query
  })
}

// 查询海康事件用户关联详细
export function getEvent(id) {
  return request({
    url: '/platform/userevent/' + id,
    method: 'get'
  })
}

// 新增海康事件用户关联
export function addEvent(data) {
  return request({
    url: '/platform/userevent',
    method: 'post',
    data: data
  })
}

// 修改海康事件用户关联
export function updateEvent(data) {
  return request({
    url: '/platform/userevent',
    method: 'put',
    data: data
  })
}

// 删除海康事件用户关联
export function delEvent(id) {
  return request({
    url: '/platform/userevent/' + id,
    method: 'delete'
  })
}
