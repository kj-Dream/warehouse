<!-- 库存查询：实时库存+状态标签+仓库筛选 -->
<template>
  <div class="ct"><el-card class="sc"><el-form :inline="true" :model="s"><el-form-item label="商品名称"><el-input v-model="s.productName" placeholder="商品名称" clearable @keyup.enter="load"/></el-form-item><el-form-item label="仓库"><el-select v-model="s.warehouseId" placeholder="全部" clearable style="width:160px"><el-option v-for="w in whs" :key="w.id" :label="w.warehouseName" :value="w.id"/></el-select></el-form-item><el-form-item><el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button><el-button @click="s.productName='';s.warehouseId=null;s.pageNum=1;load()"><el-icon><Refresh /></el-icon></el-button></el-form-item></el-form></el-card>
    <el-card class="tc"><el-table :data="list" border stripe v-loading="ld">
      <el-table-column prop="product_name" label="商品名称" min-width="130"/><el-table-column prop="product_code" label="商品编码" width="110"/><el-table-column prop="warehouse_name" label="仓库" min-width="120"/><el-table-column prop="location_name" label="库位" min-width="140"/><el-table-column prop="quantity" label="当前库存" width="100" align="center"/>
      <el-table-column label="库存状态" min-width="160" align="center"><template #default="{row}"><el-tag v-if="row.quantity<=row.min_stock" type="danger">低于最低({{row.min_stock}})</el-tag><el-tag v-else-if="row.quantity>=row.max_stock" type="warning">高于最高({{row.max_stock}})</el-tag><el-tag v-else type="success">正常</el-tag></template></el-table-column>
    </el-table><div class="pg"><el-pagination v-model:current-page="s.pageNum" v-model:page-size="s.pageSize" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next,jumper" @size-change="load" @current-change="load"/></div></el-card>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue';import {Search,Refresh} from '@element-plus/icons-vue'
import {getInventoryPage,getWarehouseList} from '../../../api/index.js'
const s=reactive({pageNum:1,pageSize:10,productName:'',warehouseId:null}),list=ref([]),total=ref(0),ld=ref(false),whs=ref([])
const load=async()=>{ld.value=true;try{const r=await getInventoryPage(s);if(r.code===200){list.value=r.data.list;total.value=r.data.total}}finally{ld.value=false}}
onMounted(async()=>{const r=await getWarehouseList();if(r.code===200)whs.value=r.data;load()})
</script>
<style scoped>.ct{padding:0}.sc,.tc{margin-bottom:16px}.pg{margin-top:16px;display:flex;justify-content:flex-end}</style>
