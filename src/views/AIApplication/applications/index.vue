<template>
  <div class="app-container">
    <!-- <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关联模型" prop="modelId">
        <el-input
          v-model="queryParams.modelId"
          placeholder="请输入关联模型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="封面" prop="cover">
        <el-input
          v-model="queryParams.cover"
          placeholder="请输入封面"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="描述" prop="des">
        <el-input
          v-model="queryParams.des"
          placeholder="请输入描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="保存时间" prop="saveTime">
        <el-date-picker clearable
          v-model="queryParams.saveTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择保存时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form> -->
    <el-row :gutter="10" class="mb8" type="flex">
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4">
        <div class="card add" @click="handleAdd">
          <div class="title">创建应用</div>
          <div class="content">
            <svg-icon icon-class="edit" />
            创建空白应用
          </div>
        </div>
      </el-col>
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4" v-for="(item) in appList" :key="item.id">
        <div class="card edit">
          <div class="content">
            <div class="img">
              <svg-icon v-if="!item.cover" icon-class="documentation" />
              <img v-else :src="item.cover" alt="" />
            </div>
            <div class="context">
              <div class="title">{{ item.name }}</div>
              <div class="desc">{{ item.des }}</div>
            </div>
          </div>
          <div class="footer">
            <div class="left">

            </div>
            <div class="right">
              <el-dropdown trigger="click" @command="(methodName) => {
                handleCommand(methodName, item);
              }">
                <el-button class="more-btn" type="text" size="mini" icon="el-icon-more"></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item icon="el-icon-edit" command="handleUpdate">编辑此应用</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-delete" command="handleDelete">删除此应用</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <!-- <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:app:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:app:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:app:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:app:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row> -->

    <!-- <el-table v-loading="loading" :data="appList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="关联模型" align="center" prop="modelId" />
      <el-table-column label="关联知识库" align="center" prop="knowledgeIds" />
      <el-table-column label="封面" align="center" prop="cover" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="提示词" align="center" prop="prompt" />
      <el-table-column label="描述" align="center" prop="des" />
      <el-table-column label="保存时间" align="center" prop="saveTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.saveTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:app:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:app:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table> -->
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改提示词对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="应用名称" prop="name" required :rules="[{required: true, message: '请输入应用名称', trigger: 'blur'}]">
          <el-input v-model="form.name" type="text" maxlength="20" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="关联模型" prop="modelId" required :rules="[{required: true, message: '请选择关联模型', trigger: 'blur'}]">
          <el-select v-model="form.modelId" remote :loading="modelsLoading" :remote-method="modelsRemoteMethod" filterable placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="item in models || []"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="应用封面" prop="cover">
          <ImageUpload v-model="form.cover" :limit="1"/>
        </el-form-item>
        <el-form-item label="应用描述" prop="des">
          <el-input v-model="form.des" type="textarea" rows="3" maxlength="300" placeholder="请输入" />
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
import { listApp, getApp, delApp, addApp, updateApp, getModels } from "./app";

export default {
  name: "App",
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
      // 提示词表格数据
      appList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        modelId: null,
        knowledgeIds: null,
        cover: null,
        name: null,
        prompt: null,
        des: null,
        saveTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 模型选项
      models: [],
      getModelsTimer: null,
      modelsLoading: false
    };
  },
  watch: {
    open(n) {
      if(!n) return;
      this.modelsRemoteMethod("", true);
    }
  },
  created() {
    this.getList();
    this.modelsRemoteMethod();
  },
  methods: {
    // 获取模型
    modelsRemoteMethod(str, immediate = false) {
      this.getModelsTimer && clearTimeout(this.getModelsTimer);
      this.getModelsTimer = setTimeout(() => {
        this.modelsLoading = true;
        getModels(str).then(res => {
          console.log(111, res)
          this.models = res.data;
        }).catch(() => {
          this.models = [];
        }).finally(() => {
          this.modelsLoading = false;
        });
      }, immediate ? 0 : 800);

      
    },
    /** 查询提示词列表 */
    getList() {
      this.loading = true;
      listApp(this.queryParams).then(response => {
        this.appList = response.rows;
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
        modelId: null,
        knowledgeIds: null,
        cover: null,
        name: null,
        prompt: null,
        des: null,
        saveTime: null,
        createTime: null
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
    handleCommand(methodName, item) {
      this[methodName](item);
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加提示词";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getApp(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改提示词";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateApp(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addApp(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除提示词编号为"' + ids + '"的数据项？').then(function() {
        return delApp(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/app/export', {
        ...this.queryParams
      }, `app_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
<style lang="scss" scoped>
.app-container {
  .card {
    border: 1px solid #DCDFE6;
    border-radius: 8px;
    padding: 10px 14px 0px 14px;
    box-sizing: border-box;
    height: 100%;
    min-height: 140px;
    &.add {
      background-color: #F3F4F6;
      cursor: pointer;
      .title {
        font-size: 16px;
        color: #6B7280;
        font-weight: bold;
      }
      .content {
        font-size: 14px;
        color: #333639;
        margin-top: 14px;
      }
    }
    &.edit {
      display: flex;
      flex-direction: column;
      .content {
        flex: 1;
        display: flex;
        .img {
          width: 70px;
          height: 70px;
          color: #333639;
          font-size: 30px;
          display: flex;
          justify-content: center;
          align-items: center;
          background-color: #DBEAFE;
          border-radius: 8px;
          img {
            width: 100%;
            height: 100%;
            object-fit: contain;
          }
        }
        .context {
          margin-left: 14px;
          flex: 1;
          .title {
            font-size: 16px;
            color: #374151;
            font-weight: bold;
          }
          .desc {
            margin-top: 8px;
            font-size: 14px;
            color: #6B7280;
            display: -webkit-box; /* 将对象作为弹性伸缩盒子模型显示 */
            -webkit-box-orient: vertical; /* 设置或检索伸缩盒对象的子元素的排列方式 */
            -webkit-line-clamp: 3; /* 限制显示的文本行数 */
            overflow: hidden; /* 隐藏超出的内容 */
            text-overflow: ellipsis; /* 使用省略号表示被修剪的文本 */
          }
        }
      }
      .footer {
        display: flex;
        .left {
          flex: 1;
        }
        .right {
          .more-btn {
            font-size: 18px;
          }
        }
      }
    }
  }
}
</style>