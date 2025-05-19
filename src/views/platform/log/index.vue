<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="摄像头" prop="equipment">
        <el-input
          v-model="queryParams.equipment"
          placeholder="请输入摄像头名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="事件名称" prop="eventName">
        <el-input
          v-model="queryParams.eventName"
          placeholder="请输入事件名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="检测时间" prop="creationDate">
        <el-date-picker clearable
          v-model="queryParams.creationDateRange"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          :default-time="['00:00:00', '23:59:59']"
          align="right">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="地址" prop="eventAddr">
        <el-input
          v-model="queryParams.eventAddr"
          placeholder="请输入地点"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="lastUpdatedBy">
        <el-input
          v-model="queryParams.eventType"
          placeholder="请输入类型"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="推送人" prop="createdBy">
        <el-input
          v-model="queryParams.createdBy"
          placeholder="请输入推送人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="statusCode">
        <el-select clearable v-model="queryParams.statusCode" placeholder="请选择状态"
          @keyup.enter.native="handleQuery" style="width:100%">
					<el-option v-for="(item,index) of statusList" :key="index" :label="item.value" :value="String(item.key)">{{item.value}}</el-option>
				</el-select>
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
          size="mini"
          @click="handleConfirm"
          v-hasPermi="['system:log:add']"
        >批量确认</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:log:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="摄像头" align="center" prop="equipment" />
      <el-table-column label="事件名称" align="center" prop="eventName" />
      <el-table-column label="查看" align="center" prop="imgUrl">
        <template slot-scope="scope">
          <img v-if="scope.row.imgUrl" :src="scope.row.imgUrl" alt="" width=25 height=25 @click="showBigImg(scope.row.imgUrl)">
        </template>
      </el-table-column>
<!--      <el-table-column label="类型" align="center" prop="eventType" />-->
      <el-table-column label="分类" align="center" prop="eventCategory" />
      <el-table-column label="检测时间" align="center" prop="creationDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.creationDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="地址" align="center" prop="eventAddr"/>
      <el-table-column label="推送人" align="center" prop="reciverUserIds" />
      <el-table-column label="处理状态" align="center" prop="statusCode">
        <template slot-scope="scope">
          <span>{{ getStatusText(scope.row.statusCode) }}</span>
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

    <!-- 添加或修改安防事件详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="请求方" prop="requestApp">
          <el-input v-model="form.requestApp" placeholder="请输入请求方" />
        </el-form-item>
        <el-form-item label="请求IP" prop="requestAppIp">
          <el-input v-model="form.requestAppIp" placeholder="请输入请求IP" />
        </el-form-item>
        <el-form-item label="请求报文" prop="requestData">
          <el-input v-model="form.requestData" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态码" prop="statusCode">
          <el-input v-model="form.statusCode" placeholder="请输入状态码" />
        </el-form-item>
        <el-form-item label="工作通知发送状态" prop="workNoticeFlag">
          <el-input v-model="form.workNoticeFlag" placeholder="请输入工作通知发送状态" />
        </el-form-item>
        <el-form-item label="钉钉消息发送状态" prop="talkMsgFlag">
          <el-input v-model="form.talkMsgFlag" placeholder="请输入钉钉消息发送状态" />
        </el-form-item>
        <el-form-item label="工作通知发送的用户" prop="workNoticeUser">
          <el-input v-model="form.workNoticeUser" placeholder="请输入工作通知发送的用户" />
        </el-form-item>
        <el-form-item label="钉钉消息发送的用户" prop="talkMsgUser">
          <el-input v-model="form.talkMsgUser" placeholder="请输入钉钉消息发送的用户" />
        </el-form-item>
        <el-form-item label="事件名称" prop="eventName">
          <el-input v-model="form.eventName" placeholder="请输入事件名称" />
        </el-form-item>
        <el-form-item label="设备名称" prop="equipment">
          <el-input v-model="form.equipment" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="地点" prop="eventAddr">
          <el-input v-model="form.eventAddr" placeholder="请输入地点" />
        </el-form-item>
        <el-form-item label="状态说明" prop="statusMessage">
          <el-input v-model="form.statusMessage" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="创建人" prop="createdBy">
          <el-input v-model="form.createdBy" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="创建时间" prop="creationDate">
          <el-date-picker clearable
            v-model="form.creationDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="修改人" prop="lastUpdatedBy">
          <el-input v-model="form.lastUpdatedBy" placeholder="请输入修改人" />
        </el-form-item>
        <el-form-item label="最后修改时间" prop="lastUpdateDate">
          <el-date-picker clearable
            v-model="form.lastUpdateDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择最后修改时间">
          </el-date-picker>
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
import { listLog, getLog, delLog, addLog, updateLog, confirmLog} from "@/api/platform/log";
import Link from '../../../layout/components/Sidebar/Link.vue';

export default {
  components: { Link },
  name: "Log",
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
      // 安防事件详情表格数据
      logList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        requestApp: null,
        requestAppIp: null,
        requestData: null,
        statusCode: null,
        workNoticeFlag: null,
        talkMsgFlag: null,
        workNoticeUser: null,
        talkMsgUser: null,
        eventName: null,
        equipment: null,
        eventAddr: null,
        imgUrl: null,
        statusMessage: null,
        createdBy: null,
        creationDate: null,
        creationDateRange: null,
        lastUpdatedBy: null,
        lastUpdateDate: null
      },
      statusList: [{
        key: 'CLOSED',
        value: '关闭'
      },{
        key: 'OPEN',
        value: '开启'
      }],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      pickerOptions: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setHours(0,0,0,0)
              end.setHours(23,59,59,999)
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近7天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              start.setHours(0,0,0,0)
              end.setHours(23,59,59,999)
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近30天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              start.setHours(0,0,0,0)
              end.setHours(23,59,59,999)
              picker.$emit('pick', [start, end]);
            }
          }]
        }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询安防事件详情列表 */
    getList() {
      this.loading = true;
      listLog(this.queryParams).then(response => {
        this.logList = response.rows;
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
        requestApp: null,
        requestAppIp: null,
        requestData: null,
        statusCode: null,
        workNoticeFlag: null,
        talkMsgFlag: null,
        workNoticeUser: null,
        talkMsgUser: null,
        eventName: null,
        equipment: null,
        eventAddr: null,
        imgUrl: null,
        statusMessage: null,
        createdBy: null,
        creationDate: null,
        lastUpdatedBy: null,
        lastUpdateDate: null
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
    /** 新增按钮操作 */
    handleConfirm() {
      confirmLog(this.ids).then(response => {
        this.getList();
        this.$modal.msgSuccess("确认成功");
      })
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getLog(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改安防事件详情";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLog(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLog(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除安防事件详情编号为"' + ids + '"的数据项？').then(function() {
        return delLog(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/log/export', {
        ...this.queryParams
      }, `log_${new Date().getTime()}.xlsx`)
    },
    getStatusText(status){
      let statusItem = this.statusList.filter(item => item.key == status)[0] || {}
      return statusItem.value
    },
    showBigImg(url){
      if(!url){
        return;
      }
      window.open(url, '_blank')
    }
  }
};
</script>

<style lang="scss" scoped>
.document-link {
  cursor: pointer;
  color: #409eff;
  line-height: 26px;
}
</style>
