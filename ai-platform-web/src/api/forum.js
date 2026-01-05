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

// 收藏帖子
export function favoritePost(postId) {
  return request({
    url: `/forum/posts/${postId}/favorite`,
    method: 'post'
  })
}

// 检查是否已收藏
export function getFavoriteStatus(postId) {
  return request({
    url: `/forum/posts/${postId}/favorite/status`,
    method: 'get'
  })
}

// 搜索帖子
export function searchPosts(query, mode = 'hybrid', page = 1, size = 10) {
  return request({
    url: '/forum/search',
    method: 'get',
    params: {
      q: query,
      mode: mode,
      page: page,
      size: size
    }
  })
}

// 获取我的发帖
export function getMyPosts(page = 1, size = 10) {
  return request({
    url: '/forum/user/posts',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取我的点赞
export function getMyLikedPosts(page = 1, size = 10) {
  return request({
    url: '/forum/user/liked-posts',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取我的收藏
export function getMyFavoritedPosts(page = 1, size = 10) {
  return request({
    url: '/forum/user/favorited-posts',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取我的评论
export function getMyComments(page = 1, size = 10) {
  return request({
    url: '/forum/user/comments',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取热门帖子
export function getHotPosts(page = 1, size = 10) {
  return request({
    url: '/forum/posts/hot',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取最近帖子
export function getRecentPosts(page = 1, size = 10) {
  return request({
    url: '/forum/posts/recent',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取大家都在看的帖子（热门帖子）
export function getTrendingPosts(page = 1, size = 5) {
  return request({
    url: '/forum/trending',
    method: 'get',
    params: {
      page: page,
      size: size
    }
  })
}

// 获取热门标签
export function getHotTags(limit = 10) {
  return request({
    url: '/forum/hot-tags',
    method: 'get',
    params: {
      limit: limit
    }
  })
}
