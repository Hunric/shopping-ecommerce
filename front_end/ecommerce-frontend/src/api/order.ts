import request from '@/utils/request';
import type { OrderInfo, OrderQueryParams, OrderStatusUpdate } from '@/types/order';

// 获取商家订单列表
export function getMerchantOrders(params: OrderQueryParams) {
  return request({
    url: '/api/merchant/orders',
    method: 'get',
    params
  });
}

// 获取订单详情
export function getOrderDetail(orderId: number | string) {
  return request({
    url: `/api/merchant/orders/${orderId}`,
    method: 'get'
  });
}

// 更新订单状态
export function updateOrderStatus(data: OrderStatusUpdate) {
  return request({
    url: `/api/merchant/orders/${data.orderId}/status`,
    method: 'put',
    data
  });
}

// 发货处理
export function shipOrder(data: { orderId: number | string, shippingCompany: string, trackingNumber: string }) {
  return request({
    url: `/api/merchant/orders/${data.orderId}/ship`,
    method: 'post',
    data
  });
}

// 订单统计数据
export function getOrderStatistics() {
  return request({
    url: '/api/merchant/orders/statistics',
    method: 'get'
  });
}