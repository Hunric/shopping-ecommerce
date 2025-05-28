import request from '@/utils/request'

// Dashboard统计数据接口
export interface DashboardStats {
  storeCount: number
  productCount: number
  orderCount: number
  totalRevenue: number
  pendingOrderCount: number
  shippedOrderCount: number
  monthlyRevenue: number
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
export const getDashboardStats = async (merchantId: number): Promise<ApiResponse<DashboardStats>> => {
  try {
    const response = await request.get(`/api/merchant/dashboard/stats/${merchantId}`)
    return response.data
  } catch (error) {
    console.error('获取Dashboard统计数据失败:', error)
    throw error
  }
} 