/*
 * ============================================================
 * 【SysUserMapper】用户表的数据操作接口
 * ============================================================
 * Mapper 是 MyBatis 框架中的概念，它的作用就是"告诉 MyBatis，
 * 我需要执行哪些数据库操作"。
 *
 * 基础方法（给登录/注册用）：
 *   1. findByUsername → 通过用户名查用户
 *   2. findById       → 通过ID查用户
 *   3. insert         → 新增用户（注册时用）
 *
 * 用户管理方法（给管理员用）：
 *   4. selectPage     → 分页+条件查询用户列表
 *   5. countPage      → 统计符合条件的用户总数（分页用）
 *   6. updateById     → 根据ID更新用户信息
 *   7. deleteById     → 根据ID删除用户
 *   8. updateStatus   → 更新用户状态（启用/禁用）
 *   9. updatePassword → 更新用户密码
 *
 * 批量操作方法（新增）：
 *   10. deleteByIds      → 批量删除用户
 *   11. updateStatusByIds → 批量启用/禁用用户
 *
 * 具体的 SQL 语句写在 resources/dao/SysUserMapper.xml 里。
 * ============================================================
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    // ========== 基础方法（给登录/注册模块用） ==========

    /** 根据用户名查询用户（登录时用） */
    SysUser findByUsername(@Param("username") String username);

    /** 根据ID查询用户 */
    SysUser findById(@Param("id") Integer id);

    /** 新增用户（注册时用），返回影响的行数 */
    int insert(SysUser user);//SQL 在 XML 文件里。MyBatis 的规则是：Mapper 接口定义方法名 → XML 里写对应的 SQL，通过 namespace + id 自动匹配。

    // ========== 用户管理方法（给管理员用） ==========

    /**
     * 分页查询用户列表（支持多条件筛选）
     */
    List<SysUser> selectPage(@Param("username") String username,
                             @Param("realName") String realName,
                             @Param("roleId") Integer roleId,
                             @Param("status") Integer status,
                             @Param("offset") int offset,
                             @Param("limit") int limit);

    /**
     * 统计符合条件的用户总数（配合 selectPage 做分页）
     */
    long countPage(@Param("username") String username,
                   @Param("realName") String realName,
                   @Param("roleId") Integer roleId,
                   @Param("status") Integer status);

    /**
     * 根据ID更新用户信息（只更新非空字段）
     */
    int updateById(SysUser user);

    /**
     * 根据ID删除用户
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 更新用户状态（启用/禁用）
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    // ========== 批量操作方法（新增） ==========

    /**
     * 批量删除用户
     * @param ids 要删除的用户ID列表
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 批量更新用户状态
     * @param ids    用户ID列表
     * @param status 1=启用，0=禁用
     */
    int updateStatusByIds(@Param("ids") List<Integer> ids, @Param("status") Integer status);
}
