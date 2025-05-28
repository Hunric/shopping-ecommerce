<template>
  <div class="sku-management">
    <div class="sku-header">
      <div class="spu-info">
        <h3>{{ spu.spuName }}</h3>
        <p>SKU管理 - 共 {{ skuList.length }} 个SKU</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleBatchEdit">
          <el-icon><Edit /></el-icon>
          批量编辑
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- SKU列表 -->
    <div class="sku-table-container">
      <el-table
        v-loading="loading"
        :data="skuList"
        @selection-change="handleSelectionChange"
        border
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="SKU信息" min-width="250">
          <template #default="{ row }">
            <div class="sku-info">
              <div class="sku-image">
                <img
                  v-if="row.exclusiveImageUrl || spu.productImage"
                  :src="row.exclusiveImageUrl || spu.productImage"
                  :alt="row.skuName"
                  @error="handleImageError"
                />
                <el-icon v-else class="default-image"><Picture /></el-icon>
              </div>
              <div class="sku-details">
                <h4>{{ row.skuName }}</h4>
                <div class="sku-attrs">
                  <el-tag
                    v-for="(value, key) in row.attributeCombination"
                    :key="key"
                    size="small"
                    style="margin-right: 4px; margin-bottom: 4px"
                  >
                    {{ key }}: {{ value }}
                  </el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="销售价格" width="150" align="center">
          <template #default="{ row }">
            <div class="editable-cell">
              <span v-if="!row.editing" class="price-display">
                ¥{{ row.salePrice.toFixed(2) }}
              </span>
              <el-input-number
                v-else
                v-model="row.salePrice"
                :min="0"
                :precision="2"
                size="small"
                style="width: 100%"
              />
              <el-button
                v-if="!row.editing"
                size="small"
                text
                @click="row.editing = true"
              >
                <el-icon><Edit /></el-icon>
              </el-button>
              <div v-else class="edit-actions">
                <el-button size="small" type="primary" @click="handleSavePrice(row)">
                  <el-icon><Check /></el-icon>
                </el-button>
                <el-button size="small" @click="handleCancelEdit(row)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="库存数量" width="150" align="center">
          <template #default="{ row }">
            <div class="editable-cell">
              <span v-if="!row.stockEditing" class="stock-display">
                {{ row.stockQuantity }}
              </span>
              <el-input-number
                v-else
                v-model="row.stockQuantity"
                :min="0"
                size="small"
                style="width: 100%"
              />
              <el-button
                v-if="!row.stockEditing"
                size="small"
                text
                @click="row.stockEditing = true"
              >
                <el-icon><Edit /></el-icon>
              </el-button>
              <div v-else class="edit-actions">
                <el-button size="small" type="primary" @click="handleSaveStock(row)">
                  <el-icon><Check /></el-icon>
                </el-button>
                <el-button size="small" @click="handleCancelStockEdit(row)">
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="库存警告" width="120" align="center">
          <template #default="{ row }">
            <span>{{ row.warnStock }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getSKUStatusType(row.status)">
              {{ getSKUStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditSKU(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteSKU(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 批量操作栏 -->
    <div v-if="selectedSKUs.length > 0" class="batch-actions">
      <div class="batch-info">
        已选择 {{ selectedSKUs.length }} 个SKU
      </div>
      <div class="batch-buttons">
        <el-button type="success" @click="handleBatchStatus(1)">
          <el-icon><Top /></el-icon>
          批量上架
        </el-button>
        <el-button type="warning" @click="handleBatchStatus(2)">
          <el-icon><Bottom /></el-icon>
          批量下架
        </el-button>
        <el-button type="danger" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- SKU编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑SKU"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        v-if="currentSKU"
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="SKU名称">
          <el-input v-model="editForm.skuName" disabled />
        </el-form-item>
        
        <el-form-item label="销售价格" prop="salePrice">
          <el-input-number
            v-model="editForm.salePrice"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input-number
            v-model="editForm.stockQuantity"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="库存警告" prop="warnStock">
          <el-input-number
            v-model="editForm.warnStock"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="SKU状态" prop="status">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="2">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="专属图片">
          <el-upload
            class="sku-image-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :http-request="handleImageUpload"
          >
            <img
              v-if="editForm.exclusiveImageUrl"
              :src="editForm.exclusiveImageUrl"
              class="uploaded-sku-image"
            />
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
              <div>上传SKU图片</div>
            </div>
          </el-upload>
          <div class="upload-tip">
            建议尺寸：800x800像素，支持JPG、PNG格式，大小不超过2MB
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSKU" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>

    <!-- 批量编辑对话框 -->
    <el-dialog
      v-model="batchEditDialogVisible"
      title="批量编辑SKU"
      width="500px"
    >
      <el-form :model="batchEditForm" label-width="100px">
        <el-form-item label="操作类型">
          <el-radio-group v-model="batchEditForm.type">
            <el-radio label="price">批量改价</el-radio>
            <el-radio label="stock">批量改库存</el-radio>
            <el-radio label="status">批量改状态</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="batchEditForm.type === 'price'" label="价格调整">
          <el-radio-group v-model="batchEditForm.priceType">
            <el-radio label="set">设置为</el-radio>
            <el-radio label="increase">增加</el-radio>
            <el-radio label="decrease">减少</el-radio>
            <el-radio label="multiply">乘以</el-radio>
          </el-radio-group>
          <el-input-number
            v-model="batchEditForm.priceValue"
            :min="0"
            :precision="2"
            style="width: 200px; margin-left: 12px"
          />
        </el-form-item>
        
        <el-form-item v-if="batchEditForm.type === 'stock'" label="库存调整">
          <el-radio-group v-model="batchEditForm.stockType">
            <el-radio label="set">设置为</el-radio>
            <el-radio label="increase">增加</el-radio>
            <el-radio label="decrease">减少</el-radio>
          </el-radio-group>
          <el-input-number
            v-model="batchEditForm.stockValue"
            :min="0"
            style="width: 200px; margin-left: 12px"
          />
        </el-form-item>
        
        <el-form-item v-if="batchEditForm.type === 'status'" label="状态设置">
          <el-radio-group v-model="batchEditForm.statusValue">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="2">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="batchEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmBatchEdit" :loading="batchSaving">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Edit, Refresh, Picture, Check, Close, Top, Bottom, Delete, Plus
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'

// 导入API和类型
import {
  getSKUList, updateSKU, deleteSKU, batchUpdateSKUPrice, batchUpdateSKUStock,
  type SPU, type SKU,
  getSKUStatusLabel, getSKUStatusType
} from '@/api/merchant/spu'

// Props
interface Props {
  spu: SPU
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  success: []
}>()

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const batchSaving = ref(false)
const skuList = ref<(SKU & { editing?: boolean; stockEditing?: boolean; originalPrice?: number; originalStock?: number })[]>([])
const selectedSKUs = ref<SKU[]>([])

// 编辑对话框
const editDialogVisible = ref(false)
const editFormRef = ref<FormInstance>()
const currentSKU = ref<SKU | null>(null)
const editForm = reactive({
  skuName: '',
  salePrice: 0,
  stockQuantity: 0,
  warnStock: 10,
  status: 2 as 1 | 2 | 3,
  exclusiveImageUrl: '',
  attributeCombination: {}
})

// 批量编辑对话框
const batchEditDialogVisible = ref(false)
const batchEditForm = reactive({
  type: 'price',
  priceType: 'set',
  priceValue: 0,
  stockType: 'set',
  stockValue: 0,
  statusValue: 1 as 1 | 2 | 3
})

// 表单验证规则
const editRules: FormRules = {
  salePrice: [
    { required: true, message: '请输入销售价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能小于0', trigger: 'blur' }
  ],
  stockQuantity: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存不能小于0', trigger: 'blur' }
  ],
  warnStock: [
    { required: true, message: '请输入库存警告值', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存警告值不能小于0', trigger: 'blur' }
  ]
}

// 方法
const loadSKUList = async () => {
  loading.value = true
  try {
    const response = await getSKUList(props.spu.spuId!)
    if (response.success && response.data) {
      skuList.value = response.data.map(sku => ({
        ...sku,
        editing: false,
        stockEditing: false,
        originalPrice: typeof sku.salePrice === 'string' ? parseFloat(sku.salePrice) : sku.salePrice,
        originalStock: sku.stockQuantity
      }))
    } else {
      ElMessage.error(response.message || '获取SKU列表失败')
    }
  } catch (error) {
    console.error('加载SKU列表失败:', error)
    ElMessage.error('加载SKU列表失败')
  } finally {
    loading.value = false
  }
}

const handleRefresh = () => {
  loadSKUList()
}

const handleSelectionChange = (selection: SKU[]) => {
  selectedSKUs.value = selection
}

// 价格编辑
const handleSavePrice = async (sku: any) => {
  try {
    // 发送完整的SKU数据，只更新价格字段
    const updateData = {
      skuName: sku.skuName,
      salePrice: sku.salePrice.toString(),
      stockQuantity: sku.stockQuantity,
      warnStock: sku.warnStock,
      status: sku.status as 1 | 2 | 3,
      exclusiveImageUrl: sku.exclusiveImageUrl || '',
      attributeCombination: sku.attributeCombination || {}
    }
    
    console.log('更新SKU价格数据:', updateData)
    
    const response = await updateSKU(sku.skuId, updateData)
    if (response.success) {
      sku.editing = false
      sku.originalPrice = sku.salePrice
      ElMessage.success('价格更新成功')
      emit('success')
    } else {
      ElMessage.error(response.message || '价格更新失败')
    }
  } catch (error) {
    console.error('更新价格失败:', error)
    ElMessage.error('价格更新失败')
  }
}

const handleCancelEdit = (sku: any) => {
  sku.salePrice = sku.originalPrice
  sku.editing = false
}

// 库存编辑
const handleSaveStock = async (sku: any) => {
  try {
    // 发送完整的SKU数据，只更新库存字段
    const updateData = {
      skuName: sku.skuName,
      salePrice: (typeof sku.salePrice === 'string' ? sku.salePrice : sku.salePrice.toString()),
      stockQuantity: sku.stockQuantity,
      warnStock: sku.warnStock,
      status: sku.status as 1 | 2 | 3,
      exclusiveImageUrl: sku.exclusiveImageUrl || '',
      attributeCombination: sku.attributeCombination || {}
    }
    
    console.log('更新SKU库存数据:', updateData)
    
    const response = await updateSKU(sku.skuId, updateData)
    if (response.success) {
      sku.stockEditing = false
      sku.originalStock = sku.stockQuantity
      ElMessage.success('库存更新成功')
      emit('success')
    } else {
      ElMessage.error(response.message || '库存更新失败')
    }
  } catch (error) {
    console.error('更新库存失败:', error)
    ElMessage.error('库存更新失败')
  }
}

const handleCancelStockEdit = (sku: any) => {
  sku.stockQuantity = sku.originalStock
  sku.stockEditing = false
}

// SKU操作
const handleEditSKU = (sku: SKU) => {
  currentSKU.value = sku
  Object.assign(editForm, {
    skuName: sku.skuName,
    salePrice: sku.salePrice,
    stockQuantity: sku.stockQuantity,
    warnStock: sku.warnStock,
    status: sku.status,
    exclusiveImageUrl: sku.exclusiveImageUrl || '',
    attributeCombination: sku.attributeCombination || {}
  })
  editDialogVisible.value = true
}

const handleToggleStatus = async (sku: SKU) => {
  const newStatus = sku.status === 1 ? 2 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}SKU"${sku.skuName}"吗？`,
      '确认操作',
      { type: 'warning' }
    )
    
    // 发送完整的SKU数据，只更新状态字段
    const updateData = {
      skuName: sku.skuName,
      salePrice: (typeof sku.salePrice === 'string' ? sku.salePrice : sku.salePrice.toString()),
      stockQuantity: sku.stockQuantity,
      warnStock: sku.warnStock,
      status: newStatus as 1 | 2 | 3,
      exclusiveImageUrl: sku.exclusiveImageUrl || '',
      attributeCombination: sku.attributeCombination || {}
    }
    
    console.log('切换SKU状态数据:', updateData)
    
    const response = await updateSKU(sku.skuId!, updateData)
    if (response.success) {
      sku.status = newStatus
      ElMessage.success(`${action}成功`)
      emit('success')
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}SKU失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDeleteSKU = async (sku: SKU) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除SKU"${sku.skuName}"吗？此操作不可恢复！`,
      '确认删除',
      { type: 'warning' }
    )
    
    const response = await deleteSKU(sku.skuId!)
    if (response.success) {
      ElMessage.success('删除成功')
      loadSKUList()
      emit('success')
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除SKU失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSaveSKU = async () => {
  if (!editFormRef.value || !currentSKU.value) return
  
  try {
    await editFormRef.value.validate()
    
    saving.value = true
    
    // 准备更新数据，确保价格为字符串格式
    const updateData = {
      skuName: editForm.skuName,
      salePrice: editForm.salePrice.toString(),
      stockQuantity: editForm.stockQuantity,
      warnStock: editForm.warnStock,
      status: editForm.status as 1 | 2 | 3,
      exclusiveImageUrl: editForm.exclusiveImageUrl,
      attributeCombination: editForm.attributeCombination
    }
    
    console.log('更新SKU数据:', updateData)
    
    const response = await updateSKU(currentSKU.value.skuId!, updateData)
    if (response.success) {
      ElMessage.success('SKU更新成功')
      editDialogVisible.value = false
      loadSKUList()
      emit('success')
    } else {
      ElMessage.error(response.message || 'SKU更新失败')
    }
  } catch (error) {
    console.error('更新SKU失败:', error)
    ElMessage.error('SKU更新失败')
  } finally {
    saving.value = false
  }
}

const handleDialogClose = () => {
  currentSKU.value = null
  Object.assign(editForm, {
    skuName: '',
    salePrice: 0,
    stockQuantity: 0,
    warnStock: 10,
    status: 2,
    exclusiveImageUrl: '',
    attributeCombination: {}
  })
}

// 批量操作
const handleBatchEdit = () => {
  if (selectedSKUs.value.length === 0) {
    ElMessage.warning('请先选择要编辑的SKU')
    return
  }
  batchEditDialogVisible.value = true
}

const handleBatchStatus = async (status: number) => {
  if (selectedSKUs.value.length === 0) {
    ElMessage.warning('请先选择要操作的SKU')
    return
  }
  
  const action = status === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(
      `确定要批量${action}选中的 ${selectedSKUs.value.length} 个SKU吗？`,
      '确认操作',
      { type: 'warning' }
    )
    
    // 这里应该调用批量更新状态的API
    // 暂时使用单个更新
    for (const sku of selectedSKUs.value) {
      const updateData = {
        skuName: sku.skuName,
        salePrice: (typeof sku.salePrice === 'string' ? sku.salePrice : sku.salePrice.toString()),
        stockQuantity: sku.stockQuantity,
        warnStock: sku.warnStock,
        status: status as 1 | 2 | 3,
        exclusiveImageUrl: sku.exclusiveImageUrl || '',
        attributeCombination: sku.attributeCombination || {}
      }
      
      await updateSKU(sku.skuId!, updateData)
    }
    
    ElMessage.success(`批量${action}成功`)
    loadSKUList()
    emit('success')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`批量${action}失败:`, error)
      ElMessage.error(`批量${action}失败`)
    }
  }
}

const handleBatchDelete = async () => {
  if (selectedSKUs.value.length === 0) {
    ElMessage.warning('请先选择要删除的SKU')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要批量删除选中的 ${selectedSKUs.value.length} 个SKU吗？此操作不可恢复！`,
      '确认删除',
      { type: 'warning' }
    )
    
    // 这里应该调用批量删除的API
    // 暂时使用单个删除
    for (const sku of selectedSKUs.value) {
      await deleteSKU(sku.skuId!)
    }
    
    ElMessage.success('批量删除成功')
    loadSKUList()
    emit('success')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

const handleConfirmBatchEdit = async () => {
  if (selectedSKUs.value.length === 0) {
    ElMessage.warning('请先选择要编辑的SKU')
    return
  }
  
  batchSaving.value = true
  try {
    const skuIds = selectedSKUs.value.map(sku => sku.skuId!)
    
    if (batchEditForm.type === 'price') {
      // 批量价格调整
      const updates = selectedSKUs.value.map(sku => {
        const currentPrice = typeof sku.salePrice === 'string' ? parseFloat(sku.salePrice) : sku.salePrice
        let newPrice = currentPrice
        
        switch (batchEditForm.priceType) {
          case 'set':
            newPrice = batchEditForm.priceValue
            break
          case 'increase':
            newPrice = currentPrice + batchEditForm.priceValue
            break
          case 'decrease':
            newPrice = Math.max(0, currentPrice - batchEditForm.priceValue)
            break
          case 'multiply':
            newPrice = currentPrice * batchEditForm.priceValue
            break
        }
        
        return {
          skuId: sku.skuId!,
          salePrice: Math.round(newPrice * 100) / 100 // 保留两位小数
        }
      })
      
      await batchUpdateSKUPrice(updates)
      
    } else if (batchEditForm.type === 'stock') {
      // 批量库存调整
      const updates = selectedSKUs.value.map(sku => {
        let newStock = sku.stockQuantity
        
        switch (batchEditForm.stockType) {
          case 'set':
            newStock = batchEditForm.stockValue
            break
          case 'increase':
            newStock = sku.stockQuantity + batchEditForm.stockValue
            break
          case 'decrease':
            newStock = Math.max(0, sku.stockQuantity - batchEditForm.stockValue)
            break
        }
        
        return {
          skuId: sku.skuId!,
          stockQuantity: newStock
        }
      })
      
      await batchUpdateSKUStock(updates)
      
    } else if (batchEditForm.type === 'status') {
      // 批量状态调整
      for (const sku of selectedSKUs.value) {
        const updateData = {
          skuName: sku.skuName,
          salePrice: (typeof sku.salePrice === 'string' ? sku.salePrice : sku.salePrice.toString()),
          stockQuantity: sku.stockQuantity,
          warnStock: sku.warnStock,
          status: batchEditForm.statusValue as 1 | 2 | 3,
          exclusiveImageUrl: sku.exclusiveImageUrl || '',
          attributeCombination: sku.attributeCombination || {}
        }
        
        await updateSKU(sku.skuId!, updateData)
      }
    }
    
    ElMessage.success('批量编辑成功')
    batchEditDialogVisible.value = false
    loadSKUList()
    emit('success')
    
  } catch (error) {
    console.error('批量编辑失败:', error)
    ElMessage.error('批量编辑失败')
  } finally {
    batchSaving.value = false
  }
}

// 图片上传
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
    editForm.exclusiveImageUrl = e.target?.result as string
  }
  reader.readAsDataURL(file)
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.style.display = 'none'
}

// 组件挂载
onMounted(() => {
  loadSKUList()
})
</script>

<style scoped>
.sku-management {
  padding: 20px;
}

.sku-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.spu-info h3 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.spu-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.sku-table-container {
  margin-bottom: 20px;
}

.sku-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sku-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sku-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-image {
  font-size: 20px;
  color: #c0c4cc;
}

.sku-details {
  flex: 1;
  min-width: 0;
}

.sku-details h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sku-attrs {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.editable-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-display, .stock-display {
  font-weight: 600;
}

.edit-actions {
  display: flex;
  gap: 4px;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
}

.batch-info {
  color: #666;
  font-size: 14px;
}

.batch-buttons {
  display: flex;
  gap: 8px;
}

.sku-image-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sku-image-uploader:hover {
  border-color: #409eff;
}

.uploaded-sku-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  font-size: 14px;
}

.upload-placeholder .el-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.upload-tip {
  margin-top: 8px;
  color: #999;
  font-size: 12px;
  line-height: 1.4;
}

@media (max-width: 768px) {
  .sku-management {
    padding: 16px;
  }
  
  .sku-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .batch-actions {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .batch-buttons {
    justify-content: center;
  }
  
  .sku-info {
    flex-direction: column;
    align-items: flex-start;
    text-align: left;
  }
}
</style> 