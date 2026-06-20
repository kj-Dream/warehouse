package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageLocation {
    private Integer id;
    private Integer warehouseId;
    private String locationCode;
    private String locationName;
    private Integer status;
}