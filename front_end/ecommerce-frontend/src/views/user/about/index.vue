<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <div class="about-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1>关于我们</h1>
          <p>了解我们的故事与愿景</p>
        </div>
        <BackToHomeButton size="small" type="info" />
      </div>
    </div>

    <div class="development-notice">
      <div class="notice-content">
        <div class="notice-icon">
          <el-icon :size="80">
            <InfoFilled />
          </el-icon>
        </div>
        <h2>页面建设中</h2>
        <p>关于我们页面正在精心制作中，很快就会与大家见面！</p>
        <div class="features-preview">
          <h3>即将展示的内容：</h3>
          <ul>
            <li>🏢 公司发展历程</li>
            <li>🎯 企业使命与愿景</li>
            <li>👥 核心团队介绍</li>
            <li>🏆 企业荣誉与资质</li>
            <li>📞 联系方式与地址</li>
            <li>🤝 合作伙伴展示</li>
          </ul>
        </div>
        <div class="temp-info">
          <h3>临时联系信息：</h3>
          <div class="contact-grid">
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <span>客服热线：400-123-4567</span>
            </div>
            <div class="contact-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：service@shopping.com</span>
            </div>
            <div class="contact-item">
              <el-icon><Location /></el-icon>
              <span>地址：北京市朝阳区科技园</span>
            </div>
            <div class="contact-item">
              <el-icon><Clock /></el-icon>
              <span>服务时间：9:00-18:00</span>
            </div>
          </div>
        </div>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><House /></el-icon>
            返回首页
          </el-button>
          <el-button type="success" size="large" @click="goProducts">
            <el-icon><ShoppingBag /></el-icon>
            开始购物
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { InfoFilled, House, ShoppingBag, Phone, Message, Location, Clock } from '@element-plus/icons-vue'
import AppNavbar from '@/components/common/AppNavbar.vue'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const navbarRef = ref()

const goHome = () => {
  router.push('/')
}

const goProducts = () => {
  router.push('/products')
}
</script>

<style scoped>
.about-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.page-header {
  margin-bottom: 40px;
  padding: 40px 30px;
  background: linear-gradient(135deg, #409eff, #67c23a);
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  text-align: center;
  flex: 1;
}

.page-header h1 {
  font-size: 32px;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.page-header p {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.development-notice {
  background: white;
  border-radius: 12px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.notice-content {
  max-width: 700px;
  margin: 0 auto;
}

.notice-icon {
  margin-bottom: 30px;
  color: #409eff;
}

.development-notice h2 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 15px 0;
}

.development-notice > p {
  font-size: 16px;
  color: #606266;
  margin: 0 0 40px 0;
  line-height: 1.6;
}

.features-preview {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 30px;
  margin: 40px 0;
  text-align: left;
}

.features-preview h3 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 20px 0;
  text-align: center;
}

.features-preview ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.features-preview li {
  padding: 8px 0;
  font-size: 15px;
  color: #606266;
  border-bottom: 1px solid #ebeef5;
}

.features-preview li:last-child {
  border-bottom: none;
}

.temp-info {
  background: #e8f4fd;
  border-radius: 8px;
  padding: 30px;
  margin: 40px 0;
  text-align: left;
}

.temp-info h3 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 20px 0;
  text-align: center;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  color: #606266;
}

.contact-item .el-icon {
  color: #409eff;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 40px;
}

.action-buttons .el-button {
  padding: 12px 30px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .about-page {
    padding: 15px;
  }
  
  .page-header {
    padding: 30px 20px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .development-notice {
    padding: 40px 20px;
  }
  
  .development-notice h2 {
    font-size: 24px;
  }
  
  .contact-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-buttons .el-button {
    width: 200px;
  }
}
</style> 