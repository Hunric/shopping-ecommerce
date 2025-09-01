<template>
  <div class="ecommerce-home">
    <!-- 顶部导航栏 -->
    <nav class="top-navbar">
      <div class="nav-container">
        <div class="nav-left">
          <div class="logo">
            <span class="logo-text">购物商城</span>
          </div>
        </div>
        
        <div class="nav-center">
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索商品、品牌、店铺"
              class="search-input"
              @keyup.enter="performSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
              <template #suffix>
                <el-button type="primary" @click="performSearch" class="search-btn">搜索</el-button>
              </template>
            </el-input>
          </div>
        </div>
        
        <div class="nav-right">
          <div class="nav-actions">
            <div class="cart-action" @click="navigateToCart">
              <el-icon><ShoppingCart /></el-icon>
              <span class="action-text">购物车</span>
              <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
            </div>
            
            <div v-if="userAuthStore.isLoggedIn" class="user-menu">
              <el-dropdown @command="handleUserCommand">
                <div class="user-info">
                  <el-icon><User /></el-icon>
                  <span class="user-name">{{ userName }}</span>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                    <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                    <el-dropdown-item command="address">收货地址</el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            
            <div v-else class="auth-actions">
              <el-button @click="navigateToLogin" text size="default" class="auth-btn">登录</el-button>
              <el-button @click="navigateToRegister" type="primary" size="default" class="auth-btn">注册</el-button>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 轮播图区域 -->
      <section class="banner-section">
        <div class="banner-container">
          <el-carousel height="400px" indicator-position="outside" arrow="hover">
            <el-carousel-item v-for="(banner, index) in banners" :key="index">
              <div class="banner-item" :style="{ backgroundImage: `url(${banner.image})` }">
                <div class="banner-content">
                  <h2 class="banner-title">{{ banner.title }}</h2>
                  <p class="banner-subtitle">{{ banner.subtitle }}</p>
                  <el-button type="primary" size="large" @click="banner.action">
                    {{ banner.buttonText }}
                  </el-button>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </section>

      <!-- 分类导航 -->
      <section class="category-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">商品分类</h2>
          </div>
          
          <div class="category-grid">
            <div v-if="categories.length === 0" class="empty-category">
              <el-empty description="暂无商品分类" :image-size="100">
                <el-button type="primary" @click="testLoadCategories">重新加载</el-button>
              </el-empty>
            </div>
            
            <div 
              v-else
              v-for="category in categories" 
              :key="category.categoryId"
              class="category-item"
              @click="navigateToCategory(category.categoryId)"
            >
              <div class="category-icon">
                <el-icon :size="32">
                  <component :is="getCategoryIcon(category.categoryName)" />
                </el-icon>
              </div>
              <span class="category-name">{{ category.categoryName }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 热门商品 -->
      <section class="hot-products-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">热门商品</h2>
            <el-button text @click="viewAllProducts">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
          </div>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="2" animated />
          </div>
          
          <div v-else-if="hotProducts.length === 0" class="empty-state">
            <el-empty description="暂无热门商品">
              <el-button type="primary" @click="testLoadHotProducts">重新加载</el-button>
            </el-empty>
          </div>
          
          <div v-else class="products-grid">
            <div 
              v-for="product in hotProducts" 
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
        </div>
      </section>

      <!-- 推荐商品 -->
      <section class="recommended-section">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">为您推荐</h2>
            <p class="section-subtitle">根据您的浏览记录为您精选</p>
          </div>
          
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="2" animated />
          </div>
          
          <div v-else-if="recommendedProducts.length === 0" class="empty-state">
            <el-empty description="暂无推荐商品">
              <el-button type="primary" @click="testLoadRecommendedProducts">重新加载</el-button>
            </el-empty>
          </div>
          
          <div v-else class="products-grid">
            <div 
              v-for="product in recommendedProducts" 
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
        </div>
      </section>


    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h4 class="footer-title">购物指南</h4>
            <ul class="footer-links">
              <li><a href="#">购物流程</a></li>
              <li><a href="#">支付方式</a></li>
              <li><a href="#">配送说明</a></li>
              <li><a href="#">退换货</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4 class="footer-title">客户服务</h4>
            <ul class="footer-links">
              <li><a href="#">联系我们</a></li>
              <li><a href="#">常见问题</a></li>
              <li><a href="#">意见反馈</a></li>
              <li><a href="#">投诉建议</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4 class="footer-title">关于我们</h4>
            <ul class="footer-links">
              <li><a href="#">公司介绍</a></li>
              <li><a href="#">招聘信息</a></li>
              <li><a href="#">合作伙伴</a></li>
              <li><a href="#">联系方式</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4 class="footer-title">关注我们</h4>
            <div class="social-links">
              <a href="#" class="social-link">微信</a>
              <a href="#" class="social-link">微博</a>
              <a href="#" class="social-link">QQ</a>
            </div>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p>&copy; 2024 购物商城. 版权所有</p>
        </div>
      </div>
    </footer>

    <!-- 调试面板 (开发环境) -->
    <div v-if="isDev" class="debug-panel">
      <h4>调试功能</h4>
      <div class="debug-buttons">
        <el-button @click="testLoadHotProducts" size="small">测试热门商品</el-button>
        <el-button @click="testLoadRecommendedProducts" size="small">测试推荐商品</el-button>
        <el-button @click="testLoadCategories" size="small">测试分类</el-button>
        <el-button @click="testLoadAllData" size="small">测试所有数据</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ShoppingCart, Search, User, Plus, ArrowRight,
  Monitor, House, Football, 
  Reading, Grid
} from '@element-plus/icons-vue'
import {
  getHotProducts,
  getRecommendedProducts,
  getCategoryList,
  type Product,
  type Category
} from '@/api/user/product'
import { addToCart as addToCartApi, type CartAddDTO } from '@/api/user/cart'
import { useUserAuthStore } from '@/store/modules/userAuth'
import { getCartCount } from '@/api/user/cart'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const hotProducts = ref<Product[]>([])
const recommendedProducts = ref<Product[]>([])
const categories = ref<Category[]>([])
const searchKeyword = ref('')
const cartCount = ref(0)

// 开发环境标识
const isDev = ref(process.env.NODE_ENV === 'development')

// 用户信息
const userName = computed(() => userAuthStore.userInfo?.username || '用户')

// 轮播图数据
const banners = ref([
  {
    image: 'https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1200&h=400&fit=crop',
    title: '新品上市',
    subtitle: '探索最新时尚潮流',
    buttonText: '立即购买',
    action: () => router.push('/products')
  },
  {
    image: 'https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=1200&h=400&fit=crop',
    title: '限时特惠',
    subtitle: '精选商品低至5折',
    buttonText: '查看优惠',
    action: () => router.push('/sale')
  },
  {
    image: 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1200&h=400&fit=crop',
    title: '品质保证',
    subtitle: '正品保障，放心购买',
    buttonText: '了解更多',
    action: () => router.push('/about')
  }
])



// 搜索功能
const performSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/search?q=${encodeURIComponent(searchKeyword.value)}`)
  }
}

// 获取分类图标
const getCategoryIcon = (categoryName: string) => {
  // 使用Element Plus图标作为分类图标
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

// 导航功能
const navigateToCart = () => {
  router.push('/cart')
}

const navigateToLogin = () => {
  router.push('/user/login')
}

const navigateToRegister = () => {
  router.push('/user/register')
}

const navigateToCategory = (categoryId: number) => {
  router.push(`/category/${categoryId}`)
}

const navigateToProduct = (productId: number) => {
  router.push(`/product/${productId}`)
}

const viewAllProducts = () => {
  router.push('/products')
}

// 用户菜单处理
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'address':
      router.push('/user/address')
      break
    case 'logout':
      userAuthStore.logout()
      ElMessage.success('退出登录成功')
      break
  }
}

// 加载热门商品
const loadHotProducts = async () => {
  try {
    console.log('=== 开始加载热门商品 ===')
    console.log('请求URL: /api/user/products/hot?limit=8')
    
    const response = await getHotProducts(8)
    console.log('热门商品API完整响应:', response)
    
    if (!response) {
      console.error('API响应为空')
      ElMessage.error('获取热门商品失败：服务器无响应')
      return
    }
    
    const apiResponse = response.data
    console.log('热门商品API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success && apiResponse.data) {
      hotProducts.value = apiResponse.data
      console.log('热门商品加载成功，数量:', hotProducts.value.length)
      if (hotProducts.value.length > 0) {
        console.log('第一个商品示例:', hotProducts.value[0])
      }
    } else {
      const errorMessage = apiResponse?.message || '获取热门商品失败'
      console.error('获取热门商品失败:', errorMessage)
      console.error('完整响应:', apiResponse)
      ElMessage.error(errorMessage)
    }
  } catch (error) {
    console.error('获取热门商品异常:', error)
    
    // 详细错误信息
    if (error.response) {
      console.error('HTTP错误状态:', error.response.status)
      console.error('HTTP错误信息:', error.response.statusText)
      console.error('错误响应数据:', error.response.data)
      ElMessage.error(`获取热门商品失败: HTTP ${error.response.status}`)
    } else if (error.request) {
      console.error('网络请求失败:', error.request)
      ElMessage.error('获取热门商品失败: 网络连接错误')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error(`获取热门商品失败: ${error.message}`)
    }
  }
}

// 加载推荐商品
const loadRecommendedProducts = async () => {
  try {
    console.log('=== 开始加载推荐商品 ===')
    const response = await getRecommendedProducts(8)
    console.log('推荐商品API响应:', response)
    
    const apiResponse = response.data
    console.log('推荐商品API数据:', apiResponse)
    
    if (apiResponse.success && apiResponse.data) {
      recommendedProducts.value = apiResponse.data
      console.log('推荐商品加载成功:', recommendedProducts.value)
    } else {
      const errorMessage = apiResponse.message || '获取推荐商品失败'
      ElMessage.error(errorMessage)
      console.error('获取推荐商品失败:', errorMessage)
    }
  } catch (error) {
    console.error('获取推荐商品异常:', error)
    ElMessage.error('获取推荐商品失败')
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    console.log('=== 开始加载分类列表 ===')
    const response = await getCategoryList()
    console.log('分类列表API响应:', response)
    
    const apiResponse = response.data
    console.log('分类列表API数据:', apiResponse)
    
    if (apiResponse.success && apiResponse.data) {
      categories.value = apiResponse.data.slice(0, 8) // 只显示前8个分类
      console.log('分类列表加载成功:', categories.value)
    } else {
      const errorMessage = apiResponse.message || '获取分类列表失败'
      ElMessage.error(errorMessage)
      console.error('获取分类列表失败:', errorMessage)
    }
  } catch (error) {
    console.error('获取分类列表异常:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 加载购物车数量
const loadCartCount = async () => {
  if (!userAuthStore.isLoggedIn) {
    cartCount.value = 0
    return
  }
  
  try {
    const response = await getCartCount()
    const apiResponse = response.data
    
    if (apiResponse.success) {
      cartCount.value = apiResponse.data || 0
    }
  } catch (error) {
    console.error('获取购物车数量失败:', error)
  }
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
      await loadCartCount() // 重新加载购物车数量
    } else {
      ElMessage.error(response.message || '添加到购物车失败')
    }
  } catch (error) {
    console.error('添加到购物车异常:', error)
    ElMessage.error('添加到购物车失败')
  }
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement
  target.src = getDefaultProductImage()
}

// 调试功能
const testLoadHotProducts = () => {
  console.log('=== 测试加载热门商品 ===')
  loadHotProducts()
}

const testLoadRecommendedProducts = () => {
  console.log('=== 测试加载推荐商品 ===')
  loadRecommendedProducts()
}

const testLoadCategories = () => {
  console.log('=== 测试加载分类 ===')
  loadCategories()
}

const testLoadAllData = () => {
  console.log('=== 测试加载所有数据 ===')
  loadAllData()
}

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadHotProducts(),
      loadRecommendedProducts(),
      loadCategories(),
      loadCartCount()
    ])
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadAllData()
})
</script>

<style scoped>
.ecommerce-home {
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 顶部导航栏 */
.top-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 70px;
}

.nav-left {
  flex: 0 0 200px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.nav-center {
  flex: 1;
  padding: 0 40px;
}

.search-bar {
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  border-radius: 25px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 25px;
  padding-right: 0;
}

.search-btn {
  border-radius: 0 25px 25px 0;
  margin-right: 1px;
}

.nav-right {
  flex: 0 0 200px;
  display: flex;
  justify-content: flex-end;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.cart-action {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  position: relative;
  height: 32px;
  min-width: 80px;
  font-size: 14px;
  border: 1px solid transparent;
  box-sizing: border-box;
  white-space: nowrap;
}

.cart-action:hover {
  background-color: #f5f7fa;
  border-color: #c6e2ff;
}

.cart-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  height: 32px;
  min-width: 80px;
  font-size: 14px;
  border: 1px solid transparent;
  box-sizing: border-box;
  white-space: nowrap;
}

.user-info:hover {
  background-color: #f5f7fa;
  border-color: #c6e2ff;
}

.auth-actions {
  display: flex;
  gap: 10px;
}

.auth-btn {
  min-width: 60px;
  height: 32px;
  font-size: 14px;
}

.action-text {
  font-size: 14px;
  color: #606266;
}

.user-name {
  font-size: 14px;
  color: #606266;
}

/* 主要内容区域 */
.main-content {
  padding-bottom: 40px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 轮播图区域 */
.banner-section {
  margin-bottom: 40px;
}

.banner-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.banner-item {
  height: 400px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.banner-content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 1;
}

.banner-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
}

.banner-subtitle {
  font-size: 20px;
  margin-bottom: 32px;
  opacity: 0.9;
}

/* 区块标题 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.section-subtitle {
  font-size: 16px;
  color: #909399;
}

/* 分类导航 */
.category-section {
  background: white;
  padding: 60px 0;
  margin-bottom: 40px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 30px;
  max-width: 900px;
  margin: 0 auto;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-item:hover {
  background-color: #f5f7fa;
  transform: translateY(-5px);
}

.category-icon {
  width: 60px;
  height: 60px;
  margin-bottom: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff, #67c23a);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s;
}

.category-item:hover .category-icon {
  background: linear-gradient(135deg, #67c23a, #409eff);
  transform: scale(1.1);
}

.category-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

/* 商品区域 */
.hot-products-section,
.recommended-section {
  background: white;
  padding: 60px 0;
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.section-header h2 {
  margin: 0;
}

.loading-container {
  padding: 40px 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
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
  background: rgba(0, 0, 0, 0.5);
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

.product-overlay:hover .add-cart-btn {
  transform: scale(1);
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-store {
  font-size: 14px;
  color: #909399;
  margin-bottom: 12px;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}



/* 页脚 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 60px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  margin-bottom: 40px;
}

.footer-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: white;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 10px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: white;
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  padding: 8px 16px;
  background: #34495e;
  border-radius: 6px;
  text-decoration: none;
  color: white;
  transition: background-color 0.3s;
}

.social-link:hover {
  background: #409eff;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #34495e;
  color: #bdc3c7;
}

/* 调试面板 */
.debug-panel {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.debug-panel h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
}

.debug-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 空状态样式 */
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.empty-category {
  grid-column: 1 / -1;
  padding: 40px 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-container {
    flex-direction: column;
    height: auto;
    padding: 15px 20px;
  }
  
  .nav-left,
  .nav-center,
  .nav-right {
    flex: none;
    width: 100%;
  }
  
  .nav-center {
    padding: 15px 0;
  }
  
  .nav-right {
    justify-content: center;
  }
  
  .banner-title {
    font-size: 32px;
  }
  
  .banner-subtitle {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .category-grid {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 20px;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>