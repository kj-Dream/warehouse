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

@Service//@Service 标记了这个类是一个 Bean，Spring Boot 启动时会自动创建这个类的实例，放到一个叫 IoC 容器的"大仓库"里。这个 Bean 的默认名称是 loginServiceImpl（类名首字母小写）。
public class LoginServiceImpl implements LoginService {

    @Resource
    private SysUserMapper sysUserMapper;  // 操作用户表的工具，用于下方的登录、注册功能

    @Resource
    private SysRoleMapper sysRoleMapper;  // 操作角色表的工具，用于下方的查询角色功能

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
        SysUser existingUser = sysUserMapper.findByUsername(request.getUsername());//根据前端传来的注册信息进行查询
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 用 BCrypt 加密密码（安全第一！）
        String hashedPassword = PasswordUtil.encode(request.getPassword());
        //加密详细说明：
        /*    对比              MD5           BCrypt
        * 同样密码加密结果	每次都一样	    每次不一样
        * 能不能反向破解	可以"撞库"（彩虹表）	几乎不可能
        * 加密速度	极快（1秒能算几亿次）	慢（1秒只能算几千次）
        * BCrypt 故意设计得很慢，目的是让黑客暴力破解的成本变得极高。
        * 原理：BCrypt = 哈希算法 + 随机盐值
        * "盐值"就是一段随机字符串。加密时算法自动生成一个随机盐值，和你的密码混在一起再加密
        * 彩虹表是提前算好的密码对照表
        * 黑客提前把几亿个常见密码的 MD5 值都算好，存成一张表
        * 然后拿数据库偷出来的密文，直接去表里反向查——不用计算，一秒就能找到明文密码。
         * */

        //知识补充：
        /*
        *  几种常见方式：
            1. SQL 注入
            黑客在登录框输入 ' OR 1=1 --，如果后端代码直接拼接 SQL 而不是用 #{} 占位符，就能让数据库执行恶意查询，把整个用户表 dump 出来。我们的项目用了 #{} 防住了。
            2. 服务端漏洞
            比如文件上传没限制→黑客上传了木马文件→拿到服务器控制权→直接执行 SELECT * FROM sys_user 导出数据。
            3. 内部泄露
            运维人员或者离职员工拷走了数据库备份文件。
            4. 数据库弱密码
            数据库设置了 root/123456，黑客扫到暴露的 3306 端口直接连上去。
            回头理解 BCrypt 的价值：
            就算上面四种情况全中，黑客把整个数据库拖走了，他看到的是：
            password: "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh"
            而绝不是：password: "123456"
            他没法用彩虹表反推，也没法撞库。这就是 BCrypt 的最后一道防线。
        * */

        // 3. 创建新用户对象，把前端传来的信息填进去
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(hashedPassword);     // 存储加密后的密码
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        // role_id 不传则默认为 2（仓管员），由 SQL 的 COALESCE 处理
        // 注册直接启用（status=1），管理员可在用户管理页面禁用异常账号
        user.setStatus(1);

        // 设默认角色 = 2（仓管员），这样插入后 roleId 就非空，后面查角色也能查到
        user.setRoleId(2);

        // 4. 插入数据库
        sysUserMapper.insert(user);
        // 插入后 user.getId() 就能拿到数据库自动生成的ID

        // 5. 查一下角色信息，因为前端需要根据什么角色配置什么权限
        SysRole role = null;
        if (user.getRoleId() != null) {
            role = sysRoleMapper.findById(user.getRoleId());
        }

        // 6. 返回注册后的用户信息，这里就是接口那里所说的去掉关键信息
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRoleId(user.getRoleId());
        response.setStatus(user.getStatus());
        response.setRole(role);

        return response;//返回这个类
    }
}
