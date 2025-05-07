import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listMethod(query) {
  return request({
    url: '/platform/method/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getMethod(id) {
  return request({
    url: '/platform/method/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addMethod(data) {
  return request({
    url: '/platform/method',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateMethod(data) {
  return request({
    url: '/platform/method',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delMethod(id) {
  return request({
    url: '/platform/method/' + id,
    method: 'delete'
  })
}
