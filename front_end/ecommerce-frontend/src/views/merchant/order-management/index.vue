<template>
  <div class="order-management">
    <el-card class="filter-container">
      <div class="filter-item">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="订单编号">
            <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="queryParams.orderStatus" placeholder="全部状态" clearable>
              <el-option
                v-for="item in orderStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="下单时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card class="order-statistics">
      <el-row :gutter="20">
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-title">今日订单</div>
            <div class="stat-value">{{ statistics.todayOrders }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-title">今日销售额</div>
            <div class="stat-value">¥{{ statistics.todaySales.toFixed(2) }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-title">待付款</div>
            <div class="stat-value">{{ statistics.pendingPayment }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-title">待发货</div>
            <div class="stat-value">{{ statistics.pendingShipment }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-title">待收货</div>
            <div class="stat-value">{{ statistics.pendingReceipt }}</div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="stat-item">
            <div class="stat-title">已完成</div>
            <div class="stat-value">{{ statistics.completed }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="order-table">
      <el-table
        v-loading="loading"
        :data="orderList"
        style="width: 100%"
        border
      >
        <el-table-column prop="orderNo" label="订单编号" width="180" />
        <el-table-column label="订单商品" min-width="300">
          <template #default="scope">
            <div v-for="(item, index) in scope.row.orderItems" :key="index" class="order-product">
              <el-image
                :src="item.imageUrl || 'https://via.placeholder.com/50'"
                style="width: 50px; height: 50px"
                fit="cover"
              />
              <div class="product-info">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-sku">{{ item.skuName }}</div>
                <div class="product-price">¥{{ item.price.toFixed(2) }} × {{ item.quantity }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实付金额" width="120">
          <template #default="scope">
            <span class="price">¥{{ scope.row.actualAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="收货信息" width="200">
          <template #default="scope">
            <div v-if="scope.row.shippingInfo">
              <div>{{ scope.row.shippingInfo.receiverName }}</div>
              <div>{{ scope.row.shippingInfo.receiverPhone }}</div>
              <div class="address-text">{{ scope.row.shippingInfo.address }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="120">
          <template #default="scope">
            <el-tag :type="getOrderStatusType(scope.row.orderStatus)">
              {{ getOrderStatusText(scope.row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="handleDetail(scope.row)">详情</el-button>
            <el-button 
              v-if="scope.row.orderStatus === OrderStatus.PENDING_SHIPMENT" 
              link 
              type="success" 
              @click="handleShip(scope.row)"
            >
              发货
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="快递公司" required>
          <el-select v-model="shipForm.shippingCompany" placeholder="请选择快递公司">
            <el-option label="顺丰速运" value="SF" />
            <el-option label="中通快递" value="ZTO" />
            <el-option label="圆通速递" value="YTO" />
            <el-option label="韵达快递" value="YD" />
            <el-option label="申通快递" value="STO" />
            <el-option label="京东物流" value="JD" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号" required>
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="shipDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmShip" :loading="shipSubmitting">
            确认发货
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getMerchantOrders, getOrderStatistics, shipOrder } from '@/api/order';
import { OrderStatus, type OrderInfo, type OrderQueryParams, type OrderStatistics } from '@/types/order';

const router = useRouter();
const loading = ref(false);
const orderList = ref<OrderInfo[]>([]);
const total = ref(0);
const dateRange = ref<[string, string] | null>(null);
const shipDialogVisible = ref(false);
const shipSubmitting = ref(false);
const currentOrder = ref<OrderInfo | null>(null);

// 订单查询参数
const queryParams = reactive<OrderQueryParams>({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  orderStatus: undefined,
  startDate: '',
  endDate: ''
});

// 订单统计数据
const statistics = reactive<OrderStatistics>({
  totalOrders: 0,
  pendingPayment: 0,
  pendingShipment: 0,
  pendingReceipt: 0,
  completed: 0,
  todayOrders: 0,
  todaySales: 0
});

// 发货表单
const shipForm = reactive({
  shippingCompany: '',
  trackingNumber: ''
});

// 订单状态选项
const orderStatusOptions = [
  { label: '待付款', value: OrderStatus.PENDING_PAYMENT },
  { label: '待发货', value: OrderStatus.PENDING_SHIPMENT },
  { label: '待收货', value: OrderStatus.PENDING_RECEIPT },
  { label: '已完成', value: OrderStatus.COMPLETED },
  { label: '已取消', value: OrderStatus.CANCELLED },
  { label: '已退款', value: OrderStatus.REFUNDED }
];

// 获取订单状态文本
const getOrderStatusText = (status: OrderStatus) => {
  const statusMap: Record<OrderStatus, string> = {
    [OrderStatus.PENDING_PAYMENT]: '待付款',
    [OrderStatus.PENDING_SHIPMENT]: '待发货',
    [OrderStatus.PENDING_RECEIPT]: '待收货',
    [OrderStatus.COMPLETED]: '已完成',
    [OrderStatus.CANCELLED]: '已取消',
    [OrderStatus.REFUNDED]: '已退款'
  };
  return statusMap[status] || '未知状态';
};

// 获取订单状态对应的Tag类型
const getOrderStatusType = (status: OrderStatus) => {
  const typeMap: Record<OrderStatus, string> = {
    [OrderStatus.PENDING_PAYMENT]: 'warning',
    [OrderStatus.PENDING_SHIPMENT]: 'primary',
    [OrderStatus.PENDING_RECEIPT]: 'info',
    [OrderStatus.COMPLETED]: 'success',
    [OrderStatus.CANCELLED]: 'danger',
    [OrderStatus.REFUNDED]: 'danger'
  };
  return typeMap[status] || '';
};

// 查询订单列表
const getOrderList = async () => {
  loading.value = true;
  try {
    // 处理日期范围
    if (dateRange.value) {
      queryParams.startDate = dateRange.value[0];
      queryParams.endDate = dateRange.value[1];
    } else {
      queryParams.startDate = undefined;
      queryParams.endDate = undefined;
    }

    const response = await getMerchantOrders(queryParams);
    orderList.value = response.data.list;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取订单列表失败', error);
    ElMessage.error('获取订单列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取订单统计数据
const getStatistics = async () => {
  try {
    const response = await getOrderStatistics();
    Object.assign(statistics, response.data);
  } catch (error) {
    console.error('获取订单统计数据失败', error);
  }
};

// 查询按钮点击
const handleQuery = () => {
  queryParams.pageNum = 1;
  getOrderList();
};

// 重置查询条件
const resetQuery = () => {
  queryParams.orderNo = '';
  queryParams.orderStatus = undefined;
  dateRange.value = null;
  queryParams.startDate = undefined;
  queryParams.endDate = undefined;
  queryParams.pageNum = 1;
  getOrderList();
};

// 页码变化
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val;
  getOrderList();
};

// 每页条数变化
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val;
  queryParams.pageNum = 1;
  getOrderList();
};

// 查看订单详情
const handleDetail = (row: OrderInfo) => {
  router.push(`/merchant/order-detail/${row.orderId}`);
};

// 处理发货
const handleShip = (row: OrderInfo) => {
  currentOrder.value = row;
  shipForm.shippingCompany = '';
  shipForm.trackingNumber = '';
  shipDialogVisible.value = true;
};

// 确认发货
const confirmShip = async () => {
  if (!shipForm.shippingCompany) {
    ElMessage.warning('请选择快递公司');
    return;
  }
  if (!shipForm.trackingNumber) {
    ElMessage.warning('请输入快递单号');
    return;
  }

  shipSubmitting.value = true;
  try {
    await shipOrder({
      orderId: currentOrder.value!.orderId,
      shippingCompany: shipForm.shippingCompany,
      trackingNumber: shipForm.trackingNumber
    });
    
    ElMessage.success('发货成功');
    shipDialogVisible.value = false;
    getOrderList();
    getStatistics();
  } catch (error) {
    console.error('发货失败', error);
    ElMessage.error('发货失败，请重试');
  } finally {
    shipSubmitting.value = false;
  }
};

onMounted(() => {
  getOrderList();
  getStatistics();
});
</script>

<style scoped>
.order-management {
  padding: 20px;
}

.filter-container,
.order-statistics,
.order-table {
  margin-bottom: 20px;
}

.order-statistics {
  background-color: #f8f8f8;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
}

.stat-title {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.order-product {
  display: flex;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-product:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.product-info {
  margin-left: 10px;
  flex: 1;
}

.product-name {
  font-weight: bold;
}

.product-sku {
  font-size: 12px;
  color: #909399;
  margin: 5px 0;
}

.product-price {
  color: #E6A23C;
}

.price {
  color: #F56C6C;
  font-weight: bold;
}

.address-text {
  font-size: 12px;
  color: #909399;
  white-space: normal;
  word-break: break-all;
}
</style> 