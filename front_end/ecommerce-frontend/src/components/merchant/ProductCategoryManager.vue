<template>
  <div class="category-manager">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品分类管理</span>
          <el-button type="primary" @click="handleAddCategory">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>
      
      <div class="category-content">
        <p>商品分类管理功能开发中...</p>
        <el-tree
          :data="categoryData"
          show-checkbox
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <span>
                <el-button type="text" size="small" @click="() => handleEdit(data)">
                  编辑
                </el-button>
                <el-button type="text" size="small" @click="() => handleDelete(data)">
                  删除
                </el-button>
              </span>
            </span>
          </template>
        </el-tree>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElCard, ElButton, ElIcon, ElTree, ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const categoryData = ref([
  {
    id: 1,
    label: '服装',
    children: [
      { id: 2, label: '男装' },
      { id: 3, label: '女装' }
    ]
  },
  {
    id: 4,
    label: '数码',
    children: [
      { id: 5, label: '手机' },
      { id: 6, label: '电脑' }
    ]
  }
])

const handleAddCategory = () => {
  ElMessage.info('添加分类功能开发中...')
}

const handleEdit = (data: any) => {
  ElMessage.info(`编辑分类: ${data.label}`)
}

const handleDelete = (data: any) => {
  ElMessage.info(`删除分类: ${data.label}`)
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-content {
  padding: 20px 0;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style> 