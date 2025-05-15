<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      
      <el-form-item label="知识库名称" prop="name" label-width="auto">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入知识库名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:knowledge:add']"
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
          v-hasPermi="['system:knowledge:edit']"
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
          v-hasPermi="['system:knowledge:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:knowledge:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row> -->
    <el-row :gutter="10" class="list mb8" type="flex">
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4">
        <div class="card add" @click="handleAdd">
          <div class="title">创建知识库</div>
          <div class="content">
            <svg-icon icon-class="edit" />
            创建空白知识库
          </div>
        </div>
      </el-col>
      <el-col :xs="10" :sm="8" :md="6" :lg="6" :xl="4" v-for="(item) in knowledgeList" :key="item.id">
        <div class="card edit" @click.stop="goTo(item)">
          <div class="content">
            <div class="img">
              <svg-icon v-if="!item.cover" icon-class="documentation" />
              <img v-else :src="item.src" alt="" />
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
                  <el-dropdown-item icon="el-icon-edit" command="handleUpdate">编辑此知识库</el-dropdown-item>
                  <el-dropdown-item icon="el-icon-delete" command="handleDelete">删除此知识库</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <!-- <el-table v-loading="loading" :data="knowledgeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="向量数据库ID" align="center" prop="embedStoreId" />
      <el-table-column label="向量模型ID" align="center" prop="embedModelId" />
      <el-table-column label="知识库名称" align="center" prop="name" />
      <el-table-column label="描述" align="center" prop="des" />
      <el-table-column label="封面" align="center" prop="cover" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:knowledge:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:knowledge:remove']"
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

    <!-- 添加或修改知识库对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="知识库名称" prop="name" required :rules="[{required: true, message: '请输入应用名称', trigger: 'blur'}]">
          <el-input v-model="form.name" type="text" maxlength="20" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="向量知识库" prop="embedStoreId" required :rules="[{required: true, message: '请选择向量知识库', trigger: 'blur'}]">
          <el-select v-model="form.embedStoreId" remote :loading="librariesLoading" :remote-method="librariesRemoteMethod" filterable placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="item in libraries || []"
              :key="item.id"
              :label="item.name"
              :value="item.id + ''">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="向量模型" prop="embedModelId" required :rules="[{required: true, message: '请选择向量模型', trigger: 'blur'}]">
          <el-select v-model="form.embedModelId" remote :loading="librariesLoading" :remote-method="librariesRemoteMethod" filterable placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="item in modelList || []"
              :key="item.id"
              :label="item.name"
              :value="item.id + ''">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="知识库图标" prop="cover">
          <ImageUpload v-model="form.cover" :limit="1"/>
        </el-form-item>
        <el-form-item label="链接地址" prop="knowledgeUrl" required :rules="[{required: true, message: '请输入链接地址', trigger: 'blur'}, {validator: (rule, value, callback) => {
          /^http/.test(value) ? callback() : callback('请输入http链接地址');
        }, message: '请输入http链接地址', trigger: 'blur'}]">
          <el-input v-model="form.knowledgeUrl" type="text" maxlength="300" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="知识库描述" prop="des">
          <el-input v-model="form.des" type="textarea" rows="3" maxlength="300" placeholder="请输入描述" />
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
import { listKnowledge, getKnowledge, delKnowledge, addKnowledge, updateKnowledge, getVectorLibrary, getModels } from "./knowledge";

export default {
  name: "Knowledge",
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
        getModels(str).then(res => {
          console.log(111, res)
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
      this.getLibrariesTimer && clearTimeout(this.getLibrariesTimer);
      this.getLibrariesTimer = setTimeout(() => {
        this.librariesLoading = true;
        getVectorLibrary(str).then(res => {
          console.log(111, res)
          this.libraries = res.data;
        }).catch(() => {
          this.libraries = [];
        }).finally(() => {
          this.librariesLoading = false;
        });
      }, immediate ? 0 : 800);

      
    },
    /** 查询知识库列表 */
    getList() {
      this.loading = true;
      listKnowledge(this.queryParams).then(response => {
        this.knowledgeList = response.rows.map(item => {
          return {
            ...item,
            src: `${process.env.VUE_APP_BASE_API}${item.cover}`
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
      this.title = "添加知识库";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getKnowledge(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改知识库";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateKnowledge(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addKnowledge(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除知识库编号为"' + ids + '"的数据项？').then(function() {
        return delKnowledge(ids);
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

<style lang="scss" scoped>
.app-container {
  .list {
    flex-wrap: wrap;
    .el-col {
      margin-top: 10px;
    }
  }
  .card {
    border: 1px solid #DCDFE6;
    border-radius: 8px;
    padding: 10px 14px;
    box-sizing: border-box;
    height: 140px;
    cursor: pointer;
    transition: all 0.3s;
    &:hover {
      box-shadow: 0px 0px 4px 4px #f1f1f1;
    }
    &.add {
      background-color: #F3F4F6;
      
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
        }
        .context {
          margin-left: 14px;
          .title {
            font-size: 16px;
            color: #374151;
            font-weight: bold;
          }
          .desc {
            margin-top: 8px;
            font-size: 14px;
            color: #6B7280;
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
