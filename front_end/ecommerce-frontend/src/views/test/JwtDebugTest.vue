<template>
  <div class="jwt-debug-container">
    <el-card class="debug-card">
      <template #header>
        <div class="card-header">
          <span>JWT Token 调试工具</span>
          <el-button type="primary" @click="refreshTokenInfo">刷新</el-button>
        </div>
      </template>

      <div class="token-section">
        <h3>当前Token状态</h3>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span>用户Token (localStorage)</span>
              </template>
              <div class="token-info">
                <p><strong>存在:</strong> {{ userTokenExists ? '是' : '否' }}</p>
                <p><strong>Token:</strong></p>
                <el-input
                  v-model="userToken"
                  type="textarea"
                  :rows="3"
                  readonly
                  placeholder="无用户Token"
                />
                <div v-if="userTokenDecoded" class="token-decoded">
                  <p><strong>解析内容:</strong></p>
                  <pre>{{ JSON.stringify(userTokenDecoded, null, 2) }}</pre>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span>商家Token (Pinia Store)</span>
              </template>
              <div class="token-info">
                <p><strong>存在:</strong> {{ merchantTokenExists ? '是' : '否' }}</p>
                <p><strong>Token:</strong></p>
                <el-input
                  v-model="merchantToken"
                  type="textarea"
                  :rows="3"
                  readonly
                  placeholder="无商家Token"
                />
                <div v-if="merchantTokenDecoded" class="token-decoded">
                  <p><strong>解析内容:</strong></p>
                  <pre>{{ JSON.stringify(merchantTokenDecoded, null, 2) }}</pre>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="test-section">
        <h3>API测试</h3>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span>用户API测试</span>
              </template>
              <div class="api-test">
                <el-button @click="testUserAPI" type="primary" :loading="userApiLoading">
                  测试购物车API
                </el-button>
                <div v-if="userApiResult" class="api-result">
                  <p><strong>结果:</strong></p>
                  <pre>{{ userApiResult }}</pre>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="hover">
              <template #header>
                <span>商家API测试</span>
              </template>
              <div class="api-test">
                <el-button @click="testMerchantAPI" type="primary" :loading="merchantApiLoading">
                  测试商家仪表板API
                </el-button>
                <div v-if="merchantApiResult" class="api-result">
                  <p><strong>结果:</strong></p>
                  <pre>{{ merchantApiResult }}</pre>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div class="action-section">
        <h3>操作</h3>
        <el-space>
          <el-button @click="clearUserToken" type="danger">清除用户Token</el-button>
          <el-button @click="clearMerchantToken" type="danger">清除商家Token</el-button>
          <el-button @click="clearAllTokens" type="danger">清除所有Token</el-button>
        </el-space>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/modules/auth'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const authStore = useAuthStore()

// Token状态
const userToken = ref('')
const merchantToken = ref('')
const userTokenExists = ref(false)
const merchantTokenExists = ref(false)
const userTokenDecoded = ref<any>(null)
const merchantTokenDecoded = ref<any>(null)

// API测试状态
const userApiLoading = ref(false)
const merchantApiLoading = ref(false)
const userApiResult = ref('')
const merchantApiResult = ref('')

// 解析JWT Token
const decodeJWT = (token: string) => {
  try {
    const payload = token.split('.')[1]
    const decoded = JSON.parse(atob(payload))
    return decoded
  } catch (error) {
    return null
  }
}

// 刷新Token信息
const refreshTokenInfo = () => {
  // 获取用户Token
  const userTokenValue = localStorage.getItem('userToken')
  userToken.value = userTokenValue || ''
  userTokenExists.value = !!userTokenValue
  userTokenDecoded.value = userTokenValue ? decodeJWT(userTokenValue) : null

  // 获取商家Token
  const merchantTokenValue = authStore.accessToken
  merchantToken.value = merchantTokenValue || ''
  merchantTokenExists.value = !!merchantTokenValue
  merchantTokenDecoded.value = merchantTokenValue ? decodeJWT(merchantTokenValue) : null
}

// 测试用户API
const testUserAPI = async () => {
  userApiLoading.value = true
  try {
    const response = await request({
      url: '/api/cart/count',
      method: 'GET'
    })
    userApiResult.value = JSON.stringify(response.data, null, 2)
    ElMessage.success('用户API测试成功')
  } catch (error: any) {
    userApiResult.value = `错误: ${error.message}\n状态码: ${error.response?.status}\n响应: ${JSON.stringify(error.response?.data, null, 2)}`
    ElMessage.error('用户API测试失败')
  } finally {
    userApiLoading.value = false
  }
}

// 测试商家API
const testMerchantAPI = async () => {
  merchantApiLoading.value = true
  try {
    const response = await request({
      url: '/api/merchant/dashboard/stats',
      method: 'GET'
    })
    merchantApiResult.value = JSON.stringify(response.data, null, 2)
    ElMessage.success('商家API测试成功')
  } catch (error: any) {
    merchantApiResult.value = `错误: ${error.message}\n状态码: ${error.response?.status}\n响应: ${JSON.stringify(error.response?.data, null, 2)}`
    ElMessage.error('商家API测试失败')
  } finally {
    merchantApiLoading.value = false
  }
}

// 清除Token
const clearUserToken = () => {
  localStorage.removeItem('userToken')
  localStorage.removeItem('userInfo')
  refreshTokenInfo()
  ElMessage.success('用户Token已清除')
}

const clearMerchantToken = () => {
  authStore.logout()
  refreshTokenInfo()
  ElMessage.success('商家Token已清除')
}

const clearAllTokens = () => {
  clearUserToken()
  clearMerchantToken()
  ElMessage.success('所有Token已清除')
}

onMounted(() => {
  refreshTokenInfo()
})
</script>

<style scoped>
.jwt-debug-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.debug-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.token-section,
.test-section,
.action-section {
  margin-bottom: 30px;
}

.token-info {
  padding: 10px 0;
}

.token-decoded {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.token-decoded pre {
  margin: 0;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.api-test {
  padding: 10px 0;
}

.api-result {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.api-result pre {
  margin: 0;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}

h3 {
  color: #409eff;
  margin-bottom: 15px;
}
</style>