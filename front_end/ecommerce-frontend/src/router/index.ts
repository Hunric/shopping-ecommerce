/**
 * 电商平台前端路由配置文件
 * 
 * @description 定义了整个电商平台前端应用的路由配置，包括商家端路由和测试路由。
 *              使用Vue Router 4实现单页应用(SPA)的页面导航和组件渲染。
 * 
 * @features
 * - 商家端路由配置（登录、注册、仪表板）
 * - 商品属性管理路由
 * - 开发测试路由（API测试、草稿测试等）
 * - 默认路由重定向
 * - 历史模式路由（使用HTML5 History API）
 * 
 * @routes
 * - / : 根路径，重定向到商家登录页
 * - /merchant/* : 商家端相关页面
 * - /test/* : 开发测试页面
 * 
 * @modules
 * - vue-router: Vue.js官方路由管理器
 * - 各种页面组件的导入
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link https://router.vuejs.org/} Vue Router官方文档
 */

import { createRouter, createWebHistory } from 'vue-router'
import MerchantLogin from '@/views/merchant/login/index.vue'
import MerchantRegister from '@/views/merchant/register/index.vue'
import MerchantForgetPassword from '@/views/merchant/login/forget-password.vue'
import MerchantDashboard from '@/views/merchant/dashboard/index.vue'
import AttributeManagement from '@/views/merchant/AttributeManagement.vue'
import ApiTest from '@/views/test/ApiTest.vue'
import DraftTest from '@/views/test/DraftTest.vue'
import StoreEditTest from '@/views/test/StoreEditTest.vue'
import StoreExtendedTest from '@/views/test/StoreExtendedTest.vue'
import StoreExtendedApiTest from '@/views/test/StoreExtendedApiTest.vue'
import CategoryAttributeTest from '@/views/test/CategoryAttributeTest.vue'
import AttributeTest from '@/views/merchant/product/AttributeTest.vue'

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
      path: '/merchant/forget-password',
      name: 'merchantForgetPassword',
      component: MerchantForgetPassword
    },
    {
      path: '/merchant/dashboard',
      name: 'merchantDashboard',
      component: MerchantDashboard
    },
    {
      path: '/merchant/category/:categoryId/attributes',
      name: 'attributeManagement',
      component: AttributeManagement
    },
    {
      path: '/test/api',
      name: 'apiTest',
      component: ApiTest
    },
    {
      path: '/test/draft',
      name: 'draftTest',
      component: DraftTest
    },
    {
      path: '/test/store-edit',
      name: 'storeEditTest',
      component: StoreEditTest
    },
    {
      path: '/test/store-extended',
      name: 'storeExtendedTest',
      component: StoreExtendedTest
    },
    {
      path: '/test/api-extended',
      name: 'storeExtendedApiTest',
      component: StoreExtendedApiTest
    },
    {
      path: '/test/category-attribute',
      name: 'categoryAttributeTest',
      component: CategoryAttributeTest
    },
    {
      path: '/test/attribute-test',
      name: 'attributeTest',
      component: AttributeTest
    }
  ]
})

export default router 