/*
 * ============================================================
 * 【RoleController】角色管理的 REST 接口
 * ============================================================
 * 提供角色管理的全部 CRUD 接口，以及菜单权限的分配功能。
 *
 * 接口清单：
 *   1. GET    /api/role/page        → 分页查询角色列表
 *   2. GET    /api/role/{id}        → 查询单个角色（含菜单权限）
 *   3. POST   /api/role             → 新增角色（含菜单权限）
 *   4. PUT    /api/role             → 修改角色（含重新分配菜单权限）
 *   5. DELETE /api/role/{id}        → 删除角色
 *   6. PUT    /api/role/{id}/status → 启用/禁用角色
 *   7. GET    /api/role/all         → 获取所有启用的角色（下拉框用）
 * ============================================================
 */
package com.example.warehouse.controller;

import com.example.warehouse.dto.Result;
import com.example.warehouse.pojo.SysRole;
import com.example.warehouse.service.RoleService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 分页查询角色列表
     * GET /api/role/page?pageNum=1&pageSize=10&roleName=管理&status=1
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleKey,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> data = roleService.listRoles(pageNum, pageSize, roleName, roleKey, status);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询单个角色（含菜单权限ID列表）
     * GET /api/role/1
     *
     * 返回数据格式：
     *   { role: {id, roleName, roleKey, ...}, menuIds: [1, 4, 5, 6, ...] }
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getById(@PathVariable Integer id) {
        try {
            Map<String, Object> data = roleService.getById(id);
            return Result.success(data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增角色
     * POST /api/role
     *
     * 请求体：
     *   { roleName: "测试角色", roleKey: "test", description: "描述",
     *     menuIds: [1, 4, 5, 6] }
     */
    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        try {
            // 解析角色基本信息
            SysRole role = new SysRole();
            role.setRoleName((String) body.get("roleName"));
            role.setRoleKey((String) body.get("roleKey"));
            role.setDescription((String) body.get("description"));
            if (body.get("status") != null) {
                role.setStatus((Integer) body.get("status"));
            }

            // 参数校验
            if (role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
                return Result.error("角色名称不能为空");
            }
            if (role.getRoleKey() == null || role.getRoleKey().trim().isEmpty()) {
                return Result.error("角色标识不能为空");
            }

            // 解析菜单权限ID列表
            @SuppressWarnings("unchecked")
            List<Integer> menuIds = (List<Integer>) body.get("menuIds");

            roleService.createRole(role, menuIds);
            return Result.success("角色创建成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改角色
     * PUT /api/role
     *
     * 请求体：
     *   { id: 4, roleName: "新名称", description: "新描述",
     *     menuIds: [1, 2, 3] }
     *
     * 注意：menuIds 如果传了，会"先删后插"更新权限；
     * 如果没传，只更新角色基本信息，不动权限。
     */
    @PutMapping
    public Result<Void> update(@RequestBody Map<String, Object> body) {
        try {
            Integer id = (Integer) body.get("id");
            if (id == null) {
                return Result.error("角色ID不能为空");
            }

            // 解析角色基本信息
            SysRole role = new SysRole();
            role.setId(id);
            role.setRoleName((String) body.get("roleName"));
            role.setRoleKey((String) body.get("roleKey"));
            role.setDescription((String) body.get("description"));
            if (body.get("status") != null) {
                role.setStatus((Integer) body.get("status"));
            }

            // 解析菜单权限ID列表（可选，不传则只改基本信息）
            @SuppressWarnings("unchecked")
            List<Integer> menuIds = (List<Integer>) body.get("menuIds");

            roleService.updateRole(role, menuIds);
            return Result.success("角色修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除角色
     * DELETE /api/role/4
     *
     * 如果角色下还有用户，会拒绝删除并提示用户数量。
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            roleService.deleteRole(id);
            return Result.success("角色删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用/禁用角色
     * PUT /api/role/1/status
     * 请求体：{status: 1} 或 {status: 0}
     */
    @PutMapping("/{id}/status")
    public Result<Void> setStatus(@PathVariable Integer id, @RequestBody Map<String, Integer> body) {
        try {
            Integer status = body.get("status");
            if (status == null) {
                return Result.error("状态不能为空");
            }
            roleService.setStatus(id, status);
            String msg = status == 1 ? "角色已启用" : "角色已禁用";
            return Result.success(msg, null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有启用的角色（给用户管理下拉框使用）
     * GET /api/role/all
     */
    @GetMapping("/all")
    public Result<List<SysRole>> getAllActive() {
        try {
            List<SysRole> roles = roleService.getAllActive();
            return Result.success(roles);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除角色
     * POST /api/role/batch-delete
     * 请求体：{ids: [4, 5, 6]}
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Integer>> body) {
        try {
            List<Integer> ids = body.get("ids");
            roleService.batchDelete(ids);
            return Result.success("批量删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量启用/禁用角色
     * PUT /api/role/batch-status
     * 请求体：{ids: [2, 3], status: 1}
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
            roleService.batchSetStatus(ids, status);
            String msg = status == 1 ? "已批量启用" : "已批量禁用";
            return Result.success(msg, null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
