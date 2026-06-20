/*
 * ============================================================
 * 【RegisterRequest】注册请求参数
 * ============================================================
 * 登录只需要用户名和密码，但注册还需要更多信息：
 * 真实姓名、手机号、邮箱等。
 * 这个类就是用来接收前端注册时传过来的所有数据。
 *
 * 对比 LoginRequest，你会发现注册比登录多几个字段，
 * 这就是为什么我们要分开写两个类，而不是共用一个。
 * ============================================================
 */
package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;  // 用户名（登录时用的账号）
    private String password;  // 密码
    private String realName;  // 真实姓名（显示在系统里的名字）
    private String phone;     // 手机号
    private String email;     // 电子邮箱
}
