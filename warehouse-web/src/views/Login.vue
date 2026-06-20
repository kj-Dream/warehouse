<!--
  ============================================================
  【Login.vue】登录页面 — 灵感来自 21st.dev Minimal Auth Page
  ============================================================
  视觉风格参考：
    - 粒子背景动画（Particles 效果）
    - 辐射渐变光晕背景
    - 居中卡片式布局
    - 简洁的排版风格

  功能包含：
    1. 用户名 + 密码登录
    2. 数学验证码校验
    3. 记住密码（localStorage 存储）
    4. 自动登录（7天内免登录）
    5. 跳转注册页

  技术点：
    - canvas 实现粒子动画背景
    - Element Plus 表单组件
    - localStorage 实现记住密码和自动登录
  ============================================================
-->
<template>
  <div class="login-wrapper">
    <!-- ===== 粒子动画背景（Canvas） ===== -->
    <canvas ref="particleCanvas" class="particle-canvas"></canvas>

    <!-- ===== 辐射渐变光晕层 ===== -->
    <div aria-hidden="true" class="glow-layer">
      <div class="glow-orb glow-orb-1" />
      <div class="glow-orb glow-orb-2" />
      <div class="glow-orb glow-orb-3" />
    </div>

    <!-- ===== 主内容区 ===== -->
    <div class="login-container">
      <div class="login-card">
        <!-- Logo 区域 -->
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
          <h1 class="title">登录或注册</h1>
          <p class="subtitle">欢迎使用云芯仓库管理系统</p>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="0"
          size="large"
          class="login-form"
        >
          <!-- 用户名 -->
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="UserIcon"
              clearable
            />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="LockIcon"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item prop="captcha">
            <div class="captcha-row">
              <el-input
                v-model="form.captcha"
                placeholder="验证码答案"
                :prefix-icon="KeyIcon"
                class="captcha-input"
                @keyup.enter="handleLogin"
              />
              <div class="captcha-display" @click="refreshCaptcha" title="点击刷新验证码">
                <span class="captcha-text">{{ captchaExpression }}</span>
              </div>
            </div>
          </el-form-item>

          <!-- 记住密码 & 自动登录 -->
          <div class="options-row">
            <el-checkbox v-model="rememberMe" label="记住密码" size="small" />
            <el-checkbox v-model="autoLogin" label="自动登录" size="small" />
          </div>

          <!-- 登录按钮 -->
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>

          <!-- 注册入口 -->
          <el-form-item>
            <el-button class="register-btn" @click="goRegister">
              没有账号？去注册
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 底部提示 -->
        <p class="footer-text">
          登录即表示同意
          <a href="#" class="footer-link">服务条款</a>
          和
          <a href="#" class="footer-link">隐私政策</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * ============================================================
 * 登录页面逻辑
 * ============================================================
 * 核心流程：
 *   1. 页面加载 → 初始化粒子动画 → 获取验证码
 *   2. 用户输入 → 表单校验 → 调后端接口
 *   3. 登录成功 → 存用户信息 → 跳转仪表盘
 *   4. 记住密码：把用户名密码存 localStorage
 *   5. 自动登录：设置7天过期标记，下次打开自动登录
 */
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getCaptcha } from '../api/index.js'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)
const autoLogin = ref(false)
const captchaExpression = ref('点击获取验证码')
const particleCanvas = ref(null)

// 图标组件（Element Plus 内置图标，直接字符串引用）
const UserIcon = (() => {
  // 简单 SVG 图标组件
  const comp = { template: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>' }
  return comp
})()
const LockIcon = (() => {
  const comp = { template: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>' }
  return comp
})()
const KeyIcon = (() => {
  const comp = { template: '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon-svg"><path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"/></svg>' }
  return comp
})()

// 表单数据
const form = reactive({
  username: '',
  password: '',
  captcha: ''
})

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, message: '密码长度不能小于 3 位', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
}

// ========== 验证码相关 ==========

/**
 * 获取数学验证码
 * 调后端 GET /api/auth/captcha 接口获取数学题
 * 后端会在 Session 里存正确答案
 */
const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    if (res.code === 200) {
      captchaExpression.value = res.data.expression  // 比如 "3 + 5 = ?"
    }
  } catch (err) {
    captchaExpression.value = '获取失败，点此重试'
  }
}

// ========== 登录相关 ==========

/**
 * 处理登录
 * 1. 表单校验
 * 2. 调后端登录接口
 * 3. 处理记住密码和自动登录
 */
const handleLogin = async () => {
  // 表单校验
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      username: form.username,
      password: form.password,
      captcha: parseInt(form.captcha)  // 转成数字发给后端
    })

    if (res.code === 200) {
      // 登录成功
      ElMessage.success('登录成功')

      // 保存用户信息到 localStorage（供 Layout 读取显示用户名等）
      localStorage.setItem('userInfo', JSON.stringify(res.data))

      // 记住密码：把用户名和密码存 localStorage
      if (rememberMe.value) {
        localStorage.setItem('rememberedAccount', JSON.stringify({
          username: form.username,
          password: form.password
        }))
      } else {
        localStorage.removeItem('rememberedAccount')
      }

      // 自动登录：设置标记，下次打开页面自动跳转
      if (autoLogin.value) {
        // 存一个7天后的时间戳作为过期标记
        const expireTime = Date.now() + 7 * 24 * 60 * 60 * 1000
        localStorage.setItem('autoLoginExpire', expireTime.toString())
      } else {
        localStorage.removeItem('autoLoginExpire')
      }

      // 跳转到首页仪表盘
      router.push('/dashboard')
    } else {
      ElMessage.error(res.msg || '登录失败')
      // 刷新验证码（防止暴力破解）
      refreshCaptcha()
    }
  } catch (err) {
    ElMessage.error('请求失败，请检查网络连接')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// ========== 导航 ==========

/** 跳转到注册页 */
const goRegister = () => {
  router.push('/register')
}

// ========== 粒子动画（Canvas） ==========

/**
 * 粒子动画效果
 * 在 Canvas 上绘制随机运动的粒子点，
 * 粒子之间距离够近时会画连线，形成"网络"效果。
 * 鼠标移动会影响粒子的运动方向。
 */
let animationId = null
let mouseX = -1000
let mouseY = -1000

const initParticles = () => {
  const canvas = particleCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  let width = (canvas.width = window.innerWidth)
  let height = (canvas.height = window.innerHeight)

  // 粒子配置
  const particleCount = 100        // 粒子数量
  const connectionDistance = 150   // 连线距离
  const particleColor = '#666666'  // 粒子颜色
  const particleOpacity = 0.5      // 粒子透明度

  // 生成粒子数组
  const particles = Array.from({ length: particleCount }, () => ({
    x: Math.random() * width,        // 随机X坐标
    y: Math.random() * height,       // 随机Y坐标
    vx: (Math.random() - 0.5) * 0.5, // X方向速度
    vy: (Math.random() - 0.5) * 0.5, // Y方向速度
    size: Math.random() * 2 + 1      // 粒子大小（1~3像素）
  }))

  /** 每一帧的绘制逻辑 */
  const draw = () => {
    ctx.clearRect(0, 0, width, height)

    // 遍历每个粒子
    for (let i = 0; i < particles.length; i++) {
      const p = particles[i]

      // 移动粒子
      p.x += p.vx
      p.y += p.vy

      // 边界检测：碰到边缘就反弹
      if (p.x < 0 || p.x > width) p.vx *= -1
      if (p.y < 0 || p.y > height) p.vy *= -1

      // 鼠标影响：鼠标附近的粒子会微微靠近鼠标
      const dx = mouseX - p.x
      const dy = mouseY - p.y
      const dist = Math.sqrt(dx * dx + dy * dy)
      if (dist < 200) {
        p.vx += dx * 0.00005
        p.vy += dy * 0.00005
      }

      // 速度衰减（防止粒子跑太快）
      p.vx *= 0.999
      p.vy *= 0.999

      // 画粒子
      ctx.beginPath()
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(102, 102, 102, ${particleOpacity})`
      ctx.fill()

      // 画连线：和后面的粒子比距离，够近就画线
      for (let j = i + 1; j < particles.length; j++) {
        const q = particles[j]
        const dx2 = p.x - q.x
        const dy2 = p.y - q.y
        const dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2)

        if (dist2 < connectionDistance) {
          // 距离越近，线越不透明
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

  // 窗口大小变化时更新 Canvas 尺寸
  const handleResize = () => {
    width = canvas.width = window.innerWidth
    height = canvas.height = window.innerHeight
  }
  window.addEventListener('resize', handleResize)

  // 鼠标移动监听
  const handleMouseMove = (e) => {
    mouseX = e.clientX
    mouseY = e.clientY
  }
  window.addEventListener('mousemove', handleMouseMove)

  // 保存清理函数引用
  canvas._cleanup = () => {
    window.removeEventListener('resize', handleResize)
    window.removeEventListener('mousemove', handleMouseMove)
  }
}

// ========== 自动登录检测 ==========

/**
 * 检查是否需要自动登录
 * 场景：用户上次登录时勾了"自动登录"，
 * 且 token 还没过期（7天内）
 */
const checkAutoLogin = async () => {
  // 先检查是否已登录
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo)
      if (user && user.id) {
        // 已登录，直接跳转
        router.push('/dashboard')
        return
      }
    } catch (e) {
      // userInfo 解析失败，继续往下走
    }
  }

  // 检查自动登录标记
  const autoLoginExpire = localStorage.getItem('autoLoginExpire')
  if (autoLoginExpire && Date.now() < parseInt(autoLoginExpire)) {
    // 还在有效期内，尝试自动登录
    const remembered = localStorage.getItem('rememberedAccount')
    if (remembered) {
      try {
        const account = JSON.parse(remembered)
        form.username = account.username
        form.password = account.password
        rememberMe.value = true
        autoLogin.value = true
        // 先获取验证码
        await refreshCaptcha()
        // 自动提交登录
        await handleLogin()
      } catch (e) {
        // 自动登录失败，留在登录页
      }
    }
  }
}

// ========== 生命周期 ==========

onMounted(async () => {
  // 1. 初始化粒子背景动画
  initParticles()

  // 2. 获取验证码
  await refreshCaptcha()

  // 3. 读取记住的账号密码
  const remembered = localStorage.getItem('rememberedAccount')
  if (remembered) {
    try {
      const account = JSON.parse(remembered)
      form.username = account.username
      form.password = account.password
      rememberMe.value = true
    } catch (e) {
      // 数据损坏，忽略
    }
  }

  // 4. 读取自动登录标记
  const autoLoginExpire = localStorage.getItem('autoLoginExpire')
  if (autoLoginExpire && Date.now() < parseInt(autoLoginExpire)) {
    autoLogin.value = true
  }
})

onBeforeUnmount(() => {
  // 清理粒子动画
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  // 清理事件监听
  if (particleCanvas.value && particleCanvas.value._cleanup) {
    particleCanvas.value._cleanup()
  }
})
</script>

<style scoped>
/* ============================================================
   全局容器
   ============================================================ */
.login-wrapper {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #0a0a0a;  /* 深色背景 */
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ===== Canvas 粒子层 ===== */
.particle-canvas {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;  /* Canvas 不拦截鼠标事件 */
}

/* ============================================================
   辐射渐变光晕层（纯 CSS）
   ============================================================ */
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

/* 最大的光晕 - 左上 */
.glow-orb-1 {
  width: 560px;
  height: 320px;
  transform: translateY(-50%) rotate(-45deg);
  background: radial-gradient(
    68.54% 68.72% at 55.02% 31.46%,
    rgba(255, 255, 255, 0.06) 0%,
    rgba(150, 150, 150, 0.02) 50%,
    rgba(255, 255, 255, 0.01) 80%
  );
}

/* 中型光晕 - 右上 */
.glow-orb-2 {
  width: 320px;
  height: 240px;
  transform: translate(5%, -50%) rotate(-45deg);
  background: radial-gradient(
    50% 50% at 50% 50%,
    rgba(255, 255, 255, 0.04) 0%,
    rgba(255, 255, 255, 0.01) 80%,
    transparent 100%
  );
}

/* 较小的光晕 */
.glow-orb-3 {
  width: 320px;
  height: 240px;
  transform: translateY(-50%) rotate(-45deg);
  background: radial-gradient(
    50% 50% at 50% 50%,
    rgba(255, 255, 255, 0.04) 0%,
    rgba(255, 255, 255, 0.01) 80%,
    transparent 100%
  );
}

/* ============================================================
   登录卡片容器
   ============================================================ */
.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 440px;
  padding: 0 24px;
}

.login-card {
  background: rgba(20, 20, 20, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 40px 32px;
}

/* ============================================================
   Logo 区域
   ============================================================ */
.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 32px;
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
  letter-spacing: -0.3px;
}

/* ============================================================
   标题区域
   ============================================================ */
.title-area {
  margin-bottom: 28px;
}

.title {
  font-size: 26px;
  font-weight: 700;
  color: #f0f0f0;
  letter-spacing: -0.5px;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.subtitle {
  font-size: 15px;
  color: #888;
  margin: 0;
  line-height: 1.5;
}

/* ============================================================
   登录表单
   ============================================================ */
.login-form {
  margin-top: 4px;
}

/* Element Plus 表单深色适配 */
.login-form :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  box-shadow: none;
  transition: border-color 0.2s, background 0.2s;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(255, 255, 255, 0.2);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.08);
}

.login-form :deep(.el-input__inner) {
  color: #e0e0e0;
  font-size: 14px;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #666;
}

.login-form :deep(.el-input__prefix) {
  color: #666;
}

.login-form :deep(.el-input__suffix) {
  color: #666;
}

/* ============================================================
   验证码行
   ============================================================ */
.captcha-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.captcha-input {
  flex: 1;
  min-width: 0;
}

.captcha-display {
  flex-shrink: 0;
  width: 130px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  cursor: pointer;
  user-select: none;
  transition: border-color 0.2s, background 0.2s;
}

.captcha-display:hover {
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
}

.captcha-text {
  font-size: 18px;
  font-weight: 600;
  color: #c0c0c0;
  letter-spacing: 2px;
  font-family: 'Consolas', 'Courier New', monospace;
}

/* ============================================================
   记住密码 & 自动登录
   ============================================================ */
.options-row {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding: 0 4px;
}

.options-row :deep(.el-checkbox) {
  margin-right: 0;
}

.options-row :deep(.el-checkbox__label) {
  color: #888;
  font-size: 13px;
}

.options-row :deep(.el-checkbox__inner) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
}

/* ============================================================
   按钮
   ============================================================ */
.login-btn {
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

.login-btn:hover {
  background: #ffffff;
  transform: translateY(-1px);
  box-shadow: 0 4px 20px rgba(255, 255, 255, 0.1);
}

.login-btn:active {
  transform: translateY(0);
}

.register-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 14px;
  color: #888;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.08);
  transition: all 0.2s;
}

.register-btn:hover {
  color: #c0c0c0;
  border-color: rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.03);
}

/* ============================================================
   底部文字
   ============================================================ */
.footer-text {
  text-align: center;
  color: #555;
  font-size: 13px;
  margin-top: 24px;
  line-height: 1.6;
}

.footer-link {
  color: #999;
  text-decoration: underline;
  text-underline-offset: 4px;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #e0e0e0;
}

/* ============================================================
   响应式适配
   ============================================================ */
@media (max-width: 480px) {
  .login-card {
    padding: 28px 20px;
    border-radius: 12px;
  }

  .title {
    font-size: 22px;
  }

  .captcha-display {
    width: 110px;
  }

  .captcha-text {
    font-size: 16px;
  }
}
</style>
