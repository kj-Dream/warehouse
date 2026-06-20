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
 * 关于角色过滤：
 *   当前版本返回所有菜单。如果后续需要按角色过滤，
 *   可以在这里根据 roleKey 筛选菜单后再构建树。
 * ============================================================
 */
package com.example.warehouse.service.impl;

import com.example.warehouse.mapper.SysMenuMapper;
import com.example.warehouse.pojo.SysMenu;
import com.example.warehouse.service.SysMenuService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    /**
     * 获取菜单树
     *
     * 步骤：
     *   1. 从数据库查出所有菜单（平铺列表）
     *   2. 找出所有一级菜单（parentId = 0）
     *   3. 给每个一级菜单挂上它的子菜单
     *   4. 返回树形结构
     */
    @Override
    public List<Map<String, Object>> getMenuTree() {
        // 1. 查出所有菜单
        List<SysMenu> allMenus = sysMenuMapper.findAll();

        // 2. 找出所有一级菜单（parentId = 0），按 orderNum 排序
        List<SysMenu> parentMenus = allMenus.stream()
                .filter(m -> m.getParentId() == 0)
                .sorted(Comparator.comparing(SysMenu::getOrderNum, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        // 3. 给每个一级菜单挂上它的子菜单
        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysMenu parent : parentMenus) {
            Map<String, Object> parentNode = new LinkedHashMap<>();
            parentNode.put("id", parent.getId());
            parentNode.put("name", parent.getName());
            parentNode.put("path", parent.getPath());
            parentNode.put("icon", parent.getIcon());

            // 找到当前父菜单的所有子菜单
            List<Map<String, Object>> children = new ArrayList<>();
            for (SysMenu menu : allMenus) {
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
            parentNode.put("children", children);
            tree.add(parentNode);
        }

        return tree;
    }
}
