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

// 挂载应用
app.use(router)
app.mount('#app')
