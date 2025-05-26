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
          <div class="logo-upload">
            <div class="logo-preview">
              <img
                v-if="formData.storeLogo"
                :src="formData.storeLogo"
                alt="店铺Logo"
                class="logo-image"
                @error="handleImageError"
              />
              <div v-else class="logo-placeholder">
                <el-icon class="logo-icon"><Plus /></el-icon>
                <span>上传Logo</span>
              </div>
            </div>
            
            <div class="logo-input">
              <el-input
                v-model="formData.storeLogo"
                placeholder="请输入Logo图片URL"
                clearable
              >
                <template #append>
                  <el-button @click="previewLogo">预览</el-button>
                </template>
              </el-input>
              <div class="logo-tips">
                <p>建议尺寸：200x200像素，支持JPG、PNG格式</p>
                <p>或者输入图片URL地址</p>
              </div>
            </div>
          </div>
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
            <el-radio label="open">立即开始营业</el-radio>
            <el-radio label="closed">暂不营业</el-radio>
          </el-radio-group>
          <div class="form-tips">
            选择"立即开始营业"后，客户可以浏览和购买您的商品
          </div>
        </el-form-item>
        
        <el-form-item label="服务承诺" prop="servicePromise">
          <el-checkbox-group v-model="formData.servicePromise">
            <el-checkbox label="7天无理由退货">7天无理由退货</el-checkbox>
            <el-checkbox label="正品保证">正品保证</el-checkbox>
            <el-checkbox label="快速发货">快速发货</el-checkbox>
            <el-checkbox label="售后保障">售后保障</el-checkbox>
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
        <el-button @click="handleCancel" size="large">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
        <el-button @click="handleSaveDraft" size="large">
          <el-icon><Document /></el-icon>
          保存草稿
        </el-button>
        <el-button
          type="primary"
          @click="handleSubmit"
          size="large"
          :loading="submitting"
        >
          <el-icon><Check /></el-icon>
          {{ formData.status === 'open' ? '创建并开始营业' : '创建店铺' }}
        </el-button>
      </div>
    </el-form>

    <!-- Logo预览对话框 -->
    <el-dialog
      v-model="logoPreviewVisible"
      title="Logo预览"
      width="400px"
      @close="logoPreviewVisible = false"
    >
      <div class="logo-preview-dialog">
        <img
          v-if="formData.storeLogo"
          :src="formData.storeLogo"
          alt="Logo预览"
          class="preview-image"
          @error="handlePreviewError"
        />
        <div v-else class="preview-placeholder">
          <el-icon><Picture /></el-icon>
          <p>请先输入Logo URL</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="logoPreviewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  Shop, Picture, Setting, Phone, Plus, Close, Document, Check
} from '@element-plus/icons-vue'

// 定义事件
const emit = defineEmits<{
  cancel: []
  success: [storeData: any]
}>()

// 响应式数据
const formRef = ref<FormInstance>()
const submitting = ref(false)
const logoPreviewVisible = ref(false)

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
  try {
    // 基本验证
    if (!formData.storeName.trim()) {
      ElMessage.warning('请至少填写店铺名称')
      return
    }

    // 模拟保存草稿
    ElMessage.success('草稿已保存')
  } catch (error) {
    ElMessage.error('保存草稿失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    // 表单验证
    await formRef.value.validate()
    
    submitting.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 构造店铺数据
    const storeData = {
      storeId: Date.now(), // 模拟生成ID
      storeName: formData.storeName,
      storeDescription: formData.storeDescription,
      category: formData.category,
      storeLogo: formData.storeLogo || undefined,
      status: formData.status,
      servicePromise: formData.servicePromise,
      servicePhone: formData.servicePhone,
      serviceEmail: formData.serviceEmail,
      businessHours: formData.businessHours,
      openTime: new Date().toISOString(),
      creditScore: 100
    }

    ElMessage.success('店铺创建成功！')
    emit('success', storeData)

  } catch (error) {
    if (error !== false) { // 不是验证失败
      ElMessage.error('创建店铺失败，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

const previewLogo = () => {
  if (!formData.storeLogo.trim()) {
    ElMessage.warning('请先输入Logo URL')
    return
  }
  logoPreviewVisible.value = true
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
  ElMessage.warning('Logo图片加载失败，请检查URL是否正确')
}

const handlePreviewError = () => {
  ElMessage.warning('图片预览失败，请检查URL是否正确')
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

/* Logo上传样式 */
.logo-upload {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.logo-preview {
  width: 120px;
  height: 120px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  transition: border-color 0.3s ease;
  flex-shrink: 0;
}

.logo-preview:hover {
  border-color: #409eff;
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.logo-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.logo-icon {
  font-size: 24px;
}

.logo-input {
  flex: 1;
}

.logo-tips {
  margin-top: 8px;
}

.logo-tips p {
  margin: 4px 0;
  color: #909399;
  font-size: 12px;
}

/* Logo预览对话框 */
.logo-preview-dialog {
  text-align: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview-placeholder {
  padding: 60px 20px;
  color: #909399;
}

.preview-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

.form-actions .el-button {
  min-width: 140px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .store-form {
    padding: 24px 16px;
    margin: 16px;
  }

  .logo-upload {
    flex-direction: column;
    align-items: center;
  }

  .logo-preview {
    width: 100px;
    height: 100px;
  }

  .form-actions {
    flex-direction: column;
    align-items: stretch;
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