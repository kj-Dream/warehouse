/*
 * ============================================================
 * 【LoginResponse】登录成功后的返回数据
 * ============================================================
 * 为什么要有这个类？
 *   数据库里的 SysUser 表有 password 字段，但登录成功后
 *   返回给前端的数据"绝对不能包含密码"（这是安全常识）。
 * 所以我们专门建一个类，只挑能返回的字段返回。
 * 这就叫"数据脱敏"——把敏感信息去掉再给别人看。
 *
 * 另外还查了一下角色表，把角色信息也一起返回，
 * 前端拿到角色后就可以决定显示哪些菜单（权限控制）。
 * ============================================================
 */
package com.example.warehouse.dto;

import com.example.warehouse.pojo.SysRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;            // 用户ID
    private String username;       // 用户名
    private String realName;       // 真实姓名
    private String phone;          // 手机号
    private String email;          // 邮箱
    private Integer roleId;        // 角色ID
    private Integer status;        // 状态（1=启用，0=禁用）
    private SysRole role;          // 角色信息（包含角色名称、角色标识等）
}
