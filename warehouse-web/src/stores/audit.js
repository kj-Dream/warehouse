/*
 * 审核角标共享状态
 * Layout.vue 和审核页面共用这个 ref，操作完立刻更新，无需刷新
 */
import { ref } from 'vue'
import request from '../api/auth.js'

// 全局共享的待审核数量
export const auditCount = ref(0)

// 刷新审核数量（Layout 和审核页都调用这个）
export async function refreshAuditCount() {
  try {
    const r = await request({ url: '/audit/pending', method: 'get' })
    if (r.code === 200) {
      auditCount.value = (r.data.stockInList || []).length + (r.data.stockOutList || []).length
    }
  } catch (e) { /* ignore */ }
}
