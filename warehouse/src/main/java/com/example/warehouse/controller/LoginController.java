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

@RestController//1.与@Controller区别？
@RequestMapping("/api/auth")
public class LoginController {

    @Resource//2.与@Autowired区别？
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
    @GetMapping("/captcha")//3.与@Mapping区别?只接受Get请求
    public Result<Map<String, Object>> captcha(HttpSession session) {//Result<T>,T == Map<String,Object>
        // 生成两个 0~9 的随机数
        Random random = new Random();
        int num1 = random.nextInt(10);  // 第一个数
        int num2 = random.nextInt(10);  // 第二个数
        int answer = num1 + num2;       // 正确答案

        // 把正确答案存到 Session 里，登录时拿出来比对，也就是把答案存入服务器
        session.setAttribute("captchaAnswer", answer);

        // 返回数学题的"题目"，不返回答案，把问题返回给前端
        Map<String, Object> data = new HashMap<>();
        data.put("expression", num1 + " + " + num2 + " = ?");  // 比如 "3 + 5 = ?"
        data.put("captchaId", session.getId());//验证码答案ID
        //data:['expression','express'],['captchaId','ID']
        return Result.success(data);//data 是Map<String,Object>类型的，
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
    @PostMapping("/login")// /api/auth/login 请求会进入这个方法中
    public Result<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {//RequestBody把前端JSON的姓名密码验证码自动转为LoginRequest,对应于request的三个字段
        try {                //HttpSession是tomcat新建的，每个用户登录后服务端自动分配一个“私人抽屉”，靠 JSESSIONID Cookie 认领。session.setAttribute("key", value) 是把东西存进去，session.getAttribute("key") 是取出来
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
    public Result<LoginResponse> register(@RequestBody RegisterRequest request) {//为什么不能直接是返回LoginResponse？通过Result里面的code,msg,还有data对象来传递，这样前端才能分清成败（通过code，用户通过msg），这就是 Result 统一响应的作用——所有接口的"成败"和"数据"分开传。
        try {//@RequestBody是把{"username": "zhangsan", "password": "123456", "realName": "张三"}这段 JSON 自动转成了 Java 对象 RegisterRequest，命名request。
            // 基础参数校验
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {//为什么是从request里取参数？上文
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {//trim()是去掉前后端空格！！！！isEmpty()是如果为空返回true,如果前端传了空字符串，不走这个判断的话，后续就会拿空字符串去数据库查，浪费资源。所以 Controller 先拦住。
                return Result.error("密码不能为空");
            }
            if (request.getRealName() == null || request.getRealName().trim().isEmpty()) {
                return Result.error("真实姓名不能为空");
            }
            // 调 Service 处理注册
            LoginResponse response = loginService.register(request);
            return Result.success("注册成功", response);
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
