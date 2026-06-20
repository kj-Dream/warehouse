<!-- 仓库管理：仓库CRUD+库位管理集成 -->
<template>
  <div class="ct"><el-card class="card"><div class="tb"><el-button type="primary" @click="addW"><el-icon><Plus /></el-icon> 新增仓库</el-button></div>
    <el-table :data="whs" border stripe><el-table-column prop="id" label="ID" width="60" align="center"/><el-table-column prop="warehouseCode" label="仓库编码" width="120"/><el-table-column prop="warehouseName" label="仓库名称" width="140"/><el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip/><el-table-column prop="manager" label="管理员" width="120" align="center"/>
      <el-table-column label="操作" width="180" align="center"><template #default="{row}"><el-button size="small" type="warning" @click="editW(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="delW(row.id)"><el-icon><Delete /></el-icon></el-button></template></el-table-column></el-table></el-card>
    <el-card class="card" style="margin-top:16px"><div class="tb"><span style="font-size:16px;font-weight:600">库位管理</span><el-select v-model="locWhId" placeholder="筛选仓库" clearable style="width:180px;margin-left:16px" @change="loadLocs"><el-option v-for="w in whs" :key="w.id" :label="w.warehouseName" :value="w.id"/></el-select><el-button type="primary" style="margin-left:16px" @click="addL"><el-icon><Plus /></el-icon> 新增库位</el-button></div>
      <el-table :data="locs" border stripe><el-table-column prop="id" label="ID" width="60" align="center"/><el-table-column prop="warehouseName" label="所属仓库" width="140"/><el-table-column prop="locationCode" label="库位编码" width="140"/><el-table-column prop="locationName" label="库位名称" min-width="200"/>
        <el-table-column label="操作" width="180" align="center"><template #default="{row}"><el-button size="small" type="warning" @click="editL(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="delL(row.id)"><el-icon><Delete /></el-icon></el-button></template></el-table-column></el-table></el-card>
    <el-dialog v-model="dw" :title="isWE?'编辑仓库':'新增仓库'" width="500px" @closed="fW={warehouseCode:'',warehouseName:'',address:'',manager:''}"><el-form :model="fW" label-width="100px"><el-form-item label="仓库编码"><el-input v-model="fW.warehouseCode"/></el-form-item><el-form-item label="仓库名称"><el-input v-model="fW.warehouseName"/></el-form-item><el-form-item label="地址"><el-input v-model="fW.address"/></el-form-item><el-form-item label="管理员"><el-input v-model="fW.manager"/></el-form-item></el-form><template #footer><el-button @click="dw=false">取消</el-button><el-button type="primary" :loading="sb" @click="saveW">{{isWE?'保存':'创建'}}</el-button></template></el-dialog>
    <el-dialog v-model="dl" :title="isLE?'编辑库位':'新增库位'" width="450px" @closed="fL={warehouseId:null,locationCode:'',locationName:''}"><el-form :model="fL" label-width="100px"><el-form-item label="所属仓库"><el-select v-model="fL.warehouseId" style="width:100%" :disabled="isLE"><el-option v-for="w in whs" :key="w.id" :label="w.warehouseName" :value="w.id"/></el-select></el-form-item><el-form-item label="库位编码"><el-input v-model="fL.locationCode"/></el-form-item><el-form-item label="库位名称"><el-input v-model="fL.locationName"/></el-form-item></el-form><template #footer><el-button @click="dl=false">取消</el-button><el-button type="primary" :loading="sb" @click="saveL">{{isLE?'保存':'创建'}}</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue';import {ElMessage,ElMessageBox} from 'element-plus';import {Plus,Edit,Delete} from '@element-plus/icons-vue'
import {getWarehouseList,createWarehouse,updateWarehouse,deleteWarehouse,getLocationList,createLocation,updateLocation,deleteLocation} from '../../../api/index.js'
const whs=ref([]),locs=ref([]),locWhId=ref(null),sb=ref(false)
const loadWhs=async()=>{const r=await getWarehouseList();if(r.code===200)whs.value=r.data}
const loadLocs=async()=>{const r=await getLocationList(locWhId.value);if(r.code===200)locs.value=r.data}
const dw=ref(false),isWE=ref(false),wId=ref(null),fW=reactive({warehouseCode:'',warehouseName:'',address:'',manager:''})
const addW=()=>{isWE.value=false;wId.value=null;dw.value=true}
const editW=(row)=>{isWE.value=true;wId.value=row.id;Object.assign(fW,{warehouseCode:row.warehouseCode,warehouseName:row.warehouseName,address:row.address,manager:row.manager});dw.value=true}
const saveW=async()=>{sb.value=true;try{const d={...fW};if(isWE.value)d.id=wId.value;const r=await(isWE.value?updateWarehouse(d):createWarehouse(d));if(r.code===200){ElMessage.success(r.msg);dw.value=false;loadWhs()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const delW=async(id)=>{try{await ElMessageBox.confirm('确定删除该仓库？','删除确认',{type:'warning'});const r=await deleteWarehouse(id);if(r.code===200){ElMessage.success('已删除');loadWhs()}else ElMessage.error(r.msg)}catch(e){}}
const dl=ref(false),isLE=ref(false),lId=ref(null),fL=reactive({warehouseId:null,locationCode:'',locationName:''})
const addL=()=>{isLE.value=false;lId.value=null;dl.value=true}
const editL=(row)=>{isLE.value=true;lId.value=row.id;Object.assign(fL,{warehouseId:whs.value.find(w=>w.warehouseName===row.warehouseName)?.id,locationCode:row.locationCode,locationName:row.locationName});dl.value=true}
const saveL=async()=>{sb.value=true;try{const d={...fL};if(isLE.value)d.id=lId.value;const r=await(isLE.value?updateLocation(d):createLocation(d));if(r.code===200){ElMessage.success(r.msg);dl.value=false;loadLocs();loadWhs()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const delL=async(id)=>{try{await ElMessageBox.confirm('确定删除该库位？','删除确认',{type:'warning'});const r=await deleteLocation(id);if(r.code===200){ElMessage.success('已删除');loadLocs()}else ElMessage.error(r.msg)}catch(e){}}
onMounted(()=>{loadWhs();loadLocs()})
</script>
<style scoped>.ct{padding:0}.card{margin-bottom:0}.tb{margin-bottom:16px;display:flex;align-items:center}</style>
