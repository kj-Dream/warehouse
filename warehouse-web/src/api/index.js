/*
 * ============================================================
 * 【index.js】API 接口函数封装
 * ============================================================
 * 这个文件把每个接口调用封装成一个函数，
 * 页面组件里直接调 login(data) 就行。
 *
 * 接口清单：
 *   认证相关：
 *     login(data)     → POST /api/auth/login      登录
 *     register(data)  → POST /api/auth/register    注册
 *     getCaptcha()    → GET  /api/auth/captcha     获取验证码
 *     logout()        → POST /api/auth/logout      退出登录
 *     getUserInfo()   → GET  /api/auth/me          获取当前用户
 *
 *   用户管理相关：
 *     getUserPage(params)     → GET  /api/user/page         分页列表
 *     getUserById(id)         → GET  /api/user/{id}         单个用户
 *     createUser(data)        → POST /api/user               新增
 *     updateUser(data)        → PUT  /api/user               修改
 *     deleteUser(id)          → DELETE /api/user/{id}        删除
 *     setUserStatus(id, status) → PUT /api/user/{id}/status  启禁
 *     resetUserPassword(id)   → PUT  /api/user/{id}/reset-password  重置密码
 *     batchDeleteUsers(ids)   → POST /api/user/batch-delete  批量删除
 *     batchSetUserStatus(ids, status) → PUT /api/user/batch-status  批量启禁
 *
 *   菜单相关：
 *     getMenus()      → GET  /api/menu              获取菜单
 * ============================================================
 */
import request from './auth'

// ==================== 认证相关 ====================

/** 用户登录 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data  // {username, password, captcha}
  })
}

/** 用户注册 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data  // {username, password, realName, phone, email}
  })
}

/** 获取数学验证码 */
export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get'
  })
}

/** 退出登录 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/** 获取当前登录用户信息（从 Session 中读取） */
export function getUserInfo() {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

// ==================== 用户管理 ====================

/** 分页查询用户列表 */
export function getUserPage(params) {
  return request({
    url: '/user/page',
    method: 'get',
    params  // {pageNum, pageSize, username, realName, roleId, status}
  })
}

/** 新增用户 */
export function createUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

/** 修改用户信息 */
export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

/** 删除用户 */
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

/** 启用/禁用用户 */
export function setUserStatus(id, status) {
  return request({
    url: `/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/** 重置用户密码 */
export function resetUserPassword(id) {
  return request({
    url: `/user/${id}/reset-password`,
    method: 'put'
  })
}

/** 批量删除用户 */
export function batchDeleteUsers(ids) {
  return request({
    url: '/user/batch-delete',
    method: 'post',
    data: { ids }
  })
}

/** 批量启用/禁用用户 */
export function batchSetUserStatus(ids, status) {
  return request({
    url: '/user/batch-status',
    method: 'put',
    data: { ids, status }
  })
}

// ==================== 角色管理 ====================

/** 分页查询角色列表 */
export function getRolePage(params) {
  return request({
    url: '/role/page',
    method: 'get',
    params  // {pageNum, pageSize, roleName, roleKey, status}
  })
}

/** 查询单个角色（含菜单权限） */
export function getRoleById(id) {
  return request({
    url: `/role/${id}`,
    method: 'get'
  })
}

/** 新增角色 */
export function createRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data  // {roleName, roleKey, description, menuIds: [1,2,3]}
  })
}

/** 修改角色 */
export function updateRole(data) {
  return request({
    url: '/role',
    method: 'put',
    data  // {id, roleName?, roleKey?, description?, menuIds?}
  })
}

/** 删除角色 */
export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  })
}

/** 启用/禁用角色 */
export function setRoleStatus(id, status) {
  return request({
    url: `/role/${id}/status`,
    method: 'put',
    data: { status }
  })
}

/** 批量删除角色 */
export function batchDeleteRoles(ids) {
  return request({
    url: '/role/batch-delete',
    method: 'post',
    data: { ids }
  })
}

/** 批量启用/禁用角色 */
export function batchSetRoleStatus(ids, status) {
  return request({
    url: '/role/batch-status',
    method: 'put',
    data: { ids, status }
  })
}

/** 获取所有启用的角色（下拉框用） */
export function getAllRoles() {
  return request({
    url: '/role/all',
    method: 'get'
  })
}

// ==================== 菜单 ====================

/** 获取系统菜单（树形结构，供角色权限分配和侧边栏使用） */
export function getMenus() {
  return request({
    url: '/menu',
    method: 'get'
  })
}
