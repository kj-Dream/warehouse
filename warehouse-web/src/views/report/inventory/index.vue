<!-- 库存统计 (3.2.10) -->
<template>
  <div class="ct">
    <el-row :gutter="20"><el-col :span="8"><el-card class="sc"><div class="num">{{stats.productCount||0}}</div><div class="lbl">商品种类</div></el-card></el-col>
      <el-col :span="8"><el-card class="sc"><div class="num">{{stats.warehouseCount||0}}</div><div class="lbl">仓库数量</div></el-card></el-col>
      <el-col :span="8"><el-card class="sc warn"><div class="num">{{stats.warningCount||0}}</div><div class="lbl">预警商品</div></el-card></el-col></el-row>
    <el-row :gutter="20" style="margin-top:20px"><el-col :span="24"><el-card><template #header>仓库库存分布</template><div ref="pie" class="ch"></div></el-card></el-col></el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import * as echarts from 'echarts'; import { getDashboard } from '../../../api/index.js'
const stats=ref({}),pie=ref(null)
onMounted(async()=>{const r=await getDashboard();if(r.code===200)stats.value=r.data
  const p=echarts.init(pie.value);const d=(stats.value.warehouseDistribution||[]).map(x=>({value:x.count,name:x.name}))
  p.setOption({tooltip:{trigger:'item'},series:[{type:'pie',radius:'60%',data:d}],color:['#409EFF','#67C23A','#E6A23C','#F56C6C','#909399']})
  window.addEventListener('resize',()=>p.resize())
})
</script>
<style scoped>.ct{padding:0}.sc{text-align:center;padding:20px}.num{font-size:36px;font-weight:700;color:#409EFF}.warn .num{color:#F56C6C}.lbl{color:#909399;margin-top:8px}.ch{height:350px}</style>
