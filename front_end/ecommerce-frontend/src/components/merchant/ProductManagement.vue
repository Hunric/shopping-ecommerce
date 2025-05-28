<template>
  <div class="product-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>商品管理</h2>
        <p>{{ storeName }} - 管理您的商品信息</p>
      </div>
      <div class="header-right">
        <el-button @click="$emit('back')">
          <el-icon><ArrowLeft /></el-icon>
          返回店铺列表
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total-icon">
          <el-icon><Goods /></el-icon>
        </div>
        <div class="stat-info">
          <h3>{{ stats.totalCount }}</h3>
          <p>商品总数</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon on-shelf-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <h3>{{ stats.onShelfCount }}</h3>
          <p>已上架</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon off-shelf-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <h3>{{ stats.offShelfCount }}</h3>
          <p>已下架</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon low-stock-icon">
          <el-icon><WarnTriangleFilled /></el-icon>
        </div>
        <div class="stat-info">
          <h3>{{ stats.lowStockCount }}</h3>
          <p>库存不足</p>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-left">
        <el-button type="primary" @click="handleCreateProduct">
          <el-icon><Plus /></el-icon>
          新建商品
        </el-button>
        <el-button 
          v-if="selectedProducts.length > 0" 
          type="success" 
          @click="handleBatchPublish"
        >
          <el-icon><Top /></el-icon>
          批量上架
        </el-button>
        <el-button 
          v-if="selectedProducts.length > 0" 
          type="warning" 
          @click="handleBatchUnpublish"
        >
          <el-icon><Bottom /></el-icon>
          批量下架
        </el-button>
        <el-button 
          v-if="selectedProducts.length > 0" 
          type="danger" 
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
      
      <div class="action-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品名称"
          style="width: 250px"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="statusFilter"
          placeholder="商品状态"
          style="width: 150px; margin-left: 12px"
          clearable
          @change="handleStatusFilter"
        >
          <el-option
            v-for="option in statusOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        
        <el-button @click="refreshProductList" style="margin-left: 12px">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list-container">
      <el-table
        v-loading="loading"
        :data="productList"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
        :table-layout="'fixed'"
        :header-cell-style="{ background: '#fafbfc', color: '#303133', fontWeight: '600' }"
        :row-style="{ height: '80px' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        
        <el-table-column label="商品信息" min-width="300" width="350">
          <template #default="{ row }">
            <div class="product-info">
              <div class="product-image">
                <img
                  v-if="row.productImage"
                  :src="row.productImage"
                  :alt="row.spuName"
                  @error="handleImageError"
                />
                <el-icon v-else class="default-image"><Picture /></el-icon>
              </div>
              <div class="product-details">
                <h4 class="product-name" :title="row.spuName">{{ row.spuName }}</h4>
                <p class="product-description" :title="row.spuDescription">{{ row.spuDescription || '暂无描述' }}</p>
                <div class="product-meta">
                  <el-tag v-if="row.brandName" size="small" type="info" class="brand-tag">{{ row.brandName }}</el-tag>
                  <el-tag size="small" class="unit-tag">{{ row.unit }}</el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="价格" width="120" align="center">
          <template #default="{ row }">
            <div class="price-cell">
              <span class="price">¥{{ row.displayPrice.toFixed(2) }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="SKU数量" width="100" align="center">
          <template #default="{ row }">
            <div class="count-cell">
              <span class="sku-count">{{ getSkuCountDisplay(row) }}</span>
              <span class="count-label">个</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="库存" width="100" align="center">
          <template #default="{ row }">
            <div class="count-cell">
              <span class="stock-count" :class="{ 'low-stock': getStockCountDisplay(row) !== '-' && parseInt(getStockCountDisplay(row)) < 10 }">
                {{ getStockCountDisplay(row) }}
              </span>
              <span class="count-label">件</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <div class="status-cell">
              <el-tag :type="getSPUStatusType(row.status)" size="default" class="status-tag">
                {{ getSPUStatusLabel(row.status) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" width="120" align="center">
          <template #default="{ row }">
            <div class="time-cell">
              <div class="create-time">{{ formatDate(row.createTime) }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-cell">
              <el-button 
                type="primary" 
                size="small" 
                link 
                @click="handleEditProduct(row)"
                class="action-btn"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                link 
                @click="handleManageSKU(row)"
                class="action-btn"
              >
                <el-icon><Setting /></el-icon>
                SKU
              </el-button>
              <el-dropdown @command="(command: string) => handleDropdownCommand(command, row)" trigger="click">
                <el-button type="info" size="small" link class="action-btn">
                  更多 <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item 
                      command="publish" 
                      v-if="row.status !== 'on_shelf'"
                    >
                      <el-icon><Top /></el-icon>
                      上架商品
                    </el-dropdown-item>
                    <el-dropdown-item 
                      command="unpublish" 
                      v-if="row.status === 'on_shelf'"
                    >
                      <el-icon><Bottom /></el-icon>
                      下架商品
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><DocumentCopy /></el-icon>
                      复制商品
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除商品
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 商品表单对话框 -->
    <el-dialog
      v-model="productFormVisible"
      :title="isEditMode ? '编辑商品' : '新建商品'"
      width="900px"
      :close-on-click-modal="false"
      @close="handleFormClose"
    >
      <ProductForm
        v-if="productFormVisible"
        :store-id="storeId"
        :edit-product="currentProduct"
        :mode="isEditMode ? 'edit' : 'create'"
        @cancel="productFormVisible = false"
        @success="handleProductSuccess"
      />
    </el-dialog>

    <!-- SKU管理对话框 -->
    <el-dialog
      v-model="skuManageVisible"
      title="SKU管理"
      width="800px"
      :close-on-click-modal="false"
      @close="skuManageVisible = false"
    >
      <SKUManagement
        v-if="skuManageVisible && currentProduct"
        :spu="currentProduct"
        @success="handleSKUSuccess"
      />
    </el-dialog>

    <!-- 复制商品对话框 -->
    <el-dialog
      v-model="copyDialogVisible"
      title="复制商品"
      width="400px"
      @close="copyDialogVisible = false"
    >
      <el-form :model="copyForm" label-width="80px">
        <el-form-item label="新商品名">
          <el-input
            v-model="copyForm.newName"
            placeholder="请输入新商品名称"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="copyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmCopy">确认复制</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, Goods, Check, Warning, WarnTriangleFilled, Plus, Top, Bottom,
  Delete, Search, Refresh, Edit, Setting, ArrowDown, Picture, Loading, DocumentCopy
} from '@element-plus/icons-vue'

// 导入API和类型
import {
  getSPUList, getSPUStats, deleteSPU, batchDeleteSPU, publishSPU, unpublishSPU,
  batchPublishSPU, batchUnpublishSPU, copySPU,
  type SPU, type SKU, type SPUQueryParams, type SPUStats, type SPUPageResponse,
  getSPUStatusLabel, getSPUStatusType, SPU_STATUS_OPTIONS
} from '@/api/merchant/spu'
import request from '@/utils/request'

// 导入组件
import ProductForm from './ProductForm.vue'
import SKUManagement from './SKUManagement.vue'

// Props
interface Props {
  storeId: number
  storeName: string
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  back: []
}>()

// 响应式数据
const loading = ref(false)
const productList = ref<SPU[]>([])
const selectedProducts = ref<SPU[]>([])
const skuCache = ref<Map<number, SKU[]>>(new Map()) // SKU数据缓存
const skuCountMap = ref<Map<number, number>>(new Map()) // SKU数量缓存
const stockCountMap = ref<Map<number, number>>(new Map()) // 库存数量缓存
const stats = ref<SPUStats>({
  totalCount: 0,
  onShelfCount: 0,
  offShelfCount: 0,
  draftCount: 0,
  lowStockCount: 0,
  totalValue: 0
})

// 搜索和筛选
const searchKeyword = ref('')
const statusFilter = ref('')
const statusOptions = SPU_STATUS_OPTIONS

// 分页
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 对话框状态
const productFormVisible = ref(false)
const skuManageVisible = ref(false)
const copyDialogVisible = ref(false)
const isEditMode = ref(false)
const currentProduct = ref<SPU | null>(null)

// 复制表单
const copyForm = ref({
  newName: ''
})

// 计算属性
const queryParams = computed<SPUQueryParams>(() => ({
  storeId: props.storeId,
  keyword: searchKeyword.value || undefined,
  status: statusFilter.value || undefined,
  page: currentPage.value,
  pageSize: pageSize.value
}))

// 方法
const loadProductList = async () => {
  loading.value = true
  try {
    const response = await getSPUList(queryParams.value)
    if (response.success) {
      productList.value = response.data?.list || []
      total.value = response.data?.total || 0
    } else {
      ElMessage.error(response.message || '获取商品列表失败')
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
    ElMessage.error('加载商品列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  try {
    const response = await getSPUStats(props.storeId)
    if (response.success && response.data) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadProductList()
}

const handleStatusFilter = () => {
  currentPage.value = 1
  loadProductList()
}

const refreshProductList = () => {
  loadProductList()
  loadStats()
}

const handleSelectionChange = (selection: SPU[]) => {
  selectedProducts.value = selection
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadProductList()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadProductList()
}

// 商品操作
const handleCreateProduct = () => {
  currentProduct.value = null
  isEditMode.value = false
  productFormVisible.value = true
}

const handleEditProduct = (product: SPU) => {
  currentProduct.value = product
  isEditMode.value = true
  productFormVisible.value = true
}

const handleManageSKU = (product: SPU) => {
  currentProduct.value = product
  skuManageVisible.value = true
}

const handleDropdownCommand = async (command: string, product: SPU) => {
  switch (command) {
    case 'publish':
      await handlePublishProduct(product)
      break
    case 'unpublish':
      await handleUnpublishProduct(product)
      break
    case 'copy':
      handleCopyProduct(product)
      break
    case 'delete':
      await handleDeleteProduct(product)
      break
  }
}

const handlePublishProduct = async (product: SPU) => {
  try {
    await ElMessageBox.confirm(
      `确定要上架商品"${product.spuName}"吗？`,
      '确认上架',
      { type: 'warning' }
    )
    
    const response = await publishSPU(product.spuId!)
    if (response.success) {
      ElMessage.success('商品上架成功')
      refreshProductList()
    } else {
      ElMessage.error(response.message || '上架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('上架商品失败:', error)
      ElMessage.error('上架失败，请稍后重试')
    }
  }
}

const handleUnpublishProduct = async (product: SPU) => {
  try {
    await ElMessageBox.confirm(
      `确定要下架商品"${product.spuName}"吗？`,
      '确认下架',
      { type: 'warning' }
    )
    
    const response = await unpublishSPU(product.spuId!)
    if (response.success) {
      ElMessage.success('商品下架成功')
      refreshProductList()
    } else {
      ElMessage.error(response.message || '下架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架商品失败:', error)
      ElMessage.error('下架失败，请稍后重试')
    }
  }
}

const handleCopyProduct = (product: SPU) => {
  currentProduct.value = product
  copyForm.value.newName = `${product.spuName}_副本`
  copyDialogVisible.value = true
}

const handleConfirmCopy = async () => {
  if (!copyForm.value.newName.trim()) {
    ElMessage.warning('请输入新商品名称')
    return
  }
  
  try {
    const response = await copySPU(currentProduct.value!.spuId!, copyForm.value.newName)
    if (response.success) {
      ElMessage.success('商品复制成功')
      copyDialogVisible.value = false
      refreshProductList()
    } else {
      ElMessage.error(response.message || '复制失败')
    }
  } catch (error) {
    console.error('复制商品失败:', error)
    ElMessage.error('复制失败，请稍后重试')
  }
}

const handleDeleteProduct = async (product: SPU) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.spuName}"吗？此操作不可恢复！`,
      '确认删除',
      { type: 'warning' }
    )
    
    const response = await deleteSPU(product.spuId!)
    if (response.success) {
      ElMessage.success('商品删除成功')
      refreshProductList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 批量操作
const handleBatchPublish = async () => {
  const spuIds = selectedProducts.value.map(p => p.spuId!)
  try {
    await ElMessageBox.confirm(
      `确定要批量上架选中的 ${spuIds.length} 个商品吗？`,
      '确认批量上架',
      { type: 'warning' }
    )
    
    const response = await batchPublishSPU(spuIds)
    if (response.success) {
      ElMessage.success('批量上架成功')
      refreshProductList()
    } else {
      ElMessage.error(response.message || '批量上架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量上架失败:', error)
      ElMessage.error('批量上架失败，请稍后重试')
    }
  }
}

const handleBatchUnpublish = async () => {
  const spuIds = selectedProducts.value.map(p => p.spuId!)
  try {
    await ElMessageBox.confirm(
      `确定要批量下架选中的 ${spuIds.length} 个商品吗？`,
      '确认批量下架',
      { type: 'warning' }
    )
    
    const response = await batchUnpublishSPU(spuIds)
    if (response.success) {
      ElMessage.success('批量下架成功')
      refreshProductList()
    } else {
      ElMessage.error(response.message || '批量下架失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量下架失败:', error)
      ElMessage.error('批量下架失败，请稍后重试')
    }
  }
}

const handleBatchDelete = async () => {
  const spuIds = selectedProducts.value.map(p => p.spuId!)
  try {
    await ElMessageBox.confirm(
      `确定要批量删除选中的 ${spuIds.length} 个商品吗？此操作不可恢复！`,
      '确认批量删除',
      { type: 'warning' }
    )
    
    const response = await batchDeleteSPU(spuIds)
    if (response.success) {
      ElMessage.success('批量删除成功')
      refreshProductList()
    } else {
      ElMessage.error(response.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败，请稍后重试')
    }
  }
}

// 表单处理
const handleFormClose = () => {
  currentProduct.value = null
  isEditMode.value = false
}

const handleProductSuccess = () => {
  productFormVisible.value = false
  refreshProductList()
}

const handleSKUSuccess = () => {
  refreshProductList()
}

// 懒加载SKU数据
const loadSkuData = async (spuId: number): Promise<SKU[]> => {
  // 检查缓存
  if (skuCache.value.has(spuId)) {
    return skuCache.value.get(spuId)!
  }

  try {
    const skuResponse = await request({
      url: `/api/merchant/sku/spu/${spuId}`,
      method: 'get'
    })
    
    if (skuResponse.data?.success && skuResponse.data?.data) {
      const skus = skuResponse.data.data
      skuCache.value.set(spuId, skus)
      
      // 更新计数缓存
      skuCountMap.value.set(spuId, skus.length)
      const totalStock = skus.reduce((total: number, sku: SKU) => total + sku.stockQuantity, 0)
      stockCountMap.value.set(spuId, totalStock)
      
      return skus
    } else {
      skuCache.value.set(spuId, [])
      skuCountMap.value.set(spuId, 0)
      stockCountMap.value.set(spuId, 0)
      return []
    }
  } catch (error) {
    console.warn(`获取SPU ${spuId} 的SKU数据失败:`, error)
    skuCache.value.set(spuId, [])
    skuCountMap.value.set(spuId, 0)
    stockCountMap.value.set(spuId, 0)
    return []
  }
}

// 获取SKU数量显示
const getSkuCountDisplay = (product: SPU): string => {
  if (!product.spuId) return '0'
  
  if (skuCountMap.value.has(product.spuId)) {
    return skuCountMap.value.get(product.spuId)!.toString()
  }
  
  // 异步加载数据
  loadSkuData(product.spuId)
  return '-'
}

// 获取库存数量显示
const getStockCountDisplay = (product: SPU): string => {
  if (!product.spuId) return '0'
  
  if (stockCountMap.value.has(product.spuId)) {
    return stockCountMap.value.get(product.spuId)!.toString()
  }
  
  // 异步加载数据
  loadSkuData(product.spuId)
  return '-'
}

// 获取SKU数量（懒加载）
const getSkuCount = async (product: SPU): Promise<number> => {
  if (!product.spuId) return 0
  const skus = await loadSkuData(product.spuId)
  return skus.length
}

// 工具方法
const getTotalStock = async (product: SPU): Promise<number> => {
  if (!product.spuId) return 0
  const skus = await loadSkuData(product.spuId)
  return skus.reduce((total, sku) => total + sku.stockQuantity, 0)
}

const formatDate = (dateString?: string): string => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  
  // 检查是否为移动端或小屏幕
  const isMobile = window.innerWidth <= 768
  
  if (isMobile) {
    // 移动端显示简洁格式：MM-DD
    return date.toLocaleDateString('zh-CN', {
      month: '2-digit',
      day: '2-digit'
    })
  } else {
    // 桌面端显示完整格式：YYYY-MM-DD
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 监听查询参数变化
watch(queryParams, () => {
  loadProductList()
}, { deep: true })

// 组件挂载
onMounted(() => {
  loadProductList()
  loadStats()
})
</script>

<style scoped>
.product-management {
  padding: 24px;
  background: #f5f7fa;
  border-radius: 8px;
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left h2 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
}

.total-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.on-shelf-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.off-shelf-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.low-stock-icon {
  background: linear-gradient(135deg, #ffeaa7 0%, #fab1a0 100%);
}

.stat-info h3 {
  margin: 0;
  color: #303133;
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.stat-info p {
  margin: 6px 0 0 0;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.action-left {
  display: flex;
  gap: 12px;
}

.action-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-list-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 8px;
  text-align: left;
}

.product-image {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  overflow: hidden;
  background: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.product-image:hover {
  border-color: #409eff;
  transform: scale(1.05);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-image img:hover {
  transform: scale(1.1);
}

.default-image {
  font-size: 24px;
  color: #c0c4cc;
}

.product-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.product-name {
  margin: 0;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
  cursor: pointer;
  transition: color 0.3s ease;
}

.product-name:hover {
  color: #409eff;
}

.product-description {
  margin: 0;
  color: #606266;
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.3;
  max-height: 2.6em;
}

.product-meta {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
}

.brand-tag {
  --el-tag-bg-color: #e8f4fd;
  --el-tag-border-color: #b3d8ff;
  --el-tag-text-color: #409eff;
  font-weight: 500;
}

.unit-tag {
  --el-tag-bg-color: #f0f9ff;
  --el-tag-border-color: #c6f7ff;
  --el-tag-text-color: #13c2c2;
  font-weight: 500;
}

.price-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.price {
  color: #e74c3c;
  font-weight: 700;
  font-size: 16px;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(231, 76, 60, 0.1);
}

.count-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 2px;
}

.sku-count, .stock-count {
  font-weight: 700;
  font-size: 16px;
  color: #303133;
  line-height: 1;
}

.stock-count.low-stock {
  color: #f56c6c;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.6; }
  100% { opacity: 1; }
}

.count-label {
  font-size: 11px;
  color: #909399;
  font-weight: 500;
}

.status-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.status-tag {
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.time-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.create-time {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  text-align: center;
  font-weight: 500;
}

.action-cell {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  height: 100%;
  flex-wrap: wrap;
}

.action-btn {
  font-size: 13px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.dropdown-icon {
  font-size: 12px;
  margin-left: 2px;
  transition: transform 0.3s ease;
}

.action-btn:hover .dropdown-icon {
  transform: rotate(180deg);
}

.pagination-container {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-table) {
  border-radius: 0;
  font-size: 14px;
  --el-table-border-color: #e4e7ed;
  --el-table-bg-color: #ffffff;
  --el-table-tr-bg-color: #ffffff;
  --el-table-expanded-cell-bg-color: #fafafa;
}

:deep(.el-table th) {
  background: #fafbfc;
  color: #303133;
  font-weight: 600;
  border-bottom: 2px solid #e4e7ed;
  padding: 16px 0;
  font-size: 14px;
  text-align: center;
}

:deep(.el-table th.el-table__cell) {
  border-right: 1px solid #f0f2f5;
}

:deep(.el-table th.el-table__cell:last-child) {
  border-right: none;
}

:deep(.el-table td) {
  padding: 0;
  border-bottom: 1px solid #f0f2f5;
  border-right: 1px solid #f8f9fa;
  vertical-align: middle;
}

:deep(.el-table td:last-child) {
  border-right: none;
}

:deep(.el-table tr) {
  transition: all 0.3s ease;
}

:deep(.el-table tr:hover > td) {
  background: #f8fafe;
  border-color: #e8f4fd;
}

:deep(.el-table .el-table__cell) {
  position: relative;
}

:deep(.el-table .el-table__cell::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: transparent;
  transition: background 0.3s ease;
}

:deep(.el-table tr:hover .el-table__cell::before) {
  background: rgba(64, 158, 255, 0.02);
}

:deep(.el-checkbox) {
  --el-checkbox-checked-bg-color: #409eff;
  --el-checkbox-checked-input-border-color: #409eff;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #409eff;
  border-color: #409eff;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button--small) {
  padding: 6px 12px;
  font-size: 13px;
  height: auto;
  min-height: 32px;
}

:deep(.el-button.is-link) {
  border: none;
  background: transparent;
  padding: 4px 8px;
}

:deep(.el-button.is-link:hover) {
  background: rgba(64, 158, 255, 0.1);
  transform: translateY(-1px);
}

:deep(.el-tag) {
  border-radius: 16px;
  font-weight: 600;
  padding: 6px 12px;
  font-size: 12px;
  border: none;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

:deep(.el-tag:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

:deep(.el-tag--success) {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
}

:deep(.el-tag--warning) {
  background: linear-gradient(135deg, #e6a23c 0%, #f0a020 100%);
  color: white;
}

:deep(.el-tag--danger) {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  color: white;
}

:deep(.el-tag--info) {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
  color: white;
}

:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: 1px solid #e4e7ed;
  padding: 8px 0;
}

:deep(.el-dropdown-menu__item) {
  padding: 8px 16px;
  font-size: 13px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f8fafe;
  color: #409eff;
  transform: translateX(2px);
}

:deep(.el-dropdown-menu__item.is-divided) {
  border-top: 1px solid #e4e7ed;
  margin-top: 4px;
  padding-top: 12px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper:hover) {
  border-color: #c0c4cc;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

:deep(.el-select .el-input__wrapper) {
  cursor: pointer;
}

:deep(.el-dropdown) {
  vertical-align: top;
}

:deep(.el-pagination) {
  --el-pagination-button-color: #606266;
  --el-pagination-hover-color: #409eff;
  --el-pagination-button-bg-color: #f4f4f5;
  --el-pagination-button-disabled-color: #c0c4cc;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  border-radius: 6px;
  margin: 0 2px;
  transition: all 0.3s ease;
}

:deep(.el-pagination .btn-prev:hover),
:deep(.el-pagination .btn-next:hover),
:deep(.el-pagination .el-pager li:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

:deep(.el-loading-spinner) {
  color: #409eff;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .product-info {
    gap: 10px;
    padding: 10px 6px;
  }
  
  .product-image {
    width: 48px;
    height: 48px;
  }
  
  .product-name {
    font-size: 14px;
  }
  
  .product-description {
    font-size: 12px;
  }
  
  .price {
    font-size: 15px;
  }
  
  .sku-count, .stock-count {
    font-size: 15px;
  }
  
  .action-cell {
    gap: 2px;
  }
  
  .action-btn {
    font-size: 12px;
    padding: 3px 6px;
  }
}

@media (max-width: 1200px) {
  .product-info {
    gap: 8px;
    padding: 8px 4px;
  }
  
  .product-image {
    width: 40px;
    height: 40px;
  }
  
  .product-name {
    font-size: 13px;
  }
  
  .product-description {
    font-size: 11px;
  }
  
  .price {
    font-size: 14px;
  }
  
  .sku-count, .stock-count {
    font-size: 14px;
  }
  
  .count-label {
    font-size: 10px;
  }
  
  .create-time {
    font-size: 12px;
  }
  
  .action-cell {
    flex-direction: column;
    gap: 4px;
  }
  
  .action-btn {
    font-size: 11px;
    padding: 2px 4px;
    min-width: 60px;
  }
  
  :deep(.el-table th) {
    padding: 12px 0;
    font-size: 13px;
  }
  
  :deep(.el-table) {
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .product-management {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .stat-card {
    padding: 16px;
    gap: 12px;
  }
  
  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 24px;
  }
  
  .stat-info h3 {
    font-size: 24px;
  }
  
  .action-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 16px;
  }
  
  .action-left, .action-right {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .product-list-container {
    border-radius: 8px;
  }
  
  .product-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 6px;
    padding: 12px 4px;
  }
  
  .product-image {
    width: 36px;
    height: 36px;
  }
  
  .product-details {
    align-items: center;
    text-align: center;
  }
  
  .product-name {
    font-size: 12px;
    text-align: center;
  }
  
  .product-description {
    font-size: 10px;
    text-align: center;
  }
  
  .product-meta {
    justify-content: center;
  }
  
  .price {
    font-size: 13px;
  }
  
  .sku-count, .stock-count {
    font-size: 13px;
  }
  
  .count-label {
    font-size: 9px;
  }
  
  .create-time {
    font-size: 11px;
  }
  
  .action-cell {
    flex-direction: column;
    gap: 2px;
  }
  
  .action-btn {
    font-size: 10px;
    padding: 2px 4px;
    min-width: 50px;
  }
  
  .dropdown-icon {
    font-size: 10px;
  }
  
  /* 移动端表格优化 */
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 8px 0;
  }
  
  :deep(.el-table th) {
    font-size: 12px;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-tag) {
    padding: 2px 6px;
    font-size: 10px;
  }
  
  /* 移动端对话框宽度调整 */
  :deep(.el-dialog) {
    width: 95% !important;
    margin: 0 auto;
  }
}

@media (max-width: 480px) {
  .action-left, .action-right {
    flex-direction: column;
    gap: 8px;
  }
  
  .product-meta {
    flex-direction: column;
    gap: 4px;
    align-items: center;
  }
  
  .product-info {
    padding: 8px 2px;
  }
  
  .product-image {
    width: 32px;
    height: 32px;
  }
  
  .product-name {
    font-size: 11px;
  }
  
  .product-description {
    font-size: 9px;
  }
  
  .price {
    font-size: 12px;
  }
  
  .sku-count, .stock-count {
    font-size: 12px;
  }
  
  .action-btn {
    font-size: 9px;
    padding: 1px 3px;
    min-width: 45px;
  }
  
  :deep(.el-table th) {
    padding: 6px 0;
    font-size: 11px;
  }
  
  :deep(.el-table td) {
    padding: 6px 0;
  }
  
  :deep(.el-tag) {
    padding: 1px 4px;
    font-size: 9px;
  }
}
</style> 