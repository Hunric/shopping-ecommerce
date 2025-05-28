<template>
  <div class="category-management-dashboard">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>分类管理</h2>
        <p>管理商品分类结构，为商品发布提供分类基础</p>
      </div>
      <div class="header-actions">
        <el-select 
          v-model="currentStoreId" 
          placeholder="选择店铺"
          @change="handleStoreChange"
          style="width: 200px; margin-right: 16px"
        >
          <el-option
            v-for="store in stores"
            :key="store.storeId"
            :label="store.storeName"
            :value="store.storeId"
          />
        </el-select>
        <el-button type="primary" @click="handleAddTopCategory" :disabled="!currentStoreId">
          <el-icon><Plus /></el-icon>
          添加顶级分类
        </el-button>
      </div>
    </div>

    <!-- 分类树 -->
    <div class="category-content" v-loading="loading">
      <el-empty v-if="!currentStoreId" description="请先选择一个店铺" />
      
      <el-empty v-else-if="categoryTree.length === 0 && !loading" description="暂无分类数据">
        <el-button type="primary" @click="handleAddTopCategory">创建第一个分类</el-button>
      </el-empty>
      
      <el-tree
        v-else
        :data="categoryTree"
        :props="treeProps"
        node-key="categoryId"
        default-expand-all
        :expand-on-click-node="false"
        class="category-tree"
      >
        <template #default="{ node, data }">
          <div class="tree-node">
            <div class="node-content">
              <el-icon v-if="data.iconUrl" class="node-icon">
                <img :src="data.iconUrl" alt="" />
              </el-icon>
              <el-icon v-else class="node-icon">
                <Folder v-if="!data.isLeaf" />
                <Document v-else />
              </el-icon>
              <span class="node-label">{{ data.categoryName }}</span>
              <el-tag v-if="data.productCount > 0" size="small" type="info" class="product-count">
                {{ data.productCount }} 个商品
              </el-tag>
              <el-tag v-if="!data.isVisible" size="small" type="warning">隐藏</el-tag>
            </div>
            
            <div class="node-actions">
              <el-button 
                v-if="data.isLeaf" 
                link 
                type="success" 
                size="small"
                @click="handleManageAttributes(data)"
              >
                <el-icon><Setting /></el-icon>
                管理属性
              </el-button>
              <el-button 
                v-if="!data.hasProducts" 
                link 
                type="primary" 
                size="small"
                @click="handleAddSubCategory(data)"
              >
                <el-icon><Plus /></el-icon>
                添加子分类
              </el-button>
              <el-button link type="primary" size="small" @click="handleEdit(data)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                v-if="!data.hasChildren && !data.hasProducts" 
                link 
                type="danger" 
                size="small"
                @click="handleDelete(data)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </template>
      </el-tree>
    </div>

    <!-- 分类编辑对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      :title="categoryDialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form 
        ref="categoryFormRef" 
        :model="categoryForm" 
        :rules="categoryRules" 
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入分类名称" maxlength="20" />
        </el-form-item>
        
        <el-form-item label="分类描述" prop="description">
          <el-input 
            v-model="categoryForm.description" 
            type="textarea" 
            placeholder="请输入分类描述" 
            maxlength="50"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        
        <el-form-item label="是否显示" prop="isVisible">
          <el-switch v-model="categoryForm.isVisible" />
        </el-form-item>
        
        <el-form-item label="分类图标" prop="iconUrl">
          <el-input v-model="categoryForm.iconUrl" placeholder="请输入图标URL" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveCategory" :loading="submitting">
          {{ categoryDialogMode === 'create' ? '创建' : '保存' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, Document, Edit, Delete, Setting } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { getStoresByMerchantId, type Store } from '@/api/merchant/store'
import {
  getCategoryTree,
  createCategory,
  updateCategory,
  deleteCategory,
  type ProductCategory,
  type CreateCategoryRequest,
  type UpdateCategoryRequest
} from '@/api/merchant/category'

const authStore = useAuthStore()
const router = useRouter()

// 状态
const loading = ref(false)
const submitting = ref(false)
const stores = ref<Store[]>([])
const currentStoreId = ref<number | null>(null)
const categoryTree = ref<ProductCategory[]>([])

// 分类对话框
const categoryDialogVisible = ref(false)
const categoryDialogMode = ref<'create' | 'edit'>('create')
const categoryDialogTitle = computed(() => 
  categoryDialogMode.value === 'create' ? '添加分类' : '编辑分类'
)
const categoryFormRef = ref()
const categoryForm = reactive({
  categoryId: null as number | null,
  parentId: null as number | null,
  categoryName: '',
  description: '',
  sortOrder: 0,
  isVisible: true,
  iconUrl: ''
})

// 表单验证规则
const categoryRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 20, message: '分类名称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 50, message: '描述长度不能超过 50 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' },
    { type: 'number', min: 0, max: 9999, message: '排序值必须在 0-9999 之间', trigger: 'blur' }
  ]
}

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'categoryName'
}

// 定义 emit 事件
const emit = defineEmits<{
  switchToAttributes: [category: ProductCategory]
}>()

// 方法
const loadStores = async () => {
  try {
    if (!authStore.merchantInfo?.merchantId) {
      ElMessage.warning('商家信息不完整，请重新登录')
      return
    }
    
    const response = await getStoresByMerchantId(authStore.merchantInfo.merchantId)
    if (response.success && response.data) {
      stores.value = response.data
      // 如果只有一个店铺，自动选择
      if (stores.value.length === 1) {
        currentStoreId.value = stores.value[0].storeId
        await loadCategoryTree()
      }
    } else {
      ElMessage.error(response.message || '获取店铺列表失败')
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    ElMessage.error('加载店铺列表失败，请稍后重试')
  }
}

const loadCategoryTree = async () => {
  if (!currentStoreId.value) return
  
  loading.value = true
  try {
    const response = await getCategoryTree(currentStoreId.value)
    if (response.success && response.data) {
      categoryTree.value = response.data
    } else {
      ElMessage.error(response.message || '获取分类树失败')
      categoryTree.value = []
    }
  } catch (error) {
    console.error('加载分类树失败:', error)
    ElMessage.error('加载分类树失败，请稍后重试')
    categoryTree.value = []
  } finally {
    loading.value = false
  }
}

const handleStoreChange = () => {
  loadCategoryTree()
}

const handleAddTopCategory = () => {
  categoryDialogMode.value = 'create'
  resetCategoryForm()
  categoryForm.parentId = null
  categoryDialogVisible.value = true
}

const handleAddSubCategory = (parentCategory: ProductCategory) => {
  categoryDialogMode.value = 'create'
  resetCategoryForm()
  categoryForm.parentId = parentCategory.categoryId
  categoryDialogVisible.value = true
}

const handleManageAttributes = (category: ProductCategory) => {
  // 通知父组件切换到属性管理标签页
  emit('switchToAttributes', category)
}

const handleEdit = (category: ProductCategory) => {
  categoryDialogMode.value = 'edit'
  categoryForm.categoryId = category.categoryId
  categoryForm.parentId = category.parentId || null
  categoryForm.categoryName = category.categoryName
  categoryForm.description = category.description || ''
  categoryForm.sortOrder = category.sortOrder
  categoryForm.isVisible = category.isVisible
  categoryForm.iconUrl = category.iconUrl || ''
  categoryDialogVisible.value = true
}

const handleDelete = async (category: ProductCategory) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${category.categoryName}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteCategory(category.categoryId)
    if (response.success) {
      ElMessage.success('删除成功')
      await loadCategoryTree()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

const handleSaveCategory = async () => {
  if (!categoryFormRef.value) return
  
  try {
    await categoryFormRef.value.validate()
    
    if (!currentStoreId.value) {
      ElMessage.error('请先选择店铺')
      return
    }
    
    submitting.value = true
    
    if (categoryDialogMode.value === 'create') {
      const createData: CreateCategoryRequest = {
        storeId: currentStoreId.value,
        parentId: categoryForm.parentId || undefined,
        categoryName: categoryForm.categoryName,
        description: categoryForm.description,
        sortOrder: categoryForm.sortOrder,
        isVisible: categoryForm.isVisible,
        iconUrl: categoryForm.iconUrl
      }
      
      const response = await createCategory(createData)
      if (response.success) {
        ElMessage.success('创建成功')
        categoryDialogVisible.value = false
        await loadCategoryTree()
      } else {
        ElMessage.error(response.message || '创建失败')
      }
    } else {
      const updateData: UpdateCategoryRequest = {
        categoryName: categoryForm.categoryName,
        description: categoryForm.description,
        sortOrder: categoryForm.sortOrder,
        isVisible: categoryForm.isVisible,
        iconUrl: categoryForm.iconUrl
      }
      
      const response = await updateCategory(categoryForm.categoryId!, updateData)
      if (response.success) {
        ElMessage.success('更新成功')
        categoryDialogVisible.value = false
        await loadCategoryTree()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    }
  } catch (error) {
    console.error('保存分类失败:', error)
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const resetCategoryForm = () => {
  categoryForm.categoryId = null
  categoryForm.parentId = null
  categoryForm.categoryName = ''
  categoryForm.description = ''
  categoryForm.sortOrder = 0
  categoryForm.isVisible = true
  categoryForm.iconUrl = ''
}

// 生命周期
onMounted(() => {
  loadStores()
})
</script>

<style scoped>
.category-management-dashboard {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0 0 8px 0;
  color: #1f2937;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.category-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 400px;
}

.category-tree {
  margin-top: 16px;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 8px 0;
}

.node-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.node-icon {
  margin-right: 8px;
  color: #6b7280;
}

.node-icon img {
  width: 16px;
  height: 16px;
}

.node-label {
  margin-right: 12px;
  font-weight: 500;
}

.product-count {
  margin-right: 8px;
}

.node-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
}

.tree-node:hover .node-actions {
  opacity: 1;
}

:deep(.el-tree-node__content) {
  height: auto;
  padding: 4px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f8fafc;
}
</style> 