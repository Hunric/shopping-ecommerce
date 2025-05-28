<template>
  <el-dialog
    v-model="visible"
    title="草稿管理"
    width="800px"
    :before-close="handleClose"
    class="draft-manager-dialog"
  >
    <div class="draft-manager">
      <!-- 头部操作栏 -->
      <div class="header-actions">
        <div class="info">
          <span class="count">共 {{ draftList.length }} 个草稿</span>
          <span class="tips">（最多保存 {{ maxDrafts }} 个草稿）</span>
        </div>
        <div class="actions">
          <el-button @click="refreshDrafts" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button 
            type="danger" 
            @click="clearAllDrafts"
            :disabled="draftList.length === 0"
          >
            <el-icon><Delete /></el-icon>
            清空所有
          </el-button>
        </div>
      </div>

      <!-- 草稿列表 -->
      <div class="draft-list" v-loading="loading">
        <div v-if="draftList.length === 0" class="empty-state">
          <el-empty description="暂无草稿">
            <el-button type="primary" @click="handleClose">
              去创建店铺
            </el-button>
          </el-empty>
        </div>

        <div
          v-for="draft in draftList"
          :key="draft.id"
          class="draft-item"
          @click="handleLoadDraft(draft.id)"
        >
          <div class="draft-content">
            <div class="draft-header">
              <h4 class="draft-title">{{ draft.title }}</h4>
              <div class="draft-time">
                <span class="create-time">创建：{{ formatTime(draft.createTime) }}</span>
                <span class="update-time">更新：{{ formatTime(draft.updateTime) }}</span>
              </div>
            </div>
            
            <div class="draft-preview">
              {{ draft.preview }}
            </div>
          </div>

          <div class="draft-actions" @click.stop>
            <el-button 
              type="primary" 
              size="small"
              @click="handleLoadDraft(draft.id)"
            >
              <el-icon><Edit /></el-icon>
              加载
            </el-button>
            <el-button 
              type="danger" 
              size="small"
              @click="handleDeleteDraft(draft.id)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Delete, Edit } from '@element-plus/icons-vue'
import { draftService, type DraftListItem, type StoreDraft } from '@/services/draftService'
import { useAuthStore } from '@/store/modules/auth'

// 定义事件
const emit = defineEmits<{
  'load-draft': [draft: StoreDraft]
  'close': []
}>()

// 定义属性
const props = defineProps<{
  modelValue: boolean
}>()

// 响应式数据
const authStore = useAuthStore()
const loading = ref(false)
const draftList = ref<DraftListItem[]>([])
const maxDrafts = 10

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('close')
})

// 监听对话框打开
watch(visible, (newValue) => {
  if (newValue) {
    loadDrafts()
  }
})

// 方法
const loadDrafts = async () => {
  if (!authStore.merchantInfo?.merchantId) {
    ElMessage.warning('商家信息不完整')
    return
  }

  loading.value = true
  try {
    draftList.value = draftService.getDraftsByMerchant(authStore.merchantInfo.merchantId)
  } catch (error) {
    console.error('加载草稿列表失败:', error)
    ElMessage.error('加载草稿列表失败')
  } finally {
    loading.value = false
  }
}

const refreshDrafts = () => {
  loadDrafts()
}

const handleLoadDraft = (draftId: string) => {
  try {
    const draft = draftService.getDraftById(draftId)
    if (draft) {
      emit('load-draft', draft)
      handleClose()
    } else {
      ElMessage.error('草稿不存在或已被删除')
      loadDrafts() // 刷新列表
    }
  } catch (error) {
    console.error('加载草稿失败:', error)
    ElMessage.error('加载草稿失败')
  }
}

const handleDeleteDraft = async (draftId: string) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个草稿吗？删除后无法恢复。',
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    draftService.deleteDraft(draftId)
    loadDrafts() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除草稿失败:', error)
    }
  }
}

const clearAllDrafts = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要清空所有 ${draftList.value.length} 个草稿吗？此操作无法恢复。`,
      '确认清空',
      {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    draftService.clearAllDrafts()
    loadDrafts() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空草稿失败:', error)
    }
  }
}

const handleClose = () => {
  emit('close')
}

const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000))
    return `${minutes}分钟前`
  }
  
  // 小于1天
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000))
    return `${hours}小时前`
  }
  
  // 小于7天
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000))
    return `${days}天前`
  }
  
  // 超过7天显示具体日期
  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.draft-manager {
  max-height: 600px;
  display: flex;
  flex-direction: column;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
  margin-bottom: 16px;
}

.info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.count {
  font-weight: 500;
  color: #333;
}

.tips {
  font-size: 12px;
  color: #999;
}

.actions {
  display: flex;
  gap: 8px;
}

.draft-list {
  flex: 1;
  overflow-y: auto;
  max-height: 450px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
}

.draft-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.draft-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.draft-item:last-child {
  margin-bottom: 0;
}

.draft-content {
  flex: 1;
  min-width: 0;
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.draft-title {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
}

.draft-time {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.draft-preview {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.draft-actions {
  display: flex;
  gap: 8px;
  margin-left: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .draft-manager-dialog {
    width: 95% !important;
  }
  
  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .info {
    justify-content: center;
  }
  
  .actions {
    justify-content: center;
  }
  
  .draft-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .draft-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .draft-title {
    max-width: none;
    margin-bottom: 4px;
  }
  
  .draft-time {
    align-items: flex-start;
  }
  
  .draft-actions {
    margin-left: 0;
    margin-top: 12px;
    justify-content: center;
  }
}
</style> 