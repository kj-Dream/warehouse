package com.example.warehouse.mapper;

import com.example.warehouse.pojo.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SupplierMapper {
    List<Supplier> findAll();
    Supplier findById(@Param("id") Integer id);
    List<Supplier> search(@Param("keyword") String keyword);
}
