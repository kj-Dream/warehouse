<!-- 商品分类管理：左侧表格+右侧统计面板 -->
<template>
  <div class="ct"><el-row :gutter="16"><el-col :span="17"><el-card><div class="tb"><el-button type="primary" @click="add(null)"><el-icon><Plus /></el-icon> 新增一级分类</el-button></div>
        <el-table :data="tree" border stripe row-key="id"><el-table-column prop="id" label="ID" width="80" align="center"/><el-table-column prop="categoryName" label="分类名称" min-width="140"/><el-table-column prop="categoryCode" label="分类编码" min-width="140"/>
          <el-table-column label="操作" width="220" align="center"><template #default="{row}"><el-button size="small" type="primary" @click="add(row.id)">添加子分类</el-button><el-button size="small" type="warning" @click="edit(row)"><el-icon><Edit /></el-icon></el-button><el-button size="small" type="danger" @click="del(row.id)"><el-icon><Delete /></el-icon></el-button></template></el-table-column></el-table></el-card></el-col>
    <el-col :span="7"><el-card><template #header>分类统计</template><div ref="pieChart" class="chart"></div>
        <el-divider /><el-tag v-for="c in tree" :key="c.id" size="small" style="margin:4px 4px 0 0">{{ c.categoryName }}</el-tag></el-card></el-col></el-row>
    <el-dialog v-model="dv" :title="isE?'编辑分类':'新增分类'" width="450px" @closed="rf"><el-form :model="f" label-width="100px"><el-form-item label="分类名称"><el-input v-model="f.categoryName" placeholder="如：电子产品"/></el-form-item><el-form-item label="分类编码"><el-input v-model="f.categoryCode" placeholder="如：ELEC"/></el-form-item><el-form-item label="父级分类"><el-input :model-value="pidName" disabled/></el-form-item></el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" :loading="sb" @click="save">{{isE?'保存':'创建'}}</el-button></template></el-dialog>
  </div>
</template>
<script setup>
import {ref,reactive,onMounted,nextTick} from 'vue';import {ElMessage,ElMessageBox} from 'element-plus';import {Plus,Edit,Delete} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {getCategoryTree,createCategory,updateCategory,deleteCategory} from '../../../api/index.js'
import {confirmDelete} from '../../../utils/confirm.js'
const tree=ref([])
const pieChart=ref(null)
const load=async()=>{const r=await getCategoryTree();if(r.code===200){tree.value=r.data;await nextTick();drawChart()}}
const drawChart=()=>{if(!pieChart.value)return;const c=echarts.init(pieChart.value);const topLevel=tree.value.length;let subLevel=0;tree.value.forEach(x=>{if(x.children)subLevel+=x.children.length})
  c.setOption({tooltip:{trigger:'item'},series:[{type:'pie',radius:['45%','70%'],center:['50%','45%'],data:[{value:topLevel,name:'一级分类'},{value:subLevel,name:'二级分类'}],label:{show:true,formatter:'{b}\n{d}%'}}],color:['#409EFF','#67C23A']})
  window.addEventListener('resize',()=>c.resize())
}
const findName=(id)=>{for(const c of tree.value){if(c.id===id)return c.categoryName;if(c.children){const s=c.children.find(x=>x.id===id);if(s)return s.categoryName}}return'-'}
const dv=ref(false),isE=ref(false),edId=ref(null),pidId=ref(null),pidName=ref('顶级分类'),sb=ref(false),f=ref({categoryName:'',categoryCode:''})
const add=(pid)=>{isE.value=false;edId.value=null;pidId.value=pid;pidName.value=pid?findName(pid):'顶级分类';dv.value=true}
const edit=(row)=>{isE.value=true;edId.value=row.id;f.value.categoryName=row.categoryName;f.value.categoryCode=row.categoryCode;pidId.value=null;pidName.value='（不变）';dv.value=true}
const rf=()=>{f.value={categoryName:'',categoryCode:''};edId.value=null}
const save=async()=>{sb.value=true;try{const d={categoryName:f.value.categoryName,categoryCode:f.value.categoryCode};if(isE.value)d.id=edId.value;else d.parentId=pidId.value||0;const r=await(isE.value?updateCategory(d):createCategory(d));if(r.code===200){ElMessage.success(r.msg);dv.value=false;load()}else ElMessage.error(r.msg)}finally{sb.value=false}}
const del=async(id)=>{try{await confirmDelete('确定删除该分类吗？');const r=await deleteCategory(id);if(r.code===200){ElMessage.success('已删除');load()}else ElMessage.error(r.msg)}catch(e){}}
onMounted(()=>load())
</script>
<style scoped>.ct{padding:0}.tb{margin-bottom:16px}.chart{width:100%;height:260px}</style>
