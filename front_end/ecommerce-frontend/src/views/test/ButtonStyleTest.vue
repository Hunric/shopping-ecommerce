<template>
  <div class="button-style-test">
    <AppNavbar />
    
    <div class="test-container">
      <h1>导航栏按钮样式一致性测试</h1>
      
      <div class="test-section">
        <h2>未登录状态测试</h2>
        <p>请确保以下元素的高度、字体大小和视觉效果保持一致：</p>
        
        <div class="test-navbar">
          <div class="test-actions">
            <!-- 购物车图标 -->
            <div class="cart-action">
              <el-icon><ShoppingCart /></el-icon>
              <span class="action-text">购物车</span>
              <span class="cart-badge">3</span>
            </div>
            
            <!-- 登录注册按钮 -->
            <div class="auth-actions">
              <el-button text size="default" class="auth-btn">登录</el-button>
              <el-button type="primary" size="default" class="auth-btn">注册</el-button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="test-section">
        <h2>已登录状态测试</h2>
        <p>请确保用户信息区域与其他元素保持一致：</p>
        
        <div class="test-navbar">
          <div class="test-actions">
            <!-- 购物车图标 -->
            <div class="cart-action">
              <el-icon><ShoppingCart /></el-icon>
              <span class="action-text">购物车</span>
              <span class="cart-badge">5</span>
            </div>
            
            <!-- 用户信息 -->
            <div class="user-info">
              <el-icon><User /></el-icon>
              <span class="user-name">测试用户</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="test-section">
        <h2>样式规范</h2>
        <ul>
          <li>高度：32px</li>
          <li>最小宽度：80px（购物车、用户信息），60px（登录注册按钮）</li>
          <li>字体大小：14px</li>
          <li>内边距：8px 12px</li>
          <li>边框圆角：6px</li>
          <li>边框：1px solid transparent</li>
          <li>文字不换行：white-space: nowrap</li>
          <li>悬停效果：背景色 #f5f7fa，边框色 #c6e2ff</li>
        </ul>
      </div>
      
      <div class="test-section">
        <h2>颜色规范</h2>
        <ul>
          <li>文本颜色：#606266</li>
          <li>主色调：#409eff</li>
          <li>悬停背景：#f5f7fa</li>
          <li>悬停边框：#c6e2ff</li>
          <li>购物车徽章：#f56c6c</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ShoppingCart, User } from '@element-plus/icons-vue'
import AppNavbar from '@/components/common/AppNavbar.vue'
</script>

<style scoped>
.button-style-test {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.test-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.test-section {
  background: white;
  padding: 30px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-navbar {
  background: white;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
}

.test-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  justify-content: center;
}

/* 复制导航栏样式 */
.cart-action {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  position: relative;
  height: 32px;
  min-width: 80px;
  font-size: 14px;
  border: 1px solid transparent;
  box-sizing: border-box;
  white-space: nowrap;
}

.cart-action:hover {
  background-color: #f5f7fa;
  border-color: #c6e2ff;
}

.action-text {
  font-size: 14px;
  color: #606266;
}

.cart-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s;
  height: 32px;
  min-width: 80px;
  font-size: 14px;
  border: 1px solid transparent;
  box-sizing: border-box;
  white-space: nowrap;
}

.user-info:hover {
  background-color: #f5f7fa;
  border-color: #c6e2ff;
}

.user-name {
  font-size: 14px;
  color: #606266;
}

.auth-actions {
  display: flex;
  gap: 10px;
}

.auth-btn {
  min-width: 60px;
  height: 32px;
  font-size: 14px;
}

h1 {
  color: #303133;
  margin-bottom: 20px;
}

h2 {
  color: #409eff;
  margin-bottom: 15px;
}

ul {
  margin: 15px 0;
  padding-left: 20px;
}

li {
  margin: 8px 0;
  color: #606266;
}

p {
  color: #606266;
  line-height: 1.6;
}
</style> 