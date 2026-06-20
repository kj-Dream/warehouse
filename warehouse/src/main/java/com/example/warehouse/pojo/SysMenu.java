package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysMenu {
    private Integer id;
    private Integer parentId;  // 注意：数据库字段名是 partne_id
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer orderNum;
}