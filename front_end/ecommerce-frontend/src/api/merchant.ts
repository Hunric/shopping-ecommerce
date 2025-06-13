/**
 * 电商平台商家服务API接口文件
 * 
 * @description 定义了商家服务相关的API接口，包括商家注册、登录认证、
 *              验证码发送等功能。提供类型安全的API调用方法。
 * 
 * @features
 * - 商家注册API接口
 * - 验证码登录API接口
 * - 密码登录API接口
 * - 登录验证码发送API接口
 * - 重置密码相关API接口
 * - TypeScript类型定义
 * - 统一的错误处理
 * 
 * @interfaces
 * - MerchantRegisterData: 商家注册数据类型定义
 * 
 * @api_endpoints
 * - POST /api/merchant/register: 商家注册
 * - POST /api/merchant/send-login-code: 发送登录验证码
 * - POST /api/merchant/login: 验证码登录
 * - POST /api/merchant/login/password: 密码登录
 * - POST /api/merchant/send-reset-password-code: 发送重置密码验证码
 * - POST /api/merchant/verify-reset-password-code: 验证重置密码验证码
 * - POST /api/merchant/reset-password: 重置密码
 * 
 * @dependencies
 * - @/utils/request: HTTP请求工具
 * 
 * @author 开发团队
 * @version 1.1.0
 * @since 2024
 * 
 * @see {@link ../utils/request.ts} HTTP请求工具文档
 */

import request from '@/utils/request'

// 商家注册数据接口
export interface MerchantRegisterData {
  merchantName: string
  merchantType: string
  email: string
  password: string
  licenseNumber: string
  legalPersonName: string
  legalPersonId: string
  contactName: string
  contactPhone: string
  contactEmail: string
  addressCodes: string[]
  detailAddress: string
}

// 商家API
const merchantApi = {
  /**
   * 商家注册
   * @param {MerchantRegisterData} registerData - 注册数据
   * @returns {Promise} - 返回Promise对象
   */
  register: (registerData: MerchantRegisterData) => {
    return request({
      url: '/api/merchant/register',
      method: 'post',
      data: registerData
    })
  },

  /**
   * 发送登录验证码
   * @param {string} email - 商家邮箱
   * @returns {Promise} - 返回Promise对象
   */
  sendLoginCode: (email: string) => {
    return request({
      url: '/api/merchant/send-login-code',
      method: 'post',
      data: { email }
    })
  },

  /**
   * 验证码登录
   * @param {string} email - 商家邮箱
   * @param {string} verificationCode - 验证码
   * @returns {Promise} - 返回Promise对象
   */
  login: (email: string, verificationCode: string) => {
    return request({
      url: '/api/merchant/login',
      method: 'post',
      data: {
        email,
        verificationCode
      }
    })
  },

  /**
   * 密码登录
   * @param {string} email - 商家邮箱
   * @param {string} password - 登录密码
   * @returns {Promise} - 返回Promise对象
   */
  loginWithPassword: (email: string, password: string) => {
    return request({
      url: '/api/merchant/login/password',
      method: 'post',
      data: {
        email,
        password
      }
    })
  },
  
  /**
   * 发送重置密码验证码
   * @param {string} email - 商家邮箱
   * @returns {Promise} - 返回Promise对象
   */
  sendResetPasswordCode: (email: string) => {
    return request({
      url: '/api/merchant/send-reset-password-code',
      method: 'post',
      data: { email }
    })
  },
  
  /**
   * 验证重置密码验证码
   * @param {string} email - 商家邮箱
   * @param {string} verificationCode - 验证码
   * @returns {Promise} - 返回Promise对象
   */
  verifyResetPasswordCode: (email: string, verificationCode: string) => {
    return request({
      url: '/api/merchant/verify-reset-password-code',
      method: 'post',
      data: {
        email,
        verificationCode
      }
    })
  },
  
  /**
   * 重置密码
   * @param {string} email - 商家邮箱
   * @param {string} newPassword - 新密码
   * @param {string} verificationCode - 验证码
   * @returns {Promise} - 返回Promise对象
   */
  resetPassword: (email: string, newPassword: string, verificationCode: string) => {
    return request({
      url: '/api/merchant/reset-password',
      method: 'post',
      data: {
        email,
        newPassword,
        verificationCode
      }
    })
  }
}

export default merchantApi
export { merchantApi } 