import axios from 'axios';

// 验证码API
export const verificationApi = {
  /**
   * 发送验证码
   * @param {string} email - 邮箱地址
   * @param {string} purpose - 用途（login/register）
   * @returns {Promise} - 返回Promise对象
   */
  sendVerificationCode: (email, purpose) => {
    // 使用代理路径，不再直连后端
    return axios.get(`/api/verification/send`, {
      params: {
        email,
        purpose
      }
    });
  },
  
  /**
   * 验证验证码
   * @param {string} email - 邮箱地址
   * @param {string} code - 验证码
   * @param {string} purpose - 用途（login/register）
   * @returns {Promise} - 返回Promise对象
   */
  verifyCode: (email, code, purpose) => {
    // 使用代理路径，不再直连后端
    return axios.get(`/api/verification/verify`, {
      params: {
        email,
        code,
        purpose
      }
    });
  }
}; 