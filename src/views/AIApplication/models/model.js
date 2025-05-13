import request from '@/utils/request'

// 查询LLM模型配置列表
export function listModel(query) {
  return request({
    url: '/system/model/list',
    method: 'get',
    params: query
  })
}

// 查询LLM模型配置详细
export function getModel(id) {
  return request({
    url: '/system/model/' + id,
    method: 'get'
  })
}

// 新增LLM模型配置
export function addModel(data) {
  return request({
    url: '/system/model',
    method: 'post',
    data: data
  })
}

// 修改LLM模型配置
export function updateModel(data) {
  return request({
    url: '/system/model',
    method: 'put',
    data: data
  })
}

// 删除LLM模型配置
export function delModel(id) {
  return request({
    url: '/system/model/' + id,
    method: 'delete'
  })
}
