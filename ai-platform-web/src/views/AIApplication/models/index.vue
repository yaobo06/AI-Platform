<!--<template>-->
<!--  <div class="app-container">-->
<!--    &lt;!&ndash; 返回按钮 &ndash;&gt;-->
<!--    <div class="back-button">-->
<!--      <el-button icon="el-icon-back" @click="goBack">返回主页</el-button>-->
<!--    </div>-->

<!--    &lt;!&ndash; <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">-->
<!--      <el-form-item label="模型名称" prop="model">-->
<!--        <el-input-->
<!--          v-model="queryParams.model"-->
<!--          placeholder="请输入模型名称"-->
<!--          clearable-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      -->
<!--      <el-form-item>-->
<!--        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
<!--      </el-form-item>-->
<!--    </el-form> &ndash;&gt;-->
<!--    <el-tabs v-model="activeTab">-->
<!--      <el-tab-pane -->
<!--        v-for="item in modelProviderTypeList" :key="item.value"-->
<!--        :name="item.value"-->
<!--        :label="item.label">-->
<!--        <transition name="fade-transform" mode="out-in">-->
<!--          <component v-if="show && item.value === activeTab" :is="item.component" :type="item.value" />-->
<!--        </transition>-->
<!--        -->
<!--      </el-tab-pane>-->
<!--    </el-tabs>-->
<!--    -->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import cmChat from "./components/Chat.vue";-->
<!--import cmEmbedding from "./components/Embedding.vue";-->
<!--import cmImage from "./components/Image.vue";-->
<!--export default {-->
<!--  data() {-->
<!--    const modelProviderTypeList = [-->
<!--      {-->
<!--        label: "Chat模型供应商",-->
<!--        value: "1",-->
<!--        component: cmChat,-->
<!--      },-->
<!--      {-->
<!--        label: "Embedding向量模型",-->
<!--        value: "2",-->
<!--        component: cmEmbedding,-->
<!--      },-->
<!--      {-->
<!--        label: "Image文生图模型",-->
<!--        value: "3",-->
<!--        component: cmImage,-->
<!--      }-->
<!--    ]-->
<!--    return {-->
<!--      activeTab: modelProviderTypeList?.[0]?.value,-->
<!--      modelProviderTypeList,-->
<!--      show: true,-->
<!--      showTimer: null-->
<!--    }-->
<!--  },-->
<!--  watch: {-->
<!--    activeTab() {-->
<!--      this.show = false-->
<!--      clearTimeout(this.showTimer)-->
<!--      this.showTimer = setTimeout(() => {-->
<!--        this.show = true-->
<!--      }, 300)-->
<!--    }-->
<!--  },-->
<!--  methods: {-->
<!--    goBack() {-->
<!--      this.$router.push('/');-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</script>-->

<!--<style lang="scss" scoped>-->
<!--.app-container {-->
<!--  width: 100%;-->
<!--  height: 100%;-->
<!--  box-sizing: border-box;-->

<!--  .back-button {-->
<!--    margin: 20px 0;-->
<!--    padding: 0 20px;-->
<!--    -->
<!--    .el-button {-->
<!--      font-size: 16px;-->
<!--      padding: 10px 20px;-->
<!--      background-color: #f1883d;-->
<!--      color: white;-->
<!--      border: none;-->
<!--      -->
<!--      &:hover {-->
<!--        background-color: #e07730;-->
<!--      }-->
<!--      -->
<!--      i {-->
<!--        margin-right: 5px;-->
<!--      }-->
<!--    }-->
<!--  }-->

<!--  ::v-deep .el-tabs {-->
<!--    width: 100%;-->
<!--    height: 100%;-->
<!--    box-sizing: border-box;-->
<!--    display: flex;-->
<!--    flex-direction: column;-->
<!--    .el-tabs__header {-->
<!--      display: flex;-->
<!--      justify-content: center;-->
<!--      padding-bottom: 50px;-->
<!--      border-bottom: 1px solid #ebeef5;-->
<!--      .el-tabs__nav-wrap::after {-->
<!--        display: none;-->
<!--      }-->
<!--      .el-tabs__item {-->
<!--        font-weight: bold;-->
<!--        font-size: 16px;-->
<!--      }-->
<!--    }-->
<!--    .el-tabs__content {-->
<!--      flex-grow: 1;-->
<!--      height: 0px;-->
<!--      .el-tab-pane {-->
<!--        width: 100%;-->
<!--        height: 100%;-->
<!--        box-sizing: border-box;-->
<!--      }-->
<!--    }-->
<!--  }-->
<!--}-->

<!--::v-deep .el-tabs__item.is-active{-->
<!--  color: #f1883d;-->
<!--}-->

<!--::v-deep .el-tabs__active-bar{-->
<!--  background-color: #f1883d;-->
<!--}-->
<!--::v-deep .el-tabs__item{-->
<!--  color: #383838;-->
<!--}-->
<!--</style>-->
