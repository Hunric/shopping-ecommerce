<template>
  <div class="product-form">
    <el-card>
      <template #header>
        <span>添加商品</span>
      </template>
      
      <div class="form-content">
        <p>商品表单功能开发中...</p>
        <el-form label-width="120px">
          <el-form-item label="商品名称">
            <el-input placeholder="请输入商品名称" />
          </el-form-item>
          <el-form-item label="商品价格">
            <el-input placeholder="请输入商品价格" />
          </el-form-item>
          <el-form-item label="商品描述">
            <el-input type="textarea" :rows="4" placeholder="请输入商品描述" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary">保存</el-button>
            <el-button>取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElCard, ElForm, ElFormItem, ElInput, ElButton } from 'element-plus'
</script>

<style scoped>
.form-content {
  padding: 20px 0;
}
</style> 