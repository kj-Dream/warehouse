/*
 * ============================================================
 * 【RoleMenuMapper】角色菜单关联表的数据操作
 * ============================================================
 * 负责维护角色和菜单之间的"多对多"关系。
 *
 * 核心操作：
 *   1. findByRoleId  → 根据角色ID查它拥有的所有菜单ID
 *   2. insert        → 给角色分配一个菜单权限
 *   3. deleteByRoleId → 删除角色的所有菜单权限（修改角色时先清空再重新分配）
 * ============================================================
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    /** 根据角色ID查询该角色拥有的所有菜单权限 */
    List<SysRoleMenu> findByRoleId(@Param("roleId") Integer roleId);

    /** 给角色分配菜单权限 */
    int insert(SysRoleMenu record);

    /** 删除角色的所有菜单权限（重新分配前先清空） */
    int deleteByRoleId(@Param("roleId") Integer roleId);

    /** 删除角色时，同时删除其所有菜单权限关联 */
    int deleteByRoleIdAndMenuId(@Param("roleId") Integer roleId, @Param("menuId") Integer menuId);

    /** 统计某个角色关联的用户数量（删除角色前检查） */
    int countUsersByRoleId(@Param("roleId") Integer roleId);
}
