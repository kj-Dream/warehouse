package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutDetail {
    private Integer id;
    private Integer stockOutId;
    private Integer productId;
    private Integer locationId;
    private String quantity;  // 注意：数据库中是varchar类型
    private BigDecimal unitPrice;
    private BigDecimal amount;
}