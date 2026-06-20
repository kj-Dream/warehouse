<!-- 库存预警：低于最低/高于最高库存的商品列表 -->
<template>
  <div class="ct"><el-card><div class="hd"><el-icon :size="20" color="#f5222d"><Warning /></el-icon><span style="font-size:18px;font-weight:600;margin-left:8px">库存预警列表</span></div>
    <el-table :data="list" border stripe v-loading="ld" style="margin-top:16px">
      <el-table-column prop="product_name" label="商品名称" min-width="130"/><el-table-column prop="product_code" label="商品编码" width="110"/><el-table-column prop="warehouse_name" label="仓库" min-width="120"/><el-table-column prop="location_name" label="库位" min-width="140"/><el-table-column prop="quantity" label="当前库存" width="100" align="center"/>
      <el-table-column label="预警类型" width="160" align="center"><template #default="{row}"><el-tag :type="row.warn_type==='低于最低库存'?'danger':'warning'">{{row.warn_type}}</el-tag></template></el-table-column>
      <el-table-column label="阈值" width="140" align="center"><template #default="{row}">{{row.min_stock}}~{{row.max_stock}}</template></el-table-column>
    </el-table><div class="pg"><el-pagination v-model:current-page="s.pageNum" v-model:page-size="s.pageSize" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next,jumper" @size-change="load" @current-change="load"/></div>
  </el-card></div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue';import {Warning} from '@element-plus/icons-vue';import {getInventoryWarning} from '../../../api/index.js'
const s=reactive({pageNum:1,pageSize:10}),list=ref([]),total=ref(0),ld=ref(false)
const load=async()=>{ld.value=true;try{const r=await getInventoryWarning(s);if(r.code===200){list.value=r.data.list;total.value=r.data.total}}finally{ld.value=false}}
onMounted(()=>load())
</script>
<style scoped>.ct{padding:0}.hd{display:flex;align-items:center}.pg{margin-top:16px;display:flex;justify-content:flex-end}</style>
