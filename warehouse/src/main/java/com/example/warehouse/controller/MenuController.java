/*
 * ============================================================
 * 【MenuController】菜单接口
 * ============================================================
 * 提供前端获取系统菜单的接口。
 *
 * GET /api/menu → 返回树形结构的菜单列表
 *
 * 关键设计——RBAC权限过滤：
 *   后端从 Session 中取出当前登录用户的角色ID，
 *   根据角色ID过滤菜单。前端不需要做任何权限判断，
 *   拿到的就是该用户能看到的菜单。
 *   这样即使有人改了前端代码，也看不到没权限的页面。
 *
 * 角色菜单分配存储在 sys_role_menu 表中，
 * 管理员在"角色管理 → 编辑 → 菜单权限"中配置。
 * ============================================================
 */
package com.example.warehouse.controller;

import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.dto.Result;
import com.example.warehouse.service.SysMenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 获取当前用户的菜单树
     * GET /api/menu
     *
     * 流程：
     *   1. 从 Session 中取出登录时存进去的用户信息
     *   2. 拿到用户的 roleId
     *   3. 调用 Service 根据 roleId 过滤菜单
     *   4. 返回过滤后的菜单树
     *
     * 如果用户未登录（Session 中没有用户信息），返回全部菜单。
     * 实际使用中 Layout.vue 会在登录后才加载菜单。
     *
     * @param session HTTP 会话，登录时存的用户信息就在里面
     * @return 树形菜单列表（已按角色权限过滤）
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getMenus(HttpSession session) {
        try {
            // 1. 从 Session 中取出登录用户信息
            LoginResponse user = (LoginResponse) session.getAttribute("user");

            // 2. 根据用户角色获取菜单
            //    - 管理员(roleId=1)：全部菜单
            //    - 仓管员(roleId=2)：商品/仓库/出入库/库存
            //    - 财务(roleId=3)：报表/成本分析
            Integer roleId = (user != null) ? user.getRoleId() : null;
            List<Map<String, Object>> menuTree = sysMenuService.getMenuTreeByRoleId(roleId);

            return Result.success(menuTree);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
