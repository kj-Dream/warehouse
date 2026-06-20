package com.example.warehouse.mapper;

import com.example.warehouse.pojo.StockOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockOutMapper {
    List<StockOut> selectPage(@Param("outNo") String outNo, @Param("outType") String outType,
                              @Param("customerId") Integer customerId, @Param("status") String status,
                              @Param("offset") int offset, @Param("limit") int limit);
    long countPage(@Param("outNo") String outNo, @Param("outType") String outType,
                   @Param("customerId") Integer customerId, @Param("status") String status);

    StockOut findById(@Param("id") Integer id);
    int insert(StockOut stockOut);
    int updateById(StockOut stockOut);
    int deleteById(@Param("id") Integer id);
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
}
