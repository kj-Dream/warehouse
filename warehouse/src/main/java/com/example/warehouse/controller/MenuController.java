/*
 * ============================================================
 * 【MenuController】菜单接口
 * ============================================================
 * 提供前端获取系统菜单的接口。
 *
 * GET /api/menu → 返回树形结构的菜单列表
 *
 * 前端拿到菜单后渲染侧边栏导航。
 * 菜单数据来自数据库 sys_menu 表，后端动态读取。
 * ============================================================
 */
package com.example.warehouse.controller;

import com.example.warehouse.dto.Result;
import com.example.warehouse.service.SysMenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 获取菜单树
     * GET /api/menu
     *
     * @return 树形结构的菜单列表，前端直接用 Element Plus 的 el-menu 渲染
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getMenus() {
        try {
            List<Map<String, Object>> menuTree = sysMenuService.getMenuTree();
            return Result.success(menuTree);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
