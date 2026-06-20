package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private String productCode;
    private String productName;
    private Integer categoryId;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Integer minStock;
    private Integer maxStock;
    private String imageUrl;
    private Integer status;


}
