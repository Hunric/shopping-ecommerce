import request from '@/utils/request'
import type { ApiResponse } from '@/types/api'

// 商家信息接口
export interface MerchantProfile {
  merchantId: number
  merchantName: string
  merchantType: string
  email: string
  businessLicenseNo: string
  legalPersonName: string
  contactPersonName: string
  contactPhone: string
  contactEmail: string
  province: string
  city: string
  district: string
  detailedAddress: string
}

// 密码修改接口参数
export interface PasswordChangeParams {
  merchantId: number
  currentPassword: string
  newPassword: string
}

/**
 * 获取商家信息
 * @param merchantId 商家ID
 */
export const getMerchantProfile = async (merchantId: number): Promise<ApiResponse<MerchantProfile>> => {
  try {
    const response = await request({
      url: `/api/merchant/info/${merchantId}`,
      method: 'get'
    })
    return response.data
  } catch (error) {
    console.error('获取商家信息失败:', error)
    return {
      success: false,
      message: '获取商家信息失败',
      data: null
    }
  }
}

/**
 * 更新商家信息
 * @param profileData 商家信息数据
 */
export const updateMerchantProfile = async (profileData: MerchantProfile): Promise<ApiResponse<any>> => {
  try {
    const response = await request({
      url: `/api/merchant/info/${profileData.merchantId}`,
      method: 'put',
      data: profileData
    })
    return response.data
  } catch (error) {
    console.error('更新商家信息失败:', error)
    return {
      success: false,
      message: '更新商家信息失败',
      data: null
    }
  }
}

/**
 * 修改密码
 * @param params 密码修改参数
 */
export const changePassword = async (params: PasswordChangeParams): Promise<ApiResponse<any>> => {
  try {
    const response = await request({
      url: `/api/merchant/change-password`,
      method: 'post',
      data: params
    })
    return response.data
  } catch (error) {
    console.error('修改密码失败:', error)
    return {
      success: false,
      message: '修改密码失败',
      data: null
    }
  }
} 