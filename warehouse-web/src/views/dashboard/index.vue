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
          <el-table :data="lowStockProducts" style="width: 100%" size="small" max-height="260">
            <el-table-column prop="product_name" label="商品名称" />
            <el-table-column prop="warehouse_name" label="仓库" width="100" />
            <el-table-column prop="quantity" label="库存" width="80" align="center" />
            <el-table-column prop="min_stock" label="最低库存" width="80" align="center" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="scope">
                <el-tag type="danger" size="small">缺货</el-tag>
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
            <el-table-column prop="in_no" label="入库单号" width="150" />
            <el-table-column prop="supplier_name" label="供应商" />
            <el-table-column prop="total_amount" label="金额" width="100" align="right">
              <template #default="scope">¥{{ scope.row.total_amount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 'approved'" type="success" size="small">已审核</el-tag>
                <el-tag v-else type="warning" size="small">待审核</el-tag>
              </template>
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
            <el-table-column prop="out_no" label="出库单号" width="150" />
            <el-table-column prop="customer_name" label="客户" />
            <el-table-column prop="total_amount" label="金额" width="100" align="right">
              <template #default="scope">¥{{ scope.row.total_amount }}</template>
            </el-table-column>
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

// ==================== 统计卡片数据 ====================
const stats = ref({
  totalProducts: 10,      // 对应 product 表10条记录
  categories: 9,           // 对应 product_category 表9条
  totalWarehouses: 3,      // 对应 warehouse 表3条
  totalLocations: 8,       // 对应 storage_location 表8条
  totalStock: 1685,        // 对应 inventory 表所有 quantity 合计
  totalStockValue: 28580,  // 库存金额合计
  lowStockCount: 2,        // 低于最低库存的商品数
  pendingOrders: 2         // 待审核单据数
})

// ==================== 图表引用 ====================
const inChartRef = ref(null)
const outChartRef = ref(null)
const whChartRef = ref(null)

// ==================== 低库存预警 ====================
const lowStockProducts = [
  { product_name: '机械键盘', warehouse_name: '主仓库', quantity: 40, min_stock: 30 },
  { product_name: '打印机', warehouse_name: '副仓库', quantity: 15, min_stock: 10 }
]

// ==================== 最近入库记录 ====================
const recentStockIn = [
  { in_no: 'IN20240615001', supplier_name: '深圳电子科技有限公司', total_amount: '1500.00', status: 'approved' },
  { in_no: 'IN20240616002', supplier_name: '上海办公用品批发中心', total_amount: '500.00', status: 'approved' },
  { in_no: 'IN20240617003', supplier_name: '广州日用品贸易公司', total_amount: '350.00', status: 'approved' },
  { in_no: 'IN20240618004', supplier_name: '北京数码配件供应商', total_amount: '800.00', status: 'pending' }
]

// ==================== 最近出库记录 ====================
const recentStockOut = [
  { out_no: 'OUT20240615001', customer_name: '北京科技有限公司', total_amount: '800.00', status: 'approved' },
  { out_no: 'OUT20240616002', customer_name: '上海贸易有限公司', total_amount: '500.00', status: 'approved' },
  { out_no: 'OUT20240617003', customer_name: '深圳电子科技有限公司', total_amount: '200.00', status: 'approved' },
  { out_no: 'OUT20240618004', customer_name: '深圳电商平台', total_amount: '1200.00', status: 'pending' }
]

// ==================== ECharts 初始化 ====================
onMounted(() => {
  // 入库趋势图
  if (inChartRef.value) {
    const inChart = echarts.init(inChartRef.value)
    inChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['6/12', '6/13', '6/14', '6/15', '6/16', '6/17', '6/18'] },
      yAxis: { type: 'value' },
      series: [
        {
          name: '入库金额',
          type: 'bar',
          data: [1200, 1800, 900, 1500, 800, 2000, 1600],
          itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] }
        }
      ]
    })
  }

  // 出库趋势图
  if (outChartRef.value) {
    const outChart = echarts.init(outChartRef.value)
    outChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['6/12', '6/13', '6/14', '6/15', '6/16', '6/17', '6/18'] },
      yAxis: { type: 'value' },
      series: [
        {
          name: '出库金额',
          type: 'bar',
          data: [800, 1500, 600, 2000, 1200, 900, 1800],
          itemStyle: { color: '#67C23A', borderRadius: [4, 4, 0, 0] }
        }
      ]
    })
  }

  // 仓库库存分布饼图
  if (whChartRef.value) {
    const whChart = echarts.init(whChartRef.value)
    whChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%' },
      series: [
        {
          name: '库存分布',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          label: { show: true, formatter: '{b}\n{d}%' },
          data: [
            { value: 970, name: '主仓库' },
            { value: 515, name: '副仓库' },
            { value: 200, name: '临时仓库' }
          ]
        }
      ]
    })
  }
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
