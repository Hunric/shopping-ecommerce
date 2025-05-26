import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

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

/**
 * 根据商家ID获取店铺列表
 */
export const getStoresByMerchantId = async (merchantId: number): Promise<ApiResponse<Store[]>> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/merchant/store/list/${merchantId}`)
    return response.data
  } catch (error) {
    console.error('获取店铺列表失败:', error)
    throw error
  }
}

/**
 * 根据店铺ID获取店铺信息
 */
export const getStoreById = async (storeId: number): Promise<ApiResponse<Store>> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/merchant/store/${storeId}`)
    return response.data
  } catch (error) {
    console.error('获取店铺信息失败:', error)
    throw error
  }
}

/**
 * 创建店铺
 */
export const createStore = async (storeData: CreateStoreRequest): Promise<ApiResponse<Store>> => {
  try {
    const response = await axios.post(`${API_BASE_URL}/api/merchant/store/create`, storeData)
    return response.data
  } catch (error) {
    console.error('创建店铺失败:', error)
    throw error
  }
}

/**
 * 更新店铺信息
 */
export const updateStore = async (storeId: number, storeData: UpdateStoreRequest): Promise<ApiResponse<Store>> => {
  try {
    const response = await axios.put(`${API_BASE_URL}/api/merchant/store/${storeId}`, storeData)
    return response.data
  } catch (error) {
    console.error('更新店铺失败:', error)
    throw error
  }
}

/**
 * 删除店铺
 */
export const deleteStore = async (storeId: number): Promise<ApiResponse<string>> => {
  try {
    const response = await axios.delete(`${API_BASE_URL}/api/merchant/store/${storeId}`)
    return response.data
  } catch (error) {
    console.error('删除店铺失败:', error)
    throw error
  }
} 