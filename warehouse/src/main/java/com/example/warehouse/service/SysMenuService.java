/*
 * ============================================================
 * 【SysMenuService】菜单业务接口
 * ============================================================
 * 提供菜单查询功能，支持根据角色返回不同的菜单。
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
     * 获取菜单树（所有菜单，当前不分角色）
     * @return 树形结构的菜单列表
     */
    List<Map<String, Object>> getMenuTree();
}
