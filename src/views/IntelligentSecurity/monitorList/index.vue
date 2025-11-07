<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="监控点名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入监控点名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <cmTaskEditor :info="taskForm" @success="onAddTaskSuccess" :disabled="!taskForm.monitors || !taskForm.monitors.length"/>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-document"
          size="mini"
          @click="onAddTaskSuccess()"

        >查看已有巡更任务</el-button>
      </el-col>
      
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" type="index" />
      <!-- <el-table-column label="主键" align="center" prop="id" /> -->
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="IP" align="center" prop="ip" />
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleDetails(scope.row)"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改Embedding向量数据库配置对话框 -->
    
  </div>
</template>

<script>
import { listStore, getStore, delStore, addStore, updateStore } from "./store";
import supplierList from "./supplierList";
import {
  cloneDeep
} from "lodash";
import cmTaskEditor from "../components/taskEditor";
export default {
  name: "Store",
  components: { cmTaskEditor },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // Embedding向量数据库配置表格数据
      dataList: [],
      // 弹出层标题
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        provider: null,
        host: null,
        port: null,
        username: null,
        password: null,
        databaseName: null,
        tableName: null,
        dimension: null
      },
      // 供应商
      supplierList,
      supplierFiles: {},
      baseDataList: [
        {
          id: 1,
          name: "一厂后门",
          ip: "192.168.55.66"
        },
        {
          id: 2,
          name: "一厂前门",
          ip: "192.168.55.67"
        },
        {
          id: 3,
          name: "一厂实验室",
          ip: "192.168.55.68"
        },
        {
          id: 4,
          name: "一厂吸烟室",
          ip: "192.168.55.69"
        }
      ],
      taskForm: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    onAddTaskSuccess() {
      this.$router.replace({
        path: "/1/taskList"
      })
    },
    handleDetails(item) {
      console.log(item)
      this.$router.replace({
        path: "/1/monitorPics"
      })
      
    },
    /** 查询Embedding向量数据库配置列表 */
    getList() {
      this.loading = true;
      listStore(this.queryParams).then(response => {
        this.dataList = this.baseDataList;
        this.total = this.baseDataList.length;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        name: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      // console.log(selection)
      // this.taskForm.monitors = this.lodash.cloneDeep(selection);
      this.$set(this.taskForm, 'monitors', this.lodash.cloneDeep(selection.map(({id}) => id)))
      console.log(this.taskForm.monitors)
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handleCommand(item) {
      this.handleAdd(item.files);
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateStore(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addStore(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除Embedding向量数据库配置编号为"' + ids + '"的数据项？').then(function() {
        return delStore(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/store/export', {
        ...this.queryParams
      }, `store_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style lang="scss" scoped>
// 移动端适应
@media (max-width: 768px) {
  .app-container {
    padding: 10px;

    // 搜索表单适应
    .el-form {
      .el-form-item {
        width: 100%;
        margin-bottom: 15px;

        .el-form-item__label {
          width: 100px;
          text-align: left;
        }

        .el-form-item__content {
          margin-left: 100px;

          .el-input {
            width: 100%;
          }
        }
      }

      &.el-form--inline {
        .el-form-item {
          width: 100%;
          display: block;
        }
      }
    }

    // 按钮组适应
    .mb8 {
      .el-col {
        margin-bottom: 8px;
        width: 100%;

        .el-button {
          width: 100%;
          font-size: 13px;
        }
      }

      .el-col:last-child {
        .right-toolbar {
          float: none;
          text-align: center;
        }
      }
    }

    // 表格适应
    .el-table {
      font-size: 13px;
      
      .el-table__header-wrapper,
      .el-table__body-wrapper {
        overflow-x: auto;
      }

      .el-table__body {
        .cell {
          word-break: break-all;
          white-space: normal;
        }
      }

      .el-table-column--selection {
        width: 40px;
      }
      
      .small-padding.fixed-width {
        width: 100px;
        
        .el-button {
          padding: 2px 6px;
          font-size: 12px;
        }
      }
    }

    // 分页适应
    .pagination-container {
      text-align: center;
      padding: 15px 0;
    }
  }
}

@media (max-width: 480px) {
  .app-container {
    padding: 5px;

    .el-form {
      .el-form-item {
        .el-form-item__label {
          width: 80px;
          font-size: 12px;
        }

        .el-form-item__content {
          margin-left: 80px;
        }
      }
    }

    .mb8 {
      .el-col {
        .el-button {
          font-size: 12px;
          padding: 6px 12px;
        }
      }
    }

    .el-table {
      font-size: 12px;
      
      .small-padding.fixed-width {
        width: 80px;
        
        .el-button {
          padding: 1px 4px;
          font-size: 11px;
        }
      }
    }
  }
}
</style>
