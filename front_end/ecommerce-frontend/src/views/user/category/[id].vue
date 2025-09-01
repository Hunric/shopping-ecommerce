<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <div class="category-detail">
    <!-- 面包屑导航 -->
    <AppBreadcrumb :items="breadcrumbItems" />

    <!-- 分类信息 -->
    <div v-if="categoryInfo" class="category-header">
      <div class="category-icon">
        <el-icon :size="64">
          <component :is="getCategoryIcon(categoryInfo.categoryName)" />
        </el-icon>
      </div>
      <div class="category-info">
        <h1>{{ categoryInfo.categoryName }}</h1>
        <p>{{ categoryInfo.categoryDescription || '暂无描述' }}</p>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-section">
      <div class="section-header">
        <h2>商品列表</h2>
        <div class="sort-options">
          <el-select v-model="sortBy" @change="loadProducts" placeholder="排序方式">
            <el-option label="默认排序" value="default" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
            <el-option label="销量优先" value="sales_desc" />
          </el-select>
        </div>
      </div>

      <div v-if="loading" class="loading">
        <el-skeleton :rows="4" animated />
      </div>

      <div v-else-if="products.length === 0" class="empty-state">
        <el-empty description="该分类下暂无商品">
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </el-empty>
      </div>

      <div v-else class="products-grid">
        <div 
          v-for="product in products" 
          :key="product.spuId"
          class="product-card"
          @click="navigateToProduct(product.spuId)"
        >
          <div class="product-image">
            <img 
              :src="product.productImage || getDefaultProductImage()" 
              :alt="product.spuName"
              @error="handleImageError"
            />
            <div class="product-overlay">
              <el-button 
                type="primary" 
                circle 
                @click.stop="addToCart(product)"
                class="add-cart-btn"
              >
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.spuName }}</h3>
            <p class="product-store">{{ product.storeName }}</p>
            <div class="product-price">
              <span class="current-price">¥{{ (product.displayPrice || 0).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Monitor, House, Football, Reading, Grid, Plus } from '@element-plus/icons-vue'
import { getCategoryList, getProductsByCategory, type Category, type Product } from '@/api/user/product'
import { addToCart as addToCartApi, type CartAddDTO } from '@/api/user/cart'
import { useUserAuthStore } from '@/store/modules/userAuth'
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppBreadcrumb from '@/components/common/AppBreadcrumb.vue'

const router = useRouter()
const route = useRoute()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const categoryInfo = ref<Category | null>(null)
const products = ref<Product[]>([])
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const navbarRef = ref()

// 计算属性
const categoryId = computed(() => Number(route.params.id))

// 面包屑数据
const breadcrumbItems = computed(() => [
  { label: '商品分类', path: '/categories', clickable: true },
  { label: categoryInfo.value?.categoryName || '分类详情', clickable: false }
])

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

// 获取默认商品图片
const getDefaultProductImage = () => {
  return 'https://via.placeholder.com/300x300/f0f0f0/666?text=商品图片'
}

// 加载分类信息
const loadCategoryInfo = async () => {
  try {
    // 通过获取所有分类列表来找到对应的分类
    const response = await getCategoryList()
    if (response.data.success) {
      const categories = response.data.data
      const category = categories.find((cat: Category) => cat.categoryId === parseInt(categoryId.value))
      if (category) {
        categoryInfo.value = category
      } else {
        ElMessage.error('分类不存在')
        router.push('/categories')
      }
    } else {
      ElMessage.error('获取分类信息失败')
    }
  } catch (error) {
    console.error('获取分类信息失败:', error)
    ElMessage.error('获取分类信息失败')
  }
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await getProductsByCategory({
      categoryId: categoryId.value,
      page: currentPage.value,
      size: pageSize.value,
      sortBy: sortBy.value
    })
    
    if (response.data.success) {
      products.value = response.data.data.list || []
      total.value = response.data.data.total || 0
    } else {
      ElMessage.error('获取商品列表失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  loadProducts()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  loadProducts()
}

// 导航方法
const goHome = () => {
  router.push('/')
}

const goCategories = () => {
  router.push('/categories')
}

const navigateToProduct = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 添加到购物车
const addToCart = async (product: Product) => {
  if (!userAuthStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }

  try {
    const cartItem: CartAddDTO = {
      skuId: product.spuId, // 暂时使用spuId作为skuId，实际项目中应该有SKU选择逻辑
      quantity: 1
    }
    
    const response = await addToCartApi(cartItem)
    if (response.success) {
      ElMessage.success('添加到购物车成功')
    } else {
      ElMessage.error(response.message || '添加到购物车失败')
    }
  } catch (error) {
    console.error('添加到购物车失败:', error)
    ElMessage.error('添加到购物车失败')
  }
}

// 图片错误处理
const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement
  target.src = getDefaultProductImage()
}

// 组件挂载时加载数据
onMounted(() => {
  if (categoryId.value) {
    loadCategoryInfo()
    loadProducts()
  } else {
    ElMessage.error('分类ID无效')
    router.push('/categories')
  }
})
</script>

<style scoped>
.category-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.category-header {
  background: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.category-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #67c23a);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.category-info h1 {
  font-size: 32px;
  color: #303133;
  margin: 0 0 10px 0;
}

.category-info p {
  font-size: 16px;
  color: #606266;
  margin: 0;
  line-height: 1.5;
}

.products-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.section-header h2 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.loading {
  padding: 40px 0;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.product-image {
  position: relative;
  width: 100%;
  height: 280px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.add-cart-btn {
  transform: scale(0.8);
  transition: transform 0.3s;
}

.product-card:hover .add-cart-btn {
  transform: scale(1);
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-store {
  font-size: 14px;
  color: #909399;
  margin: 0 0 12px 0;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-price {
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}
</style> 