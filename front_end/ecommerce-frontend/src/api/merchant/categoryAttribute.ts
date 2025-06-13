import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 分类属性接口
export interface CategoryAttribute {
  attributeId: number
  storeId: number
  categoryId: number
  attributeName: string
  attributeType: 'TEXT' | 'NUMBER' | 'DATE' | 'BOOLEAN' | 'ENUM'
  isBasicAttribute: boolean
  isRequired: boolean
  options?: string[] // 可选值列表（枚举类型时使用）
}

// 创建属性请求
export interface CreateAttributeRequest {
  attributeName: string
  attributeType: CategoryAttribute['attributeType']
  isBasicAttribute: boolean
  isRequired: boolean
  options?: string[]
}

// 单个属性创建请求（包含storeId和categoryId）
export interface CreateSingleAttributeRequest extends CreateAttributeRequest {
  storeId: number
  categoryId: number
}

// 更新属性请求
export interface UpdateAttributeRequest {
  attributeName: string
  attributeType: CategoryAttribute['attributeType']
  isBasicAttribute: boolean
  isRequired: boolean
  options?: string[]
}

// 批量创建属性请求
export interface BatchCreateAttributesRequest {
  storeId: number
  categoryId: number
  attributes: CreateAttributeRequest[]
}

/**
 * 获取分类的所有属性
 */
export const getCategoryAttributes = async (categoryId: number): Promise<ApiResponse<CategoryAttribute[]>> => {
  const response = await request.get(`/api/merchant/category/attribute/category/${categoryId}`)
  return response.data
}

/**
 * 获取属性详情
 */
export const getAttributeById = async (attributeId: number): Promise<ApiResponse<CategoryAttribute>> => {
  const response = await request.get(`/api/merchant/category/attribute/${attributeId}`)
  return response.data
}

/**
 * 创建分类属性
 */
export const createAttribute = async (data: CreateSingleAttributeRequest): Promise<ApiResponse<CategoryAttribute>> => {
  const response = await request.post('/api/merchant/category/attribute', data)
  return response.data
}

/**
 * 更新分类属性
 */
export const updateAttribute = async (attributeId: number, data: UpdateAttributeRequest): Promise<ApiResponse<CategoryAttribute>> => {
  const response = await request.put(`/api/merchant/category/attribute/${attributeId}`, data)
  return response.data
}

/**
 * 删除分类属性
 */
export const deleteAttribute = async (attributeId: number): Promise<ApiResponse<string>> => {
  const response = await request.delete(`/api/merchant/category/attribute/${attributeId}`)
  return response.data
}

/**
 * 批量创建分类属性
 */
export const batchCreateAttributes = async (data: BatchCreateAttributesRequest): Promise<ApiResponse<CategoryAttribute[]>> => {
  const response = await request.post('/api/merchant/category/attribute/batch', data)
  return response.data
}

/**
 * 检查分类是否可以管理属性
 */
export const canManageAttributes = async (categoryId: number): Promise<ApiResponse<boolean>> => {
  const response = await request.get(`/api/merchant/category/attribute/category/${categoryId}/can-manage`)
  return response.data
}

/**
 * 获取属性类型列表
 */
export const getAttributeTypes = async (): Promise<ApiResponse<string[]>> => {
  const response = await request.get('/api/merchant/category/attribute/types')
  return response.data
}

// 属性类型选项
export const ATTRIBUTE_TYPE_OPTIONS = [
  { label: '文本', value: 'TEXT', description: '单行文本输入' },
  { label: '数字', value: 'NUMBER', description: '数字输入' },
  { label: '日期', value: 'DATE', description: '日期选择' },
  { label: '布尔值', value: 'BOOLEAN', description: '是/否选择' },
  { label: '枚举', value: 'ENUM', description: '下拉选择（需要设置可选值）' }
]

// 获取属性类型标签
export const getAttributeTypeLabel = (type: string): string => {
  const option = ATTRIBUTE_TYPE_OPTIONS.find(opt => opt.value === type)
  return option ? option.label : type
}

// 获取属性类型描述
export const getAttributeTypeDescription = (type: string): string => {
  const option = ATTRIBUTE_TYPE_OPTIONS.find(opt => opt.value === type)
  return option ? option.description : ''
} 