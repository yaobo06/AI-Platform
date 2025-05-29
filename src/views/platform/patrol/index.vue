<template>
  <div class="patrol-container">
    <div class="patrol-left">
        <div class="patrol-left-top">
            <div class="patrol-left-bottom-title">
              事件总数: {{total}}
           </div>
           <div class="patrol-left-bottom-chart" ref="patrolStatus"></div>
        </div>
        <div class="patrol-left-bottom">
           <div class="patrol-left-bottom-title">
              状态分布: <span>未处理</span>
              <el-switch
                v-model="queryParams.statusCode"
                active-color="#13ce66"
                inactive-color="#ff4949"
                active-value="CLOSED" 
                inactive-value="OPEN"
                @change="statusChanged">
                </el-switch>
                <span>已处理</span>
           </div>
           <div class="patrol-left-bottom-chart" ref="patrolType"></div>
        </div>
    </div>
    <div class="patrol-right">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
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
            <el-form-item label="推送人" prop="createdBy">
                <el-input
                v-model="queryParams.createdBy"
                placeholder="请输入推送人"
                clearable
                @keyup.enter.native="handleQuery"
                />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>
        <el-table v-loading="loading" :data="logList"  height="465">
            <el-table-column label="事件名称" align="center" prop="eventName" width="120"/>
            <el-table-column label="分类" align="center" prop="eventCategory" />
            <el-table-column label="检测时间" align="center" prop="creationDate" width="180">
                <template slot-scope="scope">
                <span>{{ parseTime(scope.row.creationDate, '{y}-{m}-{d}   {h}:{i}:{s}') }}</span>
                </template>
            </el-table-column>
            <el-table-column label="地址" align="center" prop="eventAddr"/>
            <el-table-column label="推送人" align="center" :show-overflow-tooltip="true" prop="reciverUserIds" />
            <el-table-column label="处理状态" align="center" prop="statusCode">
                <template slot-scope="scope">
                <span>{{ getStatusText(scope.row.statusCode) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="查看" align="center" prop="imgUrl">
                <template slot-scope="scope">
                <img v-if="scope.row.imgUrl" :src="scope.row.imgUrl" alt="" width=25 height=25 @click="showBigImg(scope.row.imgUrl)">
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
    </div>
  </div>
</template>
<script>
import { listLog, eventGroupLog, addrGroupLog, getLog} from "@/api/platform/log";
import * as echarts from "echarts";
export default {
  name: "patrol",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 安防事件详情表格数据
      logList: [],
      eventStatus:false,
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
        value: '已处理'
      },{
        key: 'OPEN',
        value: '未处理'
      }],
      typeChart: null,
      statusChart: null,
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
    this.getList()
    this.getEventList()
    this.getAddrList()
  },
  mounted(){
    this.initChartAddr()
    this.initEchartType()
  },
  methods: {
    /** 查询安防事件详情列表 */
    getList() {
      this.loading = true;
      listLog(this.queryParams).then(response => {
        this.logList = response.rows
        this.total = response.total
        this.loading = false
      });
    },
    /** 查询安防事件详情列表 */
    getEventList() {
      this.loading = true;
      eventGroupLog(this.queryParams).then(response => {
        if(response.rows && response.rows.length > 0){
            let echartTypeData = []
            response.rows.forEach(element => {
                echartTypeData.push({
                    name: element.eventName,
                    value: element.count
                })
            });
            this.initEchartType(echartTypeData)
        }
      });
    },
    /** 查询安防事件详情列表 */
    getAddrList() {
      this.loading = true;
      addrGroupLog(this.queryParams).then(response => {
        if(response.rows && response.rows.length > 0){
            let echartAddrData = []
            response.rows.forEach((element, rowNum) => {
                if(rowNum < 10){
                    echartAddrData.push({
                        groupId: element.eventAddr,
                        value: element.count
                    })
                }
            });
            this.initChartAddr(echartAddrData)
        }

      });
    },
    initChartAddr(data, xAxisData){
        this.statusChart = echarts.init(this.$refs.patrolStatus, "macarons");
        xAxisData = []
        let yAxisData = []
        data.forEach(element => {
            xAxisData.push(element.groupId)
            yAxisData.push(element.value)
        })
        this.statusChart.setOption({
            tooltip: {
                trigger: "item",
                formatter: "{b} : {c}",
            },
            animationDurationUpdate: 500,
            xAxis: {
                type: 'category',
                data: xAxisData,
                axisLabel: {
                    rotate: 40  // 设置标签旋转度
                }
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                data: yAxisData,
                type: 'bar',
                universalTransition: {
                  enabled: true,
                  divideShape: 'clone'
                },
                label: {
                    show: true,
                    position: 'top',
                    formatter: function (params) {
                        return params.value;
                    },
                    textStyle: {
                        color: '#666666',
                        fontWeight: 'bold'
                    }
                }
            }]
        })
    },
    initEchartType(data){
        this.typeChart = echarts.init(this.$refs.patrolType, "macarons");
        this.typeChart.setOption({
            tooltip: {
                trigger: "item",
                formatter: "{a} <br/>{b} : {c} ({d}%)",
            },
            tooltip: {
                trigger: 'item'
            },
            series: [{
                name: "命令",
                type: "pie",
                radius: '50%',
                data:  data,
                animationEasing: "cubicInOut",
                animationDuration: 1000,
                label: {
                    show: true, // 显示标签
                    formatter: '{b}: {c}' // 标签格式，{b}是名称，{c}是值，{d}%是百分比
                }
            }]
        });
    },
    statusChanged(){
        console.info(this.queryParams.statusCode)
        this.handleQuery()
    },
    // 取消按钮
    cancel() {

    },
    // 表单重置
    reset() {

    },
    /** 搜索按钮操作 */
    handleQuery() {
        this.getList();
        this.getEventList()
        this.getAddrList()

    },
    /** 重置按钮操作 */
    resetQuery() {

    },
    /** 新增按钮操作 */
    handleConfirm() {

    },
    /** 修改按钮操作 */
    handleUpdate(row) {

    },
    /** 提交按钮 */
    submitForm() {

    },
    /** 删除按钮操作 */
    handleDelete(row) {

    },
    /** 导出按钮操作 */
    handleExport() {

    },
    getStatusText(status){
      let statusItem = this.statusList.filter(item => item.key == status)[0] || {}
      return statusItem.value

    },
    showBigImg(url){

    }
  }
};
</script>
<style lang="scss" scoped>
.patrol-container{
    height: calc(100% - 20px);
    width: calc(100% - 30px);
    margin: 8px 15px;
    background: #ffffff;
}
.patrol-left{
    width: calc(40% - 20px);
    height: 100%;
    float: left;
    margin-left: 12px;
}
.patrol-left-top{
    margin-top: 12px;
    height: calc(50% - 16px);
    width: 100%;
    padding: 12px 0px 0px 10px;
    border: 1px solid #e6ebf5;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}
.patrol-left-bottom{
    margin-top: 12px;
    height: calc(50% - 16px);
    width: 100%;
    padding: 12px 0px 0px 10px;
    border: 1px solid #e6ebf5;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}
.patrol-left-bottom-title{
    height: 20px;
    width: 100%;
    font-size: 17px;
    font-weight: 700;
    padding-left:6px;
    color: #464646;
}
.patrol-left-bottom-title span{
    font-size: 15px;
    font-weight: 600;
    margin-left: 6px;
    margin-right: 6px;
    color: #6e7079;
}
.patrol-left-bottom-chart{
    height:calc(100% - 20px);
    width: 100%;
}
.patrol-right{
    width: calc(60% - 16px);
    height: calc(100% - 20px);
    float: left;
    margin: 12px 0px 0px 8px;
    padding: 12px 0px 0px 10px;
    border: 1px solid #e6ebf5;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
}
</style>