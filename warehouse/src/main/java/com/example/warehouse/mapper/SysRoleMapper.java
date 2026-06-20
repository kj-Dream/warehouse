/*
 * ============================================================
 * 【SysRoleMapper】角色表的数据操作接口
 * ============================================================
 * 角色管理模块需要的数据库操作。
 *
 * 方法清单：
 *   1. findById       → 单个查询
 *   2. findByRoleKey  → 按标识查询（用于唯一性校验）
 *   3. selectPage     → 分页+条件查询
 *   4. countPage      → 统计总数（分页用）
 *   5. insert         → 新增角色
 *   6. updateById     → 修改角色信息
 *   7. deleteById     → 删除角色
 *   8. updateStatus   → 启用/禁用角色
 *   9. findAll        → 查询所有角色（给下拉框用）
 * ============================================================
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    /** 根据ID查询角色 */
    SysRole findById(@Param("id") Integer id);

    /** 根据角色标识查询（role_key 唯一，新增/修改时检查是否重复） */
    SysRole findByRoleKey(@Param("roleKey") String roleKey);

    /** 分页查询角色列表（支持条件筛选） */
    List<SysRole> selectPage(@Param("roleName") String roleName,
                             @Param("roleKey") String roleKey,
                             @Param("status") Integer status,
                             @Param("offset") int offset,
                             @Param("limit") int limit);

    /** 统计符合条件的角色总数 */
    long countPage(@Param("roleName") String roleName,
                   @Param("roleKey") String roleKey,
                   @Param("status") Integer status);

    /** 新增角色，返回影响行数 */
    int insert(SysRole role);

    /** 根据ID更新角色信息（只更新非空字段） */
    int updateById(SysRole role);

    /** 删除角色 */
    int deleteById(@Param("id") Integer id);

    /** 更新角色状态（启用/禁用） */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /** 批量删除角色 */
    int deleteByIds(@Param("ids") List<Integer> ids);

    /** 批量更新角色状态 */
    int updateStatusByIds(@Param("ids") List<Integer> ids, @Param("status") Integer status);

    /** 查询所有启用的角色（给用户管理的角色下拉框用） */
    List<SysRole> findAll();
}
