import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  server: {
    port: 3000,
    // 在开发环境下，request工具会自动处理不同服务的baseURL
    // 如果需要代理，可以根据实际情况配置
    proxy: {
      // 如果所有服务都在同一个网关后面，可以简化为：
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
