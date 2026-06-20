package com.example.warehouse.mapper;

import com.example.warehouse.pojo.StorageLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StorageLocationMapper {
    List<StorageLocation> findByWarehouseId(@Param("warehouseId") Integer warehouseId);
    StorageLocation findById(@Param("id") Integer id);
    int insert(StorageLocation location);
    int updateById(StorageLocation location);
    int deleteById(@Param("id") Integer id);
}
