<template>
  <div class="product-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      @submit.prevent
    >
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="form-section">
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="商品名称" prop="spuName" required>
                  <el-input
                    v-model="formData.spuName"
                    placeholder="请输入商品名称（最多20字符）"
                    maxlength="20"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="商品分类" prop="categoryId" required>
                  <el-cascader
                    v-model="formData.categoryId"
                    :options="categoryOptions"
                    :props="cascaderProps"
                    placeholder="请选择商品分类（必须选择到具体分类）"
                    style="width: 100%"
                    @change="handleCategoryChange"
                    clearable
                    filterable
                  />
                  <div class="category-tip">
                    <el-text size="small" type="info">
                      <el-icon><InfoFilled /></el-icon>
                      请选择到具体的商品分类，选择后将自动加载该分类的属性
                    </el-text>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="展示价格" prop="displayPrice" required>
                  <el-input-number
                    v-model="formData.displayPrice"
                    :min="0"
                    :precision="2"
                    placeholder="商品展示价格"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="计量单位" prop="unit">
                  <el-select v-model="formData.unit" placeholder="请选择单位" style="width: 100%">
                    <el-option label="件" value="件" />
                    <el-option label="台" value="台" />
                    <el-option label="套" value="套" />
                    <el-option label="个" value="个" />
                    <el-option label="盒" value="盒" />
                    <el-option label="包" value="包" />
                    <el-option label="瓶" value="瓶" />
                    <el-option label="袋" value="袋" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="品牌名称" prop="brandName">
                  <el-input
                    v-model="formData.brandName"
                    placeholder="请输入品牌名称"
                    maxlength="50"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="商品图片" prop="productImage">
                  <el-upload
                    class="image-uploader"
                    action="#"
                    :show-file-list="false"
                    :before-upload="beforeImageUpload"
                    :http-request="handleImageUpload"
                  >
                    <img v-if="formData.productImage" :src="formData.productImage" class="uploaded-image" />
                    <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="商品描述" prop="spuDescription">
              <el-input
                v-model="formData.spuDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入商品描述（最多100字符）"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="卖点描述" prop="sellingPoint">
              <el-input
                v-model="formData.sellingPoint"
                type="textarea"
                :rows="3"
                placeholder="请输入商品卖点"
              />
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 基础属性 -->
        <el-tab-pane label="基础属性" name="basic-attrs">
          <div class="form-section">
            <div class="section-header">
              <h3>基础属性设置</h3>
              <p>基础属性用于生成SKU，每个属性至少需要选择一个值。选择不同的属性值组合将生成对应的SKU</p>
            </div>
            
            <!-- 分类未选择状态 -->
            <div v-if="!formData.categoryId" class="empty-attrs">
              <el-empty description="请先选择商品分类以加载基础属性">
                <el-button type="primary" @click="activeTab = 'basic'">
                  去选择分类
                </el-button>
              </el-empty>
            </div>
            
            <!-- 分类已选择但无属性 -->
            <div v-else-if="basicAttributes.length === 0" class="empty-attrs">
              <el-empty description="该分类暂无基础属性配置">
                <el-alert
                  title="该分类暂无基础属性，无法生成SKU。请联系管理员为此分类添加基础属性。"
                  type="warning"
                  :closable="false"
                  show-icon
                />
              </el-empty>
            </div>
            
            <!-- 有基础属性的情况 -->
            <div v-else class="attrs-container">
              <div class="attrs-tip">
                <el-alert
                  title="提示：基础属性的不同组合将生成不同的SKU，请根据实际商品情况选择"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </div>
              
              <div
                v-for="attr in basicAttributes"
                :key="attr.attributeId"
                class="attr-item"
                :class="{ 'attr-error': isBasicAttributeRequired(attr) && !hasSelectedValues(attr) }"
              >
                <div class="attr-header">
                  <span class="attr-name">{{ attr.attributeName }}</span>
                  <span class="attr-required">*</span>
                  <span class="attr-count" v-if="formData.basicAttributes[attr.attributeName]?.length">
                    已选择 {{ formData.basicAttributes[attr.attributeName].length }} 项
                  </span>
                </div>
                
                <div class="attr-values">
                  <el-checkbox-group 
                    v-model="formData.basicAttributes[attr.attributeName]"
                    @change="handleBasicAttributeChange"
                  >
                    <el-checkbox
                      v-for="option in attr.options"
                      :key="option.optionId"
                      :value="option.optionValue"
                      class="attr-checkbox"
                    >
                      {{ option.optionValue }}
                    </el-checkbox>
                  </el-checkbox-group>
                </div>
                
                <div v-if="isBasicAttributeRequired(attr) && !hasSelectedValues(attr)" class="attr-error-tip">
                  请至少选择一个{{ attr.attributeName }}
                </div>
              </div>
              
              <div class="attrs-summary">
                <el-alert
                  v-if="getSelectedBasicAttributesCount() === 0"
                  title="请选择基础属性值以生成SKU"
                  type="warning"
                  :closable="false"
                  show-icon
                />
                <el-alert
                  v-else
                  :title="`将生成 ${calculateSKUCount()} 个SKU组合`"
                  type="success"
                  :closable="false"
                  show-icon
                />
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 非基础属性 -->
        <el-tab-pane label="非基础属性" name="non-basic-attrs">
          <div class="form-section">
            <div class="section-header">
              <h3>非基础属性设置</h3>
              <p>非基础属性用于商品详情展示，不影响SKU生成</p>
            </div>
            
            <!-- 分类未选择状态 -->
            <div v-if="!formData.categoryId" class="empty-attrs">
              <el-empty description="请先选择商品分类以加载非基础属性">
                <el-button type="primary" @click="activeTab = 'basic'">
                  去选择分类
                </el-button>
              </el-empty>
            </div>
            
            <!-- 分类已选择但无属性 -->
            <div v-else-if="nonBasicAttributes.length === 0" class="empty-attrs">
              <el-empty description="该分类暂无非基础属性配置">
                <el-alert
                  title="该分类暂无非基础属性，可以跳过此步骤。"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </el-empty>
            </div>
            
            <!-- 有非基础属性的情况 -->
            <div v-else class="attrs-container">
              <div class="attrs-tip">
                <el-alert
                  title="提示：非基础属性用于商品详情展示，可根据需要选择性填写"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </div>
              
              <div
                v-for="attr in nonBasicAttributes"
                :key="attr.attributeId"
                class="attr-item"
              >
                <div class="attr-header">
                  <span class="attr-name">{{ attr.attributeName }}</span>
                  <span v-if="attr.isRequired" class="attr-required">*</span>
                  <span class="attr-type">{{ getAttributeTypeLabel(attr.attributeType) }}</span>
                </div>
                
                <div class="attr-input">
                  <!-- 文本类型 -->
                  <el-input
                    v-if="attr.attributeType === 'TEXT'"
                    v-model="formData.nonBasicAttributes[attr.attributeName]"
                    :placeholder="`请输入${attr.attributeName}`"
                  />
                  
                  <!-- 数字类型 -->
                  <el-input-number
                    v-else-if="attr.attributeType === 'NUMBER'"
                    v-model="formData.nonBasicAttributes[attr.attributeName]"
                    :placeholder="`请输入${attr.attributeName}`"
                    style="width: 100%"
                  />
                  
                  <!-- 日期类型 -->
                  <el-date-picker
                    v-else-if="attr.attributeType === 'DATE'"
                    v-model="formData.nonBasicAttributes[attr.attributeName]"
                    type="date"
                    :placeholder="`请选择${attr.attributeName}`"
                    style="width: 100%"
                  />
                  
                  <!-- 布尔类型 -->
                  <el-switch
                    v-else-if="attr.attributeType === 'BOOLEAN'"
                    v-model="formData.nonBasicAttributes[attr.attributeName]"
                  />
                  
                  <!-- 枚举类型 -->
                  <el-select
                    v-else-if="attr.attributeType === 'ENUM'"
                    v-model="formData.nonBasicAttributes[attr.attributeName]"
                    :placeholder="`请选择${attr.attributeName}`"
                    style="width: 100%"
                    clearable
                  >
                    <el-option
                      v-for="option in attr.options"
                      :key="option.optionId"
                      :label="option.optionValue"
                      :value="option.optionValue"
                    />
                  </el-select>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- SKU预览 -->
        <el-tab-pane label="SKU预览" name="sku-preview">
          <div class="form-section">
            <div class="section-header">
              <h3>SKU组合预览</h3>
              <p>根据基础属性自动生成的SKU组合，请为每个SKU设置价格和库存</p>
              <div class="header-actions">
                <el-button type="primary" @click="generateSKUPreview">
                  <el-icon><Refresh /></el-icon>
                  重新生成
                </el-button>
                <el-button v-if="skuPreview.length > 0" @click="handleBatchSetPrice">
                  <el-icon><Money /></el-icon>
                  批量设价格
                </el-button>
                <el-button v-if="skuPreview.length > 0" @click="handleBatchSetStock">
                  <el-icon><Box /></el-icon>
                  批量设库存
                </el-button>
              </div>
            </div>
            
            <div v-if="skuPreview.length === 0" class="empty-sku">
              <el-empty description="请先设置基础属性以生成SKU">
                <el-button type="primary" @click="activeTab = 'basic-attrs'">
                  去设置基础属性
                </el-button>
              </el-empty>
            </div>
            
            <div v-else class="sku-table">
              <div class="sku-summary">
                <el-alert
                  :title="`已生成 ${skuPreview.length} 个SKU组合`"
                  type="info"
                  :closable="false"
                  show-icon
                />
              </div>
              
              <el-table :data="skuPreview" border class="sku-preview-table">
                <el-table-column label="序号" type="index" width="60" align="center" />
                
                <el-table-column label="SKU名称" prop="skuName" min-width="200">
                  <template #default="{ row }">
                    <div class="sku-name">
                      <el-icon class="sku-icon"><Goods /></el-icon>
                      {{ row.skuName }}
                    </div>
                  </template>
                </el-table-column>
                
                <el-table-column label="属性组合" min-width="250">
                  <template #default="{ row }">
                    <div class="attribute-tags">
                      <el-tag
                        v-for="(value, key) in row.attributeCombination"
                        :key="key"
                        size="small"
                        type="primary"
                        effect="plain"
                        style="margin-right: 8px; margin-bottom: 4px"
                      >
                        {{ key }}: {{ value }}
                      </el-tag>
                    </div>
                  </template>
                </el-table-column>
                
                <el-table-column label="销售价格" width="180" align="center">
                  <template #default="{ row, $index }">
                    <div class="price-input">
                      <el-input-number
                        v-model="row.salePrice"
                        :min="0"
                        :precision="2"
                        size="small"
                        style="width: 100%"
                        placeholder="请输入价格"
                        :class="{ 'error-input': !row.salePrice || row.salePrice <= 0 }"
                      />
                      <div v-if="!row.salePrice || row.salePrice <= 0" class="error-tip">
                        请设置价格
                      </div>
                    </div>
                  </template>
                </el-table-column>
                
                <el-table-column label="库存数量" width="150" align="center">
                  <template #default="{ row, $index }">
                    <div class="stock-input">
                      <el-input-number
                        v-model="row.stockQuantity"
                        :min="0"
                        size="small"
                        style="width: 100%"
                        placeholder="请输入库存"
                        :class="{ 'warning-input': row.stockQuantity === 0 }"
                      />
                      <div v-if="row.stockQuantity === 0" class="warning-tip">
                        库存为0
                      </div>
                    </div>
                  </template>
                </el-table-column>
                
                <el-table-column label="状态" width="100" align="center">
                  <template #default="{ row }">
                    <el-switch
                      v-model="row.status"
                      :active-value="1"
                      :inactive-value="2"
                      active-text="上架"
                      inactive-text="下架"
                      size="small"
                    />
                  </template>
                </el-table-column>
              </el-table>
              
              <div class="sku-validation">
                <el-alert
                  v-if="getInvalidSKUs().length > 0"
                  :title="`有 ${getInvalidSKUs().length} 个SKU未设置价格，请完善后再提交`"
                  type="warning"
                  :closable="false"
                  show-icon
                />
                <el-alert
                  v-else
                  title="所有SKU信息已完善，可以提交"
                  type="success"
                  :closable="false"
                  show-icon
                />
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <!-- 操作按钮 -->
    <div class="form-actions">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button @click="handleSaveDraft" :loading="submitting">保存草稿</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        {{ mode === 'edit' ? '更新商品' : '创建商品' }}
      </el-button>
      <el-button 
        type="info" 
        @click="testAPIs" 
        size="small"
        style="margin-left: 12px"
      >
        测试API
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Money, Box, Goods, InfoFilled } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'

// 导入API和类型
import {
  createSPU, updateSPU, generateSKUCombinations,
  getCategoryBasicAttributes, getCategoryNonBasicAttributes,
  type SPU, type SPUCreateData, type SPUUpdateData
} from '@/api/merchant/spu'
import { getCategoryTree, type ProductCategory } from '@/api/merchant/category'
import { useAuthStore } from '@/store/modules/auth'

// Props
interface Props {
  storeId: number
  editProduct?: SPU | null
  mode: 'create' | 'edit'
}

const props = withDefaults(defineProps<Props>(), {
  editProduct: null,
  mode: 'create'
})

// Emits
const emit = defineEmits<{
  cancel: []
  success: []
}>()

// 响应式数据
const authStore = useAuthStore()
const formRef = ref<FormInstance>()
const activeTab = ref('basic')
const submitting = ref(false)

// 表单数据
const formData = reactive<SPUCreateData>({
  merchantId: authStore.merchantInfo?.merchantId || 0,
  storeId: props.storeId,
  categoryId: 0,
  spuName: '',
  spuDescription: '',
  productImage: '',
  displayPrice: 0,
  basicAttributes: {},
  nonBasicAttributes: {},
  brandName: '',
  sellingPoint: '',
  unit: '件',
  skus: []
})

// 分类和属性数据
const categories = ref<ProductCategory[]>([])
const basicAttributes = ref<any[]>([])
const nonBasicAttributes = ref<any[]>([])
const skuPreview = ref<any[]>([])

// 表单验证规则
const formRules: FormRules = {
  spuName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 1, max: 20, message: '商品名称长度为1-20个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  displayPrice: [
    { required: true, message: '请输入展示价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ]
}

// 级联选择器配置
const cascaderProps = {
  value: 'categoryId',
  label: 'categoryName',
  children: 'children',
  checkStrictly: false,
  emitPath: false,
  // 只允许选择叶子节点
  leaf: true
}

// 计算属性
const categoryOptions = computed(() => {
  return buildCategoryTree(categories.value)
})

// 方法
const buildCategoryTree = (cats: ProductCategory[]): any[] => {
  return cats.map(cat => ({
    categoryId: cat.categoryId,
    categoryName: cat.categoryName,
    children: cat.children && cat.children.length > 0 ? buildCategoryTree(cat.children) : undefined
  }))
}

const loadCategories = async () => {
  try {
    console.log('=== 开始加载分类数据 ===')
    console.log('店铺ID:', props.storeId)
    
    if (!props.storeId) {
      console.error('店铺ID为空，无法加载分类')
      ElMessage.error('店铺ID为空，无法加载分类')
      return
    }
    
    const response = await getCategoryTree(props.storeId)
    console.log('分类API响应:', response)
    
    const apiResponse = response.data
    console.log('分类API数据:', apiResponse)
    
    if (apiResponse.success && apiResponse.data) {
      categories.value = apiResponse.data
      console.log('分类数据加载成功:', categories.value)
      console.log('分类数量:', categories.value.length)
      
      // 检查分类结构
      categories.value.forEach((category, index) => {
        console.log(`分类${index + 1}:`, {
          id: category.categoryId,
          name: category.categoryName,
          hasChildren: !!(category.children && category.children.length > 0),
          childrenCount: category.children ? category.children.length : 0
        })
      })
      
      if (categories.value.length === 0) {
        ElMessage.warning('该店铺暂无分类数据，请先创建分类')
      }
    } else {
      console.error('分类API返回失败:', apiResponse.message)
      ElMessage.error(apiResponse.message || '获取分类数据失败')
      categories.value = []
    }
  } catch (error) {
    console.error('=== 分类加载异常 ===')
    console.error('错误详情:', error)
    console.error('错误类型:', typeof error)
    console.error('错误消息:', error.message)
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
    }
    ElMessage.error('加载分类失败，请稍后重试')
    categories.value = []
  }
}

const loadCategoryAttributes = async (categoryId: number) => {
  // 重置属性数据
  basicAttributes.value = []
  nonBasicAttributes.value = []
  
  try {
    console.log('=== 开始加载分类属性 ===')
    console.log('分类ID:', categoryId)
    
    // 并行加载基础属性和非基础属性
    const [basicResponse, nonBasicResponse] = await Promise.all([
      getCategoryBasicAttributes(categoryId),
      getCategoryNonBasicAttributes(categoryId)
    ])

    console.log('=== API响应 ===')
    console.log('基础属性响应:', JSON.stringify(basicResponse, null, 2))
    console.log('非基础属性响应:', JSON.stringify(nonBasicResponse, null, 2))

    // 处理基础属性
    const basicApiResponse = basicResponse.data
    if (basicApiResponse.success && basicApiResponse.data) {
      console.log('=== 处理基础属性 ===')
      console.log('基础属性原始数据:', basicApiResponse.data)
      
      // 转换数据格式
      basicAttributes.value = basicApiResponse.data.map((attr: any) => ({
        ...attr,
        options: attr.options ? attr.options.map((option: string, index: number) => ({
          optionId: `${attr.attributeId}_${index}`,
          optionValue: option
        })) : []
      }))
      
      // 检查每个属性的数据结构
      basicAttributes.value.forEach((attr, index) => {
        console.log(`基础属性${index + 1}:`, {
          id: attr.attributeId,
          name: attr.attributeName,
          type: attr.attributeType,
          options: attr.options,
          optionsType: typeof attr.options,
          optionsLength: attr.options ? attr.options.length : 'null'
        })
        
        // 检查options的结构
        if (attr.options && Array.isArray(attr.options)) {
          attr.options.forEach((option: any, optIndex: number) => {
            console.log(`  选项${optIndex + 1}:`, option, typeof option)
          })
        }
      })
      
      // 初始化基础属性值
      basicAttributes.value.forEach(attr => {
        if (!formData.basicAttributes[attr.attributeName]) {
          formData.basicAttributes[attr.attributeName] = []
        }
      })
    } else {
      console.warn('加载基础属性失败:', basicApiResponse.message)
    }

    // 处理非基础属性
    const nonBasicApiResponse = nonBasicResponse.data
    if (nonBasicApiResponse.success && nonBasicApiResponse.data) {
      console.log('=== 处理非基础属性 ===')
      console.log('非基础属性原始数据:', nonBasicApiResponse.data)
      
      // 转换数据格式
      nonBasicAttributes.value = nonBasicApiResponse.data.map((attr: any) => ({
        ...attr,
        options: attr.options ? attr.options.map((option: string, index: number) => ({
          optionId: `${attr.attributeId}_${index}`,
          optionValue: option
        })) : []
      }))
      
      // 检查每个属性的数据结构
      nonBasicAttributes.value.forEach((attr, index) => {
        console.log(`非基础属性${index + 1}:`, {
          id: attr.attributeId,
          name: attr.attributeName,
          type: attr.attributeType,
          options: attr.options,
          optionsType: typeof attr.options,
          optionsLength: attr.options ? attr.options.length : 'null'
        })
        
        // 检查options的结构
        if (attr.options && Array.isArray(attr.options)) {
          attr.options.forEach((option: any, optIndex: number) => {
            console.log(`  选项${optIndex + 1}:`, option, typeof option)
          })
        }
      })
      
      // 初始化非基础属性值
      nonBasicAttributes.value.forEach(attr => {
        if (!formData.nonBasicAttributes[attr.attributeName]) {
          // 根据属性类型设置正确的初始值
          switch (attr.attributeType) {
            case 'BOOLEAN':
              formData.nonBasicAttributes[attr.attributeName] = false
              break
            case 'NUMBER':
              formData.nonBasicAttributes[attr.attributeName] = 0
              break
            case 'DATE':
              formData.nonBasicAttributes[attr.attributeName] = null
              break
            default:
              formData.nonBasicAttributes[attr.attributeName] = ''
          }
        }
      })
    } else {
      console.warn('加载非基础属性失败:', nonBasicApiResponse.message)
    }

    // 如果都没有加载到属性，抛出错误
    if (basicAttributes.value.length === 0 && nonBasicAttributes.value.length === 0) {
      throw new Error('该分类暂无配置属性')
    }

    console.log('=== 最终状态 ===')
    console.log('基础属性数量:', basicAttributes.value.length)
    console.log('非基础属性数量:', nonBasicAttributes.value.length)
    console.log('表单基础属性值:', formData.basicAttributes)
    console.log('表单非基础属性值:', formData.nonBasicAttributes)

  } catch (error) {
    console.error('=== 异常错误 ===')
    console.error('加载分类属性失败:', error)
    // 重置数据
    basicAttributes.value = []
    nonBasicAttributes.value = []
    formData.basicAttributes = {}
    formData.nonBasicAttributes = {}
    throw error // 重新抛出错误，让调用方处理
  }
}

const handleCategoryChange = async (categoryId: number) => {
  if (!categoryId) {
    // 清空所有数据
    basicAttributes.value = []
    nonBasicAttributes.value = []
    formData.basicAttributes = {}
    formData.nonBasicAttributes = {}
    skuPreview.value = []
    return
  }

  // 检查是否为叶子节点
  const selectedCategory = findCategoryById(categories.value, categoryId)
  if (!selectedCategory) {
    ElMessage.warning('未找到选中的分类')
    return
  }

  // 检查是否为叶子节点（没有子分类）
  if (selectedCategory.children && selectedCategory.children.length > 0) {
    ElMessage.warning('请选择具体的商品分类（叶子节点）')
    return
  }

  // 显示加载提示
  ElMessage.info('正在加载分类属性...')
  
  try {
    // 清空之前的属性值
    formData.basicAttributes = {}
    formData.nonBasicAttributes = {}
    skuPreview.value = []
    
    // 加载分类属性
    await loadCategoryAttributes(categoryId)
    
    // 检查是否成功加载到属性
    if (basicAttributes.value.length > 0 || nonBasicAttributes.value.length > 0) {
      ElMessage.success(`成功加载分类属性：${basicAttributes.value.length} 个基础属性，${nonBasicAttributes.value.length} 个非基础属性`)
      
      // 自动切换到基础属性标签页
      if (basicAttributes.value.length > 0) {
        activeTab.value = 'basic-attrs'
      }
    } else {
      ElMessage.warning('该分类暂无配置属性，请联系管理员添加')
    }
  } catch (error) {
    console.error('加载分类属性失败:', error)
    ElMessage.error('加载分类属性失败，请稍后重试')
  }
}

// 查找分类的辅助方法
const findCategoryById = (categories: ProductCategory[], categoryId: number): ProductCategory | null => {
  for (const category of categories) {
    if (category.categoryId === categoryId) {
      return category
    }
    if (category.children && category.children.length > 0) {
      const found = findCategoryById(category.children, categoryId)
      if (found) return found
    }
  }
  return null
}

const generateSKUPreview = async () => {
  // 检查是否有基础属性值
  const hasBasicAttrs = Object.keys(formData.basicAttributes).some(
    key => formData.basicAttributes[key] && formData.basicAttributes[key].length > 0
  )
  
  if (!hasBasicAttrs) {
    ElMessage.warning('请先设置基础属性值')
    return
  }

  try {
    const response = await generateSKUCombinations(formData.basicAttributes)
    const apiResponse = response.data
    if (apiResponse.success && apiResponse.data) {
      skuPreview.value = apiResponse.data.map((combination: any) => ({
        skuName: combination.skuName,
        attributeCombination: combination.attributeCombination,
        salePrice: formData.displayPrice,
        stockQuantity: 0,
        status: 2, // 默认下架
        warnStock: 10
      }))
      ElMessage.success(`成功生成 ${skuPreview.value.length} 个SKU组合`)
    } else {
      console.error('SKU生成API返回错误:', apiResponse.message)
      ElMessage.error(apiResponse.message || '生成SKU组合失败')
    }
  } catch (error) {
    console.error('生成SKU组合失败:', error)
    ElMessage.error('生成SKU组合失败')
  }
}

// 批量设置价格
const handleBatchSetPrice = async () => {
  try {
    const { value: price } = await ElMessageBox.prompt(
      '请输入要批量设置的价格',
      '批量设价格',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+(\.\d{1,2})?$/,
        inputErrorMessage: '请输入有效的价格（最多两位小数）',
        inputValue: formData.displayPrice.toString()
      }
    )
    
    const priceValue = parseFloat(price)
    if (priceValue > 0) {
      skuPreview.value.forEach(sku => {
        sku.salePrice = priceValue
      })
      ElMessage.success('批量设置价格成功')
    }
  } catch (error) {
    // 用户取消操作
  }
}

// 批量设置库存
const handleBatchSetStock = async () => {
  try {
    const { value: stock } = await ElMessageBox.prompt(
      '请输入要批量设置的库存数量',
      '批量设库存',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入有效的库存数量（正整数）',
        inputValue: '100'
      }
    )
    
    const stockValue = parseInt(stock)
    if (stockValue >= 0) {
      skuPreview.value.forEach(sku => {
        sku.stockQuantity = stockValue
      })
      ElMessage.success('批量设置库存成功')
    }
  } catch (error) {
    // 用户取消操作
  }
}

// 获取无效的SKU（价格未设置或为0）
const getInvalidSKUs = () => {
  return skuPreview.value.filter(sku => !sku.salePrice || sku.salePrice <= 0)
}

// 验证SKU信息是否完整
const validateSKUs = () => {
  const invalidSKUs = getInvalidSKUs()
  if (invalidSKUs.length > 0) {
    ElMessage.warning(`有 ${invalidSKUs.length} 个SKU未设置价格，请完善后再提交`)
    activeTab.value = 'sku-preview'
    return false
  }
  return true
}

// 基础属性相关方法
const isBasicAttributeRequired = (attr: any) => {
  return attr.isRequired || true // 基础属性通常都是必需的
}

const hasSelectedValues = (attr: any) => {
  const values = formData.basicAttributes[attr.attributeName]
  return values && values.length > 0
}

const getSelectedBasicAttributesCount = () => {
  return Object.keys(formData.basicAttributes).filter(
    key => formData.basicAttributes[key] && formData.basicAttributes[key].length > 0
  ).length
}

const calculateSKUCount = () => {
  let count = 1
  Object.keys(formData.basicAttributes).forEach(key => {
    const values = formData.basicAttributes[key]
    if (values && values.length > 0) {
      count *= values.length
    }
  })
  return count
}

const handleBasicAttributeChange = () => {
  // 基础属性变化时的处理逻辑
  // 可以在这里添加实时预览SKU数量等功能
}

// 获取属性类型标签
const getAttributeTypeLabel = (type: string): string => {
  const typeMap: Record<string, string> = {
    'TEXT': '文本',
    'NUMBER': '数字',
    'DATE': '日期',
    'BOOLEAN': '开关',
    'ENUM': '选择'
  }
  return typeMap[type] || type
}

const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleImageUpload = (options: UploadRequestOptions) => {
  // 这里应该调用实际的图片上传API
  // 暂时使用本地预览
  const file = options.file
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.productImage = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

const handleSaveDraft = async () => {
  // 保存为草稿，不需要完整验证
  await submitForm('draft')
}

const handleSubmit = async () => {
  // 完整提交，需要验证
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 检查基础属性
    const hasBasicAttrs = Object.keys(formData.basicAttributes).some(
      key => formData.basicAttributes[key] && formData.basicAttributes[key].length > 0
    )
    
    if (!hasBasicAttrs) {
      ElMessage.warning('请设置至少一个基础属性值')
      activeTab.value = 'basic-attrs'
      return
    }
    
    // 检查SKU
    if (skuPreview.value.length === 0) {
      ElMessage.warning('请生成SKU组合')
      activeTab.value = 'sku-preview'
      return
    }
    
    // 验证SKU信息完整性
    if (!validateSKUs()) {
      return
    }
    
    await submitForm('on_shelf')
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const submitForm = async (status: string) => {
  submitting.value = true
  
  try {
    // 准备SKU数据，移除不需要的字段
    const skuData = skuPreview.value.map(sku => ({
      skuName: sku.skuName,
      salePrice: sku.salePrice.toString(), // 转换为字符串以匹配BigDecimal
      stockQuantity: sku.stockQuantity,
      attributeCombination: sku.attributeCombination,
      status: sku.status,
      warnStock: sku.warnStock || 10
    }))
    
    // 准备提交数据
    const submitData: SPUCreateData | SPUUpdateData = {
      merchantId: formData.merchantId,
      storeId: formData.storeId,
      categoryId: formData.categoryId,
      spuName: formData.spuName,
      spuDescription: formData.spuDescription,
      productImage: formData.productImage,
      displayPrice: formData.displayPrice.toString(), // 转换为字符串以匹配BigDecimal
      basicAttributes: formData.basicAttributes,
      nonBasicAttributes: formData.nonBasicAttributes,
      brandName: formData.brandName,
      sellingPoint: formData.sellingPoint,
      unit: formData.unit,
      skus: skuData,
      status: status as any
    }
    
    // 添加详细的调试日志
    console.log('=== 提交商品数据 ===')
    console.log('提交模式:', props.mode)
    console.log('商品状态:', status)
    console.log('完整提交数据:', JSON.stringify(submitData, null, 2))
    console.log('基础属性:', submitData.basicAttributes)
    console.log('非基础属性:', submitData.nonBasicAttributes)
    console.log('SKU数据:', submitData.skus)
    
    // 验证必要字段
    if (!submitData.spuName) {
      throw new Error('商品名称不能为空')
    }
    if (!submitData.categoryId) {
      throw new Error('必须选择商品分类')
    }
    if (!submitData.displayPrice || parseFloat(submitData.displayPrice.toString()) <= 0) {
      throw new Error('展示价格必须大于0')
    }
    if (!submitData.merchantId) {
      throw new Error('商家ID不能为空')
    }
    if (!submitData.storeId) {
      throw new Error('店铺ID不能为空')
    }
    if (!submitData.unit) {
      throw new Error('计量单位不能为空')
    }
    if (!submitData.skus || submitData.skus.length === 0) {
      throw new Error('必须至少有一个SKU')
    }
    
    // 验证SKU数据
    for (const sku of submitData.skus) {
      if (!sku.skuName) {
        throw new Error('SKU名称不能为空')
      }
      if (!sku.salePrice || parseFloat(sku.salePrice.toString()) <= 0) {
        throw new Error('SKU价格必须大于0')
      }
      if (sku.stockQuantity < 0) {
        throw new Error('SKU库存不能小于0')
      }
      if (!sku.attributeCombination || Object.keys(sku.attributeCombination).length === 0) {
        throw new Error('SKU必须有属性组合')
      }
    }
    
    let response
    if (props.mode === 'edit' && props.editProduct) {
      response = await updateSPU(props.editProduct.spuId!, submitData as SPUUpdateData)
    } else {
      response = await createSPU(submitData as SPUCreateData)
    }
    
    console.log('=== API响应 ===')
    console.log('响应数据:', response)
    
    const apiResponse = response.data
    console.log('API数据:', apiResponse)
    
    if (apiResponse.success) {
      ElMessage.success(props.mode === 'edit' ? '商品更新成功' : '商品创建成功')
      emit('success')
    } else {
      console.error('API返回错误:', apiResponse.message)
      ElMessage.error(apiResponse.message || '操作失败')
    }
  } catch (error: any) {
    console.error('=== 提交异常 ===')
    console.error('提交失败:', error)
    
    // 检查是否是网络错误
    if (error.response) {
      console.error('响应状态:', error.response.status)
      console.error('响应数据:', error.response.data)
      
      if (error.response.data && error.response.data.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error(`请求失败 (${error.response.status})`)
      }
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    submitting.value = false
  }
}

// 初始化编辑数据
const initEditData = () => {
  if (props.mode === 'edit' && props.editProduct) {
    const product = props.editProduct
    Object.assign(formData, {
      categoryId: product.categoryId,
      spuName: product.spuName,
      spuDescription: product.spuDescription,
      productImage: product.productImage,
      displayPrice: product.displayPrice,
      basicAttributes: product.basicAttributes || {},
      nonBasicAttributes: product.nonBasicAttributes || {},
      brandName: product.brandName,
      sellingPoint: product.sellingPoint,
      unit: product.unit
    })
    
    // 加载分类属性
    if (product.categoryId) {
      loadCategoryAttributes(product.categoryId)
    }
    
    // 设置SKU预览
    if (product.skus && product.skus.length > 0) {
      skuPreview.value = product.skus.map(sku => ({
        skuName: sku.skuName,
        attributeCombination: sku.attributeCombination,
        salePrice: sku.salePrice,
        stockQuantity: sku.stockQuantity,
        status: sku.status,
        warnStock: sku.warnStock
      }))
    }
  }
}

// 监听基础属性变化，自动生成SKU
watch(
  () => formData.basicAttributes,
  (newVal, oldVal) => {
    // 延迟生成，避免频繁调用
    setTimeout(() => {
      const hasValues = Object.values(formData.basicAttributes).some(
        (values: any) => values && values.length > 0
      )
      
      if (hasValues) {
        // 检查是否所有基础属性都有值
        const allAttributesHaveValues = basicAttributes.value.every(attr => {
          const values = formData.basicAttributes[attr.attributeName]
          return values && values.length > 0
        })
        
        if (allAttributesHaveValues) {
          generateSKUPreview()
        } else {
          // 清空SKU预览，等待用户完善所有基础属性
          skuPreview.value = []
        }
      } else {
        skuPreview.value = []
      }
    }, 300) // 减少延迟时间，提高响应性
  },
  { deep: true }
)

// 测试API功能
const testAPIs = async () => {
  console.log('=== 开始测试ProductForm API ===')
  
  try {
    // 测试分类加载
    if (props.storeId) {
      ElMessage.info('测试分类加载API...')
      await loadCategories()
      
      // 如果有分类，测试属性加载
      if (categories.value.length > 0) {
        const firstCategory = categories.value[0]
        // 找到叶子节点
        let leafCategory = firstCategory
        while (leafCategory.children && leafCategory.children.length > 0) {
          leafCategory = leafCategory.children[0]
        }
        
        if (leafCategory.categoryId) {
          ElMessage.info(`测试属性加载API (分类: ${leafCategory.categoryName})...`)
          try {
            await loadCategoryAttributes(leafCategory.categoryId)
            ElMessage.success('属性加载测试成功')
          } catch (error) {
            console.error('属性加载测试失败:', error)
            ElMessage.warning('属性加载测试失败，可能该分类暂无属性')
          }
        }
      }
      
      ElMessage.success('API测试完成，请查看控制台日志')
    } else {
      ElMessage.warning('缺少店铺ID，无法测试API')
    }
  } catch (error) {
    console.error('API测试失败:', error)
    ElMessage.error('API测试失败')
  }
}

// 组件挂载
onMounted(() => {
  loadCategories()
  initEditData()
  
  // 开发环境下的测试日志
  if (import.meta.env.DEV) {
    console.log('ProductForm组件已挂载，支持以下功能：')
    console.log('1. 选择分类后自动加载属性')
    console.log('2. 基础属性自动生成SKU')
    console.log('3. 批量设置价格和库存')
    console.log('4. 实时验证SKU信息')
  }
})
</script>

<style scoped>
.product-form {
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
}

.form-section {
  padding: 24px;
  background: #fff;
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.section-header h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.section-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}

.header-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.sku-summary {
  margin-bottom: 20px;
}

.sku-preview-table {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.sku-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.sku-icon {
  color: #409eff;
}

.attribute-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.price-input, .stock-input {
  position: relative;
}

.error-input :deep(.el-input__wrapper) {
  border-color: #f56c6c !important;
  box-shadow: 0 0 0 1px #f56c6c inset !important;
}

.warning-input :deep(.el-input__wrapper) {
  border-color: #e6a23c !important;
  box-shadow: 0 0 0 1px #e6a23c inset !important;
}

.error-tip {
  position: absolute;
  top: 100%;
  left: 0;
  font-size: 12px;
  color: #f56c6c;
  margin-top: 4px;
  z-index: 1;
}

.warning-tip {
  position: absolute;
  top: 100%;
  left: 0;
  font-size: 12px;
  color: #e6a23c;
  margin-top: 4px;
  z-index: 1;
}

.sku-validation {
  margin-top: 20px;
}

.empty-attrs, .empty-sku {
  text-align: center;
  padding: 60px 20px;
  background: #fafafa;
  border-radius: 8px;
  border: 2px dashed #d9d9d9;
}

.attrs-container {
  display: grid;
  gap: 24px;
}

.attrs-tip {
  margin-bottom: 24px;
}

.attr-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.attr-item:hover {
  border-color: #409eff;
  background: #fff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.attr-item.attr-error {
  border-color: #f56c6c;
  background: #fef0f0;
}

.attr-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.attr-name {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.attr-required {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.attr-count {
  color: #409eff;
  font-size: 12px;
  background: #ecf5ff;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.attr-type {
  color: #909399;
  font-size: 12px;
  background: #f4f4f5;
  padding: 4px 8px;
  border-radius: 4px;
  margin-left: auto;
  font-weight: 500;
}

.attr-values {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.attr-checkbox {
  margin-right: 0 !important;
  margin-bottom: 8px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #fff;
  transition: all 0.3s ease;
}

.attr-checkbox:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.attr-checkbox.is-checked {
  border-color: #409eff;
  background: #ecf5ff;
}

.attr-error-tip {
  margin-top: 12px;
  color: #f56c6c;
  font-size: 13px;
  padding: 8px 12px;
  background: #fef0f0;
  border-radius: 4px;
  border-left: 3px solid #f56c6c;
}

.attrs-summary {
  margin-top: 24px;
}

.attr-input {
  width: 100%;
}

.image-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.image-uploader:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.image-uploader-icon {
  font-size: 32px;
  color: #8c939d;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 24px;
  border-top: 1px solid #e4e7ed;
  background: #fafafa;
  margin-top: 24px;
}

.category-tip {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 13px;
  padding: 8px 12px;
  background: #f0f9ff;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

/* 表单项样式优化 */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
  line-height: 1.5;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:hover) {
  border-color: #c0c4cc;
}

:deep(.el-textarea__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-cascader) {
  width: 100%;
}

:deep(.el-tabs__header) {
  margin-bottom: 0;
}

:deep(.el-tabs__content) {
  padding: 0;
}

:deep(.el-tab-pane) {
  background: #fff;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #303133;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-checkbox) {
  margin-right: 0;
}

:deep(.el-checkbox__label) {
  font-size: 14px;
  color: #303133;
}

:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

:deep(.el-alert) {
  border-radius: 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-form {
    max-width: 100%;
  }
  
  .form-section {
    padding: 16px;
  }
  
  .section-header h3 {
    font-size: 16px;
  }
  
  /* 小屏幕下所有表单项都占满宽度 */
  :deep(.el-col) {
    width: 100% !important;
    flex: 0 0 100% !important;
    max-width: 100% !important;
  }
  
  .attr-values {
    flex-direction: column;
    gap: 8px;
  }
  
  .attr-checkbox {
    width: 100%;
    text-align: center;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .header-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .attribute-tags {
    flex-direction: column;
    gap: 4px;
  }
  
  .image-uploader {
    width: 100px;
    height: 100px;
  }
}

@media (max-width: 1024px) {
  /* 中等屏幕下的调整 */
  .product-form {
    max-width: 100%;
    margin: 0;
  }
  
  .form-section {
    padding: 20px;
  }
}

/* 调试信息样式 */
.debug-info {
  background: #f0f0f0;
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 6px;
  border-left: 4px solid #909399;
}

.debug-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 14px;
}

.debug-info p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #606266;
}

.debug-info div {
  margin-bottom: 8px;
  font-size: 12px;
  color: #909399;
}
</style> 