import request from '@/utils/request'

// 商品接口
export interface Product {
  productId: number
  storeId: number
  categoryId: number
  productName: string
  productCode?: string
  description?: string
  price: number
  originalPrice?: number
  stock: number
  minStock?: number
  status: 'DRAFT' | 'ON_SHELF' | 'OFF_SHELF' | 'SOLD_OUT'
  mainImage?: string
  images?: string[]
  specifications?: ProductSpecification[]
  attributes?: ProductAttribute[]
  tags?: string[]
  weight?: number
  dimensions?: string
  brand?: string
  model?: string
  createTime: string
  updateTime: string
}

// 商品规格
export interface ProductSpecification {
  specId: number
  specName: string
  specValue: string
  price?: number
  stock?: number
  sku?: string
}

// 商品属性
export interface ProductAttribute {
  attributeId: number
  attributeName: string
  attributeValue: string
}

// 创建商品请求
export interface CreateProductRequest {
  storeId: number
  categoryId: number
  productName: string
  productCode?: string
  description?: string
  price: number
  originalPrice?: number
  stock: number
  minStock?: number
  mainImage?: string
  images?: string[]
  specifications?: Omit<ProductSpecification, 'specId'>[]
  attributes?: Omit<ProductAttribute, 'attributeId'>[]
  tags?: string[]
  weight?: number
  dimensions?: string
  brand?: string
  model?: string
}

// 更新商品请求
export interface UpdateProductRequest {
  productName?: string
  productCode?: string
  description?: string
  price?: number
  originalPrice?: number
  stock?: number
  minStock?: number
  status?: 'DRAFT' | 'ON_SHELF' | 'OFF_SHELF' | 'SOLD_OUT'
  mainImage?: string
  images?: string[]
  specifications?: Omit<ProductSpecification, 'specId'>[]
  attributes?: Omit<ProductAttribute, 'attributeId'>[]
  tags?: string[]
  weight?: number
  dimensions?: string
  brand?: string
  model?: string
}

// 商品查询参数
export interface ProductQueryParams {
  storeId?: number
  categoryId?: number
  keyword?: string
  status?: string
  minPrice?: number
  maxPrice?: number
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: 'ASC' | 'DESC'
}

// 批量操作请求
export interface BatchProductRequest {
  productIds: number[]
  action: 'ON_SHELF' | 'OFF_SHELF' | 'DELETE'
}

// API响应类型
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  page: number
  size: number
}

/**
 * 获取商品列表
 */
export const getProducts = async (params: ProductQueryParams): Promise<ApiResponse<PageResponse<Product>>> => {
  const response = await request.get('/api/merchant/product', { params })
  return response.data
}

/**
 * 获取店铺商品列表
 */
export const getProductsByStore = async (storeId: number, params?: Omit<ProductQueryParams, 'storeId'>): Promise<ApiResponse<PageResponse<Product>>> => {
  const response = await request.get(`/api/merchant/product/store/${storeId}`, { params })
  return response.data
}

/**
 * 获取分类商品列表
 */
export const getProductsByCategory = async (categoryId: number, params?: Omit<ProductQueryParams, 'categoryId'>): Promise<ApiResponse<PageResponse<Product>>> => {
  const response = await request.get(`/api/merchant/product/category/${categoryId}`, { params })
  return response.data
}

/**
 * 获取商品详情
 */
export const getProductById = async (productId: number): Promise<ApiResponse<Product>> => {
  const response = await request.get(`/api/merchant/product/${productId}`)
  return response.data
}

/**
 * 创建商品
 */
export const createProduct = async (data: CreateProductRequest): Promise<ApiResponse<Product>> => {
  const response = await request.post('/api/merchant/product', data)
  return response.data
}

/**
 * 更新商品
 */
export const updateProduct = async (productId: number, data: UpdateProductRequest): Promise<ApiResponse<Product>> => {
  const response = await request.put(`/api/merchant/product/${productId}`, data)
  return response.data
}

/**
 * 删除商品
 */
export const deleteProduct = async (productId: number): Promise<ApiResponse<string>> => {
  const response = await request.delete(`/api/merchant/product/${productId}`)
  return response.data
}

/**
 * 批量操作商品
 */
export const batchOperateProducts = async (data: BatchProductRequest): Promise<ApiResponse<string>> => {
  const response = await request.post('/api/merchant/product/batch', data)
  return response.data
}

/**
 * 上架商品
 */
export const putProductOnShelf = async (productId: number): Promise<ApiResponse<Product>> => {
  const response = await request.put(`/api/merchant/product/${productId}/on-shelf`)
  return response.data
}

/**
 * 下架商品
 */
export const putProductOffShelf = async (productId: number): Promise<ApiResponse<Product>> => {
  const response = await request.put(`/api/merchant/product/${productId}/off-shelf`)
  return response.data
}

/**
 * 复制商品
 */
export const copyProduct = async (productId: number): Promise<ApiResponse<Product>> => {
  const response = await request.post(`/api/merchant/product/${productId}/copy`)
  return response.data
}

/**
 * 获取商品统计信息
 */
export const getProductStats = async (storeId: number): Promise<ApiResponse<{
  totalProducts: number
  onShelfProducts: number
  offShelfProducts: number
  draftProducts: number
  soldOutProducts: number
  lowStockProducts: number
}>> => {
  const response = await request.get(`/api/merchant/product/stats/${storeId}`)
  return response.data
}

// 商品状态选项
export const PRODUCT_STATUS_OPTIONS = [
  { label: '草稿', value: 'DRAFT', type: 'info' },
  { label: '在售', value: 'ON_SHELF', type: 'success' },
  { label: '下架', value: 'OFF_SHELF', type: 'warning' },
  { label: '售罄', value: 'SOLD_OUT', type: 'danger' }
]

// 获取状态标签
export const getProductStatusLabel = (status: string): string => {
  const option = PRODUCT_STATUS_OPTIONS.find(opt => opt.value === status)
  return option ? option.label : status
}

// 获取状态类型
export const getProductStatusType = (status: string): string => {
  const option = PRODUCT_STATUS_OPTIONS.find(opt => opt.value === status)
  return option ? option.type : 'info'
} 