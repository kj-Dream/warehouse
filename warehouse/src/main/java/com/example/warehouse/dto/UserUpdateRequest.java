/*
 * ============================================================
 * 【UserUpdateRequest】修改用户信息的请求参数
 * ============================================================
 * 管理员编辑用户信息时使用。
 * 和 UserCreateRequest 的区别：
 *   1. 多了 id 字段——得告诉后端"改哪个人"
 *   2. 没有 password——修改信息不涉及密码修改，
 *      密码重置有单独的接口（防止误操作）
 *
 * 注意：这个类不包含密码字段！
 * 修改密码应该走"密码重置"接口，而不是编辑用户接口。
 * 这样设计是为了安全——防止编辑时不小心把密码改了。
 * ============================================================
 */
package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    /** 用户ID（必填，要修改哪个用户） */
    private Integer id;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 角色ID */
    private Integer roleId;

    /** 状态（1=启用，0=禁用） */
    private Integer status;
}
