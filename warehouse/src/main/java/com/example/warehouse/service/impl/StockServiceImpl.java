package com.example.warehouse.service.impl;

import com.example.warehouse.mapper.*;
import com.example.warehouse.pojo.*;
import com.example.warehouse.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class StockServiceImpl implements StockService {

    @Resource private StockInMapper stockInMapper;
    @Resource private StockInDetailMapper stockInDetailMapper;
    @Resource private StockOutMapper stockOutMapper;
    @Resource private StockOutDetailMapper stockOutDetailMapper;
    @Resource private InventoryMapper inventoryMapper;

    // ==================== 入库 ====================

    @Override
    public Map<String, Object> listStockIn(Integer pageNum, Integer pageSize, String inNo, String inType, Integer supplierId, String status) {
        int offset = (pageNum - 1) * pageSize;
        List<StockIn> list = stockInMapper.selectPage(inNo, inType, supplierId, status, offset, pageSize);
        long total = stockInMapper.countPage(inNo, inType, supplierId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list); result.put("total", total);
        result.put("pageNum", pageNum); result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getStockInById(Integer id) {
        StockIn stockIn = stockInMapper.findById(id);
        if (stockIn == null) throw new RuntimeException("入库单不存在");
        List<StockInDetail> details = stockInDetailMapper.findByStockInId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("order", stockIn);
        result.put("details", details);
        return result;
    }

    @Override
    @Transactional
    public void createStockIn(StockIn stockIn, List<StockInDetail> details) {
        if (details == null || details.isEmpty()) throw new RuntimeException("请添加商品明细");
        // 自动计算总金额
        BigDecimal total = details.stream()
                .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stockIn.setTotalAmount(total);
        // 生成单号: IN20260620001
        String prefix = "IN" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        stockIn.setInNo(prefix + String.format("%03d", stockInMapper.selectPage(null, null, null, null, 0, 1).size() + 1));
        stockInMapper.insert(stockIn);
        // 插入明细
        for (StockInDetail detail : details) {
            detail.setStockInId(stockIn.getId());
            stockInDetailMapper.insert(detail);
        }
    }

    @Override
    @Transactional
    public void updateStockIn(StockIn stockIn, List<StockInDetail> details) {
        StockIn exist = stockInMapper.findById(stockIn.getId());
        if (exist == null) throw new RuntimeException("入库单不存在");
        if (!"draft".equals(exist.getStatus())) throw new RuntimeException("只有草稿状态的单据可以修改");
        // 重新计算金额
        if (details != null && !details.isEmpty()) {
            BigDecimal total = details.stream()
                    .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stockIn.setTotalAmount(total);
            // 先删旧明细再插新的
            stockInDetailMapper.deleteByStockInId(stockIn.getId());
            for (StockInDetail detail : details) {
                detail.setStockInId(stockIn.getId());
                stockInDetailMapper.insert(detail);
            }
        }
        stockInMapper.updateById(stockIn);
    }

    @Override
    @Transactional
    public void deleteStockIn(Integer id) {
        StockIn exist = stockInMapper.findById(id);
        if (exist == null) throw new RuntimeException("入库单不存在");
        stockInDetailMapper.deleteByStockInId(id);
        stockInMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStockInStatus(Integer id, String status) {
        StockIn exist = stockInMapper.findById(id);
        if (exist == null) throw new RuntimeException("入库单不存在");
        stockInMapper.updateStatus(id, status);
        // 审核通过：自动增加库存
        if ("approved".equals(status)) {
            List<StockInDetail> details = stockInDetailMapper.findByStockInId(id);
            for (StockInDetail d : details) {
                inventoryMapper.addStock(d.getProductId(), exist.getWarehouseId(),
                        d.getLocationId(), d.getQuantity());
            }
        }
    }

    // ==================== 出库 ====================

    @Override
    public Map<String, Object> listStockOut(Integer pageNum, Integer pageSize, String outNo, String outType, Integer customerId, String status) {
        int offset = (pageNum - 1) * pageSize;
        List<StockOut> list = stockOutMapper.selectPage(outNo, outType, customerId, status, offset, pageSize);
        long total = stockOutMapper.countPage(outNo, outType, customerId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list); result.put("total", total);
        result.put("pageNum", pageNum); result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Map<String, Object> getStockOutById(Integer id) {
        StockOut stockOut = stockOutMapper.findById(id);
        if (stockOut == null) throw new RuntimeException("出库单不存在");
        List<StockOutDetail> details = stockOutDetailMapper.findByStockOutId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("order", stockOut);
        result.put("details", details);
        return result;
    }

    @Override
    @Transactional
    public void createStockOut(StockOut stockOut, List<StockOutDetail> details) {
        if (details == null || details.isEmpty()) throw new RuntimeException("请添加商品明细");
        BigDecimal total = details.stream()
                .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stockOut.setTotalAmount(total);
        String prefix = "OUT" + new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        stockOut.setOutNo(prefix + String.format("%03d", stockOutMapper.selectPage(null, null, null, null, 0, 1).size() + 1));
        stockOutMapper.insert(stockOut);
        for (StockOutDetail detail : details) {
            detail.setStockOutId(stockOut.getId());
            stockOutDetailMapper.insert(detail);
        }
    }

    @Override
    @Transactional
    public void updateStockOut(StockOut stockOut, List<StockOutDetail> details) {
        StockOut exist = stockOutMapper.findById(stockOut.getId());
        if (exist == null) throw new RuntimeException("出库单不存在");
        if (!"draft".equals(exist.getStatus())) throw new RuntimeException("只有草稿状态的单据可以修改");
        if (details != null && !details.isEmpty()) {
            BigDecimal total = details.stream()
                    .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            stockOut.setTotalAmount(total);
            stockOutDetailMapper.deleteByStockOutId(stockOut.getId());
            for (StockOutDetail detail : details) {
                detail.setStockOutId(stockOut.getId());
                stockOutDetailMapper.insert(detail);
            }
        }
        stockOutMapper.updateById(stockOut);
    }

    @Override
    @Transactional
    public void deleteStockOut(Integer id) {
        StockOut exist = stockOutMapper.findById(id);
        if (exist == null) throw new RuntimeException("出库单不存在");
        stockOutDetailMapper.deleteByStockOutId(id);
        stockOutMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStockOutStatus(Integer id, String status) {
        StockOut exist = stockOutMapper.findById(id);
        if (exist == null) throw new RuntimeException("出库单不存在");
        stockOutMapper.updateStatus(id, status);
        // 审核通过：自动扣减库存
        if ("approved".equals(status)) {
            List<StockOutDetail> details = stockOutDetailMapper.findByStockOutId(id);
            for (StockOutDetail d : details) {
                int rows = inventoryMapper.subtractStock(d.getProductId(), exist.getWarehouseId(),
                        d.getLocationId(), Integer.parseInt(d.getQuantity()));
                if (rows == 0) {
                    throw new RuntimeException("商品ID" + d.getProductId() + " 库存不足，无法完成出库审核");
                }
            }
        }
    }
}
