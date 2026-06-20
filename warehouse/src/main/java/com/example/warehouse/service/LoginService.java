/*
 * ============================================================
 * 【LoginService】登录/注册业务接口
 * ============================================================
 * Service 层是写"业务逻辑"的地方。
 * 这里先定义接口（Interface），再去 impl 文件夹里写实现类。
 *
 * 为什么要有接口？
 *   简单说就是"先约定，后实现"。
 *   接口告诉大家"我这个 Service 能做什么"，
 *   实现类告诉大家"具体怎么做"。
 *   以后想换实现方式（比如改成 Redis 鉴权），
 *   只要再写一个实现类就行，Controller 不用改。
 *
 * 这里定义了登录和注册两个方法。
 *
 * 关于 HttpSession 参数：
 *   Session 是 Java Web 自带的"会话管理"机制。
 *   每个浏览器访问服务器时，服务器会创建一个专属的 Session，
 *   用一个名为 JSESSIONID 的 Cookie 来区分不同用户。
 *   登录时把用户信息存进去，之后的请求就能知道"谁在操作"。
 * ============================================================
 */
package com.example.warehouse.service;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.dto.RegisterRequest;

import jakarta.servlet.http.HttpSession;

public interface LoginService {

    /**
     * 用户登录
     * @param request 前端传来的 username + password
     * @param session HTTP 会话，用于存储登录状态
     * @return 登录成功后的用户信息
     */
    LoginResponse login(LoginRequest request, HttpSession session);

    /**
     * 用户注册
     * @param request 前端传来的注册信息
     * @return 注册后的用户信息
     */
    LoginResponse register(RegisterRequest request);
}
