package com.example.warehouse.mapper;

import com.example.warehouse.pojo.StockInDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockInDetailMapper {
    List<StockInDetail> findByStockInId(@Param("stockInId") Integer stockInId);
    int insert(StockInDetail detail);
    int deleteByStockInId(@Param("stockInId") Integer stockInId);
}
