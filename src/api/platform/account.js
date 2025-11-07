import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listAccount(query) {
  return request({
    url: '/platform/account/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getAccount(id) {
  return request({
    url: '/platform/account/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addAccount(data) {
  return request({
    url: '/platform/account',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateAccount(data) {
  return request({
    url: '/platform/account',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delAccount(id) {
  return request({
    url: '/platform/account/' + id,
    method: 'delete'
  })
}
