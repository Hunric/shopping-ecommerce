<template>
  <div class="test-home">
    <h1>测试页面</h1>
    <p>如果您能看到这个页面，说明Vue组件渲染正常</p>
    
    <div class="test-section">
      <h2>基础测试</h2>
      <p>当前时间: {{ currentTime }}</p>
      <p>计数器: {{ counter }}</p>
      <el-button @click="increment" type="primary">点击增加</el-button>
    </div>
    
    <div class="test-section">
      <h2>API测试</h2>
      <p>API状态: {{ apiStatus }}</p>
      <el-button @click="testApi" type="success">测试API</el-button>
    </div>
    
    <div class="test-section">
      <h2>图标测试</h2>
      <el-icon><Monitor /></el-icon>
      <el-icon><House /></el-icon>
      <el-icon><Grid /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Monitor, House, Grid } from '@element-plus/icons-vue'

const currentTime = ref('')
const counter = ref(0)
const apiStatus = ref('未测试')

const increment = () => {
  counter.value++
  ElMessage.success(`计数器增加到 ${counter.value}`)
}

const testApi = async () => {
  try {
    apiStatus.value = '测试中...'
    const response = await fetch('/api/user/categories')
    if (response.ok) {
      apiStatus.value = 'API连接成功'
      ElMessage.success('API测试成功')
    } else {
      apiStatus.value = `API错误: ${response.status}`
      ElMessage.error(`API测试失败: ${response.status}`)
    }
  } catch (error) {
    apiStatus.value = `连接失败: ${error.message}`
    ElMessage.error(`API测试失败: ${error.message}`)
  }
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleString()
}

onMounted(() => {
  updateTime()
  setInterval(updateTime, 1000)
})
</script>

<style scoped>
.test-home {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin: 20px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

h1 {
  color: #409eff;
}

h2 {
  color: #333;
  margin-bottom: 10px;
}

p {
  margin: 10px 0;
}
</style> 