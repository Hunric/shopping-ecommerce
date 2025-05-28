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