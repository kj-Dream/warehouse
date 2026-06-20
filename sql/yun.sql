/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80044
Source Host           : localhost:3306
Source Database       : yun

Target Server Type    : MYSQL
Target Server Version : 80044
File Encoding         : 65001

Date: 2026-06-15 15:35:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` int NOT NULL,
  `customer_code` varchar(50) DEFAULT NULL,
  `customer_name` varchar(100) DEFAULT NULL,
  `contact` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES ('1', 'C001', '北京科技有限公司', '周经理', '13900139001', '北京市西城区', '1');
INSERT INTO `customer` VALUES ('2', 'C002', '上海贸易有限公司', '吴经理', '13900139002', '上海市黄浦区', '1');
INSERT INTO `customer` VALUES ('3', 'C003', '广州零售连锁', '郑经理', '13900139003', '广州市越秀区', '1');
INSERT INTO `customer` VALUES ('4', 'C004', '深圳电商平台', '王经理', '13900139004', '深圳市福田区', '1');

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `warehouse_id` int DEFAULT NULL,
  `location_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `version` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES ('1', '1', '1', '1', '200', '1');
INSERT INTO `inventory` VALUES ('2', '2', '1', '1', '80', '1');
INSERT INTO `inventory` VALUES ('3', '3', '1', '2', '40', '1');
INSERT INTO `inventory` VALUES ('4', '4', '1', '3', '50', '1');
INSERT INTO `inventory` VALUES ('5', '5', '1', '3', '500', '1');
INSERT INTO `inventory` VALUES ('6', '6', '1', '4', '100', '1');
INSERT INTO `inventory` VALUES ('7', '7', '2', '5', '15', '1');
INSERT INTO `inventory` VALUES ('8', '8', '2', '5', '300', '1');
INSERT INTO `inventory` VALUES ('9', '9', '2', '6', '100', '1');
INSERT INTO `inventory` VALUES ('10', '10', '3', '7', '150', '1');
INSERT INTO `inventory` VALUES ('11', '1', '2', '5', '100', '1');
INSERT INTO `inventory` VALUES ('12', '2', '3', '8', '50', '1');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int NOT NULL,
  `product_code` varchar(50) DEFAULT NULL,
  `product_name` varchar(100) DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `cost_price` decimal(10,2) DEFAULT NULL,
  `sale_price` decimal(10,2) DEFAULT NULL,
  `min_stock` int DEFAULT NULL,
  `max_stock` int DEFAULT NULL,
  `image_url` varchar(200) DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', 'P001', 'USB数据线', '4', '条', '5.00', '10.00', '100', '500', null, '1');
INSERT INTO `product` VALUES ('2', 'P002', '无线鼠标', '5', '个', '25.00', '45.00', '50', '200', null, '1');
INSERT INTO `product` VALUES ('3', 'P003', '机械键盘', '5', '个', '80.00', '150.00', '30', '100', null, '1');
INSERT INTO `product` VALUES ('4', 'P004', 'A4打印纸', '6', '包', '15.00', '25.00', '20', '100', null, '1');
INSERT INTO `product` VALUES ('5', 'P005', '中性笔', '6', '支', '1.50', '3.00', '200', '1000', null, '1');
INSERT INTO `product` VALUES ('6', 'P006', '订书机', '6', '个', '8.00', '15.00', '50', '200', null, '1');
INSERT INTO `product` VALUES ('7', 'P007', '打印机', '7', '台', '800.00', '1200.00', '10', '50', null, '1');
INSERT INTO `product` VALUES ('8', 'P008', '毛巾', '8', '条', '5.00', '12.00', '100', '500', null, '1');
INSERT INTO `product` VALUES ('9', 'P009', '洗发水', '8', '瓶', '20.00', '35.00', '50', '200', null, '1');
INSERT INTO `product` VALUES ('10', 'P010', '洗衣液', '9', '瓶', '15.00', '28.00', '50', '200', null, '1');

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `id` int NOT NULL,
  `parent_id` int DEFAULT NULL,
  `category_name` varchar(50) DEFAULT NULL,
  `category_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES ('1', '0', '电子产品', 'ELEC');
INSERT INTO `product_category` VALUES ('2', '0', '办公用品', 'OFFICE');
INSERT INTO `product_category` VALUES ('3', '0', '生活用品', 'LIFE');
INSERT INTO `product_category` VALUES ('4', '1', '手机配件', 'PHONE');
INSERT INTO `product_category` VALUES ('5', '1', '电脑配件', 'PC');
INSERT INTO `product_category` VALUES ('6', '2', '办公文具', 'STATIONERY');
INSERT INTO `product_category` VALUES ('7', '2', '办公设备', 'EQUIPMENT');
INSERT INTO `product_category` VALUES ('8', '3', '日用品', 'DAILY');
INSERT INTO `product_category` VALUES ('9', '3', '清洁用品', 'CLEANING');

-- ----------------------------
-- Table structure for stock_in
-- ----------------------------
DROP TABLE IF EXISTS `stock_in`;
CREATE TABLE `stock_in` (
  `id` int NOT NULL,
  `in_no` varchar(50) DEFAULT NULL,
  `in_type` varchar(20) DEFAULT NULL,
  `supplier_id` int DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  `warehouse_id` int DEFAULT NULL,
  `total_amount` decimal(12,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `operator` int DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of stock_in
-- ----------------------------
INSERT INTO `stock_in` VALUES ('1', 'IN20240615001', 'purchase', '1', null, '1', '1500.00', 'approved', '2', '常规采购', '2026-06-15 15:25:42');
INSERT INTO `stock_in` VALUES ('2', 'IN20240616002', 'purchase', '2', null, '1', '500.00', 'approved', '2', '办公用品补货', '2026-06-15 15:25:42');
INSERT INTO `stock_in` VALUES ('3', 'IN20240617003', 'return', null, '3', '2', '350.00', 'approved', '2', '客户退货', '2026-06-15 15:25:42');
INSERT INTO `stock_in` VALUES ('4', 'IN20240618004', 'purchase', '4', null, '1', '800.00', 'pending', '2', '待审核', '2026-06-15 15:25:42');

-- ----------------------------
-- Table structure for stock_in_detail
-- ----------------------------
DROP TABLE IF EXISTS `stock_in_detail`;
CREATE TABLE `stock_in_detail` (
  `id` int NOT NULL,
  `stock_in_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `location_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of stock_in_detail
-- ----------------------------
INSERT INTO `stock_in_detail` VALUES ('1', '1', '1', '1', '100', '5.00', '500.00');
INSERT INTO `stock_in_detail` VALUES ('2', '1', '2', '1', '20', '25.00', '500.00');
INSERT INTO `stock_in_detail` VALUES ('3', '1', '3', '2', '5', '80.00', '400.00');
INSERT INTO `stock_in_detail` VALUES ('4', '2', '4', '3', '20', '15.00', '300.00');
INSERT INTO `stock_in_detail` VALUES ('5', '2', '5', '3', '100', '1.50', '150.00');
INSERT INTO `stock_in_detail` VALUES ('6', '2', '6', '4', '30', '8.00', '240.00');
INSERT INTO `stock_in_detail` VALUES ('7', '3', '9', '6', '10', '20.00', '200.00');
INSERT INTO `stock_in_detail` VALUES ('8', '3', '10', '7', '5', '15.00', '75.00');
INSERT INTO `stock_in_detail` VALUES ('9', '4', '7', '5', '1', '800.00', '800.00');

-- ----------------------------
-- Table structure for stock_out
-- ----------------------------
DROP TABLE IF EXISTS `stock_out`;
CREATE TABLE `stock_out` (
  `id` int NOT NULL,
  `out_no` varchar(50) DEFAULT NULL,
  `out_type` varchar(20) DEFAULT NULL,
  `customer_id` int DEFAULT NULL,
  `supplier_id` int DEFAULT NULL,
  `warehouse_id` int DEFAULT NULL,
  `total_amount` decimal(12,2) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `operator` int DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of stock_out
-- ----------------------------
INSERT INTO `stock_out` VALUES ('1', 'OUT20240615001', 'sale', '1', null, '1', '800.00', 'approved', '2', '销售出库', '2026-06-15 15:25:42');
INSERT INTO `stock_out` VALUES ('2', 'OUT20240616002', 'sale', '2', null, '2', '500.00', 'approved', '2', '销售出库', '2026-06-15 15:25:42');
INSERT INTO `stock_out` VALUES ('3', 'OUT20240617003', 'return', null, '1', '1', '200.00', 'approved', '2', '退货出库', '2026-06-15 15:25:42');
INSERT INTO `stock_out` VALUES ('4', 'OUT20240618004', 'sale', '4', null, '1', '1200.00', 'pending', '2', '待审核', '2026-06-15 15:25:42');

-- ----------------------------
-- Table structure for stock_out_detail
-- ----------------------------
DROP TABLE IF EXISTS `stock_out_detail`;
CREATE TABLE `stock_out_detail` (
  `id` int NOT NULL,
  `stock_out_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `location_id` int DEFAULT NULL,
  `quantity` varchar(255) DEFAULT NULL,
  `unit_price` decimal(10,2) DEFAULT NULL,
  `amount` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of stock_out_detail
-- ----------------------------
INSERT INTO `stock_out_detail` VALUES ('1', '1', '3', '2', '5', '150.00', '750.00');
INSERT INTO `stock_out_detail` VALUES ('2', '1', '5', '3', '20', '3.00', '60.00');
INSERT INTO `stock_out_detail` VALUES ('3', '2', '8', '5', '20', '12.00', '240.00');
INSERT INTO `stock_out_detail` VALUES ('4', '2', '9', '6', '10', '35.00', '350.00');
INSERT INTO `stock_out_detail` VALUES ('5', '3', '2', '1', '5', '45.00', '225.00');
INSERT INTO `stock_out_detail` VALUES ('6', '4', '7', '5', '1', '1200.00', '1200.00');

-- ----------------------------
-- Table structure for storage_location
-- ----------------------------
DROP TABLE IF EXISTS `storage_location`;
CREATE TABLE `storage_location` (
  `id` int NOT NULL,
  `warehouse_id` int DEFAULT NULL,
  `location_code` varchar(50) DEFAULT NULL,
  `location_name` varchar(100) DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of storage_location
-- ----------------------------
INSERT INTO `storage_location` VALUES ('1', '1', 'A01', 'A区01号货架', '1');
INSERT INTO `storage_location` VALUES ('2', '1', 'A02', 'A区02号货架', '1');
INSERT INTO `storage_location` VALUES ('3', '1', 'B01', 'B区01号货架', '1');
INSERT INTO `storage_location` VALUES ('4', '1', 'B02', 'B区02号货架', '1');
INSERT INTO `storage_location` VALUES ('5', '2', 'C01', 'C区01号货架', '1');
INSERT INTO `storage_location` VALUES ('6', '2', 'C02', 'C区02号货架', '1');
INSERT INTO `storage_location` VALUES ('7', '3', 'D01', 'D区01号货架', '1');
INSERT INTO `storage_location` VALUES ('8', '3', 'D02', 'D区02号货架', '1');

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` int NOT NULL,
  `supplier_code` varchar(50) DEFAULT NULL,
  `supplier_name` varchar(100) DEFAULT NULL,
  `contact` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of supplier
-- ----------------------------
INSERT INTO `supplier` VALUES ('1', 'S001', '深圳电子科技有限公司', '赵经理', '13800138001', '深圳市南山区科技园', '1');
INSERT INTO `supplier` VALUES ('2', 'S002', '上海办公用品批发中心', '钱经理', '13800138002', '上海市闵行区', '1');
INSERT INTO `supplier` VALUES ('3', 'S003', '广州日用品贸易公司', '孙经理', '13800138003', '广州市白云区', '1');
INSERT INTO `supplier` VALUES ('4', 'S004', '北京数码配件供应商', '李经理', '13800138004', '北京市海淀区', '1');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int NOT NULL,
  `partne_id` int DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `component` varchar(255) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `order_num` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '系统管理', 'system', '', 'setting', '1');
INSERT INTO `sys_menu` VALUES ('2', '1', '用户管理', 'user', 'system/user/index', 'user', '1');
INSERT INTO `sys_menu` VALUES ('3', '1', '角色管理', 'role', 'system/role/index', 'team', '2');
INSERT INTO `sys_menu` VALUES ('4', '0', '商品管理', 'product', '', 'goods', '2');
INSERT INTO `sys_menu` VALUES ('5', '4', '商品列表', 'product/list', 'product/list/index', 'list', '1');
INSERT INTO `sys_menu` VALUES ('6', '4', '商品分类', 'product/category', 'product/category/index', 'folder', '2');
INSERT INTO `sys_menu` VALUES ('7', '0', '仓库管理', 'warehouse', '', 'warehouse', '3');
INSERT INTO `sys_menu` VALUES ('8', '7', '仓库列表', 'warehouse/list', 'warehouse/list/index', 'building', '1');
INSERT INTO `sys_menu` VALUES ('9', '7', '库位管理', 'warehouse/location', 'warehouse/location/index', 'map', '2');
INSERT INTO `sys_menu` VALUES ('10', '0', '出入库管理', 'stock', '', 'inbox', '4');
INSERT INTO `sys_menu` VALUES ('11', '10', '入库管理', 'stock/in', 'stock/in/index', 'download', '1');
INSERT INTO `sys_menu` VALUES ('12', '10', '出库管理', 'stock/out', 'stock/out/index', 'upload', '2');
INSERT INTO `sys_menu` VALUES ('13', '0', '库存管理', 'inventory', '', 'package', '5');
INSERT INTO `sys_menu` VALUES ('14', '13', '库存查询', 'inventory/list', 'inventory/list/index', 'search', '1');
INSERT INTO `sys_menu` VALUES ('15', '13', '库存预警', 'inventory/warning', 'inventory/warning/index', 'alert', '2');
INSERT INTO `sys_menu` VALUES ('16', '0', '统计报表', 'report', '', 'bar-chart', '6');
INSERT INTO `sys_menu` VALUES ('17', '16', '入库统计', 'report/in', 'report/in/index', 'trending-up', '1');
INSERT INTO `sys_menu` VALUES ('18', '16', '出库统计', 'report/out', 'report/out/index', 'trending-down', '2');
INSERT INTO `sys_menu` VALUES ('19', '16', '库存统计', 'report/inventory', 'report/inventory/index', 'pie-chart', '3');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL,
  `role_key` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `status` int DEFAULT '1',
  `create_time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '管理员', 'admin', '系统管理员，拥有所有权限', '1', '2026-06-11');
INSERT INTO `sys_role` VALUES ('2', '仓管员', 'warehouse', '仓库管理员，负责出入库操作', '1', '2026-06-14');
INSERT INTO `sys_role` VALUES ('3', '财务', 'finance', '财务人员，负责统计报表', '1', '2026-06-11');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  `dept_id` int DEFAULT NULL,
  `status` int DEFAULT NULL,
  `cretetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '123456', '系统管理员', '13888888888', 'admin@yuxin.com', '1', null, '1', '2026-06-15 15:25:42');
INSERT INTO `sys_user` VALUES ('2', 'warehouse', '123456', '仓库操作员', '13888888889', 'warehouse@yuxin.com', '2', null, '1', '2026-06-15 15:25:42');
INSERT INTO `sys_user` VALUES ('3', 'finance', '123456', '财务人员', '13888888890', 'finance@yuxin.com', '3', null, '1', '2026-06-15 15:25:42');
INSERT INTO `sys_user` VALUES ('4', 'user01', '123456', '张三', '13888888891', 'user01@yuxin.com', '2', null, '1', '2026-06-15 15:25:42');

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `id` int NOT NULL,
  `warehouse_code` varchar(50) DEFAULT NULL,
  `warehouse_name` varchar(100) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `manager` varchar(50) DEFAULT NULL,
  `status` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES ('1', 'WH001', '主仓库', '北京市朝阳区科技园A座', '张三', '1');
INSERT INTO `warehouse` VALUES ('2', 'WH002', '副仓库', '上海市浦东新区工业园', '李四', '1');
INSERT INTO `warehouse` VALUES ('3', 'WH003', '临时仓库', '广州市天河区物流中心', '王五', '1');
