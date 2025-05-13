<template>
  <div class="app-container">
    <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模型名称" prop="model">
        <el-input
          v-model="queryParams.model"
          placeholder="请输入模型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->
    <el-tabs v-model="activeTab">
      <el-tab-pane 
        v-for="item in modelProviderTypeList" :key="item.value"
        :name="item.value"
        :label="item.label">
        <component :is="item.component" />
      </el-tab-pane>
    </el-tabs>
    
  </div>
</template>

<script>
import cmChat from "./components/Chat.vue";
export default {
  data() {
    return {
      activeTab: 0,
      modelProviderTypeList: [
        {
          label: "Chat模型供应商",
          value: "0",
          component: cmChat,
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  .el-tabs {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    ::v-deep .el-tabs__content {
      flex-grow: 1;
      height: 0px;
    }
  }
}
</style>
