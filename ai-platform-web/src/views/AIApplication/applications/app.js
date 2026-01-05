import request from '@/utils/request'

export function getModels(keyWords = "") {
  return request({
    url: '/system/model/listBy',
    method: 'get',
    params: {
      keyWords
    }
  })
}

// 查询提示词列表
export function listApp(query) {
  return request({
    url: '/system/homepage/getIntroductionInfo',
    method: 'get',
    params: query
  })
}

// 查询提示词详细
export function getApp(id) {
  return request({
    url: '/system/app/' + id,
    method: 'get'
  })
}

// 新增提示词
export function addApp(data) {
  return request({
    url: '/system/app',
    method: 'post',
    data: data
  })
}

// 修改提示词
export function updateApp(data) {
  return request({
    url: '/system/app',
    method: 'put',
    data: data
  })
}

// 删除提示词
export function delApp(id) {
  return request({
    url: '/system/app/' + id,
    method: 'delete'
  })
}
