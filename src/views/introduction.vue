<template>
  <div class="user-home" :class="responsiveClass">
    <!-- 顶部导航 -->
    <nav class="user-nav" :class="{ 'nav-hidden': !showNav, 'nav-scrolled': isScrolled }">
      <div class="user-logo">
        <svg width="160" height="44" viewBox="0 0 160 44" fill="none" xmlns="http://www.w3.org/2000/svg">
          <!-- 背景装饰 -->
          <path class="bg-decoration" d="M2 22C2 11 11 2 22 2C33 2 42 11 42 22C42 33 33 42 22 42C11 42 2 33 2 22Z"
                stroke="#e2e8f0" stroke-width="0.5" stroke-dasharray="2 2">
            <animate attributeName="stroke-dashoffset" values="0;4" dur="2s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.3;0.8;0.3" dur="4s" repeatCount="indefinite"/>
          </path>

          <!-- 主图形 - 六边形 -->
          <path class="hex-outer" d="M22 4L38 13V31L22 40L6 31V13L22 4Z"
                stroke="#1e40af" stroke-width="1.5" fill="none">
            <animateTransform attributeName="transform" type="rotate"
                            values="0 22 22;360 22 22" dur="20s" repeatCount="indefinite"/>
          </path>

          <!-- 内部装饰线 -->
          <path class="hex-inner" d="M22 8L34 15V29L22 36L10 29V15L22 8Z"
                stroke="#1e40af" stroke-width="1" fill="none">
            <animateTransform attributeName="transform" type="rotate"
                            values="0 22 22;-360 22 22" dur="15s" repeatCount="indefinite"/>
          </path>

          <!-- 中心点 -->
          <circle cx="22" cy="22" r="3" fill="#1e40af">
            <animate attributeName="r" values="2;4;2" dur="2s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.8;1;0.8" dur="2s" repeatCount="indefinite"/>
            <animate attributeName="fill" values="#1e40af;#3b82f6;#1e40af" dur="3s" repeatCount="indefinite"/>
          </circle>

          <!-- 动态连接线 -->
          <g class="connection-lines">
            <path d="M22 12L22 19" stroke="#1e40af" stroke-width="1">
              <animate attributeName="stroke-dasharray" values="0,7;7,0" dur="1.5s" repeatCount="indefinite"/>
            </path>
            <path d="M22 25L22 32" stroke="#1e40af" stroke-width="1">
              <animate attributeName="stroke-dasharray" values="0,7;7,0" dur="1.5s" repeatCount="indefinite"/>
            </path>
            <path d="M16 22L29 22" stroke="#1e40af" stroke-width="1">
              <animate attributeName="stroke-dasharray" values="0,13;13,0" dur="1.5s" repeatCount="indefinite"/>
            </path>
          </g>

          <!-- 装饰点 -->
          <circle class="dot" cx="10" cy="15" r="1" fill="#1e40af">
            <animate attributeName="r" values="1;2;1" dur="3s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.6;1;0.6" dur="3s" repeatCount="indefinite"/>
          </circle>
          <circle class="dot" cx="34" cy="15" r="1" fill="#1e40af">
            <animate attributeName="r" values="1;2;1" dur="3s" begin="0.5s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.6;1;0.6" dur="3s" begin="0.5s" repeatCount="indefinite"/>
          </circle>
          <circle class="dot" cx="10" cy="29" r="1" fill="#1e40af">
            <animate attributeName="r" values="1;2;1" dur="3s" begin="1s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.6;1;0.6" dur="3s" begin="1s" repeatCount="indefinite"/>
          </circle>
          <circle class="dot" cx="34" cy="29" r="1" fill="#1e40af">
            <animate attributeName="r" values="1;2;1" dur="3s" begin="1.5s" repeatCount="indefinite"/>
            <animate attributeName="opacity" values="0.6;1;0.6" dur="3s" begin="1.5s" repeatCount="indefinite"/>
          </circle>

          <!-- 网站名称 -->
          <g class="brand-text">
            <text x="48" y="26" fill="#1e40af" font-size="20" font-weight="600"
                  font-family="'PingFang SC', 'Microsoft YaHei', sans-serif"
                  letter-spacing="0.5">
              AI Platform
            </text>
          </g>
        </svg>
      </div>
      <ul class="user-menu">
        <li>首页</li>
        <span class="model-link" @click="goToAIModels">
          AI应用
          <i class="el-icon-arrow-right"></i>
        </span>
        <span class="model-link" @click="goToForum">
          论坛
          <i class="el-icon-arrow-right"></i>
        </span>
        <li>社区</li>
        <li>更多</li>
      </ul>
      <div class="user-user">
        <template v-if="isLoggedIn">
          <!-- 登录后：显示主页链接和头像 -->
          <router-link to="/introduction" class="home-link">
            <i class="el-icon-house"></i>
            <span>主页</span>
          </router-link>
          <el-dropdown @command="handleUserCommand" trigger="click" placement="bottom-end">
            <div class="user-info">
              <el-avatar
                :src="userAvatar"
                :size="32"
                class="user-avatar">
              </el-avatar>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
        <template v-else>
          <!-- 未登录：显示登录和注册按钮 -->
          <el-button type="primary" size="small" @click="handleLogin">登录</el-button>
          <el-button size="small" @click="handleRegister">注册</el-button>
        </template>
      </div>
    </nav>

    <!-- 轮播图区域 -->
    <section class="user-banner">
      <div class="banner-content">
        <el-carousel :interval="5000" arrow="always" height="500px" class="course-carousel">
          <el-carousel-item v-for="(item, idx) in recommendCourses" :key="idx">
            <img :src="item.img" :alt="item.title" class="carousel-img" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 应用-->
    <section class="user-brands">
      <div class="section-header">
        <div class="section-title">
          Featured AI Applications
        </div>
      </div>
      <div class="brands-carousel">
        <el-carousel height="180px" :autoplay="true" :interval="3000" indicator-position="outside">
          <el-carousel-item v-for="(chunk, chunkIndex) in brandChunks" :key="chunkIndex">
            <div class="brands-slide">
              <a v-for="(brand, idx) in chunk"
                 :key="idx"
                 :href="brand.url"
                 target="_blank"
                 class="brand-item">
                <img :src="brand.img" :alt="brand.name" />
                <span>{{ brand.name }}</span>
              </a>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      <router-link to="/ai-models" class="explore-more-link">
        探索更多AI应用
        <i class="el-icon-arrow-right"></i>
      </router-link>
    </section>

    <!-- 帖子推荐区 -->
    <section class="user-learn-recommend">
      <div class="section-header">
        <div class="section-title">
          <span class="highlight">What</span> Everyone's Reading
        </div>
      </div>
      <div class="learn-cards">
        <div class="learn-card" v-for="(item, idx) in learnings" :key="idx" @click="goToForum">
          <img :src="item.img" class="learn-img" />
          <div class="learn-content">
            <div class="learn-title">{{ item.title }}</div>
            <div class="learn-info">
              <span class="author">
                <i class="el-icon-user"></i>
                {{ item.author }}
              </span>
              <div class="stats">
                <span class="stat-item">
                  <i class="el-icon-view"></i>
                  {{ item.views }}
                </span>
                <span class="stat-item">
                  <i class="el-icon-star-off"></i>
                  {{ item.likes }}
                </span>
                <span class="stat-item">
                  <i class="el-icon-chat-dot-round"></i>
                  {{ item.comments }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <router-link to="/forum" class="section-more">进入论坛 ></router-link>
    </section>

    <!-- 个人学习体验/功能介绍 -->
    <section class="user-features">
      <div class="features-left">
        <div class="feature-block" v-for="(f, idx) in features" :key="idx">
          <div class="feature-title">{{ f.title }}</div>
          <div class="feature-desc">{{ f.desc }}</div>
        </div>
      </div>
      <div class="features-right">
        <img src="/images/page/art_flower.png" class="feature-img" />
      </div>
    </section>

    <!-- AI宣传区 -->
    <section class="user-ai-section">
      <div class="ai-left">
        <div class="ai-title">发现</div>
        <div class="ai-desc">在
          <span class="model-link" @click="goToCommunity">
            社区
            <i class="el-icon-arrow-right"></i>
          </span>
          寻找你需要的一切
        </div>
        <el-button type="primary" @click="goToCommunity">进入</el-button>
      </div>
      <div class="ai-right">
        <img src="/images/page/art_corridor.png" class="ai-img" />
      </div>
    </section>

    <!-- 底部导航 -->
    <footer class="user-footer">
      <div class="footer-links">
        <a href="#" class="footer-link">
          <i class="el-icon-question"></i>
          <span>帮助中心</span>
        </a>
        <a href="#" class="footer-link">
          <i class="el-icon-message"></i>
          <span>联系我们</span>
        </a>
        <a href="#" class="footer-link">
          <i class="el-icon-info"></i>
          <span>关于我们</span>
        </a>
      </div>
      <div class="footer-bottom">
        <div class="footer-logo">AI平台</div>
        <div class="footer-copyright">© 2025 AI平台 试用版</div>
      </div>
    </footer>
  </div>
</template>

<script>
import { getResponsiveClass } from '@/utils/mobile'
import forumAuthMixin from '@/mixins/forum-auth'

export default {
  name: 'introduction',
  mixins: [forumAuthMixin],
  data() {
    return {
      showNav: true,
      isScrolled: false,
      lastScrollTop: 0,
      scrollThreshold: 100,
      categories: [
        { name: 'ChatGPT' }, { name: '文心一言' }, { name: '通义千问' }, { name: 'Claude' }, { name: 'Suno AI' }, { name: 'Gemini' }, { name: 'Viggle' }
      ],
      recommendCourses: [
        {
          img: '/images/carousel/dark.png',
          title: 'AI与未来'
        },
        {
          img: '/images/carousel/art.png',
          title: 'AI创新'
        },
        {
          img: '/images/carousel/mountain.png',
          title: 'AI科技'
        },
        {
          img: '/images/carousel/night_sky.png',
          title: 'AI艺术'
        },
        {
          img: '/images/carousel/ai_cube.jpg',
          title: 'AI未来'
        }
      ],
      brands: [
        {
          img: '/images/brands/chatgpt.png',
          name: 'ChatGPT',
          url: 'https://chat.openai.com'
        },
        {
          img: '/images/brands/deepseek.png',
          name: 'Deepseek',
          url: 'https://chat.deepseek.com'
        },
        {
          img: '/images/brands/dify.png',
          name: 'Dify',
          url: 'https://cloud.dify.ai'
        },
        {
          img: '/images/brands/claude.png',
          name: 'Claude',
          url: 'https://claude.ai'
        },
        {
          img: '/images/brands/midjourney.png',
          name: 'Midjourney',
          url: 'https://www.midjourney.com'
        },
        {
          img: '/images/brands/gemini.png',
          name: 'Gemini',
          url: 'https://gemini.google.com'
        },
        {
          img: '/images/brands/stability.png',
          name: 'Stable Diffusion',
          url: 'https://stability.ai'
        },
        {
          img: '/images/brands/perplexity.png',
          name: 'Perplexity',
          url: 'https://www.perplexity.ai'
        },
        {
          img: '/images/brands/copilot.png',
          name: 'Copilot',
          url: 'https://copilot.microsoft.com'
        },
        {
          img: '/images/brands/huggingface.png',
          name: 'Hugging Face',
          url: 'https://huggingface.co'
        },
        {
          img: '/images/brands/cohere.png',
          name: 'Cohere',
          url: 'https://cohere.ai'
        },
        {
          img: '/images/brands/anthropic.png',
          name: 'Anthropic',
          url: 'https://www.anthropic.com'
        }
      ],
      learnings: [],
      features: [
        { title: 'AI应用整理', desc: '收纳市面主流AI应用' },
        { title: 'AI论坛', desc: '可供交流的线上平台'},
        { title: '社区', desc: '探索属于你的AI内容' },
        { title: '线上交易网站', desc: '智能体供需对接窗口' },
      ],
      footerLinks: [
        { title: '支持', links: ['帮助中心', '联系我们', '关于我们'] },
      ],
      loading: false,
      models: [],
      activeCategory: 'all',
    };
  },
  computed: {
    isLoggedIn() {
      return this.isForumLoggedIn
    },
    userAvatar() {
      return this.forumUserAvatar
    },
    userInfo() {
      return this.forumUserInfo
    }
  },
  created() {
    this.fetchHotPosts()
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  methods: {
    handleUserCommand(command) {
      if (command === 'profile') {
        this.$router.push('/forum/profile')
      } else if (command === 'logout') {
        this.doForumLogout()
      }
    },
    handleLogin() {
      this.$router.push('/forum/login');
    },
    handleRegister() {
      this.$router.push('/forum/login?action=register');
    },
    goToForum() {
      this.$router.push('/forum');
    },
    goToAIModels() {
      this.$router.push('/ai-models');
    },
    goToCommunity() {
      this.$router.push('/community');
    },
    handleScroll() {
      const currentScrollTop = window.pageYOffset || document.documentElement.scrollTop;

      // 判断是否已经滚动
      this.isScrolled = currentScrollTop > 50;

      // 根据滚动方向决定是否显示导航栏
      if (currentScrollTop > this.lastScrollTop && currentScrollTop > this.scrollThreshold) {
        // 向下滚动且超过阈值，隐藏导航栏
        this.showNav = false;
      } else if (currentScrollTop < this.lastScrollTop || currentScrollTop <= this.scrollThreshold) {
        // 向上滚动或在页面顶部，显示导航栏
        this.showNav = true;
      }

      this.lastScrollTop = currentScrollTop;
    },
    async fetchHotPosts() {
      // 模拟从论坛获取热门帖子的数据
      this.learnings = [
        {
          id: 1,
          img: 'https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&w=600&q=80',
          title: '分享：如何使用ChatGPT提高编程效率',
          author: '张三',
          views: 1200,
          likes: 88,
          comments: 25
        },
        {
          id: 2,
          img: 'https://images.unsplash.com/photo-1620712943543-bcc4688e7485?auto=format&fit=crop&w=600&q=80',
          title: 'Stable Diffusion最新版本功能详解',
          author: '李四',
          views: 980,
          likes: 76,
          comments: 32
        },
        {
          id: 3,
          img: 'https://images.unsplash.com/photo-1555949963-aa79dcee981c?auto=format&fit=crop&w=600&q=80',
          title: 'Claude API 调用最佳实践',
          author: '王五',
          views: 850,
          likes: 65,
          comments: 28
        },
        {
          id: 4,
          img: 'https://images.unsplash.com/photo-1547954575-855750c57bd3?auto=format&fit=crop&w=600&q=80',
          title: 'AI绘画进阶技巧分享',
          author: '赵六',
          views: 760,
          likes: 59,
          comments: 22
        },
        {
          id: 5,
          img: 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?auto=format&fit=crop&w=600&q=80',
          title: 'GPT-4与传统编程的完美结合',
          author: '陈七',
          views: 1350,
          likes: 102,
          comments: 45
        },
        {
          id: 6,
          img: 'https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?auto=format&fit=crop&w=600&q=80',
          title: 'AI音频生成技术深度解析',
          author: '刘八',
          views: 920,
          likes: 73,
          comments: 38
        }
      ];
    },
    async fetchModels() {
      try {
        this.loading = true;
        const response = await this.$http.get('/api/models');
        // 将数据库返回的数据转换为前端需要的格式
        this.models = response.data.map(item => ({
          id: item.id,
          name: item.name,
          // ... 其他字段映射
        }));
      } catch (error) {
        this.$message.error('获取数据失败');
      } finally {
        this.loading = false;
      }
    },
    getStatusType(statusCode) {
      const statusMap = {
        1: 'hot',
        2: 'new',
        3: 'popular'
      };
      return statusMap[statusCode] || 'normal';
    },
    getTags(item) {
      return item.tags ? item.tags.split(',') : [];
    }
  },
  computed: {
    filteredModels() {
      if (this.activeCategory === 'all') {
        return this.models;
      }
      return this.models.filter(model => model.category === this.activeCategory);
    },
    brandChunks() {
      const chunkSize = 6; // 每页显示6个应用
      const chunks = [];
      for (let i = 0; i < this.brands.length; i += chunkSize) {
        chunks.push(this.brands.slice(i, i + chunkSize));
      }
      return chunks;
    },
    responsiveClass() {
      return getResponsiveClass();
    }
  }
};
</script>

<style scoped lang="scss">
// 响应式断点
$breakpoint-mobile: 640px;
$breakpoint-tablet: 768px;
$breakpoint-laptop: 1024px;
$breakpoint-desktop: 1280px;

@mixin container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;

  @media (max-width: $breakpoint-mobile) {
    padding: 0 16px;
  }
}

@mixin flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

@mixin flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-home {
  background: #ffffff;
  color: #333333;
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Segoe UI', Arial, sans-serif;
  min-height: 100vh;
  overflow-x: hidden;
}

.user-nav {
  @include flex-between;
  padding: 20px 48px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateY(0);

  &.nav-hidden {
    transform: translateY(-100%);
  }

  &.nav-scrolled {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid rgba(229, 231, 235, 0.8);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    padding: 16px 48px;
  }

  @media (max-width: $breakpoint-mobile) {
    padding: 16px 16px;
  }

  .user-logo {
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      transform: scale(1.05);
    }

    svg {
      overflow: visible;

      .hex-outer {
        stroke: #1e40af;
        stroke-width: 1.5;
      }

      .hex-inner {
        stroke: #1e40af;
        stroke-width: 1;
      }

      .dot {
        fill: #1e40af;
      }

      .connection-lines path {
        stroke: #1e40af;
        stroke-width: 1;
      }

      .brand-text {
        animation: gentle-glow 3s ease-in-out infinite alternate;
      }

      .brand-text text {
        fill: #1e40af;
        font-size: 22px;
        font-weight: 500;
        font-family: 'KaiTi', '楷体', 'STKaiti', serif;
        letter-spacing: 0.5px;
        transition: all 0.3s ease;
      }

      .brand-text:hover text {
        fill: #2563eb;
      }
    }
  }

  .user-menu {
    display: flex;
    align-items: center;
    gap: 32px;
    margin: 0;
    padding: 0;
    list-style: none;

    li, .model-link, a {
      font-size: 16px;
      font-weight: 500;
      color: #666666;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;

      &:hover {
        color: #1e40af;
      }
    }

    .model-link i {
      margin-left: 6px;
      transition: transform 0.3s ease;
    }

    .model-link:hover i {
      transform: translateX(4px);
    }
  }

  .user-user {
    display: flex;
    align-items: center;
    gap: 12px;

    .home-link {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #6b7280;
      text-decoration: none;
      font-size: 14px;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: 6px;
      transition: all 0.3s ease;

      i {
        font-size: 16px;
      }

      &:hover {
        color: #1e40af;
        background: #f3f4f6;
      }
    }

    .user-info {
      .user-avatar {
        cursor: pointer;
        border: 2px solid #e5e7eb;
        transition: all 0.3s ease;

        &:hover {
          border-color: #1e40af;
          transform: scale(1.05);
        }
      }
    }

    .el-button--primary {
      background: #1e40af;
      border: none;
      border-radius: 4px;
      padding: 12px 24px;
      font-weight: 600;

      &:hover {
        background: #1d4ed8;
      }
    }

    .el-button:not(.el-button--primary) {
      background: #ffffff;
      color: #666666;
      border: 1px solid #d1d5db;
      border-radius: 4px;
      padding: 12px 24px;
      font-weight: 600;

      &:hover {
        background: #f9fafb;
        border-color: #1e40af;
        color: #1e40af;
      }
    }
  }
}

.user-banner {
  @include container;
  padding-top: 120px;
  margin-bottom: 60px;
  position: relative;
  z-index: 1;

  .banner-content {
    position: relative;

    .course-carousel {
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

      :deep(.el-carousel__arrow) {
        background: rgba(255, 255, 255, 0.9);
        color: #666666;
        border-radius: 50%;
        width: 50px;
        height: 50px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

        &:hover {
          background: #1e40af;
          color: white;
        }
      }

      :deep(.el-carousel__indicators) {
        bottom: 20px;

        .el-carousel__button {
          width: 40px;
          height: 4px;
          border-radius: 2px;
          background: rgba(255, 255, 255, 0.6);
        }

        .is-active .el-carousel__button {
          background: #1e40af;
        }
      }

      .carousel-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

.user-brands {
  @include container;
  padding: 80px 0 100px 0;
  background: #f9fafb;
  border-radius: 8px;
  margin: 40px auto;
  position: relative;
  z-index: 1;

  .section-header {
    text-align: center;
    margin-bottom: 60px;
  }

  .section-title {
    font-size: 3.5rem;
    font-weight: 700;
    color: #2c2c2c;
    font-style: normal;
    letter-spacing: -0.5px;
    margin-bottom: 24px;
    font-family: 'SimHei', '黑体', 'Microsoft YaHei UI Bold', 'Arial Black', sans-serif;
    line-height: 1.2;

    .highlight {
      color: #2c2c2c;
      font-style: normal;
    }
  }

  .brands-carousel {
    padding: 0 40px;
    margin-bottom: 90px;

    :deep(.el-carousel__arrow) {
      background: rgba(255, 255, 255, 0.9);
      color: #666666;
      border-radius: 50%;
      width: 40px;
      height: 40px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);

      &:hover {
        background: #1e40af;
        color: white;
      }
    }

    :deep(.el-carousel__indicators) {
      bottom: -30px;

      .el-carousel__button {
        width: 30px;
        height: 4px;
        border-radius: 2px;
        background: rgba(30, 64, 175, 0.3);
      }

      .is-active .el-carousel__button {
        background: #1e40af;
      }
    }
  }

  .brands-slide {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 24px;
    height: 100%;
    padding: 0 20px;
  }

  .brand-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-decoration: none;
    color: #666666;
    transition: all 0.3s ease;
    padding: 24px;
    border-radius: 12px;
    background: #f8f9fa;
    border: 1px solid #e5e7eb;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    min-width: 160px;
    flex: 1;

    &:hover {
      transform: translateY(-6px);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
      border-color: #1e40af;
      background: #f1f3f4;
    }

    img {
      width: 56px;
      height: 56px;
      object-fit: contain;
      margin-bottom: 12px;
      margin-right: 0;
    }

    span {
      font-size: 16px;
      font-weight: 600;
      color: #111827;
      text-align: center;
    }
  }

  .explore-more-link {
    position: absolute;
    bottom: 40px;
    right: 40px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
    text-decoration: none;
    padding: 14px 28px;
    border-radius: 6px;
    background: #1e40af;
    box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
    transition: all 0.3s ease;

    &:hover {
      background: #1d4ed8;
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(30, 64, 175, 0.4);
    }

    i {
      font-size: 14px;
      transition: transform 0.3s ease;
    }

    &:hover i {
      transform: translateX(4px);
    }
  }
}

.user-learn-recommend {
  position: relative;
  padding: 80px 0 100px 0;
  background: #ffffff;
  border-radius: 8px;
  margin: 40px auto;
  border: 1px solid #e5e7eb;
  z-index: 1;

  .section-header {
    display: flex;
    justify-content: center;
    margin-bottom: 60px;
  }

  .section-title {
    font-size: 3.5rem;
    font-weight: 700;
    color: #2c2c2c;
    font-style: normal;
    letter-spacing: -0.5px;
    font-family: 'SimHei', '黑体', 'Microsoft YaHei UI Bold', 'Arial Black', sans-serif;
    line-height: 1.2;

    .highlight {
      color: #2c2c2c;
      font-style: normal;
    }
  }

  .learn-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 24px;
    padding: 0 40px;

    .learn-card {
      background: #ffffff;
      border: 1px solid #e5e7eb;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;
      cursor: pointer;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        border-color: #1e40af;
      }

      .learn-img {
        width: 100%;
        height: 180px;
        object-fit: cover;
      }

      .learn-content {
        padding: 20px;

        .learn-title {
          font-size: 1.2rem;
          font-weight: 700;
          color: #111827;
          margin-bottom: 12px;
          line-height: 1.4;
        }

        .learn-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
          font-size: 0.9rem;
          color: #6b7280;

          .author {
            display: flex;
            align-items: center;
            gap: 6px;
            color: #374151;
            font-weight: 600;
          }

          .stats {
            display: flex;
            gap: 12px;

            .stat-item {
              display: flex;
              align-items: center;
              gap: 4px;
              color: #6b7280;
            }
          }
        }
      }
    }
  }

  .section-more {
    position: absolute;
    bottom: 40px;
    right: 40px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
    text-decoration: none;
    padding: 14px 28px;
    border-radius: 6px;
    background: #1e40af;
    box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);
    transition: all 0.3s ease;

    &:hover {
      background: #1d4ed8;
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(30, 64, 175, 0.4);
    }
  }
}

.user-features, .user-ai-section {
  @include container;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  padding: 80px 40px;
  background: #f9fafb;
  border-radius: 8px;
  margin: 40px auto;
  align-items: center;
  z-index: 1;

  .features-left {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;

    .feature-block {
      background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
      border: 2px solid #e2e8f0;
      padding: 40px 32px;
      border-radius: 16px;
      margin-bottom: 0;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05), 0 1px 3px rgba(0, 0, 0, 0.1);
      position: relative;
      overflow: hidden;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, #1e40af, #3b82f6, #6366f1);
        transform: scaleX(0);
        transition: transform 0.4s ease;
      }

      &:hover {
        transform: translateY(-8px) scale(1.02);
        box-shadow: 0 20px 25px rgba(0, 0, 0, 0.1), 0 10px 10px rgba(0, 0, 0, 0.04);
        border-color: #1e40af;

        &::before {
          transform: scaleX(1);
        }

        .feature-title {
          color: #1d4ed8;
        }
      }

      .feature-title {
        font-size: 1.5rem;
        font-weight: 800;
        margin-bottom: 16px;
        color: #1e40af;
        transition: color 0.3s ease;
        letter-spacing: 0.5px;
        display: flex;
        align-items: center;
        gap: 12px;

        &::before {
          content: '';
          width: 8px;
          height: 8px;
          background: #1e40af;
          border-radius: 50%;
          flex-shrink: 0;
        }
      }

      .feature-desc {
        color: #64748b;
        line-height: 1.7;
        font-size: 1.1rem;
        font-weight: 500;
        margin: 0;
      }
    }
  }

  .ai-left {
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    padding-top: 40px;

    .ai-title {
      font-size: 5.5rem;
      font-weight: 900;
      color: #111827;
      margin-bottom: 50px;
      line-height: 1.2;
    }

    .ai-desc {
      font-size: 1.6rem;
      color: #6b7280;
      line-height: 1.6;
      margin-bottom: 50px;

      .model-link {
        color: #1e40af;
        font-weight: 600;
        text-decoration: none;
        border-bottom: 2px solid transparent;
        transition: all 0.3s ease;

        &:hover {
          border-bottom-color: #1e40af;
        }
      }
    }

    .el-button--primary {
      background: #1e40af;
      border: none;
      padding: 22px 48px;
      font-size: 24px;
      font-weight: 700;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3);

      &:hover {
        background: #1d4ed8;
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(30, 64, 175, 0.4);
      }
    }
  }

  .features-right, .ai-right {
    img {
      width: 100%;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      border: 1px solid #e5e7eb;
    }
  }
}

.user-footer {
  background: #f9fafb;
  color: #374151;
  padding: 60px 0 40px;
  margin-top: 40px;
  text-align: center;
  position: relative;
  z-index: 1;
  border-top: 1px solid #e5e7eb;

  .footer-links {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 2px;
    background: #e5e7eb;
    margin-bottom: 40px;
    border-radius: 8px;
    overflow: hidden;

    .footer-link {
      color: #6b7280;
      text-decoration: none;
      font-size: 1.1rem;
      font-weight: 600;
      padding: 32px 20px;
      background: #ffffff;
      transition: all 0.3s ease;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;

      i {
        font-size: 24px;
        color: #1e40af;
        transition: transform 0.3s ease;
      }

      &:hover {
        color: #111827;
        background: #f3f4f6;

        i {
          transform: scale(1.1);
        }
      }
    }
  }

  .footer-bottom {
    .footer-logo {
      font-size: 1.8rem;
      font-weight: 800;
      color: #111827;
      margin-bottom: 16px;
    }

    .footer-copyright {
      color: #6b7280;
      font-size: 0.9rem;
    }
  }
}

// 全局样式增强
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

// 动画关键帧
@keyframes gentle-glow {
  0% {
    opacity: 0.9;
  }
  100% {
    opacity: 1;
  }
}

// 移动端适配
@media (max-width: $breakpoint-mobile) {
  .user-nav {
    padding: 12px 16px;

    .user-menu {
      display: none; // 移动端隐藏导航菜单
    }

    .user-user {
      gap: 8px;

      .el-button {
        padding: 8px 16px;
        font-size: 14px;
      }
    }
  }

  .user-banner {
    padding-top: 80px;
    margin-bottom: 40px;

    .course-carousel {
      height: 250px !important;
    }
  }

  .user-brands {
    padding: 40px 0 50px 0;
    margin: 20px auto;

    .section-title {
      font-size: 2.5rem;
    }

    .brands-carousel {
      padding: 0 20px;
      margin-bottom: 60px;
    }

    .brands-slide {
      gap: 16px;
      flex-wrap: wrap;
      justify-content: center;

      .brand-item {
        width: calc(50% - 8px);
        margin-bottom: 16px;
        padding: 16px;

        img {
          width: 40px;
          height: 40px;
        }

        span {
          font-size: 12px;
        }
      }
    }
  }

  .user-learn-recommend {
    .section-title {
      font-size: 2.5rem;
    }

    .learn-cards {
      grid-template-columns: 1fr;
      gap: 20px;
    }

    .learn-card {
      .learn-img {
        height: 150px;
      }

      .learn-content {
        padding: 16px;

        .learn-title {
          font-size: 16px;
          margin-bottom: 12px;
        }

        .learn-info {
          flex-direction: column;
          align-items: flex-start;
          gap: 8px;

          .stats {
            gap: 16px;
          }
        }
      }
    }
  }

  .user-features {
    flex-direction: column;
    gap: 40px;

    .features-left {
      grid-template-columns: 1fr;
      gap: 20px;

      .feature-block {
        padding: 24px 20px;

        .feature-title {
          font-size: 1.2rem;
        }

        .feature-desc {
          font-size: 1rem;
        }
      }
    }

    .features-right {
      order: -1;
    }
  }

  .user-ai-section {
    flex-direction: column;
    gap: 40px;

    .ai-left {
      padding-top: 0;

      .ai-title {
        font-size: 3rem;
        margin-bottom: 30px;
      }

      .ai-desc {
        font-size: 1.2rem;
        margin-bottom: 30px;
      }

      .el-button--primary {
        padding: 16px 32px;
        font-size: 18px;
      }
    }

    .ai-right {
      order: -1;
    }
  }

  .user-footer {
    padding: 40px 0 30px;

    .footer-links {
      grid-template-columns: 1fr;
      gap: 1px;

      .footer-link {
        padding: 24px 20px;
        font-size: 1rem;

        i {
          font-size: 20px;
        }
      }
    }
  }
}

// 平板适配
@media (min-width: #{$breakpoint-mobile + 1px}) and (max-width: $breakpoint-tablet) {
  .user-nav {
    padding: 16px 32px;

    .user-menu {
      gap: 24px;

      li, .model-link {
        font-size: 15px;
      }
    }
  }

  .user-brands .brands-slide {
    gap: 20px;

    .brand-item {
      padding: 20px;

      img {
        width: 50px;
        height: 50px;
      }
    }
  }

  .user-learn-recommend .learn-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .user-features .features-left {
    grid-template-columns: repeat(2, 1fr);
  }
}


</style>
