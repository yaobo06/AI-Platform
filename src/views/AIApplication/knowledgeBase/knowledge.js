import request from '@/utils/request'
// 查询向量模型列表
export function getModels(keyWords) {
  return request({
    url: '/system/model/listBy',
    method: 'get',
    params: {
      keyWords
    }
  })
}
// 查询向量数据库列表
export function getVectorLibrary(keyWords) {
  return request({
    url: '/system/store/listBy',
    method: 'get',
    params: {
      keyWords
    }
  })
}
// 查询知识库列表
export function listKnowledge(query) {
  return request({
    url: '/system/knowledge/list',
    method: 'get',
    params: query
  })
}

// 查询知识库详细
export function getKnowledge(id) {
  return request({
    url: '/system/knowledge/' + id,
    method: 'get'
  })
}

// 新增知识库
export function addKnowledge(data) {
  return request({
    url: '/system/knowledge',
    method: 'post',
    data: data
  })
}

// 修改知识库
export function updateKnowledge(data) {
  return request({
    url: '/system/knowledge',
    method: 'put',
    data: data
  })
}

// 删除知识库
export function delKnowledge(id) {
  return request({
    url: '/system/knowledge/' + id,
    method: 'delete'
  })
}
