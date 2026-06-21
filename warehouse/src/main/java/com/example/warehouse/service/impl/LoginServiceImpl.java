/*
 * ============================================================
 * 【LoginServiceImpl】登录/注册业务的"具体实现"
 * ============================================================
 * 这是整个登录模块最核心的文件。
 * Controller 接收请求后，会调到这里来执行真正的业务逻辑。
 *
 * 登录的流程（6步）：
 *   1. 去数据库查用户名是否存在
 *   2. 比对密码是否正确（BCrypt 加密验证）
 *   3. 如果数据库存的还是老明文密码，自动升级为 BCrypt
 *   4. 检查账号是否被禁用
 *   5. 查询用户的角色信息
 *   6. 把结果封装好返回
 *
 * 注册的流程（4步）：
 *   1. 检查用户名有没有被注册过
 *   2. 用 BCrypt 加密密码
 *   3. 把新用户信息存到数据库（status=0，需管理员审核）
 *   4. 返回注册后的用户信息
 *
 * 关于 HttpSession：
 *   登录成功后，把用户信息存到 Session 里。
 *   之后每次请求，后端都能从 Session 中取出用户信息，
 *   知道"这个请求是谁发的"，这就是"会话管理"。
 *
 * @Service 让 Spring Boot 管理这个类
 * @Resource 是依赖注入，把 Mapper 对象自动注入进来
 * ============================================================
 */
package com.example.warehouse.service.impl;

import com.example.warehouse.dto.LoginRequest;
import com.example.warehouse.dto.LoginResponse;
import com.example.warehouse.dto.RegisterRequest;
import com.example.warehouse.mapper.SysRoleMapper;
import com.example.warehouse.mapper.SysUserMapper;
import com.example.warehouse.pojo.SysRole;
import com.example.warehouse.pojo.SysUser;
import com.example.warehouse.service.LoginService;
import com.example.warehouse.util.PasswordUtil;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private SysUserMapper sysUserMapper;  // 操作用户表的工具

    @Resource
    private SysRoleMapper sysRoleMapper;  // 操作角色表的工具

    /**
     * 登录方法
     *
     * @param request 前端传来的 username + password
     * @param session HTTP 会话对象，由 Controller 传入，用于存储登录状态
     * @return 登录成功后的用户信息
     */
    @Override
    public LoginResponse login(LoginRequest request, HttpSession session) {
        // 1. 根据用户名查找用户
        SysUser user = sysUserMapper.findByUsername(request.getUsername());//调用Mapper->执行SQL->返回SysUser对象
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 校验密码
        //    先判断数据库存的密码是不是 BCrypt 格式（以 "$2a$" 开头）
        //    - 是 BCrypt → 用 BCrypt 方式比对
        //    - 不是 BCrypt → 用明文方式比对（兼容老数据），比对成功后自动升级为 BCrypt
        if (PasswordUtil.isBCryptHash(user.getPassword())) {
            // 新版 BCrypt 密码：用 BCrypt 比对
            if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
        } else {
            // 老版明文密码：直接比对
            if (!user.getPassword().equals(request.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            // 明文密码比对成功后，自动升级为 BCrypt 密文存储
            // 这样下次登录就会走上面的 BCrypt 分支了
            String newHash = PasswordUtil.encode(request.getPassword());
            sysUserMapper.updatePassword(user.getId(), newHash);
        }

        // 3. 校验状态：status=1 是启用，status=0 是禁用
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 4. 查询角色信息：有了角色，前端才能决定显示哪些菜单
        SysRole role = null;
        if (user.getRoleId() != null) {
            role = sysRoleMapper.findById(user.getRoleId());
        }

        // 5. 封装返回结果（注意：不返回密码！）
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRoleId(user.getRoleId());
        response.setStatus(user.getStatus());
        response.setRole(role);

        // 6. 将用户信息存入 Session
        //    Session 是服务端维护的"用户会话"，每个登录用户都有自己的 Session。
        //    存入后，后续请求可以通过 session.getAttribute("user") 获取当前用户。
        session.setAttribute("user", response);

        return response;
    }

    /**
     * 注册方法
     *
     * @param request 前端传来的注册信息（username, password, realName, phone, email）
     * @return 注册后的用户信息
     *
     * 注意：自主注册的用户 status=0（禁用状态），需要管理员审核后才能登录。
     * 管理员在用户管理里创建的用户 status=1（直接启用）。
     */
    @Override
    public LoginResponse register(RegisterRequest request) {
        // 1. 检查用户名是否已存在：先查一次，如果有了就报错
        SysUser existingUser = sysUserMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 用 BCrypt 加密密码（安全第一！）
        String hashedPassword = PasswordUtil.encode(request.getPassword());

        // 3. 创建新用户对象，把前端传来的信息填进去
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(hashedPassword);     // 存储加密后的密码
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        // role_id 不传则默认为 2（仓管员），由 SQL 的 COALESCE 处理
        // status 不传则默认为 0（禁用），需要管理员审核后启用
        //   -- 0=待审核（禁用），1=正常（启用）
        user.setStatus(0);

        // 4. 插入数据库
        sysUserMapper.insert(user);
        // 插入后 user.getId() 就能拿到数据库自动生成的ID

        // 5. 查一下角色信息
        SysRole role = null;
        if (user.getRoleId() != null) {
            role = sysRoleMapper.findById(user.getRoleId());
        }

        // 6. 返回注册后的用户信息
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRoleId(user.getRoleId());
        response.setStatus(user.getStatus());
        response.setRole(role);

        return response;
    }
}
