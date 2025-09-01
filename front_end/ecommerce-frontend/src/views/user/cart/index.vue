<template>
  <!-- 统一导航栏 -->
  <AppNavbar ref="navbarRef" />
  
  <!-- 面包屑导航 -->
  <div class="page-container">
    <AppBreadcrumb :items="breadcrumbItems" />
    
    <div class="cart-container">
      <div class="cart-header">
        <div class="header-content">
          <div class="header-left">
            <h2>购物车</h2>
            <div class="cart-count">共 {{ cartList.length }} 件商品</div>
          </div>
          <BackToHomeButton size="small" type="info" />
        </div>
      </div>

      <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="cartList.length === 0" class="empty-cart">
        <div class="empty-icon">🛒</div>
        <p>购物车是空的</p>
        <button @click="goShopping" class="go-shopping-btn">去购物</button>
      </div>

      <div v-else class="cart-content">
        <!-- 全选操作 -->
        <div class="select-all">
          <label class="checkbox-label">
            <input 
              type="checkbox" 
              :checked="isAllSelected" 
              @change="toggleSelectAll"
            />
            <span class="checkmark"></span>
            全选
          </label>
          <button @click="clearCart" class="clear-btn">清空购物车</button>
        </div>

        <!-- 购物车列表 -->
        <div class="cart-list">
          <div 
            v-for="item in cartList" 
            :key="item.cartId" 
            class="cart-item"
            :class="{ 'selected': item.isSelected }"
          >
            <label class="checkbox-label">
              <input 
                type="checkbox" 
                :checked="item.isSelected" 
                @change="toggleItemSelect(item)"
              />
              <span class="checkmark"></span>
            </label>

            <div class="product-image">
              <img :src="item.productImage || '/default-product.png'" :alt="item.productName" />
            </div>

            <div class="product-info">
              <h3 class="product-name">{{ item.productName }}</h3>
              <p class="product-attr">{{ item.skuAttr }}</p>
              <div class="price">¥{{ item.skuPrice.toFixed(2) }}</div>
            </div>

            <div class="quantity-control">
              <button 
                @click="decreaseQuantity(item)" 
                :disabled="item.quantity <= 1"
                class="quantity-btn"
              >
                -
              </button>
              <input 
                v-model.number="item.quantity" 
                @blur="updateQuantity(item)"
                @keyup.enter="updateQuantity(item)"
                class="quantity-input"
                type="number"
                min="1"
              />
              <button 
                @click="increaseQuantity(item)" 
                class="quantity-btn"
              >
                +
              </button>
            </div>

            <div class="subtotal">
              ¥{{ item.subtotal.toFixed(2) }}
            </div>

            <button @click="removeItem(item)" class="remove-btn">
              删除
            </button>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="checkout-bar">
          <div class="selected-info">
            已选择 {{ selectedCount }} 件商品
          </div>
          <div class="total-price">
            合计：¥{{ totalPrice.toFixed(2) }}
          </div>
          <button 
            @click="checkout" 
            :disabled="selectedCount === 0"
            class="checkout-btn"
          >
            结算 ({{ selectedCount }})
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCartList,
  updateCartItem,
  removeCartItem,
  clearCart as clearCartApi,
  updateSelectedStatus,
  type CartItemResponseDTO
} from '@/api/user/cart'
import { useUserAuthStore } from '@/store/modules/userAuth'
import AppNavbar from '@/components/common/AppNavbar.vue'
import AppBreadcrumb from '@/components/common/AppBreadcrumb.vue'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const cartList = ref<CartItemResponseDTO[]>([])
const navbarRef = ref()

// 面包屑数据
const breadcrumbItems = computed(() => [
  { label: '购物车', clickable: false }
])

// 计算属性
const isAllSelected = computed(() => {
  return cartList.value.length > 0 && cartList.value.every(item => item.isSelected)
})

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.isSelected).length
})

const totalPrice = computed(() => {
  return cartList.value
    .filter(item => item.isSelected)
    .reduce((total, item) => total + item.subtotal, 0)
})

// 方法
const loadCartList = async () => {
  try {
    loading.value = true
    const response = await getCartList()
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    console.log('购物车API响应:', apiResponse)
    
    if (apiResponse.success) {
      cartList.value = apiResponse.data || []
      console.log('购物车数据加载成功:', cartList.value)
    } else {
      ElMessage.error(apiResponse.message || '获取购物车失败')
    }
  } catch (error) {
    console.error('获取购物车失败:', error)
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const toggleSelectAll = async () => {
  const newSelectStatus = !isAllSelected.value
  const cartIds = cartList.value.map(item => item.cartId)
  
  try {
    const response = await updateSelectedStatus(cartIds, newSelectStatus)
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      cartList.value.forEach(item => {
        item.isSelected = newSelectStatus
      })
    } else {
      ElMessage.error(apiResponse.message || '操作失败')
    }
  } catch (error) {
    console.error('更新选中状态失败:', error)
    ElMessage.error('操作失败')
  }
}

const toggleItemSelect = async (item: CartItemResponseDTO) => {
  try {
    const response = await updateCartItem({
      cartId: item.cartId,
      isSelected: !item.isSelected
    })
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      item.isSelected = !item.isSelected
    } else {
      ElMessage.error(apiResponse.message || '操作失败')
    }
  } catch (error) {
    console.error('更新选中状态失败:', error)
    ElMessage.error('操作失败')
  }
}

const updateQuantity = async (item: CartItemResponseDTO) => {
  if (item.quantity < 1) {
    item.quantity = 1
    return
  }

  try {
    const response = await updateCartItem({
      cartId: item.cartId,
      quantity: item.quantity
    })
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      item.subtotal = item.skuPrice * item.quantity
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(apiResponse.message || '更新失败')
      // 恢复原数量
      await loadCartList()
    }
  } catch (error) {
    console.error('更新数量失败:', error)
    ElMessage.error('更新失败')
    await loadCartList()
  }
}

const increaseQuantity = (item: CartItemResponseDTO) => {
  item.quantity++
  updateQuantity(item)
}

const decreaseQuantity = (item: CartItemResponseDTO) => {
  if (item.quantity > 1) {
    item.quantity--
    updateQuantity(item)
  }
}

const removeItem = async (item: CartItemResponseDTO) => {
  try {
    await ElMessageBox.confirm('确定要删除这件商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await removeCartItem(item.cartId)
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      ElMessage.success('删除成功')
      await loadCartList()
    } else {
      ElMessage.error(apiResponse.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await clearCartApi()
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      ElMessage.success('清空成功')
      cartList.value = []
    } else {
      ElMessage.error(apiResponse.message || '清空失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空购物车失败:', error)
      ElMessage.error('清空失败')
    }
  }
}

const checkout = () => {
  // 先初始化用户认证状态
  userAuthStore.initializeAuth()
  
  // 添加调试信息
  console.log('购物车结算 - 认证状态检查:')
  console.log('isLoggedIn:', userAuthStore.isLoggedIn)
  console.log('userInfo:', userAuthStore.userInfo)
  console.log('accessToken:', userAuthStore.accessToken ? '存在' : '不存在')
  console.log('localStorage token:', localStorage.getItem('user_access_token') ? '存在' : '不存在')
  
  if (!userAuthStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/user/login')
    return
  }

  if (selectedCount.value === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  // 跳转到结算页面
  router.push('/checkout')
}

const goShopping = () => {
  router.push('/')
}

// 生命周期
onMounted(() => {
  // 初始化用户认证状态
  userAuthStore.initializeAuth()
  loadCartList()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.cart-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.cart-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.cart-header h2 {
  margin: 0 0 5px 0;
  color: #333;
}

.cart-count {
  color: #666;
  font-size: 14px;
}

.loading {
  text-align: center;
  padding: 50px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-cart {
  text-align: center;
  padding: 100px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.go-shopping-btn {
  background: #409eff;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.go-shopping-btn:hover {
  background: #66b1ff;
}

.select-all {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #ddd;
  border-radius: 3px;
  margin-right: 8px;
  position: relative;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark {
  background: #409eff;
  border-color: #409eff;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  position: absolute;
  color: white;
  font-size: 12px;
  top: -2px;
  left: 2px;
}

.clear-btn {
  background: #f56c6c;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.clear-btn:hover {
  background: #f78989;
}

.cart-list {
  background: white;
  border-radius: 4px;
  overflow: hidden;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s;
}

.cart-item:hover {
  background: #f8f9fa;
}

.cart-item.selected {
  background: #f0f9ff;
}

.product-image {
  width: 80px;
  height: 80px;
  margin: 0 20px;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
  margin-right: 20px;
}

.product-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.product-attr {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

.quantity-control {
  display: flex;
  align-items: center;
  margin: 0 20px;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn:hover:not(:disabled) {
  background: #f5f5f5;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 60px;
  height: 32px;
  text-align: center;
  border: 1px solid #ddd;
  border-left: none;
  border-right: none;
}

.subtotal {
  width: 100px;
  text-align: right;
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
  margin-right: 20px;
}

.remove-btn {
  background: none;
  border: none;
  color: #f56c6c;
  cursor: pointer;
  padding: 8px;
}

.remove-btn:hover {
  color: #f78989;
}

.checkout-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-top: 1px solid #eee;
  position: sticky;
  bottom: 0;
  margin-top: 20px;
}

.selected-info {
  color: #666;
}

.total-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 600;
}

.checkout-btn {
  background: #409eff;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.checkout-btn:hover:not(:disabled) {
  background: #66b1ff;
}

.checkout-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}
</style>