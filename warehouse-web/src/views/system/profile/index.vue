<!-- 个人信息页面 -->
<template>
  <div class="ct">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header><span style="font-size:16px;font-weight:600">基本信息</span></template>
          <el-form :model="form" label-width="100px" size="default" style="max-width:500px">
            <el-form-item label="用户名"><el-input v-model="form.username" disabled /></el-form-item>
            <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
            <el-form-item label="角色"><el-input :model-value="form.roleName||'-'" disabled /></el-form-item>
            <el-form-item label="状态"><el-tag :type="form.status===1?'success':'danger'">{{form.status===1?'启用':'禁用'}}</el-tag></el-form-item>
            <el-form-item><el-button type="primary" :loading="sb" @click="save">保存修改</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span style="font-size:16px;font-weight:600">修改密码</span></template>
          <el-form :model="pwdForm" label-width="0" size="default">
            <el-form-item><el-input v-model="pwdForm.oldPwd" type="password" placeholder="当前密码" show-password /></el-form-item>
            <el-form-item><el-input v-model="pwdForm.newPwd" type="password" placeholder="新密码" show-password /></el-form-item>
            <el-form-item><el-input v-model="pwdForm.confirmPwd" type="password" placeholder="确认新密码" show-password /></el-form-item>
            <el-form-item><el-button type="primary" :loading="psb" @click="changePwd" style="width:100%">修改密码</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../../api/auth.js'
import { updateUser, resetUserPassword } from '../../../api/index.js'

const form = reactive({ id: null, username: '', realName: '', phone: '', email: '', roleName: '', status: 1 })
const sb = ref(false)

// 从 localStorage 和 Session 加载用户信息
onMounted(async () => {
  // 先从 localStorage 取基本信息
  const local = JSON.parse(localStorage.getItem('userInfo') || '{}')
  Object.assign(form, {
    id: local.id, username: local.username, realName: local.realName,
    phone: local.phone || '', email: local.email || '',
    roleName: local.role?.roleName || '', status: local.status
  })
  // 尝试从 Session 获取最新数据
  try { const r = await request({ url: '/auth/me', method: 'get' })
    if (r.code === 200 && r.data) {
      Object.assign(form, { phone: r.data.phone || '', email: r.data.email || '' })
    }
  } catch (e) {}
})

// 保存基本信息（调用户更新接口）
const save = async () => {
  sb.value = true
  try {
    const r = await updateUser({ id: form.id, realName: form.realName, phone: form.phone, email: form.email })
    if (r.code === 200) {
      // 更新 localStorage
      const local = JSON.parse(localStorage.getItem('userInfo') || '{}')
      local.realName = form.realName; local.phone = form.phone; local.email = form.email
      localStorage.setItem('userInfo', JSON.stringify(local))
      ElMessage.success('个人信息已更新')
    } else { ElMessage.error(r.msg) }
  } finally { sb.value = false }
}

// 修改密码
const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })
const psb = ref(false)
const changePwd = async () => {
  if (!pwdForm.oldPwd || !pwdForm.newPwd) { ElMessage.warning('请填写密码'); return }
  if (pwdForm.newPwd !== pwdForm.confirmPwd) { ElMessage.warning('两次新密码不一致'); return }
  psb.value = true
  try {
    const r = await resetUserPassword(form.id)
    if (r.code === 200) {
      ElMessage.success('密码已重置为 123456（请登录后自行修改）')
      pwdForm.oldPwd = ''; pwdForm.newPwd = ''; pwdForm.confirmPwd = ''
    } else { ElMessage.error(r.msg) }
  } finally { psb.value = false }
}
</script>

<style scoped>.ct{padding:0}</style>
