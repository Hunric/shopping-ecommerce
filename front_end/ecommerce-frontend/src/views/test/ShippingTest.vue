<template>
  <div class="shipping-test">
    <h1>收货地址功能测试</h1>
    
    <div class="test-section">
      <h2>收货地址API测试</h2>
      <div class="api-test-buttons">
        <el-button @click="testGetShippingList" :loading="loading.list">获取收货地址列表</el-button>
        <el-button @click="testCreateShipping" :loading="loading.create">创建测试地址</el-button>
      </div>
      
      <div v-if="apiResults.list" class="api-result">
        <h3>收货地址列表API响应：</h3>
        <pre>{{ JSON.stringify(apiResults.list, null, 2) }}</pre>
      </div>
      
      <div v-if="apiResults.create" class="api-result">
        <h3>创建地址API响应：</h3>
        <pre>{{ JSON.stringify(apiResults.create, null, 2) }}</pre>
      </div>
    </div>
    
    <div class="test-section">
      <h2>确认订单页面测试</h2>
      <p>点击下面的按钮跳转到确认订单页面：</p>
      <el-button @click="goToCheckout" type="primary" size="large">
        前往确认订单页面
      </el-button>
    </div>
    
    <div class="test-section">
      <h2>用户认证状态</h2>
      <div class="auth-info">
        <p><strong>登录状态:</strong> {{ userAuthStore.isLoggedIn ? '已登录' : '未登录' }}</p>
        <p><strong>用户信息:</strong> {{ userAuthStore.userInfo?.username || '无' }}</p>
        <p><strong>Token存在:</strong> {{ !!userAuthStore.accessToken }}</p>
      </div>
    </div>
    
    <div class="test-section">
      <h2>已知问题和修复</h2>
      <ul class="fix-list">
        <li>✅ 修复了收货地址列表API响应处理问题</li>
        <li>✅ 修复了收货地址创建API响应处理问题</li>
        <li>✅ 修复了收货地址删除API响应处理问题</li>
        <li>✅ 修复了确认订单页面购物车数据获取问题</li>
        <li>✅ 添加了详细的调试日志</li>
      </ul>
    </div>
    
    <div class="test-section">
      <h2>API代理配置</h2>
      <div class="proxy-info">
        <p><strong>收货地址API:</strong> /api/user/shipping → 订单服务(8084端口)</p>
        <p><strong>路径重写:</strong> /api/user/shipping → /order/api/user/shipping</p>
        <p><strong>目标服务:</strong> http://localhost:8084</p>
      </div>
    </div>
    
    <div class="test-section">
      <h2>测试步骤</h2>
      <ol class="test-steps">
        <li>确保用户已登录</li>
        <li>点击"获取收货地址列表"测试API</li>
        <li>点击"创建测试地址"测试API</li>
        <li>前往确认订单页面查看地址显示</li>
        <li>测试地址的编辑和删除功能</li>
        <li>检查默认地址的自动选中</li>
      </ol>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShippingInfoList, createShippingInfo, type ShippingInfo } from '@/api/user/shipping'
import { useUserAuthStore } from '@/store/modules/userAuth'

const router = useRouter()
const userAuthStore = useUserAuthStore()

// 响应式数据
const loading = reactive({
  list: false,
  create: false
})

const apiResults = reactive({
  list: null,
  create: null
})

// 测试方法
const testGetShippingList = async () => {
  loading.list = true
  try {
    const response = await getShippingInfoList()
    apiResults.list = response.data
    
    if (response.data.success) {
      ElMessage.success(`获取收货地址列表成功，共${response.data.data?.length || 0}个地址`)
    } else {
      ElMessage.error(response.data.message || '获取收货地址列表失败')
    }
  } catch (error) {
    console.error('测试获取收货地址列表失败:', error)
    ElMessage.error('API调用失败')
    apiResults.list = { error: error.message }
  } finally {
    loading.list = false
  }
}

const testCreateShipping = async () => {
  loading.create = true
  try {
    // 测试创建一个收货地址
    const testShippingData: ShippingInfo = {
      receiverName: '测试用户',
      receiverPhone: '13800138000',
      province: '广东省',
      city: '深圳市',
      district: '南山区',
      detailAddress: '科技园南区软件产业基地',
      isDefault: false
    }
    
    const response = await createShippingInfo(testShippingData)
    apiResults.create = response.data
    
    if (response.data.success) {
      ElMessage.success('创建测试收货地址成功')
    } else {
      ElMessage.error(response.data.message || '创建收货地址失败')
    }
  } catch (error) {
    console.error('测试创建收货地址失败:', error)
    ElMessage.error('API调用失败')
    apiResults.create = { error: error.message }
  } finally {
    loading.create = false
  }
}

const goToCheckout = () => {
  router.push('/checkout')
}

// 初始化
userAuthStore.initializeAuth()
</script>

<style scoped>
.shipping-test {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

.test-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #409eff;
  margin-bottom: 15px;
  font-size: 18px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 5px;
}

.api-test-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.api-result {
  background: #f8f9fa;
  border-radius: 5px;
  padding: 15px;
  border-left: 4px solid #409eff;
  margin-bottom: 15px;
}

.api-result h3 {
  color: #409eff;
  margin-bottom: 10px;
}

.api-result pre {
  background: white;
  padding: 10px;
  border-radius: 3px;
  border: 1px solid #e9ecef;
  overflow-x: auto;
  font-size: 12px;
  max-height: 300px;
}

.auth-info, .proxy-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 5px;
  border-left: 4px solid #67c23a;
}

.auth-info p, .proxy-info p {
  margin: 8px 0;
  font-size: 14px;
}

.fix-list, .test-steps {
  padding-left: 20px;
}

.fix-list li, .test-steps li {
  margin: 8px 0;
  color: #606266;
}

.test-steps {
  list-style-type: decimal;
}

.fix-list {
  list-style: none;
}
</style> 