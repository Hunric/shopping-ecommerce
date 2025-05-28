<template>
  <div class="image-upload">
    <!-- 上传区域 -->
    <div 
      class="upload-area"
      :class="{ 
        'is-dragover': isDragOver,
        'has-image': modelValue,
        'is-uploading': uploading
      }"
      @click="triggerFileInput"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
    >
      <!-- 已上传的图片 -->
      <div v-if="modelValue && !uploading" class="uploaded-image">
        <img 
          :src="modelValue" 
          :alt="alt || '上传的图片'"
          class="preview-image"
          @error="handleImageError"
        />
        <div class="image-overlay">
          <div class="overlay-actions">
            <el-button 
              type="primary" 
              :icon="View" 
              circle 
              size="small"
              @click.stop="previewImage"
            />
            <el-button 
              type="danger" 
              :icon="Delete" 
              circle 
              size="small"
              @click.stop="removeImage"
            />
          </div>
        </div>
      </div>

      <!-- 上传中状态 -->
      <div v-else-if="uploading" class="uploading-state">
        <el-progress 
          type="circle" 
          :percentage="uploadProgress"
          :width="60"
        />
        <p class="upload-text">上传中...</p>
      </div>

      <!-- 默认上传提示 -->
      <div v-else class="upload-placeholder">
        <el-icon class="upload-icon" :size="40">
          <Plus />
        </el-icon>
        <p class="upload-text">{{ placeholder }}</p>
        <p class="upload-tips">{{ tips }}</p>
      </div>
    </div>

    <!-- 隐藏的文件输入框 -->
    <input
      ref="fileInputRef"
      type="file"
      :accept="accept"
      style="display: none"
      @change="handleFileSelect"
    />

    <!-- 手动输入URL -->
    <div v-if="allowUrlInput" class="url-input-section">
      <el-divider>或</el-divider>
      <div class="url-input">
        <el-input
          v-model="urlInput"
          placeholder="输入图片URL地址"
          clearable
        >
          <template #append>
            <el-button @click="handleUrlSubmit" :disabled="!urlInput.trim()">
              确定
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="600px"
      @close="previewVisible = false"
    >
      <div class="preview-dialog">
        <img
          v-if="modelValue"
          :src="modelValue"
          alt="预览图片"
          class="preview-dialog-image"
          @error="handlePreviewError"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, View, Delete } from '@element-plus/icons-vue'
import { uploadImageWithProgress } from '@/api/upload'

// 定义Props
interface Props {
  modelValue?: string
  placeholder?: string
  tips?: string
  accept?: string
  maxSize?: number // MB
  uploadType?: string
  allowUrlInput?: boolean
  alt?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '点击或拖拽上传图片',
  tips: '支持 JPG、PNG、GIF、WebP 格式，文件大小不超过 5MB',
  accept: 'image/jpeg,image/jpg,image/png,image/gif,image/webp',
  maxSize: 5,
  uploadType: 'logo',
  allowUrlInput: true
})

// 定义事件
const emit = defineEmits<{
  'update:modelValue': [value: string]
  'upload-success': [url: string]
  'upload-error': [error: string]
}>()

// 响应式数据
const fileInputRef = ref<HTMLInputElement>()
const uploading = ref(false)
const uploadProgress = ref(0)
const isDragOver = ref(false)
const previewVisible = ref(false)
const urlInput = ref('')

// 计算属性
const maxSizeBytes = computed(() => props.maxSize * 1024 * 1024)

// 触发文件选择
const triggerFileInput = () => {
  if (uploading.value) return
  fileInputRef.value?.click()
}

// 处理文件选择
const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    uploadFile(file)
  }
  // 清空input值，允许重复选择同一文件
  target.value = ''
}

// 处理拖拽
const handleDragOver = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = true
}

const handleDragLeave = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragOver.value = false
  
  const files = event.dataTransfer?.files
  if (files && files.length > 0) {
    uploadFile(files[0])
  }
}

// 上传文件
const uploadFile = async (file: File) => {
  try {
    // 验证文件类型
    const allowedTypes = props.accept.split(',').map(type => type.trim())
    if (!allowedTypes.includes(file.type)) {
      throw new Error('不支持的文件格式')
    }

    // 验证文件大小
    if (file.size > maxSizeBytes.value) {
      throw new Error(`文件大小不能超过 ${props.maxSize}MB`)
    }

    uploading.value = true
    uploadProgress.value = 0

    const response = await uploadImageWithProgress(
      file, 
      props.uploadType,
      (progress) => {
        uploadProgress.value = progress
      }
    )

    if (response.success && response.data.url) {
      emit('update:modelValue', response.data.url)
      emit('upload-success', response.data.url)
      ElMessage.success('图片上传成功')
    } else {
      throw new Error(response.message || '上传失败')
    }
  } catch (error: any) {
    const errorMessage = error.message || '图片上传失败'
    ElMessage.error(errorMessage)
    emit('upload-error', errorMessage)
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

// 处理URL输入
const handleUrlSubmit = () => {
  if (!urlInput.value.trim()) return
  
  // 简单的URL验证
  const urlPattern = /^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)(\?.*)?$/i
  if (!urlPattern.test(urlInput.value)) {
    ElMessage.warning('请输入有效的图片URL地址')
    return
  }

  emit('update:modelValue', urlInput.value.trim())
  emit('upload-success', urlInput.value.trim())
  urlInput.value = ''
  ElMessage.success('图片URL设置成功')
}

// 移除图片
const removeImage = () => {
  emit('update:modelValue', '')
  urlInput.value = ''
}

// 预览图片
const previewImage = () => {
  if (props.modelValue) {
    previewVisible.value = true
  }
}

// 处理图片加载错误
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
  ElMessage.warning('图片加载失败，请检查图片地址是否正确')
}

const handlePreviewError = () => {
  ElMessage.warning('图片预览失败')
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-area {
  width: 200px;
  height: 200px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.upload-area:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.upload-area.is-dragover {
  border-color: #409eff;
  background: #e6f7ff;
  transform: scale(1.02);
}

.upload-area.has-image {
  border-color: #409eff;
  padding: 0;
}

.upload-area.is-uploading {
  cursor: not-allowed;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 6px;
}

.uploaded-image:hover .image-overlay {
  opacity: 1;
}

.overlay-actions {
  display: flex;
  gap: 8px;
}

.uploading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-align: center;
  padding: 20px;
}

.upload-icon {
  color: #909399;
}

.upload-text {
  margin: 0;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.upload-tips {
  margin: 0;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.url-input-section {
  margin-top: 16px;
}

.url-input {
  margin-top: 12px;
}

.preview-dialog {
  text-align: center;
  padding: 20px;
}

.preview-dialog-image {
  max-width: 100%;
  max-height: 500px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-area {
    width: 150px;
    height: 150px;
  }
  
  .upload-placeholder {
    padding: 15px;
  }
  
  .upload-icon {
    font-size: 32px;
  }
  
  .upload-text {
    font-size: 12px;
  }
  
  .upload-tips {
    font-size: 11px;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-progress-circle) {
  width: 60px !important;
  height: 60px !important;
}

:deep(.el-divider__text) {
  background: white;
  color: #909399;
  font-size: 12px;
}
</style> 