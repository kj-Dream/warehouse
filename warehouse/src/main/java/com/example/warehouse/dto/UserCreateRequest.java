/*
 * ============================================================
 * 【UserCreateRequest】新增用户的请求参数
 * ============================================================
 * 管理员创建新用户时，前端传过来这些信息。
 * 对比 RegisterRequest，这里多了 roleId（角色选择），
 * 因为管理员可以给新用户指定角色，而注册只能默认"仓管员"。
 *
 * password 是可选的：不传就用默认密码 "123456"。
 * 实际项目中一般会让管理员设置初始密码。
 * ============================================================
 */
package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    /** 用户名（必填，登录时使用） */
    private String username;

    /** 密码（选填，不填默认为 "123456"） */
    private String password;

    /** 真实姓名（必填，显示在系统里的名称） */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色ID（必填，决定用户的权限范围） */
    private Integer roleId;
}
