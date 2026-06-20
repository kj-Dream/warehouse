<!--
  ============================================================
  【角色管理页面】system/role/index.vue
  ============================================================
  功能：
    1. 分页查询角色列表（支持按名称/标识/状态筛选）
    2. 新增角色（含菜单权限分配）
    3. 修改角色（修改信息 + 重新分配菜单权限）
    4. 删除角色（有关联用户则拒绝删除）
    5. 启用/禁用角色

  核心设计：
    - 菜单权限使用 el-tree 多选框展示，支持父子联动
    - 新增/编辑共用同一个对话框
    - 删除前二次确认 + 后端检查用户关联
  ============================================================
-->
<template>
  <div class="role-container">
    <!-- ===== 搜索栏 ===== -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input v-model="searchForm.roleKey" placeholder="请输入角色标识" clearable
            @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- ===== 操作栏和表格 ===== -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增角色
        </el-button>
        <el-button
          type="success"
          :disabled="selectedRows.length === 0"
          @click="handleBatchEnable"
        >
          批量启用
        </el-button>
        <el-button
          type="warning"
          :disabled="selectedRows.length === 0"
          @click="handleBatchDisable"
        >
          批量禁用
        </el-button>
        <el-button
          type="danger"
          :disabled="selectedRows.length === 0"
          @click="handleBatchDeleteClick"
        >
          批量删除
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleKey" label="角色标识" width="150" />
        <el-table-column prop="description" label="描述" min-width="120" show-overflow-tooltip />
        <el-table-column label="状态" width="130" align="center">
          <template #default="{ row }">
            <div style="display: flex; align-items: center; justify-content: center; gap: 6px; white-space: nowrap;">
              <el-switch
                :model-value="row.status === 1"
                :active-value="true"
                :inactive-value="false"
                style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
                @change="(val) => handleStatusChange(row, val)"
              />
              <span :style="{ color: row.status === 1 ? '#13ce66' : '#ff4949', fontSize: '13px' }">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" align="center">
          <template #default="{ row }">
            {{ row.createTime ? row.createTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" size="small" @click="handleEdit(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteClick(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="searchForm.pageNum"
          v-model:page-size="searchForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- ===== 新增/编辑对话框 ===== -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '新增角色'"
      width="560px"
      :close-on-click-modal="false"
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="default"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="如：管理员、仓管员" maxlength="50" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="如：admin、warehouse（英文标识）"
            maxlength="50" :disabled="isEdit" />
          <span v-if="isEdit" class="field-tip">角色标识不可修改</span>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3"
            placeholder="对该角色的说明" maxlength="200" />
        </el-form-item>

        <!-- 菜单权限分配（树形勾选框） -->
        <el-form-item label="菜单权限">
          <div class="menu-tree-wrapper">
            <el-tree
              ref="menuTreeRef"
              :data="menuTreeData"
              show-checkbox
              node-key="id"
              :default-checked-keys="form.menuIds"
              :props="{ label: 'name', children: 'children' }"
              default-expand-all
              highlight-current
            >
              <template #default="{ data }">
                <span class="menu-tree-node">
                  <span>{{ data.name }}</span>
                </span>
              </template>
            </el-tree>
          </div>
          <div class="menu-tip">勾选该角色可以访问的菜单模块</div>
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
/**
 * ============================================================
 * 角色管理页面逻辑
 * ============================================================
 * 数据流：
 *   搜索条件 → getRolePage() → 后端分页查询 → 表格展示
 *   新增/编辑 → 收集表单 + 树选中的菜单ID → createRole/updateRole → 刷新列表
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getRolePage, getRoleById, createRole, updateRole, deleteRole, setRoleStatus,
  batchDeleteRoles, batchSetRoleStatus,
  getMenus
} from '../../../api/index.js'

// ==================== 搜索 ====================

const searchForm = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: null
})

const handleSearch = () => {
  searchForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.roleName = ''
  searchForm.roleKey = ''
  searchForm.status = null
  searchForm.pageNum = 1
  loadData()
}

// ==================== 表格数据 ====================

const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRolePage({
      pageNum: searchForm.pageNum,
      pageSize: searchForm.pageSize,
      roleName: searchForm.roleName || undefined,
      roleKey: searchForm.roleKey || undefined,
      status: searchForm.status
    })
    if (res.code === 200) {
      tableData.value = res.data.list
      total.value = res.data.total
    }
  } catch (err) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

// ==================== 多选 ====================

const selectedRows = ref([])

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// ==================== 批量操作 ====================

const handleBatchEnable = async () => {
  const ids = selectedRows.value.map(r => r.id)
  try {
    const res = await batchSetRoleStatus(ids, 1)
    if (res.code === 200) {
      ElMessage.success(res.msg)
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const handleBatchDisable = async () => {
  const ids = selectedRows.value.map(r => r.id)
  try {
    const res = await batchSetRoleStatus(ids, 0)
    if (res.code === 200) {
      ElMessage.success(res.msg)
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const handleBatchDeleteClick = async () => {
  const count = selectedRows.value.length
  try {
    await ElMessageBox.confirm(
      `确定要删除这 ${count} 个角色吗？删除后将无法恢复。`,
      '批量删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    // 用户点了确定，执行批量删除
    const ids = selectedRows.value.map(r => r.id)
    const res = await batchDeleteRoles(ids)
    if (res.code === 200) {
      ElMessage.success(res.msg)
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    // 用户点了取消或删除失败
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// ==================== 状态切换 ====================

const handleStatusChange = async (row, val) => {
  const newStatus = val ? 1 : 0
  try {
    const res = await setRoleStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(res.msg)
      row.status = newStatus  // 就地更新表格行
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// ==================== 删除 ====================

const handleDeleteClick = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色「${row.roleName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const res = await deleteRole(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    // 取消或失败
  }
}

// ==================== 新增/编辑对话框 ====================

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const menuTreeRef = ref(null)

// 菜单树数据（从后端加载）
const menuTreeData = ref([])

// 保存当前编辑的角色ID
const editingRoleId = ref(null)

const form = reactive({
  roleName: '',
  roleKey: '',
  description: '',
  menuIds: []   // 编辑时：该角色已有的菜单权限ID
})

// 表单校验规则
const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '标识只能包含英文字母、数字和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过 200 个字符', trigger: 'blur' }
  ]
}

/** 加载菜单树（权限分配用） */
const loadMenuTree = async () => {
  if (menuTreeData.value.length > 0) return  // 已加载过，不重复请求
  try {
    const res = await getMenus()
    if (res.code === 200) {
      menuTreeData.value = res.data
    }
  } catch (err) {
    console.error('加载菜单失败', err)
  }
}

/** 新增角色 */
const handleAdd = async () => {
  isEdit.value = false
  editingRoleId.value = null
  await loadMenuTree()
  dialogVisible.value = true
}

/** 编辑角色 */
const handleEdit = async (row) => {
  isEdit.value = true
  editingRoleId.value = row.id
  await loadMenuTree()

  try {
    // 获取角色详情（含菜单权限ID列表）
    const res = await getRoleById(row.id)
    if (res.code === 200) {
      const { role, menuIds } = res.data
      form.roleName = role.roleName
      form.roleKey = role.roleKey
      form.description = role.description || ''
      form.menuIds = menuIds || []
      dialogVisible.value = true
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('获取角色信息失败')
  }
}

/** 提交表单 */
const handleSubmit = async () => {
  // 表单校验
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 获取树上选中的菜单ID
  const checkedKeys = menuTreeRef.value.getCheckedKeys()        // 全选中的节点
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys() // 半选中的节点
  const allMenuIds = [...checkedKeys, ...halfCheckedKeys]

  submitting.value = true
  try {
    const payload = {
      roleName: form.roleName,
      roleKey: form.roleKey,
      description: form.description,
      menuIds: allMenuIds
    }

    let res
    if (isEdit.value) {
      payload.id = editingRoleId.value
      res = await updateRole(payload)
    } else {
      res = await createRole(payload)
    }

    if (res.code === 200) {
      ElMessage.success(res.msg)
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (err) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

/** 关闭对话框时重置表单 */
const resetForm = () => {
  form.roleName = ''
  form.roleKey = ''
  form.description = ''
  form.menuIds = []
  editingRoleId.value = null
  if (formRef.value) formRef.value.resetFields()
}

// ==================== 初始化 ====================

onMounted(() => {
  loadData()
  loadMenuTree()  // 预加载菜单树，打开对话框时秒开
})
</script>

<style scoped>
.role-container {
  padding: 0;
}

/* ===== 搜索栏 ===== */
.search-card {
  margin-bottom: 16px;
}
.search-form {
  margin-bottom: 0;
}
.search-form .el-form-item {
  margin-bottom: 0;
}

/* ===== 表格 ===== */
.table-card {
  margin-bottom: 16px;
}
.toolbar {
  margin-bottom: 16px;
}
.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

/* ===== 对话框 ===== */
.field-tip {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.menu-tree-wrapper {
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 8px 12px;
}

.menu-tree-node {
  font-size: 14px;
  display: flex;
  align-items: center;
}

.menu-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
</style>
