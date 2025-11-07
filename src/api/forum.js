import request from '@/utils/request'

// 获取帖子列表
export function getPostList(query) {
  return request({
    url: '/forum/posts/list',
    method: 'get',
    params: query
  })
}

// 获取帖子详情
export function getPostDetail(id) {
  return request({
    url: `/forum/posts/${id}`,
    method: 'get'
  })
}

// 创建帖子
export function createPost(data) {
  return request({
    url: '/forum/posts',
    method: 'post',
    data: data
  })
}

// 更新帖子
export function updatePost(id, data) {
  return request({
    url: '/forum/posts',
    method: 'put',
    data: { ...data, postId: id }
  })
}

// 删除帖子
export function deletePost(id) {
  return request({
    url: `/forum/posts/${id}`,
    method: 'delete'
  })
}

// 获取评论列表
export function getComments(postId) {
  return request({
    url: `/forum/posts/${postId}/comments`,
    method: 'get'
  })
}

// 添加评论
export function addComment(postId, data) {
  return request({
    url: `/forum/posts/${postId}/comments`,
    method: 'post',
    data: data
  })
}

// 点赞帖子
export function likePost(postId) {
  return request({
    url: `/forum/posts/${postId}/like`,
    method: 'post'
  })
}

// 取消点赞
export function unlikePost(postId) {
  return request({
    url: `/forum/posts/${postId}/unlike`,
    method: 'post'
  })
}

// 点赞评论
export function likeComment(commentId) {
  return request({
    url: `/forum/posts/comments/${commentId}/like`,
    method: 'post'
  })
}

// 取消点赞评论
export function unlikeComment(commentId) {
  return request({
    url: `/forum/posts/comments/${commentId}/unlike`,
    method: 'post'
  })
}

// 获取分类列表
export function getCategories() {
  return request({
    url: '/forum/categories',
    method: 'get'
  })
}

// 获取论坛统计信息
export function getStats() {
  return request({
    url: '/forum/stats',
    method: 'get'
  })
} 