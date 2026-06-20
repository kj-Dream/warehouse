/*
 * ============================================================
 * 【LoginController】登录/注册的"前台接待处"
 * ============================================================
 * Controller 是后端最外面的一层，负责"接客"。
 *
 * 提供的接口：
 *   1. POST /api/auth/login     → 登录（含验证码校验）
 *   2. POST /api/auth/register   → 注册
 *   3. GET  /api/auth/captcha    → 获取验证码
 *   4. POST /api/auth/logout     → 退出登录
 *   5. GET  /api/auth/me         → 获取当前登录用户信息
 *
 * @RequestMapping("/api/auth")
 *   表示这个控制器处理所有 /api/auth/xxx 的请求
 * ============================================================
 */
package com.example.warehouse.controller;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.dto.RegisterRequest;
import com.example.warehouse.dto.Result;
import com.example.warehouse.service.LoginService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Resource
    private LoginService loginService;

    /**
     * 获取数学验证码
     * GET /api/auth/captcha
     *
     * 返回一个简单的数学题（如"3 + 5 = ?"），
     * 把正确答案存到 Session 里，登录时比对。
     *
     * 为什么需要验证码？
     *   防止坏人用程序暴力破解密码（比如每秒尝试几万次）。
     *   验证码是一道数学题，只有真人才能算出来，程序很难识别。
     *
     * 返回格式：
     *   {code: 200, msg: "操作成功", data: {expression: "3 + 5 = ?", captchaId: "xxx"}}
     */
    @GetMapping("/captcha")
    public Result<Map<String, Object>> captcha(HttpSession session) {
        // 生成两个 0~9 的随机数
        Random random = new Random();
        int num1 = random.nextInt(10);  // 第一个数
        int num2 = random.nextInt(10);  // 第二个数
        int answer = num1 + num2;       // 正确答案

        // 把正确答案存到 Session 里，登录时拿出来比对
        session.setAttribute("captchaAnswer", answer);

        // 返回数学题的"题目"，不返回答案
        Map<String, Object> data = new HashMap<>();
        data.put("expression", num1 + " + " + num2 + " = ?");  // 比如 "3 + 5 = ?"
        data.put("captchaId", session.getId());

        return Result.success(data);
    }

    /**
     * 登录接口
     * POST /api/auth/login
     * 前端传 {username: "xxx", password: "xxx", captcha: 8}
     *
     * 流程：
     *   1. 校验参数
     *   2. 校验验证码（从 Session 中取正确答案比对）
     *   3. 调 Service 处理登录逻辑
     *   4. 登录成功后 Session 中保存用户信息
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            // 1. 基础参数校验
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }

            // 2. 验证码校验
            //    从 Session 中取出正确答案，和用户提交的答案比对
            Integer correctAnswer = (Integer) session.getAttribute("captchaAnswer");
            if (correctAnswer == null) {
                return Result.error("验证码已过期，请刷新后重试");
            }
            if (request.getCaptcha() == null || !request.getCaptcha().equals(correctAnswer)) {
                return Result.error("验证码错误");
            }
            // 验证通过后，清除 Session 中的验证码（防止重复使用）
            session.removeAttribute("captchaAnswer");

            // 3. 调 Service 处理登录（Service 内部会将用户信息存入 Session）
            LoginResponse response = loginService.login(request, session);
            return Result.success("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 注册接口
     * POST /api/auth/register
     * 前端传 {username: "xxx", password: "xxx", realName: "张三", ...}
     *
     * 注册后状态为"禁用"，需要管理员审核后启用。
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            // 基础参数校验
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
                return Result.error("真实姓名不能为空");
            }
            // 调 Service 处理注册
            LoginResponse response = loginService.register(request);
            return Result.success("注册成功，请等待管理员审核", response);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出登录
     * POST /api/auth/logout
     *
     * 销毁 Session，清除服务端存储的登录状态。
     * session.invalidate() 会让当前 Session 立即失效，
     * 之后用户再访问时需要重新登录。
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();  // 销毁 Session，退出登录
        return Result.success("已退出登录", null);
    }

    /**
     * 获取当前登录用户信息
     * GET /api/auth/me
     *
     * 从 Session 中取出登录时存的用户信息。
     * 前端页面刷新后，可以用这个接口恢复用户信息，
     * 而不需要把用户信息存在 localStorage 里。
     *
     * 如果返回 null 说明用户没有登录（Session 过期或不存在）。
     */
    @GetMapping("/me")
    public Result<LoginResponse> me(HttpSession session) {
        LoginResponse user = (LoginResponse) session.getAttribute("user");
        if (user == null) {
            return Result.error(401, "未登录，请先登录");
        }
        return Result.success(user);
    }
}
