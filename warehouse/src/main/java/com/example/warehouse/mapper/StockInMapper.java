package com.example.warehouse.mapper;

import com.example.warehouse.pojo.StockIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockInMapper {
    // 分页查询（按单号/类型/供应商/状态/日期筛选）
    List<StockIn> selectPage(@Param("inNo") String inNo, @Param("inType") String inType,
                             @Param("supplierId") Integer supplierId, @Param("status") String status,
                             @Param("offset") int offset, @Param("limit") int limit);
    long countPage(@Param("inNo") String inNo, @Param("inType") String inType,
                   @Param("supplierId") Integer supplierId, @Param("status") String status);

    StockIn findById(@Param("id") Integer id);
    int insert(StockIn stockIn);
    int updateById(StockIn stockIn);
    int deleteById(@Param("id") Integer id);
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    // 生成入库单号: IN + 日期 + 序号
    String generateInNo(@Param("prefix") String prefix);
}
