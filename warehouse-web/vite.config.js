/*
 * ============================================================
 * Vite 构建配置 — 跨域代理
 * ============================================================
 * 开发时，前端跑在 localhost:5173，后端跑在 localhost:8080。
 * 如果前端直接调后端接口，浏览器会报跨域错误。
 *
 * 这里配置了 proxy 代理：
 *   前端的 /api 开头的请求 → 自动转发到 http://localhost:8080
 *
 * 所以前端调 /api/auth/login，实际访问的是：
 *   http://localhost:5173/api/auth/login → 转发到
 *   http://localhost:8080/api/auth/login
 *
 * 这就解决了跨域问题（浏览器觉得是同源的）。
 * ============================================================
 */
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,  // 前端启动端口
    proxy: {
      '/api': {  // 所有 /api 开头的请求
        target: 'http://localhost:8080',  // 转发到后端地址
        changeOrigin: true
      }
    }
  }
})
