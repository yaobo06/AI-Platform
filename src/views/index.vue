<template>
  <div class="home-contianer">
    <div class="home-top">
      <div  class="home-top-item">
        <img src="../assets/images/deepseek-r1.png">
      </div>
      <div  class="home-top-item">
        <img src="../assets/images/writting-assistant.png">
      </div>
      <div  class="home-top-item">
        <img src="../assets/images/ernie-bot.png">
      </div>
    </div>
    <div class="home-middle">
      <div class="home-middle-item" v-for="item in 5">
        <div class="home-middle-item-left">
          <div class="home-middle-item-title">
            DeepSeek-R1
          </div>
          <div class="home-middle-item-context">
            随时随地 问问小艺
          </div>
        </div>
        <div class="home-middle-item-right">
          <img src="../assets/images/deepseek-icon.png">
        </div>
      </div>
    </div>
    <div class="home-bottom">
      <div class="function-pannel">
        <div class="function-pannel-title">
          <div class="function-pannel-title-left">
            <span>热门精选</span>
          </div>
          <div class="function-pannel-title-right">
            <span>更多 &gt;</span>
          </div>
        </div>
        <div class="function-pannel-type">
          <div class="function-pannel-type-label active-label">热门</div>
          <div class="function-pannel-type-label">最新</div>
          <div class="function-pannel-type-label">职场</div>
          <div class="function-pannel-type-label">创作</div>
          <div class="function-pannel-type-label">编程</div>
          <div class="function-pannel-type-label">绘画</div>
          <div class="function-pannel-type-label">学习</div>
          <div class="function-pannel-type-label">招聘</div>
          <div class="function-pannel-type-label">娱乐</div>
        </div>
        <div class="function-pannel-content">
          <div class="pannel-item-row" v-for="item in 3">
            <div class="pannel-item" v-for="item in 4">
              <div class="pannel-item-left">
                <img src="../assets/images/content-icon.png">
              </div>
              <div class="pannel-item-right">
                <div class="pannel-item-title">文案大师</div>
                <div class="pannel-item-content">
                  <img src="../assets/images/flame.png">
                  17.3万 | 为你解答各种编辑问题
                </div>
                <div class="pannel-item-content">@讯飞星火</div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
  
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.8.9"
    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    }
  }
};
</script>

<style scoped lang="scss">
.home-contianer{
  height: 100%;
  width: 100%;
  background: #f5f5f5;
}

.home-top{
  height: 220px;
  width: 100%;
}

.home-top-item{
  height: 100%;
  width: calc(33% - 25px);
  float: left;
  margin-left: 22px;
  
}

.home-top-item img{
  height: 100%;
  width: 100%;
  border-radius: 8px;
}

.home-middle {
  height: 80px;
  width: calc(100% - 20px);
  margin: 10px 15px 10px 10px;
}

.home-middle-item{
  height: 100%;
  margin-left: 16px;
  width: calc(20% - 19px);
  float: left;
  background: #fff;
  padding: 10px 5px;
  border-radius: 8px;
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

.home-middle-item-title{
  font-size: 18px;
  font-weight: 700;
  height: 50%;
  padding-top: 14px;
}

.home-middle-item-context{
  font-size: 12px;
  font-weight: 400;
  height: 50%;
  color: #999999;
  margin-top: 5px;
}

.home-bottom{
  height: calc(100% - 320px);
  width: calc(100% - 50px);
  margin: 10px 15px 10px 25px;
}

.function-pannel{
  background: #ffffff;
  width: 100%;
  height: 320px;
  border-radius: 8px;
}

.function-pannel-title{
  width: 100%;
  height: 50px;
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
  width: 25%;
  height: 70px;
  float: left;
}
.pannel-item-left{
  width: 80px;
  height: 100%;
  float: left;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中，如果需要的话 */
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
}

.pannel-item-content img{
  margin-top: 2px;
}
</style>

