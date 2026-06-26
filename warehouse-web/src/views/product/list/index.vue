<!-- 商品管理：商品列表+CRUD+分类筛选+库存阈值 -->
<template>
  <div class="ct">
    <el-card class="sc"><el-form :inline="true" :model="s">
      <el-form-item label="商品名称"><el-input v-model="s.productName" placeholder="名称/编码" clearable @keyup.enter="load"/></el-form-item>
      <el-form-item label="分类"><el-select v-model="s.categoryId" placeholder="全部" clearable style="width:140px"><el-option v-for="c in catList" :key="c.id" :label="c.categoryName" :value="c.id"/></el-select></el-form-item>
      <el-form-item label="状态"><el-select v-model="s.status" placeholder="全部" clearable style="width:100px"><el-option label="启用" :value="1"/><el-option label="禁用" :value="0"/></el-select></el-form-item>
      <el-form-item><el-button type="primary" @click="load"><el-icon><Search /></el-icon></el-button><el-button @click="s.productName='';s.categoryId=null;s.status=null;s.pageNum=1;load()"><el-icon><Refresh /></el-icon></el-button></el-form-item>
    </el-form></el-card>
    <el-card class="tc"><div class="tb"><el-button type="primary" @click="add"><el-icon><Plus /></el-icon> 新增商品</el-button></div>
      <el-table :data="list" border stripe v-loading="ld">
        <el-table-column prop="id" label="ID" width="60" align="center"/>
        <el-table-column prop="productCode" label="编码" width="110"/>
        <el-table-column prop="productName" label="商品名称" min-width="180"/>
        <el-table-column label="分类" min-width="130" align="center"><template #default="{row}">{{cats[row.categoryId]||'-'}}</template></el-table-column>
        <el-table-column prop="unit" label="单位" width="70" align="center"/>
        <el-table-column prop="costPrice" label="成本价" width="110" align="center"/>
        <el-table-column prop="salePrice" label="售价" width="110" align="center"/>
        <el-table-column label="库存阈值" width="130" align="center"><template #default="{row}">{{row.minStock||0}}~{{row.maxStock||0}}</template></el-table-column>
        <el-table-column label="状态" width="100" align="center"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="200" align="center"><template #default="{row}"><el-button size="small" type="warning" @click="edit(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="del(row)"><el-icon><Delete /></el-icon></el-button></template></el-table-column>
      </el-table>
      <div class="pg"><el-pagination v-model:current-page="s.pageNum" v-model:page-size="s.pageSize" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next,jumper" @size-change="load" @current-change="load"/></div>
    </el-card>
    <el-dialog v-model="dv" :title="isE?'编辑商品':'新增商品'" width="560px" :key="edId||'add'" @closed="rf">
      <el-form ref="fr" :model="f" label-width="100px">
        <el-row :gutter="16"><el-col :span="12"><el-form-item label="编码"><el-input v-model="fCode"/></el-form-item></el-col><el-col :span="12"><el-form-item label="名称"><el-input v-model="fName"/></el-form-item></el-col></el-row>
        <el-row :gutter="16"><el-col :span="12"><el-form-item label="分类"><el-select v-model="fCat" style="width:100%"><el-option v-for="c in catList" :key="c.id" :label="c.categoryName" :value="c.id"/></el-select></el-form-item></el-col><el-col :span="12"><el-form-item label="单位"><el-select v-model="fUnit" style="width:100%"><el-option v-for="u in ['个','条','台','支','包','瓶','吨','千克','米','箱','桶','卷','把','双']" :key="u" :label="u" :value="u"/></el-select></el-form-item></el-col></el-row>
        <el-row :gutter="16"><el-col :span="8"><el-form-item label="成本价"><el-input-number v-model="fCost" :precision="2" :min="0" controls-position="right" style="width:100%"/></el-form-item></el-col><el-col :span="8"><el-form-item label="售价"><el-input-number v-model="fSale" :precision="2" :min="0" controls-position="right" style="width:100%"/></el-form-item></el-col></el-row>
        <el-row :gutter="16"><el-col :span="8"><el-form-item label="最低库存"><el-input-number v-model="fMin" :min="0" controls-position="right" style="width:100%"/></el-form-item></el-col><el-col :span="8"><el-form-item label="最高库存"><el-input-number v-model="fMax" :min="0" controls-position="right" style="width:100%"/></el-form-item></el-col><el-col :span="8"><el-form-item label="状态"><el-switch v-model="fSta" :active-value="1" :inactive-value="0"/></el-form-item></el-col></el-row>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" :loading="sb" @click="save">{{isE?'保存':'创建'}}</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted,nextTick} from 'vue';import {ElMessage,ElMessageBox} from 'element-plus';import {Search,Refresh,Plus,Edit,Delete} from '@element-plus/icons-vue'
import {getProductPage,createProduct,updateProduct,deleteProduct,getCategoryTree} from '../../../api/index.js'
const s=reactive({pageNum:1,pageSize:50,productName:'',categoryId:null,status:null}),list=ref([]),total=ref(0),ld=ref(false),catList=ref([]),cats=ref({})
const load=async()=>{ld.value=true;try{const r=await getProductPage(s);if(r.code===200){list.value=r.data.list;total.value=r.data.total}}finally{ld.value=false}}
const loadCats=async()=>{const r=await getCategoryTree();if(r.code===200){catList.value=r.data;const m={};r.data.forEach(c=>{m[c.id]=c.categoryName;if(c.children)c.children.forEach(sc=>m[sc.id]=sc.categoryName)});cats.value=m}}
const dv=ref(false),isE=ref(false),edId=ref(null),sb=ref(false)
const fCode=ref(''),fName=ref(''),fCat=ref(null),fUnit=ref('个'),fCost=ref(0),fSale=ref(0),fMin=ref(0),fMax=ref(0),fSta=ref(1)
const add=()=>{isE.value=false;edId.value=null;fCode.value='';fName.value='';fCat.value=null;fUnit.value='个';fCost.value=0;fSale.value=0;fMin.value=0;fMax.value=0;fSta.value=1;nextTick(()=>{dv.value=true})}
const edit=(row)=>{isE.value=true;edId.value=row.id;fCode.value=row.productCode;fName.value=row.productName;fCat.value=row.categoryId;fUnit.value=row.unit;fCost.value=Number(row.costPrice)||0;fSale.value=Number(row.salePrice)||0;fMin.value=Number(row.minStock)||0;fMax.value=Number(row.maxStock)||0;fSta.value=row.status;nextTick(()=>{dv.value=true})}
const rf=()=>{edId.value=null}
const save=async()=>{sb.value=true;try{const d={productCode:fCode.value,productName:fName.value,categoryId:fCat.value,unit:fUnit.value,costPrice:fCost.value,salePrice:fSale.value,minStock:fMin.value,maxStock:fMax.value,status:fSta.value};if(isE.value)d.id=edId.value;const r=await(isE.value?updateProduct(d):createProduct(d));if(r.code===200){ElMessage.success(r.msg);dv.value=false;load()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const del=async(row)=>{try{await ElMessageBox.confirm('确定删除「'+row.productName+'」吗？','删除确认',{type:'warning'});const r=await deleteProduct(row.id);if(r.code===200){ElMessage.success('已删除');load()}else ElMessage.error(r.msg)}catch(e){}}
onMounted(()=>{load();loadCats()})
</script>
<style scoped>.ct{padding:0}.sc,.tc{margin-bottom:16px}.tb{margin-bottom:16px}.pg{margin-top:16px;display:flex;justify-content:flex-end}</style>
