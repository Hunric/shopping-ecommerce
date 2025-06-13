<template>
  <div class="attribute-test">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>分类属性测试</span>
        </div>
      </template>
      
      <el-form :model="testForm" label-width="120px">
        <el-form-item label="选择分类">
          <el-cascader
            v-model="testForm.categoryId"
            :options="categoryOptions"
            :props="{
              value: 'categoryId',
              label: 'categoryName',
              children: 'children',
              checkStrictly: true,
              emitPath: false
            }"
            placeholder="请选择分类"
            @change="loadAttributes"
          />
        </el-form-item>
        
        <el-form-item label="属性名称">
          <el-input v-model="testForm.attributeName" placeholder="请输入属性名称" />
        </el-form-item>
        
        <el-form-item label="属性类型">
          <el-select v-model="testForm.attributeType" placeholder="请选择属性类型">
            <el-option label="文本" value="TEXT" />
            <el-option label="数字" value="NUMBER" />
            <el-option label="日期" value="DATE" />
            <el-option label="布尔" value="BOOLEAN" />
            <el-option label="枚举" value="ENUM" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="是否基础属性">
          <el-switch v-model="testForm.isBasicAttribute" />
        </el-form-item>
        
        <el-form-item label="是否必填">
          <el-switch v-model="testForm.isRequired" />
        </el-form-item>
        
        <el-form-item label="枚举选项" v-if="testForm.attributeType === 'ENUM'">
          <el-input
            v-model="testForm.optionsText"
            type="textarea"
            placeholder="请输入选项，每行一个"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="createAttribute">创建属性</el-button>
          <el-button @click="loadAttributes">刷新属性</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card v-if="attributes.length > 0">
      <template #header>
        <div class="card-header">
          <span>当前分类属性列表</span>
        </div>
      </template>
      
      <el-table :data="attributes" border>
        <el-table-column prop="attributeId" label="ID" width="80" />
        <el-table-column prop="attributeName" label="属性名称" />
        <el-table-column prop="attributeType" label="类型" />
        <el-table-column prop="isBasicAttribute" label="基础属性">
          <template #default="{ row }">
            <el-tag :type="row.isBasicAttribute ? 'success' : 'info'">
              {{ row.isBasicAttribute ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRequired" label="必填">
          <template #default="{ row }">
            <el-tag :type="row.isRequired ? 'warning' : 'info'">
              {{ row.isRequired ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="options" label="选项">
          <template #default="{ row }">
            <span v-if="row.options && row.options.length > 0">
              {{ row.options.join(', ') }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="deleteAttribute(row.attributeId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getCategoryTree } from '@/api/merchant/category'
import { 
  getCategoryAttributes, 
  createAttribute as createAttributeApi, 
  deleteAttribute as deleteAttributeApi,
  type CategoryAttribute,
  type CreateAttributeRequest
} from '@/api/merchant/categoryAttribute'

// 测试表单
const testForm = reactive({
  categoryId: null,
  attributeName: '',
  attributeType: 'TEXT',
  isBasicAttribute: false,
  isRequired: false,
  optionsText: ''
})

// 分类选项
const categoryOptions = ref<any[]>([])

// 属性列表
const attributes = ref<CategoryAttribute[]>([])

// 加载分类树
const loadCategoryTree = async () => {
  try {
    const response = await getCategoryTree(1) // 使用店铺ID 1
    if (response.success) {
      categoryOptions.value = response.data
    } else {
      ElMessage.error(response.message || '加载分类失败')
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error('加载分类失败，请稍后重试')
  }
}

// 加载属性
const loadAttributes = async () => {
  if (!testForm.categoryId) {
    attributes.value = []
    return
  }
  
  try {
    const response = await getCategoryAttributes(testForm.categoryId)
    console.log('属性响应:', response)
    if (response.success) {
      attributes.value = response.data || []
      ElMessage.success(`加载了 ${attributes.value.length} 个属性`)
    } else {
      ElMessage.error(response.message || '加载属性失败')
    }
  } catch (error) {
    console.error('加载属性失败:', error)
    ElMessage.error('加载属性失败，请稍后重试')
  }
}

// 创建属性
const createAttribute = async () => {
  if (!testForm.categoryId || !testForm.attributeName) {
    ElMessage.warning('请选择分类并输入属性名称')
    return
  }
  
  try {
    const options = testForm.attributeType === 'ENUM' && testForm.optionsText
      ? testForm.optionsText.split('\n').filter(opt => opt.trim())
      : []
    
    const data: CreateAttributeRequest = {
      storeId: 1,
      categoryId: testForm.categoryId,
      attributeName: testForm.attributeName,
      attributeType: testForm.attributeType as any,
      isBasicAttribute: testForm.isBasicAttribute,
      isRequired: testForm.isRequired,
      options: options.length > 0 ? options : undefined
    }
    
    const response = await createAttributeApi(data)
    if (response.success) {
      ElMessage.success('创建属性成功')
      // 重置表单
      testForm.attributeName = ''
      testForm.optionsText = ''
      // 重新加载属性
      loadAttributes()
    } else {
      ElMessage.error(response.message || '创建属性失败')
    }
  } catch (error) {
    console.error('创建属性失败:', error)
    ElMessage.error('创建属性失败，请稍后重试')
  }
}

// 删除属性
const deleteAttribute = async (attributeId: number) => {
  try {
    const response = await deleteAttributeApi(attributeId)
    if (response.success) {
      ElMessage.success('删除属性成功')
      loadAttributes()
    } else {
      ElMessage.error(response.message || '删除属性失败')
    }
  } catch (error) {
    console.error('删除属性失败:', error)
    ElMessage.error('删除属性失败，请稍后重试')
  }
}

// 初始化
loadCategoryTree()
</script>

<style scoped>
.mb-4 {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 