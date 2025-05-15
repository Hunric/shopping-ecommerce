import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'

// 导入商家登录页面
import MerchantLogin from './views/merchant/login/index.vue'

// 创建应用实例
const app = createApp(App)

// 加载组件
app.component('MerchantLogin', MerchantLogin)

// 挂载应用
app.use(router)
app.mount('#app')
