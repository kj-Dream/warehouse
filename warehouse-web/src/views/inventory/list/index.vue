<template>
  <div class="page-container">
    <div class="page-header">
      <h2>库存查询</h2>
    </div>
    <el-card shadow="hover">
      <p>库存查询页面 — 按商品/仓库/库位查询实时库存数量</p>
      <p style="color:#909399;">对应数据表：inventory（12条记录，乐观锁 version 字段）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
