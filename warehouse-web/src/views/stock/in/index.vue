<template>
  <div class="page-container">
    <div class="page-header">
      <h2>入库管理</h2>
    </div>
    <el-card shadow="hover">
      <p>入库管理页面 — 管理采购入库、退货入库等入库单据</p>
      <p style="color:#909399;">对应数据表：stock_in + stock_in_detail（入库单号：IN20240615001~IN20240618004）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
