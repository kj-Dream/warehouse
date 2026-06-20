<template>
  <div class="page-container">
    <div class="page-header">
      <h2>库存统计</h2>
    </div>
    <el-card shadow="hover">
      <p>库存统计报表 — 按仓库/分类统计库存金额与数量分布</p>
      <p style="color:#909399;">对应数据表：inventory + product + warehouse（饼图/柱状图）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
