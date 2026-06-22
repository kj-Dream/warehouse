<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #e6f7ff;">
              <el-icon color="#1890ff" :size="36"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalProducts }}</div>
              <div class="stat-label">商品种类</div>
            </div>
          </div>
          <div class="stat-footer">
            共 {{ stats.categories }} 个分类
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #fff7e6;">
              <el-icon color="#fa8c16" :size="36"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalWarehouses }}</div>
              <div class="stat-label">仓库数量</div>
            </div>
          </div>
          <div class="stat-footer">
            {{ stats.totalLocations }} 个库位
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #f6ffed;">
              <el-icon color="#52c41a" :size="36"><Select /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalStock }}</div>
              <div class="stat-label">库存总量（件）</div>
            </div>
          </div>
          <div class="stat-footer">
            库存金额 ¥{{ stats.totalStockValue.toLocaleString() }}
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon" style="background: #fff1f0;">
              <el-icon color="#f5222d" :size="36"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.lowStockCount }}</div>
              <div class="stat-label">低库存预警</div>
            </div>
          </div>
          <div class="stat-footer">
            {{ stats.pendingOrders }} 笔待审核单据
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表 + 快速操作区 -->
    <el-row :gutter="20" class="middle-row">
      <!-- 入库趋势 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📥 近7天入库趋势</span>
              <span v-if="inCompare!==null" :style="{color:inCompare>=0?'#e74c3c':'#67C23A',fontSize:'13px',marginLeft:'auto'}">{{inCompare>=0?'📈':'📉'}} {{Math.abs(inCompare)}}% 较上期</span>
            </div>
          </template>
          <div class="chart-box" ref="inChartRef"></div>
        </el-card>
      </el-col>

      <!-- 出库趋势 -->
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📤 近7天出库趋势</span>
              <span v-if="outCompare!==null" :style="{color:outCompare>=0?'#e74c3c':'#67C23A',fontSize:'13px',marginLeft:'auto'}">{{outCompare>=0?'📈':'📉'}} {{Math.abs(outCompare)}}% 较上期</span>
            </div>
          </template>
          <div class="chart-box" ref="outChartRef"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 仓库库存分布 + 预警 -->
    <el-row :gutter="20" class="middle-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🏭 仓库库存分布</span>
            </div>
          </template>
          <div class="chart-box" ref="whChartRef"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>⚡ 低库存预警</span>
            </div>
          </template>
          <el-table :data="lowStockProducts" style="width: 100%; height: 260px" size="small">
            <el-table-column prop="product_name" label="商品名称" />
            <el-table-column prop="warehouse_name" label="仓库" width="100" />
            <el-table-column prop="quantity" label="库存" width="80" align="center" />
            <el-table-column prop="min_stock" label="最低库存" width="80" align="center" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{row}">
                <el-tag :type="row.warn_type==='低于最低库存'?'danger':'warning'" size="small">{{ row.warn_type }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近出入库记录 -->
    <el-row :gutter="20" class="middle-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📋 最近入库记录</span>
              <el-button type="primary" size="small" text @click="$router.push('/stock/in')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentStockIn" style="width: 100%" size="small">
            <el-table-column prop="inNo" label="入库单号" width="150" />
            <el-table-column label="供应商" min-width="120"><template #default="{row}">{{ supplierMap[row.supplierId]||'-' }}</template></el-table-column>
            <el-table-column label="金额" width="100" align="right"><template #default="{row}">¥{{ row.totalAmount }}</template></el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{row}"><el-tag v-if="row.status==='approved'" type="success" size="small">已审核</el-tag><el-tag v-else-if="row.status==='pending'" type="warning" size="small">待审核</el-tag><el-tag v-else type="info" size="small">草稿</el-tag></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📋 最近出库记录</span>
              <el-button type="primary" size="small" text @click="$router.push('/stock/out')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentStockOut" style="width: 100%" size="small">
            <el-table-column prop="outNo" label="出库单号" width="150" />
            <el-table-column label="客户" min-width="120"><template #default="{row}">{{ customerMap[row.customerId]||'-' }}</template></el-table-column>
            <el-table-column label="金额" width="100" align="right"><template #default="{row}">¥{{ row.totalAmount }}</template></el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 'approved'" type="success" size="small">已审核</el-tag>
                <el-tag v-else type="warning" size="small">待审核</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Box, OfficeBuilding, Select, Warning } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard, getInventoryWarning, getStockInPage, getStockOutPage, getSuppliers, getCustomers, getCategoryTree, getWarehouseList, getInventoryPage } from '../../api/index.js'

// ==================== 统计卡片 ====================
const stats = ref({ totalProducts: '-', categories: '-', totalWarehouses: '-', totalLocations: '-', totalStock: 0, totalStockValue: 0, lowStockCount: 0, pendingOrders: 0 })

// ==================== 图表引用 ====================
const inChartRef = ref(null)
const outChartRef = ref(null)
const whChartRef = ref(null)

// ==================== 真实数据 ====================
const lowStockProducts = ref([])
const recentStockIn = ref([])
const recentStockOut = ref([])
const supplierMap = ref({})
const customerMap = ref({})
const inCompare = ref(null)   // 入库本期vs上期变化百分比
const outCompare = ref(null)  // 出库本期vs上期变化百分比

// ==================== 加载全部真实数据 ====================
onMounted(async () => {
  // 并行加载所有数据
  const [dash, inv, warn, inR, outR, supR, custR, cats, whs, prevInR, prevOutR] = await Promise.all([
    getDashboard(),
    getInventoryPage({ pageNum: 1, pageSize: 500 }),
    getInventoryWarning({ pageNum: 1, pageSize: 10 }),
    getStockInPage({ pageNum: 1, pageSize: 5 }),
    getStockOutPage({ pageNum: 1, pageSize: 5 }),
    getSuppliers(),
    getCustomers(),
    getCategoryTree(),
    getWarehouseList(),
    getStockInPage({ pageNum: 2, pageSize: 7 }),
    getStockOutPage({ pageNum: 2, pageSize: 7 })
  ])

  // 1. 统计卡片
  if (dash.code === 200) {
    const d = dash.data
    stats.value.totalProducts = d.productCount || 0
    stats.value.totalWarehouses = d.warehouseCount || 0
    stats.value.lowStockCount = d.warningCount || 0
    stats.value.pendingOrders = (d.stockInTotal || 0) + (d.stockOutTotal || 0)
  }

  // 2. 库存总量+金额
  if (inv.code === 200) {
    const list = inv.data.list || []
    stats.value.totalStock = list.reduce((s, i) => s + (Number(i.quantity) || 0), 0)
    stats.value.totalStockValue = list.reduce((s, i) => s + (Number(i.quantity) || 0) * (Number(i.cost_price) || 0), 0)
  }

  // 3. 低库存预警
  if (warn.code === 200) lowStockProducts.value = warn.data.list || []

  // 4. 最近出入库
  if (inR.code === 200) recentStockIn.value = inR.data.list || []
  if (outR.code === 200) recentStockOut.value = outR.data.list || []

  // 5. 供应商/客户名称映射
  if (supR.code === 200) supR.data.forEach(s => { supplierMap.value[s.id] = s.supplierName })
  if (custR.code === 200) custR.data.forEach(c => { customerMap.value[c.id] = c.customerName })

  // 6. 分类和库位
  if (cats.code === 200) {
    let count = 0; cats.data.forEach(c => { count++; if (c.children) count += c.children.length })
    stats.value.categories = count
  }
  if (whs.code === 200) stats.value.totalLocations = whs.data.length

  // 计算本期 vs 上期对比
  const prevInTotal = (prevInR.data?.list||[]).reduce((s,o)=>s+(Number(o.totalAmount)||0),0)
  const prevOutTotal = (prevOutR.data?.list||[]).reduce((s,o)=>s+(Number(o.totalAmount)||0),0)
  const curInTotal = recentStockIn.value.reduce((s,o)=>s+(Number(o.totalAmount)||0),0)
  const curOutTotal = recentStockOut.value.reduce((s,o)=>s+(Number(o.totalAmount)||0),0)
  inCompare.value = prevInTotal>0 ? Math.round((curInTotal-prevInTotal)/prevInTotal*100) : null
  outCompare.value = prevOutTotal>0 ? Math.round((curOutTotal-prevOutTotal)/prevOutTotal*100) : null

  // ============ ECharts 图表（动态真实数据） ============
  const inList = recentStockIn.value.slice(0, 7).reverse()
  const outListArr = recentStockOut.value.slice(0, 7).reverse()
  if (inChartRef.value) {
    echarts.init(inChartRef.value).setOption({
      tooltip:{trigger:'axis'},grid:{left:'3%',right:'4%',bottom:'3%',containLabel:true},
      xAxis:{type:'category',data:inList.map(o=>(o.createTime||'').substring(5,10))},
      yAxis:{type:'value'},series:[{name:'入库金额',type:'bar',data:inList.map(o=>Number(o.totalAmount)||0),itemStyle:{color:'#409EFF',borderRadius:[4,4,0,0]}}]})
  }
  if (outChartRef.value) {
    echarts.init(outChartRef.value).setOption({
      tooltip:{trigger:'axis'},grid:{left:'3%',right:'4%',bottom:'3%',containLabel:true},
      xAxis:{type:'category',data:outListArr.map(o=>(o.createTime||'').substring(5,10))},
      yAxis:{type:'value'},series:[{name:'出库金额',type:'bar',data:outListArr.map(o=>Number(o.totalAmount)||0),itemStyle:{color:'#67C23A',borderRadius:[4,4,0,0]}}]})
  }

  // 仓库分布饼图
  if (whChartRef.value) {
    const whChart = echarts.init(whChartRef.value)
    const dist = dash.data.warehouseDistribution || []
    whChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: ['40%', '70%'], data: dist.map(d => ({ value: d.count, name: d.name })), emphasis: { itemStyle: { shadowBlur: 10 } } }],
      color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#1890ff', '#fa8c16']
    })
  }

  window.addEventListener('resize', () => {
    [inChartRef, outChartRef, whChartRef].forEach(r => {
      if (r.value) { const c = echarts.getInstanceByDom(r.value); if (c) c.resize() }
    })
  })
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ===== 统计卡片 ===== */
.stat-cards {
  margin-bottom: 0;
}
.stat-card {
  cursor: default;
}
.stat-card :deep(.el-card__body) {
  padding: 20px;
}
.stat-body {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.stat-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #909399;
}

/* ===== 中间行 ===== */
.middle-row {
  margin-bottom: 0;
}

/* ===== 卡片通用 ===== */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
}

/* ===== 图表容器 ===== */
.chart-box {
  width: 100%;
  height: 260px;
}
</style>
