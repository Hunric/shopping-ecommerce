<template>
  <div class="draft-test">
    <el-card>
      <template #header>
        <span>草稿功能测试</span>
      </template>
      
      <div class="test-section">
        <h3>测试草稿服务</h3>
        
        <el-form :model="testForm" label-width="100px">
          <el-form-item label="店铺名称:">
            <el-input v-model="testForm.storeName" placeholder="输入店铺名称" />
          </el-form-item>
          <el-form-item label="店铺描述:">
            <el-input v-model="testForm.storeDescription" type="textarea" placeholder="输入店铺描述" />
          </el-form-item>
          <el-form-item label="经营类目:">
            <el-select v-model="testForm.category" placeholder="选择类目">
              <el-option label="服装鞋帽" value="clothing" />
              <el-option label="数码电器" value="electronics" />
              <el-option label="食品饮料" value="food" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveDraft">保存草稿</el-button>
            <el-button @click="loadDrafts">加载草稿列表</el-button>
            <el-button @click="clearForm">清空表单</el-button>
          </el-form-item>
        </el-form>
        
        <div v-if="currentDraftId" class="current-draft">
          <p><strong>当前草稿ID:</strong> {{ currentDraftId }}</p>
        </div>
      </div>
      
      <el-divider />
      
      <div class="test-section">
        <h3>草稿列表</h3>
        <div v-if="draftList.length === 0" class="empty">
          <p>暂无草稿</p>
        </div>
        <div v-else>
          <div v-for="draft in draftList" :key="draft.id" class="draft-item">
            <div class="draft-info">
              <h4>{{ draft.title }}</h4>
              <p>{{ draft.preview }}</p>
              <small>更新时间: {{ formatTime(draft.updateTime) }}</small>
            </div>
            <div class="draft-actions">
              <el-button size="small" @click="loadDraft(draft.id)">加载</el-button>
              <el-button size="small" type="danger" @click="deleteDraft(draft.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { draftService, type DraftListItem } from '@/services/draftService'
import { useAuthStore } from '@/store/modules/auth'

const authStore = useAuthStore()

const testForm = reactive({
  storeName: '',
  storeDescription: '',
  category: ''
})

const currentDraftId = ref<string | null>(null)
const draftList = ref<DraftListItem[]>([])

const saveDraft = () => {
  try {
    if (!testForm.storeName.trim()) {
      ElMessage.warning('请输入店铺名称')
      return
    }
    
    if (!authStore.merchantInfo?.merchantId) {
      ElMessage.warning('请先登录')
      return
    }
    
    if (currentDraftId.value) {
      draftService.updateDraft(currentDraftId.value, testForm)
    } else {
      const draftId = draftService.saveDraft(testForm, authStore.merchantInfo.merchantId)
      currentDraftId.value = draftId
    }
    
    loadDrafts()
  } catch (error) {
    console.error('保存草稿失败:', error)
  }
}

const loadDrafts = () => {
  if (!authStore.merchantInfo?.merchantId) {
    ElMessage.warning('请先登录')
    return
  }
  
  draftList.value = draftService.getDraftsByMerchant(authStore.merchantInfo.merchantId)
}

const loadDraft = (draftId: string) => {
  try {
    const draft = draftService.getDraftById(draftId)
    if (draft) {
      Object.assign(testForm, {
        storeName: draft.storeName,
        storeDescription: draft.storeDescription,
        category: draft.category
      })
      currentDraftId.value = draft.id
      ElMessage.success(`草稿"${draft.title}"已加载`)
    }
  } catch (error) {
    console.error('加载草稿失败:', error)
  }
}

const deleteDraft = (draftId: string) => {
  try {
    draftService.deleteDraft(draftId)
    if (currentDraftId.value === draftId) {
      currentDraftId.value = null
    }
    loadDrafts()
  } catch (error) {
    console.error('删除草稿失败:', error)
  }
}

const clearForm = () => {
  Object.assign(testForm, {
    storeName: '',
    storeDescription: '',
    category: ''
  })
  currentDraftId.value = null
}

const formatTime = (timeString: string) => {
  return new Date(timeString).toLocaleString('zh-CN')
}

onMounted(() => {
  authStore.initializeAuth()
  loadDrafts()
})
</script>

<style scoped>
.draft-test {
  max-width: 800px;
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

.current-draft {
  padding: 12px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 6px;
  margin-top: 16px;
}

.empty {
  text-align: center;
  color: #999;
  padding: 20px;
}

.draft-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
}

.draft-info {
  flex: 1;
}

.draft-info h4 {
  margin: 0 0 8px 0;
  color: #333;
}

.draft-info p {
  margin: 0 0 4px 0;
  color: #666;
  font-size: 14px;
}

.draft-info small {
  color: #999;
  font-size: 12px;
}

.draft-actions {
  display: flex;
  gap: 8px;
}
</style> 