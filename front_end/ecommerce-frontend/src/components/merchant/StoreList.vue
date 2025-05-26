<template>
  <div class="store-list">
    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索店铺名称"
        style="width: 300px"
        clearable
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select
        v-model="statusFilter"
        placeholder="店铺状态"
        style="width: 150px; margin-left: 16px"
        clearable
        @change="handleStatusFilter"
      >
        <el-option label="营业中" value="open" />
        <el-option label="已关闭" value="closed" />
        <el-option label="已暂停" value="suspended" />
      </el-select>
      
      <el-button type="primary" @click="refreshStoreList" style="margin-left: 16px">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 店铺列表 -->
    <div class="store-cards" v-loading="loading">
      <div v-if="filteredStores.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无店铺数据">
          <el-button type="primary" @click="$emit('create-store')">
            <el-icon><Plus /></el-icon>
            创建第一个店铺
          </el-button>
        </el-empty>
      </div>
      
      <div
        v-for="store in filteredStores"
        :key="store.storeId"
        class="store-card"
      >
        <div class="store-header">
          <div class="store-logo">
            <img
              v-if="store.storeLogo"
              :src="store.storeLogo"
              :alt="store.storeName"
              @error="handleImageError"
            />
            <el-icon v-else class="default-logo"><Shop /></el-icon>
          </div>
          
          <div class="store-info">
            <h3>{{ store.storeName }}</h3>
            <p class="store-description">{{ store.storeDescription || '暂无描述' }}</p>
            <div class="store-meta">
              <span class="open-time">开店时间：{{ formatDate(store.openTime) }}</span>
              <span class="credit-score">信用分：{{ store.creditScore }}</span>
            </div>
          </div>
          
          <div class="store-status">
            <el-tag
              :type="getStatusType(store.status)"
              size="large"
            >
              {{ getStatusText(store.status) }}
            </el-tag>
          </div>
        </div>
        
        <div class="store-actions">
          <el-button size="small" @click="viewStore(store)">
            <el-icon><View /></el-icon>
            查看
          </el-button>
          <el-button size="small" type="primary" @click="editStore(store)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button
            size="small"
            :type="store.status === 'open' ? 'warning' : 'success'"
            @click="toggleStoreStatus(store)"
          >
            <el-icon><Switch /></el-icon>
            {{ store.status === 'open' ? '暂停营业' : '恢复营业' }}
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="deleteStore(store)"
            :disabled="store.status === 'open'"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑店铺对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑店铺"
      width="600px"
      @close="resetEditForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="店铺名称" prop="storeName">
          <el-input v-model="editForm.storeName" placeholder="请输入店铺名称" />
        </el-form-item>
        
        <el-form-item label="店铺Logo" prop="storeLogo">
          <el-input v-model="editForm.storeLogo" placeholder="请输入Logo URL" />
        </el-form-item>
        
        <el-form-item label="店铺描述" prop="storeDescription">
          <el-input
            v-model="editForm.storeDescription"
            type="textarea"
            :rows="4"
            placeholder="请输入店铺描述"
          />
        </el-form-item>
        
        <el-form-item label="店铺状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="营业中" value="open" />
            <el-option label="已关闭" value="closed" />
            <el-option label="已暂停" value="suspended" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitting">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Refresh, Plus, Shop, View, Edit, Switch, Delete
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/modules/auth'
import {
  getStoresByMerchantId,
  updateStore,
  deleteStore as deleteStoreApi,
  type Store,
  type UpdateStoreRequest
} from '@/api/merchant/store'

// 定义事件
const emit = defineEmits(['create-store', 'view-store'])

const authStore = useAuthStore()

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const stores = ref<Store[]>([])
const searchKeyword = ref('')
const statusFilter = ref('')

// 编辑对话框
const editDialogVisible = ref(false)
const editFormRef = ref()
const editForm = ref({
  storeId: 0,
  storeName: '',
  storeLogo: '',
  storeDescription: '',
  status: 'open'
})

// 表单验证规则
const editRules = {
  storeName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 50, message: '店铺名称长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const filteredStores = computed(() => {
  let result = stores.value

  // 按关键词搜索
  if (searchKeyword.value) {
    result = result.filter(store =>
      store.storeName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 按状态筛选
  if (statusFilter.value) {
    result = result.filter(store => store.status === statusFilter.value)
  }

  return result
})

// 方法
const loadStores = async () => {
  if (!authStore.merchantInfo?.merchantId) {
    ElMessage.error('商家信息不完整')
    return
  }

  loading.value = true
  try {
    const response = await getStoresByMerchantId(authStore.merchantInfo.merchantId)
    if (response.success) {
      stores.value = response.data
    } else {
      ElMessage.error(response.message || '获取店铺列表失败')
    }
  } catch (error) {
    console.error('获取店铺列表失败:', error)
    ElMessage.error('获取店铺列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const refreshStoreList = () => {
  loadStores()
}

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const handleStatusFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

const viewStore = (store: Store) => {
  emit('view-store', store)
}

const editStore = (store: Store) => {
  editForm.value = {
    storeId: store.storeId,
    storeName: store.storeName,
    storeLogo: store.storeLogo || '',
    storeDescription: store.storeDescription || '',
    status: store.status
  }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const updateData: UpdateStoreRequest = {
      storeName: editForm.value.storeName,
      storeLogo: editForm.value.storeLogo,
      storeDescription: editForm.value.storeDescription,
      status: editForm.value.status
    }

    const response = await updateStore(editForm.value.storeId, updateData)
    if (response.success) {
      ElMessage.success('店铺更新成功')
      editDialogVisible.value = false
      await loadStores()
    } else {
      ElMessage.error(response.message || '更新店铺失败')
    }
  } catch (error) {
    console.error('更新店铺失败:', error)
    ElMessage.error('更新店铺失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const resetEditForm = () => {
  editForm.value = {
    storeId: 0,
    storeName: '',
    storeLogo: '',
    storeDescription: '',
    status: 'open'
  }
  if (editFormRef.value) {
    editFormRef.value.clearValidate()
  }
}

const toggleStoreStatus = async (store: Store) => {
  const newStatus = store.status === 'open' ? 'suspended' : 'open'
  const action = newStatus === 'open' ? '恢复营业' : '暂停营业'

  try {
    await ElMessageBox.confirm(
      `确定要${action}店铺"${store.storeName}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await updateStore(store.storeId, { status: newStatus })
    if (response.success) {
      ElMessage.success(`${action}成功`)
      await loadStores()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败，请稍后重试`)
    }
  }
}

const deleteStore = async (store: Store) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除店铺"${store.storeName}"吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    const response = await deleteStoreApi(store.storeId)
    if (response.success) {
      ElMessage.success('删除成功')
      await loadStores()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除店铺失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'open':
      return 'success'
    case 'suspended':
      return 'warning'
    case 'closed':
      return 'danger'
    default:
      return 'info'
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
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 生命周期
onMounted(() => {
  loadStores()
})
</script>

<style scoped>
.store-list {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.store-cards {
  display: grid;
  gap: 16px;
}

.store-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.store-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.store-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.store-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.store-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-logo {
  font-size: 24px;
  color: #909399;
}

.store-info {
  flex: 1;
}

.store-info h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.store-description {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.store-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.store-status {
  flex-shrink: 0;
}

.store-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .store-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .store-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style> 