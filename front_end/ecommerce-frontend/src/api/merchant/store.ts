import request from '@/utils/request'
import type { AxiosResponse } from 'axios'

// 店铺信息接口
export interface Store {
  storeId: number
  merchantId: number
  storeName: string
  storeLogo?: string
  storeDescription?: string
  openTime: string
  status: string
  creditScore: number
}

// 创建店铺请求
export interface CreateStoreRequest {
  merchantId: number
  storeName: string
  storeLogo?: string
  storeDescription?: string
}

// 更新店铺请求
export interface UpdateStoreRequest {
  storeName?: string
  storeLogo?: string
  storeDescription?: string
  status?: string
}

// API响应格式
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// 店铺创建数据接口
export interface StoreCreateData {
  merchantId: number
  storeName: string
  storeLogo?: string
  storeDescription?: string
}

// 店铺更新数据接口
export interface StoreUpdateData {
  storeName?: string
  storeLogo?: string
  storeDescription?: string
  status?: string
}

/**
 * 获取商家的店铺列表
 */
export const getStoresByMerchantId = async (merchantId: number): Promise<AxiosResponse<ApiResponse<Store[]>>> => {
  try {
    const response = await request.get(`/api/merchant/store/list/${merchantId}`)
    return response
  } catch (error) {
    console.error('获取店铺列表失败:', error)
    throw error
  }
}

/**
 * 根据店铺ID获取店铺信息
 */
export const getStoreById = async (storeId: number): Promise<AxiosResponse<ApiResponse<Store>>> => {
  try {
    const response = await request.get(`/api/merchant/store/${storeId}`)
    return response
  } catch (error) {
    console.error('获取店铺信息失败:', error)
    throw error
  }
}

/**
 * 创建店铺
 */
export const createStore = async (storeData: StoreCreateData): Promise<AxiosResponse<ApiResponse<Store>>> => {
  try {
    const response = await request.post('/api/merchant/store/create', storeData)
    return response
  } catch (error) {
    console.error('创建店铺失败:', error)
    throw error
  }
}

/**
 * 更新店铺信息
 */
export const updateStore = async (storeId: number, storeData: StoreUpdateData): Promise<AxiosResponse<ApiResponse<Store>>> => {
  try {
    const response = await request.put(`/api/merchant/store/${storeId}`, storeData)
    return response
  } catch (error) {
    console.error('更新店铺失败:', error)
    throw error
  }
}

/**
 * 删除店铺
 */
export const deleteStore = async (storeId: number): Promise<AxiosResponse<ApiResponse<string>>> => {
  try {
    const response = await request.delete(`/api/merchant/store/${storeId}`)
    return response
  } catch (error) {
    console.error('删除店铺失败:', error)
    throw error
  }
}

/**
 * 获取商家店铺数量
 */
export const getStoreCount = async (merchantId: number): Promise<AxiosResponse<ApiResponse<number>>> => {
  try {
    const response = await request.get(`/api/merchant/store/count/${merchantId}`)
    return response
  } catch (error) {
    console.error('获取店铺数量失败:', error)
    throw error
  }
} 