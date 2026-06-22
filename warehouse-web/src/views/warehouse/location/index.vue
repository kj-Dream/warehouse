<!-- 库位管理：选仓库查看/管理库位 -->
<template>
  <div class="ct"><el-card><div class="tb"><el-select v-model="whId" placeholder="选择仓库" style="width:200px" @change="load"><el-option v-for="w in whs" :key="w.id" :label="w.warehouseName" :value="w.id"/></el-select><el-button type="primary" style="margin-left:12px" @click="add" :disabled="!whId"><el-icon><Plus /></el-icon> 新增库位</el-button></div>
    <el-table :data="list" border stripe v-loading="ld"><el-table-column prop="id" label="ID" width="60" align="center"/><el-table-column label="仓库" width="140"><template #default="{row}">{{ row.warehouseName || getWhName(row.warehouseId) }}</template></el-table-column><el-table-column prop="locationCode" label="库位编码" min-width="150"/><el-table-column prop="locationName" label="库位名称" min-width="200"/><el-table-column label="状态" width="100" align="center"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column><el-table-column label="操作" width="160" align="center"><template #default="{row}"><el-button size="small" type="warning" @click="edit(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="del(row.id)"><el-icon><Delete /></el-icon></el-button></template></el-table-column></el-table>
  </el-card>
    <el-dialog v-model="dw" :title="isE?'编辑库位':'新增库位'" width="450px" @closed="rf"><el-form :model="f" label-width="100px"><el-form-item label="所属仓库"><el-select v-model="f.warehouseId" style="width:100%" :disabled="isE"><el-option v-for="w in whs" :key="w.id" :label="w.warehouseName" :value="w.id"/></el-select></el-form-item><el-form-item label="库位编码"><el-input v-model="f.locationCode" placeholder="如：A01"/></el-form-item><el-form-item label="库位名称"><el-input v-model="f.locationName" placeholder="如：A区01号货架"/></el-form-item><el-form-item label="状态"><el-switch v-model="f.status" :active-value="1" :inactive-value="0"/></el-form-item></el-form><template #footer><el-button @click="dw=false">取消</el-button><el-button type="primary" :loading="sb" @click="save">{{isE?'保存':'创建'}}</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue';import {ElMessage,ElMessageBox} from 'element-plus';import {Plus,Edit,Delete} from '@element-plus/icons-vue'
import {getWarehouseList,getLocationList,createLocation,updateLocation,deleteLocation} from '../../../api/index.js'
import {confirmDelete} from '../../../utils/confirm.js'
const whs=ref([]),list=ref([]),ld=ref(false),whId=ref(null)
const loadWhs=async()=>{const r=await getWarehouseList();if(r.code===200)whs.value=r.data}
const load=async()=>{ld.value=true;try{const r=await getLocationList(whId.value);if(r.code===200)list.value=r.data}finally{ld.value=false}}
const getWhName=(wid)=>{const w=whs.value.find(x=>x.id===wid);return w?w.warehouseName:'-'}
const dw=ref(false),isE=ref(false),eId=ref(null),sb=ref(false),f=reactive({warehouseId:null,locationCode:'',locationName:'',status:1})
const add=()=>{isE.value=false;eId.value=null;f.warehouseId=whId.value;dw.value=true}
const edit=(row)=>{isE.value=true;eId.value=row.id;Object.assign(f,{warehouseId:row.warehouseId||whId.value,locationCode:row.locationCode,locationName:row.locationName,status:row.status!=null?row.status:1});dw.value=true}
const rf=()=>{f.warehouseId=null;f.locationCode='';f.locationName='';f.status=1;eId.value=null}
const save=async()=>{sb.value=true;try{const d={...f};if(isE.value)d.id=eId.value;const r=await(isE.value?updateLocation(d):createLocation(d));if(r.code===200){ElMessage.success(r.msg);dw.value=false;load()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const del=async(id)=>{try{await confirmDelete('确定删除该库位？');const r=await deleteLocation(id);if(r.code===200){ElMessage.success('已删除');load()}else ElMessage.error(r.msg)}catch(e){}}
onMounted(async()=>{await loadWhs();load()})
</script>
<style scoped>.ct{padding:0}.tb{margin-bottom:16px;display:flex;align-items:center}</style>
