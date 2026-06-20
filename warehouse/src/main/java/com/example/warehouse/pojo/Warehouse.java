package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {
    private Integer id;
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private String manager;
    private Integer status;

}
