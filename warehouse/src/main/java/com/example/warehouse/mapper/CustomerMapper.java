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

    List<Customer> selectPage(@Param("customerName") String customerName, @Param("status") Integer status,
                              @Param("offset") int offset, @Param("limit") int limit);
    long countPage(@Param("customerName") String customerName, @Param("status") Integer status);
    int insert(Customer customer);
    int updateById(Customer customer);
    int deleteById(@Param("id") Integer id);
}
