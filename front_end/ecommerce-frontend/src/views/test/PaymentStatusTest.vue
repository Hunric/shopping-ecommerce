<template>
  <div class="payment-status-test">
    <h1>支付状态更新测试</h1>
    
    <div class="test-section">
      <h2>问题修复验证</h2>
      <div class="fix-description">
        <p><strong>修复内容：</strong>模拟支付成功后订单状态仍显示为待付款的问题</p>
        <p><strong>解决方案：</strong></p>
        <ul>
          <li>修改后端支付API，直接处理支付成功逻辑</li>
          <li>支付成功时更新订单状态为"待发货"</li>
          <li>记录支付时间和支付方式</li>
          <li>添加事务保证数据一致性</li>
        </ul>
      </div>
    </div>
    
    <div class="test-section">
      <h2>测试工具</h2>
      
      <div class="test-tool">
        <h3>1. 创建测试订单</h3>
        <div class="tool-content">
          <el-button @click="createTestOrder" type="primary" :loading="creating">
            创建测试订单
          </el-button>
          <span v-if="testOrderId" class="success-text">
            测试订单已创建，ID: {{ testOrderId }}
          </span>
        </div>
      </div>
      
      <div class="test-tool">
        <h3>2. 查看订单状态（支付前）</h3>
        <div class="tool-content">
          <el-input 
            v-model="checkOrderId" 
            placeholder="请输入订单ID"
            style="width: 200px; margin-right: 10px;"
          />
          <el-button @click="checkOrderStatus" type="info" :loading="checking">
            查询状态
          </el-button>
        </div>
        <div v-if="orderStatusBefore" class="result">
          <h4>支付前订单状态：</h4>
          <div class="status-info">
            <p><strong>订单状态：</strong> 
              <span :class="getStatusClass(orderStatusBefore.orderStatus)">
                {{ getStatusText(orderStatusBefore.orderStatus) }}
              </span>
            </p>
            <p><strong>支付方式：</strong> {{ orderStatusBefore.paymentMethod || '未设置' }}</p>
            <p><strong>支付时间：</strong> {{ orderStatusBefore.payTime || '未支付' }}</p>
            <p><strong>创建时间：</strong> {{ formatTime(orderStatusBefore.createTime) }}</p>
          </div>
        </div>
      </div>
      
      <div class="test-tool">
        <h3>3. 模拟支付</h3>
        <div class="tool-content">
          <el-input 
            v-model="payOrderId" 
            placeholder="请输入待支付订单ID"
            style="width: 200px; margin-right: 10px;"
          />
          <el-select v-model="paymentMethod" style="width: 120px; margin-right: 10px;">
            <el-option label="支付宝" value="alipay" />
            <el-option label="微信支付" value="wechat" />
            <el-option label="银行卡" value="bank" />
          </el-select>
          <el-button @click="processPayment" type="success" :loading="paying">
            执行支付
          </el-button>
        </div>
        <div v-if="paymentResult" class="result">
          <h4>支付结果：</h4>
          <div v-if="paymentResult.success" class="success-result">
            <p>✅ 支付成功！</p>
            <p>支付方式：{{ paymentResult.method }}</p>
            <p>处理时间：{{ paymentResult.time }}</p>
          </div>
          <div v-else class="error-result">
            <p>❌ 支付失败：{{ paymentResult.error }}</p>
          </div>
        </div>
      </div>
      
      <div class="test-tool">
        <h3>4. 查看订单状态（支付后）</h3>
        <div class="tool-content">
          <el-button @click="checkOrderStatusAfter" type="warning" :loading="checkingAfter">
            查询支付后状态
          </el-button>
        </div>
        <div v-if="orderStatusAfter" class="result">
          <h4>支付后订单状态：</h4>
          <div class="status-info">
            <p><strong>订单状态：</strong> 
              <span :class="getStatusClass(orderStatusAfter.orderStatus)">
                {{ getStatusText(orderStatusAfter.orderStatus) }}
              </span>
            </p>
            <p><strong>支付方式：</strong> {{ orderStatusAfter.paymentMethod || '未设置' }}</p>
            <p><strong>支付时间：</strong> {{ formatTime(orderStatusAfter.payTime) || '未支付' }}</p>
            <p><strong>更新时间：</strong> {{ formatTime(orderStatusAfter.updateTime) }}</p>
          </div>
          
          <div class="status-comparison" v-if="orderStatusBefore">
            <h5>状态对比：</h5>
            <div class="comparison-row">
              <span>订单状态：</span>
              <span class="before">{{ getStatusText(orderStatusBefore.orderStatus) }}</span>
              <span class="arrow">→</span>
              <span class="after">{{ getStatusText(orderStatusAfter.orderStatus) }}</span>
              <span v-if="orderStatusBefore.orderStatus !== orderStatusAfter.orderStatus" class="success-icon">✅</span>
              <span v-else class="error-icon">❌</span>
            </div>
            <div class="comparison-row">
              <span>支付方式：</span>
              <span class="before">{{ orderStatusBefore.paymentMethod || '未设置' }}</span>
              <span class="arrow">→</span>
              <span class="after">{{ orderStatusAfter.paymentMethod || '未设置' }}</span>
              <span v-if="orderStatusAfter.paymentMethod" class="success-icon">✅</span>
            </div>
            <div class="comparison-row">
              <span>支付时间：</span>
              <span class="before">{{ orderStatusBefore.payTime ? '已设置' : '未设置' }}</span>
              <span class="arrow">→</span>
              <span class="after">{{ orderStatusAfter.payTime ? '已设置' : '未设置' }}</span>
              <span v-if="orderStatusAfter.payTime" class="success-icon">✅</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>测试步骤</h2>
      <div class="test-steps">
        <div class="step" :class="{ completed: testOrderId }">
          <div class="step-number">1</div>
          <div class="step-content">
            <h4>创建测试订单</h4>
            <p>点击"创建测试订单"按钮，系统会自动创建一个待付款的订单</p>
          </div>
        </div>
        
        <div class="step" :class="{ completed: orderStatusBefore }">
          <div class="step-number">2</div>
          <div class="step-content">
            <h4>查看支付前状态</h4>
            <p>输入订单ID，查询订单当前状态，确认为"待付款"</p>
          </div>
        </div>
        
        <div class="step" :class="{ completed: paymentResult?.success }">
          <div class="step-number">3</div>
          <div class="step-content">
            <h4>执行模拟支付</h4>
            <p>选择支付方式，点击"执行支付"，模拟支付成功</p>
          </div>
        </div>
        
        <div class="step" :class="{ completed: orderStatusAfter && orderStatusAfter.orderStatus === 'pending_shipment' }">
          <div class="step-number">4</div>
          <div class="step-content">
            <h4>验证状态更新</h4>
            <p>查询支付后状态，确认订单状态已更新为"待发货"</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>快速操作</h2>
      <div class="quick-actions">
        <el-button @click="runFullTest" type="primary" :loading="runningFullTest">
          一键完整测试
        </el-button>
        <el-button @click="goToOrders" type="info">
          查看订单列表
        </el-button>
        <el-button @click="goToPaymentPage" type="success" :disabled="!testOrderId">
          前往支付页面
        </el-button>
        <el-button @click="resetTest" type="warning">
          重置测试
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderDetail, payOrder, createOrder } from '@/api/user/order'
import { useUserAuthStore } from '@/store/modules/userAuth'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const testOrderId = ref('')
const checkOrderId = ref('')
const payOrderId = ref('')
const paymentMethod = ref('alipay')

const creating = ref(false)
const checking = ref(false)
const checkingAfter = ref(false)
const paying = ref(false)
const runningFullTest = ref(false)

const orderStatusBefore = ref(null)
const orderStatusAfter = ref(null)
const paymentResult = ref(null)

// 方法
const createTestOrder = async () => {
  try {
    creating.value = true
    
    // 确保用户已登录
    userAuthStore.initializeAuth()
    if (!userAuthStore.isLoggedIn) {
      ElMessage.error('请先登录')
      return
    }
    
    // 创建测试订单数据
    const testOrderData = {
      userId: userAuthStore.userInfo.userId,
      storeId: 1,
      shippingId: 1, // 假设有默认收货地址
      totalAmount: 99.99,
      actualAmount: 99.99,
      discountAmount: 0,
      shippingFee: 0,
      orderNote: '支付状态测试订单',
      orderItems: [
        {
          productId: 1,
          productName: '测试商品',
          productImage: '/default-product.png',
          skuId: 1,
          skuSpecs: '测试规格',
          productPrice: 99.99,
          quantity: 1,
          subtotal: 99.99
        }
      ]
    }
    
    console.log('创建测试订单:', testOrderData)
    const response = await createOrder(testOrderData)
    console.log('创建订单响应:', response)
    
    const apiResponse = response.data
    if (apiResponse.success) {
      testOrderId.value = apiResponse.data.toString()
      checkOrderId.value = testOrderId.value
      payOrderId.value = testOrderId.value
      ElMessage.success(`测试订单创建成功，ID: ${testOrderId.value}`)
    } else {
      ElMessage.error(apiResponse.message || '创建订单失败')
    }
  } catch (error) {
    console.error('创建测试订单失败:', error)
    ElMessage.error('创建测试订单失败')
  } finally {
    creating.value = false
  }
}

const checkOrderStatus = async () => {
  if (!checkOrderId.value) {
    ElMessage.warning('请输入订单ID')
    return
  }
  
  try {
    checking.value = true
    console.log('查询订单状态:', checkOrderId.value)
    
    const response = await getOrderDetail(Number(checkOrderId.value))
    console.log('订单状态响应:', response)
    
    const apiResponse = response.data
    if (apiResponse.success) {
      orderStatusBefore.value = apiResponse.data
      ElMessage.success('查询成功')
    } else {
      ElMessage.error(apiResponse.message || '查询失败')
    }
  } catch (error) {
    console.error('查询订单状态失败:', error)
    ElMessage.error('查询订单状态失败')
  } finally {
    checking.value = false
  }
}

const processPayment = async () => {
  if (!payOrderId.value) {
    ElMessage.warning('请输入订单ID')
    return
  }
  
  try {
    paying.value = true
    console.log('执行支付:', payOrderId.value, paymentMethod.value)
    
    const response = await payOrder(Number(payOrderId.value), paymentMethod.value)
    console.log('支付响应:', response)
    
    const apiResponse = response.data
    if (apiResponse.success) {
      paymentResult.value = {
        success: true,
        method: paymentMethod.value,
        time: new Date().toLocaleString()
      }
      ElMessage.success('支付成功！')
    } else {
      paymentResult.value = {
        success: false,
        error: apiResponse.message || '支付失败'
      }
      ElMessage.error(apiResponse.message || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    paymentResult.value = {
      success: false,
      error: error.message
    }
    ElMessage.error('支付失败')
  } finally {
    paying.value = false
  }
}

const checkOrderStatusAfter = async () => {
  const orderId = payOrderId.value || checkOrderId.value
  if (!orderId) {
    ElMessage.warning('请先执行支付或输入订单ID')
    return
  }
  
  try {
    checkingAfter.value = true
    console.log('查询支付后状态:', orderId)
    
    const response = await getOrderDetail(Number(orderId))
    console.log('支付后状态响应:', response)
    
    const apiResponse = response.data
    if (apiResponse.success) {
      orderStatusAfter.value = apiResponse.data
      ElMessage.success('查询成功')
    } else {
      ElMessage.error(apiResponse.message || '查询失败')
    }
  } catch (error) {
    console.error('查询支付后状态失败:', error)
    ElMessage.error('查询支付后状态失败')
  } finally {
    checkingAfter.value = false
  }
}

const runFullTest = async () => {
  try {
    runningFullTest.value = true
    
    // 1. 创建测试订单
    await createTestOrder()
    if (!testOrderId.value) return
    
    // 等待一下
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 2. 查看支付前状态
    await checkOrderStatus()
    
    // 等待一下
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 3. 执行支付
    await processPayment()
    
    // 等待一下
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 4. 查看支付后状态
    await checkOrderStatusAfter()
    
    ElMessage.success('完整测试执行完成！')
  } catch (error) {
    console.error('完整测试失败:', error)
    ElMessage.error('完整测试失败')
  } finally {
    runningFullTest.value = false
  }
}

const resetTest = () => {
  testOrderId.value = ''
  checkOrderId.value = ''
  payOrderId.value = ''
  orderStatusBefore.value = null
  orderStatusAfter.value = null
  paymentResult.value = null
  ElMessage.info('测试已重置')
}

const goToOrders = () => {
  router.push('/user/orders')
}

const goToPaymentPage = () => {
  if (testOrderId.value) {
    router.push(`/payment/${testOrderId.value}`)
  }
}

const getStatusText = (status: string) => {
  const statusMap = {
    'pending_payment': '待付款',
    'pending_shipment': '待发货',
    'pending_receipt': '待收货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const getStatusClass = (status: string) => {
  const classMap = {
    'pending_payment': 'status-warning',
    'pending_shipment': 'status-info',
    'pending_receipt': 'status-primary',
    'completed': 'status-success',
    'cancelled': 'status-danger'
  }
  return classMap[status] || 'status-default'
}

const formatTime = (timeString: string) => {
  if (!timeString) return ''
  return new Date(timeString).toLocaleString()
}
</script>

<style scoped>
.payment-status-test {
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

.test-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #409eff;
  margin-bottom: 15px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 5px;
}

.fix-description {
  background: #f0f9ff;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.fix-description p {
  margin: 0 0 10px 0;
}

.fix-description ul {
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

.success-text {
  color: #67c23a;
  font-weight: 600;
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

.status-info p {
  margin: 5px 0;
  font-size: 14px;
}

.status-warning { color: #e6a23c; }
.status-info { color: #409eff; }
.status-primary { color: #909399; }
.status-success { color: #67c23a; }
.status-danger { color: #f56c6c; }

.success-result {
  color: #67c23a;
}

.error-result {
  color: #f56c6c;
}

.status-comparison {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.status-comparison h5 {
  margin: 0 0 10px 0;
  color: #333;
}

.comparison-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  font-size: 14px;
}

.comparison-row span:first-child {
  min-width: 80px;
  font-weight: 600;
}

.before {
  color: #909399;
}

.arrow {
  color: #409eff;
  font-weight: bold;
}

.after {
  color: #333;
  font-weight: 600;
}

.success-icon {
  color: #67c23a;
  font-size: 16px;
}

.error-icon {
  color: #f56c6c;
  font-size: 16px;
}

.test-steps {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.step {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #ddd;
  transition: all 0.3s;
}

.step.completed {
  border-left-color: #67c23a;
  background: #f0f9ff;
}

.step-number {
  width: 30px;
  height: 30px;
  background: #ddd;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
  transition: all 0.3s;
}

.step.completed .step-number {
  background: #67c23a;
}

.step-content {
  flex: 1;
}

.step-content h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.step-content p {
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
  .payment-status-test {
    padding: 15px;
  }
  
  .tool-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .comparison-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .quick-actions {
    flex-direction: column;
  }
}
</style> 