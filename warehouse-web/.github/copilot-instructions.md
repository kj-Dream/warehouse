# Copilot 使用说明（项目专用）

目的：为 AI 编程助手提供快速可用的上下文，帮助在本仓库中编写、修改和审查代码。

1) 项目一览（大图）
- 技术栈：Vue 3 + Vite + Vue Router + Element Plus。前端通过 Axios 调用后端接口。
- 入口：`src/main.js`（挂载 ElementPlus 和路由），根组件为 `src/App.vue`。

2) 架构与数据流要点
- 路由：使用 `src/router/index.js`，HTML5 history 模式；根路径 `/` 重定向到 `/login`。
- API：所有后台请求通过 `src/api/*.js` 封装。示例：`src/api/auth.js` 创建了一个 Axios 实例，`baseURL: '/api'`，响应拦截器返回 `response.data`。
- UI：使用 Element Plus 组件库；页面组件放在 `src/views/`（如 `Login.vue`、`Register.vue`）。

3) 关键约定（必须遵守）
- 所有网络请求应使用 `src/api` 下的封装请求实例（避免直接在组件中调用 `axios`）。参考：[src/api/auth.js](src/api/auth.js#L1-L50)
- 后端返回包裹层已在拦截器剥离，调用者直接期待接口返回的业务数据（无二次 `.data`）。
- 请求基路径以 `/api` 前缀约定，后端代理/部署需保证相同前缀。

4) 开发 / 构建 / 调试 命令（来自 package.json）
- 本地开发：`npm run dev` → 启动 Vite 开发服务器。
- 打包：`npm run build`。
- 预览打包结果：`npm run preview`。

5) 常见修改场景与示例
- 新增页面：在 `src/views/` 添加组件，并在 `src/router/index.js` 注册路由。
- 新增后端接口：在 `src/api/` 新建模块，导出调用函数；保持使用默认请求实例（`baseURL:'/api'` 与响应拦截器）。

6) 注意事项和巡检点（AI 代码改动需检查）
- 不要移除或改变 `src/api/auth.js` 中的响应拦截器行为，除非同时更新所有调用点。
- 修改 `baseURL` 前请确认后端或本地代理配置一致。
- 避免在组件中重复创建请求实例；优先复用 `src/api` 中的封装。

7) 参考文件
- 入口：[src/main.js](src/main.js#L1-L50)
- 路由：[src/router/index.js](src/router/index.js#L1-L200)
- API 示例：[src/api/auth.js](src/api/auth.js#L1-L200)
- 根组件：[src/App.vue](src/App.vue#L1-L200)

如果以上内容有遗漏或你想补充特定开发流程（例如后端代理、环境变量或 CI 命令），请告诉我我会合并进来。
