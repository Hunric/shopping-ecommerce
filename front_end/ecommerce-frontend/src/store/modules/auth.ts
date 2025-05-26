import { defineStore } from 'pinia'
import { ref } from 'vue'

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

  // 登录成功后设置状态
  const setAuthData = (loginResponse: LoginResponse) => {
    isLoggedIn.value = true
    accessToken.value = loginResponse.accessToken
    refreshToken.value = loginResponse.refreshToken
    
    merchantInfo.value = {
      merchantId: loginResponse.merchantId,
      merchantName: loginResponse.merchantName,
      email: loginResponse.email
    }

    // 保存到localStorage
    localStorage.setItem('merchant_access_token', loginResponse.accessToken)
    localStorage.setItem('merchant_refresh_token', loginResponse.refreshToken)
    localStorage.setItem('merchant_info', JSON.stringify(merchantInfo.value))
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
    isTokenExpired
  }
}) 