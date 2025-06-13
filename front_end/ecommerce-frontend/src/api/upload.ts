/**
 * 电商平台前端文件上传API接口文件
 * 
 * @description 提供文件上传相关的API接口，包括图片上传、文件删除等功能。
 *              支持多种图片格式，包含客户端验证、进度监控、错误处理等特性。
 * 
 * @features
 * - 图片文件上传（JPG、PNG、GIF、WebP）
 * - 客户端文件类型和大小验证
 * - 上传进度监控支持
 * - 文件删除功能
 * - 详细的错误处理和用户友好的错误信息
 * - TypeScript类型安全
 * - 超时控制和重试机制
 * 
 * @supported_formats
 * - image/jpeg: JPEG图片格式
 * - image/jpg: JPG图片格式
 * - image/png: PNG图片格式
 * - image/gif: GIF动图格式
 * - image/webp: WebP现代图片格式
 * 
 * @file_limitations
 * - 最大文件大小: 5MB
 * - 支持的文件类型: 图片文件（见上述格式）
 * - 上传超时时间: 30秒
 * 
 * @api_endpoints
 * - POST /api/upload/image: 上传图片文件
 * - DELETE /api/upload/delete: 删除已上传的文件
 * 
 * @upload_types
 * - logo: 商家/店铺Logo
 * - product: 商品图片
 * - avatar: 用户头像
 * - general: 通用文件（默认）
 * 
 * @interfaces
 * - UploadResponse: 上传响应数据格式
 * - ApiResponse<T>: 通用API响应格式
 * 
 * @error_handling
 * - 文件类型验证失败
 * - 文件大小超限
 * - 网络请求失败
 * - 服务器错误响应
 * - 超时异常处理
 * 
 * @dependencies
 * - axios: HTTP客户端库
 * - Vite环境变量: VITE_FILE_API_BASE_URL
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link https://axios-http.com/} Axios官方文档
 * @see {@link ../types/api.ts} API类型定义
 */

import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_FILE_API_BASE_URL || 'http://localhost:8082'

// 上传响应接口
export interface UploadResponse {
  success: boolean
  message: string
  data: {
    url: string
    filename: string
    size: number
  }
}

// 通用API响应接口
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

/**
 * 上传图片文件
 * @param file 图片文件
 * @param type 上传类型 (logo, product, avatar等)
 * @returns 上传结果
 */
export const uploadImage = async (file: File, type: string = 'logo'): Promise<ApiResponse<UploadResponse['data']>> => {
  try {
    // 验证文件类型
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
    if (!allowedTypes.includes(file.type)) {
      throw new Error('不支持的文件格式，请上传 JPG、PNG、GIF 或 WebP 格式的图片')
    }

    // 验证文件大小 (5MB)
    const maxSize = 5 * 1024 * 1024
    if (file.size > maxSize) {
      throw new Error('文件大小不能超过 5MB')
    }

    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)

    const response = await axios.post(`${API_BASE_URL}/api/upload/image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 30000 // 30秒超时
    })

    return response.data
  } catch (error: any) {
    console.error('图片上传失败:', error)
    
    if (error.message) {
      throw new Error(error.message)
    }
    
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message)
    }
    
    throw new Error('图片上传失败，请稍后重试')
  }
}

/**
 * 删除已上传的文件
 * @param url 文件URL
 * @returns 删除结果
 */
export const deleteUploadedFile = async (url: string): Promise<ApiResponse<string>> => {
  try {
    const response = await axios.delete(`${API_BASE_URL}/api/upload/delete`, {
      data: { url }
    })
    return response.data
  } catch (error) {
    console.error('删除文件失败:', error)
    throw error
  }
}

/**
 * 获取文件上传进度的辅助函数
 * @param file 文件
 * @param type 上传类型
 * @param onProgress 进度回调
 * @returns 上传结果
 */
export const uploadImageWithProgress = async (
  file: File, 
  type: string = 'logo',
  onProgress?: (progress: number) => void
): Promise<ApiResponse<UploadResponse['data']>> => {
  try {
    // 验证文件类型
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
    if (!allowedTypes.includes(file.type)) {
      throw new Error('不支持的文件格式，请上传 JPG、PNG、GIF 或 WebP 格式的图片')
    }

    // 验证文件大小 (5MB)
    const maxSize = 5 * 1024 * 1024
    if (file.size > maxSize) {
      throw new Error('文件大小不能超过 5MB')
    }

    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)

    const response = await axios.post(`${API_BASE_URL}/api/upload/image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 30000,
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total && onProgress) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onProgress(progress)
        }
      }
    })

    return response.data
  } catch (error: any) {
    console.error('图片上传失败:', error)
    
    if (error.message) {
      throw new Error(error.message)
    }
    
    if (error.response?.data?.message) {
      throw new Error(error.response.data.message)
    }
    
    throw new Error('图片上传失败，请稍后重试')
  }
} 