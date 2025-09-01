import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 商品分类接口
export interface ProductCategory {
  categoryId: number
  storeId: number
  parentId?: number
  categoryName: string
  description?: string
  categoryLevel: number
  sortOrder: number
  isVisible: boolean
  iconUrl?: string
  createTime: string
  
  // 扩展字段
  children?: ProductCategory[]
  hasChildren?: boolean
  hasProducts?: boolean
  productCount?: number
  isLeaf?: boolean
  parentName?: string
}

// 创建分类请求
export interface CreateCategoryRequest {
  storeId: number
  parentId?: number
  categoryName: string
  description?: string
  sortOrder?: number
  isVisible?: boolean
  iconUrl?: string
}

// 更新分类请求
export interface UpdateCategoryRequest {
  categoryName?: string
  description?: string
  sortOrder?: number
  isVisible?: boolean
  iconUrl?: string
}

/**
 * 获取分类树
 */
export const getCategoryTree = async (storeId: number): Promise<ApiResponse<ProductCategory[]>> => {
  const response = await request.get(`/api/merchant/category/tree/${storeId}`)
  return response as any
}

/**
 * 获取顶级分类
 */
export const getTopCategories = async (storeId: number): Promise<ApiResponse<ProductCategory[]>> => {
  const response = await request.get(`/api/merchant/category/top/${storeId}`)
  return response as any
}

/**
 * 获取子分类
 */
export const getChildCategories = async (storeId: number, parentId: number): Promise<ApiResponse<ProductCategory[]>> => {
  const response = await request.get(`/api/merchant/category/children/${storeId}/${parentId}`)
  return response as any
}

/**
 * 获取叶子分类（可添加商品的分类）
 */
export const getLeafCategories = async (storeId: number): Promise<ApiResponse<ProductCategory[]>> => {
  const response = await request.get(`/api/merchant/category/leaf/${storeId}`)
  return response as any
}

/**
 * 获取分类详情
 */
export const getCategoryById = async (categoryId: number): Promise<ApiResponse<ProductCategory>> => {
  const response = await request.get(`/api/merchant/category/${categoryId}`)
  return response as any
}

/**
 * 创建分类
 */
export const createCategory = async (data: CreateCategoryRequest): Promise<ApiResponse<ProductCategory>> => {
  const response = await request.post('/api/merchant/category', data)
  return response as any
}

/**
 * 更新分类
 */
export const updateCategory = async (categoryId: number, data: Partial<ProductCategory>): Promise<ApiResponse<ProductCategory>> => {
  const response = await request.put(`/api/merchant/category/${categoryId}`, data)
  return response as any
}

/**
 * 删除分类
 */
export const deleteCategory = async (categoryId: number): Promise<ApiResponse<string>> => {
  const response = await request.delete(`/api/merchant/category/${categoryId}`)
  return response as any
}

/**
 * 移动分类
 */
export const moveCategory = async (categoryId: number, newParentId: number): Promise<ApiResponse<string>> => {
  const response = await request.put(`/api/merchant/category/${categoryId}/move`, null, {
    params: { newParentId }
  })
  return response as any
}

/**
 * 检查是否为叶子分类
 */
export const isLeafCategory = async (categoryId: number): Promise<ApiResponse<boolean>> => {
  const response = await request.get(`/api/merchant/category/${categoryId}/is-leaf`)
  return response as any
} 