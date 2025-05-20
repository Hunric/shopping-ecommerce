import axios from 'axios';

// API基础URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/merchant';

// 商家API
const merchantApi = {
  /**
   * 商家注册
   * @param {Object} registerData - 注册数据
   * @returns {Promise} - 返回Promise对象
   */
  register: (registerData) => {
    return axios.post(`${API_BASE_URL}/api/merchant/register`, registerData);
  }
};

export default merchantApi;
export { merchantApi }; 