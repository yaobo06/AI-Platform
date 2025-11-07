<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="任务名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入任务名称"
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
        <el-button
          type="primary"
          plain
          icon="el-icon-document"
          size="mini"
          @click="$router.replace({path: '/1/monitorList'})"

        >新增任务</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column label="任务名称" align="center" prop="name" />
      <el-table-column label="最后执行时间" align="center" prop="lastTime" />
      <el-table-column label="时间间隔" align="center" prop="timeInterval">
        
        <template slot-scope="scope">
          {{ scope.row.timeInterval }} {{ unitList.find(({value}) => value === scope.row.unit).label }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="ifvalid" >
        <template slot-scope="scope">
          <el-tag :type="scope.row.ifvalid === 1? 'success' : 'danger'">{{ scope.row.ifvalid === 1? "启用" : "停用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleChangeState(scope.row)"
          >{{ scope.row.ifvalid === 1 ? "停用" : "启用" }}</el-button>
          <cmTaskEditor :info="{
            ...scope.row,
            monitors: scope.row.monitors.map(({id}) => (id)),

          }" class="el-button el-button--text el-button--mini">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
            >编辑</el-button>
          </cmTaskEditor>
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
      v-show="total>0"
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
          name: "一厂巡更",
          lastTime: "2024-08-01 12:00:00",
          startTime: "2024-08-01 12:00:00",
          timeInterval: "10",
          unit: "1",
          ifvalid: 1,
          receivers: ["1", "2", "3"],
          monitors: [
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
          ]
        },
        {
          id: 2,
          name: "二厂巡更",
          lastTime: "2024-08-01 12:00:00",
          timeInterval: "10",
          unit: "2",
          ifvalid: 0,
          receivers: ["1", "3"],
          monitors: [
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
          ]
        },
        {
          id: 3,
          name: "三厂巡更",
          lastTime: "2024-08-01 12:00:00",
          timeInterval: "1",
          unit: "3",
          ifvalid: 1,
          receivers: ["1"],
          monitors: [
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
          ]
        },
      ],
      unitList: [
        {
          value: "1",
          label: "分钟"
        },
        {
          value: "2",
          label: "小时"
        },
        {
          value: "3",
          label: "天"
        },
        {
          value: "4",
          label: "月"
        }
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleChangeState() {

    },
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
      // this.$set(this.taskForm, 'monitors', this.lodash.cloneDeep(selection))
      // console.log(this.taskForm.monitors)
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
