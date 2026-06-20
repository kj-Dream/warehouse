<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品列表</h2>
    </div>
    <el-card shadow="hover">
      <p>商品列表页面 — 管理所有商品信息（编码/名称/分类/成本价/售价/库存上下限）</p>
      <p style="color:#909399;">对应数据表：product（10条记录：USB数据线、无线鼠标、机械键盘、A4打印纸等）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
