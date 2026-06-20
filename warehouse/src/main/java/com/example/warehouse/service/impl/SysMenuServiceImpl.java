/*
 * ============================================================
 * 【SysMenuServiceImpl】菜单业务实现
 * ============================================================
 * 把数据库查出来的平铺菜单列表组装成树形结构。
 *
 * 数据转换过程：
 *   数据库（平铺）：
 *     id=1, parentId=0, name="系统管理"
 *     id=2, parentId=1, name="用户管理"
 *     id=3, parentId=1, name="角色管理"
 *
 *   转换后（树形）：
 *     [{ name:"系统管理", children: [
 *         { name:"用户管理" },
 *         { name:"角色管理" }
 *     ]}]
 *
 * 角色过滤逻辑：
 *   管理员(roleId=1)：返回全部菜单（不限制）
 *   仓管员(roleId=2)：只返回 sys_role_menu 中分配给它的菜单
 *   财务(roleId=3)：只返回报表相关菜单
 *   如果 roleId 为 null：返回全部菜单（未登录状态兼容）
 * ============================================================
 */
package com.example.warehouse.service.impl;

import com.example.warehouse.mapper.RoleMenuMapper;
import com.example.warehouse.mapper.SysMenuMapper;
import com.example.warehouse.pojo.SysMenu;
import com.example.warehouse.pojo.SysRoleMenu;
import com.example.warehouse.service.SysMenuService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    /**
     * 获取完整菜单树（角色权限分配时使用）
     */
    @Override
    public List<Map<String, Object>> getMenuTree() {
        List<SysMenu> allMenus = sysMenuMapper.findAll();
        return buildTree(allMenus);
    }

    /**
     * 根据角色ID获取菜单树（用户登录后加载侧边栏时调用）
     *
     * @param roleId 用户的角色ID
     *               - roleId=1（管理员）：返回全部菜单，不限制
     *               - roleId=2（仓管员）：只返回商品/仓库/出入库/库存模块
     *               - roleId=3（财务）：只返回报表/成本分析模块
     */
    @Override
    public List<Map<String, Object>> getMenuTreeByRoleId(Integer roleId) {
        // 1. 查出所有菜单
        List<SysMenu> allMenus = sysMenuMapper.findAll();

        // 2. 管理员(roleId=1)：不做过滤，返回全部
        if (roleId == null || roleId == 1) {
            return buildTree(allMenus);
        }

        // 3. 非管理员：查 sys_role_menu 获取该角色有权查看的菜单ID列表
        List<SysRoleMenu> roleMenus = roleMenuMapper.findByRoleId(roleId);
        Set<Integer> allowedMenuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 4. 过滤：只保留角色有权限的菜单
        //    注意：如果某个一级菜单有一个子菜单被授权，该一级菜单也要保留（否则子菜单无法展示）
        List<SysMenu> filteredMenus = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            if (allowedMenuIds.contains(menu.getId())) {
                filteredMenus.add(menu);
            }
        }

        // 5. 确保一级菜单（parentId=0）的子菜单也能显示
        //    如果 role 有子菜单的权限，自动把父菜单也带上
        Set<Integer> filteredIds = filteredMenus.stream().map(SysMenu::getId).collect(Collectors.toSet());
        for (SysMenu menu : allMenus) {
            if (menu.getParentId() == 0 && !filteredIds.contains(menu.getId())) {
                // 检查这个一级菜单下有没有被授权的子菜单
                for (SysMenu child : filteredMenus) {
                    if (Objects.equals(child.getParentId(), menu.getId())) {
                        filteredMenus.add(menu);
                        break;
                    }
                }
            }
        }

        // 6. 组装树形结构
        return buildTree(filteredMenus);
    }

    /**
     * 将平铺菜单列表组装成树形结构
     *
     * 步骤：
     *   1. 找出所有一级菜单（parentId = 0）
     *   2. 给每个一级菜单挂上它的子菜单
     *   3. 返回树形列表
     */
    private List<Map<String, Object>> buildTree(List<SysMenu> menus) {
        // 找出一级菜单
        List<SysMenu> parentMenus = menus.stream()
                .filter(m -> m.getParentId() == 0)
                .sorted(Comparator.comparing(SysMenu::getOrderNum, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysMenu parent : parentMenus) {
            Map<String, Object> parentNode = new LinkedHashMap<>();
            parentNode.put("id", parent.getId());
            parentNode.put("name", parent.getName());
            parentNode.put("path", parent.getPath());
            parentNode.put("icon", parent.getIcon());

            // 找当前父菜单的所有子菜单
            List<Map<String, Object>> children = new ArrayList<>();
            for (SysMenu menu : menus) {
                if (Objects.equals(menu.getParentId(), parent.getId())) {
                    Map<String, Object> childNode = new LinkedHashMap<>();
                    childNode.put("id", menu.getId());
                    childNode.put("name", menu.getName());
                    childNode.put("path", menu.getPath());
                    childNode.put("component", menu.getComponent());
                    childNode.put("icon", menu.getIcon());
                    children.add(childNode);
                }
            }
            // 只有一级菜单下确实有子菜单时才设置 children
            if (!children.isEmpty()) {
                parentNode.put("children", children);
            }
            tree.add(parentNode);
        }
        return tree;
    }
}
