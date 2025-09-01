<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <div class="sale-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1>促销活动</h1>
          <p>精选优惠，限时特价</p>
        </div>
        <BackToHomeButton size="small" type="info" />
      </div>
    </div>

    <div class="development-notice">
      <div class="notice-content">
        <div class="notice-icon">
          <el-icon :size="80">
            <Tools />
          </el-icon>
        </div>
        <h2>功能开发中</h2>
        <p>促销活动页面正在紧张开发中，敬请期待！</p>
        <div class="features-preview">
          <h3>即将上线的功能：</h3>
          <ul>
            <li>🎉 限时秒杀活动</li>
            <li>💰 满减优惠券</li>
            <li>🛍️ 品牌特卖会</li>
            <li>🎁 新用户专享优惠</li>
            <li>📱 每日签到奖励</li>
            <li>🔥 热门商品推荐</li>
          </ul>
        </div>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon><House /></el-icon>
            返回首页
          </el-button>
          <el-button type="success" size="large" @click="goProducts">
            <el-icon><ShoppingBag /></el-icon>
            浏览商品
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Tools, House, ShoppingBag } from '@element-plus/icons-vue'
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
.sale-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.page-header {
  margin-bottom: 40px;
  padding: 40px 30px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
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
  max-width: 600px;
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
  .sale-page {
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
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .action-buttons .el-button {
    width: 200px;
  }
}
</style> 