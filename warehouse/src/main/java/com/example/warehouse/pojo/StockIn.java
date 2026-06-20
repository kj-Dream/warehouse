package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockIn {
    private Integer id;
    private String inNo;
    private String inType;
    private Integer supplierId;
    private Integer customerId;
    private Integer warehouseId;
    private BigDecimal totalAmount;
    private String status;
    private Integer operator;
    private String remark;
    private Date createTime;
}