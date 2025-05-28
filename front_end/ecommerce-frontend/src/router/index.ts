import { createRouter, createWebHistory } from 'vue-router'
import MerchantLogin from '@/views/merchant/login/index.vue'
import MerchantRegister from '@/views/merchant/register/index.vue'
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