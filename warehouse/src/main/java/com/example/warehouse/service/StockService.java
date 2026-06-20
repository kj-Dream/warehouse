package com.example.warehouse.service;

import com.example.warehouse.pojo.*;

import java.util.List;
import java.util.Map;

/*
 * 出入库管理业务接口
 * 包含入库单和出库单的完整 CRUD 操作
 */
public interface StockService {

    // ========== 入库 ==========
    Map<String, Object> listStockIn(Integer pageNum, Integer pageSize, String inNo, String inType, Integer supplierId, String status);
    Map<String, Object> getStockInById(Integer id);
    void createStockIn(StockIn stockIn, List<StockInDetail> details);
    void updateStockIn(StockIn stockIn, List<StockInDetail> details);
    void deleteStockIn(Integer id);
    void updateStockInStatus(Integer id, String status);

    // ========== 出库 ==========
    Map<String, Object> listStockOut(Integer pageNum, Integer pageSize, String outNo, String outType, Integer customerId, String status);
    Map<String, Object> getStockOutById(Integer id);
    void createStockOut(StockOut stockOut, List<StockOutDetail> details);
    void updateStockOut(StockOut stockOut, List<StockOutDetail> details);
    void deleteStockOut(Integer id);
    void updateStockOutStatus(Integer id, String status);
}
