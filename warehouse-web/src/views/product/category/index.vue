<template>
  <div class="page-container">
    <div class="page-header">
      <h2>商品分类</h2>
    </div>
    <el-card shadow="hover">
      <p>商品分类页面 — 管理商品分类目录树（树形结构，parent_id 关联）</p>
      <p style="color:#909399;">对应数据表：product_category（9条记录：电子产品/办公用品/生活用品/手机配件/电脑配件等）</p>
    </el-card>
  </div>
</template>

<script setup>
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 20px; }
.page-header h2 { margin: 0; font-size: 20px; color: #303133; }
</style>
