# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

云芯仓储 (YunXin Warehouse) — a warehouse management system with a Spring Boot backend and Vue 3 frontend.

## Development Commands

### Backend (`warehouse/`)
```bash
# Build & run (from warehouse/ directory)
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=LoginControllerTest

# Package JAR
mvn package -DskipTests
```
- Backend starts on port **8080**.
- Requires MySQL database `yun` running on `localhost:3306` (root/123456).

### Frontend (`warehouse-web/`)
```bash
npm run dev      # Start Vite dev server on port 5173
npm run build    # Production build
npm run preview  # Preview production build
```
- Vite proxies `/api` requests to `http://localhost:8080` — no CORS issues in dev.

## Architecture

### Backend (Spring Boot 3.5.15, Java 17, MyBatis 3, MySQL)

**Layered architecture: Controller → Service → Mapper → DB**

- **`controller/`** — REST endpoints annotated `@RestController` + `@RequestMapping`. Receives requests, validates params, delegates to services, returns `Result<T>`.
- **`service/`** — Interface + `service/impl/` implementation pattern. Contains business logic.
- **`mapper/`** — MyBatis mapper interfaces (`@Mapper`). Each interface corresponds to an XML file in `src/main/resources/dao/`.
- **`pojo/`** — Entity classes matching database tables (e.g., `SysUser`, `Product`, `StockIn`).
- **`dto/`** — Request/response objects: `Result<T>` (unified response), `LoginRequest`, `LoginResponse`, etc.
- **`config/`** — `WebConfig.java`: CORS configuration allowing all origins with credentials.
- **`util/`** — `PasswordUtil.java`: BCrypt password encoding/matching/auto-upgrade from legacy plain-text passwords.

**Key patterns:**
- **Unified response format**: All endpoints return `Result<T>` with `{code: 200/500, msg: "...", data: ...}`. Success = `Result.success(data)`, Error = `Result.error("message")`.
- **Session-based auth**: `HttpSession` stores user info after login (`session.setAttribute("user", response)`). Retrieved via `GET /api/auth/me`. Logout destroys the session.
- **BCrypt passwords**: `spring-security-crypto` (BCrypt only, NOT full Spring Security). Passwords stored as `$2a$...` hashes. Legacy plain-text passwords auto-upgraded on successful login.
- **CAPTCHA**: Math expression CAPTCHA (`num1 + num2 = ?`), answer stored in session, validated at login.
- **MyBatis XML mappers**: All SQL in `resources/dao/*.xml` using `namespace` matching mapper interface. Dynamic SQL with `<if>`, `<where>`, `<set>`, `<foreach>`. `useGeneratedKeys="true"` for auto-increment IDs.
- **Database**: MySQL `yun` database, tables prefixed `sys_` (system), plain names for business tables. Database fields use snake_case, Java uses camelCase — mapped via `<resultMap>`.

### Frontend (Vue 3, Vite, Element Plus, Vue Router, Axios, ECharts)

- **`src/main.js`** — Entry point. Mounts ElementPlus + Router onto `#app`.
- **`src/App.vue`** — Root component with `<router-view>`.
- **`src/router/index.js`** — HTML5 history mode routes. `/` redirects to `/dashboard`; `/login` and `/register` are standalone pages; all other pages are children of `Layout.vue`. Most routes use lazy loading (`() => import(...)`).
- **`src/views/Layout.vue`** — Main layout: collapsible sidebar, top header with breadcrumbs + user dropdown, `<router-view>` content area. Menus loaded dynamically from backend `GET /api/menu`. Icon names from the database are mapped to Element Plus icon components via `iconMap`.
- **`src/api/auth.js`** — Axios instance: `baseURL: '/api'`, `timeout: 10000`, `withCredentials: true`. Response interceptor returns `response.data` — callers get the unwrapped body directly (no `.data.data`).
- **`src/api/index.js`** — All API functions: auth (login/register/captcha/logout/me), user CRUD, role CRUD, menus, product CRUD, warehouse/location, stock in/out, inventory, reports. All use the shared `request` instance from `auth.js`.
- **`src/stores/audit.js`** — Shared reactive state for pending audit count badge.
- **`src/utils/confirm.js`** — Confirmation dialog utility.

**Key patterns:**
- Axios interceptor already unwraps the response — callers access business data at `res.data` (not `res.data.data`), and check `res.code === 200`.
- All API calls go through `src/api/index.js` functions; never call `axios` directly in components.
- Menu/permission system: backend `sys_menu` table → `/api/menu` returns tree → `Layout.vue` renders dynamic sidebar. Role-menu assignments managed in role CRUD.

### Request Flow Example (Login)

1. User submits form → `login(data)` in `src/api/index.js`
2. `POST /api/auth/login` → Vite proxy → `LoginController.login()`
3. Controller validates CAPTCHA against session → calls `LoginService.login()`
4. `LoginServiceImpl`: finds user in DB, verifies BCrypt password, checks status, loads role, stores user in session
5. Returns `Result<LoginResponse>` → Axios interceptor strips to `{code, msg, data}`
6. Frontend checks `res.code === 200`, stores `res.data` in localStorage, redirects to `/dashboard`

## Database

- MySQL database: `yun`
- Connection: `jdbc:mysql://localhost:3306/yun`
- Credentials: `root` / `123456`
- MyBatis mapper XML locations: `classpath:dao/*.xml`
