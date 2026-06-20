package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInDetail {
    private Integer id;
    private Integer stockInId;
    private Integer productId;
    private Integer locationId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}