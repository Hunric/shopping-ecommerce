<template>
  <div class="api-test-page">
    <h1>扩展店铺API测试</h1>
    
    <div class="test-section">
      <h2>API测试结果</h2>
      
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>获取店铺列表测试</span>
            <el-button type="primary" @click="testGetStores">测试</el-button>
          </div>
        </template>
        
        <div v-if="storeListLoading" class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          正在测试...
        </div>
        
        <div v-else-if="storeListResult">
          <div class="result-status" :class="storeListResult.success ? 'success' : 'error'">
            状态: {{ storeListResult.success ? '成功' : '失败' }}
          </div>
          <div class="result-message">
            消息: {{ storeListResult.message }}
          </div>
          <div v-if="storeListResult.data" class="result-data">
            <p>数据数量: {{ storeListResult.data.length }}</p>
            <pre>{{ JSON.stringify(storeListResult.data, null, 2) }}</pre>
          </div>
        </div>
      </el-card>
      
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>获取店铺数量测试</span>
            <el-button type="primary" @click="testGetStoreCount">测试</el-button>
          </div>
        </template>
        
        <div v-if="storeCountLoading" class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          正在测试...
        </div>
        
        <div v-else-if="storeCountResult">
          <div class="result-status" :class="storeCountResult.success ? 'success' : 'error'">
            状态: {{ storeCountResult.success ? '成功' : '失败' }}
          </div>
          <div class="result-message">
            消息: {{ storeCountResult.message }}
          </div>
          <div v-if="storeCountResult.data !== undefined" class="result-data">
            <p>店铺数量: {{ storeCountResult.data }}</p>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="test-section">
      <h2>错误日志</h2>
      <div v-if="errorLogs.length === 0" class="no-errors">
        暂无错误
      </div>
      <div v-else>
        <div v-for="(error, index) in errorLogs" :key="index" class="error-log">
          <div class="error-time">{{ error.time }}</div>
          <div class="error-message">{{ error.message }}</div>
          <pre v-if="error.details" class="error-details">{{ error.details }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/modules/auth'
import { 
  getStoresExtendedByMerchantId, 
  getStoreExtendedCount,
  type StoreExtended,
  type ApiResponse 
} from '@/api/merchant/storeExtended'

const authStore = useAuthStore()

// 测试状态
const storeListLoading = ref(false)
const storeCountLoading = ref(false)

// 测试结果
const storeListResult = ref<ApiResponse<StoreExtended[]> | null>(null)
const storeCountResult = ref<ApiResponse<number> | null>(null)

// 错误日志
const errorLogs = ref<Array<{time: string, message: string, details?: string}>>([])

const addErrorLog = (message: string, details?: any) => {
  errorLogs.value.push({
    time: new Date().toLocaleString(),
    message,
    details: details ? JSON.stringify(details, null, 2) : undefined
  })
}

const testGetStores = async () => {
  if (!authStore.merchantInfo?.merchantId) {
    ElMessage.error('请先登录')
    return
  }
  
  storeListLoading.value = true
  storeListResult.value = null
  
  try {
    console.log('开始测试获取店铺列表，merchantId:', authStore.merchantInfo.merchantId)
    const result = await getStoresExtendedByMerchantId(authStore.merchantInfo.merchantId)
    console.log('API返回结果:', result)
    
    storeListResult.value = result
    
    if (result.success) {
      ElMessage.success('获取店铺列表成功')
    } else {
      ElMessage.error(`获取店铺列表失败: ${result.message}`)
      addErrorLog('获取店铺列表失败', result)
    }
  } catch (error) {
    console.error('获取店铺列表异常:', error)
    ElMessage.error('获取店铺列表异常')
    addErrorLog('获取店铺列表异常', error)
  } finally {
    storeListLoading.value = false
  }
}

const testGetStoreCount = async () => {
  if (!authStore.merchantInfo?.merchantId) {
    ElMessage.error('请先登录')
    return
  }
  
  storeCountLoading.value = true
  storeCountResult.value = null
  
  try {
    console.log('开始测试获取店铺数量，merchantId:', authStore.merchantInfo.merchantId)
    const result = await getStoreExtendedCount(authStore.merchantInfo.merchantId)
    console.log('API返回结果:', result)
    
    storeCountResult.value = result
    
    if (result.success) {
      ElMessage.success('获取店铺数量成功')
    } else {
      ElMessage.error(`获取店铺数量失败: ${result.message}`)
      addErrorLog('获取店铺数量失败', result)
    }
  } catch (error) {
    console.error('获取店铺数量异常:', error)
    ElMessage.error('获取店铺数量异常')
    addErrorLog('获取店铺数量异常', error)
  } finally {
    storeCountLoading.value = false
  }
}
</script>

<style scoped>
.api-test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
}

.test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409eff;
}

.result-status {
  font-weight: bold;
  margin-bottom: 8px;
}

.result-status.success {
  color: #67c23a;
}

.result-status.error {
  color: #f56c6c;
}

.result-message {
  margin-bottom: 8px;
}

.result-data {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.result-data pre {
  margin: 8px 0 0 0;
  font-size: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.no-errors {
  color: #67c23a;
  font-style: italic;
}

.error-log {
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
}

.error-time {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.error-message {
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 8px;
}

.error-details {
  font-size: 12px;
  background: #fff;
  padding: 8px;
  border-radius: 4px;
  max-height: 200px;
  overflow-y: auto;
}
</style> 