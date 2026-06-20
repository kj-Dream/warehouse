/*
 * ============================================================
 * 【PasswordUtil】密码加密工具类
 * ============================================================
 * 这个工具类负责所有和密码加密相关的操作。
 *
 * 为什么不能用明文存密码？
 *   假设数据库被人看到了（比如被黑客攻击、运维人员误操作），
 *   如果密码是明文"123456"，那所有账号都暴露了。
 *   加密后存的是"$2a$10$xxx..."，就算数据库泄露也猜不出原密码。
 *
 * BCrypt 算法有什么特点？
 *   1. 自带"盐值"（Salt）：同样的密码每次加密结果都不一样，
 *      这样就算两个人密码相同，数据库里存的值也不同。
 *   2. 加密速度慢：故意设计得慢，防止坏人用计算机暴力破解。
 *   3. 不可逆：只能从明文→密文，不能从密文→明文。
 *
 * 使用方式：
 *   注册时：PasswordUtil.encode("123456") → 存到数据库
 *   登录时：PasswordUtil.matches("123456", 数据库存的密文) → true/false
 *
 * 兼容老数据的方式：
 *   isBCryptHash(password) 判断数据库里的密码是不是 BCrypt 格式。
 *   如果不是（比如旧的明文"123456"），就用明文比对，
 *   比对成功后自动升级为 BCrypt 密文存回去。
 * ============================================================
 */
package com.example.warehouse.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    /**
     * BCrypt 加密器（单例）
     * BCryptPasswordEncoder 是 spring-security-crypto 提供的加密工具。
     * 内部已经实现好了所有加密逻辑，我们只需要调用就行。
     */
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 加密密码（注册/重置密码时使用）
     *
     * @param rawPassword 明文密码，比如 "123456"
     * @return BCrypt 密文，比如 "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh"
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码（登录时使用）
     *
     * @param rawPassword     用户输入的明文密码
     * @param encodedPassword 数据库里存的密文
     * @return true=密码正确, false=密码错误
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 判断数据库里的密码是不是 BCrypt 格式
     *
     * BCrypt 密文的特征：以 "$2a$" 或 "$2b$" 或 "$2y$" 开头
     * 老数据是明文"123456"，没有这个前缀。
     *
     * @param password 数据库里的密码
     * @return true=已经是 BCrypt 密文, false=是明文或其它格式
     */
    public static boolean isBCryptHash(String password) {
        return password != null && password.startsWith("$2a$");
    }
}
