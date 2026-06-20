package com.example.warehouse.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {
    private Integer id;
    private Integer parentId;
    private String categoryName;
    private String categoryCode;


}
