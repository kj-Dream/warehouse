/*
 * 库存数据操作接口
 * 负责库存的查询和更新，出入库审核通过后自动调用
 */
package com.example.warehouse.mapper;

import com.example.warehouse.pojo.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryMapper {

    /** 查询某商品在某仓库某库位的库存记录 */
    Inventory findByProductAndLocation(@Param("productId") Integer productId,
                                       @Param("warehouseId") Integer warehouseId,
                                       @Param("locationId") Integer locationId);

    /** 增加库存（入库审核通过时调用） */
    int addStock(@Param("productId") Integer productId,
                 @Param("warehouseId") Integer warehouseId,
                 @Param("locationId") Integer locationId,
                 @Param("qty") Integer qty);

    /** 扣减库存（出库审核通过时调用），返回影响行数=0表示库存不足 */
    int subtractStock(@Param("productId") Integer productId,
                      @Param("warehouseId") Integer warehouseId,
                      @Param("locationId") Integer locationId,
                      @Param("qty") Integer qty);

    /** 插入新的库存记录 */
    int insert(Inventory inventory);

    /** 分页查询所有库存 */
    List<Inventory> selectPage(@Param("productName") String productName,
                               @Param("warehouseId") Integer warehouseId,
                               @Param("offset") int offset, @Param("limit") int limit);
    long countPage(@Param("productName") String productName,
                   @Param("warehouseId") Integer warehouseId);

    /** 查询库存预警（低于最低库存或高于最高库存的商品） */
    List<Map<String, Object>> selectWarning(@Param("offset") int offset, @Param("limit") int limit);
    long countWarning();
}
