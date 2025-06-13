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
      // 商家服务代理 - 将 /api/merchant 请求代理到商家服务
      '/api/merchant': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          const newPath = '/merchant' + path
          console.log('商家服务代理:', path, ' -> ', newPath)
          return newPath  // 添加 /merchant 前缀以匹配后端 context-path
        }
      },
      // 验证码服务代理 - common模块通过merchant服务提供
      '/api/verification': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {
          const newPath = '/merchant' + path
          console.log('验证码服务代理:', path, ' -> ', newPath)
          return newPath  // 添加 /merchant 前缀以匹配后端 context-path
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
      // 文件访问代理
      '/uploads': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
