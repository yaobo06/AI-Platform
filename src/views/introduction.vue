<template>
    <div class="container">
        <div class="introduction-brief" v-if="step == 'frist'">
            <div class="introduction-left">
                <div class="introduction-title">
                    {{ title }}
                </div>
                <div class="introduction-content">
                    {{ content }}
                </div>
                <div class="introduction-bottom">
                    <el-button size="big" @click="nextStep()">Contact US</el-button>
                </div>
            </div>
            <div class="introduction-right">
            </div>
        </div>
        <div class="introduction-list"  v-if="step == 'second'">
            <div class="list-above">
                <el-carousel indicator-position="outside">
                    <el-carousel-item v-for="(aboveItem, index) in aboveCarousel" :key="index">
                        <div v-for="(aboveElement, num) in aboveItem" :key="num" class="list-above-item" @click="nextStep()">
                            <img :src="aboveElement.imgUrl">
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
            <div class="list-bottom">
                <el-carousel indicator-position="outside">
                    <el-carousel-item v-for="(buttomItem, index) in bottomCarousel" :key="index">
                        <div v-for="(buttomElement, num) in buttomItem" :key="num" class="list-bottom-item" @click="nextStep()">
                            <img :src="buttomElement.imgUrl">
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </div>
        <div class="introduction-detail" v-if="step == 'thrid'">
            <div class="detail-above" @click="nextStep()">
                <div class="detail-above-text">
                    <div class="detail-above-title">
                        {{ detailTitle }}
                    </div>
                    <div class="detail-above-content">
                        {{ detailContent}}
                    </div>
                </div>
            </div>
            <div class="detail-bottom">
                <img src="../assets/images/black-mainboard.png">
            </div>
        </div>
    </div>
</template>

<script>
import { listApp, getModels} from "@/views/AIApplication/applications/app";
export default {
  name: "introduction",
  data() {
    return {
        step: 'frist',
        content: 'Discover cutting-edge Al applications designed to transform your business landscape. With our advanced solutions, we empower you to leverage technology for enhanced productivity and innovation. Join us in shaping the future of Al today!',
        title: 'Elevate Your AI Experience',
        detailTitle: 'mpowering innovation through advanced AI solutions.',
        detailContent: 'discover the future of technology today!',
        queryParams:{

        },
        aboveCards: [{
            imgUrl: require('@/assets/images/black-globe.png')
        },{
            imgUrl: require('@/assets/images/black-grid.png')
        }],
        bottomCards:[{
            imgUrl: require('@/assets/images/black-corridor.png')
        },{
            imgUrl: require('@/assets/images/black-virtual.png')
        },{
            imgUrl: require('@/assets/images/black-watch.png')
        }],
        aboveCarousel: [],
        bottomCarousel: [],
        settings:{
            //  dots: true,
            infinite: true,   //循环播放
            initialSlide: 1,  // 第几个图片
            speed: 500,
            slidesToShow: 1,
            slidesToScroll: 1,   // 几行图片
            swipeToSlide: true
      }
    };
  },
  created() {
    this.getIntroductionInfo()
    this.getAboveCarousel(this.aboveCards)
    this.getBottomCarousel(this.bottomCards)
  },
  methods: {
    getIntroductionInfo(){
      this.queryParams.type = '0'
      listApp(this.queryParams).then(response => {
        let responseData = response.rows 
        //this.title = responseData.title
        //this.content = responseData.content
      });
    },
    getBottomCarousel(cards){
      let length = cards.length
      this.bottomCarousel = []
      cards.forEach((item, index)=> {
        let carouselRow = []
        carouselRow.push({...item})
        let nextIndex = index + 1;
        if(nextIndex < length - 1){
          carouselRow.push({...cards[nextIndex]})
          carouselRow.push({...cards[nextIndex + 1]})
        }else if(nextIndex == length - 1){
          carouselRow.push({...cards[nextIndex]})
          carouselRow.push({...cards[0]})
        }else if(nextIndex > length - 1){
          carouselRow.push({...cards[0]})
          carouselRow.push({...cards[1]})
        }
        this.bottomCarousel.push(carouselRow)
      })
      console.info( this.bottomCarousel)
    },
    getAboveCarousel(cards){
      let length = cards.length
      this.aboveCarousel = []
      cards.forEach((item, index)=> {
        let carouselRow = []
        carouselRow.push({...item})
        let nextIndex = index + 1;
        if(nextIndex <= length - 1){
          carouselRow.push({...cards[nextIndex]})
        }if(nextIndex > length - 1){
          carouselRow.push({...cards[0]})
        }
        this.aboveCarousel.push(carouselRow)
      })
    },
    nextStep(){
        if(this.step == 'frist'){
            this.step = 'second'
        }else if(this.step == 'second'){
            this.step = 'thrid'
        } else if(this.step == 'thrid'){
            this.step = 'frist'
        }
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.container{
    height: 100%;
    width: 100%;
}
.introduction-brief{
    padding: 25px 10% 0px 10%;
    height: 100%;
    width: 100%;
}
.introduction-left{
    width: 50%;
    height: 100%;
    float: left;
    padding: 10px 25px;
}
.introduction-right{
    width: 50%;
    height: 100%;
    float: left;
    padding: 10px 25px;
    background-image: url('../assets/images/introduction.png');
    background-size: cover;
    background-position: center;
    border-radius: 8px;
}
.introduction-title{
    font-size: 86px;
    height: calc(50%  - 30px);
    width: 100%;
    font-family: 'Petit Formal Script';
}
.introduction-content{
    margin-top: 26px;
    font-size: 26px;
    height: auto;
    width: 100%;
    color: #20293b;
    line-height: 1.6;
}
.introduction-bottom{
    margin-top: 20px;
    width: 100%;
    height: 60px;
    display: flex;
    align-items: center;
}
.introduction-list{
    height: 100%;
    width: 100%;
    padding: 35px 10% 20px 10%;
}
.list-above{
    height: 55%;
    width: 100%;
}
.list-bottom{
    height: calc(45% - 12px);
    width: 100%;
    margin-top: 12px;
}
.list-above-item{
    width: calc(50% - 6px);
    height: 100%;
    float: left;
    margin-left: 12px;
    border-radius: 8px;
}
.list-above-item:nth-child(1){
    margin-left: 0px;
}
.list-above-item img{
    height: 100%;
    width: 100%;
    border-radius: 8px;
}
.list-bottom-item{
    width: calc(33.33% - 8px);
    height: 100%;
    float: left;
    margin-left: 12px;
    
}
.list-bottom-item:nth-child(1){
    margin-left: 0px;
}
.list-bottom-item img{
    width: 100%;
    height: 100%;
    border-radius: 8px;
}
.introduction-detail{
    width: 100%;
    height: 100%;
}
.detail-above{
    width: 100%;
    height: 55%;
    background: #777694;
    display: flex;
    justify-content: center;
    align-items: center;
}
.detail-above-text{
    height: 60%;
    width: 40%;
    color: #ffffff;
    text-align: center;
}
.detail-above-title{
    font-family: 'Petit Formal Script';
    font-size: 50px;
    height: calc(100% - 50px);

}
.detail-above-content{
    font-size: 22px;
    height: 50px;
    line-height: 50px;

}
.detail-bottom{
    width: 100%;
    height: 45%;
}
.detail-bottom img{
    width: 100%;
    height: 100%;
}
::v-deep .el-button{
    background: #4338ca;
    color: #ffffff;
    font-size: 20px;
}
::v-deep .el-carousel{
    height: 100%;
}
::v-deep .el-carousel__container{
    height: 100%;
}
::v-deep .el-carousel__indicators--outside{
    display: none;
}

.slick-slider {
  display: flex;
  align-items: center;
  border: 1px solid grey;
}

.slick-item {
  margin: 0 5px;
  background-color: #e5e5e5;
  aspect-ratio: 1.3/1;
  max-width: 280px;
}

.arrow-btn {
  border: none;
  img {
    height: 40px;
  }
}


</style>
