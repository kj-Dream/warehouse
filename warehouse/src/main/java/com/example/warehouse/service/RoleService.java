/*
 * ============================================================
 * 【RoleService】角色管理业务接口
 * ============================================================
 * 角色管理模块的核心接口，定义了管理员能做的所有角色操作。
 *
 * RBAC 说明：
 *   RBAC（Role-Based Access Control），即"基于角色的访问控制"。
 *   管理员先创建角色 → 给角色分配菜单权限 → 把角色分配给用户。
 *   用户登录后，系统根据用户的角色来决定他能看到哪些菜单。
 *
 * 操作清单：
 *   1. 分页查询角色列表
 *   2. 查询单个角色（含其菜单权限）
 *   3. 新增角色（含菜单权限分配）
 *   4. 修改角色（先清空旧权限，再分配新权限）
 *   5. 删除角色（先检查是否有用户，有则不能删）
 *   6. 启用/禁用角色
 *   7. 查询所有启用的角色（供用户管理下拉框使用）
 * ============================================================
 */
package com.example.warehouse.service;

import com.example.warehouse.pojo.SysRole;

import java.util.List;
import java.util.Map;

public interface RoleService {

    /** 分页查询角色列表 */
    Map<String, Object> listRoles(Integer pageNum, Integer pageSize,
                                  String roleName, String roleKey, Integer status);

    /** 根据ID查询角色（含其拥有的菜单权限ID列表） */
    Map<String, Object> getById(Integer id);

    /** 新增角色（含菜单权限分配） */
    void createRole(SysRole role, List<Integer> menuIds);

    /** 修改角色（含重新分配菜单权限） */
    void updateRole(SysRole role, List<Integer> menuIds);

    /** 删除角色（有用户关联则拒绝删除） */
    void deleteRole(Integer id);

    /** 启用/禁用角色 */
    void setStatus(Integer id, Integer status);

    /** 批量删除角色 */
    void batchDelete(List<Integer> ids);

    /** 批量启用/禁用角色 */
    void batchSetStatus(List<Integer> ids, Integer status);

    /** 查询所有启用的角色（给下拉框用） */
    List<SysRole> getAllActive();
}
