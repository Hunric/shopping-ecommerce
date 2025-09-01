<template>
  <div class="categories-page">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1>商品分类</h1>
          <p>浏览所有商品分类</p>
        </div>
        <BackToHomeButton size="small" type="info" />
      </div>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="4" animated />
    </div>

    <div v-else-if="categories.length === 0" class="empty-state">
      <el-empty description="暂无分类数据">
        <el-button type="primary" @click="loadCategories">重新加载</el-button>
      </el-empty>
    </div>

    <div v-else class="categories-grid">
      <div 
        v-for="category in categories" 
        :key="category.categoryId"
        class="category-card"
        @click="navigateToCategory(category.categoryId)"
      >
        <div class="category-icon">
          <el-icon :size="48">
            <component :is="getCategoryIcon(category.categoryName)" />
          </el-icon>
        </div>
        <h3 class="category-name">{{ category.categoryName }}</h3>
        <p class="category-desc">{{ category.categoryDescription || '暂无描述' }}</p>
        <div class="category-stats">
          <span>商品数量: {{ category.productCount || 0 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Monitor, House, Football, Reading, Grid } from '@element-plus/icons-vue'
import { getCategoryList, type Category } from '@/api/user/product'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const categories = ref<Category[]>([])

// 获取分类图标
const getCategoryIcon = (categoryName: string) => {
  const iconMap: { [key: string]: string } = {
    '服装': 'Grid',
    '数码': 'Monitor',
    '家居': 'House',
    '美妆': 'Grid',
    '运动': 'Football',
    '食品': 'Grid',
    '母婴': 'Grid',
    '图书': 'Reading',
    '汽车': 'Grid',
    '手机': 'Monitor'
  }
  return iconMap[categoryName] || 'Grid'
}

// 加载分类列表
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await getCategoryList()
    if (response.data.success) {
      categories.value = response.data.data || []
    } else {
      ElMessage.error(response.data.message || '获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

// 导航到分类详情
const navigateToCategory = (categoryId: number) => {
  router.push(`/category/${categoryId}`)
}

// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.categories-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  margin-bottom: 40px;
  padding: 40px 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  color: #303133;
  margin: 0 0 10px 0;
}

.page-header p {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.loading {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-state {
  background: white;
  border-radius: 12px;
  padding: 60px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.category-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.category-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #67c23a);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s;
}

.category-card:hover .category-icon {
  background: linear-gradient(135deg, #67c23a, #409eff);
  transform: scale(1.1);
}

.category-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px 0;
}

.category-desc {
  font-size: 14px;
  color: #606266;
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.category-stats {
  font-size: 12px;
  color: #909399;
}
</style> 