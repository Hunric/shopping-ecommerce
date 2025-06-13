<template>
  <div class="store-edit-test">
    <el-card>
      <template #header>
        <span>店铺编辑功能测试</span>
      </template>
      
      <div class="test-section">
        <h3>选择要编辑的店铺</h3>
        
        <el-select 
          v-model="selectedStoreId" 
          placeholder="请选择店铺"
          style="width: 300px"
          @change="handleStoreSelect"
        >
          <el-option
            v-for="store in stores"
            :key="store.storeId"
            :label="store.storeName"
            :value="store.storeId"
          />
        </el-select>
        
        <el-button 
          type="primary" 
          @click="loadStores" 
          style="margin-left: 16px"
        >
          刷新店铺列表
        </el-button>
      </div>
      
      <el-divider />
      
      <div v-if="selectedStore" class="test-section">
        <h3>当前选中店铺信息</h3>
        <div class="store-info">
          <p><strong>店铺ID:</strong> {{ selectedStore.storeId }}</p>
          <p><strong>店铺名称:</strong> {{ selectedStore.storeName }}</p>
          <p><strong>店铺描述:</strong> {{ selectedStore.storeDescription || '暂无描述' }}</p>
          <p><strong>店铺状态:</strong> {{ getStatusText(selectedStore.status) }}</p>
          <p><strong>信用评分:</strong> {{ selectedStore.creditScore }}</p>
        </div>
        
        <el-button 
          type="primary" 
          @click="showEditForm = true"
          style="margin-top: 16px"
        >
          开始编辑
        </el-button>
      </div>
      
      <el-divider v-if="selectedStore" />
      
      <div v-if="showEditForm && selectedStore" class="test-section">
        <h3>编辑店铺表单</h3>
        <StoreForm
          :edit-store="selectedStore"
          mode="edit"
          @cancel="showEditForm = false"
          @success="handleUpdateSuccess"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'
import { getStoresByMerchantId, updateStore } from '@/api/merchant/store'
import type { Store, StoreUpdateData, StoreCreateData } from '@/api/merchant/store'
import type { StoreExtendedCreateData, StoreExtendedUpdateData } from '@/api/merchant/storeExtended'
import StoreForm from '@/components/merchant/StoreForm.vue'

const authStore = useAuthStore()

const stores = ref<Store[]>([])
const selectedStoreId = ref<number | null>(null)
const selectedStore = ref<Store | null>(null)
const showEditForm = ref(false)

const loadStores = async () => {
  try {
    if (!authStore.merchantInfo?.merchantId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const response = await getStoresByMerchantId(authStore.merchantInfo.merchantId)
    if (response.success && response.data) {
      stores.value = response.data
      ElMessage.success(`加载了 ${response.data.length} 个店铺`)
    } else {
      ElMessage.error(response.message || '加载店铺列表失败')
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    ElMessage.error('加载店铺列表失败')
  }
}

const handleStoreSelect = (storeId: number) => {
  selectedStore.value = stores.value.find(store => store.storeId === storeId) || null
  showEditForm.value = false
}

const handleUpdateSuccess = async (updateData: StoreCreateData | StoreUpdateData | StoreExtendedCreateData | StoreExtendedUpdateData, storeId?: number) => {
  if (!storeId) {
    ElMessage.error('店铺ID缺失')
    return
  }
  
  try {
    const response = await updateStore(storeId, updateData as StoreUpdateData)
    if (response.success && response.data) {
      // 更新本地数据
      const storeIndex = stores.value.findIndex(store => store.storeId === storeId)
      if (storeIndex !== -1) {
        stores.value[storeIndex] = response.data
        selectedStore.value = response.data
      }
      
      showEditForm.value = false
      ElMessage.success(`店铺"${response.data.storeName}"更新成功！`)
    } else {
      ElMessage.error(response.message || '更新店铺失败')
    }
  } catch (error) {
    console.error('更新店铺失败:', error)
    ElMessage.error('更新店铺失败，请稍后重试')
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'open':
      return '营业中'
    case 'suspended':
      return '已暂停'
    case 'closed':
      return '已关闭'
    default:
      return '未知'
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleString('zh-CN')
}

onMounted(() => {
  authStore.initializeAuth()
  loadStores()
})
</script>

<style scoped>
.store-edit-test {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
}

.test-section {
  margin-bottom: 20px;
}

.test-section h3 {
  color: #333;
  margin-bottom: 16px;
}

.store-info {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.store-info p {
  margin: 8px 0;
  color: #333;
}

.store-info strong {
  color: #495057;
  margin-right: 8px;
}
</style> 