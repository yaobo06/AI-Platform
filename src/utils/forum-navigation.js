/**
 * 论坛导航工具函数
 */

/**
 * 跳转到登录页面
 * @param {Object} router - Vue Router 实例
 * @param {string} redirect - 登录后重定向的路径
 */
export function goToForumLogin(router, redirect) {
  const path = redirect ? `/forum/login?redirect=${encodeURIComponent(redirect)}` : '/forum/login'
  router.push(path)
}

/**
 * 跳转到注册页面（或登录页面）
 * @param {Object} router - Vue Router 实例
 */
export function goToForumRegister(router) {
  router.push('/forum/login?action=register')
}

/**
 * 跳转到发帖页面
 * @param {Object} router - Vue Router 实例
 * @param {Function} isLoggedIn - 检查是否登录的函数
 * @param {Function} message - Element UI Message 实例
 */
export function goToPostCreate(router, isLoggedIn, message) {
  if (!isLoggedIn()) {
    message.warning('请先登录才能发帖！')
    goToForumLogin(router, '/forum')
    return false
  }
  router.push('/forum/post/create')
  return true
}

