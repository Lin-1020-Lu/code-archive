-- 修改 temperature_data 表的 vehicle_id 字段类型
ALTER TABLE temperature_data MODIFY COLUMN vehicle_id VARCHAR(64) NOT NULL COMMENT '车辆ID';

-- 查看修改后的表结构
DESCRIBE temperature_data;