<template>
  <div class="patrol-container">
    <div class="patrol-left">
        <div class="patrol-left-top" ref="patrolStatus">

        </div>
        <div class="patrol-left-bottom" ref="patrolType">
            
        </div>
    </div>
    <div class="patrol-right">


    </div>
  </div>
</template>
<script>
import { listLog, getLog, delLog, addLog, updateLog, confirmLog} from "@/api/platform/log";
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
      typeChart: null,
      statusChart: null

    };
  },
  created() {
    this.getList()
  },
  mounted(){
    this.initChartStatus()
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
    initChartStatus(){
        this.statusChart = echarts.init(this.$refs.patrolStatus, "macarons");
        this.statusChart.setOption({
            xAxis: {
                data: ['未处理','已处理']
            },
            yAxis: {},
            dataGroupId: '',
            animationDurationUpdate: 500,
            series: {
                type: 'bar',
                id: 'sales',
                data: [
                {
                    value: 20000,
                    groupId: 'OPEN'
                },
                {
                    value: 7,
                    groupId: 'CLOSE'
                }],
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

            }
        })

    },
    initEchartType(){
        this.typeChart = echarts.init(this.$refs.patrolType, "macarons");
        this.typeChart.setOption({
            tooltip: {
                trigger: "item",
                formatter: "{a} <br/>{b} : {c} ({d}%)",
            },
            title: {
                text: '事件类型分布',
                left: 'center'
            },
            tooltip: {
                trigger: 'item'
            },
            series: [{
                name: "命令",
                type: "pie",
                radius: '50%',
                data:  [
                    { value: 203, name: '行为检测异常' },
                    { value: 162, name: '未戴安全帽越线' },
                    { value: 125, name: '穿越警戒线' },
                    { value: 998, name: '其他' }
                ],
                animationEasing: "cubicInOut",
                animationDuration: 1000,
                }
           ]
        });

    },
    // 取消按钮
    cancel() {

    },
    // 表单重置
    reset() {

    },
    /** 搜索按钮操作 */
    handleQuery() {

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

    },
    showBigImg(url){

    }
  }
};
</script>
<style lang="scss" scoped>
.patrol-container{
    height: 100%;
    width: 100%;
}
.patrol-left{
    width: 35%;
    height: 100%;
    float: left;
}
.patrol-left-top{
    height: 50%;
    width: 100%;
}
.patrol-left-bottom{
    height: 50%;
    width: 100%;
}
.patrol-right{
    width: calc(65% - 20px);
    height: 100%;
    float: left;
}
</style>