package com.example.warehouse.controller;

import com.example.warehouse.dto.Result;
import com.example.warehouse.mapper.*;
import com.example.warehouse.pojo.*;
import com.example.warehouse.service.StockService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

/*
 * 出入库管理 REST 接口
 * 入库: /api/stock-in/*    出库: /api/stock-out/*
 * 公共下拉: /api/stock/*
 */
@RestController
@RequestMapping("/api")
public class StockController {

    @Resource private StockService stockService;
    @Resource private SupplierMapper supplierMapper;
    @Resource private CustomerMapper customerMapper;
    @Resource private ProductMapper productMapper;
    @Resource private WarehouseMapper warehouseMapper;
    @Resource private StorageLocationMapper locationMapper;
    @Resource private StockInMapper stockInMapper;
    @Resource private StockOutMapper stockOutMapper;

    // ==================== 下拉数据 ====================

    @GetMapping("/supplier/all")
    public Result<List<Supplier>> getSuppliers(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) return Result.success(supplierMapper.search(keyword));
        return Result.success(supplierMapper.findAll());
    }

    @GetMapping("/customer/all")
    public Result<List<Customer>> getCustomers(@RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) return Result.success(customerMapper.search(keyword));
        return Result.success(customerMapper.findAll());
    }

    @GetMapping("/product/search")
    public Result<List<Product>> searchProducts(@RequestParam(required = false) String keyword) {
        return Result.success(productMapper.search(keyword != null ? keyword : ""));
    }

    @GetMapping("/warehouse/all")
    public Result<List<Warehouse>> getWarehouses() {
        return Result.success(warehouseMapper.findAll());
    }

    @GetMapping("/warehouse/{id}/locations")
    public Result<List<StorageLocation>> getLocations(@PathVariable Integer id) {
        return Result.success(locationMapper.findByWarehouseId(id));
    }

    // ==================== 入库 ====================

    @GetMapping("/stock-in/page")
    public Result<Map<String, Object>> listStockIn(
            @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String inNo, @RequestParam(required = false) String inType,
            @RequestParam(required = false) Integer supplierId, @RequestParam(required = false) String status) {
        return Result.success(stockService.listStockIn(pageNum, pageSize, inNo, inType, supplierId, status));
    }

    @GetMapping("/stock-in/{id}")
    public Result<Map<String, Object>> getStockIn(@PathVariable Integer id) {
        return Result.success(stockService.getStockInById(id));
    }

    @PostMapping("/stock-in")
    public Result<Void> createStockIn(@RequestBody Map<String, Object> body) {
        StockIn stockIn = buildStockIn(body);
        List<StockInDetail> details = buildInDetails((List<Map<String, Object>>) body.get("details"));
        stockService.createStockIn(stockIn, details);
        return Result.success("入库单创建成功", null);
    }

    @PutMapping("/stock-in")
    public Result<Void> updateStockIn(@RequestBody Map<String, Object> body) {
        StockIn stockIn = buildStockIn(body);
        stockIn.setId((Integer) body.get("id"));
        List<StockInDetail> details = buildInDetails((List<Map<String, Object>>) body.get("details"));
        stockService.updateStockIn(stockIn, details);
        return Result.success("入库单修改成功", null);
    }

    @DeleteMapping("/stock-in/{id}")
    public Result<Void> deleteStockIn(@PathVariable Integer id) {
        stockService.deleteStockIn(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/stock-in/{id}/status")
    public Result<Void> updateStockInStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        stockService.updateStockInStatus(id, body.get("status"));
        return Result.success("状态更新成功", null);
    }

    // ==================== 出库 ====================

    @GetMapping("/stock-out/page")
    public Result<Map<String, Object>> listStockOut(
            @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String outNo, @RequestParam(required = false) String outType,
            @RequestParam(required = false) Integer customerId, @RequestParam(required = false) String status) {
        return Result.success(stockService.listStockOut(pageNum, pageSize, outNo, outType, customerId, status));
    }

    @GetMapping("/stock-out/{id}")
    public Result<Map<String, Object>> getStockOut(@PathVariable Integer id) {
        return Result.success(stockService.getStockOutById(id));
    }

    @PostMapping("/stock-out")
    public Result<Void> createStockOut(@RequestBody Map<String, Object> body) {
        StockOut stockOut = buildStockOut(body);
        List<StockOutDetail> details = buildOutDetails((List<Map<String, Object>>) body.get("details"));
        stockService.createStockOut(stockOut, details);
        return Result.success("出库单创建成功", null);
    }

    @PutMapping("/stock-out")
    public Result<Void> updateStockOut(@RequestBody Map<String, Object> body) {
        StockOut stockOut = buildStockOut(body);
        stockOut.setId((Integer) body.get("id"));
        List<StockOutDetail> details = buildOutDetails((List<Map<String, Object>>) body.get("details"));
        stockService.updateStockOut(stockOut, details);
        return Result.success("出库单修改成功", null);
    }

    @DeleteMapping("/stock-out/{id}")
    public Result<Void> deleteStockOut(@PathVariable Integer id) {
        stockService.deleteStockOut(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/stock-out/{id}/status")
    public Result<Void> updateStockOutStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        stockService.updateStockOutStatus(id, body.get("status"));
        return Result.success("状态更新成功", null);
    }

    // ==================== 审核管理 (3.2.4) ====================

    /** 查询所有待审核的出入库单 */
    @GetMapping("/audit/pending")
    public Result<Map<String, Object>> getPendingAudits() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("stockInList", stockInMapper.selectPage(null, null, null, "pending", 0, 100));
        data.put("stockOutList", stockOutMapper.selectPage(null, null, null, "pending", 0, 100));
        return Result.success(data);
    }

    /** 审核通过（入库） */
    @PutMapping("/audit/stock-in/{id}/approve")
    public Result<Void> approveStockIn(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) {
        stockService.updateStockInStatus(id, "approved");
        return Result.success("入库单已审核通过，库存已更新", null);
    }

    /** 审核驳回（入库）——退回草稿状态，仓管员修改后重新提交 */
    @PutMapping("/audit/stock-in/{id}/reject")
    public Result<Void> rejectStockIn(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        stockService.updateStockInStatus(id, "draft");
        return Result.success("入库单已驳回" + (reason.isEmpty() ? "" : "，原因：" + reason), null);
    }

    /** 审核通过（出库） */
    @PutMapping("/audit/stock-out/{id}/approve")
    public Result<Void> approveStockOut(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) {
        stockService.updateStockOutStatus(id, "approved");
        return Result.success("出库单已审核通过，库存已扣减", null);
    }

    /** 审核驳回（出库） */
    @PutMapping("/audit/stock-out/{id}/reject")
    public Result<Void> rejectStockOut(@PathVariable Integer id, @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.getOrDefault("reason", "") : "";
        stockService.updateStockOutStatus(id, "draft");
        return Result.success("出库单已驳回" + (reason.isEmpty() ? "" : "，原因：" + reason), null);
    }

    // ==================== 辅助方法 ====================

    private StockIn buildStockIn(Map<String, Object> body) {
        StockIn s = new StockIn();
        s.setInType((String) body.get("inType"));
        s.setSupplierId(toInt(body.get("supplierId")));
        s.setWarehouseId(toInt(body.get("warehouseId")));
        s.setRemark((String) body.get("remark"));
        s.setStatus((String) body.getOrDefault("status", "draft"));
        return s;
    }

    private StockOut buildStockOut(Map<String, Object> body) {
        StockOut s = new StockOut();
        s.setOutType((String) body.get("outType"));
        s.setCustomerId(toInt(body.get("customerId")));
        s.setWarehouseId(toInt(body.get("warehouseId")));
        s.setRemark((String) body.get("remark"));
        s.setStatus((String) body.getOrDefault("status", "draft"));
        return s;
    }

    private List<StockInDetail> buildInDetails(List<Map<String, Object>> list) {
        if (list == null) return new ArrayList<>();
        List<StockInDetail> result = new ArrayList<>();
        for (Map<String, Object> m : list) {
            StockInDetail d = new StockInDetail();
            d.setProductId(toInt(m.get("productId")));
            d.setLocationId(toInt(m.get("locationId")));
            d.setQuantity(toInt(m.get("quantity")));
            d.setUnitPrice(toBigDecimal(m.get("unitPrice")));
            d.setAmount(toBigDecimal(m.get("amount")));
            result.add(d);
        }
        return result;
    }

    private List<StockOutDetail> buildOutDetails(List<Map<String, Object>> list) {
        if (list == null) return new ArrayList<>();
        List<StockOutDetail> result = new ArrayList<>();
        for (Map<String, Object> m : list) {
            StockOutDetail d = new StockOutDetail();
            d.setProductId(toInt(m.get("productId")));
            d.setLocationId(toInt(m.get("locationId")));
            Object qty = m.get("quantity");
            d.setQuantity(qty != null ? qty.toString() : null);
            d.setUnitPrice(toBigDecimal(m.get("unitPrice")));
            d.setAmount(toBigDecimal(m.get("amount")));
            result.add(d);
        }
        return result;
    }

    private Integer toInt(Object val) {
        if (val instanceof Integer) return (Integer) val;
        if (val instanceof Number) return ((Number) val).intValue();
        return null;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return BigDecimal.valueOf(((Number) val).doubleValue());
        if (val instanceof String) return new BigDecimal((String) val);
        return null;
    }
}
