<template>
  <div class="home-contianer">
    <div class="home-top">
      <div class="homa-logo left">
        <img src="../assets/images/homa-logo.png">
      </div>
      <div class="homa-top-right">
        
      </div>
    </div>
    <div class="home-middle">
      <div class="homa-detail">
        🎉 homa模型更新，思考更深，推理更强，在网页端、APP 和 API 全面上线。
      </div>
      <div class="homa-middle-logo">
         <div>Homa AI</div>
      </div>
      <div class="homa-description">
        <div>探索未至之境</div>
      </div>
      <div class="homa-panel">
        <div  class="homa-application" v-for="(model, index) in modelList" :key="index" @click.stop="goTo(model)">
          <div class="homa-application-img left">
            <img :src="model.src" alt="" style="height: 80px">
          </div>
          <div class="homa-application-text right">
            <div class="homa-application-title">{{ model.name }}</div>
            <div class="homa-application-content" :title="model.des">{{ model.des }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listModel } from "@/views/AIApplication/models/model";

export default {
  name: "home",
  data() {
    return {
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
      // 向量模型选项
      modelList: [],
 
    };
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      listModel(this.queryParams).then(response => {
        this.modelList = response.rows.map(item => {
          return {
            ...item,
           //src: `${process.env.VUE_APP_BASE_API}${item.imageUrl}`
           src: `http://192.168.16.67/prod-api/${item.imageUrl}`
          }
        });
      });
    },
    goTo({baseUrl}) {
      if(!baseUrl) {
        this.$modal.msgError('链接地址为空');
        return;
      }
      window.open(baseUrl);
    },
  
  }
};
</script>

<style scoped lang="scss">
.home-contianer {
  height: 100%;
  width: 100%;
  background-image: url('../assets/images/blue-sky.webp');
  background-size: cover;
  background-position: center;
  padding: 8px 20px;

}

.home-top {
  height: 80px;
  width: 100%;
  padding: 12px 2px;
}

.left {
  float: left;
}

.right {
  float: right;
}

.homa-logo{
  height: 100%;
  width: 50%;
}

.home-middle {
  height: calc(100% - 160px);
  width: 100%;
  padding-top: 25px;
}

.homa-detail {
  text-align: center;
  color: #94abd0;
  font-size: 14px;
}

.homa-middle-logo {
  width: 100%;
  height: 160px;
  display: flex; 
  justify-content: center;
  padding-top: 60px;
}

.homa-middle-logo div{
  height: 80px;
  width: 500px;
  // background-image: url('../assets/images/logo.png');
  // background-repeat: no-repeat;
  // background-size: cover;
  text-align: center;
  color: #eb6112;
  font-size: 80px;
  font-weight: 700;
}

.homa-description {
  width: 100%;
  height: 50px;
  display: flex; 
  justify-content: center;
  font-size: 26px;
  color: rgb(71 85 105 / var(--tw-text-opacity, 1));
  font-size: 1.875rem;
  font-weight: 700;
}

.homa-panel {
  margin-top: 50px;
  width: 100%;
  height: 125px;
  display: flex; 
  justify-content: center;
}

.homa-application{
  width: 360px;
  height: 100%;
  background-color: hsla(0, 0%, 100%, .5);
  margin-left: 40px;
  border-radius: 8px;
  padding: 12px 12px;
  cursor: pointer;
  box-shadow: 0 0 0 1px #f1f5f9, 0 2px 4px rgba(0, 0, 0, .05), 0 12px 24px rgba(0, 0, 0, .05);
}

.homa-application:nth-child(1){
  margin-left: 0px;
}

.homa-application-img{
  width: 80px;
  height: 100%;
  display: flex; 
  justify-content: center;
  align-items: center;
}

.homa-application-img img{
  height: 80px;
}


.homa-application-text{
  width: calc(100% - 90px);
  height: 100%;
  margin-left: 10px
}


.homa-application-title{
  height: 40%;
  width: 100%;
  color: #4a4a4a;
  font-size: 18px;
  line-height: 40px;
  font-weight: 700;
}
.homa-application-content{
  height: 60%;
  width: 100%;
  font-size: 14px;
  color: #8b8b8b;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

