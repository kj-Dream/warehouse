<!--
  ============================================================
  【Register.vue】注册页面 — 与登录页统一深色风格
  ============================================================
  包含 Element Plus 表单校验：
    - 用户名 3~20 个字符
    - 密码至少 6 位
    - 确认密码一致性校验
    - 手机号格式校验
    - 邮箱格式校验

  注册后状态为"待审核"，需管理员启用后才能登录。
  ============================================================
-->
<template>
  <div class="register-wrapper">
    <!-- ===== 粒子背景 ===== -->
    <canvas ref="particleCanvas" class="particle-canvas"></canvas>

    <!-- ===== 光晕层 ===== -->
    <div aria-hidden="true" class="glow-layer">
      <div class="glow-orb glow-orb-1" />
      <div class="glow-orb glow-orb-2" />
      <div class="glow-orb glow-orb-3" />
    </div>

    <!-- ===== 主内容 ===== -->
    <div class="register-container">
      <div class="register-card">
        <!-- Logo -->
        <div class="logo-area">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="logo-svg">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
          </div>
          <span class="logo-text">云芯仓储</span>
        </div>

        <!-- 标题 -->
        <div class="title-area">
          <h1 class="title">创建账号</h1>
          <p class="subtitle">注册后需等待管理员审核通过方可登录</p>
        </div>

        <!-- 注册表单 -->
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="0"
          size="large"
          class="register-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名（3~20个字符）"
              clearable
            >
              <template #prefix>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              show-password
            >
              <template #prefix>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
            >
              <template #prefix>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" clearable>
              <template #prefix>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号（选填）" clearable>
              <template #prefix>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱（选填）" clearable>
              <template #prefix>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              注 册
            </el-button>
          </el-form-item>

          <el-form-item>
            <el-button class="login-btn" @click="goLogin">
              已有账号？去登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/index.js'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const particleCanvas = ref(null)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: ''
})

// 自定义校验：确认密码一致性
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

/** 注册 */
const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { confirmPassword, ...registerData } = form
    const res = await register(registerData)
    if (res.code === 200) {
      ElMessage.success('注册成功，请等待管理员审核')
      router.push('/login')
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (err) {
    ElMessage.error('请求失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.push('/login')
}

// ========== 粒子动画（与登录页相同的实现） ==========
let animationId = null
let mouseX = -1000
let mouseY = -1000

const initParticles = () => {
  const canvas = particleCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  let width = (canvas.width = window.innerWidth)
  let height = (canvas.height = window.innerHeight)

  const particleCount = 100
  const connectionDistance = 150

  const particles = Array.from({ length: particleCount }, () => ({
    x: Math.random() * width,
    y: Math.random() * height,
    vx: (Math.random() - 0.5) * 0.5,
    vy: (Math.random() - 0.5) * 0.5,
    size: Math.random() * 2 + 1
  }))

  const draw = () => {
    ctx.clearRect(0, 0, width, height)
    for (let i = 0; i < particles.length; i++) {
      const p = particles[i]
      p.x += p.vx
      p.y += p.vy
      if (p.x < 0 || p.x > width) p.vx *= -1
      if (p.y < 0 || p.y > height) p.vy *= -1

      const dx = mouseX - p.x
      const dy = mouseY - p.y
      const dist = Math.sqrt(dx * dx + dy * dy)
      if (dist < 200) {
        p.vx += dx * 0.00005
        p.vy += dy * 0.00005
      }
      p.vx *= 0.999
      p.vy *= 0.999

      ctx.beginPath()
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2)
      ctx.fillStyle = 'rgba(102, 102, 102, 0.5)'
      ctx.fill()

      for (let j = i + 1; j < particles.length; j++) {
        const q = particles[j]
        const dx2 = p.x - q.x
        const dy2 = p.y - q.y
        const dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2)
        if (dist2 < connectionDistance) {
          const opacity = (1 - dist2 / connectionDistance) * 0.15
          ctx.beginPath()
          ctx.moveTo(p.x, p.y)
          ctx.lineTo(q.x, q.y)
          ctx.strokeStyle = `rgba(102, 102, 102, ${opacity})`
          ctx.lineWidth = 0.5
          ctx.stroke()
        }
      }
    }
    animationId = requestAnimationFrame(draw)
  }
  draw()

  const handleResize = () => {
    width = canvas.width = window.innerWidth
    height = canvas.height = window.innerHeight
  }
  window.addEventListener('resize', handleResize)

  const handleMouseMove = (e) => {
    mouseX = e.clientX
    mouseY = e.clientY
  }
  window.addEventListener('mousemove', handleMouseMove)

  canvas._cleanup = () => {
    window.removeEventListener('resize', handleResize)
    window.removeEventListener('mousemove', handleMouseMove)
  }
}

onMounted(() => initParticles())

onBeforeUnmount(() => {
  if (animationId) cancelAnimationFrame(animationId)
  if (particleCanvas.value && particleCanvas.value._cleanup) {
    particleCanvas.value._cleanup()
  }
})
</script>

<style scoped>
/* ============================================================
   整体布局
   ============================================================ */
.register-wrapper {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #0a0a0a;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

.particle-canvas {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

/* ===== 光晕 ===== */
.glow-layer {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}
.glow-orb {
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 50%;
  filter: blur(80px);
}
.glow-orb-1 {
  width: 560px; height: 320px;
  transform: translateY(-50%) rotate(-45deg);
  background: radial-gradient(68.54% 68.72% at 55.02% 31.46%, rgba(255,255,255,0.06) 0%, rgba(150,150,150,0.02) 50%, rgba(255,255,255,0.01) 80%);
}
.glow-orb-2 {
  width: 320px; height: 240px;
  transform: translate(5%, -50%) rotate(-45deg);
  background: radial-gradient(50% 50% at 50% 50%, rgba(255,255,255,0.04) 0%, rgba(255,255,255,0.01) 80%, transparent 100%);
}
.glow-orb-3 {
  width: 320px; height: 240px;
  transform: translateY(-50%) rotate(-45deg);
  background: radial-gradient(50% 50% at 50% 50%, rgba(255,255,255,0.04) 0%, rgba(255,255,255,0.01) 80%, transparent 100%);
}

/* ===== 卡片 ===== */
.register-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 480px;
  padding: 0 24px;
}
.register-card {
  background: rgba(20, 20, 20, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 36px 32px;
}

/* ===== Logo ===== */
.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 28px;
}
.logo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: #a0a0a0;
}
.logo-svg {
  width: 24px;
  height: 24px;
}
.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #e0e0e0;
}

/* ===== 标题 ===== */
.title-area {
  margin-bottom: 28px;
}
.title {
  font-size: 26px;
  font-weight: 700;
  color: #f0f0f0;
  margin: 0 0 8px 0;
}
.subtitle {
  font-size: 14px;
  color: #777;
  margin: 0;
}

/* ===== 表单 ===== */
.register-form {
  margin-top: 4px;
}

.register-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  box-shadow: none;
}
.register-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.2);
}
.register-form :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.08);
}
.register-form :deep(.el-input__inner) {
  color: #e0e0e0;
  font-size: 14px;
}
.register-form :deep(.el-input__inner::placeholder) {
  color: #666;
}
.register-form :deep(.el-input__prefix) {
  color: #666;
}
.register-form :deep(.el-input__suffix) {
  color: #666;
}

.icon-svg {
  width: 16px;
  height: 16px;
  color: #666;
}

/* ===== 按钮 ===== */
.register-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  background: #e0e0e0;
  border: none;
  color: #1a1a1a;
  transition: all 0.2s;
}
.register-btn:hover {
  background: #ffffff;
  transform: translateY(-1px);
  box-shadow: 0 4px 20px rgba(255,255,255,0.1);
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 14px;
  color: #888;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.2s;
}
.login-btn:hover {
  color: #c0c0c0;
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.03);
}

/* ===== 响应式 ===== */
@media (max-width: 480px) {
  .register-card {
    padding: 24px 18px;
    border-radius: 12px;
  }
  .title {
    font-size: 22px;
  }
}
</style>
