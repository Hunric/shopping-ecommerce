/**
 * API配置文件
 * 统一管理各个服务的基础URL
 */

// 获取环境变量
const getEnvVar = (key: string, defaultValue: string = '') => {
  return import.meta.env[key] || defaultValue
}

// API基础配置
export const API_CONFIG = {
  // 基础API地址
  BASE_URL: getEnvVar('VITE_API_BASE_URL', 'http://localhost:8081'),
  
  // 各个服务的基础URL
  MERCHANT: getEnvVar('VITE_MERCHANT_API_BASE_URL', 'http://localhost:8081'),
  FILE: getEnvVar('VITE_FILE_API_BASE_URL', 'http://localhost:8082'),
  USER: getEnvVar('VITE_USER_API_BASE_URL', 'http://localhost:8083'),
  
  // API路径前缀
  API_PREFIX: '/api',
  
  // 超时时间
  TIMEOUT: 30000,
  
  // 是否开发环境
  IS_DEV: import.meta.env.DEV
}

// 构建完整的API URL
export const buildApiUrl = (service: keyof typeof API_CONFIG, path: string) => {
  const baseUrl = API_CONFIG[service]
  if (typeof baseUrl !== 'string') {
    throw new Error(`Invalid service: ${service}`)
  }
  
  // 确保路径以 / 开头
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  
  return `${baseUrl}${normalizedPath}`
}

// 商家服务API路径
export const MERCHANT_API = {
  // 认证相关
  REGISTER: '/api/merchant/register',
  LOGIN: '/api/merchant/login',
  SEND_LOGIN_CODE: '/api/merchant/send-login-code',
  
  // 店铺管理
  STORE_LIST: (merchantId: number) => `/api/merchant/store/list/${merchantId}`,
  STORE_DETAIL: (storeId: number) => `/api/merchant/store/${storeId}`,
  STORE_CREATE: '/api/merchant/store/create',
  STORE_UPDATE: (storeId: number) => `/api/merchant/store/${storeId}`,
  STORE_DELETE: (storeId: number) => `/api/merchant/store/${storeId}`,
  STORE_COUNT: (merchantId: number) => `/api/merchant/store/count/${merchantId}`,
  
  // 商品管理
  SPU_LIST: '/api/merchant/spu',
  SPU_DETAIL: (spuId: number) => `/api/merchant/spu/${spuId}`,
  SPU_CREATE: '/api/merchant/spu',
  SPU_UPDATE: (spuId: number) => `/api/merchant/spu/${spuId}`,
  SPU_DELETE: (spuId: number) => `/api/merchant/spu/${spuId}`,
  SPU_BATCH_DELETE: '/api/merchant/spu/batch-delete',
  SPU_PUBLISH: (spuId: number) => `/api/merchant/spu/${spuId}/publish`,
  SPU_UNPUBLISH: (spuId: number) => `/api/merchant/spu/${spuId}/unpublish`,
  SPU_BATCH_PUBLISH: '/api/merchant/spu/batch-publish',
  SPU_BATCH_UNPUBLISH: '/api/merchant/spu/batch-unpublish',
  SPU_COPY: (spuId: number) => `/api/merchant/spu/${spuId}/copy`,
  SPU_STATS: '/api/merchant/spu/stats',
  
  // SKU管理
  SKU_LIST: (spuId: number) => `/api/merchant/spu/${spuId}/sku`,
  SKU_CREATE: (spuId: number) => `/api/merchant/spu/${spuId}/sku`,
  SKU_UPDATE: (skuId: number) => `/api/merchant/sku/${skuId}`,
  SKU_DELETE: (skuId: number) => `/api/merchant/sku/${skuId}`,
  SKU_BATCH_UPDATE_STOCK: '/api/merchant/sku/batch-update-stock',
  SKU_BATCH_UPDATE_PRICE: '/api/merchant/sku/batch-update-price',
  
  // 分类属性
  CATEGORY_BASIC_ATTRIBUTES: (categoryId: number) => `/api/merchant/category/${categoryId}/basic-attributes`,
  CATEGORY_NON_BASIC_ATTRIBUTES: (categoryId: number) => `/api/merchant/category/${categoryId}/non-basic-attributes`,
  
  // 工具
  GENERATE_SKU_COMBINATIONS: '/api/merchant/spu/generate-sku-combinations'
}

// 文件服务API路径
export const FILE_API = {
  UPLOAD_IMAGE: '/api/upload/image'
}

// 用户服务API路径（预留）
export const USER_API = {
  // 用户相关API路径
}

export default API_CONFIG 