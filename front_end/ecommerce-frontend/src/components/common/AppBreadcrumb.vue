<template>
  <div class="app-breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="goHome">首页</el-breadcrumb-item>
      <el-breadcrumb-item 
        v-for="(item, index) in breadcrumbItems" 
        :key="index"
        @click="item.clickable ? handleBreadcrumbClick(item) : null"
        :class="{ 'clickable': item.clickable }"
      >
        {{ item.label }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

interface BreadcrumbItem {
  label: string
  path?: string
  clickable?: boolean
}

interface Props {
  items: BreadcrumbItem[]
}

const props = defineProps<Props>()
const router = useRouter()

const breadcrumbItems = computed(() => props.items)

const goHome = () => {
  router.push('/')
}

const handleBreadcrumbClick = (item: BreadcrumbItem) => {
  if (item.path && item.clickable) {
    router.push(item.path)
  }
}
</script>

<script lang="ts">
import { computed } from 'vue'
</script>

<style scoped>
.app-breadcrumb {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.app-breadcrumb :deep(.el-breadcrumb__item:not(:last-child)) {
  cursor: pointer;
}

.app-breadcrumb :deep(.el-breadcrumb__item:not(:last-child):hover) {
  color: #409eff;
}

.clickable {
  cursor: pointer;
}

.clickable:hover {
  color: #409eff;
}
</style> 