/**
 * 电商平台前端API配置文件
 * 
 * @description 统一管理所有API相关的配置信息，包括服务地址、路径前缀、
 *              超时时间等。支持开发环境和生产环境的不同配置。
 * 
 * @features
 * - 多服务API地址配置
 * - 环境变量支持
 * - 统一的API路径管理
 * - 超时时间配置
 * - 开发/生产环境自动适配
 * 
 * @services
 * - 商家服务 (Merchant Service)
 * - 文件服务 (File Service)  
 * - 用户服务 (User Service)
 * 
 * @environment_handling
 * - 开发环境: 通过Vite代理转发，使用相对路径
 * - Docker生产环境: 通过Nginx代理，使用相对路径
 * 
 * @author 开发团队
 * @version 2.0.0
 * @since 2024
 */

// API基础配置
export const API_CONFIG = {
  // 基础API地址 - 统一使用相对路径，通过代理转发
  BASE_URL: '',
  
  // 各个服务的基础URL - 统一使用相对路径，通过代理转发
  MERCHANT: '',
  FILE: '',
  USER: '',
  
  // API路径前缀
  API_PREFIX: '/api',
  
  // 超时时间
  TIMEOUT: 30000,
  
  // 是否开发环境
  IS_DEV: import.meta.env.DEV
}

// API路径常量
export const API_PATHS = {
  // 商家服务相关路径
  MERCHANT: {
    REGISTER: '/api/merchant/register',
    LOGIN: '/api/merchant/login',
    SEND_LOGIN_CODE: '/api/merchant/send-login-code',
    DASHBOARD_STATS: '/api/merchant/dashboard/stats',
    STORE_LIST: '/api/merchant/store-extended/merchant',
    STORE_CREATE: '/api/merchant/store-extended',
    STORE_UPDATE: '/api/merchant/store-extended',
    INFO: '/api/merchant/info'
  },
  
  // 验证码服务路径
  VERIFICATION: {
    SEND: '/api/verification/send',
    VERIFY: '/api/verification/verify'
  },
  
  // 文件服务路径
  FILE: {
    UPLOAD_IMAGE: '/api/upload/image',
    UPLOAD_FILE: '/api/upload/file'
  }
}

// 构建完整的API URL
export const buildApiUrl = (path: string): string => {
  // 在开发环境和Docker环境中都使用相对路径
  // 代理会自动处理路由到正确的后端服务
  return path
}

// 获取服务类型（用于日志记录）
export const getServiceType = (url: string): string => {
  if (url.startsWith('/api/merchant')) return 'merchant'
  if (url.startsWith('/api/upload')) return 'file'
  if (url.startsWith('/api/verification')) return 'verification'
  if (url.startsWith('/api/user')) return 'user'
  return 'unknown'
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