<template>
  <nav class="app-navbar">
    <div class="navbar-container">
      <!-- 左侧Logo -->
      <div class="navbar-left">
        <div class="logo" @click="goHome">
          <span class="logo-text">购物商城</span>
        </div>
      </div>

      <!-- 中间搜索栏 -->
      <div class="navbar-center">
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            size="large"
            @keyup.enter="performSearch"
            class="search-input"
          >
            <template #append>
              <el-button @click="performSearch" type="primary" class="search-btn">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 右侧操作区 -->
      <div class="navbar-right">
        <div class="navbar-actions">
          <!-- 购物车 -->
          <div class="cart-action" @click="navigateToCart">
            <el-icon><ShoppingCart /></el-icon>
            <span class="action-text">购物车</span>
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </div>
          
          <!-- 用户菜单 -->
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
          
          <!-- 未登录状态 -->
          <div v-else class="auth-actions">
            <el-button @click="navigateToLogin" text size="default" class="auth-btn">登录</el-button>
            <el-button @click="navigateToRegister" type="primary" size="default" class="auth-btn">注册</el-button>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Search, User } from '@element-plus/icons-vue'
import { useUserAuthStore } from '@/store/modules/userAuth'
import { getCartCount } from '@/api/user/cart'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const searchKeyword = ref('')
const cartCount = ref(0)

// 计算属性
const userName = computed(() => {
  return userAuthStore.userInfo?.username || '用户'
})

// 方法
const goHome = () => {
  router.push('/')
}

const performSearch = () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  router.push({
    path: '/search',
    query: { q: searchKeyword.value.trim() }
  })
}

const navigateToCart = () => {
  router.push('/cart')
}

const navigateToLogin = () => {
  router.push('/user/login')
}

const navigateToRegister = () => {
  router.push('/user/register')
}

const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'address':
      ElMessage.info('收货地址管理功能开发中...')
      break
    case 'logout':
      userAuthStore.logout()
      ElMessage.success('退出登录成功')
      break
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
    
    // 修复：访问response.data获取实际的API响应数据
    const apiResponse = response.data
    if (apiResponse.success) {
      cartCount.value = apiResponse.data || 0
      console.log('购物车数量更新:', cartCount.value)
    }
  } catch (error) {
    console.error('获取购物车数量失败:', error)
  }
}

// 组件挂载时初始化
onMounted(() => {
  userAuthStore.initializeAuth()
  loadCartCount()
})

// 暴露方法供父组件调用
defineExpose({
  loadCartCount
})
</script>

<style scoped>
.app-navbar {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 70px;
}

.navbar-left {
  flex: 0 0 200px;
}

.logo {
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.navbar-center {
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

.navbar-right {
  flex: 0 0 200px;
  display: flex;
  justify-content: flex-end;
}

.navbar-actions {
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

.action-text {
  font-size: 14px;
  color: #606266;
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

.user-name {
  font-size: 14px;
  color: #606266;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 15px;
    height: 60px;
  }
  
  .navbar-left {
    flex: 0 0 120px;
  }
  
  .logo-text {
    font-size: 18px;
  }
  
  .navbar-center {
    padding: 0 20px;
  }
  
  .navbar-right {
    flex: 0 0 120px;
  }
  
  .action-text,
  .user-name {
    display: none;
  }
  
  .navbar-actions {
    gap: 10px;
  }
}
</style> 