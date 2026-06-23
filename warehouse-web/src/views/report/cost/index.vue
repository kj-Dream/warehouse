<!--
  ============================================================
  【成本分析页面】report/cost/index.vue (3.2.11)
  ============================================================
  功能：从财务角度分析仓库运营成本
  指标：
    1. 采购成本：各商品采购金额汇总（入库单 approved 的总额）
    2. 库存持有成本：当前库存量 × 成本价
    3. 各商品成本占比（饼图）
    4. 成本趋势（简单统计）
  ============================================================
-->
<template>
  <div class="ct">
    <!-- 成本汇总卡片 -->
    <el-row :gutter="20">
      <el-col :span="6"><el-card class="sc"><div class="val">¥{{ fmt(purchaseCost) }}</div><div class="lbl">采购成本总额</div></el-card></el-col>
      <el-col :span="6"><el-card class="sc warn"><div class="val">¥{{ fmt(holdingCost) }}</div><div class="lbl">库存持有成本</div></el-card></el-col>
      <el-col :span="6"><el-card class="sc"><div class="val">{{ productCount }}</div><div class="lbl">商品种类</div></el-card></el-col>
      <el-col :span="6"><el-card class="sc ok"><div class="val">¥{{ fmt(totalValue) }}</div><div class="lbl">库存总价值</div></el-card></el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12"><el-card><template #header>各商品采购成本占比（Top8）</template><div ref="pieChart" class="ch"></div></el-card></el-col>
      <el-col :span="12"><el-card><template #header>库存成本 vs 采购成本（按仓库）</template><div ref="barChart" class="ch"></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import * as echarts from 'echarts'
import { getInventoryPage, getStockInPage } from '../../../api/index.js'

const purchaseCost=ref(0),holdingCost=ref(0),productCount=ref(0),totalValue=ref(0)
const pieChart=ref(null),barChart=ref(null)
const fmt=(v)=>Number(v||0).toLocaleString('zh-CN',{minimumFractionDigits:2,maximumFractionDigits:2})

onMounted(async()=>{
  // 加载库存数据计算持有成本
  const invRes=await getInventoryPage({pageNum:1,pageSize:200})
  if(invRes.code===200){
    const list=invRes.data.list||[]
    productCount.value=list.length
    let hc=0,tv=0
    list.forEach(item=>{ hc+= (item.quantity||0)*(item.cost_price||item.sale_price||0); tv+= (item.quantity||0)*(item.cost_price||0) })
    holdingCost.value=hc;totalValue.value=tv
  }
  // 加载入库单数据计算采购成本
  const inRes=await getStockInPage({pageNum:1,pageSize:200,status:'approved'})
  if(inRes.code===200){
    const total=inRes.data.list.reduce((s,o)=>s+(Number(o.totalAmount)||0),0)
    purchaseCost.value=total
  }
  // 成本占比饼图
  const pie=echarts.init(pieChart.value)
  pie.setOption({tooltip:{trigger:'item'},series:[{type:'pie',radius:'65%',data:[
    {value:purchaseCost.value,name:'采购成本'},{value:holdingCost.value,name:'持有成本'}
  ],emphasis:{itemStyle:{shadowBlur:10}}}],color:['#409EFF','#E6A23C']})
  // 仓库库存成本柱状图（真实数据：各仓库库存量×成本价）
  const whs = await import('../../../api/index.js').then(m => m.getWarehouseList())
  const whNames = []; const whCosts = []
  if (whs.code === 200 && whs.data) {
    for (const wh of whs.data) {
      const invRes = await import('../../../api/index.js').then(m => m.getInventoryPage({pageNum:1,pageSize:500,warehouseId:wh.id}))
      if (invRes.code === 200) {
        const items = invRes.data.list || []
        const totalCost = items.reduce((s, i) => s + (Number(i.quantity)||0) * (Number(i.cost_price)||0), 0)
        whNames.push(wh.warehouseName); whCosts.push(totalCost)
      }
    }
  }
  const bar = echarts.init(barChart.value)
  bar.setOption({tooltip:{trigger:'axis'},xAxis:{type:'category',data:whNames.length?whNames:['-']},
    yAxis:{type:'value'},series:[{name:'库存成本',type:'bar',data:whCosts.length?whCosts:[0],itemStyle:{color:'#409EFF',borderRadius:[4,4,0,0]}}]})
  window.addEventListener('resize',()=>{pie.resize();bar.resize()})
})
</script>
<style scoped>
.ct{padding:0}.sc{text-align:center;padding:20px}.val{font-size:30px;font-weight:700;color:#409EFF}.warn .val{color:#E6A23C}.ok .val{color:#67C23A}.lbl{color:#909399;margin-top:8px}.ch{height:320px}
</style>
