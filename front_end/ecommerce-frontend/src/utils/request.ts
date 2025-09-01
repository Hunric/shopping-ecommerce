/**
 * 电商平台前端HTTP请求工具文件
 * 
 * @description 基于Axios封装的HTTP请求工具，提供统一的请求/响应处理、
 *              多服务路由、认证管理、错误处理等功能。支持微服务架构的API调用。
 * 
 * @features
 * - 基于Axios的HTTP客户端封装
 * - 开发环境和生产环境自动适配
 * - JWT认证token自动添加
 * - 统一的请求/响应拦截器
 * - 详细的请求/响应日志记录
 * - 错误状态码统一处理
 * - Element Plus消息提示集成
 * - 跨域请求支持
 * 
 * @environment_handling
 * - 开发环境: 通过Vite代理转发到后端服务
 * - 生产环境: 通过Nginx代理统一处理
 * 
 * @error_handling
 * - 401: 认证失败，提示重新登录
 * - 403: 权限不足
 * - 404: 资源不存在
 * - 500: 服务器内部错误
 * - 网络错误: 连接超时或网络异常
 * 
 * @dependencies
 * - axios: HTTP客户端库
 * - element-plus: UI组件库（消息提示）
 * - @/store/modules/auth: 认证状态管理
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link https://axios-http.com/} Axios官方文档
 * @see {@link https://element-plus.org/} Element Plus文档
 */

import axios from 'axios'
import { useAuthStore } from '@/store/modules/auth'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  // 在开发环境下，通过Vite代理转发请求
  // 在Docker生产环境下，通过Nginx代理，使用相对路径
  baseURL: '',
  timeout: 30000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 无需认证的接口路径
const noAuthRequired = [
  // 商家认证相关
  '/api/merchant/register',
  '/api/merchant/send-login-code',
  '/api/merchant/login',
  '/api/merchant/login/password',
  '/api/merchant/send-reset-password-code',
  '/api/merchant/verify-reset-password-code',
  '/api/merchant/reset-password',
  // 用户认证相关
  '/api/user/auth/register',
  '/api/user/auth/send-register-code',
  '/api/user/auth/verify-register-code',
  '/api/user/auth/send-login-code',
  '/api/user/auth/login',
  '/api/user/auth/login/password',
  '/api/user/auth/send-reset-password-code',
  '/api/user/auth/verify-reset-password-code',
  '/api/user/auth/reset-password',
  // 公共API
  '/api/user/product/hot',
  '/api/user/product/recommended',
  '/api/user/product/category/list'
];

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    
    // 添加详细的请求日志
    console.log('=== API请求详情 ===')
    console.log('请求URL:', config.url)
    console.log('Base URL:', config.baseURL)
    console.log('完整URL:', config.baseURL ? `${config.baseURL}${config.url}` : config.url)
    console.log('请求方法:', config.method)
    console.log('请求参数:', config.params)
    console.log('请求数据:', config.data)
    console.log('环境信息:', {
      isDev: import.meta.env.DEV,
      mode: import.meta.env.MODE,
      baseUrl: import.meta.env.BASE_URL
    })
    
    // 判断是否需要添加认证令牌
    const isNoAuthPath = noAuthRequired.some(path => config.url?.includes(path));
    
    // 只在需要认证的接口上添加令牌
    if (!isNoAuthPath) {
      const userToken = localStorage.getItem('user_access_token')
      const merchantToken = authStore.accessToken
      
      // 根据API路径判断使用哪种token
      if (config.url?.includes('/api/merchant/') || config.url?.includes('/merchant/')) {
        // 商家API使用商家token
        if (merchantToken) {
          config.headers.Authorization = `Bearer ${merchantToken}`
          console.log('已添加商家认证token')
        } else {
          console.log('未找到商家认证token')
        }
      } else if (config.url?.includes('/api/user/') || config.url?.includes('/api/cart/')) {
        // 用户API和购物车API使用用户token
        console.log('=== 用户/购物车API认证检查 ===')
        console.log('请求URL:', config.url)
        console.log('userToken存在:', !!userToken)
        console.log('userToken值:', userToken ? `${userToken.substring(0, 20)}...` : 'null')
        
        if (userToken) {
          config.headers.Authorization = `Bearer ${userToken}`
          console.log('已添加用户认证token')
        } else {
          console.log('⚠️ 未找到用户认证token - 将导致403错误')
          console.log('所有localStorage用户数据:')
          console.log('- user_access_token:', localStorage.getItem('user_access_token'))
          console.log('- user_info:', localStorage.getItem('user_info'))
          console.log('- user_refresh_token:', localStorage.getItem('user_refresh_token'))
        }
      } else {
        // 其他API，优先使用用户token，然后是商家token
        if (userToken) {
          config.headers.Authorization = `Bearer ${userToken}`
          console.log('已添加用户认证token（默认）')
        } else if (merchantToken) {
          config.headers.Authorization = `Bearer ${merchantToken}`
          console.log('已添加商家认证token（默认）')
        } else {
          console.log('未找到认证token')
        }
      }
    } else {
      console.log('该接口无需认证令牌')
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
    
    // 关键修复：返回完整的响应对象，而不是提前解包
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