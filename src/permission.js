import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { getForumToken } from '@/utils/forum-auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/introduction', '/ai-models', '/forum', '/forum/login', '/forum/post/**', '/community', '/user/login', '/user/register', '/user/auth']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

// 判断是否是论坛相关路径
const isForumPath = (path) => {
  return path.startsWith('/forum')
}

router.beforeEach((to, from, next) => {
  NProgress.start()

  // 论坛相关路径特殊处理
  if (isForumPath(to.path)) {
    // 论坛路径在白名单中，直接放行（匿名可访问）
    if (isWhiteList(to.path)) {
      next()
      return
    }
    // 如果不在白名单，检查是否有论坛token
    // 这里可以根据需要添加论坛token验证逻辑
    // 目前论坛路径都在白名单中，所以直接放行
    next()
    return
  }

  // 主系统路径处理
  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    // 管理员登录页面，只允许 /admin 路径访问
    if (to.path === '/login') {
      // 如果是访问管理员登录页面，且不是 /admin 相关路径，重定向到论坛登录
      if (!to.query || !to.query.admin) {
        next({ path: '/forum/login', query: { redirect: to.fullPath } })
        return
      }
    }

    if (to.path === '/login' || to.path === '/user/login') {
      next({ path: '/introduction' })
      NProgress.done()
    } else if (to.path === '/' || to.path === '/index') {
      // 访问根路径时跳转到介绍页
      next({ path: '/introduction', replace: true })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/introduction' })
            })
          })
      } else {
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      // 默认跳转到介绍页，而不是登录页
      next(`/introduction?redirect=${encodeURIComponent(to.fullPath)}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
