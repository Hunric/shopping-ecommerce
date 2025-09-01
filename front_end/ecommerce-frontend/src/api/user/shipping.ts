import request from '@/utils/request'

// 收货地址接口
export interface ShippingInfo {
  shippingId?: number
  userId?: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
  isDefault: boolean
}

// API响应格式
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  code?: number
}

// 创建收货地址
export const createShippingInfo = (data: ShippingInfo): Promise<ApiResponse<number>> => {
  return request({
    url: '/api/user/shipping',
    method: 'post',
    data
  })
}

// 获取收货地址列表
export const getShippingInfoList = (): Promise<ApiResponse<ShippingInfo[]>> => {
  return request({
    url: '/api/user/shipping',
    method: 'get'
  })
}

// 获取收货地址详情
export const getShippingInfo = (id: number): Promise<ApiResponse<ShippingInfo>> => {
  return request({
    url: `/api/user/shipping/${id}`,
    method: 'get'
  })
}

// 更新收货地址
export const updateShippingInfo = (id: number, data: ShippingInfo): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/user/shipping/${id}`,
    method: 'put',
    data
  })
}

// 删除收货地址
export const deleteShippingInfo = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/user/shipping/${id}`,
    method: 'delete'
  })
}

// 设置默认地址
export const setDefaultShippingInfo = (id: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/user/shipping/${id}/default`,
    method: 'post'
  })
}