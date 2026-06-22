<!-- 客户管理：CRUD+搜索+分页 -->
<template>
  <div class="ct">
    <el-card class="sc"><el-form :inline="true" :model="s">
      <el-form-item label="客户名称"><el-input v-model="s.customerName" placeholder="客户名称" clearable @keyup.enter="load"/></el-form-item>
      <el-form-item label="状态"><el-select v-model="s.status" placeholder="全部" clearable style="width:100px"><el-option label="启用" :value="1"/><el-option label="禁用" :value="0"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button><el-button @click="s.customerName='';s.status=null;s.pageNum=1;load()"><el-icon><Refresh /></el-icon></el-button></el-form-item>
    </el-form></el-card>
    <el-card class="tc"><div class="tb"><el-button type="primary" @click="add"><el-icon><Plus /></el-icon> 新增客户</el-button></div>
      <el-table :data="list" border stripe v-loading="ld">
        <el-table-column prop="id" label="ID" width="60" align="center"/>
        <el-table-column prop="customerCode" label="编码" width="110"/>
        <el-table-column prop="customerName" label="客户名称" min-width="160"/>
        <el-table-column prop="contact" label="联系人" width="100" align="center"/>
        <el-table-column prop="phone" label="电话" width="130" align="center"/>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip/>
        <el-table-column label="状态" width="80" align="center"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="180" align="center"><template #default="{row}"><el-button size="small" type="warning" @click="edit(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="del(row.id)"><el-icon><Delete /></el-icon></el-button></template></el-table-column>
      </el-table>
      <div class="pg"><el-pagination v-model:current-page="s.pageNum" v-model:page-size="s.pageSize" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next,jumper" @size-change="load" @current-change="load"/></div>
    </el-card>
    <el-dialog v-model="dv" :title="isE?'编辑客户':'新增客户'" width="500px" @closed="rf">
      <el-form :model="f" label-width="100px">
        <el-row :gutter="16"><el-col :span="12"><el-form-item label="客户编码"><el-input v-model="f.customerCode"/></el-form-item></el-col><el-col :span="12"><el-form-item label="客户名称"><el-input v-model="f.customerName"/></el-form-item></el-col></el-row>
        <el-row :gutter="16"><el-col :span="12"><el-form-item label="联系人"><el-input v-model="f.contact"/></el-form-item></el-col><el-col :span="12"><el-form-item label="电话"><el-input v-model="f.phone"/></el-form-item></el-col></el-row>
        <el-form-item label="地址"><el-input v-model="f.address"/></el-form-item>
        <el-form-item label="状态"><el-switch v-model="f.status" :active-value="1" :inactive-value="0"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" :loading="sb" @click="save">{{isE?'保存':'创建'}}</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted} from 'vue';import {ElMessage,ElMessageBox} from 'element-plus';import {Search,Refresh,Plus,Edit,Delete} from '@element-plus/icons-vue'
import {confirmDelete} from '../../../utils/confirm.js'
const s=reactive({pageNum:1,pageSize:10,customerName:'',status:null}),list=ref([]),total=ref(0),ld=ref(false)
const load=async()=>{ld.value=true;try{const r=await request({url:'/customer/page',method:'get',params:s});if(r.code===200){list.value=r.data.list;total.value=r.data.total}}finally{ld.value=false}}
import request from '../../../api/auth.js'
const dv=ref(false),isE=ref(false),edId=ref(null),sb=ref(false),f=reactive({customerCode:'',customerName:'',contact:'',phone:'',address:'',status:1})
const add=()=>{isE.value=false;edId.value=null;dv.value=true}
const edit=(row)=>{isE.value=true;edId.value=row.id;Object.assign(f,{customerCode:row.customerCode,customerName:row.customerName,contact:row.contact,phone:row.phone,address:row.address,status:row.status});dv.value=true}
const rf=()=>{Object.assign(f,{customerCode:'',customerName:'',contact:'',phone:'',address:'',status:1});edId.value=null}
const save=async()=>{sb.value=true;try{const d={...f};if(isE.value)d.id=edId.value;const r=await request({url:'/customer',method:isE.value?'put':'post',data:d});if(r.code===200){ElMessage.success(r.msg);dv.value=false;load()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const del=async(id)=>{try{await confirmDelete('确定删除该客户吗？');const r=await request({url:'/customer/'+id,method:'delete'});if(r.code===200){ElMessage.success('已删除');load()}else ElMessage.error(r.msg)}catch(e){}}
onMounted(()=>load())
</script>
<style scoped>.ct{padding:0}.sc,.tc{margin-bottom:16px}.tb{margin-bottom:16px}.pg{margin-top:16px;display:flex;justify-content:flex-end}</style>
