<template>
  <div class="page-container">
    <div class="page-header">
      <h2>库位管理</h2>
    </div>
    <el-card shadow="hover">
      <p>库位管理页面 — 管理仓库内的货架/库位信息</p>
      <p style="color:#909399;">对应数据表：storage_location（8条记录：A区01号/A区02号/B区01号等）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
