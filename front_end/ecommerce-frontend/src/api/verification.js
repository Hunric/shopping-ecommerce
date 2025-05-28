import axios from 'axios';

// API基础URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/merchant';

// 验证码API
export const verificationApi = {
  /**
   * 发送验证码
   * @param {string} email - 邮箱地址
   * @param {string} purpose - 用途（login/register）
   * @returns {Promise} - 返回Promise对象
   */
  sendVerificationCode: (email, purpose) => {
    // 尝试使用GET请求和URL参数
    return axios.get(`${API_BASE_URL}/api/verification/send`, {
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
    // 尝试使用GET请求和URL参数
    return axios.get(`${API_BASE_URL}/api/verification/verify`, {
      params: {
        email,
        code,
        purpose
      }
    });
  }
}; 