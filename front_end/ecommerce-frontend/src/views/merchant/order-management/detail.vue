<template>
  <div class="order-detail">
    <div class="page-header">
      <el-page-header @back="goBack">
        <template #content>
          <span class="text-large font-600 mr-3">订单详情</span>
        </template>
      </el-page-header>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton style="width: 100%" :rows="10" animated />
    </div>

    <template v-else>
      <el-card class="order-info-card">
        <template #header>
          <div class="card-header">
            <span>订单信息</span>
            <el-tag :type="getOrderStatusType(orderInfo.orderStatus)" class="ml-2">
              {{ getOrderStatusText(orderInfo.orderStatus) }}
            </el-tag>
          </div>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="订单编号">{{ orderInfo.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ orderInfo.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPaymentMethodText(orderInfo.paymentMethod) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="orderInfo.payTime">{{ orderInfo.payTime }}</el-descriptions-item>
          <el-descriptions-item label="发货时间" v-if="orderInfo.shipTime">{{ orderInfo.shipTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间" v-if="orderInfo.completeTime">{{ orderInfo.completeTime }}</el-descriptions-item>
          <el-descriptions-item label="订单备注" :span="3" v-if="orderInfo.orderNote">{{ orderInfo.orderNote }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="customer-info-card">
        <template #header>
          <div class="card-header">
            <span>客户信息</span>
          </div>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="客户账号">{{ orderInfo.userName }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ orderInfo.shippingInfo?.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ orderInfo.shippingInfo?.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ orderInfo.shippingInfo?.address }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="logistics-info-card" v-if="orderInfo.orderStatus !== OrderStatus.PENDING_PAYMENT">
        <template #header>
          <div class="card-header">
            <span>物流信息</span>
          </div>
        </template>
        <div v-if="orderInfo.orderStatus === OrderStatus.PENDING_SHIPMENT">
          <el-alert
            title="订单待发货"
            type="info"
            :closable="false"
            show-icon
          >
            <p>您可以点击下方按钮进行发货操作</p>
          </el-alert>
          <div class="action-container">
            <el-button type="primary" @click="handleShip">立即发货</el-button>
          </div>
        </div>
        <el-descriptions v-else-if="logisticsInfo" :column="1" border>
          <el-descriptions-item label="快递公司">{{ logisticsInfo.shippingCompany }}</el-descriptions-item>
          <el-descriptions-item label="快递单号">{{ logisticsInfo.trackingNumber }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ logisticsInfo.shipTime }}</el-descriptions-item>
          <el-descriptions-item label="物流状态">{{ getLogisticsStatusText(logisticsInfo.logisticsStatus) }}</el-descriptions-item>
          <el-descriptions-item label="物流轨迹" v-if="logisticsInfo.logisticsInfo && logisticsInfo.logisticsInfo.length > 0">
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in logisticsInfo.logisticsInfo"
                :key="index"
                :timestamp="item.time"
                :type="index === 0 ? 'primary' : ''"
              >
                {{ item.content }}
              </el-timeline-item>
            </el-timeline>
          </el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="暂无物流信息" />
      </el-card>

      <el-card class="product-info-card">
        <template #header>
          <div class="card-header">
            <span>商品信息</span>
          </div>
        </template>
        <el-table :data="orderInfo.orderItems" style="width: 100%" border>
          <el-table-column label="商品图片" width="100">
            <template #default="scope">
              <el-image
                :src="scope.row.imageUrl || 'https://via.placeholder.com/80'"
                style="width: 80px; height: 80px"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="productName" label="商品名称" min-width="200" />
          <el-table-column prop="skuName" label="规格" width="150" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="scope">
              <span>¥{{ scope.row.price.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="120">
            <template #default="scope">
              <span class="subtotal">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <div class="order-summary">
          <div class="summary-item">
            <span>商品总价：</span>
            <span>¥{{ orderInfo.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item">
            <span>运费：</span>
            <span>¥{{ orderInfo.shippingFee.toFixed(2) }}</span>
          </div>
          <div class="summary-item">
            <span>优惠金额：</span>
            <span>-¥{{ orderInfo.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-item total">
            <span>实付金额：</span>
            <span class="price">¥{{ orderInfo.actualAmount.toFixed(2) }}</span>
          </div>
        </div>
      </el-card>

      <el-card class="action-card">
        <template #header>
          <div class="card-header">
            <span>订单操作</span>
          </div>
        </template>
        <div class="action-buttons">
          <el-button 
            v-if="orderInfo.orderStatus === OrderStatus.PENDING_SHIPMENT" 
            type="primary" 
            @click="handleShip"
          >
            发货
          </el-button>
          <el-button 
            v-if="orderInfo.orderStatus === OrderStatus.PENDING_RECEIPT" 
            type="success" 
            @click="handleComplete"
          >
            标记完成
          </el-button>
          <el-button 
            v-if="orderInfo.orderStatus === OrderStatus.PENDING_PAYMENT" 
            type="danger" 
            @click="handleCancel"
          >
            取消订单
          </el-button>
          <el-button type="info" @click="handlePrint">打印订单</el-button>
        </div>
      </el-card>
    </template>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="订单编号">
          <span>{{ orderInfo.orderNo }}</span>
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
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getOrderDetail, shipOrder, updateOrderStatus } from '@/api/order';
import { OrderStatus, PaymentMethod, type OrderInfo } from '@/types/order';

const route = useRoute();
const router = useRouter();
const orderId = ref<string>(route.params.id as string);
const loading = ref(true);
const orderInfo = ref<OrderInfo>({} as OrderInfo);
const logisticsInfo = ref<any>(null);
const shipDialogVisible = ref(false);
const shipSubmitting = ref(false);

// 发货表单
const shipForm = reactive({
  shippingCompany: '',
  trackingNumber: ''
});

// 获取订单详情
const getOrder = async () => {
  loading.value = true;
  try {
    const response = await getOrderDetail(orderId.value);
    orderInfo.value = response.data;
    
    // 如果订单已发货，获取物流信息
    if (orderInfo.value.orderStatus !== OrderStatus.PENDING_PAYMENT && 
        orderInfo.value.orderStatus !== OrderStatus.PENDING_SHIPMENT) {
      // 这里应该调用获取物流信息的API，暂时模拟数据
      logisticsInfo.value = {
        shippingCompany: '顺丰速运',
        trackingNumber: 'SF1234567890',
        shipTime: orderInfo.value.shipTime,
        logisticsStatus: 'in_transit',
        logisticsInfo: [
          {
            time: '2023-06-15 14:30:00',
            content: '快件已签收，签收人：张三，感谢使用顺丰快递'
          },
          {
            time: '2023-06-15 09:15:00',
            content: '快件已到达【北京朝阳区望京营业点】，正在派送中'
          },
          {
            time: '2023-06-14 20:30:00',
            content: '快件已到达【北京转运中心】'
          },
          {
            time: '2023-06-13 18:20:00',
            content: '快件已从【上海浦东转运中心】发出'
          },
          {
            time: '2023-06-13 15:40:00',
            content: '快件已到达【上海浦东转运中心】'
          },
          {
            time: '2023-06-13 12:30:00',
            content: '顺丰速运已收取快件'
          }
        ]
      };
    }
  } catch (error) {
    console.error('获取订单详情失败', error);
    ElMessage.error('获取订单详情失败');
  } finally {
    loading.value = false;
  }
};

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

// 获取支付方式文本
const getPaymentMethodText = (method?: PaymentMethod) => {
  if (!method) return '未支付';
  
  const methodMap: Record<PaymentMethod, string> = {
    [PaymentMethod.ALIPAY]: '支付宝',
    [PaymentMethod.WECHAT]: '微信支付',
    [PaymentMethod.CREDIT_CARD]: '信用卡',
    [PaymentMethod.COD]: '货到付款'
  };
  return methodMap[method] || '未知支付方式';
};

// 获取物流状态文本
const getLogisticsStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'not_shipped': '未发货',
    'waiting_pickup': '待揽收',
    'in_transit': '运输中',
    'delivered': '已送达',
    'rejected': '已拒收'
  };
  return statusMap[status] || '未知状态';
};

// 返回上一页
const goBack = () => {
  router.push('/merchant/order-management');
};

// 处理发货
const handleShip = () => {
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
      orderId: orderId.value,
      shippingCompany: shipForm.shippingCompany,
      trackingNumber: shipForm.trackingNumber
    });
    
    ElMessage.success('发货成功');
    shipDialogVisible.value = false;
    getOrder(); // 重新获取订单信息
  } catch (error) {
    console.error('发货失败', error);
    ElMessage.error('发货失败，请重试');
  } finally {
    shipSubmitting.value = false;
  }
};

// 标记订单完成
const handleComplete = () => {
  ElMessageBox.confirm('确认将订单标记为已完成？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateOrderStatus({
        orderId: orderId.value,
        orderStatus: OrderStatus.COMPLETED
      });
      ElMessage.success('操作成功');
      getOrder(); // 重新获取订单信息
    } catch (error) {
      console.error('操作失败', error);
      ElMessage.error('操作失败，请重试');
    }
  }).catch(() => {});
};

// 取消订单
const handleCancel = () => {
  ElMessageBox.confirm('确认取消该订单？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateOrderStatus({
        orderId: orderId.value,
        orderStatus: OrderStatus.CANCELLED
      });
      ElMessage.success('订单已取消');
      getOrder(); // 重新获取订单信息
    } catch (error) {
      console.error('取消失败', error);
      ElMessage.error('取消失败，请重试');
    }
  }).catch(() => {});
};

// 打印订单
const handlePrint = () => {
  window.print();
};

onMounted(() => {
  getOrder();
});
</script>

<style scoped>
.order-detail {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.order-info-card,
.customer-info-card,
.logistics-info-card,
.product-info-card,
.action-card {
  margin-bottom: 20px;
}

.loading-container {
  padding: 20px;
}

.order-summary {
  margin-top: 20px;
  text-align: right;
  padding-right: 20px;
}

.summary-item {
  margin-bottom: 10px;
}

.summary-item.total {
  font-weight: bold;
  font-size: 16px;
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.price {
  color: #F56C6C;
  font-size: 18px;
}

.subtotal {
  color: #E6A23C;
  font-weight: bold;
}

.action-container {
  margin-top: 20px;
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

@media print {
  .page-header,
  .action-card,
  .el-button {
    display: none !important;
  }
}
</style> 