<!--
  ============================================================
  【用户管理页面】system/user/index.vue
  ============================================================
  功能：
    1. 分页查询用户列表（按用户名/姓名/角色/状态筛选）
    2. 新增用户（分配角色，密码默认123456）
    3. 修改用户信息
    4. 删除用户（弹窗确认）
    5. 重置密码
    6. 启用/禁用用户
    7. 批量删除、批量启用、批量禁用
  ============================================================
-->
<template>
  <div class="user-container">
    <!-- ===== 搜索栏 ===== -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="真实姓名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.roleId" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="r in roleList" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ===== 表格 ===== -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增用户</el-button>
        <el-button type="success" :disabled="selectedRows.length === 0" @click="handleBatchEnable">批量启用</el-button>
        <el-button type="warning" :disabled="selectedRows.length === 0" @click="handleBatchDisable">批量禁用</el-button>
        <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDeleteClick">批量删除</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            {{ getRoleName(row.roleId) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; justify-content: center; gap: 6px; white-space: nowrap;">
              <el-switch :model-value="row.status === 1" :active-value="true" :inactive-value="false"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                @change="(val) => handleStatusChange(row, val)" />
              <span :style="{ color: row.status === 1 ? '#13ce66' : '#ff4949', fontSize: '13px' }">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="120" align="center">
          <template #default="{ row }">
            {{ row.creteTime ? row.creteTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" size="small" @click="handleEdit(row)"><el-icon><Edit /></el-icon></el-button>
            <el-button type="info" size="small" @click="handleResetPwdClick(row)"><el-icon><Lock /></el-icon></el-button>
            <el-button type="danger" size="small" @click="handleDeleteClick(row)"><el-icon><Delete /></el-icon></el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
          :page-sizes="[10, 20, 50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <!-- ===== 新增/编辑对话框 ===== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px"
      :close-on-click-modal="false" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" size="default">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="登录时使用" maxlength="50" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="不填则默认为 123456" show-password maxlength="50" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="显示在系统里的名字" maxlength="50" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="选填" maxlength="11" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="选填" maxlength="100" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option v-for="r in roleList" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ isEdit ? '保存修改' : '确认创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Lock } from '@element-plus/icons-vue'
import {
  getUserPage, createUser, updateUser, deleteUser, setUserStatus, resetUserPassword,
  batchDeleteUsers, batchSetUserStatus, getAllRoles
} from '../../../api/index.js'
import {confirmDelete} from '../../../utils/confirm.js'

// ==================== 搜索 ====================

const searchForm = reactive({
  pageNum: 1, pageSize: 10,
  username: '', realName: '', roleId: null, status: null
})

const handleSearch = () => { searchForm.pageNum = 1; loadData() }
const handleReset = () => {
  searchForm.username = ''; searchForm.realName = ''; searchForm.roleId = null; searchForm.status = null
  searchForm.pageNum = 1; loadData()
}

// ==================== 表格 ====================

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const roleList = ref([])

// 角色ID → 角色名 映射
const getRoleName = (roleId) => {
  const role = roleList.value.find(r => r.id === roleId)
  return role ? role.roleName : '-'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserPage({
      pageNum: searchForm.pageNum, pageSize: searchForm.pageSize,
      username: searchForm.username || undefined,
      realName: searchForm.realName || undefined,
      roleId: searchForm.roleId,
      status: searchForm.status
    })
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (err) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const loadRoles = async () => {
  try {
    const res = await getAllRoles()
    if (res.code === 200) roleList.value = res.data
  } catch (err) { /* ignore */ }
}

// ==================== 多选 ====================

const selectedRows = ref([])
const handleSelectionChange = (rows) => { selectedRows.value = rows }

// ==================== 批量操作 ====================

const handleBatchEnable = async () => {
  const ids = selectedRows.value.map(r => r.id)
  try {
    const res = await batchSetUserStatus(ids, 1)
    if (res.code === 200) { ElMessage.success(res.msg); loadData() }
    else ElMessage.error(res.msg)
  } catch (err) { ElMessage.error('操作失败') }
}

const handleBatchDisable = async () => {
  const ids = selectedRows.value.map(r => r.id)
  try {
    const res = await batchSetUserStatus(ids, 0)
    if (res.code === 200) { ElMessage.success(res.msg); loadData() }
    else ElMessage.error(res.msg)
  } catch (err) { ElMessage.error('操作失败') }
}

const handleBatchDeleteClick = async () => {
  const count = selectedRows.value.length
  try {
    await confirmDelete('确定要删除这 ' + count + ' 个用户吗？', '删除后将无法恢复')
    const ids = selectedRows.value.map(r => r.id)
    const res = await batchDeleteUsers(ids)
    if (res.code === 200) { ElMessage.success(res.msg); loadData() }
    else ElMessage.error(res.msg)
  } catch (err) { /* 取消 */ }
}

// ==================== 单行操作 ====================

const handleStatusChange = async (row, val) => {
  const newStatus = val ? 1 : 0
  try {
    const res = await setUserStatus(row.id, newStatus)
    if (res.code === 200) { ElMessage.success(res.msg); row.status = newStatus }
    else ElMessage.error(res.msg)
  } catch (err) { ElMessage.error('操作失败') }
}

const handleResetPwdClick = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置「${row.username}」的密码为 123456 吗？`, '重置密码确认', {
      confirmButtonText: '确定重置', cancelButtonText: '取消', type: 'warning'
    })
    const res = await resetUserPassword(row.id)
    if (res.code === 200) ElMessage.success(res.msg)
    else ElMessage.error(res.msg)
  } catch (err) { /* 取消 */ }
}

const handleDeleteClick = async (row) => {
  try {
    await confirmDelete('确定要删除用户「' + row.username + '」吗？')
    const res = await deleteUser(row.id)
    if (res.code === 200) { ElMessage.success('删除成功'); loadData() }
    else ElMessage.error(res.msg)
  } catch (err) { /* 取消 */ }
}

// ==================== 新增/编辑对话框 ====================

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const form = reactive({
  username: '', password: '', realName: '', phone: '', email: '', roleId: null
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '2~20个字符', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleAdd = () => {
  isEdit.value = false; editingId.value = null; dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true; editingId.value = row.id
  form.username = row.username
  form.password = ''          // 编辑时不回填密码
  form.realName = row.realName
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.roleId = row.roleId
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const payload = {
      username: form.username,
      realName: form.realName,
      phone: form.phone || undefined,
      email: form.email || undefined,
      roleId: form.roleId
    }
    let res
    if (isEdit.value) {
      payload.id = editingId.value
      res = await updateUser(payload)
    } else {
      if (form.password) payload.password = form.password  // 不填则后端用默认密码
      res = await createUser(payload)
    }
    if (res.code === 200) { ElMessage.success(res.msg); dialogVisible.value = false; loadData() }
    else ElMessage.error(res.msg)
  } catch (err) { ElMessage.error('操作失败') }
  finally { submitting.value = false }
}

const resetForm = () => {
  form.username = ''; form.password = ''; form.realName = ''; form.phone = ''; form.email = ''; form.roleId = null
  editingId.value = null
  formRef.value?.resetFields()
}

// ==================== 初始化 ====================

onMounted(() => { loadData(); loadRoles() })
</script>

<style scoped>
.user-container { padding: 0; }
.search-card { margin-bottom: 16px; }
.search-form { margin-bottom: 0; }
.search-form .el-form-item { margin-bottom: 0; }
.table-card { margin-bottom: 16px; }
.toolbar { margin-bottom: 16px; display: flex; gap: 10px; }
.pagination-wrapper { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
