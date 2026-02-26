-- 冷链运输系统数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS coldchain DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE coldchain;

-- ========== 设备表 ==========
CREATE TABLE IF NOT EXISTS `device` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `device_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '设备唯一标识',
  `device_name` VARCHAR(128) COMMENT '设备名称',
  `device_type` VARCHAR(32) NOT NULL COMMENT '设备类型: sensor温湿度计/gps定位器',
  `vehicle_id` BIGINT COMMENT '关联车辆ID',
  `corp_id` VARCHAR(64) NOT NULL COMMENT '公司ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_device_id (`device_id`),
  INDEX idx_vehicle_id (`vehicle_id`),
  INDEX idx_corp_id (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备表';

-- ========== 车辆表 ==========
CREATE TABLE IF NOT EXISTS `vehicle` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `vehicle_id` VARCHAR(64) NOT NULL UNIQUE COMMENT '车辆编号',
  `vehicle_name` VARCHAR(128) COMMENT '车牌号',
  `corp_id` VARCHAR(64) NOT NULL COMMENT '公司ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_vehicle_id (`vehicle_id`),
  INDEX idx_corp_id (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆表';

-- ========== 温度告警规则表 ==========
CREATE TABLE IF NOT EXISTS `alert_rule` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(128) NOT NULL COMMENT '规则名称',
  `corp_id` VARCHAR(64) NOT NULL COMMENT '公司ID',
  `vehicle_id` BIGINT COMMENT '车辆ID, null表示该公司的所有车辆',
  `metric_type` VARCHAR(32) NOT NULL COMMENT '指标类型: temperature/humidity',
  `threshold_min` DECIMAL(5,2) COMMENT '最小阈值',
  `threshold_max` DECIMAL(5,2) COMMENT '最大阈值',
  `duration_seconds` INT DEFAULT 0 COMMENT '持续多少秒才算告警',
  `enabled` TINYINT DEFAULT 1 COMMENT '是否启用: 0禁用 1启用',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_corp_id (`corp_id`),
  INDEX idx_vehicle_id (`vehicle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='告警规则表';

-- ========== 告警记录表 ==========
CREATE TABLE IF NOT EXISTS `alert_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `alert_rule_id` BIGINT NOT NULL COMMENT '告警规则ID',
  `vehicle_id` BIGINT NOT NULL COMMENT '车辆ID',
  `device_id` VARCHAR(64) COMMENT '设备ID',
  `metric_type` VARCHAR(32) NOT NULL COMMENT '指标类型',
  `alert_value` DECIMAL(6,2) NOT NULL COMMENT '告警值',
  `threshold_value` VARCHAR(64) COMMENT '阈值',
  `alert_level` TINYINT COMMENT '告警级别: 1警告 2严重 3紧急',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0待处理 1已确认 2已恢复',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `resolved_at` TIMESTAMP NULL COMMENT '恢复时间',
  INDEX idx_alert_rule_id (`alert_rule_id`),
  INDEX idx_vehicle_id (`vehicle_id`),
  INDEX idx_device_id (`device_id`),
  INDEX idx_status (`status`),
  INDEX idx_created_at (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='告警记录表';

-- ========== 温度数据存储表 ==========
CREATE TABLE IF NOT EXISTS `temperature_data` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `vehicle_id` BIGINT NOT NULL COMMENT '车辆ID',
  `device_id` VARCHAR(64) NOT NULL COMMENT '设备ID',
  `temperature` DECIMAL(5,2) NOT NULL COMMENT '温度值',
  `humidity` DECIMAL(5,2) COMMENT '湿度值',
  `location` JSON COMMENT '位置信息',
  `timestamp` BIGINT NOT NULL COMMENT '时间戳',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_vehicle_id (`vehicle_id`),
  INDEX idx_device_id (`device_id`),
  INDEX idx_timestamp (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='温度数据表';

-- ========== 初始化测试数据 ==========
INSERT INTO `vehicle` (`vehicle_id`, `vehicle_name`, `corp_id`, `status`) VALUES
('V001', '京A12345', 'corp001', 1),
('V002', '京B67890', 'corp001', 1);

INSERT INTO `device` (`device_id`, `device_name`, `device_type`, `vehicle_id`, `corp_id`, `status`) VALUES
('sensor001', '温度传感器001', 'sensor', 1, 'corp001', 1),
('sensor002', '温度传感器002', 'sensor', 2, 'corp001', 1);

INSERT INTO `alert_rule` (`rule_name`, `corp_id`, `vehicle_id`, `metric_type`, `threshold_min`, `threshold_max`, `duration_seconds`, `enabled`) VALUES
('冷冻车低温告警', 'corp001', NULL, 'temperature', -25.00, -15.00, 0, 1),
('冷藏车高温告警', 'corp001', NULL, 'temperature', 0.00, 5.00, 0, 1);
