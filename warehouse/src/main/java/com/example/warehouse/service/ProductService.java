package com.example.warehouse.service;

import com.example.warehouse.pojo.Product;
import com.example.warehouse.pojo.ProductCategory;

import java.util.List;
import java.util.Map;

public interface ProductService {
    // 商品CRUD
    Map<String, Object> listProducts(Integer pageNum, Integer pageSize, String productName, Integer categoryId, Integer status);
    Product getById(Integer id);
    void createProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Integer id);

    // 分类管理
    List<Map<String, Object>> getCategoryTree();
    void createCategory(ProductCategory category);
    void updateCategory(ProductCategory category);
    void deleteCategory(Integer id);
}
