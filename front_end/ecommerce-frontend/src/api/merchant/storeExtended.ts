import request from '@/utils/request'

// 扩展店铺信息接口
export interface StoreExtended {
  storeId: number
  merchantId: number
  storeName: string
  storeLogo?: string
  storeDescription?: string
  category?: string
  status: 'open' | 'closed' | 'suspended'
  servicePromise?: string[]
  servicePhone?: string
  serviceEmail?: string
  businessHours?: string
  creditScore: number
  openTime: string
  createTime: string
  updateTime?: string
}

// 创建店铺扩展数据接口
export interface StoreExtendedCreateData {
  merchantId: number
  storeName: string
  storeLogo?: string
  storeDescription: string
  category: string
  status: 'open' | 'closed'
  servicePromise: string[]
  servicePhone?: string
  serviceEmail?: string
  businessHours: string
}

// 更新店铺扩展数据接口
export interface StoreExtendedUpdateData {
  storeName: string
  storeLogo?: string
  storeDescription: string
  category: string
  status: 'open' | 'closed' | 'suspended'
  servicePromise: string[]
  servicePhone?: string
  serviceEmail?: string
  businessHours: string
}

// API响应接口
export interface ApiResponse<T> {
  success: boolean
  message: string
  data?: T
}

/**
 * 根据商家ID获取店铺列表（扩展版）
 */
export const getStoresExtendedByMerchantId = async (merchantId: number): Promise<ApiResponse<StoreExtended[]>> => {
  const response = await request.get(`/api/merchant/store-extended/merchant/${merchantId}`)
  return response.data
}

/**
 * 根据店铺ID获取店铺信息（扩展版）
 */
export const getStoreExtendedById = async (storeId: number): Promise<ApiResponse<StoreExtended>> => {
  const response = await request.get(`/api/merchant/store-extended/${storeId}`)
  return response.data
}

/**
 * 创建店铺（扩展版）
 */
export const createStoreExtended = async (storeData: StoreExtendedCreateData): Promise<ApiResponse<StoreExtended>> => {
  const response = await request.post('/api/merchant/store-extended', storeData)
  return response.data
}

/**
 * 更新店铺信息（扩展版）
 */
export const updateStoreExtended = async (storeId: number, storeData: StoreExtendedUpdateData): Promise<ApiResponse<StoreExtended>> => {
  const response = await request.put(`/api/merchant/store-extended/${storeId}`, storeData)
  return response.data
}

/**
 * 删除店铺
 */
export const deleteStoreExtended = async (storeId: number): Promise<ApiResponse<string>> => {
  const response = await request.delete(`/api/merchant/store-extended/${storeId}`)
  return response.data
}

/**
 * 获取商家店铺数量
 */
export const getStoreExtendedCount = async (merchantId: number): Promise<ApiResponse<number>> => {
  const response = await request.get(`/api/merchant/store-extended/count/${merchantId}`)
  return response.data
} 