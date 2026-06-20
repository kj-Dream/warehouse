package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {
    private Integer id;
    private String supplierCode;
    private String supplierName;
    private String contact;
    private String phone;
    private String address;
    private Integer status;


}
