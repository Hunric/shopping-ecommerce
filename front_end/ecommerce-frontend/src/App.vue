<!--
/**
 * 电商平台前端应用程序根组件
 * 
 * @description 这是整个电商平台前端应用的根组件，负责提供应用程序的基础结构和全局样式。
 *              所有的页面组件都将通过router-view在此组件内渲染。
 * 
 * @features
 * - 作为应用程序的根容器
 * - 提供路由视图渲染插槽
 * - 定义全局样式基础
 * - 导入商家登录组件（可能用于某些特殊场景）
 * 
 * @structure
 * - template: 包含router-view用于渲染当前路由对应的组件
 * - script: 使用Vue 3 Composition API设置
 * - style: 全局样式定义区域
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link https://router.vuejs.org/} Vue Router文档
 */
-->

<script setup lang="ts">
import MerchantLogin from './views/merchant/login/index.vue'
</script>

<template>
  <router-view />
</template>

<style>
/* 全局样式可以在这里添加 */
</style>
