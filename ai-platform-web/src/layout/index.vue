<template>
  <div :class="classObj" class="app-wrapper" :style="{'--current-color': theme}">
    <div v-if="device==='mobile'&&sidebar.opened" class="drawer-bg" @click="handleClickOutside"/>
    <sidebar v-if="!sidebar.hide" class="sidebar-container"/>
    <div :class="{hasTagsView:needTagsView,sidebarHide:sidebar.hide}" class="main-container">
      <div :class="{'fixed-header':fixedHeader}">
        <navbar/>
        <tags-view v-if="needTagsView"/>
      </div>
      <app-main/>
      <right-panel>
        <settings/>
      </right-panel>
    </div>
  </div>
</template>

<script>
import RightPanel from '@/components/RightPanel'
import { AppMain, Navbar, Settings, Sidebar, TagsView } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    RightPanel,
    Settings,
    Sidebar,
    TagsView
  },
  mixins: [ResizeMixin],
  computed: {
    ...mapState({
      theme: state => state.settings.theme,
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables;
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;

    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
  }

  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$base-sidebar-width});
    transition: width 0.28s;
  }

  .hideSidebar .fixed-header {
    width: calc(100% - 54px);
  }

  .sidebarHide .fixed-header {
    width: 100%;
  }

  .mobile .fixed-header {
    width: 100%;
  }

  // 移动端额外样式
  @media (max-width: 768px) {
    .app-wrapper {
      &.mobile {
        .main-container {
          margin-left: 0;
          min-height: calc(100vh - 50px);
        }

        .sidebar-container {
          transition: transform 0.28s;
          width: $base-sidebar-width !important;
        }

        &.hideSidebar .sidebar-container {
          pointer-events: none;
          transition-duration: 0.3s;
          transform: translate3d(-$base-sidebar-width, 0, 0);
        }

        &.openSidebar .sidebar-container {
          transform: translate3d(0, 0, 0);
        }
      }
    }

    .hasTagsView {
      .main-container {
        min-height: calc(100vh - 84px);
      }
    }

    .fixed-header + .app-main {
      padding-top: 84px;
    }

    .fixed-header {
      position: fixed;
      top: 0;
      right: 0;
      z-index: 9;
      width: 100%;
      transition: width 0.28s;
    }
  }

  .main-container ::v-deep .el-button--primary{
    background: #ea6111;
    border: #ea6111;
  }

  .main-container ::v-deep .el-button--primary.is-plain{
    color: #ec6c21;
    background: #fdece2;
    border: 1px solid #ed7732;
  }

  .main-container ::v-deep td .el-icon-delete:before{
    color: #e76259;
  }
  .main-container ::v-deep td .el-icon-delete + span{
    color: #e76259;
  }

  .main-container ::v-deep td .el-button--text{
    color: #413f40
  }

  .main-container ::v-deep td .el-button{
    font-size: 16px;
  }

  .sidebar-container ::v-deep .el-menu .el-menu-item {
    background: #393d45 !important;
    color: #ffffff !important;
  }

  .sidebar-container ::v-deep  .el-submenu__title{
    background: #393d45 !important;
    color: #ffffff !important;
  }

 .sidebar-container ::v-deep .el-menu-item.is-active{
    color: #ff5c00 !important;
 }

  .sidebar-container ::v-deep .submenu-title-noDropdown:hover{
    color: #ff5c00 !important;
 }

  .sidebar-container ::v-deep .el-submenu__title:hover{
    color: #ff5c00 !important;
 }

 .sidebar-container ::v-deep  .el-submenu .el-menu-item:hover{
    color: #ff5c00 !important;
 }
</style>
