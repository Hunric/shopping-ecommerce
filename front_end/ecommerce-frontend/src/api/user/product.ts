import request from '@/utils/request'

// 商品信息接口
export interface Product {
  spuId: number
  merchantId: number
  storeId: number
  categoryId: number
  spuName: string
  spuDescription?: string
  productImage?: string
  displayPrice: number
  basicAttributes: Record<string, any>
  nonBasicAttributes: Record<string, any>
  brandName?: string
  sellingPoint?: string
  unit: string
  status: string
  createTime?: string
  updateTime?: string
  storeName?: string
  categoryName?: string
}

// 商品分类接口
export interface Category {
  categoryId: number
  storeId: number
  parentId?: number
  categoryName: string
  description?: string
  categoryLevel: number
  sortOrder: number
  isVisible: boolean
  iconUrl?: string
  createTime?: string
}

// 商品查询参数
export interface ProductQueryParams {
  keyword?: string
  categoryId?: number
  storeId?: number
  minPrice?: number
  maxPrice?: number
  brandName?: string
  page?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

// 分页响应
export interface ProductPageResponse {
  list: Product[]
  total: number
  page: number
  pageSize: number
}

// API响应格式
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  code?: number
}

// 获取商品列表（用户端）
export const getProductList = (params: ProductQueryParams): Promise<ApiResponse<ProductPageResponse>> => {
  return request({
    url: '/api/user/products',
    method: 'get',
    params
  })
}

// 获取商品详情
export const getProductDetail = (spuId: number): Promise<ApiResponse<Product>> => {
  return request({
    url: `/api/user/products/${spuId}`,
    method: 'get'
  })
}

// 获取商品分类列表
export const getCategoryList = (): Promise<ApiResponse<Category[]>> => {
  return request({
    url: '/api/user/categories',
    method: 'get'
  })
}

// 获取热门商品
export const getHotProducts = (limit: number = 20): Promise<ApiResponse<Product[]>> => {
  return request({
    url: '/api/user/products/hot',
    method: 'get',
    params: { limit }
  })
}

// 获取推荐商品
export const getRecommendedProducts = (limit: number = 50): Promise<ApiResponse<Product[]>> => {
  return request({
    url: '/api/user/products/recommended',
    method: 'get',
    params: { limit }
  })
}

// 搜索商品
export const searchProducts = (params: {
  keyword: string
  categoryId?: number
  priceRange?: string
  sortBy?: string
  page?: number
  size?: number
}): Promise<ApiResponse<ProductPageResponse>> => {
  return request({
    url: '/api/user/products/search',
    method: 'get',
    params
  })
}

// 根据分类获取商品
export const getProductsByCategory = (params: {
  categoryId: number
  page?: number
  size?: number
  sortBy?: string
}): Promise<ApiResponse<ProductPageResponse>> => {
  return request({
    url: `/api/user/categories/${params.categoryId}/products`,
    method: 'get',
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy
    }
  })
}

// 获取分类详情
export const getCategoryDetail = (categoryId: number): Promise<ApiResponse<Category>> => {
  return request({
    url: `/api/user/categories/${categoryId}`,
    method: 'get'
  })
}