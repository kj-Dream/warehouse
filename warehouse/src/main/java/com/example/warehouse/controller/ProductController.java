package com.example.warehouse.controller;

import com.example.warehouse.dto.Result;
import com.example.warehouse.pojo.*;
import com.example.warehouse.service.ProductService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * 商品管理 REST 接口
 * 商品CRUD: /api/product/*
 * 分类CRUD: /api/product-category/*
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    @Resource private ProductService productService;

    // ===== 商品 =====
    @GetMapping("/product/page")
    public Result<Map<String, Object>> page(@RequestParam(defaultValue="1")Integer pageNum, @RequestParam(defaultValue="10")Integer pageSize,
            @RequestParam(required=false)String productName, @RequestParam(required=false)Integer categoryId, @RequestParam(required=false)Integer status) {
        return Result.success(productService.listProducts(pageNum,pageSize,productName,categoryId,status));
    }

    @GetMapping("/product/{id}")
    public Result<Product> getById(@PathVariable Integer id) { return Result.success(productService.getById(id)); }

    @PostMapping("/product")
    public Result<Void> create(@RequestBody Map<String,Object> body) {
        Product p = new Product();
        p.setProductCode((String)body.get("productCode"));
        p.setProductName((String)body.get("productName"));
        p.setCategoryId(toInt(body.get("categoryId")));
        p.setUnit((String)body.get("unit"));
        p.setCostPrice(toBigDecimal(body.get("costPrice")));
        p.setSalePrice(toBigDecimal(body.get("salePrice")));
        p.setMinStock(toInt(body.get("minStock")));
        p.setMaxStock(toInt(body.get("maxStock")));
        productService.createProduct(p);
        return Result.success("商品创建成功",null);
    }

    @PutMapping("/product")
    public Result<Void> update(@RequestBody Map<String,Object> body) {
        Product p = new Product();
        p.setId(toInt(body.get("id")));
        p.setProductCode((String)body.get("productCode"));
        p.setProductName((String)body.get("productName"));
        p.setCategoryId(toInt(body.get("categoryId")));
        p.setUnit((String)body.get("unit"));
        p.setCostPrice(toBigDecimal(body.get("costPrice")));
        p.setSalePrice(toBigDecimal(body.get("salePrice")));
        p.setMinStock(toInt(body.get("minStock")));
        p.setMaxStock(toInt(body.get("maxStock")));
        p.setStatus(toInt(body.get("status")));
        productService.updateProduct(p);
        return Result.success("修改成功",null);
    }

    @DeleteMapping("/product/{id}")
    public Result<Void> delete(@PathVariable Integer id) { productService.deleteProduct(id); return Result.success("删除成功",null); }

    // ===== 分类 =====
    @GetMapping("/product-category/tree")
    public Result<List<Map<String,Object>>> categoryTree() { return Result.success(productService.getCategoryTree()); }

    @PostMapping("/product-category")
    public Result<Void> createCategory(@RequestBody Map<String,Object> body) {
        ProductCategory c = new ProductCategory();
        c.setParentId(toInt(body.get("parentId")));
        c.setCategoryName((String)body.get("categoryName"));
        c.setCategoryCode((String)body.get("categoryCode"));
        productService.createCategory(c);
        return Result.success("分类创建成功",null);
    }

    @PutMapping("/product-category")
    public Result<Void> updateCategory(@RequestBody Map<String,Object> body) {
        ProductCategory c = new ProductCategory();
        c.setId(toInt(body.get("id")));
        c.setCategoryName((String)body.get("categoryName"));
        c.setCategoryCode((String)body.get("categoryCode"));
        productService.updateCategory(c);
        return Result.success("修改成功",null);
    }

    @DeleteMapping("/product-category/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id) { productService.deleteCategory(id); return Result.success("删除成功",null); }

    private Integer toInt(Object v) { return v instanceof Number?((Number)v).intValue():null; }
    private BigDecimal toBigDecimal(Object v) {
        if(v instanceof BigDecimal)return(BigDecimal)v; if(v instanceof Number)return BigDecimal.valueOf(((Number)v).doubleValue());
        if(v instanceof String)return new BigDecimal((String)v); return null;
    }
}
