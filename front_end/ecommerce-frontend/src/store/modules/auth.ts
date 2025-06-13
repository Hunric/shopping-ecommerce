/**
 * 电商平台商家认证状态管理模块
 * 
 * @description 基于Pinia的商家认证状态管理，负责处理商家登录、登出、
 *              token管理、用户信息存储等认证相关的状态和操作。
 * 
 * @features
 * - 商家登录状态管理
 * - JWT token存储和验证
 * - 商家信息持久化存储
 * - 自动token过期检测
 * - localStorage数据持久化
 * - 认证状态初始化恢复
 * - 动态获取商家名称
 * 
 * @state
 * - isLoggedIn: 登录状态标识
 * - merchantInfo: 商家基本信息
 * - accessToken: 访问令牌
 * - refreshToken: 刷新令牌
 * 
 * @methods
 * - initializeAuth(): 从localStorage恢复认证状态
 * - setAuthData(): 设置登录成功后的认证数据
 * - logout(): 清除认证状态和本地存储
 * - isTokenExpired(): 检查JWT token是否过期
 * - fetchMerchantInfo(): 获取商家详细信息
 * 
 * @interfaces
 * - MerchantInfo: 商家信息类型定义
 * - LoginResponse: 登录响应数据类型定义
 * 
 * @storage
 * - merchant_access_token: 访问令牌
 * - merchant_refresh_token: 刷新令牌
 * - merchant_info: 商家信息JSON字符串
 * 
 * @dependencies
 * - pinia: 状态管理库
 * - vue: Vue 3响应式API
 * - axios: HTTP客户端
 * 
 * @author 开发团队
 * @version 2.0.0
 * @since 2024
 * 
 * @see {@link https://pinia.vuejs.org/} Pinia官方文档
 * @see {@link https://jwt.io/} JWT官方文档
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

// 商家信息接口
export interface MerchantInfo {
  merchantId: number
  merchantName: string
  email: string
}

// 登录响应接口
export interface LoginResponse {
  success: boolean
  message: string
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  merchantId: number
  merchantName: string
  email: string
}

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const isLoggedIn = ref(false)
  const merchantInfo = ref<MerchantInfo | null>(null)
  const accessToken = ref<string>('')
  const refreshToken = ref<string>('')

  // 从localStorage恢复状态
  const initializeAuth = () => {
    const token = localStorage.getItem('merchant_access_token')
    const merchant = localStorage.getItem('merchant_info')
    
    if (token && merchant) {
      accessToken.value = token
      refreshToken.value = localStorage.getItem('merchant_refresh_token') || ''
      merchantInfo.value = JSON.parse(merchant)
      isLoggedIn.value = true
    }
  }

  // 获取商家详细信息
  const fetchMerchantInfo = async (merchantId: number): Promise<MerchantInfo | null> => {
    try {
      const response = await axios.get(`/api/merchant/info/${merchantId}`, {
        headers: {
          'Authorization': `Bearer ${accessToken.value}`
        }
      })
      
      if (response.data.success) {
        return {
          merchantId: response.data.data.merchantId,
          merchantName: response.data.data.merchantName,
          email: response.data.data.email
        }
      }
      return null
    } catch (error) {
      console.error('获取商家信息失败:', error)
      return null
    }
  }

  // 登录成功后设置状态
  const setAuthData = async (loginResponse: LoginResponse) => {
    console.log('设置认证数据:', loginResponse)
    
    isLoggedIn.value = true
    accessToken.value = loginResponse.accessToken
    refreshToken.value = loginResponse.refreshToken
    
    // 先设置基本信息
    merchantInfo.value = {
      merchantId: loginResponse.merchantId,
      merchantName: loginResponse.merchantName,
      email: loginResponse.email
    }

    // 保存到localStorage
    localStorage.setItem('merchant_access_token', loginResponse.accessToken)
    localStorage.setItem('merchant_refresh_token', loginResponse.refreshToken)
    localStorage.setItem('merchant_info', JSON.stringify(merchantInfo.value))
    
    console.log('认证数据设置完成')
    console.log('isLoggedIn:', isLoggedIn.value)
    console.log('merchantInfo:', merchantInfo.value)
    console.log('accessToken存在:', !!accessToken.value)
  }

  // 登出
  const logout = () => {
    isLoggedIn.value = false
    merchantInfo.value = null
    accessToken.value = ''
    refreshToken.value = ''

    // 清除localStorage
    localStorage.removeItem('merchant_access_token')
    localStorage.removeItem('merchant_refresh_token')
    localStorage.removeItem('merchant_info')
  }

  // 检查token是否过期
  const isTokenExpired = () => {
    if (!accessToken.value) return true
    
    try {
      const payload = JSON.parse(atob(accessToken.value.split('.')[1]))
      const currentTime = Date.now() / 1000
      return payload.exp < currentTime
    } catch (error) {
      return true
    }
  }
  
  // 更新商家名称
  const updateMerchantName = (newName: string) => {
    if (merchantInfo.value) {
      merchantInfo.value.merchantName = newName
      // 更新localStorage中的信息
      localStorage.setItem('merchant_info', JSON.stringify(merchantInfo.value))
    }
  }

  return {
    // 状态
    isLoggedIn,
    merchantInfo,
    accessToken,
    refreshToken,
    
    // 方法
    initializeAuth,
    setAuthData,
    logout,
    isTokenExpired,
    fetchMerchantInfo,
    updateMerchantName
  }
}) 