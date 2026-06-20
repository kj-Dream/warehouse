/*
 * ============================================================
 * 【UserService】用户管理的业务接口
 * ============================================================
 * 这个接口定义了"用户管理"模块能做的所有事情。
 *
 * 管理员登录系统后，可以：
 *   1. 查看用户列表（分页 + 条件搜索）
 *   2. 新增用户（分配角色，密码加密）
 *   3. 修改用户信息
 *   4. 删除用户
 *   5. 启用/禁用用户（状态控制）
 *   6. 重置用户密码
 *   7. 批量删除用户
 *   8. 批量启用/禁用
 * ============================================================
 */
package com.example.warehouse.service;

import com.example.warehouse.dto.*;
import com.example.warehouse.pojo.SysUser;

import java.util.List;
import java.util.Map;

public interface UserService {

    /** 分页查询用户列表 */
    Map<String, Object> listUsers(UserPageRequest request);

    /** 根据 ID 查询单个用户 */
    SysUser getById(Integer id);

    /** 新增用户（管理员操作，密码加密存储，status=1 直接启用） */
    void createUser(UserCreateRequest request);

    /** 修改用户信息（不修改密码） */
    void updateUser(UserUpdateRequest request);

    /** 删除用户 */
    void deleteUser(Integer id);

    /** 启用/禁用用户（状态控制） */
    void setStatus(Integer id, Integer status);

    /** 重置用户密码为 BCrypt 加密后的默认密码 */
    void resetPassword(Integer id);

    /** 批量删除用户 */
    void batchDelete(List<Integer> ids);

    /** 批量启用/禁用用户 */
    void batchSetStatus(List<Integer> ids, Integer status);
}
