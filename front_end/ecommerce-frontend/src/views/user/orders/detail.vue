<template>
  <div class="order-detail-container">
    <div class="detail-header">
      <div class="header-buttons">
        <button @click="goBack" class="back-btn">
          ← 返回订单列表
        </button>
        <BackToHomeButton :show-text="false" size="small" type="info" />
      </div>
      <h2>订单详情</h2>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="orderDetail" class="detail-content">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-info">
          <h3>订单状态</h3>
          <span :class="['status-tag', getStatusClass(orderDetail.orderStatus)]">
            {{ getStatusText(orderDetail.orderStatus) }}
          </span>
        </div>
        <div class="order-basic-info">
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ orderDetail.orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">下单时间：</span>
            <span class="value">{{ formatDate(orderDetail.createTime) }}</span>
          </div>
          <div v-if="orderDetail.payTime" class="info-item">
            <span class="label">支付时间：</span>
            <span class="value">{{ formatDate(orderDetail.payTime) }}</span>
          </div>
          <div v-if="orderDetail.shipTime" class="info-item">
            <span class="label">发货时间：</span>
            <span class="value">{{ formatDate(orderDetail.shipTime) }}</span>
          </div>
          <div v-if="orderDetail.receiveTime" class="info-item">
            <span class="label">收货时间：</span>
            <span class="value">{{ formatDate(orderDetail.receiveTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="products-section">
        <h3>商品信息</h3>
        <div class="product-list">
          <div 
            v-for="item in orderDetail.orderItems" 
            :key="item.orderItemId" 
            class="product-item"
          >
            <div class="product-image">
              <img :src="item.productImage || '/default-product.png'" :alt="item.productName" />
            </div>
            <div class="product-info">
              <h4>{{ item.productName }}</h4>
              <p class="product-attr">{{ item.skuSpecs }}</p>
              <div class="price-quantity">
                <span class="price">¥{{ (item.productPrice || 0).toFixed(2) }}</span>
                <span class="quantity">x{{ item.quantity || 0 }}</span>
              </div>
            </div>
            <div class="subtotal">
              ¥{{ (item.subtotal || 0).toFixed(2) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 收货信息 -->
      <div class="address-section">
        <h3>收货信息</h3>
        <div class="address-info">
          <div class="info-item">
            <span class="label">收货人：</span>
            <span class="value">{{ orderDetail.shippingInfo?.receiverName || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话：</span>
            <span class="value">{{ orderDetail.shippingInfo?.receiverPhone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">收货地址：</span>
            <span class="value">{{ getFullAddress(orderDetail.shippingInfo) || '未设置' }}</span>
          </div>
        </div>
      </div>

      <!-- 支付信息 -->
      <div class="payment-section">
        <h3>支付信息</h3>
        <div class="payment-info">
          <div class="info-item">
            <span class="label">支付方式：</span>
            <span class="value">{{ getPaymentMethodText(orderDetail.paymentMethod) }}</span>
          </div>
          <div class="info-item">
            <span class="label">商品总价：</span>
            <span class="value">¥{{ (orderDetail.totalAmount || 0).toFixed(2) }}</span>
          </div>
          <div class="info-item">
            <span class="label">实付金额：</span>
            <span class="value total-amount">¥{{ (orderDetail.actualAmount || 0).toFixed(2) }}</span>
          </div>
        </div>
      </div>

      <!-- 订单备注 -->
      <div v-if="orderDetail.orderNote" class="remark-section">
        <h3>订单备注</h3>
        <p class="remark-text">{{ orderDetail.orderNote }}</p>
      </div>

      <!-- 操作按钮 -->
      <div class="actions-section">
        <button 
          v-if="orderDetail.orderStatus === 'pending_payment'" 
          @click="payOrder"
          class="action-btn pay-btn"
        >
          立即支付
        </button>
        <button 
          v-if="orderDetail.orderStatus === 'pending_payment'" 
          @click="cancelOrder"
          class="action-btn cancel-btn"
        >
          取消订单
        </button>
        <button 
          v-if="orderDetail.orderStatus === 'pending_receipt'" 
          @click="confirmReceipt"
          class="action-btn confirm-btn"
        >
          确认收货
        </button>
      </div>
    </div>

    <div v-else class="error-state">
      <p>订单不存在或已被删除</p>
      <button @click="goBack" class="back-btn">返回订单列表</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getOrderDetail,
  cancelOrder as cancelOrderApi,
  confirmReceipt as confirmReceiptApi,
  payOrder as payOrderApi,
  type OrderDetailDTO
} from '@/api/user/order'
import { useUserAuthStore } from '@/store/modules/userAuth'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const route = useRoute()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const orderDetail = ref<OrderDetailDTO | null>(null)

// 方法
const loadOrderDetail = async (retryCount = 0) => {
  const orderId = Number(route.params.orderId)
  if (!orderId) {
    ElMessage.error('订单ID无效')
    goBack()
    return
  }

  try {
    console.log('=== 开始加载订单详情 ===')
    console.log('订单ID:', orderId)
    console.log('重试次数:', retryCount)
    
    loading.value = true
    const response: any = await getOrderDetail(orderId)
    console.log('订单详情API响应:', response)
    
    const apiResponse = response.data
    console.log('订单详情API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success && apiResponse.data) {
      // 修复响应式更新问题 - 直接赋值而不是用扩展运算符
      orderDetail.value = null // 先清空
      await nextTick() // 等待DOM更新
      orderDetail.value = apiResponse.data // 然后赋新值
      console.log('订单详情加载成功:', orderDetail.value)
    } else {
      const errorMessage = apiResponse?.message || '获取订单详情失败'
      
      // 如果是第一次加载失败，且可能是支付刚完成，尝试重试
      if (retryCount < 2 && errorMessage.includes('不存在')) {
        console.log('订单可能正在更新状态，1秒后重试...')
        setTimeout(() => {
          loadOrderDetail(retryCount + 1)
        }, 1000)
        return
      }
      
      ElMessage.error(errorMessage)
      console.error('获取订单详情失败:', errorMessage)
      orderDetail.value = null
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    
    // 网络错误也尝试重试
    if (retryCount < 2) {
      console.log('网络错误，1秒后重试...')
      setTimeout(() => {
        loadOrderDetail(retryCount + 1)
      }, 1000)
      return
    }
    
    ElMessage.error('获取订单详情失败')
    orderDetail.value = null
  } finally {
    loading.value = false
  }
}

const payOrder = () => {
  if (!orderDetail.value) return
  
  // 跳转到支付页面
  router.push(`/payment/${orderDetail.value.orderId}`)
}

const cancelOrder = async () => {
  if (!orderDetail.value) return

  try {
    await ElMessageBox.confirm(
      `确定要取消订单 ${orderDetail.value.orderNo} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '不取消',
        type: 'warning'
      }
    )

    console.log('=== 开始取消订单详情 ===')
    console.log('订单详情:', orderDetail.value)
    
    const response: any = await cancelOrderApi(orderDetail.value.orderId)
    console.log('取消订单详情API响应:', response)
    
    const apiResponse = response.data
    console.log('取消订单详情API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      ElMessage.success('订单已取消')
      await loadOrderDetail() // 刷新订单详情
    } else {
      const errorMessage = apiResponse?.message || '取消订单失败'
      ElMessage.error(errorMessage)
      console.error('取消订单失败:', errorMessage)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败，请重试')
    }
  }
}

const confirmReceipt = async () => {
  if (!orderDetail.value) return

  try {
    await ElMessageBox.confirm(
      `确认收到订单 ${orderDetail.value.orderNo} 的商品了吗？`,
      '确认收货',
      {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    console.log('=== 开始确认收货详情 ===')
    console.log('订单详情:', orderDetail.value)
    
    const response: any = await confirmReceiptApi(orderDetail.value.orderId)
    console.log('确认收货详情API响应:', response)
    
    const apiResponse = response.data
    console.log('确认收货详情API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      ElMessage.success('确认收货成功！')
      await loadOrderDetail() // 刷新订单详情
    } else {
      const errorMessage = apiResponse?.message || '确认收货失败'
      ElMessage.error(errorMessage)
      console.error('确认收货失败:', errorMessage)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error('确认收货失败，请重试')
    }
  }
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'pending_payment': '待付款',
    'pending_shipment': '待发货',
    'pending_receipt': '待收货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知状态'
}

const getStatusClass = (status: string) => {
  const classMap: Record<string, string> = {
    'pending_payment': 'warning',
    'pending_shipment': 'info',
    'pending_receipt': 'primary',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return classMap[status] || 'default'
}

const getPaymentMethodText = (method: string) => {
  const methodMap: Record<string, string> = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'bank': '银行卡'
  }
  return methodMap[method] || method
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const getFullAddress = (shippingInfo: any) => {
  if (!shippingInfo) return ''
  
  const parts = []
  if (shippingInfo.province) parts.push(shippingInfo.province)
  if (shippingInfo.city) parts.push(shippingInfo.city)
  if (shippingInfo.district) parts.push(shippingInfo.district)
  if (shippingInfo.detailAddress) parts.push(shippingInfo.detailAddress)
  
  return parts.join(' ')
}

const goBack = () => {
  router.push('/user/orders')
}

// 生命周期
onMounted(() => {
  // 初始化用户认证状态
  userAuthStore.initializeAuth()
  
  // 检查用户是否已登录
  if (!userAuthStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/user/login')
    return
  }
  
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.detail-header {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #409eff;
}

.header-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.back-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  color: #666;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #f5f5f5;
  color: #409eff;
}

.detail-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
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

.detail-content > div {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-content h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

/* 订单状态样式 */
.status-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 30px;
}

.status-info h3 {
  margin-bottom: 10px;
}

.status-tag {
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
}

.status-tag.warning {
  background: #fdf6ec;
  color: #e6a23c;
}

.status-tag.info {
  background: #f4f4f5;
  color: #909399;
}

.status-tag.primary {
  background: #ecf5ff;
  color: #409eff;
}

.status-tag.success {
  background: #f0f9ff;
  color: #67c23a;
}

.status-tag.danger {
  background: #fef0f0;
  color: #f56c6c;
}

.order-basic-info {
  flex: 1;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.label {
  width: 100px;
  color: #666;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
}

.total-amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

/* 商品信息样式 */
.product-list {
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.product-item:last-child {
  border-bottom: none;
}

.product-image {
  width: 80px;
  height: 80px;
  margin-right: 15px;
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
}

.product-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.product-attr {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.price-quantity {
  display: flex;
  align-items: center;
  gap: 15px;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.quantity {
  color: #666;
}

.subtotal {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
  margin-left: 20px;
}

/* 地址信息样式 */
.address-info .info-item {
  margin-bottom: 15px;
}

.address-info .label {
  width: 80px;
}

/* 支付信息样式 */
.payment-info .info-item {
  margin-bottom: 15px;
}

.payment-info .label {
  width: 80px;
}

/* 订单备注样式 */
.remark-text {
  margin: 0;
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
}

/* 操作按钮样式 */
.actions-section {
  display: flex;
  justify-content: center;
  gap: 15px;
  background: #f8f9fa !important;
}

.action-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
}

.pay-btn {
  background: #409eff;
  color: white;
}

.pay-btn:hover {
  background: #66b1ff;
}

.cancel-btn {
  background: #f56c6c;
  color: white;
}

.cancel-btn:hover {
  background: #f78989;
}

.confirm-btn {
  background: #67c23a;
  color: white;
}

.confirm-btn:hover {
  background: #85ce61;
}

.error-state {
  text-align: center;
  padding: 100px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.error-state p {
  margin-bottom: 20px;
  color: #666;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .order-detail-container {
    padding: 10px;
  }
  
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .status-section {
    flex-direction: column;
    gap: 20px;
  }
  
  .product-item {
    flex-direction: column;
    align-items: flex-start;
    text-align: left;
  }
  
  .product-image {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .subtotal {
    margin-left: 0;
    margin-top: 10px;
  }
  
  .info-item {
    flex-direction: column;
  }
  
  .label {
    width: auto;
    margin-bottom: 5px;
    font-weight: 600;
  }
  
  .actions-section {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
}
</style>