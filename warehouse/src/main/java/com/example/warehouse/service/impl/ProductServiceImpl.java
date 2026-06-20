package com.example.warehouse.service.impl;

import com.example.warehouse.mapper.*;
import com.example.warehouse.pojo.*;
import com.example.warehouse.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource private ProductMapper productMapper;
    @Resource private ProductCategoryMapper categoryMapper;

    @Override
    public Map<String, Object> listProducts(Integer pageNum, Integer pageSize, String productName, Integer categoryId, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        Map<String, Object> r = new HashMap<>();
        r.put("list", productMapper.selectPage(productName, categoryId, status, offset, pageSize));
        r.put("total", productMapper.countPage(productName, categoryId, status));
        r.put("pageNum", pageNum); r.put("pageSize", pageSize);
        return r;
    }

    @Override public Product getById(Integer id) { return productMapper.findById(id); }

    @Override public void createProduct(Product product) { productMapper.insert(product); }

    @Override public void updateProduct(Product product) { productMapper.updateById(product); }

    @Override public void deleteProduct(Integer id) { productMapper.deleteById(id); }

    /** 组装分类树（parentId=0为一级） */
    @Override
    public List<Map<String, Object>> getCategoryTree() {
        List<ProductCategory> all = categoryMapper.findAll();
        List<Map<String, Object>> tree = new ArrayList<>();
        for (ProductCategory c : all) {
            if (c.getParentId() == 0) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", c.getId()); node.put("categoryName", c.getCategoryName());
                node.put("categoryCode", c.getCategoryCode());
                List<Map<String, Object>> children = new ArrayList<>();
                for (ProductCategory sub : all) {
                    if (Objects.equals(sub.getParentId(), c.getId())) {
                        Map<String, Object> cn = new LinkedHashMap<>();
                        cn.put("id", sub.getId()); cn.put("categoryName", sub.getCategoryName());
                        cn.put("categoryCode", sub.getCategoryCode());
                        children.add(cn);
                    }
                }
                node.put("children", children);
                tree.add(node);
            }
        }
        return tree;
    }

    @Override public void createCategory(ProductCategory category) { categoryMapper.insert(category); }

    @Override public void updateCategory(ProductCategory category) { categoryMapper.updateById(category); }

    @Override @Transactional
    public void deleteCategory(Integer id) {
        if (categoryMapper.countChildren(id) > 0) throw new RuntimeException("该分类下还有子分类，请先删除子分类");
        if (categoryMapper.countProducts(id) > 0) throw new RuntimeException("该分类下还有商品，请先移走商品");
        categoryMapper.deleteById(id);
    }
}
