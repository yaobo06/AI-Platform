<template>
  <div class="global-navbar">
    <div class="navbar-container">
      <!-- Logo部分 -->
      <div class="navbar-logo">
        <router-link to="/introduction" class="logo-link">
          <div class="logo-icon">
            <i class="el-icon-cpu"></i>
          </div>
          <span class="logo-text">AI Platform</span>
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <div class="navbar-nav">
        <router-link to="/introduction" class="nav-item" 
                     :class="{ active: $route.path === '/introduction' }">
          首页
        </router-link>
        <router-link to="/ai-models" class="nav-item" 
                     :class="{ active: $route.path === '/ai-models' }">
          AI应用
          <i class="el-icon-arrow-right nav-arrow"></i>
        </router-link>
        <router-link to="/forum" class="nav-item" 
                     :class="{ active: $route.path === '/forum' || $route.path.startsWith('/forum/') }">
          论坛
          <i class="el-icon-arrow-right nav-arrow"></i>
        </router-link>
        <a href="#" class="nav-item">社区</a>
        <div class="nav-item dropdown">
          更多
          <div class="dropdown-menu">
            <a href="#" class="dropdown-item">帮助中心</a>
            <a href="#" class="dropdown-item">开发文档</a>
            <a href="#" class="dropdown-item">关于我们</a>
          </div>
        </div>
      </div>

      <!-- 用户操作区 -->
      <div class="navbar-actions">
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button type="text" @click="handleRegister">注册</el-button>
      </div>

      <!-- 移动端菜单按钮 -->
      <div class="mobile-menu-btn" @click="toggleMobileMenu">
        <i class="el-icon-menu"></i>
      </div>
    </div>

    <!-- 移动端菜单 -->
    <div class="mobile-menu" :class="{ show: showMobileMenu }">
      <router-link to="/introduction" class="mobile-nav-item" @click="closeMobileMenu">首页</router-link>
      <router-link to="/ai-models" class="mobile-nav-item" @click="closeMobileMenu">AI应用</router-link>
      <router-link to="/forum" class="mobile-nav-item" @click="closeMobileMenu">论坛</router-link>
      <a href="#" class="mobile-nav-item" @click="closeMobileMenu">社区</a>
      <div class="mobile-actions">
        <el-button type="primary" size="small" @click="handleLogin">登录</el-button>
        <el-button type="text" size="small" @click="handleRegister">注册</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GlobalNavbar',
  data() {
    return {
      showMobileMenu: false
    }
  },
  methods: {
    handleLogin() {
      this.$router.push('/login');
    },
    handleRegister() {
      this.$router.push('/register');
    },
    toggleMobileMenu() {
      this.showMobileMenu = !this.showMobileMenu;
    },
    closeMobileMenu() {
      this.showMobileMenu = false;
    }
  },
  mounted() {
    // 点击外部关闭移动端菜单
    document.addEventListener('click', (e) => {
      if (!this.$el.contains(e.target)) {
        this.showMobileMenu = false;
      }
    });
  }
}
</script>

<style scoped lang="scss">
.global-navbar {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: 64px;

  .navbar-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .navbar-logo {
    .logo-link {
      display: flex;
      align-items: center;
      text-decoration: none;
      color: #1f2937;
      font-weight: 600;
      font-size: 18px;

      .logo-icon {
        width: 40px;
        height: 40px;
        background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;

        i {
          color: white;
          font-size: 20px;
        }
      }

      .logo-text {
        color: #1f2937;
        font-weight: 600;
      }

      &:hover {
        .logo-text {
          color: #1e40af;
        }
      }
    }
  }

  .navbar-nav {
    display: flex;
    align-items: center;
    gap: 32px;

    .nav-item {
      position: relative;
      display: flex;
      align-items: center;
      color: #6b7280;
      text-decoration: none;
      font-size: 15px;
      font-weight: 500;
      padding: 8px 0;
      transition: all 0.3s ease;

      .nav-arrow {
        margin-left: 4px;
        font-size: 12px;
        transform: rotate(90deg);
        transition: transform 0.3s ease;
      }

      &:hover {
        color: #1e40af;

        .nav-arrow {
          transform: rotate(90deg) translateX(2px);
        }
      }

      &.active {
        color: #1e40af;
        font-weight: 600;

        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 0;
          right: 0;
          height: 2px;
          background: #1e40af;
          border-radius: 1px;
        }
      }
    }

    .dropdown {
      cursor: pointer;

      .dropdown-menu {
        position: absolute;
        top: 100%;
        left: 0;
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 6px;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.3s ease;
        min-width: 120px;
        padding: 8px 0;

        .dropdown-item {
          display: block;
          padding: 8px 16px;
          color: #6b7280;
          text-decoration: none;
          font-size: 14px;
          transition: all 0.3s ease;

          &:hover {
            background: #f3f4f6;
            color: #1e40af;
          }
        }
      }

      &:hover .dropdown-menu {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
      }
    }
  }

  .navbar-actions {
    display: flex;
    align-items: center;
    gap: 12px;

    .el-button {
      border-radius: 6px;
      font-weight: 500;

      &--primary {
        background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%);
        border: none;
        padding: 8px 20px;

        &:hover {
          background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
        }
      }

      &--text {
        color: #6b7280;
        padding: 8px 16px;

        &:hover {
          color: #1e40af;
          background: #f3f4f6;
        }
      }
    }
  }

  .mobile-menu-btn {
    display: none;
    font-size: 24px;
    color: #6b7280;
    cursor: pointer;
    padding: 8px;

    &:hover {
      color: #1e40af;
    }
  }

  .mobile-menu {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border-bottom: 1px solid #e5e7eb;
    transform: translateY(-100%);
    opacity: 0;
    visibility: hidden;
    transition: all 0.3s ease;
    padding: 20px;

    &.show {
      transform: translateY(0);
      opacity: 1;
      visibility: visible;
    }

    .mobile-nav-item {
      display: block;
      padding: 12px 0;
      color: #6b7280;
      text-decoration: none;
      font-size: 16px;
      border-bottom: 1px solid #f3f4f6;

      &:hover {
        color: #1e40af;
      }

      &:last-child {
        border-bottom: none;
      }
    }

    .mobile-actions {
      margin-top: 20px;
      display: flex;
      gap: 12px;
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    .navbar-nav,
    .navbar-actions {
      display: none;
    }

    .mobile-menu-btn {
      display: block;
    }
  }
}

// 为有导航栏的页面添加顶部间距
.has-global-navbar {
  padding-top: 64px;
}
</style> 