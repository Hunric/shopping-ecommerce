<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <div class="navbar-test-page">
    <!-- 面包屑导航示例 -->
    <AppBreadcrumb :items="breadcrumbItems" />
    
    <div class="test-content">
      <h1>导航栏设计测试页面</h1>
      
      <div class="design-showcase">
        <div class="section">
          <h2>统一导航栏特性</h2>
          <ul>
            <li>✅ 响应式设计，适配移动端和桌面端</li>
            <li>✅ 统一的Logo和品牌标识</li>
            <li>✅ 集成搜索功能，支持全站搜索</li>
            <li>✅ 购物车图标和数量显示</li>
            <li>✅ 用户登录状态和下拉菜单</li>
            <li>✅ 粘性定位，滚动时保持在顶部</li>
          </ul>
        </div>
        
        <div class="section">
          <h2>面包屑导航特性</h2>
          <ul>
            <li>✅ 统一的样式和交互</li>
            <li>✅ 支持可点击和不可点击项目</li>
            <li>✅ 自动包含"首页"链接</li>
            <li>✅ 响应式设计</li>
          </ul>
        </div>
        
        <div class="section">
          <h2>页面布局规范</h2>
          <ul>
            <li>✅ 最大宽度1200px，居中对齐</li>
            <li>✅ 统一的内边距和外边距</li>
            <li>✅ 背景色和阴影效果</li>
            <li>✅ 最小高度计算（100vh - 70px）</li>
          </ul>
        </div>
        
        <div class="section">
          <h2>已应用统一导航栏的页面</h2>
          <div class="page-links">
            <el-button @click="testPage('/')" type="primary">首页</el-button>
            <el-button @click="testPage('/cart')" type="success">购物车</el-button>
            <el-button @click="testPage('/search')" type="info">搜索页面</el-button>
            <el-button @click="testPage('/products')" type="warning">商品列表</el-button>
            <el-button @click="testPage('/categories')" type="danger">分类列表</el-button>
          </div>
        </div>
        
        <div class="section">
          <h2>设计系统颜色</h2>
          <div class="color-palette">
            <div class="color-item">
              <div class="color-box primary"></div>
              <span>主色调 #409eff</span>
            </div>
            <div class="color-item">
              <div class="color-box success"></div>
              <span>成功色 #67c23a</span>
            </div>
            <div class="color-item">
              <div class="color-box warning"></div>
              <span>警告色 #e6a23c</span>
            </div>
            <div class="color-item">
              <div class="color-box danger"></div>
              <span>危险色 #f56c6c</span>
            </div>
            <div class="color-item">
              <div class="color-box info"></div>
              <span>信息色 #909399</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppBreadcrumb from '@/components/common/AppBreadcrumb.vue'

const router = useRouter()
const navbarRef = ref()

// 面包屑数据
const breadcrumbItems = computed(() => [
  { label: '测试页面', path: '/test', clickable: true },
  { label: '导航栏设计测试', clickable: false }
])

const testPage = (path: string) => {
  router.push(path)
}
</script>

<style scoped>
.navbar-test-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.test-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.test-content h1 {
  text-align: center;
  color: #303133;
  margin-bottom: 40px;
  font-size: 32px;
}

.design-showcase {
  display: grid;
  gap: 30px;
}

.section {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.section h2 {
  color: #409eff;
  margin-bottom: 15px;
  font-size: 20px;
}

.section ul {
  margin: 0;
  padding-left: 20px;
}

.section li {
  margin-bottom: 8px;
  color: #606266;
}

.page-links {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.color-palette {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.color-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-box {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.color-box.primary {
  background: #409eff;
}

.color-box.success {
  background: #67c23a;
}

.color-box.warning {
  background: #e6a23c;
}

.color-box.danger {
  background: #f56c6c;
}

.color-box.info {
  background: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-test-page {
    padding: 15px;
  }
  
  .test-content {
    padding: 20px;
  }
  
  .test-content h1 {
    font-size: 24px;
  }
  
  .page-links {
    justify-content: center;
  }
  
  .color-palette {
    grid-template-columns: 1fr;
  }
}
</style> 