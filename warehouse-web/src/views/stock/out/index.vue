<template>
  <div class="page-container">
    <div class="page-header">
      <h2>出库管理</h2>
    </div>
    <el-card shadow="hover">
      <p>出库管理页面 — 管理销售出库、退货出库等出库单据</p>
      <p style="color:#909399;">对应数据表：stock_out + stock_out_detail（出库单号：OUT20240615001~OUT20240618004）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
