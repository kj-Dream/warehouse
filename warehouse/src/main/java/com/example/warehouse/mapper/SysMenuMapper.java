/*
 * ============================================================
 * 【SysMenuMapper】菜单表的数据操作接口
 * ============================================================
 * 这个 Mapper 负责查询系统菜单。
 *
 * 菜单数据存在 sys_menu 表里，是树形结构：
 *   系统管理（parent_id=0）
 *     ├── 用户管理（parent_id=1）
 *     └── 角色管理（parent_id=1）
 *   商品管理（parent_id=0）
 *     ├── 商品列表（parent_id=4）
 *     └── 商品分类（parent_id=4）
 *   ...
 *
 * 目前的方法：
 *   1. findAll → 查询所有菜单（按 order_num 排序）
 *
 * 注意：数据库里父级ID字段名叫 partne_id（拼写问题），
 * MyBatis 通过 resultMap 映射到 Java 的 parentId。
 * ============================================================
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysMenuMapper {

    /**
     * 查询所有菜单（按排序号升序排列）
     * @return 所有菜单列表（平铺，不是树形）
     */
    List<SysMenu> findAll();
}
