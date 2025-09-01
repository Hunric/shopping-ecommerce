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
import { useAuthStore } from '@/store/modules/auth'
import { ElMessage } from 'element-plus'

// 用户端页面
import UserHome from '@/views/user/home/index.vue'
import TestHome from '@/views/user/home/test.vue'
import UserLogin from '@/views/user/auth/login.vue'
import UserRegister from '@/views/user/auth/register.vue'
// 商家端页面
import MerchantLogin from '@/views/merchant/login/index.vue'
import MerchantRegister from '@/views/merchant/register/index.vue'
import MerchantForgetPassword from '@/views/merchant/login/forget-password.vue'
import MerchantDashboard from '@/views/merchant/dashboard/index.vue'
import AttributeManagement from '@/views/merchant/AttributeManagement.vue'
// 测试页面
import ApiTest from '@/views/test/ApiTest.vue'
import DraftTest from '@/views/test/DraftTest.vue'
import StoreEditTest from '@/views/test/StoreEditTest.vue'
import StoreExtendedTest from '@/views/test/StoreExtendedTest.vue'
import StoreExtendedApiTest from '@/views/test/StoreExtendedApiTest.vue'
import CategoryAttributeTest from '@/views/test/CategoryAttributeTest.vue'
import AttributeTest from '@/views/merchant/product/AttributeTest.vue'
import TokenClearTest from '@/views/test/TokenClearTest.vue'

// 需要商家登录的路由路径
const requiresMerchantAuth = [
  '/merchant/dashboard',
  '/merchant/category'
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    // 用户端路由
    {
      path: '/home',
      name: 'userHome',
      component: UserHome
    },
    {
      path: '/home-original',
      name: 'userHomeOriginal',
      component: UserHome
    },
    {
      path: '/user/home',
      name: 'userHomeAlias',
      component: UserHome
    },
    {
      path: '/user/login',
      name: 'userLogin',
      component: UserLogin
    },
    {
      path: '/user/register',
      name: 'userRegister',
      component: UserRegister
    },
    {
      path: '/user/profile',
      name: 'userProfile',
      component: () => import('@/views/user/profile/index.vue')
    },
    {
      path: '/user/forgot-password',
      name: 'userForgotPassword',
      component: () => import('@/views/user/auth/forgot-password.vue')
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('@/views/user/cart/index.vue')
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: () => import('@/views/user/checkout/index.vue')
    },
    {
      path: '/user/orders',
      name: 'userOrders',
      component: () => import('@/views/user/orders/index.vue')
    },
    {
      path: '/user/orders/:orderId',
      name: 'orderDetail',
      component: () => import('@/views/user/orders/detail.vue')
    },
    {
      path: '/payment/:orderId',
      name: 'payment',
      component: () => import('@/views/user/payment/index.vue')
    },
    {
      path: '/product/:id',
      name: 'productDetail',
      component: () => import('@/views/user/product/detail.vue')
    },
    {
      path: '/categories',
      name: 'categories',
      component: () => import('@/views/user/categories/index.vue')
    },
    {
      path: '/category/:id',
      name: 'categoryDetail',
      component: () => import('@/views/user/category/[id].vue')
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/user/search/index.vue')
    },
    {
      path: '/products',
      name: 'products',
      component: () => import('@/views/user/products/index.vue')
    },
    {
      path: '/special-offers',
      name: 'specialOffers',
      component: () => import('@/views/user/search/index.vue')
    },
    {
      path: '/new-products',
      name: 'newProducts',
      component: () => import('@/views/user/search/index.vue')
    },
    {
      path: '/hot-products',
      name: 'hotProducts',
      component: () => import('@/views/user/search/index.vue')
    },
    {
      path: '/recommended-products',
      name: 'recommendedProducts',
      component: () => import('@/views/user/search/index.vue')
    },
    {
      path: '/sale',
      name: 'sale',
      component: () => import('@/views/user/sale/index.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/user/about/index.vue')
    },
    // 商家端路由
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
    },
    {
      path: '/test/token-clear',
      name: 'tokenClearTest',
      component: TokenClearTest
    },
    {
      path: '/test/jwt-debug',
      name: 'jwtDebugTest',
      component: () => import('@/views/test/JwtDebugTest.vue')
    },
    {
      path: '/test/routes',
      name: 'routeTest',
      component: () => import('@/views/test/RouteTest.vue')
    },
    {
      path: '/test/navbar',
      name: 'navbarTest',
      component: () => import('@/views/test/NavbarTest.vue')
    },
    {
      path: '/test/button-style',
      name: 'buttonStyleTest',
      component: () => import('@/views/test/ButtonStyleTest.vue')
    },
    {
      path: '/test/product-detail',
      name: 'productDetailTest',
      component: () => import('@/views/test/ProductDetailTest.vue')
    },
    {
      path: '/test/cart',
      name: 'cartTest',
      component: () => import('@/views/test/CartTest.vue')
    },
    {
      path: '/test/shipping',
      name: 'shippingTest',
      component: () => import('@/views/test/ShippingTest.vue')
    },
    {
      path: '/test/payment',
      name: 'paymentTest',
      component: () => import('@/views/test/PaymentTest.vue')
    },
    {
      path: '/test/payment-debug',
      name: 'paymentDebug',
      component: () => import('@/views/test/PaymentDebug.vue')
    },
    {
      path: '/test/payment-status',
      name: 'paymentStatusTest',
      component: () => import('@/views/test/PaymentStatusTest.vue')
    }
  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 初始化认证状态
  const authStore = useAuthStore()
  authStore.initializeAuth()
  
  // 检查是否需要商家登录权限
  const needsMerchantAuth = requiresMerchantAuth.some(path => to.path.startsWith(path))
  
  if (needsMerchantAuth) {
    // 如果需要商家登录权限，检查是否已登录
    if (!authStore.isLoggedIn) {
      ElMessage.warning('请先登录商家账号')
      next('/merchant/login')
      return
    }
    
    // 检查token是否过期
    if (authStore.isTokenExpired()) {
      ElMessage.warning('登录已过期，请重新登录')
      authStore.logout()
      next('/merchant/login')
      return
    }
  }
  
  next()
})

export default router 