package com.example.warehouse.mapper;

import com.example.warehouse.pojo.StockOutDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockOutDetailMapper {
    List<StockOutDetail> findByStockOutId(@Param("stockOutId") Integer stockOutId);
    int insert(StockOutDetail detail);
    int deleteByStockOutId(@Param("stockOutId") Integer stockOutId);
}
