<template>
  <div class="page-container">
    <div class="page-header">
      <h2>入库统计</h2>
    </div>
    <el-card shadow="hover">
      <p>入库统计报表 — 按时间/供应商/商品维度统计入库数据</p>
      <p style="color:#909399;">对应数据表：stock_in + stock_in_detail（图表+汇总）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
