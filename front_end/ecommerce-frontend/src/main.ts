/**
 * 电商平台前端应用程序主入口文件
 * 
 * @description 这是Vue 3 + TypeScript + Vite构建的电商平台前端应用的主入口文件。
 *              负责应用程序的初始化、依赖项配置、插件注册和应用挂载。
 * 
 * @features
 * - Vue 3应用程序创建和配置
 * - Pinia状态管理集成
 * - Element Plus UI框架集成和中文本地化
 * - Vue Router路由系统配置
 * - 全局样式导入
 * - Element Plus图标全局注册
 * - 浏览器扩展程序错误处理
 * 
 * @dependencies
 * - vue: Vue 3框架核心
 * - pinia: 状态管理库
 * - element-plus: UI组件库
 * - vue-router: 路由管理
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link https://vuejs.org/} Vue.js官方文档
 * @see {@link https://element-plus.org/} Element Plus文档
 * @see {@link https://pinia.vuejs.org/} Pinia文档
 */

// 处理浏览器扩展程序的runtime.lastError错误
if (typeof window !== 'undefined') {
  // 忽略Chrome扩展程序的连接错误
  const originalError = console.error
  console.error = (...args) => {
    if (args[0] && typeof args[0] === 'string' && 
        (args[0].includes('runtime.lastError') || 
         args[0].includes('Could not establish connection') ||
         args[0].includes('Receiving end does not exist'))) {
      // 静默处理扩展程序错误
      return
    }
    originalError.apply(console, args)
  }
}

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'
import './assets/main.css' // 导入全局CSS

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入Element Plus中文语言包
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 导入商家登录页面
import MerchantLogin from './views/merchant/login/index.vue'

// 创建应用实例
const app = createApp(App)
const pinia = createPinia()

// 加载组件
app.component('MerchantLogin', MerchantLogin)

// 使用Pinia状态管理
app.use(pinia)

// 使用Element Plus，配置中文
app.use(ElementPlus, {
  locale: zhCn,
})

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用路由
app.use(router)

// 挂载应用
app.mount('#app')
