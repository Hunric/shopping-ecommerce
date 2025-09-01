import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

// Dashboard统计数据接口
export interface DashboardStats {
  storeCount: number
  productCount: number
  orderCount: number
  totalRevenue: number | string  // 后端返回BigDecimal，可能是数字或字符串
  pendingOrderCount: number
  shippedOrderCount: number
  monthlyRevenue: number | string  // 后端返回BigDecimal，可能是数字或字符串
  monthlyOrderCount: number
}

// API响应格式
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

/**
 * 获取商家Dashboard统计数据
 */
export const getDashboardStats = async (merchantId: number): Promise<AxiosResponse<ApiResponse<DashboardStats>>> => {
  try {
    // 确保API路径与后端匹配
    const response = await request.get<ApiResponse<DashboardStats>>(`/api/merchant/dashboard/stats/${merchantId}`)
    return response
  } catch (error) {
    console.error('获取Dashboard统计数据失败:', error)
    throw error
  }
} 