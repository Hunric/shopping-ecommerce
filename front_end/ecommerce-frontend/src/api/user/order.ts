import request from '@/utils/request'

// 创建订单DTO
export interface CreateOrderDTO {
  userId: number
  storeId: number
  shippingId: number
  totalAmount: number
  actualAmount: number
  discountAmount?: number
  shippingFee?: number
  orderNote?: string
  orderItems: CreateOrderItemDTO[]
}

// 订单项DTO（用于创建订单）
export interface CreateOrderItemDTO {
  productId: number
  productName?: string
  productImage?: string
  skuId?: number
  skuSpecs?: string
  productPrice: number
  quantity: number
  subtotal: number
}

// 订单DTO
export interface OrderDTO {
  orderId: number
  orderNo: string
  userId: number
  totalAmount: number
  paymentAmount: number
  paymentMethod: string
  orderStatus: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark?: string
  createTime: string
  payTime?: string
  shipTime?: string
  receiveTime?: string
  cancelTime?: string
}

// 订单详情DTO
export interface OrderDetailDTO extends OrderDTO {
  orderItems: OrderItemDTO[]
}

// 订单项DTO（用于查询订单）
export interface OrderItemDTO {
  orderItemId: number
  orderId: number
  productId: number
  skuId: number
  productName: string
  productImage: string
  skuAttr: string
  skuPrice: number
  quantity: number
  subtotal: number
}

// 创建订单
export function createOrder(data: CreateOrderDTO) {
  return request({
    url: '/api/user/orders',
    method: 'post',
    data
  })
}

// 获取用户订单列表
export function getUserOrders(params: {
  pageNum?: number
  pageSize?: number
  orderNo?: string
  orderStatus?: string
}) {
  return request({
    url: '/api/user/orders',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(orderId: number) {
  return request({
    url: `/api/user/orders/${orderId}`,
    method: 'get'
  })
}

// 取消订单
export function cancelOrder(orderId: number) {
  return request({
    url: `/api/user/orders/${orderId}/cancel`,
    method: 'post'
  })
}

// 确认收货
export function confirmReceipt(orderId: number) {
  return request({
    url: `/api/user/orders/${orderId}/confirm-receipt`,
    method: 'post'
  })
}

// 支付订单
export function payOrder(orderId: number, paymentMethod: string) {
  return request({
    url: `/api/user/orders/${orderId}/pay`,
    method: 'post',
    params: { paymentMethod }
  })
}