package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer roleId;
    private Integer deptId;
    private Integer status;
    private Date creteTime;  // 注意：数据库字段名是 cretetime
}