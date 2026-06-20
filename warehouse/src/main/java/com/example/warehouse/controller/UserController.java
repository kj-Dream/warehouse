/*
 * ============================================================
 * 【UserController】用户管理的"前台接待处"
 * ============================================================
 * 这是用户管理模块的 Controller，负责处理前端发来的
 * 所有和"用户管理"相关的请求。
 *
 * 功能清单：
 *   1. GET    /api/user/page          → 分页查询用户列表
 *   2. GET    /api/user/{id}          → 根据ID查询单个用户
 *   3. POST   /api/user               → 新增用户
 *   4. PUT    /api/user               → 修改用户信息
 *   5. DELETE /api/user/{id}          → 删除用户
 *   6. PUT    /api/user/{id}/status   → 启用/禁用用户
 *   7. PUT    /api/user/{id}/reset-password → 重置密码
 *   8. POST   /api/user/batch-delete  → 批量删除（新增）
 *   9. PUT    /api/user/batch-status  → 批量启用/禁用（新增）
 *
 * RESTful 风格说明：
 *   - GET    读资源（查）
 *   - POST   建资源（增）
 *   - PUT    改资源（改）
 *   - DELETE 删资源（删）
 * ============================================================
 */
package com.example.warehouse.controller;

import com.example.warehouse.dto.*;
import com.example.warehouse.pojo.SysUser;
import com.example.warehouse.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 分页查询用户列表
     * GET /api/user/page?pageNum=1&pageSize=10&username=admin&status=1
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(UserPageRequest request) {
        try {
            Map<String, Object> data = userService.listUsers(request);
            return Result.success("查询成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询单个用户
     * GET /api/user/1
     */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Integer id) {
        try {
            SysUser user = userService.getById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success("查询成功", user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增用户（管理员操作）
     * POST /api/user
     * 前端传：{username, password?, realName, phone, email, roleId}
     *
     * 管理员创建的用户默认 status=1（直接启用），
     * 密码会被 BCrypt 加密存储。
     */
    @PostMapping
    public Result<Void> create(@RequestBody UserCreateRequest request) {
        try {
            // 参数校验
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
                return Result.error("真实姓名不能为空");
            }
            if (request.getRoleId() == null) {
                return Result.error("请选择角色");
            }

            userService.createUser(request);
            return Result.success("新增用户成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改用户信息
     * PUT /api/user
     * 前端传：{id, username?, realName?, phone?, email?, roleId?, status?}
     */
    @PutMapping
    public Result<Void> update(@RequestBody UserUpdateRequest request) {
        try {
            if (request.getId() == null) {
                return Result.error("用户ID不能为空");
            }

            userService.updateUser(request);
            return Result.success("修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     * DELETE /api/user/1
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用/禁用用户（单个用户状态控制）
     * PUT /api/user/1/status
     * 前端传：{status: 1} 或 {status: 0}
     */
    @PutMapping("/{id}/status")
    public Result<Void> setStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        try {
            Integer status = body.get("status");
            if (status == null) {
                return Result.error("状态不能为空");
            }

            userService.setStatus(id, status);
            String msg = status == 1 ? "用户已启用" : "用户已禁用";
            return Result.success(msg, null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置用户密码为默认密码
     * PUT /api/user/1/reset-password
     * 重置后的密码为 "123456"（BCrypt 加密存储）
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Integer id) {
        try {
            userService.resetPassword(id);
            return Result.success("密码已重置为 123456", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除用户（新增）
     * POST /api/user/batch-delete
     * 前端传：{ids: [1, 2, 3]}
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Integer>> body) {
        try {
            List<Integer> ids = body.get("ids");
            userService.batchDelete(ids);
            return Result.success("批量删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量启用/禁用用户（新增）
     * PUT /api/user/batch-status
     * 前端传：{ids: [1, 2, 3], status: 1}
     */
    @PutMapping("/batch-status")
    public Result<Void> batchSetStatus(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) body.get("ids");
            Integer status = (Integer) body.get("status");
            if (status == null) {
                return Result.error("状态不能为空");
            }
            userService.batchSetStatus(ids, status);
            String msg = status == 1 ? "已批量启用" : "已批量禁用";
            return Result.success(msg, null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
