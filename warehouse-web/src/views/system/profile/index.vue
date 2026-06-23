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
            <el-form-item><el-input v-model="pwdForm.newPwd" type="password" placeholder="新密码（至少6位）" show-password @change="pwdForm.confirmPwd=''" /></el-form-item>
            <el-form-item v-if="pwdForm.newPwd">
              <div :class="['confirm-track', { shake: shakeConfirm }]">
                <div v-for="(_, i) in pwdForm.newPwd.split('')" :key="i" :class="['confirm-dot', getConfirmClass(i)]">●</div>
              </div>
            </el-form-item>
            <el-form-item><el-input v-model="pwdForm.confirmPwd" type="password" placeholder="确认新密码" show-password @input="onConfirmInput" /></el-form-item>
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
const shakeConfirm = ref(false)
const onConfirmInput = () => {
  if (pwdForm.confirmPwd.length > pwdForm.newPwd.length) {
    shakeConfirm.value = true
    pwdForm.confirmPwd = pwdForm.confirmPwd.slice(0, pwdForm.newPwd.length)
    setTimeout(() => shakeConfirm.value = false, 500)
  }
}
const getConfirmClass = (i) => {
  if (!pwdForm.confirmPwd[i]) return ''
  return pwdForm.confirmPwd[i] === pwdForm.newPwd[i] ? 'match' : 'mismatch'
}
const changePwd = async () => {
  if (!pwdForm.oldPwd || !pwdForm.newPwd) { ElMessage.warning('请填写密码'); return }
  if (pwdForm.newPwd.length < 6) { ElMessage.warning('新密码至少6位'); return }
  if (pwdForm.newPwd === pwdForm.oldPwd) { ElMessage.warning('新密码不能与旧密码相同'); return }
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

<style scoped>.ct { padding: 0; }
.confirm-track { height: 44px; border: 2px solid #e4e7ed; border-radius: 10px; display: flex; align-items: center; justify-content: center; gap: 0; }
.confirm-track.shake { animation: shake .5s ease-in-out; }
@keyframes shake { 0%,100%{ transform:translateX(0) } 20%{ transform:translateX(-10px) } 40%{ transform:translateX(10px) } 60%{ transform:translateX(-10px) } 80%{ transform:translateX(10px) } }
.confirm-dot { font-size: 7px; width: 16px; height: 16px; display: flex; align-items: center; justify-content: center; color: #c0c4cc; transition: all .3s ease; border-radius: 50%; }
.confirm-dot.match { color: #67C23A; background: rgba(103,194,58,.15); }
.confirm-dot.mismatch { color: #F56C6C; background: rgba(245,108,108,.15); }
</style>
