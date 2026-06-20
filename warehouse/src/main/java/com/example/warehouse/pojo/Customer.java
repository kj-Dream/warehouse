package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private Integer id;
    private String customerCode;
    private String customerName;
    private String contact;
    private String phone;
    private String address;
    private Integer status;

}
