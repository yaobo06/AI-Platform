import request from '@/utils/request'

// 查询AI模型列表（分页）
export function listAiModels(query) {
  return request({
    url: '/ai/models/list',
    method: 'get',
    params: query
  })
}

// 查询所有AI模型列表（前端展示用）
export function getAllAiModels(query) {
  return request({
    url: '/ai/models/lists',
    method: 'get',
    params: query,
    headers: {
      'isToken': false
    }
  })
}

// 查询AI模型详细
export function getAiModel(id) {
  return request({
    url: '/ai/models/' + id,
    method: 'get',
    headers: {
      'isToken': false
    }
  })
}

// 新增AI模型
export function addAiModel(data) {
  return request({
    url: '/ai/models',
    method: 'post',
    data: data
  })
}

// 修改AI模型
export function updateAiModel(data) {
  return request({
    url: '/ai/models',
    method: 'put',
    data: data
  })
}

// 删除AI模型
export function delAiModel(ids) {
  return request({
    url: '/ai/models/' + ids,
    method: 'delete'
  })
} 