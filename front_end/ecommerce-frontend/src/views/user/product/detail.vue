<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <div class="product-detail">
    <!-- 面包屑导航 -->
    <AppBreadcrumb :items="breadcrumbItems" />

    <!-- 返回主页按钮 -->
    <BackToHomeButton :fixed="true" position="bottom-right" :show-text="false" size="large" />

    <!-- 商品详情内容 -->
    <div class="product-content" v-loading="loading">
      <div v-if="product" class="product-info">
        <!-- 商品图片区域 -->
        <div class="product-images">
          <div class="main-image">
            <img 
              :src="getProductImage(product.productImage)" 
              :alt="product.spuName"
              @error="handleImageError"
            />
          </div>
        </div>

        <!-- 商品信息区域 -->
        <div class="product-details">
          <h1 class="product-title">{{ product.spuName }}</h1>
          
          <div class="product-description">
            <p>{{ product.spuDescription || '暂无描述' }}</p>
          </div>

          <div class="product-price">
            <span class="current-price">¥{{ (product.displayPrice || 0).toFixed(2) }}</span>
          </div>

          <div class="product-meta">
            <div class="meta-item" v-if="product.brandName">
              <span class="label">品牌：</span>
              <span class="value">{{ product.brandName }}</span>
            </div>
            <div class="meta-item" v-if="product.sellingPoint">
              <span class="label">卖点：</span>
              <span class="value">{{ product.sellingPoint }}</span>
            </div>
            <div class="meta-item" v-if="product.unit">
              <span class="label">单位：</span>
              <span class="value">{{ product.unit }}</span>
            </div>
            <div class="meta-item" v-if="product.storeName">
              <span class="label">店铺：</span>
              <span class="value">{{ product.storeName }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="product-actions">
            <el-button 
              type="primary" 
              size="large" 
              @click="addToCart"
              :loading="addingToCart"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button 
              type="danger" 
              size="large" 
              @click="buyNow"
              :loading="buying"
            >
              立即购买
            </el-button>
          </div>
        </div>
      </div>

      <!-- 商品不存在 -->
      <div v-else-if="!loading" class="product-not-found">
        <el-empty description="商品不存在或已下架">
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 商品详细信息 -->
    <div v-if="product" class="product-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="商品详情" name="detail">
          <div class="tab-content">
            <h3>商品详情</h3>
            <p>{{ product.spuDescription || '暂无详细描述' }}</p>
            
            <div v-if="product.brandName" class="detail-section">
              <h4>品牌信息</h4>
              <p>品牌：{{ product.brandName }}</p>
            </div>
            
            <div v-if="product.sellingPoint" class="detail-section">
              <h4>产品特色</h4>
              <p>{{ product.sellingPoint }}</p>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="规格参数" name="specs">
          <div class="tab-content">
            <h3>规格参数</h3>
            <table class="specs-table">
              <tr v-if="product.brandName">
                <td>品牌</td>
                <td>{{ product.brandName }}</td>
              </tr>
              <tr v-if="product.unit">
                <td>计量单位</td>
                <td>{{ product.unit }}</td>
              </tr>
              <tr>
                <td>商品编号</td>
                <td>{{ product.spuId }}</td>
              </tr>
            </table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="售后服务" name="service">
          <div class="tab-content">
            <h3>售后服务</h3>
            <ul class="service-list">
              <li>7天无理由退货</li>
              <li>15天免费换货</li>
              <li>全国联保</li>
              <li>正品保证</li>
            </ul>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import { getProductDetail, type Product } from '@/api/user/product'
import { addToCart as addToCartApi, type CartAddDTO } from '@/api/user/cart'
import { useUserAuthStore } from '@/store/modules/userAuth'
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppBreadcrumb from '@/components/common/AppBreadcrumb.vue'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const route = useRoute()
const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const addingToCart = ref(false)
const buying = ref(false)
const navbarRef = ref()

// 面包屑数据
const breadcrumbItems = computed(() => {
  const items = []
  if (product.value?.categoryName) {
    items.push({ label: product.value.categoryName, clickable: false })
  }
  items.push({ label: product.value?.spuName || '商品详情', clickable: false })
  return items
})
const activeTab = ref('detail')
const product = ref<Product | null>(null)

// 获取商品详情
const loadProductDetail = async () => {
  const productId = route.params.id as string
  if (!productId) {
    ElMessage.error('商品ID无效')
    router.push('/')
    return
  }

  loading.value = true
  try {
    const response = await getProductDetail(parseInt(productId))
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success && apiResponse.data) {
      product.value = apiResponse.data
      console.log('=== 商品详情数据 ===')
      console.log('商品ID:', product.value.spuId)
      console.log('店铺ID:', product.value.storeId)
      console.log('店铺名称:', product.value.storeName)
      console.log('商品完整数据:', product.value)
    } else {
      ElMessage.error(apiResponse.message || '商品不存在或已下架')
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  } finally {
    loading.value = false
  }
}

// 图片处理
const getProductImage = (imageUrl?: string): string => {
  if (!imageUrl) return '/assets/images/placeholder-image.svg'
  
  // 处理Base64图片数据
  if (imageUrl.startsWith('data:image/')) {
    return imageUrl
  }
  
  // 处理HTTP/HTTPS URL
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  
  // 处理本地图片路径
  if (imageUrl.includes('phone_') || imageUrl.includes('computer_')) {
    return imageUrl.replace('.jpg', '.svg')
  }
  
  // 处理相对路径
  if (imageUrl.startsWith('/')) {
    return imageUrl
  }
  
  // 默认返回原始路径
  return imageUrl
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.log('图片加载失败，使用默认图片:', img.src)
  img.src = '/assets/images/placeholder-image.svg'
}

// 操作方法
const addToCart = async () => {
  if (!product.value) return
  
  // 先初始化用户认证状态
  userAuthStore.initializeAuth()
  
  // 检查用户是否已登录
  if (!userAuthStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/user/login')
    return
  }
  
  addingToCart.value = true
  try {
    console.log('=== 添加到购物车 ===')
    console.log('商品信息:', product.value)
    console.log('商品storeId:', product.value.storeId)
    
    // 构造添加购物车的数据
    const cartData: CartAddDTO = {
      skuId: Number(product.value.spuId) || 0, // 确保是数字类型
      quantity: 1
    }
    
    console.log('添加购物车数据:', cartData)
    
    // 调用真实的添加购物车API
    const response = await addToCartApi(cartData)
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      ElMessage.success(`"${product.value.spuName}"已添加到购物车`)
      console.log('商品已成功添加到购物车')
      
      // 更新导航栏购物车数量
      if (navbarRef.value) {
        navbarRef.value.loadCartCount()
      }
    } else {
      ElMessage.error(apiResponse.message || '添加到购物车失败')
      console.error('添加购物车失败:', apiResponse.message)
    }
  } catch (error) {
    console.error('添加到购物车失败:', error)
    ElMessage.error('添加到购物车失败')
  } finally {
    addingToCart.value = false
  }
}

const buyNow = async () => {
  if (!product.value) return
  
  // 先初始化用户认证状态
  userAuthStore.initializeAuth()
  
  // 检查用户是否已登录
  if (!userAuthStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/user/login')
    return
  }
  
  buying.value = true
  try {
    console.log('=== 立即购买 - 商品信息 ===')
    console.log('商品数据:', product.value)
    console.log('商品storeId:', product.value.storeId)
    
    // 创建临时购物车项用于立即购买，确保所有必要字段都是正确的类型
    const cartItem = {
      productId: Number(product.value.spuId) || 0,
      spuId: Number(product.value.spuId) || 0, // 添加spuId字段
      productName: product.value.spuName || '',
      spuName: product.value.spuName || '', // 添加spuName字段
      productImage: product.value.productImage || '',
      price: Number(product.value.displayPrice) || 0,
      displayPrice: Number(product.value.displayPrice) || 0, // 添加displayPrice字段
      quantity: 1,
      storeId: Number(product.value.storeId) || 1,
      storeName: product.value.storeName || '',
      categoryId: Number(product.value.categoryId) || 0,
      categoryName: product.value.categoryName || '',
      brandName: product.value.brandName || '',
      unit: product.value.unit || '件',
      selected: true
    }
    
    console.log('创建的购买项:', cartItem)
    console.log('购买项storeId:', cartItem.storeId)
    
    // 将商品信息存储到 sessionStorage 用于结算页面
    sessionStorage.setItem('buyNowItem', JSON.stringify(cartItem))
    
    // 跳转到结算页面
    router.push('/checkout?from=buyNow')
    
    ElMessage.success('正在跳转到结算页面...')
  } catch (error) {
    console.error('立即购买失败:', error)
    ElMessage.error('购买失败，请重试')
  } finally {
    buying.value = false
  }
}

const goHome = () => {
  router.push('/')
}

// 生命周期
onMounted(() => {
  // 初始化用户认证状态
  userAuthStore.initializeAuth()
  loadProductDetail()
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 70px);
}

.product-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.product-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  align-items: start;
}

.product-images {
  position: sticky;
  top: 20px;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-details {
  padding: 20px 0;
}

.product-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px 0;
  line-height: 1.4;
}

.product-description {
  margin-bottom: 24px;
}

.product-description p {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.product-price {
  margin-bottom: 30px;
}

.current-price {
  font-size: 32px;
  font-weight: bold;
  color: #e74c3c;
}

.product-meta {
  margin-bottom: 40px;
}

.meta-item {
  display: flex;
  margin-bottom: 12px;
  font-size: 16px;
}

.meta-item .label {
  color: #909399;
  width: 80px;
  flex-shrink: 0;
}

.meta-item .value {
  color: #303133;
  font-weight: 500;
}

.product-actions {
  display: flex;
  gap: 16px;
}

.product-actions .el-button {
  flex: 1;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
}

.product-not-found {
  text-align: center;
  padding: 60px 20px;
}

.product-tabs {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tab-content {
  padding: 20px 0;
}

.tab-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.tab-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 20px 0 8px 0;
}

.tab-content p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.detail-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.specs-table {
  width: 100%;
  border-collapse: collapse;
}

.specs-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
}

.specs-table td:first-child {
  background: #f5f7fa;
  color: #909399;
  width: 150px;
  font-weight: 500;
}

.specs-table td:last-child {
  color: #303133;
}

.service-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.service-list li {
  padding: 8px 0;
  font-size: 14px;
  color: #606266;
  position: relative;
  padding-left: 20px;
}

.service-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #67c23a;
  font-weight: bold;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-detail {
    padding: 12px;
  }
  
  .product-content {
    padding: 20px;
  }
  
  .product-info {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .product-images {
    position: static;
  }
  
  .product-title {
    font-size: 24px;
  }
  
  .current-price {
    font-size: 28px;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .product-tabs {
    padding: 20px;
  }
}
</style>