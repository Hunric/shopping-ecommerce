<template>
  <div class="attribute-management-dashboard">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="header-title">
          <h2>属性管理</h2>
          <p v-if="selectedCategory">
            {{ selectedCategory.categoryName }} - 管理商品分类属性，为商品发布提供标准化属性模板
          </p>
          <p v-else>管理商品分类属性，为商品发布提供标准化属性模板</p>
        </div>
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
        <el-button type="primary" @click="showCreateDialog" :disabled="!selectedCategory">
          <el-icon><Plus /></el-icon>
          添加属性
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content" :class="{ 'single-column': isFromRoute }">
      <!-- 左侧分类树 -->
      <div v-if="!isFromRoute" class="category-sidebar">
        <div class="sidebar-header">
          <h3>商品分类</h3>
          <el-button link @click="refreshCategories">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </div>
        
        <div class="category-tree-container" v-loading="categoryLoading">
          <el-empty v-if="!currentStoreId" description="请先选择店铺" />
          
          <el-tree
            v-else-if="categoryTree.length > 0"
            :data="categoryTree"
            :props="treeProps"
            node-key="categoryId"
            :expand-on-click-node="false"
            :highlight-current="true"
            @node-click="handleCategorySelect"
            class="category-tree"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <div class="node-content">
                  <el-icon class="node-icon">
                    <Folder v-if="!data.isLeaf" />
                    <Document v-else />
                  </el-icon>
                  <span class="node-label">{{ data.categoryName }}</span>
                  <el-tag v-if="data.isLeaf" size="small" type="success">叶子分类</el-tag>
                </div>
              </div>
            </template>
          </el-tree>
          
          <el-empty v-else description="暂无分类数据" />
        </div>
      </div>

      <!-- 右侧属性管理区域 -->
      <div class="attribute-main">
        <!-- 未选择分类时的提示 -->
        <div v-if="!selectedCategory" class="empty-selection">
          <div class="empty-icon">
            <el-icon size="80"><Setting /></el-icon>
          </div>
          <h3>{{ isFromRoute ? '加载分类信息中...' : '选择分类开始管理属性' }}</h3>
          <p>{{ isFromRoute ? '正在获取分类详情，请稍候' : '请从左侧分类树中选择一个叶子分类来管理其属性' }}</p>
        </div>

                 <!-- 非叶子分类提示 -->
         <div v-else-if="!canManageAttributesFlag" class="permission-warning">
          <el-alert
            title="只有叶子分类才能管理属性"
            description="请选择没有子分类的叶子分类来管理属性"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>

        <!-- 属性管理主界面 -->
        <div v-else class="attribute-content">
          <!-- 分类信息卡片 -->
          <div class="category-info-card">
            <div class="category-header">
              <div class="category-title">
                <h3>{{ selectedCategory.categoryName }}</h3>
                <el-tag type="info" size="small">属性管理</el-tag>
              </div>
              <div class="category-actions">
                <el-button @click="showBatchDialog">
                  <el-icon><DocumentAdd /></el-icon>
                  批量添加
                </el-button>
                <el-button type="primary" @click="showCreateDialog">
                  <el-icon><Plus /></el-icon>
                  添加属性
                </el-button>
              </div>
            </div>
            
            <!-- 属性统计 -->
            <div class="attribute-stats" v-if="attributes.length > 0">
              <div class="stat-item">
                <div class="stat-number">{{ attributes.length }}</div>
                <div class="stat-label">总属性</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ basicAttributeCount }}</div>
                <div class="stat-label">基础属性</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ requiredAttributeCount }}</div>
                <div class="stat-label">必填属性</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ enumAttributeCount }}</div>
                <div class="stat-label">枚举属性</div>
              </div>
            </div>
          </div>

          <!-- 属性列表 -->
          <div class="attribute-list-container" v-loading="attributeLoading">
            <!-- 空状态 -->
            <div v-if="attributes.length === 0 && !attributeLoading" class="empty-attributes">
              <div class="empty-illustration">
                <el-icon size="60"><Box /></el-icon>
              </div>
              <h4>还没有属性</h4>
              <p>为这个分类添加属性，让商品信息更加丰富完整</p>
              <div class="empty-actions">
                <el-button type="primary" @click="showCreateDialog">
                  <el-icon><Plus /></el-icon>
                  创建第一个属性
                </el-button>
                <el-button @click="showBatchDialog">
                  <el-icon><DocumentAdd /></el-icon>
                  批量添加属性
                </el-button>
              </div>
            </div>

            <!-- 属性卡片列表 -->
            <div v-else class="attribute-cards">
              <div
                v-for="attribute in attributes"
                :key="attribute.attributeId"
                class="attribute-card"
              >
                <div class="card-header">
                  <div class="attribute-name">
                    <h4>{{ attribute.attributeName }}</h4>
                    <div class="attribute-badges">
                      <el-tag v-if="attribute.isBasicAttribute" size="small" type="danger">
                        基础属性
                      </el-tag>
                      <el-tag v-if="attribute.isRequired" size="small" type="warning">
                        必填
                      </el-tag>
                    </div>
                  </div>
                  <div class="card-actions">
                    <el-button link @click="editAttribute(attribute)">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                                         <el-button link type="danger" @click="deleteAttributeItem(attribute)">
                       <el-icon><Delete /></el-icon>
                     </el-button>
                  </div>
                </div>
                
                <div class="card-content">
                  <div class="attribute-type">
                    <span class="label">类型：</span>
                    <el-tag :type="getAttributeTypeTagType(attribute.attributeType)" size="small">
                      {{ getAttributeTypeLabel(attribute.attributeType) }}
                    </el-tag>
                  </div>
                  
                  <div v-if="attribute.attributeType === 'ENUM' && attribute.options" class="attribute-options">
                    <span class="label">可选值：</span>
                    <div class="options-list">
                      <el-tag
                        v-for="(option, index) in attribute.options.slice(0, 3)"
                        :key="index"
                        size="small"
                        effect="plain"
                      >
                        {{ option }}
                      </el-tag>
                      <el-tag v-if="attribute.options.length > 3" size="small" type="info" effect="plain">
                        +{{ attribute.options.length - 3 }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑属性对话框 -->
    <el-dialog
      v-model="attributeDialogVisible"
      :title="attributeDialogTitle"
      width="650px"
      destroy-on-close
      class="attribute-dialog"
    >
      <el-form 
        ref="attributeFormRef" 
        :model="attributeForm" 
        :rules="attributeRules" 
        label-width="120px"
        class="attribute-form"
      >
        <el-form-item label="属性名称" prop="attributeName">
          <el-input 
            v-model="attributeForm.attributeName" 
            placeholder="请输入属性名称，如：颜色、尺寸等" 
            maxlength="50" 
            show-word-limit
            style="width: 400px"
          />
        </el-form-item>
        
        <el-form-item label="属性类型" prop="attributeType">
          <el-select 
            v-model="attributeForm.attributeType" 
            placeholder="请选择属性类型"
            @change="handleAttributeTypeChange"
            style="width: 400px"
          >
            <el-option
              v-for="option in ATTRIBUTE_TYPE_OPTIONS"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            >
              <div class="type-option">
                <div class="type-label">{{ option.label }}</div>
                <div class="type-description">{{ option.description }}</div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="基础属性" class="switch-form-item">
          <div class="switch-container">
            <el-switch 
              v-model="attributeForm.isBasicAttribute"
              @change="handleBasicAttributeChange"
              size="default"
            />
            <div class="switch-description">
              <div class="switch-tip">基础属性用于生成SKU，只能是枚举类型，会自动设为必填</div>
              <div v-if="attributeForm.isBasicAttribute && attributeForm.attributeType !== 'ENUM'" class="switch-warning">
                <el-alert
                  title="基础属性只能是枚举类型，请先选择枚举类型"
                  type="warning"
                  :closable="false"
                  show-icon
                  size="small"
                />
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="是否必填" v-if="!attributeForm.isBasicAttribute" class="switch-form-item">
          <div class="switch-container">
            <el-switch 
              v-model="attributeForm.isRequired" 
              size="default"
            />
            <div class="switch-description">
              <div class="switch-tip">设置该属性在商品发布时是否为必填项</div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item 
          label="可选值设置" 
          prop="options"
          v-if="attributeForm.attributeType === 'ENUM'"
          class="options-form-item"
        >
          <div class="options-container">
            <div class="options-input-area">
              <div class="options-tags">
                <el-tag
                  v-for="(option, index) in attributeForm.options"
                  :key="index"
                  closable
                  @close="removeOption(index)"
                  class="option-tag"
                  size="default"
                >
                  {{ option }}
                </el-tag>
              </div>
              <div class="add-option-area">
                <el-input
                  v-if="inputVisible"
                  ref="inputRef"
                  v-model="inputValue"
                  size="default"
                  placeholder="输入选项值"
                  style="width: 150px"
                  @keyup.enter="handleInputConfirm"
                  @blur="handleInputConfirm"
                />
                <el-button v-else size="default" @click="showInput" type="primary" plain>
                  <el-icon><Plus /></el-icon>
                  添加选项
                </el-button>
              </div>
            </div>
            <div class="options-tip">为枚举类型属性设置可选值，用户在发布商品时可从这些选项中选择</div>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="attributeDialogVisible = false" size="default">取消</el-button>
          <el-button type="primary" @click="handleSaveAttribute" :loading="submitting" size="default">
            {{ attributeDialogMode === 'create' ? '创建属性' : '保存修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量添加对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量添加属性"
      width="900px"
      destroy-on-close
      class="batch-dialog"
    >
      <div class="batch-add-content">
        <div class="batch-header">
          <div class="batch-title">
            <h4>批量添加属性</h4>
            <p>一次性添加多个属性，提高配置效率</p>
          </div>
          <div class="batch-actions">
            <el-button type="primary" @click="addBatchAttribute" size="default">
              <el-icon><Plus /></el-icon>
              添加属性
            </el-button>
            <el-button @click="clearBatchAttributes" size="default">清空全部</el-button>
          </div>
        </div>
        
        <div class="batch-list" v-if="batchAttributes.length > 0">
          <div
            v-for="(attr, index) in batchAttributes"
            :key="index"
            class="batch-item"
          >
            <el-card class="batch-card">
              <div class="batch-item-header">
                <div class="item-title">
                  <el-icon><Setting /></el-icon>
                  <span>属性 {{ index + 1 }}</span>
                </div>
                <el-button 
                  link 
                  type="danger" 
                  @click="removeBatchAttribute(index)"
                  :icon="Delete"
                  size="small"
                >
                  删除
                </el-button>
              </div>
              
              <el-form :model="attr" label-width="100px" size="default" class="batch-form">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="属性名称" required>
                      <el-input 
                        v-model="attr.attributeName" 
                        placeholder="请输入属性名称" 
                        maxlength="50"
                      />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="属性类型" required>
                      <el-select v-model="attr.attributeType" placeholder="选择类型" style="width: 100%">
                        <el-option
                          v-for="option in ATTRIBUTE_TYPE_OPTIONS"
                          :key="option.value"
                          :label="option.label"
                          :value="option.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="基础属性">
                      <div class="batch-switch-item">
                        <el-switch v-model="attr.isBasicAttribute" />
                        <span class="switch-label">用于生成SKU</span>
                      </div>
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="是否必填">
                      <div class="batch-switch-item">
                        <el-switch v-model="attr.isRequired" :disabled="attr.isBasicAttribute" />
                        <span class="switch-label">商品发布时必填</span>
                      </div>
                    </el-form-item>
                  </el-col>
                </el-row>
                
                <el-form-item v-if="attr.attributeType === 'ENUM'" label="可选值" class="batch-options-item">
                  <el-input
                    v-model="attr.optionsText"
                    type="textarea"
                    placeholder="请输入可选值，每行一个选项&#10;例如：&#10;红色&#10;蓝色&#10;绿色"
                    :rows="4"
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
              </el-form>
            </el-card>
          </div>
        </div>
        
        <div v-else class="batch-empty">
          <div class="empty-icon">
            <el-icon size="48"><DocumentAdd /></el-icon>
          </div>
          <h4>还没有添加属性</h4>
          <p>点击"添加属性"按钮开始批量创建属性</p>
          <el-button type="primary" @click="addBatchAttribute" size="default">
            <el-icon><Plus /></el-icon>
            添加第一个属性
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchDialogVisible = false" size="default">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleBatchSave" 
            :loading="batchSubmitting" 
            :disabled="batchAttributes.length === 0"
            size="default"
          >
            批量保存 ({{ batchAttributes.length }} 个属性)
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Plus, DocumentAdd, Delete, Edit, Box, Setting, Folder, Document, Refresh
} from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { getStoresByMerchantId, type Store } from '@/api/merchant/store'
import { getCategoryTree, type ProductCategory } from '@/api/merchant/category'
import {
  getCategoryAttributes,
  createAttribute,
  updateAttribute,
  deleteAttribute,
  batchCreateAttributes,
  canManageAttributes,
  type CategoryAttribute,
  type CreateAttributeRequest,
  type UpdateAttributeRequest,
  ATTRIBUTE_TYPE_OPTIONS,
  getAttributeTypeLabel
} from '@/api/merchant/categoryAttribute'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

// 状态管理
const categoryLoading = ref(false)
const attributeLoading = ref(false)
const submitting = ref(false)
const batchSubmitting = ref(false)

// 店铺和分类数据
const stores = ref<Store[]>([])
const currentStoreId = ref<number | null>(null)
const categoryTree = ref<ProductCategory[]>([])
const selectedCategory = ref<ProductCategory | null>(null)
const canManageAttributesFlag = ref(false)

// 属性数据
const attributes = ref<CategoryAttribute[]>([])

// 对话框状态
const attributeDialogVisible = ref(false)
const attributeDialogMode = ref<'create' | 'edit'>('create')
const attributeDialogTitle = computed(() => 
  attributeDialogMode.value === 'create' ? '添加属性' : '编辑属性'
)

const batchDialogVisible = ref(false)
const batchAttributes = ref<Array<{
  attributeName: string
  attributeType: string
  isBasicAttribute: boolean
  isRequired: boolean
  optionsText: string
}>>([])

// 表单数据
const attributeFormRef = ref()
const attributeForm = reactive({
  attributeId: null as number | null,
  attributeName: '',
  attributeType: '',
  isBasicAttribute: false,
  isRequired: false,
  options: [] as string[]
})

// 可选值输入
const inputVisible = ref(false)
const inputValue = ref('')
const inputRef = ref()

// 树形控件配置
const treeProps = {
  children: 'children',
  label: 'categoryName'
}

// 表单验证规则
const attributeRules = {
  attributeName: [
    { required: true, message: '请输入属性名称', trigger: 'blur' },
    { min: 1, max: 50, message: '属性名称长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  attributeType: [
    { required: true, message: '请选择属性类型', trigger: 'change' },
    { 
      validator: (rule: any, value: string, callback: Function) => {
        if (attributeForm.isBasicAttribute && value !== 'ENUM') {
          callback(new Error('基础属性只能是枚举类型'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  options: [
    { 
      validator: (rule: any, value: string[], callback: Function) => {
        if (attributeForm.attributeType === 'ENUM' && (!value || value.length === 0)) {
          callback(new Error('枚举类型必须设置可选值'))
        } else if (attributeForm.isBasicAttribute && (!value || value.length === 0)) {
          callback(new Error('基础属性必须设置可选值'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
}

// 计算属性
const isFromRoute = computed(() => !!route.params.categoryId)

const basicAttributeCount = computed(() => 
  attributes.value.filter(attr => attr.isBasicAttribute).length
)

const requiredAttributeCount = computed(() => 
  attributes.value.filter(attr => attr.isRequired).length
)

const enumAttributeCount = computed(() => 
  attributes.value.filter(attr => attr.attributeType === 'ENUM').length
)

// 定义 props
const props = defineProps<{
  selectedCategory?: ProductCategory | null
}>()

// 监听传入的分类变化
watch(() => props.selectedCategory, async (newCategory) => {
  if (newCategory && newCategory.categoryId) {
    try {
      await handleCategorySelect(newCategory)
    } catch (error) {
      console.error('处理分类选择失败:', error)
    }
  }
}, { immediate: false })

// 初始化
onMounted(async () => {
  try {
    await loadStores()
    
    // 如果路由中有categoryId参数，自动选择对应的分类
    const categoryId = route.params.categoryId
    if (categoryId && !isNaN(Number(categoryId))) {
      await selectCategoryById(Number(categoryId))
    }
    
    // 如果有传入的分类，处理它
    if (props.selectedCategory && props.selectedCategory.categoryId) {
      await handleCategorySelect(props.selectedCategory)
    }
  } catch (error) {
    console.error('组件初始化失败:', error)
  }
})

// 加载店铺列表
const loadStores = async () => {
  if (!authStore.merchantInfo?.merchantId) {
    console.warn('商家信息不完整，跳过加载店铺列表')
    return
  }
  
  try {
    const response = await getStoresByMerchantId(authStore.merchantInfo.merchantId)
    if (response.success && response.data) {
      stores.value = response.data
      if (stores.value.length > 0 && !currentStoreId.value) {
        currentStoreId.value = stores.value[0].storeId
        await loadCategoryTree()
      }
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
    // 不显示错误消息，因为这可能在组件初始化时发生
  }
}

// 加载分类树
const loadCategoryTree = async () => {
  if (!currentStoreId.value) return
  
  categoryLoading.value = true
  try {
    const response = await getCategoryTree(currentStoreId.value)
    if (response.success && response.data) {
      categoryTree.value = response.data
    } else {
      ElMessage.error(response.message || '加载分类失败')
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败')
  } finally {
    categoryLoading.value = false
  }
}

// 根据categoryId选择分类
const selectCategoryById = async (categoryId: number) => {
  // 等待分类树加载完成
  if (categoryTree.value.length === 0) {
    await loadCategoryTree()
  }
  
  // 递归查找分类
  const findCategory = (categories: ProductCategory[], id: number): ProductCategory | null => {
    for (const category of categories) {
      if (category.categoryId === id) {
        return category
      }
      if (category.children && category.children.length > 0) {
        const found = findCategory(category.children, id)
        if (found) return found
      }
    }
    return null
  }
  
  const category = findCategory(categoryTree.value, categoryId)
  if (category) {
    await handleCategorySelect(category)
  } else {
    ElMessage.warning('未找到指定的分类')
  }
}

// 刷新分类
const refreshCategories = () => {
  selectedCategory.value = null
  attributes.value = []
  loadCategoryTree()
}

// 切换店铺
const handleStoreChange = () => {
  selectedCategory.value = null
  attributes.value = []
  loadCategoryTree()
}

 // 选择分类
 const handleCategorySelect = async (category: ProductCategory) => {
   selectedCategory.value = category
   
   // 检查是否可以管理属性
   try {
     const response = await canManageAttributes(category.categoryId)
     canManageAttributesFlag.value = response.success && response.data
     
     if (canManageAttributesFlag.value) {
       loadAttributes()
     } else {
       attributes.value = []
     }
   } catch (error) {
     console.error('检查权限失败:', error)
     canManageAttributesFlag.value = false
     attributes.value = []
   }
 }

// 加载属性列表
const loadAttributes = async () => {
  if (!selectedCategory.value) return
  
  attributeLoading.value = true
  try {
    const response = await getCategoryAttributes(selectedCategory.value.categoryId)
    if (response.success && response.data) {
      attributes.value = response.data
    } else {
      ElMessage.error(response.message || '加载属性失败')
    }
  } catch (error) {
    console.error('加载属性失败:', error)
    ElMessage.error('加载属性失败')
  } finally {
    attributeLoading.value = false
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  if (!selectedCategory.value) {
    ElMessage.warning('请先选择一个分类')
    return
  }
  resetAttributeForm()
  attributeDialogMode.value = 'create'
  attributeDialogVisible.value = true
}

// 编辑属性
const editAttribute = (attribute: CategoryAttribute) => {
  resetAttributeForm()
  attributeForm.attributeId = attribute.attributeId
  attributeForm.attributeName = attribute.attributeName
  attributeForm.attributeType = attribute.attributeType
  attributeForm.isBasicAttribute = attribute.isBasicAttribute
  attributeForm.isRequired = attribute.isRequired
  attributeForm.options = attribute.options ? [...attribute.options] : []
  attributeDialogMode.value = 'edit'
  attributeDialogVisible.value = true
}

 // 删除属性
 const deleteAttributeItem = async (attribute: CategoryAttribute) => {
   try {
     await ElMessageBox.confirm(
       `确定要删除属性"${attribute.attributeName}"吗？`,
       '确认删除',
       {
         confirmButtonText: '确定',
         cancelButtonText: '取消',
         type: 'warning'
       }
     )
     
     const response = await deleteAttribute(attribute.attributeId)
     if (response.success) {
       ElMessage.success('删除成功')
       loadAttributes()
     } else {
       ElMessage.error(response.message || '删除失败')
     }
   } catch (error) {
     if (error !== 'cancel') {
       console.error('删除属性失败:', error)
       ElMessage.error('删除失败')
     }
   }
 }

// 保存属性
const handleSaveAttribute = async () => {
  const valid = await attributeFormRef.value.validate()
  if (!valid) return
  
  submitting.value = true
  try {
    if (attributeDialogMode.value === 'create') {
      const createData: CreateAttributeRequest = {
        storeId: currentStoreId.value!,
        categoryId: selectedCategory.value!.categoryId,
        attributeName: attributeForm.attributeName,
        attributeType: attributeForm.attributeType as "TEXT" | "NUMBER" | "DATE" | "BOOLEAN" | "ENUM",
        isBasicAttribute: attributeForm.isBasicAttribute,
        isRequired: attributeForm.isRequired,
        options: attributeForm.attributeType === 'ENUM' ? attributeForm.options : undefined
      }
      
      const response = await createAttribute(createData)
      if (response.success) {
        ElMessage.success('创建成功')
        attributeDialogVisible.value = false
        loadAttributes()
      } else {
        ElMessage.error(response.message || '创建失败')
      }
    } else {
      const updateData: UpdateAttributeRequest = {
        attributeName: attributeForm.attributeName,
        attributeType: attributeForm.attributeType as "TEXT" | "NUMBER" | "DATE" | "BOOLEAN" | "ENUM",
        isBasicAttribute: attributeForm.isBasicAttribute,
        isRequired: attributeForm.isRequired,
        options: attributeForm.attributeType === 'ENUM' ? attributeForm.options : undefined
      }
      
      const response = await updateAttribute(attributeForm.attributeId!, updateData)
      if (response.success) {
        ElMessage.success('更新成功')
        attributeDialogVisible.value = false
        loadAttributes()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    }
  } catch (error) {
    console.error('保存属性失败:', error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

// 批量添加相关方法
const showBatchDialog = () => {
  if (!selectedCategory.value) {
    ElMessage.warning('请先选择一个分类')
    return
  }
  batchAttributes.value = []
  addBatchAttribute()
  batchDialogVisible.value = true
}

const addBatchAttribute = () => {
  batchAttributes.value.push({
    attributeName: '',
    attributeType: 'TEXT',
    isBasicAttribute: false,
    isRequired: false,
    optionsText: ''
  })
}

const removeBatchAttribute = (index: number) => {
  batchAttributes.value.splice(index, 1)
}

const clearBatchAttributes = () => {
  batchAttributes.value = []
}

const handleBatchSave = async () => {
  const validAttributes = batchAttributes.value.filter(attr => 
    attr.attributeName.trim() && attr.attributeType
  )
  
  if (validAttributes.length === 0) {
    ElMessage.warning('请至少添加一个有效属性')
    return
  }
  
  batchSubmitting.value = true
  try {
    const createRequests: CreateAttributeRequest[] = validAttributes.map(attr => ({
      storeId: currentStoreId.value!,
      categoryId: selectedCategory.value!.categoryId,
      attributeName: attr.attributeName.trim(),
      attributeType: attr.attributeType as "TEXT" | "NUMBER" | "DATE" | "BOOLEAN" | "ENUM",
      isBasicAttribute: attr.isBasicAttribute,
      isRequired: attr.isBasicAttribute || attr.isRequired,
      options: attr.attributeType === 'ENUM' && attr.optionsText 
        ? attr.optionsText.split('\n').map(s => s.trim()).filter(s => s)
        : undefined
    }))
    
    const response = await batchCreateAttributes({
      storeId: currentStoreId.value!,
      categoryId: selectedCategory.value!.categoryId,
      attributes: createRequests
    })
    
    if (response.success) {
      ElMessage.success(`成功创建 ${response.data.length} 个属性`)
      batchDialogVisible.value = false
      loadAttributes()
    } else {
      ElMessage.error(response.message || '批量创建失败')
    }
  } catch (error) {
    console.error('批量创建属性失败:', error)
    ElMessage.error('批量创建失败')
  } finally {
    batchSubmitting.value = false
  }
}

// 工具方法
const handleAttributeTypeChange = () => {
  if (attributeForm.attributeType !== 'ENUM') {
    attributeForm.options = []
    // 如果当前是基础属性但选择了非枚举类型，自动取消基础属性
    if (attributeForm.isBasicAttribute) {
      attributeForm.isBasicAttribute = false
      attributeForm.isRequired = false
      ElMessage.warning('基础属性只能是枚举类型，已自动取消基础属性选择')
    }
  }
}

const handleBasicAttributeChange = (value: boolean) => {
  if (value) {
    // 基础属性必须是枚举类型且必填
    if (attributeForm.attributeType !== 'ENUM') {
      attributeForm.attributeType = 'ENUM'
      ElMessage.info('基础属性已自动设置为枚举类型')
    }
    attributeForm.isRequired = true
  }
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value && !attributeForm.options.includes(inputValue.value)) {
    attributeForm.options.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

const removeOption = (index: number) => {
  attributeForm.options.splice(index, 1)
}

const resetAttributeForm = () => {
  attributeForm.attributeId = null
  attributeForm.attributeName = ''
  attributeForm.attributeType = ''
  attributeForm.isBasicAttribute = false
  attributeForm.isRequired = false
  attributeForm.options = []
  attributeFormRef.value?.clearValidate()
}

const getAttributeTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    'TEXT': 'info',
    'NUMBER': 'success',
    'DATE': 'primary',
    'BOOLEAN': 'warning',
    'ENUM': 'danger'
  }
  return typeMap[type] || 'info'
}
</script>

<style scoped>
.attribute-management-dashboard {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: flex-start;
}

.header-title {
  display: flex;
  flex-direction: column;
}

.header-title h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-title p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.main-content.single-column {
  display: block;
}

.main-content.single-column .attribute-main {
  width: 100%;
  max-width: none;
  padding: 0;
}

.category-sidebar {
  width: 300px;
  background: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.category-tree-container {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.category-tree {
  font-size: 14px;
}

.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-icon {
  font-size: 16px;
  color: #909399;
}

.node-label {
  font-size: 14px;
}

.attribute-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.empty-selection {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 24px;
  opacity: 0.6;
}

.empty-selection h3 {
  margin: 0 0 12px 0;
  font-size: 20px;
  color: #303133;
}

.empty-selection p {
  margin: 0;
  color: #606266;
}

.permission-warning {
  margin-bottom: 24px;
}

.category-info-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-title h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.category-actions {
  display: flex;
  gap: 12px;
}

.attribute-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.attribute-list-container {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.empty-attributes {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-illustration {
  margin-bottom: 24px;
  opacity: 0.6;
}

.empty-attributes h4 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #303133;
}

.empty-attributes p {
  margin: 0 0 24px 0;
  color: #606266;
}

.empty-actions {
  display: flex;
  gap: 16px;
}

.attribute-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.attribute-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
}

.attribute-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.attribute-name h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.attribute-badges {
  display: flex;
  gap: 4px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.attribute-type,
.attribute-options {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.label {
  font-size: 14px;
  color: #606266;
  min-width: 60px;
}

.options-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.form-tip {
  font-size: 13px;
  color: #606266;
  margin-top: 6px;
  line-height: 1.5;
}

.form-warning {
  margin-top: 8px;
}

.option-description {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  line-height: 1.4;
}

.options-input {
  min-height: 80px;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #fafafa;
}

.options-input-area {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  padding: 12px;
  background: #fafafa;
  min-height: 80px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.options-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 32px;
  align-items: flex-start;
}

.option-tag {
  margin: 0;
}

.add-option-area {
  display: flex;
  align-items: center;
}

.options-tip {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  margin: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 0 0 0;
}

.batch-dialog {
  /* 批量对话框样式 */
}

.batch-add-content {
  max-height: 600px;
  overflow-y: auto;
}

.batch-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.batch-title h4 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.batch-title p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.batch-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.batch-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.batch-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  background: #fafafa;
  border-radius: 8px;
  border: 2px dashed #d9d9d9;
}

.batch-empty .empty-icon {
  margin-bottom: 16px;
  color: #c0c4cc;
}

.batch-empty h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.batch-empty p {
  margin: 0 0 20px 0;
  color: #606266;
  font-size: 14px;
}

.batch-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.batch-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.batch-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.item-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #303133;
}

.item-title .el-icon {
  color: #409eff;
}

.batch-form {
  /* 批量表单样式 */
}

.batch-form .el-form-item {
  margin-bottom: 20px;
}

.batch-form .el-form-item__label {
  font-weight: 500;
  color: #303133;
}

.batch-switch-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.switch-label {
  font-size: 13px;
  color: #606266;
  margin: 0;
}

.batch-options-item .el-form-item__content {
  line-height: normal;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .attribute-form .el-form-item__label {
    width: 100px !important;
  }
  
  .attribute-form .el-input,
  .attribute-form .el-select {
    width: 100% !important;
  }
  
  .switch-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .batch-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .batch-actions {
    justify-content: flex-start;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-form-item__label) {
  line-height: 1.5;
}

:deep(.el-input__wrapper) {
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc;
}

:deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

:deep(.el-switch) {
  --el-switch-on-color: #409eff;
}

:deep(.el-tag) {
  border-radius: 4px;
}

:deep(.el-alert) {
  border-radius: 6px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 0 20px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 0 20px 20px 20px;
}

.attribute-dialog {
  /* 属性对话框样式 */
}

.attribute-form {
  padding: 8px 0;
}

.attribute-form .el-form-item {
  margin-bottom: 28px;
}

.attribute-form .el-form-item__label {
  font-weight: 500;
  color: #303133;
}

.type-option {
  padding: 4px 0;
}

.type-label {
  font-weight: 500;
  color: #303133;
}

.type-description {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
  line-height: 1.4;
}

.switch-form-item .el-form-item__content {
  line-height: normal;
}

.switch-container {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  width: 100%;
}

.switch-description {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.switch-tip {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  margin: 0;
}

.switch-warning {
  margin-top: 4px;
}

.options-form-item .el-form-item__content {
  line-height: normal;
}

.options-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 树形控件样式 */
:deep(.el-tree-node__content) {
  height: 36px;
}

:deep(.el-tree-node__expand-icon) {
  font-size: 16px;
}

/* 卡片样式覆盖 */
:deep(.el-card__body) {
  padding: 20px;
}
</style> 