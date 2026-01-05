import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listModels(query) {
  return request({
    url: '/ai/models/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getModels(id) {
  return request({
    url: '/ai/models/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addModels(data) {
  return request({
    url: '/ai/models',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateModels(data) {
  return request({
    url: '/ai/models',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delModels(id) {
  return request({
    url: '/ai/models/' + id,
    method: 'delete'
  })
}
