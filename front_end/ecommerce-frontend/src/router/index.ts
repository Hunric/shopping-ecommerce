import { createRouter, createWebHistory } from 'vue-router'
import MerchantLogin from '@/views/merchant/login/index.vue'
import MerchantRegister from '@/views/merchant/register/index.vue'
import MerchantDashboard from '@/views/merchant/dashboard/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/merchant/login'
    },
    {
      path: '/merchant/login',
      name: 'merchantLogin',
      component: MerchantLogin
    },
    {
      path: '/merchant/register',
      name: 'merchantRegister',
      component: MerchantRegister
    },
    {
      path: '/merchant/dashboard',
      name: 'merchantDashboard',
      component: MerchantDashboard
    }
  ]
})

export default router 