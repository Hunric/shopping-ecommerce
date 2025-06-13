<template>
  <div class="category-attribute-test">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>分类属性管理功能测试</h2>
          <p>测试叶子分类的属性管理功能</p>
        </div>
      </template>
      
      <div class="test-content">
        <!-- 测试配置 -->
        <el-card class="test-section">
          <template #header>
            <h3>测试配置</h3>
          </template>
          
          <el-form :model="testConfig" label-width="120px">
            <el-row :gutter="16">
              <el-col :span="8">
                <el-form-item label="店铺ID">
                  <el-input-number v-model="testConfig.storeId" :min="1" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="分类ID">
                  <el-input-number v-model="testConfig.categoryId" :min="1" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="属性ID">
                  <el-input-number v-model="testConfig.attributeId" :min="1" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>

        <!-- API测试按钮 -->
        <el-card class="test-section">
          <template #header>
            <h3>API测试</h3>
          </template>
          
          <div class="test-buttons">
            <el-button type="primary" @click="testGetAttributeTypes">
              获取属性类型列表
            </el-button>
            
            <el-button type="primary" @click="testCanManageAttributes">
              检查是否可管理属性
            </el-button>
            
            <el-button type="primary" @click="testGetCategoryAttributes">
              获取分类属性列表
            </el-button>
            
            <el-button type="success" @click="testCreateAttribute">
              创建属性
            </el-button>
            
            <el-button type="warning" @click="testUpdateAttribute">
              更新属性
            </el-button>
            
            <el-button type="info" @click="testGetAttributeById">
              获取属性详情
            </el-button>
            
            <el-button type="success" @click="testBatchCreateAttributes">
              批量创建属性
            </el-button>
            
            <el-button type="danger" @click="testDeleteAttribute">
              删除属性
            </el-button>
          </div>
        </el-card>

        <!-- 测试结果 -->
        <el-card class="test-section">
          <template #header>
            <div class="result-header">
              <h3>测试结果</h3>
              <el-button @click="clearResults" size="small">清空结果</el-button>
            </div>
          </template>
          
          <div class="test-results">
            <div
              v-for="(result, index) in testResults"
              :key="index"
              class="test-result-item"
              :class="{ 'success': result.success, 'error': !result.success }"
            >
              <div class="result-header-info">
                <span class="result-title">{{ result.title }}</span>
                <span class="result-time">{{ result.time }}</span>
                <el-tag :type="result.success ? 'success' : 'danger'" size="small">
                  {{ result.success ? '成功' : '失败' }}
                </el-tag>
              </div>
              
              <div class="result-content">
                <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
              </div>
            </div>
            
            <el-empty v-if="testResults.length === 0" description="暂无测试结果" />
          </div>
        </el-card>

        <!-- 快速创建测试数据 -->
        <el-card class="test-section">
          <template #header>
            <h3>快速创建测试数据</h3>
          </template>
          
          <div class="quick-actions">
            <el-button type="primary" @click="createTestCategory">
              创建测试分类
            </el-button>
            
            <el-button type="success" @click="createTestAttributes">
              创建测试属性
            </el-button>
            
            <el-button type="warning" @click="createCompleteTestData">
              创建完整测试数据
            </el-button>
          </div>
          
          <div class="quick-info">
            <el-alert
              title="说明"
              description="快速创建功能会自动创建测试用的分类和属性数据，方便测试各种功能"
              type="info"
              :closable="false"
            />
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getCategoryAttributes,
  getAttributeById,
  createAttribute,
  updateAttribute,
  deleteAttribute,
  batchCreateAttributes,
  canManageAttributes,
  getAttributeTypes,
  type CategoryAttribute,
  type CreateAttributeRequest,
  type UpdateAttributeRequest
} from '@/api/merchant/categoryAttribute'
import {
  createCategory,
  type CreateCategoryRequest
} from '@/api/merchant/category'

// 测试配置
const testConfig = reactive({
  storeId: 1,
  categoryId: 1,
  attributeId: 1
})

// 测试结果
interface TestResult {
  title: string
  time: string
  success: boolean
  data: any
}

const testResults = ref<TestResult[]>([])

// 添加测试结果
const addTestResult = (title: string, success: boolean, data: any) => {
  testResults.value.unshift({
    title,
    time: new Date().toLocaleTimeString(),
    success,
    data
  })
}

// 清空结果
const clearResults = () => {
  testResults.value = []
}

// 测试获取属性类型列表
const testGetAttributeTypes = async () => {
  try {
    const response = await getAttributeTypes()
    addTestResult('获取属性类型列表', response.success, response)
    if (response.success) {
      ElMessage.success('获取属性类型列表成功')
    } else {
      ElMessage.error(response.message || '获取失败')
    }
  } catch (error) {
    addTestResult('获取属性类型列表', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试检查是否可管理属性
const testCanManageAttributes = async () => {
  try {
    const response = await canManageAttributes(testConfig.categoryId)
    addTestResult('检查是否可管理属性', response.success, response)
    if (response.success) {
      ElMessage.success(`分类${testConfig.categoryId}${response.data ? '可以' : '不能'}管理属性`)
    } else {
      ElMessage.error(response.message || '检查失败')
    }
  } catch (error) {
    addTestResult('检查是否可管理属性', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试获取分类属性列表
const testGetCategoryAttributes = async () => {
  try {
    const response = await getCategoryAttributes(testConfig.categoryId)
    addTestResult('获取分类属性列表', response.success, response)
    if (response.success) {
      ElMessage.success(`获取到${response.data?.length || 0}个属性`)
    } else {
      ElMessage.error(response.message || '获取失败')
    }
  } catch (error) {
    addTestResult('获取分类属性列表', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试创建属性
const testCreateAttribute = async () => {
  const createData: CreateAttributeRequest = {
    storeId: testConfig.storeId,
    categoryId: testConfig.categoryId,
    attributeName: `测试属性_${Date.now()}`,
    attributeType: 'TEXT',
    isBasicAttribute: false,
    isRequired: true
  }
  
  try {
    const response = await createAttribute(createData)
    addTestResult('创建属性', response.success, response)
    if (response.success) {
      ElMessage.success('创建属性成功')
      // 更新测试配置中的属性ID
      if (response.data?.attributeId) {
        testConfig.attributeId = response.data.attributeId
      }
    } else {
      ElMessage.error(response.message || '创建失败')
    }
  } catch (error) {
    addTestResult('创建属性', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试更新属性
const testUpdateAttribute = async () => {
  const updateData: UpdateAttributeRequest = {
    attributeName: `更新属性_${Date.now()}`,
    attributeType: 'NUMBER',
    isBasicAttribute: true,
    isRequired: true
  }
  
  try {
    const response = await updateAttribute(testConfig.attributeId, updateData)
    addTestResult('更新属性', response.success, response)
    if (response.success) {
      ElMessage.success('更新属性成功')
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    addTestResult('更新属性', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试获取属性详情
const testGetAttributeById = async () => {
  try {
    const response = await getAttributeById(testConfig.attributeId)
    addTestResult('获取属性详情', response.success, response)
    if (response.success) {
      ElMessage.success('获取属性详情成功')
    } else {
      ElMessage.error(response.message || '获取失败')
    }
  } catch (error) {
    addTestResult('获取属性详情', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试批量创建属性
const testBatchCreateAttributes = async () => {
  const batchData = {
    storeId: testConfig.storeId,
    categoryId: testConfig.categoryId,
    attributes: [
      {
        storeId: testConfig.storeId,
        categoryId: testConfig.categoryId,
        attributeName: `批量属性1_${Date.now()}`,
        attributeType: 'TEXT' as const,
        isBasicAttribute: true,
        isRequired: true
      },
      {
        storeId: testConfig.storeId,
        categoryId: testConfig.categoryId,
        attributeName: `批量属性2_${Date.now()}`,
        attributeType: 'ENUM' as const,
        isBasicAttribute: false,
        isRequired: false,
        options: ['选项1', '选项2', '选项3']
      },
      {
        storeId: testConfig.storeId,
        categoryId: testConfig.categoryId,
        attributeName: `批量属性3_${Date.now()}`,
        attributeType: 'BOOLEAN' as const,
        isBasicAttribute: false,
        isRequired: true
      }
    ]
  }
  
  try {
    const response = await batchCreateAttributes(batchData)
    addTestResult('批量创建属性', response.success, response)
    if (response.success) {
      ElMessage.success(`批量创建${response.data?.length || 0}个属性成功`)
    } else {
      ElMessage.error(response.message || '批量创建失败')
    }
  } catch (error) {
    addTestResult('批量创建属性', false, error)
    ElMessage.error('请求失败')
  }
}

// 测试删除属性
const testDeleteAttribute = async () => {
  try {
    const response = await deleteAttribute(testConfig.attributeId)
    addTestResult('删除属性', response.success, response)
    if (response.success) {
      ElMessage.success('删除属性成功')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    addTestResult('删除属性', false, error)
    ElMessage.error('请求失败')
  }
}

// 创建测试分类
const createTestCategory = async () => {
  const categoryData: CreateCategoryRequest = {
    storeId: testConfig.storeId,
    categoryName: `测试叶子分类_${Date.now()}`,
    description: '用于测试属性管理的叶子分类',
    sortOrder: 1,
    isVisible: true
  }
  
  try {
    const response = await createCategory(categoryData)
    addTestResult('创建测试分类', response.success, response)
    if (response.success) {
      ElMessage.success('创建测试分类成功')
      // 更新测试配置中的分类ID
      if (response.data?.categoryId) {
        testConfig.categoryId = response.data.categoryId
      }
    } else {
      ElMessage.error(response.message || '创建分类失败')
    }
  } catch (error) {
    addTestResult('创建测试分类', false, error)
    ElMessage.error('请求失败')
  }
}

// 创建测试属性
const createTestAttributes = async () => {
  const attributes = [
    {
      attributeName: '商品名称',
      attributeType: 'TEXT' as const,
      isBasicAttribute: true,
      isRequired: true
    },
    {
      attributeName: '商品价格',
      attributeType: 'NUMBER' as const,
      isBasicAttribute: true,
      isRequired: true
    },
    {
      attributeName: '商品颜色',
      attributeType: 'ENUM' as const,
      isBasicAttribute: false,
      isRequired: false,
      options: ['红色', '蓝色', '绿色', '黑色', '白色']
    },
    {
      attributeName: '是否包邮',
      attributeType: 'BOOLEAN' as const,
      isBasicAttribute: false,
      isRequired: false
    },
    {
      attributeName: '生产日期',
      attributeType: 'DATE' as const,
      isBasicAttribute: false,
      isRequired: false
    }
  ]
  
  for (const attr of attributes) {
    try {
      const createData: CreateAttributeRequest = {
        storeId: testConfig.storeId,
        categoryId: testConfig.categoryId,
        ...attr
      }
      
      const response = await createAttribute(createData)
      addTestResult(`创建属性: ${attr.attributeName}`, response.success, response)
      
      if (!response.success) {
        ElMessage.error(`创建属性"${attr.attributeName}"失败: ${response.message}`)
      }
    } catch (error) {
      addTestResult(`创建属性: ${attr.attributeName}`, false, error)
      ElMessage.error(`创建属性"${attr.attributeName}"请求失败`)
    }
  }
  
  ElMessage.success('测试属性创建完成')
}

// 创建完整测试数据
const createCompleteTestData = async () => {
  ElMessage.info('开始创建完整测试数据...')
  
  // 先创建测试分类
  await createTestCategory()
  
  // 等待一下确保分类创建成功
  setTimeout(async () => {
    // 再创建测试属性
    await createTestAttributes()
    ElMessage.success('完整测试数据创建完成')
  }, 1000)
}
</script>

<style scoped>
.category-attribute-test {
  padding: 20px;
}

.card-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.card-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.test-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.test-section {
  width: 100%;
}

.test-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-results {
  max-height: 500px;
  overflow-y: auto;
}

.test-result-item {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 12px;
  overflow: hidden;
}

.test-result-item.success {
  border-color: #67c23a;
}

.test-result-item.error {
  border-color: #f56c6c;
}

.result-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.result-title {
  font-weight: 500;
  color: #303133;
}

.result-time {
  font-size: 12px;
  color: #909399;
}

.result-content {
  padding: 16px;
  background-color: #fafafa;
}

.result-content pre {
  margin: 0;
  font-size: 12px;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-all;
}

.quick-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.quick-info {
  margin-top: 16px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 