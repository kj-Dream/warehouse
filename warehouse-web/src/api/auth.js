/*
 * ============================================================
 * 【auth.js】axios 请求工具的封装
 * ============================================================
 * axios 是前端用来发 HTTP 请求的工具，相当于前端的"邮递员"。
 *
 * 这里做了三件事：
 *   1. 创建一个 axios 实例，设 baseURL='/api'
 *   2. withCredentials: true → 让浏览器每次请求都自动携带 Cookie
 *      （Cookie 里有 JSESSIONID，后端靠它识别是谁在访问）
 *      这就是"Session 会话管理"的前端配合！
 *   3. 响应拦截器 → 直接返回 response.data
 */
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',          // 所有请求前面自动加 /api
  timeout: 10000,           // 10秒超时，防止请求卡死
  withCredentials: true     // 携带 Cookie（Session 依赖这个！）
})

// 响应拦截器：在拿到数据之前做一层处理
request.interceptors.response.use(
  (response) => {
    return response.data  // 直接返回 data，省去 .data.data 的麻烦
  },
  (error) => {
    console.error('请求失败:', error)
    return Promise.reject(error)
  }
)

export default request
