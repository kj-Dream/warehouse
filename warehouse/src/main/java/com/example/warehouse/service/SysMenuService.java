/*
 * ============================================================
 * 【SysMenuService】菜单业务接口
 * ============================================================
 * 提供菜单查询功能，支持根据用户角色返回不同的菜单。
 *
 * RBAC 权限控制的最后一环：
 *   1. 管理员创建角色 → 给角色分配菜单权限（sys_role_menu 表）
 *   2. 管理员创建用户 → 给用户分配角色（sys_user.role_id）
 *   3. 用户登录 → 系统根据用户角色过滤菜单（本接口）
 *   4. 前端渲染 → 不同角色看到不同的侧边栏菜单
 *
 * 菜单数据从数据库 sys_menu 表读取，是平铺列表，
 * Service 层负责把它组装成树形结构返回给前端。
 * ============================================================
 */
package com.example.warehouse.service;

import java.util.List;
import java.util.Map;

public interface SysMenuService {

    /**
     * 获取菜单树（所有菜单，供角色权限分配时使用）
     *
     * @return 完整树形菜单列表（管理员用）
     */
    List<Map<String, Object>> getMenuTree();

    /**
     * 根据角色ID获取菜单树（RBAC权限过滤）
     *
     * 业务逻辑：
     *   1. 先查 sys_role_menu 表找出该角色拥有的菜单ID列表
     *   2. 再查 sys_menu 表拿所有菜单，只保留角色有权限的那些
     *   3. 组装成树形结构返回
     *   4. 如果 roleId 是管理员(1)，返回全部菜单
     *
     * @param roleId 用户的角色ID
     * @return 过滤后的树形菜单（该角色有权看到的菜单）
     */
    List<Map<String, Object>> getMenuTreeByRoleId(Integer roleId);
}
