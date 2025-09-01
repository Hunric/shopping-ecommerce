/**
 * 电商平台用户认证状态管理模块
 * 
 * @description 基于Pinia的用户认证状态管理，负责处理用户登录、登出、
 *              token管理、用户信息存储等认证相关的状态和操作。
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'

// 用户信息接口
export interface UserInfo {
  userId: number
  username: string
  email: string
  phone?: string
  avatar?: string
}

// 用户登录响应接口
export interface UserLoginResponse {
  success: boolean
  message: string
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
  email: string
  phone?: string
  avatar?: string
}

export const useUserAuthStore = defineStore('userAuth', () => {
  // 状态
  const isLoggedIn = ref(false)
  const userInfo = ref<UserInfo | null>(null)
  const accessToken = ref<string>('')
  const refreshToken = ref<string>('')

  // 从localStorage恢复状态
  const initializeAuth = () => {
    console.log('=== 初始化用户认证状态 ===')
    
    // 检查所有localStorage中的认证相关数据
    console.log('localStorage中的所有认证数据:')
    console.log('- user_access_token:', localStorage.getItem('user_access_token'))
    console.log('- user_info:', localStorage.getItem('user_info'))
    console.log('- user_refresh_token:', localStorage.getItem('user_refresh_token'))
    console.log('- access_token (商家):', localStorage.getItem('access_token'))
    console.log('- refresh_token (商家):', localStorage.getItem('refresh_token'))
    console.log('- merchant_info (商家):', localStorage.getItem('merchant_info'))
    
    const token = localStorage.getItem('user_access_token')
    const user = localStorage.getItem('user_info')
    const refreshTokenStored = localStorage.getItem('user_refresh_token')
    
    console.log('用户认证数据状态:')
    console.log('- token存在:', !!token)
    console.log('- user存在:', !!user)
    console.log('- refreshToken存在:', !!refreshTokenStored)
    
    if (token && user) {
      try {
        accessToken.value = token
        refreshToken.value = refreshTokenStored || ''
        userInfo.value = JSON.parse(user)
        
        // 检查token是否过期
        if (isTokenExpired()) {
          console.log('Token已过期，清除认证状态')
          logout()
          return
        }
        
        isLoggedIn.value = true
        console.log('认证状态初始化成功:')
        console.log('- isLoggedIn:', isLoggedIn.value)
        console.log('- userInfo:', userInfo.value)
      } catch (error) {
        console.error('解析用户信息失败:', error)
        // 清除损坏的数据
        logout()
      }
    } else {
      console.log('缺少必要的认证数据，用户未登录')
      isLoggedIn.value = false
      userInfo.value = null
      accessToken.value = ''
      refreshToken.value = ''
    }
    
    console.log('=== 认证状态初始化完成 ===')
  }

  // 登录成功后设置状态
  const setAuthData = (loginResponse: UserLoginResponse) => {
    console.log('设置用户认证数据:', loginResponse)
    
    isLoggedIn.value = true
    accessToken.value = loginResponse.accessToken
    refreshToken.value = loginResponse.refreshToken
    
    // 设置用户信息
    userInfo.value = {
      userId: loginResponse.userId,
      username: loginResponse.username,
      email: loginResponse.email,
      phone: loginResponse.phone,
      avatar: loginResponse.avatar
    }

    // 保存到localStorage
    localStorage.setItem('user_access_token', loginResponse.accessToken)
    localStorage.setItem('user_refresh_token', loginResponse.refreshToken)
    localStorage.setItem('user_info', JSON.stringify(userInfo.value))
    
    console.log('用户认证数据设置完成')
    console.log('isLoggedIn:', isLoggedIn.value)
    console.log('userInfo:', userInfo.value)
  }

  // 登出
  const logout = () => {
    isLoggedIn.value = false
    userInfo.value = null
    accessToken.value = ''
    refreshToken.value = ''

    // 清除localStorage
    localStorage.removeItem('user_access_token')
    localStorage.removeItem('user_refresh_token')
    localStorage.removeItem('user_info')
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
  
  // 更新用户信息
  const updateUserInfo = (newUserInfo: Partial<UserInfo>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...newUserInfo }
      // 更新localStorage中的信息
      localStorage.setItem('user_info', JSON.stringify(userInfo.value))
    }
  }

  return {
    // 状态
    isLoggedIn,
    userInfo,
    accessToken,
    refreshToken,
    
    // 方法
    initializeAuth,
    setAuthData,
    logout,
    isTokenExpired,
    updateUserInfo
  }
})