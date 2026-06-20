/*
 * 商品数据操作接口
 * 支持商品的分页查询、模糊搜索、增删改查
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    Product findById(@Param("id") Integer id);
    List<Product> search(@Param("keyword") String keyword);

    // 完整CRUD
    List<Product> selectPage(@Param("productName") String productName,
                             @Param("categoryId") Integer categoryId,
                             @Param("status") Integer status,
                             @Param("offset") int offset, @Param("limit") int limit);
    long countPage(@Param("productName") String productName,
                   @Param("categoryId") Integer categoryId,
                   @Param("status") Integer status);
    int insert(Product product);
    int updateById(Product product);
    int deleteById(@Param("id") Integer id);
}
