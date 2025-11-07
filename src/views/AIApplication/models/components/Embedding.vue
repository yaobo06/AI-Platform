<template>
  <div class="cm-embedding-container">
    <div class="nav-list-box">
      <div class="title">Embedding模型列表</div>
      <div class="nav-list">
        <div 
          :class="{'nav-item': true, 'active': item.value === activeNav}"
          v-for="item in navList"
          :key="item.value"
          @click="activeNav = item.value"
        >
          {{ item.label }}
        </div>
      </div>
    </div>
    <div class="main">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd"
            v-hasPermi="['system:model:add']">新增模型</el-button>
        </el-col>
        <!-- <el-col :span="1.5">
          <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate"
            v-hasPermi="['system:model:edit']">修改</el-button>
        </el-col> -->
        <!-- <el-col :span="1.5">
          <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete"
            v-hasPermi="['system:model:remove']">删除</el-button>
        </el-col> -->
        <!-- <el-col :span="1.5">
          <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport"
            v-hasPermi="['system:model:export']">导出</el-button>
        </el-col> -->
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="modelList" @selection-change="handleSelectionChange" border>
        <el-table-column type="selection" width="55" align="center" />
        <!-- <el-table-column label="主键" align="center" prop="id" />
        <el-table-column label="类型: CHAT、Embedding、Image" align="center" prop="type" /> -->
        <el-table-column label="模型别名" align="center" prop="name" fixed />
        <el-table-column label="模型版本" align="center" prop="model" />
        <!-- <el-table-column label="供应商" align="center" prop="provider" /> -->
        <el-table-column label="向量纬度" align="center" prop="dimension" />
        <el-table-column label="apiKey" align="center" prop="apiKey" />
        <el-table-column label="baseUrl" align="center" prop="baseUrl" />
        <!-- <el-table-column label="secretKey" align="center" prop="secretKey" />
        <el-table-column label="回复上限" align="center" prop="responseLimit" />
        <el-table-column label="生成随机性" align="center" prop="temperature" />
        <el-table-column label="topP" align="center" prop="topP" /> -->
        <!-- <el-table-column label="endpoint" align="center" prop="endpoint" />
        <el-table-column label="azure模型参数" align="center" prop="azureDeploymentName" />
        <el-table-column label="gemini模型参数" align="center" prop="geminiProject" />
        <el-table-column label="gemini模型参数" align="center" prop="geminiLocation" />
        <el-table-column label="图片大小" align="center" prop="imageSize" />
        <el-table-column label="图片质量" align="center" prop="imageQuality" />
        <el-table-column label="图片风格" align="center" prop="imageStyle" />
        <el-table-column label="向量维数" align="center" prop="dimension" /> -->
        <el-table-column label="操作" align="center" width="140" fixed="right" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              v-hasPermi="['system:model:edit']" title="修改"></el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)"
              v-hasPermi="['system:model:remove']" title="删除"></el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize"
        @pagination="getList" />
    </div>
    <!-- 添加或修改LLM模型配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模型别名" prop="name" required :rules="[{required: true, message: '请输入模型别名', trigger: 'blur'}]">
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="模型版本" prop="model" required :rules="[{required: true, message: '请选择模型版本', trigger: 'blur'}]">
          <!-- <el-input v-model="form.model" placeholder="请输入" /> -->
          <el-select v-model="form.model" filterable placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="item in activeNavItem.list || []"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="供应商" prop="provider">
          <el-input v-model="form.provider" placeholder="请输入" />
        </el-form-item> -->
        
        
        <el-form-item label="向量纬度" prop="dimension" required :rules="[{required: true, message: '请选择向量纬度', trigger: 'blur'}]">
          <el-select v-model="form.dimension" filterable placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="item in dimensionList || []"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="apiKey" prop="apiKey" required :rules="[{required: true, message: '请输入apiKey', trigger: 'blur'}]">
          <el-input v-model="form.apiKey" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="baseUrl" prop="baseUrl">
          <el-input v-model="form.baseUrl" placeholder="请输入" />
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
import { listModel, getModel, delModel, addModel, updateModel } from "../model";
export default {
  name: "EmbeddingModel",
  props: {
    type: {}
  },
  data() {
    const navList = [
        {
          label: "OpenAI",
          value: "OpenAI",
          list: [
            {
              label: "text-embedding-3-small",
              value: "text-embedding-3-small",
            },
            {
              label: "text-embedding-3-large",
              value: "text-embedding-3-large",
            },
            {
              label: "text-embedding-ada-002",
              value: "text-embedding-ada-002",
            },

          ]
        },
        {
          label: "百度千帆",
          value: "BDQF",
          list: [
            {
              label: "bge-large-zh",
              value: "bge-large-zh",
            },
            {
              label: "bge-large-en",
              value: "bge-large-en",
            },
            {
              label: "tao-8k",
              value: "tao-8k",
            },
          ]
        },
        {
          label: "阿里百炼",
          value: "ALBL",
          list: [
            {
              label: "text-embedding-v3",
              value: "text-embedding-v3",
            },
          ]
        },
        {
          label: "智谱清言",
          value: "ZPQY",
          list: [
            {
              label: "embedding-2",
              value: "embedding-2",
            },
            {
              label: "embedding-3",
              value: "embedding-3",
            },
          ]
        },
        
        {
          label: "抖音豆包",
          value: "DYDB",
          list: [
            {
              label: "text-240715",
              value: "text-240715",
            },
            {
              label: "text-240515",
              value: "text-240515",
            },
          ]
        },
        
      ]
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
      // LLM模型配置表格数据
      modelList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        // type: null,
        model: null,
        // provider: null,
        name: null,
        responseLimit: null,
        temperature: null,
        topP: null,
        apiKey: null,
        baseUrl: null,
        secretKey: null,
        endpoint: null,
        azureDeploymentName: null,
        geminiProject: null,
        geminiLocation: null,
        imageSize: null,
        imageQuality: null,
        imageStyle: null,
        dimension: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      navList,
      activeNav: navList?.[0]?.value,
      dimensionList: [
        {
          label: "1024",
          value: 1024
        },
        {
          label: "1536",
          value: 1536
        },
      ]
    };
  },
  computed: {
    activeNavItem() {
      return this.navList.find(item => item.value === this.activeNav);
    }
  },
  watch: {
    activeNav(val) {
      this.handleQuery();
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询LLM模型配置列表 */
    getList() {
      this.loading = true;
      this.modelList = [];
      listModel({...this.queryParams, provider: this.activeNav, type: this.type}).then(response => {
        this.modelList = response.rows;
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
        // type: null,
        model: null,
        // provider: null,
        name: null,
        responseLimit: null,
        temperature: null,
        topP: null,
        apiKey: null,
        baseUrl: null,
        secretKey: null,
        endpoint: null,
        azureDeploymentName: null,
        geminiProject: null,
        geminiLocation: null,
        imageSize: null,
        imageQuality: null,
        imageStyle: null,
        dimension: 1024
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
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getModel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改";
      });
    },
    /** 提交按钮 */
    submitForm() {
      // console.log(this.activeNav)
      this.$refs["form"].validate(valid => {
        if (valid) {
          const params = {
            ...this.form,
            provider: this.activeNav,
            type: this.type
          }
          if (this.form.id != null) {
            updateModel(params).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addModel(params).then(response => {
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
      this.$modal.confirm('是否确认删除模型配置编号为"' + ids + '"的数据项？').then(function () {
        return delModel(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => { });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/model/export', {
        ...this.queryParams
      }, `model_${new Date().getTime()}.xlsx`)
    }
  }
};

</script>

<style lang="scss" scoped>
.cm-embedding-container {
  display: flex;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  .nav-list-box {
    padding: 20px;
    width: 200px;
    height: 100%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    & > .title {
      font-size: 16px;
      font-weight: bold;
      text-align: left;
    }
    .nav-list {
      flex-grow: 1;
      height: 0px;
      margin-top: 20px;
      display: flex;
      flex-direction: column;
      overflow-y: auto;
      .nav-item {
        padding: 8px 10px;
        text-align: left;
        margin: 4px 0px;
        cursor: pointer;
        transition: all 0.3s;
        &:hover {
          background-color: #f5f5f5;
        }
        &.active {
          font-weight: bold;
          color: #f1883d;
          border-left: 2px solid #f1883d;
        }
      }
    }
    
  }
  .main {
    flex-grow: 1;
    width: 0px;
  }
}
</style>