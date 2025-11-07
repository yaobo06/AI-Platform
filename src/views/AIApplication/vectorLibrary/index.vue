<template>
  <div class="app-container  new-ui">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="数据库别名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入别名"
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
        <el-dropdown trigger="click" @command="(index) => {
                handleCommand(supplierList[index]);
              }">
                <el-button
                  type="primary"
                  icon="el-icon-plus"
                  size="mini"
                >新增向量数据库</el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item 
                    v-for="(item, index) in supplierList" 
                    :key="item.value" 
                    :command="index">
                    {{item.label}}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
        
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:store:edit']"
        >修改</el-button>
      </el-col> -->
      <!-- <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:store:remove']"
        >删除</el-button>
      </el-col> -->
      <!-- <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:store:export']"
        >导出</el-button>
      </el-col> -->
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" border :data="storeList" @selection-change="handleSelectionChange">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!-- <el-table-column label="主键" align="center" prop="id" /> -->
      <el-table-column label="数据库别名" align="center" prop="name" />
      <el-table-column label="供应商" align="center" prop="provider" />
      <el-table-column label="数据库地址" align="center" prop="host" />
      <el-table-column label="数据库端口" align="center" prop="port" />
      <el-table-column label="数据库用户名" align="center" prop="username" />
      <el-table-column label="数据库密码" align="center" prop="password" />
      <el-table-column label="数据库名称" align="center" prop="databaseName" />
      <el-table-column label="表名称" align="center" prop="tableName" />
      <el-table-column label="向量维数" align="center" prop="dimension" />
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="big"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            title="编辑"
            v-hasPermi="['system:store:edit']"
          ></el-button>
          <el-button
            size="big"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            title="删除"
            v-hasPermi="['system:store:remove']"
          ></el-button>
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
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item v-if="'name' in supplierFiles" label="数据库别名" prop="name">
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item  v-if="'provider' in supplierFiles" label="供应商" prop="provider" >
          <el-input :value="form.provider" disabled />
        </el-form-item>
        <el-form-item v-if="'host' in supplierFiles" label="数据库地址" prop="host">
          <el-input v-model="form.host" placeholder="请输入" />
        </el-form-item>
        <el-form-item v-if="'port' in supplierFiles" label="数据库端口" prop="port">
          <el-input v-model="form.port" placeholder="请输入" />
        </el-form-item>
        <el-form-item v-if="'databaseName' in supplierFiles" :label="form.provider === 'Redis' ? 'Redis库索引名' : '数据库名'" prop="databaseName">
          <el-input v-model="form.databaseName" placeholder="请输入" />
        </el-form-item>
        <el-form-item v-if="'username' in supplierFiles" label="数据库用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入" />
        </el-form-item>
        <el-form-item v-if="'password' in supplierFiles" label="数据库密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入" />
        </el-form-item>
        
        <el-form-item v-if="'tableName' in supplierFiles" label="表名称" prop="tableName">
          <el-input v-model="form.tableName" placeholder="请输入" />
        </el-form-item>
        <el-form-item v-if="'dimension' in supplierFiles" label="向量维数" prop="dimension">
          <el-input v-model="form.dimension" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStore, getStore, delStore, addStore, updateStore } from "./store";
import supplierList from "./supplierList";
import {
  cloneDeep
} from "lodash";
export default {
  name: "Store",
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
      storeList: [],
      // 弹出层标题
      title: "",
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
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 供应商
      supplierList,
      supplierFiles: {},
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询Embedding向量数据库配置列表 */
    getList() {
      this.loading = true;
      listStore(this.queryParams).then(response => {
        this.storeList = response.rows;
        this.total = response.total;
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
        id: null,
        name: null,
        provider: null,
        host: null,
        port: null,
        username: null,
        password: null,
        databaseName: null,
        tableName: null,
        dimension: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handleCommand(item) {
      this.handleAdd(item.files);
    },
    /** 新增按钮操作 */
    handleAdd(files) {
      this.reset();
      this.supplierFiles = cloneDeep(files);
      this.form = cloneDeep(files);
      this.open = true;
      this.title = "添加";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getStore(id).then(({data}) => {
        // this.form = response.data;
        this.supplierFiles = cloneDeep(supplierList.find(({value}) => value === data.provider)?.files || {});
        this.form = data;
        this.open = true;
        this.title = "修改";
      });
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
