import request from '@/utils/request'

// 查询海康异常事件类型列表
export function listEvent(query) {
  return request({
    url: '/platform/event/list',
    method: 'get',
    params: query
  })
}

// 查询海康异常事件类型详细
export function getEvent(id) {
  return request({
    url: '/platform/event/' + id,
    method: 'get'
  })
}

// 新增海康异常事件类型
export function addEvent(data) {
  return request({
    url: '/platform/event',
    method: 'post',
    data: data
  })
}

// 修改海康异常事件类型
export function updateEvent(data) {
  return request({
    url: '/platform/event',
    method: 'put',
    data: data
  })
}

// 删除海康异常事件类型
export function delEvent(id) {
  return request({
    url: '/platform/event/' + id,
    method: 'delete'
  })
}
