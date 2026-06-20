/*
 * ============================================================
 * 【LoginRequest】登录请求参数
 * ============================================================
 * 这个文件的作用：
 *   当前端用户点击"登录"按钮时，前端会把用户名、密码和验证码
 *   打包成一个 JSON 对象发过来，这个类就是用来"接住"那个 JSON 的。
 *
 * 比如前端发来 {"username":"admin","password":"123456","captcha":8}，
 * Spring Boot 会自动把这个 JSON 转成 LoginRequest 对象。
 *
 * captcha 字段：
 *   验证码答案，和 GET /api/auth/captcha 返回的数学题对应。
 *   比如数学题是"3 + 5 = ?"，用户填入 8，captcha 就是 8。
 * ============================================================
 */
package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username;  // 用户名
    private String password;  // 密码
    private Integer captcha;  // 验证码答案（数学题的结果）
}
