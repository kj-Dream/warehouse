package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private Integer id;
    private Integer productId;
    private Integer warehouseId;
    private Integer locationId;
    private Integer quantity;
    private Integer version;
}