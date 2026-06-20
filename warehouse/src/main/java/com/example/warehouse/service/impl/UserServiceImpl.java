/*
 * ============================================================
 * 【UserServiceImpl】用户管理业务的"具体实现"
 * ============================================================
 * 这是用户管理模块的核心文件。
 *
 * 流程概览：
 *   前端请求 → UserController（接收请求）
 *            → UserServiceImpl（处理业务逻辑）
 *            → SysUserMapper（操作数据库）
 *            → 返回结果给前端
 *
 * 关键设计：
 *   1. 管理员创建用户时，密码会被 BCrypt 加密后再存入数据库
 *   2. 管理员创建的用户 status=1（直接启用），自助注册 status=0（需审核）
 *   3. 重置密码也是加密后存回去
 *   4. 批量操作支持：批量删除、批量启用/禁用
 * ============================================================
 */
package com.example.warehouse.service.impl;

import com.example.warehouse.dto.UserCreateRequest;
import com.example.warehouse.dto.UserPageRequest;
import com.example.warehouse.dto.UserUpdateRequest;
import com.example.warehouse.mapper.SysUserMapper;
import com.example.warehouse.pojo.SysUser;
import com.example.warehouse.service.UserService;
import com.example.warehouse.util.PasswordUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private SysUserMapper sysUserMapper;

    /** 默认密码：新用户创建或密码重置时使用 */
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public Map<String, Object> listUsers(UserPageRequest request) {
        // 1. 计算分页的起始位置
        int offset = (request.getPageNum() - 1) * request.getPageSize();

        // 2. 查询符合条件的用户列表（带分页）
        List<SysUser> userList = sysUserMapper.selectPage(
                request.getUsername(),
                request.getRealName(),
                request.getRoleId(),
                request.getStatus(),
                offset,
                request.getPageSize()
        );

        // 3. 查询符合条件的总记录数（用于前端计算总页数）
        long total = sysUserMapper.countPage(
                request.getUsername(),
                request.getRealName(),
                request.getRoleId(),
                request.getStatus()
        );

        // 4. 封装成 Map 返回
        Map<String, Object> result = new HashMap<>();
        result.put("list", userList);
        result.put("total", total);
        result.put("pageNum", request.getPageNum());
        result.put("pageSize", request.getPageSize());

        return result;
    }

    @Override
    public SysUser getById(Integer id) {
        return sysUserMapper.findById(id);
    }

    @Override
    public void createUser(UserCreateRequest request) {
        // 1. 检查用户名是否已存在
        SysUser existingUser = sysUserMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名【" + request.getUsername() + "】已存在");
        }

        // 2. 组装用户对象
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        // 密码加密：管理员创建用户时，密码也要 BCrypt 加密存储
        String rawPassword = (request.getPassword() != null && !request.getPassword().isEmpty())
                ? request.getPassword() : DEFAULT_PASSWORD;
        user.setPassword(PasswordUtil.encode(rawPassword));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoleId(request.getRoleId());
        // 管理员创建的用户 status=1（直接启用），与自助注册的 status=0 区分
        user.setStatus(1);

        // 3. 插入数据库
        sysUserMapper.insert(user);
    }

    @Override
    public void updateUser(UserUpdateRequest request) {
        // 1. 检查用户是否存在
        SysUser existingUser = sysUserMapper.findById(request.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 如果改了用户名，需要检查新用户名是否被占用
        if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername())) {
            SysUser userWithSameName = sysUserMapper.findByUsername(request.getUsername());
            if (userWithSameName != null) {
                throw new RuntimeException("用户名【" + request.getUsername() + "】已被占用");
            }
        }

        // 3. 组装要更新的数据（只更新非空字段）
        SysUser user = new SysUser();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoleId(request.getRoleId());
        user.setStatus(request.getStatus());

        // 4. 执行更新
        sysUserMapper.updateById(user);
    }

    @Override
    public void deleteUser(Integer id) {
        // 1. 检查用户是否存在
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 执行删除
        sysUserMapper.deleteById(id);
    }

    @Override
    public void setStatus(Integer id, Integer status) {
        // 1. 检查用户是否存在
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查状态值是否合法（1=启用，0=禁用）
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值不合法：只能为 1（启用）或 0（禁用）");
        }

        // 3. 更新状态
        sysUserMapper.updateStatus(id, status);
    }

    @Override
    public void resetPassword(Integer id) {
        // 1. 检查用户是否存在
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 重置密码为 BCrypt 加密后的默认密码
        sysUserMapper.updatePassword(id, PasswordUtil.encode(DEFAULT_PASSWORD));
    }

    @Override
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的用户");
        }
        sysUserMapper.deleteByIds(ids);
    }

    @Override
    public void batchSetStatus(List<Integer> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要操作的用户");
        }
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值不合法：只能为 1（启用）或 0（禁用）");
        }
        sysUserMapper.updateStatusByIds(ids, status);
    }
}
