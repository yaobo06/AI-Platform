import request from '@/utils/request'

// 查询安防事件详情列表
export function listLog(query) {
  return request({
    url: '/platform/log/list',
    method: 'get',
    params: query
  })
}

// 查询安防事件详情详细
export function getLog(id) {
  return request({
    url: '/platform/log/' + id,
    method: 'get'
  })
}

// 新增安防事件详情
export function addLog(data) {
  return request({
    url: '/platform/log',
    method: 'post',
    data: data
  })
}

// 修改安防事件详情
export function updateLog(data) {
  return request({
    url: '/platform/log',
    method: 'put',
    data: data
  })
}

// 删除安防事件详情
export function delLog(id) {
  return request({
    url: '/platform/log/' + id,
    method: 'delete'
  })
}

// 确认安防事件详情
export function confirmLog(ids) {
  return request({
    url: '/platform/log/confirmations',
    method: 'put',
    data: ids
  })
}

// 查询安防事件详情列表
export function eventGroupLog(query) {
  return request({
    url: '/platform/log/eventGroup',
    method: 'get',
    params: query
  })
}

// 查询安防事件详情列表
export function addrGroupLog(query) {
  return request({
    url: '/platform/log/addrGroup',
    method: 'get',
    params: query
  })
}

