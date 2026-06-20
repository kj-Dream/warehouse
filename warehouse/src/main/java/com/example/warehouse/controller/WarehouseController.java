package com.example.warehouse.controller;

import com.example.warehouse.dto.Result;
import com.example.warehouse.mapper.*;
import com.example.warehouse.pojo.*;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.*;

/*
 * 仓库 & 库位 & 库存管理
 */
@RestController
@RequestMapping("/api")
public class WarehouseController {

    @Resource private WarehouseMapper warehouseMapper;
    @Resource private StorageLocationMapper locationMapper;
    @Resource private InventoryMapper inventoryMapper;

    // ===== 仓库 =====
    @GetMapping("/warehouses/page")
    public Result<List<Warehouse>> listWarehouses() { return Result.success(warehouseMapper.findAll()); }

    @PostMapping("/warehouses")
    public Result<Void> createWarehouse(@RequestBody Warehouse w) {
        warehouseMapper.insert(w); return Result.success("创建成功", null);
    }

    @PutMapping("/warehouses")
    public Result<Void> updateWarehouse(@RequestBody Warehouse w) {
        warehouseMapper.updateById(w); return Result.success("修改成功", null);
    }

    @DeleteMapping("/warehouses/{id}")
    public Result<Void> deleteWarehouse(@PathVariable Integer id) {
        warehouseMapper.deleteById(id); return Result.success("删除成功", null);
    }

    // ===== 库位 =====
    @GetMapping("/locations/list")
    public Result<Object> listLocations(@RequestParam(required = false) Integer warehouseId) {
        if (warehouseId != null) return Result.success(locationMapper.findByWarehouseId(warehouseId));
        // 查所有
        List<Warehouse> whs = warehouseMapper.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Warehouse wh : whs) {
            List<StorageLocation> locs = locationMapper.findByWarehouseId(wh.getId());
            for (StorageLocation l : locs) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", l.getId()); m.put("warehouseName", wh.getWarehouseName());
                m.put("locationCode", l.getLocationCode()); m.put("locationName", l.getLocationName());
                m.put("status", l.getStatus());
                result.add(m);
            }
        }
        return Result.success(result);
    }

    @PostMapping("/locations")
    public Result<Void> createLocation(@RequestBody StorageLocation loc) {
        locationMapper.insert(loc); return Result.success("创建成功", null);
    }

    @PutMapping("/locations")
    public Result<Void> updateLocation(@RequestBody StorageLocation loc) {
        locationMapper.updateById(loc); return Result.success("修改成功", null);
    }

    @DeleteMapping("/locations/{id}")
    public Result<Void> deleteLocation(@PathVariable Integer id) {
        locationMapper.deleteById(id); return Result.success("删除成功", null);
    }

    // ===== 库存查询 =====
    @GetMapping("/inventory/page")
    public Result<Map<String, Object>> inventoryPage(
            @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String productName, @RequestParam(required = false) Integer warehouseId) {
        Map<String, Object> r = new HashMap<>();
        r.put("list", inventoryMapper.selectPage(productName, warehouseId, (pageNum - 1) * pageSize, pageSize));
        r.put("total", inventoryMapper.countPage(productName, warehouseId));
        r.put("pageNum", pageNum); r.put("pageSize", pageSize);
        return Result.success(r);
    }

    // ===== 库存预警 =====
    @GetMapping("/inventory/warning")
    public Result<Map<String, Object>> inventoryWarning(
            @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> r = new HashMap<>();
        r.put("list", inventoryMapper.selectWarning((pageNum - 1) * pageSize, pageSize));
        r.put("total", inventoryMapper.countWarning());
        return Result.success(r);
    }
}
