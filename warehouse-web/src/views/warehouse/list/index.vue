<!-- 仓库管理：仓库CRUD+库位管理集成 -->
<template>
  <div class="ct"><el-card class="card"><div class="tb"><el-button type="primary" @click="addW"><el-icon><Plus /></el-icon> 新增仓库</el-button></div>
    <el-table :data="whs" border stripe><el-table-column prop="id" label="ID" width="60" align="center"/><el-table-column prop="warehouseCode" label="仓库编码" min-width="130" align="center"/><el-table-column prop="warehouseName" label="仓库名称" min-width="140" align="center"/><el-table-column prop="address" label="地址" min-width="200" align="center" show-overflow-tooltip/><el-table-column prop="manager" label="管理员" min-width="130" align="center"/>
      <el-table-column label="状态" width="100" align="center"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="160" align="center"><template #default="{row}"><el-button size="small" type="warning" @click="editW(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="delW(row.id)"><el-icon><Delete /></el-icon></el-button></template></el-table-column></el-table></el-card>
    <el-dialog v-model="dw" :title="isWE?'编辑仓库':'新增仓库'" width="500px" @closed="fW={warehouseCode:'',warehouseName:'',address:'',manager:''}"><el-form :model="fW" label-width="100px"><el-form-item label="仓库编码"><el-input v-model="fW.warehouseCode"/></el-form-item><el-form-item label="仓库名称"><el-input v-model="fW.warehouseName"/></el-form-item><el-form-item label="地址"><el-input v-model="fW.address"/></el-form-item><el-form-item label="管理员"><el-input v-model="fW.manager"/></el-form-item></el-form><template #footer><el-button @click="dw=false">取消</el-button><el-button type="primary" :loading="sb" @click="saveW">{{isWE?'保存':'创建'}}</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue';import {ElMessage,ElMessageBox} from 'element-plus';import {Plus,Edit,Delete} from '@element-plus/icons-vue'
import {getWarehouseList,createWarehouse,updateWarehouse,deleteWarehouse} from '../../../api/index.js'
import {confirmDelete} from '../../../utils/confirm.js'
const whs=ref([]),sb=ref(false)
const loadWhs=async()=>{const r=await getWarehouseList();if(r.code===200)whs.value=r.data}
const dw=ref(false),isWE=ref(false),wId=ref(null),fW=reactive({warehouseCode:'',warehouseName:'',address:'',manager:''})
const addW=()=>{isWE.value=false;wId.value=null;dw.value=true}
const editW=(row)=>{isWE.value=true;wId.value=row.id;Object.assign(fW,{warehouseCode:row.warehouseCode,warehouseName:row.warehouseName,address:row.address,manager:row.manager});dw.value=true}
const saveW=async()=>{sb.value=true;try{const d={...fW};if(isWE.value)d.id=wId.value;const r=await(isWE.value?updateWarehouse(d):createWarehouse(d));if(r.code===200){ElMessage.success(r.msg);dw.value=false;loadWhs()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const delW=async(id)=>{try{await confirmDelete('确定删除该仓库？');const r=await deleteWarehouse(id);if(r.code===200){ElMessage.success('已删除');loadWhs()}else ElMessage.error(r.msg)}catch(e){}}
onMounted(()=>loadWhs())
</script>
<style scoped>.ct{padding:0}.card{margin-bottom:0}.tb{margin-bottom:16px;display:flex;align-items:center}</style>
