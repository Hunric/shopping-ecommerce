import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

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

// API响应接口
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  success: boolean
}

// 创建店铺扩展数据接口
export interface StoreExtendedCreateData {
  merchantId: number
  storeName: string
  storeLogo?: string
  storeDescription?: string
  category?: string
  servicePromise?: string[]
  servicePhone?: string
  serviceEmail?: string
  businessHours?: string
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

/**
 * 根据商家ID获取店铺列表（扩展版）
 */
export const getStoresExtendedByMerchantId = async (
  merchantId: number
): Promise<AxiosResponse<ApiResponse<StoreExtended[]>>> => {
  const response = await request.get<ApiResponse<StoreExtended[]>>(
    `/api/merchant/store-extended/merchant/${merchantId}`
  )
  return response
}

/**
 * 根据店铺ID获取店铺信息（扩展版）
 */
export const getStoreExtendedById = async (
  storeId: number
): Promise<AxiosResponse<ApiResponse<StoreExtended>>> => {
  const response = await request.get<ApiResponse<StoreExtended>>(
    `/api/merchant/store-extended/${storeId}`
  )
  return response
}

/**
 * 创建店铺（扩展版）
 */
export const createStoreExtended = async (
  storeData: StoreExtendedCreateData
): Promise<AxiosResponse<ApiResponse<StoreExtended>>> => {
  const response = await request.post<ApiResponse<StoreExtended>>(
    '/api/merchant/store-extended',
    storeData
  )
  return response
}

/**
 * 更新店铺信息（扩展版）
 */
export const updateStoreExtended = async (
  storeId: number,
  storeData: StoreExtendedUpdateData
): Promise<AxiosResponse<ApiResponse<StoreExtended>>> => {
  const response = await request.put<ApiResponse<StoreExtended>>(
    `/api/merchant/store-extended/${storeId}`,
    storeData
  )
  return response
}

/**
 * 删除店铺
 */
export const deleteStoreExtended = async (
  storeId: number
): Promise<AxiosResponse<ApiResponse<string>>> => {
  const response = await request.delete<ApiResponse<string>>(
    `/api/merchant/store-extended/${storeId}`
  )
  return response
}

/**
 * 获取商家店铺数量
 */
export const getStoreExtendedCount = async (
  merchantId: number
): Promise<AxiosResponse<ApiResponse<number>>> => {
  const response = await request.get<ApiResponse<number>>(
    `/api/merchant/store-extended/count/${merchantId}`
  )
  return response
}