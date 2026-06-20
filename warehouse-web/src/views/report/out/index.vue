<!-- 出库统计 (3.2.9 财务视角) -->
<template>
  <div class="ct">
    <el-row :gutter="20"><el-col :span="8"><el-card class="sc"><div class="num">{{stats.saleCount||0}}</div><div class="lbl">销售出库</div></el-card></el-col>
      <el-col :span="8"><el-card class="sc warn"><div class="num">{{stats.returnCount||0}}</div><div class="lbl">退货出库</div></el-card></el-col>
      <el-col :span="8"><el-card class="sc ok"><div class="num">{{stats.approvedCount||0}}</div><div class="lbl">已审核</div></el-card></el-col></el-row>
    <el-row :gutter="20" style="margin-top:20px"><el-col :span="12"><el-card><template #header>出库类型分布</template><div ref="pie" class="ch"></div></el-card></el-col>
      <el-col :span="12"><el-card><template #header>审核状态</template><div ref="bar" class="ch"></div></el-card></el-col></el-row>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'; import * as echarts from 'echarts'; import { getStockOutStats } from '../../../api/index.js'
const stats=ref({}),pie=ref(null),bar=ref(null)
onMounted(async()=>{const r=await getStockOutStats();if(r.code===200)stats.value=r.data
  const p=echarts.init(pie.value);p.setOption({tooltip:{trigger:'item'},series:[{type:'pie',radius:'70%',data:[{value:stats.value.saleCount||0,name:'销售出库'},{value:stats.value.returnCount||0,name:'退货出库'}]}],color:['#67C23A','#E6A23C']})
  const b=echarts.init(bar.value);b.setOption({tooltip:{trigger:'axis'},xAxis:{type:'category',data:['草稿','待审核','已审核']},yAxis:{type:'value'},series:[{type:'bar',data:[stats.value.draftCount||0,stats.value.pendingCount||0,stats.value.approvedCount||0],itemStyle:{color:'#67C23A'}}]})
  window.addEventListener('resize',()=>{p.resize();b.resize()})
})
</script>
<style scoped>.ct{padding:0}.sc{text-align:center;padding:20px}.num{font-size:36px;font-weight:700;color:#67C23A}.warn .num{color:#E6A23C}.ok .num{color:#409EFF}.lbl{color:#909399;margin-top:8px}.ch{height:300px}</style>
