/*
 * ============================================================
 * 【UserPageRequest】用户列表的分页查询参数
 * ============================================================
 * 这个类用来接收前端传过来的"查询条件"。
 * 管理员在用户管理页面可以按条件筛选用户：
 *   - 按用户名搜索
 *   - 按真实姓名搜索
 *   - 按角色ID筛选
 *   - 按状态筛选（启用/禁用）
 *   - 分页参数（第几页、每页多少条）
 *
 * 所有查询条件都是可选的（Optional），
 * 如果不传某个条件，就查全部。
 * 比如不传 username，就不按用户名过滤。
 *
 * 为什么用 Integer 而不是 int？
 *   int 默认值是 0，但 0 本身也是有效值（status=0 表示禁用），
 *   用 Integer 才能区分"没传"和"传了0"这两种情况。
 * ============================================================
 */
package com.example.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPageRequest {

    /** 当前页码（从 1 开始，默认 1） */
    private Integer pageNum = 1;

    /** 每页条数（默认 10） */
    private Integer pageSize = 10;

    /** 用户名（模糊搜索） */
    private String username;

    /** 真实姓名（模糊搜索） */
    private String realName;

    /** 角色ID（精确筛选） */
    private Integer roleId;

    /** 状态（1=启用，0=禁用，不传=查全部） */
    private Integer status;
}
