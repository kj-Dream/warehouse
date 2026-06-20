/*
 * 商品分类数据操作接口
 * 分类是树形结构（parent_id），支持层级管理
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    /** 查所有分类（平铺列表，Service层组装成树） */
    List<ProductCategory> findAll();
    ProductCategory findById(@Param("id") Integer id);
    int insert(ProductCategory category);
    int updateById(ProductCategory category);
    int deleteById(@Param("id") Integer id);
    /** 检查分类下是否有子分类 */
    int countChildren(@Param("parentId") Integer parentId);
    /** 检查分类下是否有商品 */
    int countProducts(@Param("categoryId") Integer categoryId);
}
