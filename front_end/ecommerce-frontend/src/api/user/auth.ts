import request from '@/utils/request'

// 用户注册请求接口
export interface UserRegisterRequest {
  username: string
  email: string
  password: string
  gender?: string
  avatarUrl?: string
}

// 用户注册响应接口
export interface UserRegisterResponse {
  success: boolean
  message: string
  userId?: number
  username?: string
  email?: string
  gender?: string
  avatarUrl?: string
}

// 用户登录请求接口
export interface UserLoginRequest {
  email: string
  verificationCode: string
}

// 用户密码登录请求接口
export interface UserPasswordLoginRequest {
  email: string
  password: string
}

// 用户登录响应接口
export interface UserLoginResponse {
  success: boolean
  message: string
  accessToken?: string
  refreshToken?: string
  tokenType?: string
  expiresIn?: number
  userId?: number
  username?: string
  email?: string
  gender?: string
  avatarUrl?: string
}

// 发送验证码请求接口
export interface SendCodeRequest {
  email: string
}

// API响应接口
export interface ApiResponse<T> {
  success: boolean
  message: string
  data?: T
}

// 发送注册验证码
export const sendRegisterCode = (data: SendCodeRequest): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/verification/send',
    method: 'get',
    params: {
      email: data.email,
      purpose: 'register'
    }
  })
}

// 验证注册验证码
export const verifyRegisterCode = (email: string, verificationCode: string): Promise<ApiResponse<boolean>> => {
  return request({
    url: '/api/user/auth/verify-register-code',
    method: 'post',
    data: { email, verificationCode }
  })
}

// 用户注册
export const userRegister = (data: UserRegisterRequest & { verificationCode: string }): Promise<UserRegisterResponse> => {
  return request({
    url: '/api/user/auth/register',
    method: 'post',
    data
  })
}

// 发送登录验证码
export const sendLoginCode = (data: SendCodeRequest): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/verification/send',
    method: 'get',
    params: {
      email: data.email,
      purpose: 'login'
    }
  })
}

// 验证码登录
export const loginWithCode = (data: UserLoginRequest): Promise<UserLoginResponse> => {
  return request({
    url: '/api/user/auth/login',
    method: 'post',
    data
  })
}

// 密码登录
export const loginWithPassword = (data: UserPasswordLoginRequest): Promise<UserLoginResponse> => {
  return request({
    url: '/api/user/auth/login/password',
    method: 'post',
    data
  })
}

// 获取用户信息
export const getUserInfo = (userId: number): Promise<ApiResponse<any>> => {
  return request({
    url: `/api/user/auth/info/${userId}`,
    method: 'get'
  })
}

// 发送重置密码验证码
export const sendResetPasswordCode = (email: string): Promise<ApiResponse<string>> => {
  return request({
    url: '/api/verification/send',
    method: 'get',
    params: {
      email: email,
      purpose: 'reset-password'
    }
  })
}

// 验证重置密码验证码
export const verifyResetPasswordCode = (email: string, verificationCode: string): Promise<ApiResponse<boolean>> => {
  return request({
    url: '/api/user/auth/verify-reset-password-code',
    method: 'post',
    data: { email, verificationCode }
  })
}

// 重置密码
export const resetPassword = (email: string, newPassword: string, verificationCode: string): Promise<ApiResponse<boolean>> => {
  return request({
    url: '/api/user/auth/reset-password',
    method: 'post',
    data: { email, newPassword, verificationCode }
  })
}