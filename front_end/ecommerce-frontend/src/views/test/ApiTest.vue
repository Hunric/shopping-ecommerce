<template>
  <div class="api-test">
    <el-card>
      <template #header>
        <span>API连接测试</span>
      </template>
      
      <div class="test-section">
        <h3>Dashboard统计数据测试</h3>
        <el-form inline>
          <el-form-item label="商家ID:">
            <el-input v-model="merchantId" placeholder="输入商家ID" style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="testDashboardStats" :loading="loading.dashboard">
              测试Dashboard API
            </el-button>
          </el-form-item>
        </el-form>
        
        <div v-if="dashboardResult" class="result-box">
          <h4>Dashboard API结果:</h4>
          <pre>{{ JSON.stringify(dashboardResult, null, 2) }}</pre>
        </div>
      </div>
      
      <el-divider />
      
      <div class="test-section">
        <h3>店铺列表测试</h3>
        <el-button type="primary" @click="testStoreList" :loading="loading.stores">
          测试店铺列表 API
        </el-button>
        
        <div v-if="storeResult" class="result-box">
          <h4>店铺列表 API结果:</h4>
          <pre>{{ JSON.stringify(storeResult, null, 2) }}</pre>
        </div>
      </div>
      
      <el-divider />
      
      <div class="test-section">
        <h3>创建店铺测试</h3>
        <el-form :model="testStore" label-width="100px">
          <el-form-item label="店铺名称:">
            <el-input v-model="testStore.storeName" placeholder="输入店铺名称" />
          </el-form-item>
          <el-form-item label="店铺描述:">
            <el-input v-model="testStore.storeDescription" type="textarea" placeholder="输入店铺描述" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="testCreateStore" :loading="loading.create">
              测试创建店铺 API
            </el-button>
          </el-form-item>
        </el-form>
        
        <div v-if="createResult" class="result-box">
          <h4>创建店铺 API结果:</h4>
          <pre>{{ JSON.stringify(createResult, null, 2) }}</pre>
        </div>
      </div>
      
      <el-divider />
      
      <div class="test-section">
        <h3>认证状态检查</h3>
        <div class="auth-info">
          <p><strong>登录状态:</strong> {{ authStore.isLoggedIn ? '已登录' : '未登录' }}</p>
          <p><strong>商家信息:</strong> {{ authStore.merchantInfo ? JSON.stringify(authStore.merchantInfo) : '无' }}</p>
          <p><strong>Access Token:</strong> {{ authStore.accessToken ? '有效' : '无' }}</p>
          <p><strong>Token前缀:</strong> {{ authStore.accessToken ? authStore.accessToken.substring(0, 20) + '...' : '无' }}</p>
        </div>
        
        <el-button type="warning" @click="clearAuth">清除认证信息</el-button>
        <el-button type="success" @click="getTestToken">获取测试Token</el-button>
        <el-button type="info" @click="mockLogin">模拟登录</el-button>
      </div>
      
      <el-divider />
      
      <div class="test-section">
        <h3>API配置信息</h3>
        <p><strong>API Base URL:</strong> {{ apiBaseUrl }}</p>
        <p><strong>当前时间:</strong> {{ currentTime }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDashboardStats } from '@/api/merchant/dashboard'
import { getStoresByMerchantId, createStore } from '@/api/merchant/store'
import type { StoreCreateData } from '@/api/merchant/store'
import { useAuthStore } from '@/store/modules/auth'

// 响应式数据
const authStore = useAuthStore()
const merchantId = ref('1')
const currentTime = ref('')
const apiBaseUrl = ref('')

const loading = reactive({
  dashboard: false,
  stores: false,
  create: false
})

const dashboardResult = ref<any>(null)
const storeResult = ref<any>(null)
const createResult = ref<any>(null)

const testStore = reactive({
  storeName: '测试店铺',
  storeDescription: '这是一个测试店铺的描述'
})

// 测试Dashboard统计数据API
const testDashboardStats = async () => {
  loading.dashboard = true
  try {
    const result = await getDashboardStats(parseInt(merchantId.value))
    dashboardResult.value = result
    ElMessage.success('Dashboard API测试成功')
  } catch (error: any) {
    console.error('Dashboard API测试失败:', error)
    dashboardResult.value = { error: error.message || '请求失败' }
    ElMessage.error('Dashboard API测试失败')
  } finally {
    loading.dashboard = false
  }
}

// 测试店铺列表API
const testStoreList = async () => {
  loading.stores = true
  try {
    const result = await getStoresByMerchantId(parseInt(merchantId.value))
    storeResult.value = result
    ElMessage.success('店铺列表API测试成功')
  } catch (error: any) {
    console.error('店铺列表API测试失败:', error)
    storeResult.value = { error: error.message || '请求失败' }
    ElMessage.error('店铺列表API测试失败')
  } finally {
    loading.stores = false
  }
}

// 测试创建店铺API
const testCreateStore = async () => {
  loading.create = true
  try {
    const storeData: StoreCreateData = {
      merchantId: parseInt(merchantId.value),
      storeName: testStore.storeName,
      storeDescription: testStore.storeDescription
    }
    
    const result = await createStore(storeData)
    createResult.value = result
    ElMessage.success('创建店铺API测试成功')
  } catch (error: any) {
    console.error('创建店铺API测试失败:', error)
    createResult.value = { error: error.message || '请求失败' }
    ElMessage.error('创建店铺API测试失败')
  } finally {
    loading.create = false
  }
}

// 清除认证信息
const clearAuth = () => {
  authStore.logout()
  ElMessage.success('认证信息已清除')
}

// 获取测试Token
const getTestToken = async () => {
  try {
    const response = await fetch(`/api/merchant/test/generate-token/${merchantId.value}`)
    const result = await response.json()
    
    if (result.success && result.data) {
      const tokenData = result.data
      const loginResponse = {
        success: true,
        message: '获取测试Token成功',
        accessToken: tokenData.accessToken,
        refreshToken: tokenData.refreshToken,
        tokenType: tokenData.tokenType,
        expiresIn: tokenData.expiresIn,
        merchantId: tokenData.merchantId,
        merchantName: '测试商家',
        email: tokenData.email
      }
      
      authStore.setAuthData(loginResponse)
      ElMessage.success('获取测试Token成功，现在可以测试API了')
    } else {
      ElMessage.error('获取测试Token失败: ' + (result.message || '未知错误'))
    }
  } catch (error) {
    console.error('获取测试Token失败:', error)
    ElMessage.error('获取测试Token失败，请检查后端服务')
  }
}

// 模拟登录
const mockLogin = () => {
  // 创建一个模拟的登录响应
  const mockLoginResponse = {
    success: true,
    message: '登录成功',
    accessToken: 'mock-jwt-token-for-testing-' + Date.now(),
    refreshToken: 'mock-refresh-token-' + Date.now(),
    tokenType: 'Bearer',
    expiresIn: 3600,
    merchantId: parseInt(merchantId.value),
    merchantName: '测试商家',
    email: 'test@example.com'
  }
  
  authStore.setAuthData(mockLoginResponse)
  ElMessage.success('模拟登录成功（注意：这是假token，无法调用真实API）')
}

// 更新当前时间
const updateTime = () => {
  currentTime.value = new Date().toLocaleString()
}

onMounted(() => {
  updateTime()
  setInterval(updateTime, 1000)
  
  // 获取API配置
  apiBaseUrl.value = '通过代理访问 (开发环境: Vite代理, Docker环境: Nginx代理)'
  
  // 初始化认证状态
  authStore.initializeAuth()
})
</script>

<style scoped>
.api-test {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
}

.test-section {
  margin-bottom: 20px;
}

.test-section h3 {
  color: #333;
  margin-bottom: 16px;
}

.result-box {
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.result-box h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.result-box pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 12px;
  line-height: 1.4;
  color: #606266;
}

.auth-info {
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 16px;
}

.auth-info p {
  margin: 8px 0;
  font-size: 14px;
}
</style> 