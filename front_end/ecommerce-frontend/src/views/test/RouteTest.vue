<template>
  <div class="route-test">
    <h1>路由测试页面</h1>
    <p>点击下面的链接测试各个路由是否正常工作</p>
    
    <div class="test-sections">
      <!-- 认证相关路由 -->
      <div class="test-section">
        <h2>认证相关</h2>
        <div class="test-links">
          <router-link to="/user/login" class="test-link">用户登录</router-link>
          <router-link to="/user/register" class="test-link">用户注册</router-link>
          <router-link to="/user/forgot-password" class="test-link">忘记密码</router-link>
          <router-link to="/user/profile" class="test-link">个人中心</router-link>
        </div>
      </div>
      
      <!-- 主要页面 -->
      <div class="test-section">
        <h2>主要页面</h2>
        <div class="test-links">
          <router-link to="/" class="test-link">首页</router-link>
          <router-link to="/home" class="test-link">首页(home)</router-link>
          <router-link to="/categories" class="test-link">商品分类</router-link>
          <router-link to="/products" class="test-link">商品列表</router-link>
          <router-link to="/search" class="test-link">搜索页面</router-link>
        </div>
      </div>
      
      <!-- 购物相关 -->
      <div class="test-section">
        <h2>购物相关</h2>
        <div class="test-links">
          <router-link to="/cart" class="test-link">购物车</router-link>
          <router-link to="/checkout" class="test-link">结算页面</router-link>
          <router-link to="/user/orders" class="test-link">我的订单</router-link>
        </div>
      </div>
      
      <!-- 动态路由测试 -->
      <div class="test-section">
        <h2>动态路由测试</h2>
        <div class="test-links">
          <router-link to="/category/1" class="test-link">分类详情(ID:1)</router-link>
          <router-link to="/product/1" class="test-link">商品详情(ID:1)</router-link>
          <router-link to="/user/orders/1" class="test-link">订单详情(ID:1)</router-link>
        </div>
      </div>
      
      <!-- 搜索测试 -->
      <div class="test-section">
        <h2>搜索测试</h2>
        <div class="test-links">
          <router-link to="/search?q=手机" class="test-link">搜索"手机"</router-link>
          <router-link to="/search?q=电脑&category=1" class="test-link">搜索"电脑"(分类1)</router-link>
          <router-link to="/hot-products" class="test-link">热门商品</router-link>
          <router-link to="/recommended-products" class="test-link">推荐商品</router-link>
        </div>
      </div>
    </div>
    
    <div class="current-route">
      <h3>当前路由信息</h3>
      <p><strong>路径:</strong> {{ $route.path }}</p>
      <p><strong>名称:</strong> {{ $route.name }}</p>
      <p><strong>参数:</strong> {{ JSON.stringify($route.params) }}</p>
      <p><strong>查询:</strong> {{ JSON.stringify($route.query) }}</p>
    </div>

    <el-button @click="testRoute('/user/profile')" type="primary">用户个人中心</el-button>
    <el-button @click="testRoute('/cart')" type="success">购物车</el-button>
    <el-button @click="testRoute('/checkout')" type="warning">结算页面</el-button>
    <el-button @click="testRoute('/user/orders')" type="info">订单管理</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.route-test {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 10px;
}

p {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
}

.test-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.test-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #409eff;
  margin-bottom: 15px;
  font-size: 18px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 5px;
}

.test-links {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.test-link {
  display: inline-block;
  padding: 10px 15px;
  background: #f8f9fa;
  color: #333;
  text-decoration: none;
  border-radius: 5px;
  border: 1px solid #e9ecef;
  transition: all 0.3s;
}

.test-link:hover {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.test-link.router-link-active {
  background: #67c23a;
  color: white;
  border-color: #67c23a;
}

.current-route {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid #409eff;
}

.current-route h3 {
  color: #409eff;
  margin-bottom: 15px;
}

.current-route p {
  text-align: left;
  margin: 5px 0;
  font-family: monospace;
  background: white;
  padding: 5px 10px;
  border-radius: 3px;
  border: 1px solid #e9ecef;
}
</style> 