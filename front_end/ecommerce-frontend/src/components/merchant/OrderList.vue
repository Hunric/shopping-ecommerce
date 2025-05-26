<template>
  <div class="order-list">
    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索订单号或客户姓名"
        style="width: 300px"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select
        v-model="storeFilter"
        placeholder="选择店铺"
        style="width: 200px; margin-left: 16px"
        clearable
      >
        <el-option label="时尚服装店" value="时尚服装店" />
        <el-option label="数码科技馆" value="数码科技馆" />
        <el-option label="美食天地" value="美食天地" />
      </el-select>
      
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="margin-left: 16px"
      />
      
      <el-button type="primary" @click="refreshOrderList" style="margin-left: 16px">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 订单表格 -->
    <div class="order-table" v-loading="loading">
      <div v-if="filteredOrders.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无订单数据" />
      </div>
      
      <el-table v-else :data="filteredOrders" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="160">
          <template #default="{ row }">
            <el-link type="primary" @click="viewOrderDetail(row)">
              {{ row.orderNo }}
            </el-link>
          </template>
        </el-table-column>
        
        <el-table-column prop="customerName" label="客户" width="100" />
        
        <el-table-column prop="storeName" label="店铺" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.storeName }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="订单状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewOrderDetail(row)">
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending_shipment'"
              size="small"
              type="primary"
              @click="shipOrder(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 'pending_payment'"
              size="small"
              type="warning"
              @click="cancelOrder(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailVisible"
      title="订单详情"
      width="800px"
      @close="orderDetailVisible = false"
    >
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <div class="detail-row">
            <span class="label">订单号：</span>
            <span>{{ currentOrder.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="label">下单时间：</span>
            <span>{{ formatDateTime(currentOrder.createTime) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">订单状态：</span>
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>客户信息</h4>
          <div class="detail-row">
            <span class="label">客户姓名：</span>
            <span>{{ currentOrder.customerName }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>金额信息</h4>
          <div class="detail-row">
            <span class="label">订单总额：</span>
            <span class="amount">¥{{ currentOrder.totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="orderDetailVisible = false">关闭</el-button>
        <el-button
          v-if="currentOrder?.status === 'pending_shipment'"
          type="primary"
          @click="shipOrder(currentOrder)"
        >
          发货
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, View
} from '@element-plus/icons-vue'

// 定义Props
interface Props {
  filter: string
  orders: any[]
}

const props = defineProps<Props>()

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const storeFilter = ref('')
const dateRange = ref<[Date, Date] | null>(null)
const orderDetailVisible = ref(false)
const currentOrder = ref<any>(null)

// 计算属性
const filteredOrders = computed(() => {
  let result = props.orders

  // 按关键词搜索
  if (searchKeyword.value) {
    result = result.filter(order =>
      order.orderNo.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      order.customerName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 按店铺筛选
  if (storeFilter.value) {
    result = result.filter(order => order.storeName === storeFilter.value)
  }

  // 按日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    result = result.filter(order => {
      const orderDate = new Date(order.createTime)
      return orderDate >= startDate && orderDate <= endDate
    })
  }

  return result
})

// 方法
const refreshOrderList = () => {
  ElMessage.success('订单列表已刷新')
}

const viewOrderDetail = (order: any) => {
  currentOrder.value = order
  orderDetailVisible.value = true
}

const shipOrder = async (order: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要发货订单"${order.orderNo}"吗？`,
      '确认发货',
      {
        confirmButtonText: '确定发货',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟API调用
    order.status = 'pending_receipt'
    ElMessage.success('发货成功')
    orderDetailVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

const cancelOrder = async (order: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单"${order.orderNo}"吗？此操作不可恢复！`,
      '确认取消',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    // 模拟API调用
    order.status = 'cancelled'
    ElMessage.success('订单已取消')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'pending_payment':
      return 'warning'
    case 'pending_shipment':
      return 'primary'
    case 'pending_receipt':
      return 'success'
    case 'completed':
      return 'success'
    case 'cancelled':
      return 'danger'
    default:
      return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'pending_payment':
      return '待付款'
    case 'pending_shipment':
      return '待发货'
    case 'pending_receipt':
      return '待收货'
    case 'completed':
      return '已完成'
    case 'cancelled':
      return '已取消'
    default:
      return '未知'
  }
}

const formatDateTime = (dateString: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 监听props变化
watch(() => props.orders, () => {
  // 当订单数据变化时，可以做一些处理
}, { deep: true })
</script>

<style scoped>
.order-list {
  background: white;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.order-table {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.order-detail {
  padding: 16px 0;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.detail-row .label {
  width: 100px;
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}
</style> 