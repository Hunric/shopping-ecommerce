<template>
  <div class="store-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="store-form-content"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Shop /></el-icon>
          基本信息
        </h3>
        
        <el-form-item label="店铺名称" prop="storeName">
          <el-input
            v-model="formData.storeName"
            placeholder="请输入店铺名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="店铺描述" prop="storeDescription">
          <el-input
            v-model="formData.storeDescription"
            type="textarea"
            :rows="4"
            placeholder="请输入店铺描述，让客户更好地了解您的店铺"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="经营类目" prop="category">
          <el-select
            v-model="formData.category"
            placeholder="请选择主要经营类目"
            style="width: 100%"
          >
            <el-option label="服装鞋帽" value="clothing" />
            <el-option label="数码电器" value="electronics" />
            <el-option label="食品饮料" value="food" />
            <el-option label="美妆护肤" value="beauty" />
            <el-option label="家居用品" value="home" />
            <el-option label="运动户外" value="sports" />
            <el-option label="母婴用品" value="baby" />
            <el-option label="图书文具" value="books" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
      </div>

      <!-- 店铺Logo -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Picture /></el-icon>
          店铺Logo
        </h3>
        
        <el-form-item label="店铺Logo" prop="storeLogo">
          <ImageUpload
            v-model="formData.storeLogo"
            placeholder="点击或拖拽上传店铺Logo"
            tips="建议尺寸：200x200像素，支持 JPG、PNG、GIF、WebP 格式，文件大小不超过 5MB"
            upload-type="store-logo"
            alt="店铺Logo"
            @upload-success="handleLogoUploadSuccess"
            @upload-error="handleLogoUploadError"
          />
        </el-form-item>
      </div>

      <!-- 营业设置 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Setting /></el-icon>
          营业设置
        </h3>
        
        <el-form-item label="营业状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio value="open">立即开始营业</el-radio>
            <el-radio value="closed">暂不营业</el-radio>
          </el-radio-group>
          <div class="form-tips">
            选择"立即开始营业"后，客户可以浏览和购买您的商品
          </div>
        </el-form-item>
        
        <el-form-item label="服务承诺" prop="servicePromise">
          <el-checkbox-group v-model="formData.servicePromise">
            <el-checkbox value="7天无理由退货">7天无理由退货</el-checkbox>
            <el-checkbox value="正品保证">正品保证</el-checkbox>
            <el-checkbox value="快速发货">快速发货</el-checkbox>
            <el-checkbox value="售后保障">售后保障</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </div>

      <!-- 联系信息 -->
      <div class="form-section">
        <h3 class="section-title">
          <el-icon><Phone /></el-icon>
          联系信息
        </h3>
        
        <el-form-item label="客服电话" prop="servicePhone">
          <el-input
            v-model="formData.servicePhone"
            placeholder="请输入客服电话"
            maxlength="20"
          />
        </el-form-item>
        
        <el-form-item label="客服邮箱" prop="serviceEmail">
          <el-input
            v-model="formData.serviceEmail"
            placeholder="请输入客服邮箱"
            maxlength="100"
          />
        </el-form-item>
        
        <el-form-item label="营业时间" prop="businessHours">
          <el-input
            v-model="formData.businessHours"
            placeholder="例如：周一至周日 9:00-22:00"
            maxlength="100"
          />
        </el-form-item>
      </div>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <div class="left-actions">
          <el-button @click="handleCancel" size="large">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
          <el-button 
            v-if="props.mode === 'create'" 
            @click="openDraftManager" 
            size="large"
          >
            <el-icon><FolderOpened /></el-icon>
            草稿管理
          </el-button>
        </div>
        <div class="right-actions">
          <el-button 
            v-if="props.mode === 'create'" 
            @click="handleSaveDraft" 
            size="large" 
            :loading="submitting"
          >
            <el-icon><Document /></el-icon>
            <span>{{ currentDraftId ? '更新草稿' : '保存草稿' }}</span>
          </el-button>
          <el-button
            type="primary"
            @click="handleSubmit"
            size="large"
            :loading="submitting"
          >
            <el-icon><Check /></el-icon>
            {{ getSubmitButtonText() }}
          </el-button>
        </div>
      </div>
    </el-form>

    <!-- 草稿管理对话框 -->
    <DraftManager
      v-model="draftManagerVisible"
      @load-draft="handleLoadDraft"
      @close="draftManagerVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  Shop, Picture, Setting, Phone, Plus, Close, Document, Check, FolderOpened
} from '@element-plus/icons-vue'
import ImageUpload from '@/components/common/ImageUpload.vue'
import DraftManager from './DraftManager.vue'
import { useAuthStore } from '@/store/modules/auth'
import type { StoreCreateData, StoreUpdateData, Store } from '@/api/merchant/store'
import type { StoreExtended, StoreExtendedCreateData, StoreExtendedUpdateData } from '@/api/merchant/storeExtended'
import { draftService, type StoreDraft } from '@/services/draftService'

// 定义Props
interface Props {
  editStore?: Store | StoreExtended // 编辑模式时传入的店铺数据
  mode?: 'create' | 'edit' // 模式：创建或编辑
  useExtendedApi?: boolean // 是否使用扩展API
}

const props = withDefaults(defineProps<Props>(), {
  mode: 'create',
  useExtendedApi: false
})

// 定义事件
const emit = defineEmits<{
  cancel: []
  success: [storeData: StoreCreateData | StoreUpdateData | StoreExtendedCreateData | StoreExtendedUpdateData, storeId?: number]
}>()

// 响应式数据
const formRef = ref<FormInstance>()
const submitting = ref(false)
const authStore = useAuthStore()
const draftManagerVisible = ref(false)
const currentDraftId = ref<string | null>(null)
const lastSavedData = ref<any>(null)
const autoSaveTimer = ref<number | null>(null)

// 表单数据
const formData = reactive({
  storeName: '',
  storeDescription: '',
  category: '',
  storeLogo: '',
  status: 'open',
  servicePromise: [] as string[],
  servicePhone: '',
  serviceEmail: '',
  businessHours: '周一至周日 9:00-22:00'
})

// 表单验证规则
const formRules: FormRules = {
  storeName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 50, message: '店铺名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  storeDescription: [
    { required: true, message: '请输入店铺描述', trigger: 'blur' },
    { min: 10, max: 500, message: '店铺描述长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择经营类目', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择营业状态', trigger: 'change' }
  ],
  servicePhone: [
    { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码', trigger: 'blur' }
  ],
  serviceEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 方法
const handleCancel = async () => {
  // 检查是否有未保存的内容
  const hasContent = Object.values(formData).some(value => {
    if (Array.isArray(value)) {
      return value.length > 0
    }
    return value !== '' && value !== 'open' && value !== '周一至周日 9:00-22:00'
  })

  if (hasContent) {
    try {
      await ElMessageBox.confirm(
        '您有未保存的内容，确定要离开吗？',
        '确认离开',
        {
          confirmButtonText: '确定离开',
          cancelButtonText: '继续编辑',
          type: 'warning'
        }
      )
    } catch {
      return
    }
  }

  emit('cancel')
}

const handleSaveDraft = async () => {
  // 编辑模式下不支持草稿功能
  if (props.mode === 'edit') {
    ElMessage.warning('编辑模式下不支持草稿功能')
    return
  }

  try {
    // 基本验证
    if (!formData.storeName.trim()) {
      ElMessage.warning('请至少填写店铺名称')
      return
    }

    if (!authStore.merchantInfo?.merchantId) {
      ElMessage.warning('商家信息不完整，请重新登录')
      return
    }

    submitting.value = true

    if (currentDraftId.value) {
      // 更新现有草稿
      draftService.updateDraft(currentDraftId.value, formData)
    } else {
      // 创建新草稿
      const draftId = draftService.saveDraft(formData, authStore.merchantInfo.merchantId)
      currentDraftId.value = draftId
    }

    // 更新最后保存的数据
    lastSavedData.value = { ...formData }
  } catch (error) {
    console.error('保存草稿失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 表单验证
    await formRef.value.validate()
    
    submitting.value = true

    if (props.mode === 'edit' && props.editStore) {
      // 编辑模式：构造更新数据
      if (props.useExtendedApi) {
        const updateData: StoreExtendedUpdateData = {
          storeName: formData.storeName,
          storeDescription: formData.storeDescription,
          storeLogo: formData.storeLogo || undefined,
          category: formData.category,
          status: formData.status as 'open' | 'closed' | 'suspended',
          servicePromise: formData.servicePromise,
          servicePhone: formData.servicePhone,
          serviceEmail: formData.serviceEmail,
          businessHours: formData.businessHours
        }
        emit('success', updateData, props.editStore.storeId)
      } else {
        const updateData: StoreUpdateData = {
          storeName: formData.storeName,
          storeDescription: formData.storeDescription,
          storeLogo: formData.storeLogo || undefined,
          status: formData.status
        }
        emit('success', updateData, props.editStore.storeId)
      }
    } else {
      // 创建模式：构造创建数据
      if (!authStore.merchantInfo?.merchantId) {
        ElMessage.warning('商家信息不完整，请重新登录')
        return
      }
      
      if (props.useExtendedApi) {
        const storeData: StoreExtendedCreateData = {
          merchantId: authStore.merchantInfo.merchantId,
          storeName: formData.storeName,
          storeDescription: formData.storeDescription,
          storeLogo: formData.storeLogo || undefined,
          category: formData.category,
          status: formData.status as 'open' | 'closed',
          servicePromise: formData.servicePromise,
          servicePhone: formData.servicePhone,
          serviceEmail: formData.serviceEmail,
          businessHours: formData.businessHours
        }
        emit('success', storeData)
      } else {
        const storeData: StoreCreateData = {
          merchantId: authStore.merchantInfo.merchantId,
          storeName: formData.storeName,
          storeDescription: formData.storeDescription,
          storeLogo: formData.storeLogo || undefined
        }
        emit('success', storeData)
      }
    }

  } catch (error) {
    if (error !== false) { // 不是验证失败
      const action = props.mode === 'edit' ? '更新店铺' : '创建店铺'
      ElMessage.error(`${action}失败，请稍后重试`)
    }
  } finally {
    submitting.value = false
  }
}

const handleLogoUploadSuccess = (url: string) => {
  console.log('Logo上传成功:', url)
  // formData.storeLogo 已经通过 v-model 自动更新
}

const handleLogoUploadError = (error: string) => {
  console.error('Logo上传失败:', error)
}

// 打开草稿管理器
const openDraftManager = () => {
  draftManagerVisible.value = true
}

// 加载草稿
const handleLoadDraft = async (draft: StoreDraft) => {
  try {
    // 检查是否有未保存的更改
    if (hasUnsavedChanges() && !currentDraftId.value) {
      await ElMessageBox.confirm(
        '当前有未保存的内容，加载草稿将覆盖当前内容，是否继续？',
        '确认加载草稿',
        {
          confirmButtonText: '确定加载',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    }

    // 加载草稿数据到表单
    Object.assign(formData, {
      storeName: draft.storeName,
      storeDescription: draft.storeDescription,
      category: draft.category,
      storeLogo: draft.storeLogo,
      status: draft.status,
      servicePromise: draft.servicePromise,
      servicePhone: draft.servicePhone,
      serviceEmail: draft.serviceEmail,
      businessHours: draft.businessHours
    })

    // 设置当前草稿ID
    currentDraftId.value = draft.id
    lastSavedData.value = { ...formData }

    ElMessage.success(`草稿"${draft.title}"已加载`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('加载草稿失败:', error)
      ElMessage.error('加载草稿失败')
    }
  }
}

// 检查是否有未保存的更改
const hasUnsavedChanges = () => {
  if (!lastSavedData.value) {
    // 检查是否有任何内容
    return Object.values(formData).some(value => {
      if (Array.isArray(value)) {
        return value.length > 0
      }
      return value !== '' && value !== 'open' && value !== '周一至周日 9:00-22:00'
    })
  }
  
  return draftService.hasUnsavedChanges(formData, lastSavedData.value)
}

// 自动保存
const autoSave = () => {
  // 编辑模式下不启用自动保存
  if (props.mode === 'edit') {
    return
  }

  if (hasUnsavedChanges() && formData.storeName.trim() && authStore.merchantInfo?.merchantId) {
    try {
      if (currentDraftId.value) {
        draftService.updateDraft(currentDraftId.value, formData)
        lastSavedData.value = { ...formData }
      }
    } catch (error) {
      console.error('自动保存失败:', error)
    }
  }
}

// 启动自动保存
const startAutoSave = () => {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
  }
  
  // 每30秒自动保存一次
  autoSaveTimer.value = setInterval(autoSave, 30000)
}

// 停止自动保存
const stopAutoSave = () => {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
    autoSaveTimer.value = null
  }
}

// 获取类目显示名称
const getCategoryName = (value: string) => {
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
  return categoryMap[value] || value
}

// 获取提交按钮文本
const getSubmitButtonText = () => {
  if (props.mode === 'edit') {
    return '保存修改'
  }
  return formData.status === 'open' ? '创建并开始营业' : '创建店铺'
}

// 初始化表单数据
const initializeForm = () => {
  if (props.mode === 'edit' && props.editStore) {
    // 编辑模式：加载现有店铺数据
    const store = props.editStore as StoreExtended
    Object.assign(formData, {
      storeName: store.storeName || '',
      storeDescription: store.storeDescription || '',
      category: store.category || '',
      storeLogo: store.storeLogo || '',
      status: store.status || 'open',
      servicePromise: store.servicePromise || [],
      servicePhone: store.servicePhone || '',
      serviceEmail: store.serviceEmail || '',
      businessHours: store.businessHours || '周一至周日 9:00-22:00'
    })
    lastSavedData.value = { ...formData }
  }
}

// 生命周期钩子
onMounted(() => {
  initializeForm()
  startAutoSave()
})

onUnmounted(() => {
  stopAutoSave()
})

// 监听表单变化
watch(formData, () => {
  // 表单有变化时，重置自动保存计时器
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
    startAutoSave()
  }
}, { deep: true })
</script>

<style scoped>
.store-form {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.store-form-content {
  margin-bottom: 32px;
}

.form-section {
  margin-bottom: 40px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 24px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.section-title .el-icon {
  color: #409eff;
}

.form-tips {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}



/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

.left-actions,
.right-actions {
  display: flex;
  gap: 16px;
}

.form-actions .el-button {
  min-width: 120px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .store-form {
    padding: 24px 16px;
    margin: 16px;
  }



  .form-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .left-actions,
  .right-actions {
    justify-content: center;
  }

  .form-actions .el-button {
    min-width: auto;
  }
}

/* 表单项样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

:deep(.el-radio-group) {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style> 