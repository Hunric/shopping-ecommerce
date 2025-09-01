// 订单状态枚举
export enum OrderStatus {
  PENDING_PAYMENT = 'pending_payment',
  PENDING_SHIPMENT = 'pending_shipment',
  PENDING_RECEIPT = 'pending_receipt',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
  REFUNDED = 'refunded'
}

// 支付方式枚举
export enum PaymentMethod {
  ALIPAY = 'alipay',
  WECHAT = 'wechat',
  CREDIT_CARD = 'credit_card',
  COD = 'cod'
}

// 订单项类型
export interface OrderItem {
  skuId: number;
  productId: number;
  productName: string;
  skuName: string;
  price: number;
  quantity: number;
  imageUrl: string;
  attributes?: Record<string, string>;
}

// 订单信息类型
export interface OrderInfo {
  orderId: number;
  orderNo: string;
  userId: number;
  storeId: number;
  shippingId: number;
  totalAmount: number;
  actualAmount: number;
  discountAmount: number;
  shippingFee: number;
  orderStatus: OrderStatus;
  paymentMethod?: PaymentMethod;
  orderNote?: string;
  orderItems: OrderItem[];
  createTime: string;
  payTime?: string;
  shipTime?: string;
  completeTime?: string;
  cancelTime?: string;
  updateTime: string;
  
  // 扩展字段 - 前端展示用
  userName?: string;
  storeName?: string;
  shippingInfo?: {
    receiverName: string;
    receiverPhone: string;
    address: string;
  };
  statusText?: string;
  paymentMethodText?: string;
}

// 订单查询参数
export interface OrderQueryParams {
  pageNum?: number;
  pageSize?: number;
  orderNo?: string;
  orderStatus?: OrderStatus;
  startDate?: string;
  endDate?: string;
  userId?: number;
}

// 订单状态更新参数
export interface OrderStatusUpdate {
  orderId: number | string;
  orderStatus: OrderStatus;
  remark?: string;
}

// 订单统计数据
export interface OrderStatistics {
  totalOrders: number;
  pendingPayment: number;
  pendingShipment: number;
  pendingReceipt: number;
  completed: number;
  todayOrders: number;
  todaySales: number;
} 