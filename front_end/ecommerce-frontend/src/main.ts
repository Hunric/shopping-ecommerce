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
