<template>
  <div class="back-to-home-container" :class="{ 'fixed': fixed, 'inline': !fixed }">
    <el-button 
      @click="goToHome" 
      :type="type" 
      :size="size"
      :icon="HomeIcon"
      class="back-to-home-btn"
      :class="{ 'with-text': showText }"
    >
      <span v-if="showText">{{ text }}</span>
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElButton } from 'element-plus'
import { House as HomeIcon } from '@element-plus/icons-vue'

interface Props {
  // 按钮类型
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'text' | 'default'
  // 按钮大小
  size?: 'large' | 'default' | 'small'
  // 是否显示文字
  showText?: boolean
  // 按钮文字
  text?: string
  // 是否固定定位
  fixed?: boolean
  // 固定位置
  position?: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
}

const props = withDefaults(defineProps<Props>(), {
  type: 'primary',
  size: 'default',
  showText: true,
  text: '返回主页',
  fixed: false,
  position: 'top-left'
})

const router = useRouter()

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.back-to-home-container.fixed {
  position: fixed;
  z-index: 1000;
}

.back-to-home-container.fixed.top-left {
  top: 20px;
  left: 20px;
}

.back-to-home-container.fixed.top-right {
  top: 20px;
  right: 20px;
}

.back-to-home-container.fixed.bottom-left {
  bottom: 20px;
  left: 20px;
}

.back-to-home-container.fixed.bottom-right {
  bottom: 20px;
  right: 20px;
}

.back-to-home-container.inline {
  display: inline-block;
}

.back-to-home-btn {
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-to-home-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.back-to-home-btn.with-text {
  padding: 8px 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .back-to-home-container.fixed {
    bottom: 20px;
    right: 20px;
    top: auto;
    left: auto;
  }
  
  .back-to-home-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style> 