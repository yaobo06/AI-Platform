<template>
  <div class="home-contianer">
    <div class="app-container-box">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模型名称" prop="name" label-width="auto">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入模型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="list mb8" type="flex">
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4">
        <div class="card add" @click="handleAdd">
          <div class="content">
            <svg-icon icon-class="edit" />
            创建空白模型
          </div>
        </div>
      </el-col>
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4" v-for="(item) in knowledgeList" :key="item.id">
        <div class="card edit" @click.stop="goTo(item)">
          <div class="content">
            <div class="img">
              <svg-icon v-if="!item.imageUrl" icon-class="documentation" />
              <img v-else :src="item.src" alt="" style="height: 100%;width: 100%;"/>
            </div>
            <div class="context">
              <div class="title">{{ item.name }}</div>
              <div class="desc">{{ item.des }}</div>
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
                  <el-dropdown-item icon="el-icon-edit" command="handleUpdate">编辑此模型</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-delete" command="handleDelete">删除此模型</el-dropdown-item>
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

    <!-- 添加或修改知识库对话框 -->
     <!-- 添加或修改LLM模型配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body class="model-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模型别名" prop="name" style="width: 50%; float:left;" required :rules="[{required: true, message: '请输入模型别名', trigger: 'blur'}]">
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="模型版本" prop="model" style="width: 50%; float:left;" required :rules="[{required: true, message: '请输入模型版本', trigger: 'blur'}]">
          <el-input v-model="form.model" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="apiKey" prop="apiKey" style="width: 50%; float:left;">
          <el-input v-model="form.apiKey" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="secretKey" prop="secretKey" style="width: 50%; float:left;">
          <el-input v-model="form.secretKey" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="baseUrl" prop="baseUrl" style="width: 50%; float:left;">
          <el-input v-model="form.baseUrl" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="应用封面" prop="cover" style="width: 50%; float:left;">
          <ImageUpload v-model="form.imageUrl" :limit="1"/>
        </el-form-item>
        <el-form-item label="回复上限" prop="responseLimit" style="width: 50%; float:left;">
          <el-slider v-model="form.responseLimit" :min="1" :max="8192"></el-slider>
        </el-form-item>
        <el-form-item label="生成随机性" prop="temperature" style="width: 50%; float:left;">
          <el-slider v-model="form.temperature" :min="0" :max="2" :step="0.05"></el-slider>
        </el-form-item>
        <el-form-item label="topP" prop="topP" style="width: 50%; float:left;">
          <el-slider v-model="form.topP" :min="0" :max="1" :step="0.1"></el-slider>
        </el-form-item>
        <el-form-item label="应用描述" prop="des" style="clear:both">
          <el-input v-model="form.des" type="textarea" rows="3" maxlength="300" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>

  </div>

</template>

<script>
import { listModel, getModel, delModel, updateModel, addModel} from "@/views/AIApplication/models/model";

export default {
  name: "home",
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
      // 知识库表格数据
      knowledgeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        embedStoreId: null,
        embedModelId: null,
        name: null,
        des: null,
        cover: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      // 向量数据库选项
      libraries: [],
      getLibrariesTimer: null,
      librariesLoading: false,
      // 向量模型选项
      modelList: [],
      getModelsTimer: null,
      modelsLoading: false,
    };
  },
  watch: {
    open(n) {
      if(!n) return;
      this.librariesRemoteMethod();
      this.modelsRemoteMethod();
    }
  },
  created() {
    this.getList();
    this.librariesRemoteMethod();
    this.modelsRemoteMethod();
  },
  methods: {
    goTo({knowledgeUrl}) {
      if(!knowledgeUrl) {
        this.$modal.msgError('链接地址为空');
        return;
      }
      window.open(knowledgeUrl);
    },
    // 获取向量模型
    modelsRemoteMethod(str, immediate = false) {
      this.getModelsTimer && clearTimeout(this.getModelsTimer);
      this.getModelsTimer = setTimeout(() => {
        this.modelsLoading = true;
        getModel(str).then(res => {
          this.modelList = res.data;
        }).catch(() => {
          this.modelList = [];
        }).finally(() => {
          this.modelsLoading = false;
        });
      }, immediate ? 0 : 800);

      
    },
    // 获取向量知识库
    librariesRemoteMethod(str, immediate = false) {
      // this.getLibrariesTimer && clearTimeout(this.getLibrariesTimer);
      // this.getLibrariesTimer = setTimeout(() => {
      //   this.librariesLoading = true;
      //   getVectorLibrary(str).then(res => {
      //     this.libraries = res.data;
      //   }).catch(() => {
      //     this.libraries = [];
      //   }).finally(() => {
      //     this.librariesLoading = false;
      //   });
      // }, immediate ? 0 : 800);
    },
    /** 查询知识库列表 */
    getList() {
      this.loading = true;
      listModel(this.queryParams).then(response => {
        this.knowledgeList = response.rows.map(item => {
          return {
            ...item,
           src: `${process.env.VUE_APP_BASE_API}${item.imageUrl}`
          }
        });
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
        userId: null,
        embedStoreId: null,
        embedModelId: null,
        name: null,
        des: null,
        cover: null,
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
      this.title = "添加模型";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getModel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "编辑模型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateModel(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addModel(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除模型编号为"' + ids + '"的数据项？').then(function() {
        return delModel(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/knowledge/export', {
        ...this.queryParams
      }, `knowledge_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped lang="scss">
.pagination-container {
  background: #f4f4f4;
}

.el-button--text{
  color: #7a7a7a;
}

.home-contianer ::v-deep .el-pagination.is-background .btn-prev{
  background: #ffffff;
}
.home-contianer::v-deep .el-pagination.is-background .btn-next{
  background: #ffffff;
}

.home-contianer ::v-deep .el-upload--picture-card{
  width: 60px;
  height: 60px;
  line-height: 65px;
  background-color: #f6f6f6;
  border: 1px dashed #eeeeee;
  vertical-align: middle;
}

.model-dialog ::v-deep .el-upload-list--picture-card .el-upload-list__item{
  width: 35px;
  height: 35px;
  line-height: 65px;
  background-color: #f6f6f6;
  border: 1px dashed #eeeeee;
  vertical-align: middle;
}
.model-dialog ::v-deep .el-upload--picture-card{
  width: 35px;
  height: 35px;
  line-height: 40px;
  background-color: #f6f6f6;
  border: 1px dashed #eeeeee;
  vertical-align: middle;
}
.model-dialog ::v-deep .el-upload-list--picture-card .el-upload-list__item{
  width: 35px;
  height: 35px;
  line-height: 40px;
  background-color: #f6f6f6;
  border: 1px dashed #eeeeee;
  vertical-align: middle;
}

.model-dialog ::v-deep .el-slider__bar{
  background-color: #ea6111;
}

.model-dialog ::v-deep .el-upload__tip{
  display: none;
}

.model-dialog ::v-deep .el-dialog__body{
  padding: 20px 20px;
}

.model-dialog ::v-deep  .el-button--primary{
  background-color: #ea6111;
  border-color: #ea6111;
}

.home-contianer ::v-deep .el-dialog__title{
  line-height: 24px;
  font-size: 18px;
  color: #3d3d3d;
  font-weight: 700;
}

.app-container-box{
  background: #f4f4f4;
  margin: 10px 18px;
  width: calc(100% - 36px);
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
    height: 140px;
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
        }
        .context {
          margin-left: 14px;
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

