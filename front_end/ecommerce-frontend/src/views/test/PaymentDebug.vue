<template>
  <div class="payment-debug">
    <h1>支付功能调试</h1>
    
    <div class="debug-section">
      <h2>问题诊断</h2>
      <div class="issue-description">
        <p><strong>问题：</strong>模拟支付成功后点击查看订单加载不出来</p>
        <p><strong>可能原因：</strong></p>
        <ul>
          <li>支付API调用失败</li>
          <li>订单状态更新延迟</li>
          <li>前端缓存问题</li>
          <li>路由跳转时机问题</li>
        </ul>
      </div>
    </div>
    
    <div class="debug-section">
      <h2>测试工具</h2>
      
      <div class="test-tool">
        <h3>1. 订单状态查询</h3>
        <div class="tool-content">
          <el-input 
            v-model="testOrderId" 
            placeholder="请输入订单ID"
            style="width: 200px; margin-right: 10px;"
          />
          <el-button @click="checkOrderStatus" type="primary" :loading="checking">
            查询订单状态
          </el-button>
        </div>
        <div v-if="orderStatus" class="result">
          <h4>订单信息：</h4>
          <pre>{{ JSON.stringify(orderStatus, null, 2) }}</pre>
        </div>
      </div>
      
      <div class="test-tool">
        <h3>2. 模拟支付测试</h3>
        <div class="tool-content">
          <el-input 
            v-model="paymentTestOrderId" 
            placeholder="请输入待支付订单ID"
            style="width: 200px; margin-right: 10px;"
          />
          <el-select v-model="paymentMethod" style="width: 120px; margin-right: 10px;">
            <el-option label="支付宝" value="alipay" />
            <el-option label="微信支付" value="wechat" />
            <el-option label="银行卡" value="bank" />
          </el-select>
          <el-button @click="testPayment" type="success" :loading="paying">
            模拟支付
          </el-button>
        </div>
        <div v-if="paymentResult" class="result">
          <h4>支付结果：</h4>
          <pre>{{ JSON.stringify(paymentResult, null, 2) }}</pre>
        </div>
      </div>
      
      <div class="test-tool">
        <h3>3. 用户认证状态</h3>
        <div class="tool-content">
          <el-button @click="checkAuthStatus" type="info">
            检查认证状态
          </el-button>
        </div>
        <div v-if="authStatus" class="result">
          <h4>认证信息：</h4>
          <pre>{{ JSON.stringify(authStatus, null, 2) }}</pre>
        </div>
      </div>
    </div>
    
    <div class="debug-section">
      <h2>解决方案</h2>
      <div class="solutions">
        <div class="solution">
          <h4>1. 增加重试机制</h4>
          <p>在订单详情页面添加重试逻辑，处理状态更新延迟</p>
        </div>
        
        <div class="solution">
          <h4>2. 添加调试日志</h4>
          <p>在支付流程中添加详细的控制台日志，便于排查问题</p>
        </div>
        
        <div class="solution">
          <h4>3. 优化跳转时机</h4>
          <p>在支付成功后延迟跳转，给后端时间更新订单状态</p>
        </div>
        
        <div class="solution">
          <h4>4. 状态同步</h4>
          <p>支付成功后立即更新前端订单状态，减少依赖后端</p>
        </div>
      </div>
    </div>
    
    <div class="debug-section">
      <h2>快速操作</h2>
      <div class="quick-actions">
        <el-button @click="goToOrders" type="primary">查看订单列表</el-button>
        <el-button @click="goToPaymentTest" type="success">支付功能测试</el-button>
        <el-button @click="clearCache" type="warning">清除缓存</el-button>
        <el-button @click="refreshPage" type="info">刷新页面</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElButton, ElInput, ElSelect, ElOption } from 'element-plus'
import { getOrderDetail, payOrder } from '@/api/user/order'
import { useUserAuthStore } from '@/store/modules/userAuth'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const testOrderId = ref('')
const paymentTestOrderId = ref('')
const paymentMethod = ref('alipay')
const checking = ref(false)
const paying = ref(false)
const orderStatus = ref(null)
const paymentResult = ref(null)
const authStatus = ref(null)

// 方法
const checkOrderStatus = async () => {
  if (!testOrderId.value) {
    ElMessage.warning('请输入订单ID')
    return
  }
  
  try {
    checking.value = true
    console.log('=== 查询订单状态 ===')
    console.log('订单ID:', testOrderId.value)
    
    const response = await getOrderDetail(Number(testOrderId.value))
    console.log('API响应:', response)
    
    const apiResponse = response.data
    console.log('API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      orderStatus.value = apiResponse.data
      ElMessage.success('查询成功')
    } else {
      orderStatus.value = { error: apiResponse?.message || '查询失败' }
      ElMessage.error(apiResponse?.message || '查询失败')
    }
  } catch (error) {
    console.error('查询失败:', error)
    orderStatus.value = { error: error.message }
    ElMessage.error('查询失败')
  } finally {
    checking.value = false
  }
}

const testPayment = async () => {
  if (!paymentTestOrderId.value) {
    ElMessage.warning('请输入订单ID')
    return
  }
  
  try {
    paying.value = true
    console.log('=== 测试支付 ===')
    console.log('订单ID:', paymentTestOrderId.value)
    console.log('支付方式:', paymentMethod.value)
    
    const response = await payOrder(Number(paymentTestOrderId.value), paymentMethod.value)
    console.log('支付API响应:', response)
    
    const apiResponse = response.data
    console.log('支付API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      paymentResult.value = { success: true, data: apiResponse }
      ElMessage.success('支付成功')
    } else {
      paymentResult.value = { success: false, error: apiResponse?.message || '支付失败' }
      ElMessage.error(apiResponse?.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    paymentResult.value = { success: false, error: error.message }
    ElMessage.error('支付失败')
  } finally {
    paying.value = false
  }
}

const checkAuthStatus = () => {
  userAuthStore.initializeAuth()
  
  authStatus.value = {
    isLoggedIn: userAuthStore.isLoggedIn,
    userInfo: userAuthStore.userInfo,
    accessToken: userAuthStore.accessToken ? '已设置' : '未设置',
    refreshToken: userAuthStore.refreshToken ? '已设置' : '未设置'
  }
  
  console.log('=== 用户认证状态 ===')
  console.log('认证状态:', authStatus.value)
}

const goToOrders = () => {
  router.push('/user/orders')
}

const goToPaymentTest = () => {
  router.push('/test/payment')
}

const clearCache = () => {
  localStorage.clear()
  sessionStorage.clear()
  ElMessage.success('缓存已清除')
}

const refreshPage = () => {
  window.location.reload()
}
</script>

<style scoped>
.payment-debug {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

.debug-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.debug-section h2 {
  color: #409eff;
  margin-bottom: 15px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 5px;
}

.issue-description {
  background: #fef0f0;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #f56c6c;
}

.issue-description p {
  margin: 0 0 10px 0;
}

.issue-description ul {
  margin: 10px 0 0 20px;
}

.test-tool {
  margin-bottom: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
}

.test-tool h3 {
  margin: 0 0 15px 0;
  color: #333;
}

.tool-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.result {
  background: #f0f9ff;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.result h4 {
  margin: 0 0 10px 0;
  color: #409eff;
}

.result pre {
  background: #fff;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
  margin: 0;
}

.solutions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.solution {
  padding: 15px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 4px solid #67c23a;
}

.solution h4 {
  margin: 0 0 10px 0;
  color: #67c23a;
}

.solution p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

.quick-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-debug {
    padding: 15px;
  }
  
  .tool-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .solutions {
    grid-template-columns: 1fr;
  }
  
  .quick-actions {
    flex-direction: column;
  }
}
</style> 