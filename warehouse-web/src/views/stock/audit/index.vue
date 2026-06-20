<template>
  <div class="ct">
    <el-card class="card">
      <template #header>
        <div class="hd"><span class="hd-txt">待审核入库单</span></div>
      </template>
      <el-table :data="inList" border stripe v-loading="ld">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="inNo" label="入库单号" width="170" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{row}">{{ row.inType === 'purchase' ? '采购入库' : '退货入库' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right" />
        <el-table-column prop="createTime" label="提交时间" width="160" align="center">
          <template #default="{row}">{{ row.createTime ? row.createTime.substring(0, 16) : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center">
          <template #default="{row}">
            <el-button size="small" @click="viewIn(row)">查看</el-button>
            <el-button size="small" type="success" @click="approveIn(row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectIn(row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="inList.length===0" class="empty">暂无待审核的入库单</div>
    </el-card>

    <el-card class="card">
      <template #header>
        <div class="hd"><span class="hd-txt">待审核出库单</span></div>
      </template>
      <el-table :data="outList" border stripe v-loading="ld">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="outNo" label="出库单号" width="170" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{row}">{{ row.outType === 'sale' ? '销售出库' : '退货出库' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right" />
        <el-table-column prop="createTime" label="提交时间" width="160" align="center">
          <template #default="{row}">{{ row.createTime ? row.createTime.substring(0, 16) : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center">
          <template #default="{row}">
            <el-button size="small" @click="viewOut(row)">查看</el-button>
            <el-button size="small" type="success" @click="approveOut(row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectOut(row.id)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="outList.length===0" class="empty">暂无待审核的出库单</div>
    </el-card>

    <el-dialog v-model="dv" title="单据详情" width="750px">
      <template v-if="order">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="单号">{{ order.inNo || order.outNo }}</el-descriptions-item>
          <el-descriptions-item label="金额">¥{{ order.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ order.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
        <el-divider>商品明细</el-divider>
        <el-table :data="details" border size="small">
          <el-table-column label="商品ID" width="80" align="center">
            <template #default="{row}">{{ row.productId }}</template>
          </el-table-column>
          <el-table-column label="库位ID" width="80" align="center">
            <template #default="{row}">{{ row.locationId }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" align="center" />
          <el-table-column prop="unitPrice" label="单价" width="120" align="right" />
          <el-table-column prop="amount" label="金额" width="120" align="right" />
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/*
 * 审核页面逻辑
 * 管理员对仓管员提交的出入库单进行审核
 * 通过：更新库存，状态变 approved
 * 驳回：退回草稿状态，仓管员修改后重提
 */
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStockInById, getStockOutById } from '../../../api/index.js'
import request from '../../../api/auth.js'

const inList = ref([])
const outList = ref([])
const ld = ref(false)

/* 加载待审核列表 */
const load = async () => {
  ld.value = true
  try {
    const r = await request({ url: '/audit/pending', method: 'get' })
    if (r.code === 200) {
      inList.value = r.data.stockInList || []
      outList.value = r.data.stockOutList || []
    }
  } finally {
    ld.value = false
  }
}

/* 查看详情 */
const dv = ref(false)
const order = ref(null)
const details = ref([])

const viewIn = async (row) => {
  const r = await getStockInById(row.id)
  if (r.code === 200) {
    order.value = r.data.order
    details.value = r.data.details
    dv.value = true
  }
}

const viewOut = async (row) => {
  const r = await getStockOutById(row.id)
  if (r.code === 200) {
    order.value = r.data.order
    details.value = r.data.details
    dv.value = true
  }
}

/* 审核通过 */
const approveIn = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确认审核通过该入库单吗？审核通过后库存将自动更新。',
      '审核通过',
      { confirmButtonText: '确定通过', cancelButtonText: '取消', type: 'success' }
    )
    const r = await request({ url: '/audit/stock-in/' + id + '/approve', method: 'put' })
    if (r.code === 200) {
      ElMessage.success(r.msg)
      load()
    } else {
      ElMessage.error(r.msg)
    }
  } catch (e) { /* 取消 */ }
}

const approveOut = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确认审核通过该出库单吗？审核通过后库存将自动扣减。',
      '审核通过',
      { confirmButtonText: '确定通过', cancelButtonText: '取消', type: 'success' }
    )
    const r = await request({ url: '/audit/stock-out/' + id + '/approve', method: 'put' })
    if (r.code === 200) {
      ElMessage.success(r.msg)
      load()
    } else {
      ElMessage.error(r.msg)
    }
  } catch (e) { /* 取消 */ }
}

/* 审核驳回 */
const rejectIn = async (id) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入驳回理由（仓管员将看到此理由）',
      '审核驳回',
      {
        confirmButtonText: '确定驳回',
        cancelButtonText: '取消',
        type: 'warning',
        inputPlaceholder: '如：数量填写有误，请核实后重新提交'
      }
    )
    if (reason !== null) {
      const r = await request({
        url: '/audit/stock-in/' + id + '/reject',
        method: 'put',
        data: { reason: reason }
      })
      if (r.code === 200) {
        ElMessage.success(r.msg)
        load()
      } else {
        ElMessage.error(r.msg)
      }
    }
  } catch (e) { /* 取消/关闭 */ }
}

const rejectOut = async (id) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      '请输入驳回理由（仓管员将看到此理由）',
      '审核驳回',
      {
        confirmButtonText: '确定驳回',
        cancelButtonText: '取消',
        type: 'warning',
        inputPlaceholder: '如：数量填写有误，请核实后重新提交'
      }
    )
    if (reason !== null) {
      const r = await request({
        url: '/audit/stock-out/' + id + '/reject',
        method: 'put',
        data: { reason: reason }
      })
      if (r.code === 200) {
        ElMessage.success(r.msg)
        load()
      } else {
        ElMessage.error(r.msg)
      }
    }
  } catch (e) { /* 取消/关闭 */ }
}

onMounted(() => load())
</script>

<style scoped>
.ct { padding: 0; }
.card { margin-bottom: 16px; }
.hd { display: flex; align-items: center; }
.hd-txt { font-size: 16px; font-weight: 600; }
.empty { text-align: center; color: #909399; padding: 40px 0; }
</style>
