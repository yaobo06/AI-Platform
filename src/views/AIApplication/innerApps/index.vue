<template>
  <div class="home-contianer">
    <div class="home-middle">
      <div class="home-middle-title">
        <span>奥马应用</span>
      </div>
      <div class="home-middle-panel">
        <!-- <div class="home-middle-item" v-for="(item, index) in applicationNav" :key="index"  @click.stop="goTo(item)">
          <div class="home-middle-item-left">
            <div class="home-middle-item-title" :title="item.name">
              {{item.name}}
            </div>
            <div class="home-middle-item-context" :title="item.des">
              {{item.des}}
            </div>
          </div>
          <div class="home-middle-item-right" v-if="item.src">
            <svg-icon v-if="!item.cover" icon-class="documentation" />
            <img v-else :src="item.src" alt="" />
          </div>
        </div> -->
        <div class="pannel-item" v-for="(application, num) in applicationNav" :key="num" @click.stop="goTo(application)">
              <div class="pannel-item-left">
                <svg-icon v-if="!application.cover" icon-class="documentation" />
                <img v-else :src="application.src" alt="" />
              </div>
              <div class="pannel-item-right">
                <div class="pannel-item-title" :title="application.name" >{{application.name}}</div>
                <div class="pannel-item-content" :title="application.des" v-if="application.des"><img src="../../../assets/images/flame.png">
                <span>&nbsp;{{application.des}}s</span></div>
              </div>
            </div>
      </div>
    </div>
<!--    <div class="home-bottom">
      <div class="function-pannel">
        <div class="function-pannel-title">
          <div class="function-pannel-title-left">
            <span>热门精选</span>
          </div>
          <div class="function-pannel-title-right">
            &lt;!&ndash; <span>更多 &gt;</span> &ndash;&gt;
          </div>
        </div>
        <div class="function-pannel-content">
          <div class="pannel-item-row" v-for="(applicationRow, index) in applicationList" :key="index">
            <div class="pannel-item" v-for="(application, num) in applicationRow" :key="num" @click.stop="goTo(application)">
              <div class="pannel-item-left">
                <svg-icon v-if="!application.cover" icon-class="documentation" />
                <img v-else :src="application.src" alt="" />
              </div>
              <div class="pannel-item-right">
                <div class="pannel-item-title" :title="application.name" >{{application.name}}</div>
                <div class="pannel-item-content" :title="application.des" v-if="application.des"><img src="../../../assets/images/flame.png">
                <span>&nbsp;{{application.des}}s</span></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>-->
  </div>

</template>

<script>
import { listApp, getModels} from "@/views/AIApplication/applications/app";
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.9",
      applicationList: [],
      applicationNav: [],
      total: 0,
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
        type: null
      },
      topCards:[],
      carousel:[],
      rowNum: 4,
      applicationTypes: [{
        key: '0',
        label: '轮播',
        section: 'top'
      },{
        key: '1',
        label: '导航栏',
        section: 'mid'
      },{
        key: '2',
        label: '热门',
        section: 'buttom'
      }],
      activeType: '2',
      className: ''
    };
  },
  created() {
    this.getList()
    this.getApplicationNav()
    this.getApplicationTop()
  },
  mounted(){
   // this.getApplicationTop()
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
    selectType(key){
      this.activeType = key
      this.getList()
    },
    getTypeClass(key){
      if(key == this.activeType){
        return 'function-pannel-type-label active-label'
      }
      return 'function-pannel-type-label'
    },
    getList() {
      this.queryParams.type = this.activeType
      listApp(this.queryParams).then(response => {
        this.total = response.total;
        this.applicationList = this.formatResult(response.rows)
      });
    },
    getApplicationTop(){
      this.queryParams.type = '99'
      listApp(this.queryParams).then(response => {
        let responseData = response.rows || []
        responseData.forEach(item => {
          this.appendIconSrc(item)
        })
        this.topCards = responseData
      });
    },
    getApplicationNav(){
      this.queryParams.type = '99'
      listApp(this.queryParams).then(response => {
        this.total = response.total;
        let responseData = response.rows || []
        let length = responseData.length
        this.applicationNav = []
        for(let i = 0; i < 4; i++){
          if(i < length ){
            this.appendIconSrc(responseData[i])
            this.applicationNav.push(responseData[i])
          }else{
            this.applicationNav.push([])
          }
        }
      });
    },
    appendIconSrc(element){
      Object.assign(element, {
        'src': process.env.VUE_APP_BASE_API + element.cover
      })
    },
    formatResult(responseData){
      if(!responseData || responseData.length == 0){
          return []
      }
      let applicationGrid = []
      let applicationRow = []
      responseData.forEach(element => {
        this.appendIconSrc(element)
        if(applicationRow.length == this.rowNum){
          applicationGrid.push([...applicationRow])
          applicationRow = []
        }
        applicationRow.push(element)
      });
      if(applicationRow.length > 0){
        applicationGrid.push([...applicationRow])
      }
      return applicationGrid
    },
    goTo({appUrl}) {
      if(!appUrl) {
        this.$modal.msgError('链接地址为空');
        return;
      }
      window.open(appUrl);
    },
  }
};
</script>

<style scoped lang="scss">

.home-contianer{
  height: 100%;
  width: 100%;
  margin-top: 8px;
  background: #f5f5f5;
}

.home-top{
  height: 225px;
  width: calc(100% - 50px);
  margin: 0px 15px 10px 25px;
  background: #ffffff;
  border-radius: 8px;
}

.carousel {
  overflow: hidden; /* 隐藏溢出部分 */
  white-space: nowrap; /* 防止子元素换行 */
}
.carousel-inner {
  display: flex; /* 使用flex布局 */
  transition: transform 0.5s ease; /* 平滑过渡效果 */
}
.carousel-item {
  display: inline-block; /* 让项目横向排列 */
  width: 30%; /* 根据需要调整宽度 */
}

.home-middle {
  height: 140px;
  width: calc(100% - 50px);
  margin-left: 25px;
  margin-top: 16px;
  border-radius: 8px;
  background: #ffffff;
}

.home-middle-title{
  font-size: 20px;
  font-weight: 700;
  height: 48px;
  padding-top: 8px;
  line-height: 36px;
  width:  100%;
  padding-left: 16px;
  border-bottom: 1px solid #e6ebf5;
}

.home-middle-panel{
  margin-top: 16px;
  height: calc(100% - 56px);
  width: 100%;
}


.home-middle-item{
  height: 100%;
  margin-left: 16px;
  width: calc(25% - 19px);
  float: left;
  background: #fff;
  padding: 2px 5px;
  border-radius: 8px;
  cursor: pointer;
}

.home-middle-item-left{
  height: 100%;
  width: 75%;
  float: left;
  padding-left: 10px;
}

.home-middle-item-right{
  height: 100%;
  width: 25%;
  float: left;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: right; /* 水平居中，如果需要的话 */
  padding-right: 12px;
}

.home-middle-item-right img{
  width: 50px;
  height: 50px;
  border-radius: 8px;
}

.home-middle-item-title{
  font-size: 16px;
  font-weight: 700;
  height: 35%;
  width: 100%;
  padding-top: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}

.home-middle-item-context{
  font-size: 12px;
  font-weight: 400;
  height: 60%;
  color: #999999;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
}

.home-bottom{
  height: calc(100% - 205px);
  width: calc(100% - 50px);
  margin: 20px 15px 10px 25px;
}

.function-pannel{
  background: #ffffff;
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.function-pannel-title{
  width: 100%;
  height: 50px;
  border-bottom: 1px solid #e6ebf5;
}

.function-pannel-title-left{
  font-size: 20px;
  float: left;
  font-weight: 700;
  width: calc(90% - 50px);
  height: 100%;
  display: flex;
  align-items: center; /* 垂直居中 */
  padding-left: 16px;
}

.function-pannel-title-right{
  font-size: 14px;
  float: right;
  width: calc(10% + 10px);
  color: #999999;
  padding-top: 15px;
}

.function-pannel-title-right span{
  float: right;
  padding-right: 18px;
}

.function-pannel-type {
  height: 28px;
  width: 100%;
  padding-left: 16px;
  font-size: 14px;
  font-weight: 700;
  line-height: 28px;
}

.function-pannel-type-label{
  height: 100%;
  width: 60px;
  background: #f2f2f2;
  border-radius: 16px;
  float: left;
  margin-left: 12px;
  text-align: center;
  cursor: pointer;
}

.function-pannel-type-label:first-of-type {
  margin-left:0px;
}

.active-label {
  background-color: #0a59f7 !important;
  color: #ffffff;
}

.function-pannel-content {
  height: 270px;
  width: 100%;
}

.pannel-item-row {
  width: 100%;
  height: 80px;
  padding-top: 12px;
}

.pannel-item{
  width: calc(25% - 1px);
  height: 70px;
  float: left;
  cursor: pointer;
}
.pannel-item-left{
  width: 80px;
  height: 100%;
  float: left;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中，如果需要的话 */
}

.pannel-item-left img{
  height: 55px;
  width: 55px;
  border-radius: 12px;
}

.pannel-item-right{
  width: calc(100% - 80px);
  height:  100%;
  float: left;
}
.pannel-item-title{
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 2px;
}
.pannel-item-content{
  font-size: 14px;
  color: #939393;
  margin-top: 2px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.pannel-item-content img{
  margin-top: 1px;
}
::v-deep .el-carousel__indicators--outside{
  display: none
}

</style>

