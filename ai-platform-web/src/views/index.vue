<template>
  <div class="ai-home-container">
<!--    <header class="ai-header">
      <img src="../assets/images/homa-logo.png" class="ai-logo" alt="AI Logo" />
      <div class="ai-title">AI Platform</div>
      <div class="ai-slogan">Empowering the Future with Artificial Intelligence</div>
    </header>
    <section class="ai-carousel-section">
      <el-carousel height="340px" indicator-position="outside" arrow="always" class="ai-carousel">
        <el-carousel-item v-for="(img, idx) in carouselImages" :key="idx">
          <img :src="img.src" :alt="img.alt" class="ai-carousel-img" />
        </el-carousel-item>
      </el-carousel>
    </section>
    <section class="ai-models-section">
      <div class="ai-models-title">AI 应用模型</div>
      <div class="ai-models-panel">
        <div class="ai-model-card" v-for="(model, index) in modelList" :key="index" @click.stop="goTo(model)">
          <div class="ai-model-img">
            <img :src="model.src" alt="" />
          </div>
          <div class="ai-model-info">
            <div class="ai-model-name">{{ model.name }}</div>
            <div class="ai-model-desc" :title="model.des">{{ model.des }}</div>
          </div>
        </div>
      </div>
    </section>-->
  </div>
</template>

<!--<script>
import { listModel } from "@/views/AIApplication/models/model";

export default {
  name: "home",
  data() {
    return {
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
      modelList: [],
      carouselImages: [
        { src: require("@/assets/images/black-globe.png"), alt: "AI Globe" },
        { src: require("@/assets/images/black-grid.png"), alt: "AI Grid" },
        { src: require("@/assets/images/black-corridor.png"), alt: "AI Corridor" },
        { src: require("@/assets/images/black-virtual.png"), alt: "AI Virtual" },
        { src: require("@/assets/images/black-watch.png"), alt: "AI Watch" },
      ],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      listModel(this.queryParams).then((response) => {
        this.modelList = response.rows.map((item) => {
          return {
            ...item,
            src: `${process.env.VUE_APP_BASE_API}${item.imageUrl}`,
          };
        });
      });
    },
    goTo({ baseUrl }) {
      if (!baseUrl) {
        this.$modal.msgError("链接地址为空");
        return;
      }
      window.open(baseUrl);
    },
  },
};
</script>-->

<style scoped lang="scss">
.ai-home-container {
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #181c2a 0%, #232946 100%);
  color: #fff;
  font-family: 'Segoe UI', 'Roboto', 'Arial', sans-serif;
  overflow-x: hidden;
}
.ai-header {
  width: 100vw;
  padding: 48px 0 24px 0;
  text-align: center;
  background: transparent;
}
.ai-logo {
  height: 60px;
  margin-bottom: 12px;
}
.ai-title {
  font-size: 3.2rem;
  font-weight: 800;
  letter-spacing: 2px;
  margin-bottom: 10px;
  color: #f7c873;
  text-shadow: 0 4px 24px #000a, 0 1px 0 #fff2;
}
.ai-slogan {
  font-size: 1.4rem;
  color: #b3b8c5;
  margin-bottom: 0;
  letter-spacing: 1px;
}
.ai-carousel-section {
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 32px auto;
}
.ai-carousel {
  width: 820px;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 8px 32px 0 #0006;
  background: #232946;
}
.ai-carousel-img {
  width: 100%;
  height: 340px;
  object-fit: cover;
  background: #181c2a;
  border-radius: 18px;
}
.ai-models-section {
  width: 100vw;
  padding: 0 0 48px 0;
  margin-top: 0;
}
.ai-models-title {
  font-size: 2rem;
  font-weight: 700;
  color: #f7c873;
  text-align: center;
  margin-bottom: 32px;
  letter-spacing: 1px;
}
.ai-models-panel {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 32px;
}
.ai-model-card {
  width: 320px;
  min-height: 140px;
  background: rgba(36, 40, 60, 0.95);
  border-radius: 14px;
  box-shadow: 0 2px 16px 0 #0003;
  display: flex;
  align-items: center;
  padding: 24px 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #232946;
}
.ai-model-card:hover {
  transform: translateY(-6px) scale(1.03);
  box-shadow: 0 8px 32px 0 #0006;
  border: 1px solid #f7c873;
}
.ai-model-img {
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 18px;
}
.ai-model-img img {
  width: 60px;
  height: 60px;
  object-fit: contain;
  border-radius: 10px;
  background: #232946;
}
.ai-model-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.ai-model-name {
  font-size: 1.2rem;
  font-weight: 600;
  color: #fff;
  margin-bottom: 8px;
}
.ai-model-desc {
  font-size: 1rem;
  color: #b3b8c5;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

// 移动端适应
@media (max-width: 768px) {
  .ai-home-container {
    padding: 0;
  }

  .ai-header {
    padding: 24px 15px 16px 15px;
    
    .ai-logo {
      height: 40px;
    }
    
    .ai-title {
      font-size: 2rem;
      margin-bottom: 8px;
    }
    
    .ai-slogan {
      font-size: 1rem;
    }
  }

  .ai-carousel-section {
    margin: 0 auto 20px auto;
    padding: 0 15px;
    
    .ai-carousel {
      width: 100%;
      max-width: 100%;
      
      .ai-carousel-img {
        height: 200px;
      }
    }
  }

  .ai-models-section {
    padding: 0 15px 32px 15px;
    
    .ai-models-title {
      font-size: 1.5rem;
      margin-bottom: 20px;
    }
    
    .ai-models-panel {
      gap: 15px;
      
      .ai-model-card {
        width: 100%;
        min-height: 120px;
        padding: 16px;
        flex-direction: column;
        text-align: center;
        
        .ai-model-img {
          margin-right: 0;
          margin-bottom: 12px;
          
          img {
            width: 50px;
            height: 50px;
          }
        }
        
        .ai-model-info {
          .ai-model-name {
            font-size: 1rem;
            margin-bottom: 6px;
          }
          
          .ai-model-desc {
            font-size: 0.9rem;
            -webkit-line-clamp: 3;
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .ai-home-container {
    .ai-header {
      .ai-title {
        font-size: 1.8rem;
      }
      
      .ai-slogan {
        font-size: 0.9rem;
      }
    }
    
    .ai-carousel-section {
      .ai-carousel {
        .ai-carousel-img {
          height: 150px;
        }
      }
    }
    
    .ai-models-section {
      .ai-model-card {
        min-height: 100px;
        padding: 12px;
        
        .ai-model-img img {
          width: 40px;
          height: 40px;
        }
        
        .ai-model-info {
          .ai-model-name {
            font-size: 0.9rem;
          }
          
          .ai-model-desc {
            font-size: 0.8rem;
          }
        }
      }
    }
  }
}
</style>

