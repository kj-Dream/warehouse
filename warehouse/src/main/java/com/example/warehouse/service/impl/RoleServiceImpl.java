/*
 * ============================================================
 * 【RoleServiceImpl】角色管理业务实现
 * ============================================================
 * 角色管理的核心业务逻辑。
 *
 * 关键设计：
 *   1. 新增/修改角色时，先保存角色信息，再处理菜单权限关联。
 *      菜单权限的处理策略是"先删后插"——先清除旧权限再插入新权限，
 *      这样避免了复杂的"对比差异"逻辑。
 *
 *   2. 删除角色时，先查 sys_user 表看有没有用户关联了这个角色。
 *      如果有用户在用，就不能删（"保护性删除"），
 *      管理员需要先把这些用户改到别的角色再删。
 *
 *   3. 用 @Transactional 保证数据一致性：
 *      如果操作过程中任何一步出错，所有已执行的 SQL 都会回滚，
 *      不会出现"角色创建了但权限没分配"这种半成品状态。
 * ============================================================
 */
package com.example.warehouse.service.impl;

import com.example.warehouse.mapper.RoleMenuMapper;
import com.example.warehouse.mapper.SysRoleMapper;
import com.example.warehouse.pojo.SysRole;
import com.example.warehouse.pojo.SysRoleMenu;
import com.example.warehouse.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    /**
     * 分页查询角色列表
     */
    @Override
    public Map<String, Object> listRoles(Integer pageNum, Integer pageSize,
                                         String roleName, String roleKey, Integer status) {
        int offset = (pageNum - 1) * pageSize;

        List<SysRole> list = sysRoleMapper.selectPage(roleName, roleKey, status, offset, pageSize);
        long total = sysRoleMapper.countPage(roleName, roleKey, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 根据ID查询角色（含其拥有的菜单权限ID列表）
     *
     * 为什么要返回菜单ID列表？
     *   前端打开"编辑角色"对话框时，需要把该角色已有的菜单权限
     *   预勾选在复选框树里，管理员才知道当前配置了什么权限。
     */
    @Override
    public Map<String, Object> getById(Integer id) {
        SysRole role = sysRoleMapper.findById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 查该角色拥有的所有菜单ID
        List<SysRoleMenu> roleMenus = roleMenuMapper.findByRoleId(id);
        List<Integer> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("role", role);
        result.put("menuIds", menuIds);
        return result;
    }

    /**
     * 新增角色（含菜单权限分配）
     *
     * @param role    角色基本信息（roleName, roleKey, description）
     * @param menuIds 分配给的菜单ID列表（前端勾选的菜单权限）
     */
    @Override
    @Transactional  // 事务：保证角色信息和权限关联同时成功或同时失败
    public void createRole(SysRole role, List<Integer> menuIds) {
        // 1. 校验 role_key 唯一性
        SysRole existingRole = sysRoleMapper.findByRoleKey(role.getRoleKey());
        if (existingRole != null) {
            throw new RuntimeException("角色标识【" + role.getRoleKey() + "】已存在");
        }

        // 2. 插入角色信息
        sysRoleMapper.insert(role);
        // 插入后 role.getId() 就能拿到新生成的ID

        // 3. 插入菜单权限关联（给角色分配菜单）
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Integer menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    /**
     * 修改角色（含重新分配菜单权限）
     *
     * 权限更新策略："先删后插"
     *   先删除该角色的所有旧菜单权限，
     *   再插入前端传来的新菜单权限。
     *   这样不用逐条对比哪些被删了、哪些是新增的。
     */
    @Override
    @Transactional  // 事务：保证清空旧权限和插入新权限是原子操作
    public void updateRole(SysRole role, List<Integer> menuIds) {
        // 1. 检查角色是否存在
        SysRole existingRole = sysRoleMapper.findById(role.getId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 2. 如果改了 role_key，需要检查是否和别的角色冲突
        if (role.getRoleKey() != null && !role.getRoleKey().equals(existingRole.getRoleKey())) {
            SysRole conflictRole = sysRoleMapper.findByRoleKey(role.getRoleKey());
            if (conflictRole != null) {
                throw new RuntimeException("角色标识【" + role.getRoleKey() + "】已被占用");
            }
        }

        // 3. 更新角色基本信息
        sysRoleMapper.updateById(role);

        // 4. 如果前端传了菜单权限，则"先删后插"更新权限
        //    前端可能不传 menuIds（只修改角色基本信息，不动权限）
        if (menuIds != null) {
            // 先清空旧权限
            roleMenuMapper.deleteByRoleId(role.getId());
            // 再插入新权限
            for (Integer menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    /**
     * 删除角色
     *
     * 安全机制：如果还有用户关联了这个角色，拒绝删除。
     * 这防止了"用户突然失去所有权限"的尴尬情况。
     */
    @Override
    @Transactional  // 事务：保证删除角色和删除权限关联同时成功
    public void deleteRole(Integer id) {
        // 1. 检查角色是否存在
        SysRole role = sysRoleMapper.findById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 2. 检查是否有用户关联了此角色
        int userCount = roleMenuMapper.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new RuntimeException("该角色下还有 " + userCount + " 个用户，请先将这些用户改为其他角色后再删除");
        }

        // 3. 删除角色的菜单权限关联
        roleMenuMapper.deleteByRoleId(id);

        // 4. 删除角色本身
        sysRoleMapper.deleteById(id);
    }

    /**
     * 启用/禁用角色
     *
     * 禁用角色后，拥有该角色的用户将看不到对应的菜单。
     * 这是一种"软删除"方式，比直接删除更安全。
     */
    @Override
    public void setStatus(Integer id, Integer status) {
        SysRole role = sysRoleMapper.findById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值只能为 1（启用）或 0（禁用）");
        }
        sysRoleMapper.updateStatus(id, status);
    }

    /**
     * 查询所有启用的角色
     * 给用户管理页面的"角色选择"下拉框使用
     */
    @Override
    public List<SysRole> getAllActive() {
        return sysRoleMapper.findAll();
    }

    @Override
    @Transactional
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的角色");
        }
        // 检查每个角色是否有关联用户
        for (Integer id : ids) {
            int userCount = roleMenuMapper.countUsersByRoleId(id);
            if (userCount > 0) {
                SysRole role = sysRoleMapper.findById(id);
                String name = role != null ? role.getRoleName() : id.toString();
                throw new RuntimeException("角色【" + name + "】下还有用户，请先处理后再删除");
            }
            // 删除角色的菜单权限关联
            roleMenuMapper.deleteByRoleId(id);
        }
        // 批量删除角色
        sysRoleMapper.deleteByIds(ids);
    }

    @Override
    public void batchSetStatus(List<Integer> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要操作的角色");
        }
        if (status != 0 && status != 1) {
            throw new RuntimeException("状态值只能为 1（启用）或 0（禁用）");
        }
        sysRoleMapper.updateStatusByIds(ids, status);
    }
}
