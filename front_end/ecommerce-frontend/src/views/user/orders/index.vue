<template>
  <div class="orders-container">
    <div class="orders-header">
      <div class="header-content">
        <h2>我的订单</h2>
        <BackToHomeButton size="small" type="info" />
      </div>
    </div>

    <!-- 订单状态筛选 -->
    <div class="order-tabs">
      <button 
        v-for="tab in orderTabs" 
        :key="tab.value"
        @click="activeTab = tab.value"
        :class="['tab-btn', { active: activeTab === tab.value }]"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        type="text" 
        placeholder="搜索订单号"
        class="search-input"
        @keyup.enter="loadOrders"
      />
      <button @click="loadOrders" class="search-btn">搜索</button>
    </div>

    <!-- 调试按钮区域 -->
    <div class="debug-section" style="margin-bottom: 20px; padding: 10px; background: #f0f0f0; border-radius: 5px;">
      <h3>调试功能</h3>
      <el-button @click="testLoadOrders" type="primary" size="small">测试加载订单列表</el-button>
      <el-button @click="testCreateTestOrder" type="success" size="small">创建测试订单</el-button>
      <el-button @click="testCheckAuth" type="warning" size="small">检查认证状态</el-button>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="orderList.length === 0" class="empty-orders">
      <div class="empty-icon">📋</div>
      <p>暂无订单</p>
      <button @click="goShopping" class="go-shopping-btn">去购物</button>
    </div>

    <div v-else class="orders-content">
      <!-- 订单列表 -->
      <div class="order-list">
        <div 
          v-for="order in orderList" 
          :key="order.orderId" 
          class="order-item"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatDate(order.createTime) }}</span>
            </div>
            <div class="order-status">
              <span :class="['status-tag', getStatusClass(order.orderStatus)]">
                {{ getStatusText(order.orderStatus) }}
              </span>
            </div>
          </div>

          <div class="order-content" @click="viewOrderDetail(order.orderId)">
            <div class="order-amount">
              <span class="amount-label">订单金额：</span>
              <span class="amount-value">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="order-address">
              <span class="address-label">收货地址：</span>
              <span class="address-value">{{ order.receiverAddress }}</span>
            </div>
            <div class="order-receiver">
              <span class="receiver-label">收货人：</span>
              <span class="receiver-value">{{ order.receiverName }} {{ order.receiverPhone }}</span>
            </div>
          </div>

          <div class="order-actions">
            <button 
              v-if="order.orderStatus === 'pending_payment'" 
              @click.stop="payOrder(order)"
              class="action-btn pay-btn"
            >
              立即支付
            </button>
            <button 
              v-if="order.orderStatus === 'pending_payment'" 
              @click.stop="cancelOrder(order)"
              class="action-btn cancel-btn"
            >
              取消订单
            </button>
            <button 
              v-if="order.orderStatus === 'pending_receipt'" 
              @click.stop="confirmReceipt(order)"
              class="action-btn confirm-btn"
            >
              确认收货
            </button>
            <button 
              @click.stop="viewOrderDetail(order.orderId)"
              class="action-btn detail-btn"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="pagination">
        <button 
          @click="changePage(pageNum - 1)"
          :disabled="pageNum <= 1"
          class="page-btn"
        >
          上一页
        </button>
        <span class="page-info">
          第 {{ pageNum }} 页，共 {{ Math.ceil(total / pageSize) }} 页
        </span>
        <button 
          @click="changePage(pageNum + 1)"
          :disabled="pageNum >= Math.ceil(total / pageSize)"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserOrders,
  cancelOrder as cancelOrderApi,
  confirmReceipt as confirmReceiptApi,
  payOrder as payOrderApi,
  type OrderDTO
} from '@/api/user/order'
import { useUserAuthStore } from '@/store/modules/userAuth'
import BackToHomeButton from '@/components/common/BackToHomeButton.vue'

const router = useRouter()
const route = useRoute()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = ref(false)
const orderList = ref<OrderDTO[]>([])
const activeTab = ref('all')
const searchKeyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 订单状态标签页
const orderTabs = [
  { label: '全部', value: 'all' },
  { label: '待付款', value: 'pending_payment' },
  { label: '待发货', value: 'pending_shipment' },
  { label: '待收货', value: 'pending_receipt' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

// 计算属性
const currentStatus = computed(() => {
  return activeTab.value === 'all' ? undefined : activeTab.value
})

// 方法
const loadOrders = async () => {
  if (!userAuthStore.isLoggedIn) {
    ElMessage.error('请先登录')
    router.push('/user/login')
    return
  }

  try {
    console.log('=== 开始加载订单列表 ===')
    loading.value = true
    const response: any = await getUserOrders({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      orderNo: searchKeyword.value || undefined,
      orderStatus: currentStatus.value
    })
    
    console.log('订单列表API响应:', response)
    
    const apiResponse = response.data
    console.log('订单列表API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success && apiResponse.data) {
      // 修复响应式更新问题
      total.value = apiResponse.data.total || 0
      orderList.value = [] // 先清空数组
      
      // 然后添加新数据
      if (Array.isArray(apiResponse.data.list)) {
        orderList.value = apiResponse.data.list
      }
      
      console.log('订单列表加载成功:', orderList.value)
    } else {
      const errorMessage = apiResponse?.message || '获取订单列表失败'
      ElMessage.error(errorMessage)
      console.error('获取订单列表失败:', errorMessage)
      orderList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
    orderList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const changePage = (newPage: number) => {
  pageNum.value = newPage
  loadOrders()
}

const viewOrderDetail = (orderId: number) => {
  router.push(`/user/orders/${orderId}`)
}

const payOrder = (order: OrderDTO) => {
  // 跳转到支付页面
  router.push(`/payment/${order.orderId}`)
}

const cancelOrder = async (order: OrderDTO) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 ${order.orderNo} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '不取消',
        type: 'warning'
      }
    )

    console.log('=== 开始取消订单 ===')
    console.log('订单信息:', order)
    
    const response: any = await cancelOrderApi(order.orderId)
    console.log('取消订单API响应:', response)
    
    const apiResponse = response.data
    console.log('取消订单API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      ElMessage.success('订单已取消')
      await loadOrders() // 刷新订单列表
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

const confirmReceipt = async (order: OrderDTO) => {
  try {
    await ElMessageBox.confirm(
      `确认收到订单 ${order.orderNo} 的商品了吗？`,
      '确认收货',
      {
        confirmButtonText: '确认收货',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    console.log('=== 开始确认收货 ===')
    console.log('订单信息:', order)
    
    const response: any = await confirmReceiptApi(order.orderId)
    console.log('确认收货API响应:', response)
    
    const apiResponse = response.data
    console.log('确认收货API数据:', apiResponse)
    
    if (apiResponse && apiResponse.success) {
      ElMessage.success('确认收货成功！')
      await loadOrders() // 刷新订单列表
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

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const goShopping = () => {
  router.push('/')
}

// 监听标签页变化
const handleTabChange = () => {
  pageNum.value = 1
  loadOrders()
}

// 测试函数
const testLoadOrders = () => {
  console.log('=== 手动测试加载订单列表 ===')
  loadOrders()
}

const testCreateTestOrder = () => {
  console.log('=== 创建测试订单功能 ===')
  ElMessage.info('测试订单创建功能暂未实现，请通过购物车下单')
}

const testCheckAuth = () => {
  console.log('=== 检查认证状态 ===')
  console.log('用户认证状态:', userAuthStore.isLoggedIn)
  console.log('用户信息:', userAuthStore.userInfo)
  console.log('用户Token:', userAuthStore.token)
  ElMessage.info(`认证状态: ${userAuthStore.isLoggedIn ? '已登录' : '未登录'}`)
}

// 生命周期
onMounted(() => {
  // 如果路由参数中有订单ID，说明是从订单详情页返回的
  const orderId = route.params.orderId
  if (orderId) {
    // 可以根据需要设置默认状态或其他逻辑
  }
  
  loadOrders()
})

// 监听activeTab变化
watch(activeTab, handleTabChange)
</script>

<style scoped>
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.orders-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.orders-header h2 {
  margin: 0;
  color: #333;
  font-size: 24px;
}

.order-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.tab-btn {
  padding: 10px 20px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-btn:hover {
  color: #409eff;
}

.tab-btn.active {
  color: #409eff;
  border-bottom-color: #409eff;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  max-width: 300px;
  height: 40px;
  padding: 0 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-input:focus {
  outline: none;
  border-color: #409eff;
}

.search-btn {
  padding: 0 20px;
  height: 40px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.search-btn:hover {
  background: #66b1ff;
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

.empty-orders {
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-no {
  font-weight: 600;
  color: #333;
}

.order-time {
  color: #666;
  font-size: 14px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
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

.order-content {
  padding: 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.order-content:hover {
  background: #f8f9fa;
}

.order-content > div {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.order-content > div:last-child {
  margin-bottom: 0;
}

.amount-label,
.address-label,
.receiver-label {
  width: 80px;
  color: #666;
  font-size: 14px;
}

.amount-value {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

.address-value,
.receiver-value {
  color: #333;
  font-size: 14px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  background: #f8f9fa;
  border-top: 1px solid #eee;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.pay-btn {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.pay-btn:hover {
  background: #66b1ff;
}

.cancel-btn {
  background: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.cancel-btn:hover {
  background: #f78989;
}

.confirm-btn {
  background: #67c23a;
  color: white;
  border-color: #67c23a;
}

.confirm-btn:hover {
  background: #85ce61;
}

.detail-btn {
  background: white;
  color: #666;
}

.detail-btn:hover {
  background: #f5f5f5;
  color: #409eff;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
  padding: 20px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  color: #666;
  border-radius: 4px;
  cursor: pointer;
}

.page-btn:hover:not(:disabled) {
  background: #f5f5f5;
  color: #409eff;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .orders-container {
    padding: 10px;
  }
  
  .order-tabs {
    flex-wrap: wrap;
  }
  
  .tab-btn {
    padding: 8px 12px;
    font-size: 14px;
  }
  
  .search-bar {
    flex-direction: column;
  }
  
  .search-input {
    max-width: none;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .order-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .order-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
}
</style>