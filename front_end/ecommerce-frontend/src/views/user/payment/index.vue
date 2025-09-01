<template>
  <div class="payment-container">
    <!-- 支付页面头部 -->
    <div class="payment-header">
      <div class="header-content">
        <div class="header-left">
          <h2>订单支付</h2>
          <div class="order-info">
            <span>订单号：{{ orderInfo.orderNo }}</span>
            <span class="amount">应付金额：<strong>¥{{ orderInfo.actualAmount?.toFixed(2) }}</strong></span>
          </div>
        </div>
        <BackToHomeButton size="small" type="info" />
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="orderInfo.orderId" class="payment-content">
      <!-- 订单详情 -->
      <div class="order-section">
        <h3>订单详情</h3>
        <div class="order-items">
          <div v-for="item in orderInfo.orderItems" :key="item.productId" class="order-item">
            <img :src="item.productImage || '/default-product.png'" :alt="item.productName" class="item-image">
            <div class="item-info">
              <h4>{{ item.productName }}</h4>
              <p class="item-specs">{{ item.skuSpecs }}</p>
              <div class="item-price">
                <span>¥{{ item.productPrice?.toFixed(2) }}</span>
                <span class="quantity">x{{ item.quantity }}</span>
              </div>
            </div>
            <div class="item-total">
              ¥{{ item.subtotal?.toFixed(2) }}
            </div>
          </div>
        </div>
        
        <div class="order-summary">
          <div class="summary-row">
            <span>商品总价：</span>
            <span>¥{{ orderInfo.totalAmount?.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费：</span>
            <span>¥{{ orderInfo.shippingFee?.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span>应付总额：</span>
            <span class="total-amount">¥{{ orderInfo.actualAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-method-section">
        <h3>选择支付方式</h3>
        <div class="payment-methods">
          <div 
            class="payment-option" 
            :class="{ active: selectedPaymentMethod === 'alipay' }"
            @click="selectPaymentMethod('alipay')"
          >
            <div class="payment-icon alipay-icon">
              <span class="fallback-icon">💰</span>
            </div>
            <div class="payment-info">
              <h4>支付宝</h4>
              <p>推荐有支付宝账户的用户使用</p>
            </div>
            <div class="payment-radio">
              <input type="radio" :checked="selectedPaymentMethod === 'alipay'" readonly>
            </div>
          </div>

          <div 
            class="payment-option" 
            :class="{ active: selectedPaymentMethod === 'wechat' }"
            @click="selectPaymentMethod('wechat')"
          >
            <div class="payment-icon wechat-icon">
              <span class="fallback-icon">💚</span>
            </div>
            <div class="payment-info">
              <h4>微信支付</h4>
              <p>推荐有微信账户的用户使用</p>
            </div>
            <div class="payment-radio">
              <input type="radio" :checked="selectedPaymentMethod === 'wechat'" readonly>
            </div>
          </div>

          <div 
            class="payment-option" 
            :class="{ active: selectedPaymentMethod === 'bank' }"
            @click="selectPaymentMethod('bank')"
          >
            <div class="payment-icon bank-icon">
              <span class="fallback-icon">🏦</span>
            </div>
            <div class="payment-info">
              <h4>银行卡支付</h4>
              <p>支持各大银行储蓄卡及信用卡</p>
            </div>
            <div class="payment-radio">
              <input type="radio" :checked="selectedPaymentMethod === 'bank'" readonly>
            </div>
          </div>
        </div>
      </div>

      <!-- 支付按钮 -->
      <div class="payment-actions">
        <button @click="goBack" class="back-btn">返回订单</button>
        <button 
          @click="proceedPayment" 
          :disabled="!selectedPaymentMethod || paying"
          class="pay-btn"
        >
          {{ paying ? '支付中...' : `立即支付 ¥${orderInfo.actualAmount?.toFixed(2)}` }}
        </button>
      </div>
    </div>

    <!-- 支付处理弹窗 -->
    <el-dialog
      v-model="paymentDialogVisible"
      :title="getPaymentDialogTitle()"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >
      <div class="payment-dialog-content">
        <!-- 支付宝支付 -->
        <div v-if="selectedPaymentMethod === 'alipay'" class="alipay-payment">
          <div class="qr-code-container">
            <div class="qr-code">
              <div class="qr-placeholder">
                <div class="qr-grid">
                  <div v-for="i in 25" :key="i" class="qr-dot" :class="{ active: Math.random() > 0.3 }"></div>
                </div>
              </div>
            </div>
            <p class="qr-tip">请使用支付宝扫描二维码完成支付</p>
          </div>
          <div class="payment-amount">
            <span>支付金额：¥{{ orderInfo.actualAmount?.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 微信支付 -->
        <div v-if="selectedPaymentMethod === 'wechat'" class="wechat-payment">
          <div class="qr-code-container">
            <div class="qr-code wechat-qr">
              <div class="qr-placeholder">
                <div class="qr-grid">
                  <div v-for="i in 25" :key="i" class="qr-dot" :class="{ active: Math.random() > 0.4 }"></div>
                </div>
              </div>
            </div>
            <p class="qr-tip">请使用微信扫描二维码完成支付</p>
          </div>
          <div class="payment-amount">
            <span>支付金额：¥{{ orderInfo.actualAmount?.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 银行卡支付 -->
        <div v-if="selectedPaymentMethod === 'bank'" class="bank-payment">
          <div class="bank-form">
            <div class="form-group">
              <label>银行卡号</label>
              <input 
                v-model="bankForm.cardNumber" 
                type="text" 
                placeholder="请输入银行卡号"
                maxlength="19"
                @input="formatCardNumber"
              >
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>有效期</label>
                <input 
                  v-model="bankForm.expiryDate" 
                  type="text" 
                  placeholder="MM/YY"
                  maxlength="5"
                  @input="formatExpiryDate"
                >
              </div>
              <div class="form-group">
                <label>CVV</label>
                <input 
                  v-model="bankForm.cvv" 
                  type="text" 
                  placeholder="CVV"
                  maxlength="3"
                >
              </div>
            </div>
            <div class="form-group">
              <label>持卡人姓名</label>
              <input 
                v-model="bankForm.holderName" 
                type="text" 
                placeholder="请输入持卡人姓名"
              >
            </div>
          </div>
        </div>

        <!-- 支付状态 -->
        <div v-if="paymentStatus" class="payment-status">
          <div v-if="paymentStatus === 'processing'" class="status-processing">
            <div class="loading-spinner small"></div>
            <p>正在处理支付...</p>
          </div>
          <div v-if="paymentStatus === 'success'" class="status-success">
            <div class="success-icon">✓</div>
            <p>支付成功！</p>
          </div>
          <div v-if="paymentStatus === 'failed'" class="status-failed">
            <div class="failed-icon">✗</div>
            <p>支付失败，请重试</p>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!paymentStatus" @click="cancelPayment">取消支付</el-button>
          <el-button 
            v-if="selectedPaymentMethod === 'bank' && !paymentStatus" 
            type="primary" 
            @click="confirmBankPayment"
            :disabled="!isBankFormValid"
          >
            确认支付
          </el-button>
          <el-button 
            v-if="selectedPaymentMethod !== 'bank' && !paymentStatus" 
            type="primary" 
            @click="simulatePayment"
          >
            模拟支付成功
          </el-button>
          <el-button 
            v-if="paymentStatus === 'success'" 
            type="primary" 
            @click="goToOrderDetail"
          >
            查看订单
          </el-button>
          <el-button 
            v-if="paymentStatus === 'failed'" 
            type="primary" 
            @click="retryPayment"
          >
            重新支付
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 错误状态 -->
    <div v-if="!loading && !orderInfo.orderId" class="error-state">
      <div class="error-icon">⚠️</div>
      <h3>订单不存在</h3>
      <p>该订单可能已被删除或不存在</p>
      <button @click="goToOrders" class="back-btn">返回订单列表</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElDialog, ElButton } from 'element-plus'
import { getOrderDetail, payOrder as payOrderApi } from '@/api/user/order'
import { useUserAuthStore } from '@/store/modules/userAuth'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const route = useRoute()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const paying = ref(false)
const paymentDialogVisible = ref(false)
const paymentStatus = ref<'processing' | 'success' | 'failed' | null>(null)
const selectedPaymentMethod = ref<'alipay' | 'wechat' | 'bank'>('alipay')

// 订单信息
const orderInfo = ref<any>({})

// 银行卡表单
const bankForm = ref({
  cardNumber: '',
  expiryDate: '',
  cvv: '',
  holderName: ''
})

// 计算属性
const isBankFormValid = computed(() => {
  return bankForm.value.cardNumber.length >= 16 &&
         bankForm.value.expiryDate.length === 5 &&
         bankForm.value.cvv.length === 3 &&
         bankForm.value.holderName.trim().length > 0
})

// 方法
const loadOrderDetail = async () => {
  const orderId = route.params.orderId as string
  if (!orderId) {
    ElMessage.error('订单ID不能为空')
    return
  }

  try {
    loading.value = true
    const response = await getOrderDetail(Number(orderId))
    
    const apiResponse = response.data
    if (apiResponse.success) {
      orderInfo.value = apiResponse.data || {}
      
      // 检查订单状态
      if (orderInfo.value.orderStatus !== 'pending_payment') {
        if (orderInfo.value.orderStatus === 'pending_shipment' || 
            orderInfo.value.orderStatus === 'pending_receipt' || 
            orderInfo.value.orderStatus === 'completed') {
          ElMessage.success('订单已支付')
        } else {
          ElMessage.warning('该订单不需要支付')
        }
        router.push(`/user/orders/${orderId}`)
        return
      }
      
      // 设置默认支付方式
      if (orderInfo.value.paymentMethod) {
        selectedPaymentMethod.value = orderInfo.value.paymentMethod
      }
    } else {
      ElMessage.error(apiResponse.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const selectPaymentMethod = (method: 'alipay' | 'wechat' | 'bank') => {
  selectedPaymentMethod.value = method
}

const getPaymentDialogTitle = () => {
  const titles = {
    alipay: '支付宝支付',
    wechat: '微信支付',
    bank: '银行卡支付'
  }
  return titles[selectedPaymentMethod.value]
}

const proceedPayment = () => {
  if (!selectedPaymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  paymentDialogVisible.value = true
  paymentStatus.value = null
}

const cancelPayment = () => {
  paymentDialogVisible.value = false
  paymentStatus.value = null
  resetBankForm()
}

const simulatePayment = async () => {
  paymentStatus.value = 'processing'
  
  // 模拟支付处理时间
  await new Promise(resolve => setTimeout(resolve, 2000))
  
  // 模拟支付结果（90%成功率）
  const isSuccess = Math.random() > 0.1
  
  if (isSuccess) {
    await processPaymentSuccess()
  } else {
    paymentStatus.value = 'failed'
  }
}

const confirmBankPayment = async () => {
  if (!isBankFormValid.value) {
    ElMessage.warning('请填写完整的银行卡信息')
    return
  }
  
  paymentStatus.value = 'processing'
  
  // 模拟银行卡支付处理
  await new Promise(resolve => setTimeout(resolve, 3000))
  
  // 模拟支付结果（95%成功率）
  const isSuccess = Math.random() > 0.05
  
  if (isSuccess) {
    await processPaymentSuccess()
  } else {
    paymentStatus.value = 'failed'
  }
}

const processPaymentSuccess = async () => {
  try {
    console.log('=== 开始处理支付成功 ===')
    console.log('订单ID:', orderInfo.value.orderId)
    console.log('支付方式:', selectedPaymentMethod.value)
    
    // 调用后端支付API
    const response = await payOrderApi(orderInfo.value.orderId, selectedPaymentMethod.value)
    console.log('支付API响应:', response)
    
    const apiResponse = response.data
    console.log('支付API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      paymentStatus.value = 'success'
      ElMessage.success('支付成功！')
      
      // 更新本地订单状态
      if (orderInfo.value) {
        orderInfo.value.orderStatus = 'pending_shipment'
        orderInfo.value.paymentMethod = selectedPaymentMethod.value
        orderInfo.value.payTime = new Date().toISOString()
      }
      
      console.log('支付成功，订单状态已更新')
    } else {
      paymentStatus.value = 'failed'
      const errorMessage = apiResponse?.message || '支付失败'
      ElMessage.error(errorMessage)
      console.error('支付失败:', errorMessage)
    }
  } catch (error) {
    console.error('支付处理失败:', error)
    paymentStatus.value = 'failed'
    ElMessage.error('支付处理失败')
  }
}

const retryPayment = () => {
  paymentStatus.value = null
  resetBankForm()
}

const resetBankForm = () => {
  bankForm.value = {
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    holderName: ''
  }
}

const formatCardNumber = (event: Event) => {
  const input = event.target as HTMLInputElement
  let value = input.value.replace(/\D/g, '')
  value = value.replace(/(\d{4})(?=\d)/g, '$1 ')
  bankForm.value.cardNumber = value
}

const formatExpiryDate = (event: Event) => {
  const input = event.target as HTMLInputElement
  let value = input.value.replace(/\D/g, '')
  if (value.length >= 2) {
    value = value.substring(0, 2) + '/' + value.substring(2, 4)
  }
  bankForm.value.expiryDate = value
}

const goBack = () => {
  router.push(`/user/orders/${orderInfo.value.orderId}`)
}

const goToOrderDetail = async () => {
  paymentDialogVisible.value = false
  
  // 给后端一点时间更新订单状态
  await new Promise(resolve => setTimeout(resolve, 500))
  
  console.log('=== 跳转到订单详情 ===')
  console.log('订单ID:', orderInfo.value.orderId)
  
  router.push(`/user/orders/${orderInfo.value.orderId}`)
}

const goToOrders = () => {
  router.push('/user/orders')
}

// 生命周期
onMounted(() => {
  userAuthStore.initializeAuth()
  
  if (!userAuthStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  
  loadOrderDetail()
})
</script>

<style scoped>
.payment-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 70px);
}

.payment-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left {
  flex: 1;
}

.payment-header h2 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 24px;
}

.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
}

.order-info .amount {
  font-size: 18px;
  color: #f56c6c;
}

.order-info .amount strong {
  font-size: 20px;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
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

.loading-spinner.small {
  width: 20px;
  height: 20px;
  border-width: 2px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.payment-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-section, .payment-method-section {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-section h3, .payment-method-section h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.order-items {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.item-specs {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.item-price {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #f56c6c;
  font-weight: 600;
}

.quantity {
  color: #666;
  font-weight: normal;
}

.item-total {
  color: #f56c6c;
  font-size: 16px;
  font-weight: 600;
  margin-left: 20px;
}

.order-summary {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #666;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  border-top: 1px solid #eee;
  padding-top: 10px;
  margin-top: 10px;
}

.total-amount {
  color: #f56c6c;
  font-size: 20px;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-option {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.payment-option.active {
  border-color: #409eff;
  background: #f0f9ff;
}

.payment-icon {
  width: 50px;
  height: 50px;
  margin-right: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.fallback-icon {
  font-size: 24px;
}

.alipay-icon {
  background: #1677ff;
  color: white;
}

.wechat-icon {
  background: #07c160;
  color: white;
}

.bank-icon {
  background: #722ed1;
  color: white;
}

.payment-info {
  flex: 1;
}

.payment-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.payment-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.payment-radio {
  margin-left: 15px;
}

.payment-actions {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.back-btn {
  padding: 12px 30px;
  border: 1px solid #ddd;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
}

.back-btn:hover {
  border-color: #409eff;
  color: #409eff;
}

.pay-btn {
  padding: 12px 40px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
}

.pay-btn:hover:not(:disabled) {
  background: #66b1ff;
}

.pay-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}

/* 支付弹窗样式 */
.payment-dialog-content {
  text-align: center;
  padding: 20px 0;
}

.qr-code-container {
  margin-bottom: 20px;
}

.qr-code {
  width: 200px;
  height: 200px;
  margin: 0 auto 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
}

.qr-placeholder {
  width: 180px;
  height: 180px;
  background: #f8f9fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qr-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  width: 120px;
  height: 120px;
}

.qr-dot {
  background: #eee;
  border-radius: 2px;
  transition: all 0.3s;
}

.qr-dot.active {
  background: #333;
}

.wechat-qr .qr-dot.active {
  background: #07c160;
}

.qr-tip {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.payment-amount {
  font-size: 18px;
  color: #f56c6c;
  font-weight: 600;
}

/* 银行卡支付表单 */
.bank-form {
  text-align: left;
  max-width: 300px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 15px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #409eff;
}

/* 支付状态 */
.payment-status {
  margin: 20px 0;
}

.status-processing, .status-success, .status-failed {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.success-icon, .failed-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  font-weight: bold;
  color: white;
}

.success-icon {
  background: #67c23a;
}

.failed-icon {
  background: #f56c6c;
}

.status-processing p, .status-success p, .status-failed p {
  margin: 0;
  font-size: 16px;
  color: #333;
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.error-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.error-state h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.error-state p {
  margin: 0 0 30px 0;
  color: #666;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .payment-container {
    padding: 15px;
  }
  
  .order-info {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .order-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .item-total {
    margin-left: 0;
  }
  
  .payment-actions {
    flex-direction: column;
  }
  
  .form-row {
    flex-direction: column;
    gap: 15px;
  }
}
</style> 