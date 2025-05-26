<template>
  <div class="analytics-dashboard">
    <el-card>
      <template #header>
        <span>数据分析</span>
      </template>
      
      <div class="analytics-content">
        <p>数据分析功能开发中...</p>
        <div class="charts-grid">
          <div class="chart-item">
            <h4>销售趋势</h4>
            <div class="chart-placeholder">图表占位符</div>
          </div>
          <div class="chart-item">
            <h4>商品销量排行</h4>
            <div class="chart-placeholder">图表占位符</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElCard } from 'element-plus'
</script>

<style scoped>
.analytics-content {
  padding: 20px 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.chart-item h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.chart-placeholder {
  height: 200px;
  background: #f5f7fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}
</style> 