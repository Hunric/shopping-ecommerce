<template>
  <div class="product-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>{{ storeName }} - 商品管理</h2>
        <p>管理店铺的所有商品信息</p>
      </div>
      <div class="header-actions">
        <el-button @click="$emit('back')">
          <el-icon><ArrowLeft /></el-icon>
          返回店铺列表
        </el-button>
        <el-button type="primary" @click="showAddProduct = true">
          <el-icon><Plus /></el-icon>
          添加商品
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商品名称"
        style="width: 300px"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select
        v-model="categoryFilter"
        placeholder="商品分类"
        style="width: 150px; margin-left: 16px"
        clearable
      >
        <el-option label="服装鞋帽" value="clothing" />
        <el-option label="数码电器" value="electronics" />
        <el-option label="食品饮料" value="food" />
        <el-option label="美妆护肤" value="beauty" />
        <el-option label="家居用品" value="home" />
      </el-select>
      
      <el-select
        v-model="statusFilter"
        placeholder="商品状态"
        style="width: 150px; margin-left: 16px"
        clearable
      >
        <el-option label="在售" value="on_shelf" />
        <el-option label="下架" value="off_shelf" />
        <el-option label="草稿" value="draft" />
      </el-select>
      
      <el-button type="primary" @click="refreshProductList" style="margin-left: 16px">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <!-- 商品列表 -->
    <div class="product-list" v-loading="loading">
      <div v-if="filteredProducts.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无商品数据">
          <el-button type="primary" @click="showAddProduct = true">
            <el-icon><Plus /></el-icon>
            添加第一个商品
          </el-button>
        </el-empty>
      </div>
      
      <div class="product-table" v-else>
        <el-table :data="filteredProducts" style="width: 100%">
          <el-table-column width="80">
            <template #default="{ row }">
              <div class="product-image">
                <img
                  v-if="row.mainImage"
                  :src="row.mainImage"
                  :alt="row.productName"
                  @error="handleImageError"
                />
                <el-icon v-else class="default-image"><Picture /></el-icon>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="productName" label="商品名称" min-width="200">
            <template #default="{ row }">
              <div class="product-info">
                <h4>{{ row.productName }}</h4>
                <p class="product-desc">{{ row.description || '暂无描述' }}</p>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="category" label="分类" width="120">
            <template #default="{ row }">
              <el-tag size="small">{{ getCategoryText(row.category) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="stock" label="库存" width="80">
            <template #default="{ row }">
              <span :class="{ 'low-stock': row.stock < 10 }">{{ row.stock }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="创建时间" width="120">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editProduct(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                size="small"
                :type="row.status === 'on_shelf' ? 'warning' : 'success'"
                @click="toggleProductStatus(row)"
              >
                {{ row.status === 'on_shelf' ? '下架' : '上架' }}
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="deleteProduct(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 添加商品对话框 -->
    <el-dialog
      v-model="showAddProduct"
      title="添加商品"
      width="800px"
      @close="resetProductForm"
    >
      <el-form
        ref="productFormRef"
        :model="productForm"
        :rules="productRules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="productForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择分类">
            <el-option label="服装鞋帽" value="clothing" />
            <el-option label="数码电器" value="electronics" />
            <el-option label="食品饮料" value="food" />
            <el-option label="美妆护肤" value="beauty" />
            <el-option label="家居用品" value="home" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="商品价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="库存数量" prop="stock">
          <el-input-number
            v-model="productForm.stock"
            :min="0"
            placeholder="请输入库存数量"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="商品图片" prop="mainImage">
          <el-input v-model="productForm.mainImage" placeholder="请输入图片URL" />
        </el-form-item>
        
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddProduct = false">取消</el-button>
        <el-button type="primary" @click="submitProduct" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, Plus, Search, Refresh, Edit, Picture
} from '@element-plus/icons-vue'

// 定义Props
interface Props {
  storeId: number
  storeName: string
}

const props = defineProps<Props>()

// 定义事件
const emit = defineEmits(['back'])

// 定义商品类型
interface Product {
  productId: number
  productName: string
  category: string
  price: number
  stock: number
  status: string
  mainImage?: string
  description?: string
  createTime: string
}

// 响应式数据
const loading = ref(false)
const submitting = ref(false)
const products = ref<Product[]>([])
const searchKeyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')
const showAddProduct = ref(false)

// 商品表单
const productFormRef = ref()
const productForm = ref({
  productName: '',
  category: '',
  price: 0,
  stock: 0,
  mainImage: '',
  description: ''
})

// 表单验证规则
const productRules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' }
  ]
}

// 计算属性
const filteredProducts = computed(() => {
  let result = products.value

  // 按关键词搜索
  if (searchKeyword.value) {
    result = result.filter(product =>
      product.productName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 按分类筛选
  if (categoryFilter.value) {
    result = result.filter(product => product.category === categoryFilter.value)
  }

  // 按状态筛选
  if (statusFilter.value) {
    result = result.filter(product => product.status === statusFilter.value)
  }

  return result
})

// 方法
const loadProducts = async () => {
  loading.value = true
  try {
    // 模拟数据
    const mockProducts: Product[] = [
      {
        productId: 1,
        productName: '时尚女装连衣裙',
        category: 'clothing',
        price: 299.00,
        stock: 50,
        status: 'on_shelf',
        mainImage: 'https://via.placeholder.com/100x100/409EFF/FFFFFF?text=Dress',
        description: '优质面料，时尚设计，适合多种场合',
        createTime: '2024-12-01T10:00:00'
      },
      {
        productId: 2,
        productName: '智能手机',
        category: 'electronics',
        price: 2999.00,
        stock: 20,
        status: 'on_shelf',
        mainImage: 'https://via.placeholder.com/100x100/67C23A/FFFFFF?text=Phone',
        description: '高性能处理器，超清摄像头',
        createTime: '2024-12-02T10:00:00'
      },
      {
        productId: 3,
        productName: '有机蜂蜜',
        category: 'food',
        price: 89.00,
        stock: 5,
        status: 'on_shelf',
        mainImage: 'https://via.placeholder.com/100x100/E6A23C/FFFFFF?text=Honey',
        description: '纯天然有机蜂蜜，营养丰富',
        createTime: '2024-12-03T10:00:00'
      }
    ]
    products.value = mockProducts
  } catch (error) {
    console.error('加载商品列表失败:', error)
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const refreshProductList = () => {
  loadProducts()
}

const editProduct = (product: Product) => {
  ElMessage.info(`编辑商品: ${product.productName}`)
  // TODO: 实现编辑商品功能
}

const toggleProductStatus = async (product: Product) => {
  const newStatus = product.status === 'on_shelf' ? 'off_shelf' : 'on_shelf'
  const action = newStatus === 'on_shelf' ? '上架' : '下架'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}商品"${product.productName}"吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    product.status = newStatus
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const deleteProduct = async (product: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.productName}"吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    const index = products.value.findIndex(p => p.productId === product.productId)
    if (index > -1) {
      products.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitProduct = async () => {
  if (!productFormRef.value) return

  try {
    await productFormRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    // 模拟API调用
    const newProduct: Product = {
      productId: Date.now(),
      productName: productForm.value.productName,
      category: productForm.value.category,
      price: productForm.value.price,
      stock: productForm.value.stock,
      status: 'on_shelf',
      mainImage: productForm.value.mainImage,
      description: productForm.value.description,
      createTime: new Date().toISOString()
    }
    
    products.value.unshift(newProduct)
    ElMessage.success('添加商品成功')
    showAddProduct.value = false
    resetProductForm()
  } catch (error) {
    console.error('添加商品失败:', error)
    ElMessage.error('添加商品失败')
  } finally {
    submitting.value = false
  }
}

const resetProductForm = () => {
  productForm.value = {
    productName: '',
    category: '',
    price: 0,
    stock: 0,
    mainImage: '',
    description: ''
  }
  if (productFormRef.value) {
    productFormRef.value.clearValidate()
  }
}

const getCategoryText = (category: string) => {
  const categoryMap: Record<string, string> = {
    clothing: '服装鞋帽',
    electronics: '数码电器',
    food: '食品饮料',
    beauty: '美妆护肤',
    home: '家居用品'
  }
  return categoryMap[category] || category
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'on_shelf':
      return 'success'
    case 'off_shelf':
      return 'warning'
    case 'draft':
      return 'info'
    default:
      return 'info'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'on_shelf':
      return '在售'
    case 'off_shelf':
      return '下架'
    case 'draft':
      return '草稿'
    default:
      return '未知'
  }
}

const formatDate = (dateString: string) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 组件挂载时加载数据
onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-management {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.header-left h2 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.product-list {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.product-table {
  background: white;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-image {
  font-size: 24px;
  color: #909399;
}

.product-info h4 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
}

.product-desc {
  margin: 0;
  color: #666;
  font-size: 12px;
  line-height: 1.4;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.low-stock {
  color: #f56c6c;
  font-weight: 600;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .search-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}
</style> 