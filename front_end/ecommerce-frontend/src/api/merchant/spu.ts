import request from '@/utils/request'

// SPU相关接口
export interface SPU {
  spuId?: number
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
  status: 'draft' | 'pending' | 'approved' | 'rejected' | 'on_shelf' | 'off_shelf'
  createTime?: string
  updateTime?: string
  skus?: SKU[]
}

// SKU相关接口
export interface SKU {
  skuId?: number
  spuId: number
  skuName: string
  salePrice: number | string
  stockQuantity: number
  attributeCombination: Record<string, any>
  status: 1 | 2 | 3 // 1:上架，2:下架，3:库存不足
  exclusiveImageUrl?: string
  warnStock: number
  createTime?: string
  updateTime?: string
}

// 商品创建数据
export interface SPUCreateData {
  merchantId: number
  storeId: number
  categoryId: number
  spuName: string
  spuDescription?: string
  productImage?: string
  displayPrice: number | string
  basicAttributes: Record<string, any>
  nonBasicAttributes: Record<string, any>
  brandName?: string
  sellingPoint?: string
  unit: string
  skus: Omit<SKU, 'skuId' | 'spuId' | 'createTime' | 'updateTime'>[]
  status?: string
}

// 商品更新数据
export interface SPUUpdateData extends Partial<SPUCreateData> {
  status?: 'draft' | 'pending' | 'approved' | 'rejected' | 'on_shelf' | 'off_shelf'
}

// 商品查询参数
export interface SPUQueryParams {
  storeId?: number
  categoryId?: number
  keyword?: string
  status?: string
  page?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

// 商品统计信息
export interface SPUStats {
  totalCount: number
  onShelfCount: number
  offShelfCount: number
  draftCount: number
  lowStockCount: number
  totalValue: number
}

// 分页响应
export interface SPUPageResponse {
  list: SPU[]
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

// 获取商品列表（分页）
export const getSPUList = (params: SPUQueryParams): Promise<ApiResponse<SPUPageResponse>> => {
  return request({
    url: '/api/merchant/spu',
    method: 'get',
    params
  }).then(response => response.data)
}

// 获取商品详情
export const getSPUDetail = (spuId: number): Promise<ApiResponse<SPU>> => {
  return request({
    url: `/api/merchant/spu/${spuId}`,
    method: 'get'
  }).then(response => response.data)
}

// 创建商品
export const createSPU = (data: SPUCreateData): Promise<ApiResponse<SPU>> => {
  return request({
    url: '/api/merchant/spu',
    method: 'post',
    data
  }).then(response => response.data)
}

// 更新商品
export const updateSPU = (spuId: number, data: SPUUpdateData): Promise<ApiResponse<SPU>> => {
  return request({
    url: `/api/merchant/spu/${spuId}`,
    method: 'put',
    data
  }).then(response => response.data)
}

// 删除商品
export const deleteSPU = (spuId: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/merchant/spu/${spuId}`,
    method: 'delete'
  }).then(response => response.data)
}

// 批量删除商品
export const batchDeleteSPU = (spuIds: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/api/merchant/spu/batch-delete',
    method: 'post',
    data: { spuIds }
  }).then(response => response.data)
}

// 上架商品
export const publishSPU = (spuId: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/merchant/spu/${spuId}/publish`,
    method: 'post'
  }).then(response => response.data)
}

// 下架商品
export const unpublishSPU = (spuId: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/merchant/spu/${spuId}/unpublish`,
    method: 'post'
  }).then(response => response.data)
}

// 批量上架
export const batchPublishSPU = (spuIds: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/api/merchant/spu/batch-publish',
    method: 'post',
    data: { spuIds }
  }).then(response => response.data)
}

// 批量下架
export const batchUnpublishSPU = (spuIds: number[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/api/merchant/spu/batch-unpublish',
    method: 'post',
    data: { spuIds }
  }).then(response => response.data)
}

// 复制商品
export const copySPU = (spuId: number, newName: string): Promise<ApiResponse<SPU>> => {
  return request({
    url: `/api/merchant/spu/${spuId}/copy`,
    method: 'post',
    data: { newName }
  }).then(response => response.data)
}

// 获取商品统计信息
export const getSPUStats = (storeId: number): Promise<ApiResponse<SPUStats>> => {
  return request({
    url: `/api/merchant/spu/stats`,
    method: 'get',
    params: { storeId }
  }).then(response => response.data)
}

// SKU相关API

// 获取SKU列表
export const getSKUList = (spuId: number): Promise<ApiResponse<SKU[]>> => {
  return request({
    url: `/api/merchant/spu/${spuId}/sku`,
    method: 'get'
  }).then(response => response.data)
}

// 创建SKU
export const createSKU = (spuId: number, data: Omit<SKU, 'skuId' | 'spuId' | 'createTime' | 'updateTime'>): Promise<ApiResponse<SKU>> => {
  return request({
    url: `/api/merchant/spu/${spuId}/sku`,
    method: 'post',
    data
  }).then(response => response.data)
}

// 更新SKU
export const updateSKU = (skuId: number, data: Partial<SKU>): Promise<ApiResponse<SKU>> => {
  return request({
    url: `/api/merchant/sku/${skuId}`,
    method: 'put',
    data
  }).then(response => response.data)
}

// 删除SKU
export const deleteSKU = (skuId: number): Promise<ApiResponse<void>> => {
  return request({
    url: `/api/merchant/sku/${skuId}`,
    method: 'delete'
  }).then(response => response.data)
}

// 批量更新SKU库存
export const batchUpdateSKUStock = (updates: { skuId: number; stockQuantity: number }[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/api/merchant/sku/batch-update-stock',
    method: 'post',
    data: { updates }
  }).then(response => response.data)
}

// 批量更新SKU价格
export const batchUpdateSKUPrice = (updates: { skuId: number; salePrice: number }[]): Promise<ApiResponse<void>> => {
  return request({
    url: '/api/merchant/sku/batch-update-price',
    method: 'post',
    data: { updates }
  }).then(response => response.data)
}

// 获取分类的基础属性
export const getCategoryBasicAttributes = (categoryId: number): Promise<ApiResponse<any[]>> => {
  return request({
    url: `/api/merchant/category/${categoryId}/basic-attributes`,
    method: 'get'
  }).then(response => response.data)
}

// 获取分类的非基础属性
export const getCategoryNonBasicAttributes = (categoryId: number): Promise<ApiResponse<any[]>> => {
  return request({
    url: `/api/merchant/category/${categoryId}/non-basic-attributes`,
    method: 'get'
  }).then(response => response.data)
}

// 生成SKU组合
export const generateSKUCombinations = (basicAttributes: Record<string, string[]>): Promise<ApiResponse<any[]>> => {
  return request({
    url: '/api/merchant/spu/generate-sku-combinations',
    method: 'post',
    data: { basicAttributes }
  }).then(response => response.data)
}

// 状态选项
export const SPU_STATUS_OPTIONS = [
  { label: '草稿', value: 'draft', type: 'info' },
  { label: '待审核', value: 'pending', type: 'warning' },
  { label: '已通过', value: 'approved', type: 'success' },
  { label: '已拒绝', value: 'rejected', type: 'danger' },
  { label: '已上架', value: 'on_shelf', type: 'success' },
  { label: '已下架', value: 'off_shelf', type: 'warning' }
]

export const SKU_STATUS_OPTIONS = [
  { label: '上架', value: 1, type: 'success' },
  { label: '下架', value: 2, type: 'warning' },
  { label: '库存不足', value: 3, type: 'danger' }
]

// 获取状态标签
export const getSPUStatusLabel = (status: string): string => {
  const option = SPU_STATUS_OPTIONS.find(opt => opt.value === status)
  return option ? option.label : status
}

export const getSKUStatusLabel = (status: number): string => {
  const option = SKU_STATUS_OPTIONS.find(opt => opt.value === status)
  return option ? option.label : String(status)
}

// 获取状态类型
export const getSPUStatusType = (status: string): string => {
  const option = SPU_STATUS_OPTIONS.find(opt => opt.value === status)
  return option ? option.type : 'info'
}

export const getSKUStatusType = (status: number): string => {
  const option = SKU_STATUS_OPTIONS.find(opt => opt.value === status)
  return option ? option.type : 'info'
} 