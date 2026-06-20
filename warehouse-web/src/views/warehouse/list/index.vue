<template>
  <div class="page-container">
    <div class="page-header">
      <h2>仓库列表</h2>
    </div>
    <el-card shadow="hover">
      <p>仓库列表页面 — 管理仓库信息（编码/名称/地址/负责人）</p>
      <p style="color:#909399;">对应数据表：warehouse（主仓库/副仓库/临时仓库）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
