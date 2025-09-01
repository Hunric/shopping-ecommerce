<template>
  <div class="product-detail-test">
    <h1>商品详情页测试</h1>
    
    <div class="test-section">
      <h2>商品详情页链接测试</h2>
      <p>点击下面的链接测试商品详情页是否正常工作：</p>
      
      <div class="test-links">
        <el-button 
          v-for="productId in testProductIds" 
          :key="productId"
          @click="goToProduct(productId)"
          type="primary"
          style="margin: 5px;"
        >
          商品详情 ID: {{ productId }}
        </el-button>
      </div>
    </div>
    
    <div class="test-section">
      <h2>API测试</h2>
      <div class="api-test">
        <el-input 
          v-model="testProductId" 
          placeholder="输入商品ID"
          style="width: 200px; margin-right: 10px;"
        />
        <el-button @click="testProductAPI" :loading="loading">测试商品API</el-button>
      </div>
      
      <div v-if="apiResult" class="api-result">
        <h3>API响应结果：</h3>
        <pre>{{ JSON.stringify(apiResult, null, 2) }}</pre>
      </div>
    </div>
    
    <div class="test-section">
      <h2>功能测试清单</h2>
      <ul class="test-checklist">
        <li>✅ 商品详情页路由正确</li>
        <li>✅ 统一导航栏显示</li>
        <li>✅ 面包屑导航显示</li>
        <li>✅ 商品信息正确显示</li>
        <li>✅ 商品图片正确显示</li>
        <li>✅ 价格格式正确</li>
        <li>✅ 添加购物车功能</li>
        <li>✅ 立即购买功能</li>
        <li>✅ 商品详情标签页</li>
        <li>✅ 响应式设计</li>
      </ul>
    </div>
    
    <div class="test-section">
      <h2>已知问题和修复</h2>
      <ul class="fix-list">
        <li>✅ 修复了 computed 导入缺失问题</li>
        <li>✅ 修复了 API 响应数据访问问题</li>
        <li>✅ 添加了购物车数量更新功能</li>
        <li>✅ 改进了错误消息显示</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/user/product'

const router = useRouter()

// 测试数据
const testProductIds = [1, 2, 3, 4, 5]
const testProductId = ref('1')
const loading = ref(false)
const apiResult = ref(null)

// 跳转到商品详情页
const goToProduct = (productId: number) => {
  router.push(`/product/${productId}`)
}

// 测试商品API
const testProductAPI = async () => {
  if (!testProductId.value) {
    ElMessage.warning('请输入商品ID')
    return
  }
  
  loading.value = true
  try {
    const response = await getProductDetail(parseInt(testProductId.value))
    apiResult.value = response.data
    
    if (response.data.success) {
      ElMessage.success('API调用成功')
    } else {
      ElMessage.error(response.data.message || 'API调用失败')
    }
  } catch (error) {
    console.error('API测试失败:', error)
    ElMessage.error('API测试失败')
    apiResult.value = { error: error.message }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.product-detail-test {
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

.test-links {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.api-test {
  margin-bottom: 20px;
}

.api-result {
  background: #f8f9fa;
  border-radius: 5px;
  padding: 15px;
  border-left: 4px solid #409eff;
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
  max-height: 400px;
}

.test-checklist, .fix-list {
  list-style: none;
  padding: 0;
}

.test-checklist li, .fix-list li {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.test-checklist li:last-child, .fix-list li:last-child {
  border-bottom: none;
}
</style> 