package com.example.warehouse.mapper;

import com.example.warehouse.pojo.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    List<Customer> findAll();
    Customer findById(@Param("id") Integer id);
    List<Customer> search(@Param("keyword") String keyword);
}
