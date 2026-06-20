package com.example.warehouse.mapper;

import com.example.warehouse.pojo.Warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarehouseMapper {
    List<Warehouse> findAll();
    Warehouse findById(@Param("id") Integer id);
    int insert(Warehouse warehouse);
    int updateById(Warehouse warehouse);
    int deleteById(@Param("id") Integer id);
}
