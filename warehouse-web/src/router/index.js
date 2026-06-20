/*
 * ============================================================
 * 路由配置 — 云芯仓储管理系统
 * ============================================================
 */
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/dashboard/index.vue'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表盘' }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'UserManagement',
        component: () => import('../views/system/user/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'RoleManagement',
        component: () => import('../views/system/role/index.vue'),
        meta: { title: '角色管理' }
      },
      // 商品管理
      {
        path: 'product/list',
        name: 'ProductList',
        component: () => import('../views/product/list/index.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'product/category',
        name: 'ProductCategory',
        component: () => import('../views/product/category/index.vue'),
        meta: { title: '商品分类' }
      },
      // 仓库管理
      {
        path: 'warehouse/list',
        name: 'WarehouseList',
        component: () => import('../views/warehouse/list/index.vue'),
        meta: { title: '仓库列表' }
      },
      {
        path: 'warehouse/location',
        name: 'LocationManagement',
        component: () => import('../views/warehouse/location/index.vue'),
        meta: { title: '库位管理' }
      },
      // 出入库管理
      {
        path: 'stock/in',
        name: 'StockIn',
        component: () => import('../views/stock/in/index.vue'),
        meta: { title: '入库管理' }
      },
      {
        path: 'stock/out',
        name: 'StockOut',
        component: () => import('../views/stock/out/index.vue'),
        meta: { title: '出库管理' }
      },
      // 库存管理
      {
        path: 'inventory/list',
        name: 'InventoryList',
        component: () => import('../views/inventory/list/index.vue'),
        meta: { title: '库存查询' }
      },
      {
        path: 'inventory/warning',
        name: 'InventoryWarning',
        component: () => import('../views/inventory/warning/index.vue'),
        meta: { title: '库存预警' }
      },
      // 统计报表
      {
        path: 'report/in',
        name: 'ReportIn',
        component: () => import('../views/report/in/index.vue'),
        meta: { title: '入库统计' }
      },
      {
        path: 'report/out',
        name: 'ReportOut',
        component: () => import('../views/report/out/index.vue'),
        meta: { title: '出库统计' }
      },
      {
        path: 'report/inventory',
        name: 'ReportInventory',
        component: () => import('../views/report/inventory/index.vue'),
        meta: { title: '库存统计' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
