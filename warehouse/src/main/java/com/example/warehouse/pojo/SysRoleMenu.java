package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 角色菜单关联表 POJO
 * 一个角色可以拥有多个菜单权限，一个菜单也可以分配给多个角色
 * 这就是 RBAC（基于角色的访问控制）的核心关联
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleMenu {
    private Integer id;
    private Integer roleId;
    private Integer menuId;
}
