<template>
  <div class="layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <svg class="logo-svg" viewBox="0 0 32 32" fill="none" v-if="!isCollapse">
          <rect x="2" y="14" width="20" height="16" rx="2" stroke="#fff" stroke-width="2"/>
          <path d="M22 8L30 13V27L22 31V8Z" stroke="#fff" stroke-width="2" stroke-linejoin="round"/>
          <path d="M22 8L2 14" stroke="#fff" stroke-width="2"/>
          <path d="M30 13L10 19" stroke="#fff" stroke-width="2"/>
        </svg>
        <svg class="logo-svg-mini" viewBox="0 0 32 32" fill="none" v-else>
          <rect x="2" y="14" width="20" height="16" rx="2" stroke="#fff" stroke-width="2"/>
          <path d="M22 8L30 13V27L22 31V8Z" stroke="#fff" stroke-width="2" stroke-linejoin="round"/>
          <path d="M22 8L2 14" stroke="#fff" stroke-width="2"/>
          <path d="M30 13L10 19" stroke="#fff" stroke-width="2"/>
        </svg>
        <span v-if="!isCollapse" class="logo-text">云芯仓储</span>
      </div>

      <!--
        动态菜单：
          菜单数据不再写死在代码里，而是登录后从后端 /api/menu 获取。
          后端从数据库 sys_menu 表读取并组装成树形结构返回。
          这样以后要改菜单，只需要改数据库，不用改前端代码。
      -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <template v-for="item in menuList" :key="item.id">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="item.children && item.children.length" :index="item.path">
            <template #title>
              <el-icon><component :is="iconMap[item.icon] || 'Menu'" /></el-icon>
              <span>{{ item.name }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.id"
              :index="'/' + child.path"
            >
              <el-icon><component :is="iconMap[child.icon] || 'Menu'" /></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 无子菜单（一级菜单） -->
          <el-menu-item v-else :index="'/' + item.path">
            <el-icon><component :is="iconMap[item.icon] || 'Menu'" /></el-icon>
            <span>{{ item.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <!-- 右侧主体 -->
    <div class="main">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              {{ userInfo?.realName || userInfo?.username || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Setting, User, UserFilled, List, Folder, OfficeBuilding,
  MapLocation, Download, Upload, Box, Search, Bell,
  DataAnalysis, TrendCharts, PieChart, Fold, Expand, ArrowDown,
  Menu
} from '@element-plus/icons-vue'
import { getMenus, logout, getUserInfo } from '../api/index.js'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

// 从 localStorage 读取用户信息（登录时存的）
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

// 菜单列表：从后端动态加载，初始为空
const menuList = ref([])

// 数据库 icon 名称 → Element Plus 图标组件 的映射表
// 数据库存的是字符串（如 "setting"），前端需要转成实际的图标组件
const iconMap = {
  'setting': Setting,
  'user': User,
  'team': UserFilled,
  'list': List,
  'folder': Folder,
  'goods': Box,
  'warehouse': OfficeBuilding,
  'building': OfficeBuilding,
  'map': MapLocation,
  'inbox': DataAnalysis,
  'download': Download,
  'upload': Upload,
  'package': Box,
  'search': Search,
  'alert': Bell,
  'bar-chart': TrendCharts,
  'trending-up': TrendCharts,
  'trending-down': DataAnalysis,
  'pie-chart': PieChart
}

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 面包屑
const breadcrumb = computed(() => {
  const path = route.path
  for (const menu of menuList.value) {
    if (menu.children) {
      const child = menu.children.find(c => '/' + c.path === path)
      if (child) return menu.name + ' / ' + child.name
    }
  }
  if (path === '/dashboard') return ''
  return ''
})

/**
 * 从后端加载菜单
 * 页面加载时调用一次，拿到菜单数据渲染侧边栏。
 */
const loadMenus = async () => {
  try {
    const res = await getMenus()
    if (res.code === 200) {
      menuList.value = res.data
    }
  } catch (err) {
    console.error('菜单加载失败，使用默认菜单:', err)
  }
}

/**
 * 尝试从 Session 恢复用户信息
 * 场景：用户刷新页面后，localStorage 可能被清，
 * 但 Session 还活着，从 /api/auth/me 拿回用户信息。
 */
const restoreUserInfo = async () => {
  if (userInfo.value && userInfo.value.id) return  // 已有用户信息，不用恢复

  try {
    const res = await getUserInfo()
    if (res.code === 200 && res.data) {
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
  } catch (err) {
    // Session 也过期了，跳回登录页
    console.error('获取用户信息失败')
  }
}

// 用户下拉菜单操作
const handleUserCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 清除前端存储
    localStorage.removeItem('userInfo')
    localStorage.removeItem('autoLoginExpire')
    // 调后端退出接口（销毁 Session）
    try {
      await logout()
    } catch (e) {
      // 即使后端退出失败，前端也要跳转
    }
    router.push('/login')
  }
}

// 页面加载时加载菜单和恢复用户信息
onMounted(() => {
  loadMenus()
  restoreUserInfo()
})
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ===== 侧边栏 ===== */
.aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: rgba(0, 0, 0, 0.15);
  flex-shrink: 0;
}
.logo-svg { width: 22px; height: 22px; flex-shrink: 0; }
.logo-svg-mini { width: 22px; height: 22px; flex-shrink: 0; }
.logo-text { white-space: nowrap; }
.el-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

/* ===== 右侧主体 ===== */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ===== 顶栏 ===== */
.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
  z-index: 10;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #666;
}
.collapse-btn:hover {
  color: #409EFF;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}
.user-info:hover {
  color: #409EFF;
}

/* ===== 内容区 ===== */
.content {
  flex: 1;
  overflow-y: auto;
  background: #f0f2f5;
  padding: 20px;
}
</style>
