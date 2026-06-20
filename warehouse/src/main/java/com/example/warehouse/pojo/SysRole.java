package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRole {
    private Integer id;
    private String roleName;
    private String roleKey;
    private String description;
    private Integer status;
    private Date createTime;
}