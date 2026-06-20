<template>
  <div class="page-container">
    <div class="page-header">
      <h2>库存预警</h2>
    </div>
    <el-card shadow="hover">
      <p>库存预警页面 — 显示库存低于 min_stock 或高于 max_stock 的商品</p>
      <p style="color:#909399;">对应数据表：inventory JOIN product（min_stock / max_stock 比对）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
