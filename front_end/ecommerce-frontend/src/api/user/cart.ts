import request from '@/utils/request'

// 购物车相关接口

/**
 * 添加商品到购物车DTO
 */
export interface CartAddDTO {
  skuId: number
  quantity: number
}

/**
 * 更新购物车DTO
 */
export interface CartUpdateDTO {
  cartId: number
  quantity?: number
  isSelected?: boolean
}

/**
 * 购物车项响应DTO
 */
export interface CartItemResponseDTO {
  cartId: number
  productId: number
  skuId: number
  storeId: number
  quantity: number
  isSelected: boolean
  productName: string
  productImage: string
  skuAttr: string
  skuPrice: number
  subtotal: number
  createTime: string
  updateTime: string
}

// API响应格式
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  code?: number
}

/**
 * 添加商品到购物车
 */
export const addToCart = (data: CartAddDTO): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/cart/add',
    method: 'POST',
    data
  })
}

/**
 * 获取购物车列表
 */
export const getCartList = (): Promise<ApiResponse<CartItemResponseDTO[]>> => {
  return request({
    url: '/api/cart/list',
    method: 'GET'
  })
}

/**
 * 更新购物车项
 */
export const updateCartItem = (data: CartUpdateDTO): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/cart/update',
    method: 'PUT',
    data
  })
}

/**
 * 删除购物车项
 */
export const removeCartItem = (cartId: number): Promise<ApiResponse<string>> => {
  return request({
    url: `/api/cart/remove/${cartId}`,
    method: 'DELETE'
  })
}

/**
 * 清空购物车
 */
export const clearCart = (): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/cart/clear',
    method: 'DELETE'
  })
}

/**
 * 获取购物车商品数量
 */
export const getCartCount = (): Promise<ApiResponse<number>> => {
  return request({
    url: '/api/cart/count',
    method: 'GET'
  })
}

/**
 * 批量更新购物车项选中状态
 */
export const updateSelectedStatus = (cartIds: number[], isSelected: boolean): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/cart/select',
    method: 'PUT',
    params: {
      cartIds: cartIds.join(','),
      isSelected
    }
  })
}

/**
 * 获取选中的购物车项
 */
export const getSelectedCartItems = (): Promise<ApiResponse<CartItemResponseDTO[]>> => {
  return request({
    url: '/api/cart/selected',
    method: 'GET'
  })
}