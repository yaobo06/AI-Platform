<template>
  <div class="app-container">
    <el-row :gutter="10" class="list mb8" type="flex" gutter="25">
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4" style="margin-top: 20px;">
        <div class="card add" @click="handleAdd">
          <div class="content">
            <svg-icon icon-class="edit" />
            创建空白应用
          </div>
        </div>
      </el-col>
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4" v-for="(item) in appList" :key="item.id" style="margin-top: 20px;">
        <div class="card edit" @click.stop="goTo(item)">
          <div class="content">
            <div class="img">
              <svg-icon v-if="!item.cover" icon-class="documentation" />
              <img v-else :src="item.src" alt="" />
            </div>
            <div class="context">
              <div class="title">{{ item.name }}</div>
              <div class="desc" :title="item.des">{{ item.des }}</div>
            </div>
          </div>
          <div class="footer">
            <div class="left">

            </div>
            <div class="right" @click.stop>
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
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
      class="app-page"
    />
    <!-- 添加或修改提示词对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body class="app-dialog">
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
          <ImageUpload v-model="form.cover" :limit="1" width="50px" height="50px"/>
        </el-form-item>
        <el-form-item label="链接地址" prop="appUrl" required :rules="[{required: true, message: '请输入链接地址', trigger: 'blur'}, {validator: (rule, value, callback) => {
          /^http/.test(value) ? callback() : callback('请输入http链接地址');
        }, message: '请输入http链接地址', trigger: 'blur'}]">
          <el-input v-model="form.appUrl" type="text" maxlength="300" placeholder="请输入内容" />
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
    goTo({appUrl}) {
      if(!appUrl) {
        this.$modal.msgError('链接地址为空');
        return;
      }
      window.open(appUrl);
    },
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
      // console.log(888,process.env.VUE_APP_BASE_API)
      this.loading = true;
      listApp(this.queryParams).then(response => {
        this.appList = response.rows.map(item => {
          return {
            ...item,
            src: `${process.env.VUE_APP_BASE_API}${item.cover}`
          }
        });
        console.log(888, this.appList)
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
      this.title = "新增/编辑";
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
.pagination-container {
  background: #f4f4f4;
}

.el-button--text{
  color: #7a7a7a;
}

.app-page ::v-deep .el-pagination.is-background .btn-prev{
  background: #ffffff;
}
.app-page ::v-deep .el-pagination.is-background .btn-next{
  background: #ffffff;
}
.app-dialog ::v-deep .el-upload--picture-card{
  width: 60px;
  height: 60px;
  line-height: 65px;
  background-color: #f6f6f6;
  border: 1px dashed #eeeeee;
  vertical-align: middle;
}
.app-dialog ::v-deep .el-dialog__title{
  line-height: 24px;
  font-size: 18px;
  color: #3d3d3d;
  font-weight: 700;
}
.app-dialog ::v-deep .el-form-item__label{
   color: #656565;
}
.app-dialog ::v-deep .el-button--primary{
  background: #ea6111;
  border: #ea6111;
}

.app-container {
  background: #f4f4f4;
  width: 100%;
  height: 100%;
  .list {
    flex-wrap: wrap;
    .el-col {
      margin-top: 10px;
    }
  }
  .card {
    border: 1px solid #DCDFE6;
    border-radius: 8px;
    padding: 10px 14px 8px 14px;
    box-sizing: border-box;
    height: 100%;
    min-height: 140px;
    cursor: pointer;
    transition: all 0.3s;
    background-color: #ffffff;
    
    &:hover {
      box-shadow: 0px 0px 4px 4px #f1f1f1;
    }
    &.add {
      .title {
        font-size: 16px;
        color: #6B7280;
        font-weight: bold;
      }
      .content {
        font-size: 18px;
        color: #7a7a7a;
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        width: 100%;
      }
    }
    &.edit {
      display: flex;
      flex-direction: column;
      .content {
        flex: 1;
        display: flex;
        padding: 16px 2px 0px 2px;
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
            color: #4a4a4a;
            font-weight: bold;
          }
          .desc {
            margin-top: 8px;
            font-size: 14px;
            color: #8b8b8b;
            display: -webkit-box; /* 将对象作为弹性伸缩盒子模型显示 */
            -webkit-box-orient: vertical; /* 设置或检索伸缩盒对象的子元素的排列方式 */
            -webkit-line-clamp: 2; /* 限制显示的文本行数 */
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
          width: 30px;
          height: 30px;
          background: #e8e8e8;
          border-radius: 4px;
          display: flex;
          align-items: center;
          justify-content: center;
          .more-btn {
            font-size: 18px;
          }
        }
      }
    }
  }
}
</style>