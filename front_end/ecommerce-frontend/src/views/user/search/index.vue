<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="search-header">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品"
          size="large"
          @keyup.enter="performSearch"
          class="search-input"
        >
          <template #append>
            <el-button @click="performSearch" type="primary">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      
      <div v-if="searchKeyword" class="search-info">
        <span>搜索结果：{{ searchKeyword }}</span>
        <span class="result-count">共找到 {{ total }} 件商品</span>
      </div>
    </div>

    <!-- 筛选和排序 -->
    <div class="filter-bar">
      <div class="filter-options">
        <el-select v-model="selectedCategory" @change="performSearch" placeholder="选择分类" clearable>
          <el-option label="全部分类" value="" />
          <el-option 
            v-for="category in categories" 
            :key="category.categoryId"
            :label="category.categoryName" 
            :value="category.categoryId" 
          />
        </el-select>
        
        <el-select v-model="priceRange" @change="performSearch" placeholder="价格区间" clearable>
          <el-option label="全部价格" value="" />
          <el-option label="0-100元" value="0-100" />
          <el-option label="100-500元" value="100-500" />
          <el-option label="500-1000元" value="500-1000" />
          <el-option label="1000元以上" value="1000-" />
        </el-select>
      </div>
      
      <div class="sort-options">
        <el-select v-model="sortBy" @change="performSearch" placeholder="排序方式">
          <el-option label="综合排序" value="default" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
          <el-option label="销量优先" value="sales_desc" />
        </el-select>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results">
      <div v-if="loading" class="loading">
        <el-skeleton :rows="4" animated />
      </div>

      <div v-else-if="products.length === 0 && hasSearched" class="empty-state">
        <el-empty description="没有找到相关商品">
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </el-empty>
      </div>

      <div v-else-if="!hasSearched" class="no-search">
        <div class="no-search-content">
          <h2>请输入关键词搜索商品</h2>
          <p>您可以搜索商品名称、品牌等关键词</p>
        </div>
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
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { searchProducts, getCategoryList, type Product, type Category } from '@/api/user/product'
import { addToCart as addToCartApi, type CartAddDTO } from '@/api/user/cart'
import { useUserAuthStore } from '@/store/modules/userAuth'
import AppNavbar from '@/components/common/AppNavbar.vue'

const router = useRouter()
const route = useRoute()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const hasSearched = ref(false)
const searchKeyword = ref('')
const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const selectedCategory = ref('')
const priceRange = ref('')
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const navbarRef = ref()

// 获取默认商品图片
const getDefaultProductImage = () => {
  return 'https://via.placeholder.com/300x300/f0f0f0/666?text=商品图片'
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await getCategoryList()
    if (response.data.success) {
      categories.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 执行搜索
const performSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  hasSearched.value = true
  
  try {
    const searchParams = {
      keyword: searchKeyword.value.trim(),
      categoryId: selectedCategory.value || undefined,
      priceRange: priceRange.value || undefined,
      sortBy: sortBy.value,
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await searchProducts(searchParams)
    
    if (response.data.success) {
      products.value = response.data.data.list || []
      total.value = response.data.data.total || 0
      
      // 更新URL参数
      const query: any = { q: searchKeyword.value }
      if (selectedCategory.value) query.category = selectedCategory.value
      if (priceRange.value) query.price = priceRange.value
      if (sortBy.value !== 'default') query.sort = sortBy.value
      if (currentPage.value > 1) query.page = currentPage.value
      
      router.replace({ query })
    } else {
      ElMessage.error('搜索失败')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize
  currentPage.value = 1
  performSearch()
}

const handleCurrentChange = (newPage: number) => {
  currentPage.value = newPage
  performSearch()
}

// 导航方法
const goHome = () => {
  router.push('/')
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

// 监听路由变化
watch(() => route.query, (newQuery) => {
  if (newQuery.q) {
    searchKeyword.value = newQuery.q as string
    selectedCategory.value = (newQuery.category as string) || ''
    priceRange.value = (newQuery.price as string) || ''
    sortBy.value = (newQuery.sort as string) || 'default'
    currentPage.value = Number(newQuery.page) || 1
    
    if (hasSearched.value) {
      performSearch()
    }
  }
}, { immediate: true })

// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
  
  // 如果URL中有搜索参数，自动执行搜索
  if (route.query.q) {
    performSearch()
  }
})
</script>

<style scoped>
.search-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.search-header {
  background: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.search-bar {
  max-width: 600px;
  margin: 0 auto 20px;
}

.search-input :deep(.el-input-group__append) {
  padding: 0;
}

.search-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.result-count {
  color: #409eff;
  font-weight: 500;
}

.filter-bar {
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.filter-options {
  display: flex;
  gap: 15px;
}

.search-results {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading {
  padding: 40px 0;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.no-search {
  padding: 80px 0;
  text-align: center;
}

.no-search-content h2 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 15px 0;
}

.no-search-content p {
  font-size: 16px;
  color: #606266;
  margin: 0;
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