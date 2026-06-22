<!-- 审核管理页面：统计卡片 + 待审核出入库单列表 + 审核操作 -->
<template>
  <div class="ct">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6"><el-card shadow="hover" class="stat-card"><div class="stat-num">{{ inList.length }}</div><div class="stat-lbl">待审核入库单</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="stat-card warn"><div class="stat-num">{{ outList.length }}</div><div class="stat-lbl">待审核出库单</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="stat-card"><div class="stat-num">¥{{ totalPendingAmount }}</div><div class="stat-lbl">待审核总金额</div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="stat-card ok"><div class="stat-num">{{ inList.length + outList.length }}</div><div class="stat-lbl">待处理总数</div></el-card></el-col>
    </el-row>

    <!-- 待审核入库单 -->
    <el-card class="card">
      <template #header><div class="hd"><span class="hd-txt">待审核入库单</span></div></template>
      <el-table :data="inList" border stripe v-loading="ld" style="width:100%">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="inNo" label="入库单号" width="170" align="center" />
        <el-table-column label="类型" width="100" align="center"><template #default="{row}">{{ row.inType==='purchase'?'采购入库':'退货入库' }}</template></el-table-column>
        <el-table-column label="明细数" width="80" align="center"><template #default="{row}">{{ detailCountMap[row.id]||'-' }}</template></el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="130" align="center" />
        <el-table-column label="操作人" width="100" align="center"><template #default="{row}">{{ userMap[row.operator]||'-' }}</template></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip>
          <template #default="{row}"><span class="remark-cell">{{ row.remark||'-' }}</span></template>
          <template #header><span style="padding-left:30px">备注</span></template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="175" align="center"><template #default="{row}">{{ row.createTime?row.createTime.substring(0,16):'-' }}</template></el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{row}">
            <el-button size="small" @click="viewIn(row)">查看</el-button>
            <el-button size="small" type="success" @click="approveIn(row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectIn(row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="inList.length===0" class="empty">暂无待审核的入库单</div>
    </el-card>

    <!-- 待审核出库单 -->
    <el-card class="card">
      <template #header><div class="hd"><span class="hd-txt">待审核出库单</span></div></template>
      <el-table :data="outList" border stripe v-loading="ld" style="width:100%">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column prop="outNo" label="出库单号" width="170" align="center" />
        <el-table-column label="类型" width="100" align="center"><template #default="{row}">{{ row.outType==='sale'?'销售出库':'退货出库' }}</template></el-table-column>
        <el-table-column label="明细数" width="80" align="center"><template #default="{row}">{{ detailCountMap[row.id]||'-' }}</template></el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="130" align="center" />
        <el-table-column label="操作人" width="100" align="center"><template #default="{row}">{{ userMap[row.operator]||'-' }}</template></el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip>
          <template #default="{row}"><span class="remark-cell">{{ row.remark||'-' }}</span></template>
          <template #header><span style="padding-left:30px">备注</span></template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="175" align="center"><template #default="{row}">{{ row.createTime?row.createTime.substring(0,16):'-' }}</template></el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{row}">
            <el-button size="small" @click="viewOut(row)">查看</el-button>
            <el-button size="small" type="success" @click="approveOut(row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectOut(row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="outList.length===0" class="empty">暂无待审核的出库单</div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="dv" title="单据详情" width="750px">
      <template v-if="order">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="单号">{{ order.inNo||order.outNo }}</el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ order.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ order.remark||'无' }}</el-descriptions-item>
        </el-descriptions>
        <el-divider>商品明细</el-divider>
        <el-table :data="details" border size="small">
          <el-table-column label="商品ID" width="80" align="center"><template #default="{row}">{{ row.productId }}</template></el-table-column>
          <el-table-column label="库位ID" width="80" align="center"><template #default="{row}">{{ row.locationId }}</template></el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="unitPrice" label="单价" width="120" align="right" />
          <el-table-column prop="amount" label="金额" width="120" align="right" />
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStockInById, getStockOutById, getUserPage } from '../../../api/index.js'
import request from '../../../api/auth.js'
import { refreshAuditCount } from '../../../stores/audit.js'

const inList = ref([])
const outList = ref([])
const ld = ref(false)
const userMap = ref({})
const detailCountMap = ref({})

const totalPendingAmount = computed(() => {
  const inTotal = inList.value.reduce((s, o) => s + (Number(o.totalAmount) || 0), 0)
  const outTotal = outList.value.reduce((s, o) => s + (Number(o.totalAmount) || 0), 0)
  return (inTotal + outTotal).toLocaleString()
})

const load = async () => {
  ld.value = true
  try {
    const r = await request({ url: '/audit/pending', method: 'get' })
    if (r.code === 200) {
      inList.value = r.data.stockInList || []
      outList.value = r.data.stockOutList || []
      // 加载明细数
      const all = [...inList.value, ...outList.value]
      for (const item of all) {
        const isIn = !!item.inNo
        const dr = isIn ? await getStockInById(item.id) : await getStockOutById(item.id)
        if (dr.code === 200) detailCountMap.value[item.id] = (dr.data.details || []).length
      }
    }
    const userRes = await getUserPage({ pageNum: 1, pageSize: 100 })
    if (userRes.code === 200) (userRes.data.list || []).forEach(u => { userMap.value[u.id] = u.realName || u.username })
  } finally { ld.value = false }
}

const dv = ref(false), order = ref(null), details = ref([])
const viewIn = async (row) => { const r = await getStockInById(row.id); if (r.code === 200) { order.value = r.data.order; details.value = r.data.details; dv.value = true } }
const viewOut = async (row) => { const r = await getStockOutById(row.id); if (r.code === 200) { order.value = r.data.order; details.value = r.data.details; dv.value = true } }

const approveIn = async (id) => {
  try { await ElMessageBox.confirm('确认审核通过该入库单吗？审核通过后库存将自动更新。', '审核通过', { confirmButtonText: '确定通过', cancelButtonText: '取消', type: 'success' })
    const r = await request({ url: '/audit/stock-in/' + id + '/approve', method: 'put' })
    if (r.code === 200) { ElMessage.success(r.msg); load(); refreshAuditCount() } else ElMessage.error(r.msg)
  } catch (e) {}
}
const approveOut = async (id) => {
  try { await ElMessageBox.confirm('确认审核通过该出库单吗？审核通过后库存将自动扣减。', '审核通过', { confirmButtonText: '确定通过', cancelButtonText: '取消', type: 'success' })
    const r = await request({ url: '/audit/stock-out/' + id + '/approve', method: 'put' })
    if (r.code === 200) { ElMessage.success(r.msg); load(); refreshAuditCount() } else ElMessage.error(r.msg)
  } catch (e) {}
}
const rejectIn = async (id) => {
  try { const { value: reason } = await ElMessageBox.prompt('请输入驳回理由（仓管员将看到此理由）', '审核驳回', { confirmButtonText: '确定驳回', cancelButtonText: '取消', type: 'warning', inputPlaceholder: '如：数量填写有误，请核实后重新提交' })
    if (reason !== null) { const r = await request({ url: '/audit/stock-in/' + id + '/reject', method: 'put', data: { reason } })
      if (r.code === 200) { ElMessage.success(r.msg); load(); refreshAuditCount() } else ElMessage.error(r.msg) }
  } catch (e) {}
}
const rejectOut = async (id) => {
  try { const { value: reason } = await ElMessageBox.prompt('请输入驳回理由（仓管员将看到此理由）', '审核驳回', { confirmButtonText: '确定驳回', cancelButtonText: '取消', type: 'warning', inputPlaceholder: '如：数量填写有误，请核实后重新提交' })
    if (reason !== null) { const r = await request({ url: '/audit/stock-out/' + id + '/reject', method: 'put', data: { reason } })
      if (r.code === 200) { ElMessage.success(r.msg); load(); refreshAuditCount() } else ElMessage.error(r.msg) }
  } catch (e) {}
}

onMounted(() => load())
</script>

<style scoped>
.ct { padding: 0; }
.card { margin-bottom: 16px; }
.hd { display: flex; align-items: center; }
.hd-txt { font-size: 16px; font-weight: 600; }
.empty { text-align: center; color: #909399; padding: 40px 0; }
.stats-row { margin-bottom: 16px; }
.stat-card { text-align: center; padding: 16px; }
.stat-num { font-size: 28px; font-weight: 700; color: #409EFF; }
.stat-card.warn .stat-num { color: #E6A23C; }
.stat-card.ok .stat-num { color: #67C23A; }
.stat-lbl { color: #909399; margin-top: 6px; font-size: 13px; }
.remark-cell { display: block; padding: 0 30px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
:deep(.el-table__body-wrapper .el-table__cell) { vertical-align: middle; }
</style>
