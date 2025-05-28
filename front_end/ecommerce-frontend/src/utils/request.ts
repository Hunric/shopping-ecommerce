import axios from 'axios'
import { useAuthStore } from '@/store/modules/auth'
import { ElMessage } from 'element-plus'
import { API_CONFIG } from '@/config/api'

// 根据API路径确定服务类型
const getServiceType = (url: string): 'merchant' | 'file' | 'user' | 'default' => {
  if (url.startsWith('/api/merchant')) return 'merchant'
  if (url.startsWith('/api/upload')) return 'file'
  if (url.startsWith('/api/user')) return 'user'
  return 'default'
}

// 获取对应服务的baseURL
const getBaseURL = (serviceType: string): string => {
  switch (serviceType) {
    case 'merchant':
      return API_CONFIG.MERCHANT
    case 'file':
      return API_CONFIG.FILE
    case 'user':
      return API_CONFIG.USER
    default:
      return API_CONFIG.BASE_URL
  }
}

// 创建axios实例
const request = axios.create({
  timeout: API_CONFIG.TIMEOUT,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    
    // 根据请求URL确定服务类型和baseURL
    const serviceType = getServiceType(config.url || '')
    const baseURL = getBaseURL(serviceType)
    
    // 设置baseURL
    config.baseURL = baseURL
    
    // 处理URL路径，避免重复
    let finalUrl = config.url || ''
    if (serviceType === 'merchant' && finalUrl.startsWith('/api/merchant')) {
      // merchant服务保持完整路径，因为后端控制器需要 /api/merchant 前缀
      finalUrl = finalUrl
    } else if (serviceType === 'file' && finalUrl.startsWith('/api/upload')) {
      // 文件服务保持原样，因为baseURL不包含路径
      finalUrl = finalUrl
    } else if (serviceType === 'user' && finalUrl.startsWith('/api/user')) {
      // 用户服务去掉 /api/user 前缀
      finalUrl = finalUrl.replace('/api/user', '')
    }
    
    // 更新config.url
    config.url = finalUrl
    
    // 添加详细的请求日志
    console.log('=== API请求详情 ===')
    console.log('服务类型:', serviceType)
    console.log('基础URL:', baseURL)
    console.log('原始URL:', config.url)
    console.log('处理后URL:', finalUrl)
    console.log('完整URL:', `${baseURL}${finalUrl}`)
    console.log('请求方法:', config.method)
    console.log('请求参数:', config.params)
    console.log('请求数据:', config.data)
    console.log('请求头:', config.headers)
    
    // 如果有token，添加到请求头
    if (authStore.accessToken) {
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
      console.log('已添加认证token')
    } else {
      console.log('未找到认证token')
    }
    
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    console.log('=== API响应详情 ===')
    console.log('响应状态:', response.status)
    console.log('响应头:', response.headers)
    console.log('响应数据:', response.data)
    
    // 检查响应是否为HTML（说明被重定向到前端页面）
    if (typeof response.data === 'string' && response.data.includes('<!DOCTYPE html>')) {
      console.error('API响应返回HTML页面，可能是路径错误或认证失败')
      const error = new Error('API请求失败：返回了HTML页面而不是JSON数据')
      return Promise.reject(error)
    }
    
    return response
  },
  (error) => {
    console.error('=== API响应错误 ===')
    console.error('错误详情:', error)
    console.error('错误响应:', error.response)
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          console.error('认证失败，请重新登录')
          ElMessage.error('认证失败，请重新登录')
          // 可以在这里处理登录跳转
          break
        case 403:
          console.error('权限不足')
          ElMessage.error('权限不足')
          break
        case 404:
          console.error('请求的资源不存在')
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器内部错误')
          ElMessage.error('服务器内部错误')
          break
        default:
          console.error(`请求失败: ${status}`)
          ElMessage.error(data?.message || `请求失败: ${status}`)
      }
    } else if (error.request) {
      console.error('网络错误，请检查网络连接')
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      console.error('请求配置错误:', error.message)
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export default request 