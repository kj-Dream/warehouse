/*
 * ============================================================
 * 【Result】统一响应格式
 * ============================================================
 * 这个类非常重要！它是前后端约定的"通信协议"。
 *
 * 如果没有这个类，每个接口返回的格式可能都不一样：
 *   - 登录成功返回 {"id":1,"name":"admin"}
 *   - 登录失败返回 {"error":"密码错误"}
 *   前端就得针对每个接口写不同的解析逻辑，非常痛苦。
 *
 * 有了 Result 后，所有接口统一返回：
 *   ✅ 成功：{"code":200, "msg":"登录成功", "data":{...}}
 *   ❌ 失败：{"code":500, "msg":"密码错误", "data":null}
 *
 * 前端只要判断 code 是不是 200，就能知道成功还是失败，
 * 然后从 data 里拿数据，从 msg 里看提示信息。
 *
 * <T> 是泛型，意思是 data 字段可以是任意类型，
 * 登录返回 LoginResponse，查商品返回 List<Product>，都行。
 * ============================================================
 */
package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;  // 状态码：200=成功，500=失败
    private String msg;    // 提示信息
    private T data;        // 具体返回的数据

    // 下面的 static 方法就是"快捷方式"，方便我们直接调用
    // 比如 Result.success(data) 就返回一个 code=200 的成功结果

    /** 操作成功（不带自定义消息） */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /** 操作成功（带自定义消息） */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /** 操作失败 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /** 操作失败（自定义状态码） */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}
