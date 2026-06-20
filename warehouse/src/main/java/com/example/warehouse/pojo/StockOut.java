package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOut {
    private Integer id;
    private String outNo;
    private String outType;
    private Integer customerId;
    private Integer supplierId;
    private Integer warehouseId;
    private BigDecimal totalAmount;
    private String status;
    private Integer operator;
    private String remark;
    private Date createTime;
}