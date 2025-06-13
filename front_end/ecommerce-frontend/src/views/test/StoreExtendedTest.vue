<template>
  <div class="store-extended-test">
    <div class="page-header">
      <h1>扩展店铺API测试</h1>
      <p>测试新的扩展店铺API功能，包含所有前端表单字段</p>
    </div>

    <div class="test-sections">
      <!-- 店铺列表测试 -->
      <div class="test-section">
        <h2>店铺列表（扩展版）</h2>
        <div class="test-controls">
          <el-button type="primary" @click="loadStoresExtended" :loading="loading">
            <el-icon><Refresh /></el-icon>
            加载店铺列表
          </el-button>
        </div>
        
        <div class="test-result" v-if="storesExtended.length > 0">
          <h3>店铺列表 ({{ storesExtended.length }} 个)</h3>
          <div class="store-cards">
            <div v-for="store in storesExtended" :key="store.storeId" class="store-card">
              <div class="store-header">
                <h4>{{ store.storeName }}</h4>
                <el-tag :type="getStatusType(store.status)">{{ getStatusText(store.status) }}</el-tag>
              </div>
              <div class="store-details">
                <p><strong>描述：</strong>{{ store.storeDescription || '无' }}</p>
                <p><strong>类目：</strong>{{ getCategoryText(store.category) }}</p>
                <p><strong>服务承诺：</strong>{{ store.servicePromise?.join(', ') || '无' }}</p>
                <p><strong>客服电话：</strong>{{ store.servicePhone || '无' }}</p>
                <p><strong>客服邮箱：</strong>{{ store.serviceEmail || '无' }}</p>
                <p><strong>营业时间：</strong>{{ store.businessHours || '无' }}</p>
                <p><strong>信用分：</strong>{{ store.creditScore }}</p>
                <p><strong>创建时间：</strong>{{ formatDate(store.createTime) }}</p>
              </div>
              <div class="store-actions">
                <el-button size="small" @click="selectStoreForEdit(store)">选择编辑</el-button>
                <el-button size="small" type="danger" @click="deleteStoreExtended(store.storeId)">删除</el-button>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else-if="!loading" class="empty-state">
          <el-empty description="暂无店铺数据" />
        </div>
      </div>

      <!-- 创建店铺测试 -->
      <div class="test-section">
        <h2>创建店铺（扩展版）</h2>
        <StoreForm
          :use-extended-api="true"
          @cancel="handleFormCancel"
          @success="handleCreateSuccess"
        />
      </div>

      <!-- 编辑店铺测试 -->
      <div class="test-section" v-if="selectedStore">
        <h2>编辑店铺（扩展版）</h2>
        <div class="current-store-info">
          <h3>当前选中店铺：{{ selectedStore.storeName }}</h3>
          <el-button @click="selectedStore = null" size="small">取消选择</el-button>
        </div>
        <StoreForm
          :edit-store="selectedStore"
          :use-extended-api="true"
          mode="edit"
          @cancel="handleFormCancel"
          @success="handleUpdateSuccess"
        />
      </div>

      <!-- API响应日志 -->
      <div class="test-section">
        <h2>API响应日志</h2>
        <div class="log-container">
          <div v-for="(log, index) in apiLogs" :key="index" class="log-entry">
            <div class="log-header">
              <span class="log-time">{{ log.time }}</span>
              <span class="log-method" :class="log.method.toLowerCase()">{{ log.method }}</span>
              <span class="log-url">{{ log.url }}</span>
              <span class="log-status" :class="log.success ? 'success' : 'error'">
                {{ log.success ? '成功' : '失败' }}
              </span>
            </div>
            <div class="log-body">
              <pre>{{ JSON.stringify(log.data, null, 2) }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import StoreForm from '@/components/merchant/StoreForm.vue'
import { useAuthStore } from '@/store/modules/auth'
import {
  getStoresExtendedByMerchantId,
  createStoreExtended,
  updateStoreExtended,
  deleteStoreExtended as deleteStoreExtendedApi,
  type StoreExtended,
  type StoreExtendedCreateData,
  type StoreExtendedUpdateData
} from '@/api/merchant/storeExtended'
import type { StoreCreateData, StoreUpdateData } from '@/api/merchant/store'

const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const storesExtended = ref<StoreExtended[]>([])
const selectedStore = ref<StoreExtended | null>(null)
const apiLogs = ref<Array<{
  time: string
  method: string
  url: string
  success: boolean
  data: any
}>>([])

// 方法
const addApiLog = (method: string, url: string, success: boolean, data: any) => {
  apiLogs.value.unshift({
    time: new Date().toLocaleTimeString(),
    method,
    url,
    success,
    data
  })
  
  // 只保留最近20条日志
  if (apiLogs.value.length > 20) {
    apiLogs.value = apiLogs.value.slice(0, 20)
  }
}

const loadStoresExtended = async () => {
  if (!authStore.merchantInfo?.merchantId) {
    ElMessage.warning('请先登录')
    return
  }

  loading.value = true
  try {
    const response = await getStoresExtendedByMerchantId(authStore.merchantInfo.merchantId)
    addApiLog('GET', `/api/merchant/store-extended/merchant/${authStore.merchantInfo.merchantId}`, response.success, response)
    
    if (response.success && response.data) {
      storesExtended.value = response.data
      ElMessage.success(`成功加载 ${response.data.length} 个店铺`)
    } else {
      ElMessage.error(response.message || '加载店铺列表失败')
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    ElMessage.error('加载店铺列表失败')
    addApiLog('GET', `/api/merchant/store-extended/merchant/${authStore.merchantInfo.merchantId}`, false, error)
  } finally {
    loading.value = false
  }
}

const selectStoreForEdit = (store: StoreExtended) => {
  selectedStore.value = store
  ElMessage.info(`已选择店铺"${store.storeName}"进行编辑`)
}

const handleFormCancel = () => {
  selectedStore.value = null
}

const handleCreateSuccess = async (storeData: StoreCreateData | StoreUpdateData | StoreExtendedCreateData | StoreExtendedUpdateData) => {
  try {
    const response = await createStoreExtended(storeData as StoreExtendedCreateData)
    addApiLog('POST', '/api/merchant/store-extended', response.success, { request: storeData, response })
    
    if (response.success && response.data) {
      storesExtended.value.unshift(response.data)
      ElMessage.success(`店铺"${response.data.storeName}"创建成功！`)
    } else {
      ElMessage.error(response.message || '创建店铺失败')
    }
  } catch (error) {
    console.error('创建店铺失败:', error)
    ElMessage.error('创建店铺失败')
    addApiLog('POST', '/api/merchant/store-extended', false, { request: storeData, error })
  }
}

const handleUpdateSuccess = async (storeData: StoreCreateData | StoreUpdateData | StoreExtendedCreateData | StoreExtendedUpdateData, storeId?: number) => {
  if (!storeId) {
    ElMessage.error('店铺ID缺失')
    return
  }
  
  try {
    const response = await updateStoreExtended(storeId, storeData as StoreExtendedUpdateData)
    addApiLog('PUT', `/api/merchant/store-extended/${storeId}`, response.success, { request: storeData, response })
    
    if (response.success && response.data) {
      // 更新本地数据
      const index = storesExtended.value.findIndex(store => store.storeId === storeId)
      if (index !== -1) {
        storesExtended.value[index] = response.data
      }
      selectedStore.value = null
      ElMessage.success(`店铺"${response.data.storeName}"更新成功！`)
    } else {
      ElMessage.error(response.message || '更新店铺失败')
    }
  } catch (error) {
    console.error('更新店铺失败:', error)
    ElMessage.error('更新店铺失败')
    addApiLog('PUT', `/api/merchant/store-extended/${storeId}`, false, { request: storeData, error })
  }
}

const deleteStoreExtended = async (storeId: number) => {
  const store = storesExtended.value.find(s => s.storeId === storeId)
  if (!store) return

  try {
    await ElMessageBox.confirm(
      `确定要删除店铺"${store.storeName}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteStoreExtendedApi(storeId)
    addApiLog('DELETE', `/api/merchant/store-extended/${storeId}`, response.success, response)
    
    if (response.success) {
      // 从列表中移除
      const index = storesExtended.value.findIndex(s => s.storeId === storeId)
      if (index !== -1) {
        storesExtended.value.splice(index, 1)
      }
      
      // 如果删除的是当前选中的店铺，清除选择
      if (selectedStore.value?.storeId === storeId) {
        selectedStore.value = null
      }
      
      ElMessage.success(`店铺"${store.storeName}"删除成功！`)
    } else {
      ElMessage.error(response.message || '删除店铺失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除店铺失败:', error)
      ElMessage.error('删除店铺失败')
      addApiLog('DELETE', `/api/merchant/store-extended/${storeId}`, false, error)
    }
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'open': return 'success'
    case 'suspended': return 'warning'
    case 'closed': return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'open': return '营业中'
    case 'suspended': return '已暂停'
    case 'closed': return '已关闭'
    default: return '未知'
  }
}

const getCategoryText = (category?: string) => {
  const categoryMap: Record<string, string> = {
    clothing: '服装鞋帽',
    electronics: '数码电器',
    food: '食品饮料',
    beauty: '美妆护肤',
    home: '家居用品',
    sports: '运动户外',
    baby: '母婴用品',
    books: '图书文具',
    other: '其他'
  }
  return category ? (categoryMap[category] || category) : '无'
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  loadStoresExtended()
})
</script>

<style scoped>
.store-extended-test {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.test-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.test-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.test-controls {
  margin-bottom: 16px;
}

.store-cards {
  display: grid;
  gap: 16px;
}

.store-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
}

.store-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.store-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.store-header h4 {
  margin: 0;
  color: #333;
}

.store-details {
  margin-bottom: 16px;
}

.store-details p {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}

.store-actions {
  display: flex;
  gap: 8px;
}

.current-store-info {
  background: #f0f9ff;
  border: 1px solid #409eff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-store-info h3 {
  margin: 0;
  color: #409eff;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.log-container {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
}

.log-entry {
  border-bottom: 1px solid #f0f0f0;
}

.log-entry:last-child {
  border-bottom: none;
}

.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8f9fa;
  font-size: 12px;
}

.log-time {
  color: #666;
  min-width: 80px;
}

.log-method {
  padding: 2px 8px;
  border-radius: 4px;
  color: white;
  font-weight: bold;
  min-width: 60px;
  text-align: center;
}

.log-method.get { background: #67c23a; }
.log-method.post { background: #409eff; }
.log-method.put { background: #e6a23c; }
.log-method.delete { background: #f56c6c; }

.log-url {
  flex: 1;
  color: #333;
  font-family: monospace;
}

.log-status {
  padding: 2px 8px;
  border-radius: 4px;
  color: white;
  font-weight: bold;
}

.log-status.success { background: #67c23a; }
.log-status.error { background: #f56c6c; }

.log-body {
  padding: 12px 16px;
  background: #fafafa;
  font-family: monospace;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.log-body pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}

@media (max-width: 768px) {
  .store-extended-test {
    padding: 16px;
  }
  
  .store-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .current-store-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .log-header {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style> 