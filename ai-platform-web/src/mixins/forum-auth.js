/**
 * 论坛登录状态管理 Mixin
 * 用于复用登录状态检查、用户信息加载等逻辑
 */
import { getForumToken, removeForumToken } from '@/utils/forum-auth'
import { getForumUserInfo } from '@/api/forum-user'

export default {
  data() {
    return {
      forumUserInfo: null,
      _lastLoggedInState: false
    }
  },
  computed: {
    isForumLoggedIn() {
      const token = getForumToken()
      return !!token
    },
    forumUserName() {
      if (this.forumUserInfo) {
        return this.forumUserInfo.nickname || this.forumUserInfo.username || '用户'
      }
      return '用户'
    },
    forumUserAvatar() {
      if (this.forumUserInfo && this.forumUserInfo.avatarUrl) {
        return this.forumUserInfo.avatarUrl
      }
      return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }
  },
  created() {
    this.loadForumUserInfo()
    // 监听登录状态变化
    window.addEventListener('storage', this.handleForumStorageChange)
    window.addEventListener('forum-login-success', this.handleForumLoginSuccess)
    window.addEventListener('forum-logout', this.handleForumLogoutEvent)
    // 定期检查登录状态
    this.forumLoginCheckInterval = setInterval(() => {
      const token = getForumToken()
      const currentLoggedIn = !!token
      if (currentLoggedIn !== this._lastLoggedInState) {
        this._lastLoggedInState = currentLoggedIn
        this.$forceUpdate()
        if (currentLoggedIn) {
          this.loadForumUserInfo()
        } else {
          this.forumUserInfo = null
        }
      } else if (token && !this.forumUserInfo) {
        this.loadForumUserInfo()
      } else if (!token && this.forumUserInfo) {
        this.forumUserInfo = null
        this._lastLoggedInState = false
      }
    }, 500)
  },
  beforeDestroy() {
    window.removeEventListener('storage', this.handleForumStorageChange)
    window.removeEventListener('forum-login-success', this.handleForumLoginSuccess)
    window.removeEventListener('forum-logout', this.handleForumLogoutEvent)
    if (this.forumLoginCheckInterval) {
      clearInterval(this.forumLoginCheckInterval)
    }
  },
  methods: {
    async loadForumUserInfo() {
      const token = getForumToken()
      if (!token) {
        this.forumUserInfo = null
        return
      }

      try {
        const response = await getForumUserInfo()
        if (response.code === 200) {
          this.forumUserInfo = response.data?.user || response.user || response.data
        } else {
          this.forumUserInfo = null
          removeForumToken()
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.forumUserInfo = null
      }
    },
    handleForumStorageChange(e) {
      if (e.key === 'Forum-Token') {
        this.$forceUpdate()
        if (e.newValue) {
          this.loadForumUserInfo()
        } else {
          this.forumUserInfo = null
        }
      }
    },
    handleForumLoginSuccess() {
      this.$forceUpdate()
      this.loadForumUserInfo()
    },
    handleForumLogoutEvent() {
      this.$forceUpdate()
      this.forumUserInfo = null
      this._lastLoggedInState = false
    },
    async doForumLogout() {
      try {
        const { forumLogout } = await import('@/api/forum-user')
        await forumLogout()
      } catch (error) {
        console.error('退出登录失败:', error)
      }
      removeForumToken()
      this.forumUserInfo = null
      this._lastLoggedInState = false
      window.dispatchEvent(new Event('forum-logout'))
      this.$forceUpdate()
      this.$message.success('退出登录成功')
    }
  }
}

