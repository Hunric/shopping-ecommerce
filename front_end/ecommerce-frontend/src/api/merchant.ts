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
    return request.post('/api/merchant/register', registerData)
  },

  /**
   * 发送登录验证码
   * @param {string} email - 商家邮箱
   * @returns {Promise} - 返回Promise对象
   */
  sendLoginCode: (email: string) => {
    return request.post('/api/merchant/send-login-code', { email })
  },

  /**
   * 验证码登录
   * @param {string} email - 商家邮箱
   * @param {string} verificationCode - 验证码
   * @returns {Promise} - 返回Promise对象
   */
  login: (email: string, verificationCode: string) => {
    return request.post('/api/merchant/login', {
      email,
      verificationCode
    })
  }
}

export default merchantApi
export { merchantApi } 