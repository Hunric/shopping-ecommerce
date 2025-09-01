/**
 * 电商平台前端Vite构建配置文件
 * 
 * @description Vite构建工具的配置文件，定义了开发服务器、构建选项、
 *              插件配置、路径别名、代理设置等构建相关的配置。
 * 
 * @features
 * - Vue 3单文件组件支持
 * - TypeScript编译配置
 * - 路径别名配置（@指向src目录）
 * - 开发服务器配置（端口3000）
 * - API代理配置（开发环境）
 * - 热模块替换（HMR）支持
 * - 生产环境构建优化
 * 
 * @development_server
 * - 端口: 3000
 * - 热重载: 启用
 * - 代理: /api -> 对应的后端服务
 * 
 * @build_configuration
 * - 目标: ES2020
 * - 模块格式: ESM
 * - 代码分割: 自动
 * - 资源优化: 启用
 * 
 * @plugins
 * - @vitejs/plugin-vue: Vue 3单文件组件支持
 * 
 * @path_aliases
 * - @: 指向src目录，简化导入路径
 * 
 * @proxy_configuration
 * 开发环境下的API代理配置：
 * - 商家服务: http://localhost:8081
 * - 文件服务: http://localhost:8082
 * - 跨域处理: 启用
 * - HTTPS: 禁用（开发环境）
 * 
 * @environment_variables
 * 支持的环境变量：
 * - VITE_API_BASE_URL: API基础地址
 * - VITE_MERCHANT_API_BASE_URL: 商家服务地址
 * - VITE_FILE_API_BASE_URL: 文件服务地址
 * 
 * @dependencies
 * - vite: 构建工具核心
 * - @vitejs/plugin-vue: Vue插件
 * - vue: Vue 3框架
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link https://vitejs.dev/config/} Vite配置文档
 * @see {@link https://vuejs.org/} Vue.js官方文档
 */

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 3000,
    host: '0.0.0.0',
    // 配置代理以支持多服务架构
    proxy: {
      // 直接/merchant路径的代理 - 主要用于orders等API
      '/merchant': {  // 代理所有/merchant路径，但排除前端路由
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('代理错误:', err.message);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('发送代理请求:', req.method, req.url, '->', proxyReq.getHeader('host') + proxyReq.path);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('收到代理响应:', proxyRes.statusCode, req.url);
          });
        },
        bypass: (req, res, options) => {
          // 排除前端路由路径，只代理API请求
          const path = req.url || '';
          console.log('检查路径是否需要代理:', path);
          
          // 如果是前端路由路径，则不代理
          if (path.startsWith('/merchant/login') ||
              path.startsWith('/merchant/register') ||
              path.startsWith('/merchant/forget-password') ||
              path.startsWith('/merchant/dashboard') ||
              path.startsWith('/merchant/category')) {
            console.log('前端路由，不代理:', path);
            return path;
          }
          
          // 对于API请求，继续代理
          console.log('API请求，继续代理:', path);
          return; // 返回undefined表示继续代理
        },
        rewrite: (path) => {
          console.log('重写路径 - 原始:', path);
          // 移除/merchant前缀，避免与context-path重复
          // /merchant/orders/statistics -> /orders/statistics
          const newPath = path.replace('/merchant', '');
          console.log('重写路径 - 结果:', newPath);
          return newPath;
        }
      },
      // 商家服务代理 - 将 /api/merchant 请求代理到商家服务
      '/api/merchant': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          // 修复：商家服务的context-path是/merchant，但控制器已经有/api/merchant前缀
          // 所以只需要添加context-path前缀，不要重复/api/merchant
          console.log('商家服务代理 (原始路径):', path)
          const newPath = '/merchant' + path
          console.log('商家服务代理 (重写后):', newPath)
          return newPath
        }
      },
      // 验证码服务代理
      '/api/verification': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          const newPath = '/merchant' + path
          console.log('验证码服务代理:', path, ' -> ', newPath)
          return newPath
        }
      },

      // 文件服务代理
      '/api/upload': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          console.log('文件服务代理:', path, ' -> ', path)
          return path  // 保持原路径不变
        }
      },
      // 用户收货地址服务代理 - 将 /api/user/shipping 请求代理到订单服务
      '/api/user/shipping': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          console.log('用户收货地址服务代理 (原始路径):', path)
          const newPath = '/order' + path
          console.log('用户收货地址服务代理 (重写后):', newPath)
          return newPath  // 重写路径以匹配订单服务的context-path
        }
      },
      // 用户订单服务代理 - 将 /api/user/orders 请求代理到订单服务
      '/api/user/orders': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          console.log('用户订单服务代理 (原始路径):', path)
          const newPath = '/order' + path
          console.log('用户订单服务代理 (重写后):', newPath)
          return newPath  // 重写路径以匹配订单服务的context-path
        }
      },
      // 用户购物服务代理 - 将 /api/user 请求代理到购物服务
      '/api/user': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          console.log('用户服务代理:', path, ' -> ', path)
          return path  // 保持原路径不变
        }
      },
      // 购物车服务代理 - 将 /api/cart 请求代理到购物服务
      '/api/cart': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          console.log('购物车服务代理:', path, ' -> ', path)
          return path  // 保持原路径不变
        }
      },
      // 订单服务代理 - 将 /api/order 请求代理到订单服务
      '/api/order': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          console.log('订单服务代理:', path, ' -> ', path.replace('/api/order', '/order/api/order'))
          return path.replace('/api/order', '/order/api/order')  // 重写路径以匹配订单服务的context-path
        }
      },
      // 文件访问代理
      '/uploads': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
