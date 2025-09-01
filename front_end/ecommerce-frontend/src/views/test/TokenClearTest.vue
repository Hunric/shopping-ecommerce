<template>
  <div class="token-clear-test">
    <h2>JWT Token 清理工具</h2>
    
    <div class="info-section">
      <el-alert
        title="JWT密钥已更新"
        type="warning"
        description="由于JWT密钥已更新，所有现有的token都已失效。请清除旧token并重新登录。"
        show-icon
        :closable="false"
      />
    </div>

    <div class="current-tokens">
      <h3>当前存储的Token:</h3>
      <div class="token-info">
        <p><strong>用户Token:</strong> {{ userToken ? '存在' : '不存在' }}</p>
        <p><strong>商家Token:</strong> {{ merchantToken ? '存在' : '不存在' }}</p>
      </div>
    </div>

    <div class="actions">
      <el-button type="danger" @click="clearAllTokens" size="large">
        清除所有Token
      </el-button>
      <el-button type="primary" @click="goToUserLogin" size="large">
        用户登录
      </el-button>
      <el-button type="success" @click="goToMerchantLogin" size="large">
        商家登录
      </el-button>
    </div>

    <div class="test-section">
      <h3>测试API调用:</h3>
      <el-button @click="testUserAPI">测试用户API</el-button>
      <el-button @click="testMerchantAPI">测试商家API</el-button>
      
      <div v-if="testResult" class="result">
        <h4>测试结果:</h4>
        <pre>{{ testResult }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'
import request from '@/utils/request'

const router = useRouter()
const authStore = useAuthStore()

const userToken = ref<string | null>(null)
const merchantToken = ref<string | null>(null)
const testResult = ref<string>('')

onMounted(() => {
  loadTokenInfo()
})

const loadTokenInfo = () => {
  userToken.value = localStorage.getItem('userToken')
  merchantToken.value = authStore.accessToken
}

const clearAllTokens = () => {
  // 清除用户token
  localStorage.removeItem('userToken')
  localStorage.removeItem('userRefreshToken')
  localStorage.removeItem('userInfo')
  
  // 清除商家token
  authStore.logout()
  
  // 重新加载token信息
  loadTokenInfo()
  
  ElMessage.success('所有Token已清除')
}

const goToUserLogin = () => {
  router.push('/user/login')
}

const goToMerchantLogin = () => {
  router.push('/merchant/login')
}

const testUserAPI = async () => {
  try {
    const response = await request.get('/api/cart/count')
    testResult.value = `用户API测试成功: ${JSON.stringify(response.data, null, 2)}`
  } catch (error: any) {
    testResult.value = `用户API测试失败: ${error.message}\n${JSON.stringify(error.response?.data, null, 2)}`
  }
}

const testMerchantAPI = async () => {
  try {
    const response = await request.get('/api/merchant/stores')
    testResult.value = `商家API测试成功: ${JSON.stringify(response.data, null, 2)}`
  } catch (error: any) {
    testResult.value = `商家API测试失败: ${error.message}\n${JSON.stringify(error.response?.data, null, 2)}`
  }
}
</script>

<style scoped>
.token-clear-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.info-section {
  margin-bottom: 20px;
}

.current-tokens {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.token-info p {
  margin: 5px 0;
}

.actions {
  margin-bottom: 20px;
  text-align: center;
}

.actions .el-button {
  margin: 0 10px;
}

.test-section {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.result {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 5px;
}

.result pre {
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>