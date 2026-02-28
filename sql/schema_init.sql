-- Schema 隔离初始化脚本
-- 用于为租户创建独立的 Schema
-- 
-- 使用方法：
-- 1. 先执行本脚本创建 Schema
-- 2. 更新租户表的 schema_name 字段
-- 3. 设置租户的 isolation_type = 2

-- ============================================
-- 为租户创建 Schema（示例：corp001）
-- ============================================

-- 1. 创建租户 Schema
CREATE SCHEMA IF NOT EXISTS corp001_schema DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 将表结构复制到租户 Schema
CREATE TABLE IF NOT EXISTS corp001_schema.user LIKE user;
CREATE TABLE IF NOT EXISTS corp001_schema.tenant LIKE tenant;
CREATE TABLE IF NOT EXISTS corp001_schema.role LIKE role;
CREATE TABLE IF NOT EXISTS corp001_schema.permission LIKE permission;
CREATE TABLE IF NOT EXISTS corp001_schema.login_log LIKE login_log;
CREATE TABLE IF NOT EXISTS corp001_schema.audit_log LIKE audit_log;

-- 3. 为租户 Schema 创建索引（继承主表的索引）
SHOW CREATE TABLE user;
-- 根据主表的索引，手动在租户 Schema 中创建索引

-- 4. 更新租户配置：使用 Schema 隔离
UPDATE tenant
SET isolation_type = 2,
    schema_name = 'corp001_schema'
WHERE tenant_id = 'corp001';

-- 5. 验证 Schema 创建成功
SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'corp001_schema';

-- 6. 测试 Schema 切换（手动测试）
SET SCHEMA corp001_schema;
SELECT * FROM user LIMIT 1;

-- ============================================
-- 批量创建租户 Schema（存储过程）
-- ============================================

DELIMITER $$

DROP PROCEDURE IF EXISTS create_tenant_schema$$

CREATE PROCEDURE create_tenant_schema(
    IN p_tenant_id VARCHAR(64),
    IN p_schema_suffix VARCHAR(64)
)
BEGIN
    DECLARE v_schema_name VARCHAR(128);
    DECLARE v_table_name VARCHAR(64);
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_cursor CURSOR FOR 
        SELECT TABLE_NAME 
        FROM INFORMATION_SCHEMA.TABLES 
        WHERE TABLE_SCHEMA = 'coldchain' 
        AND TABLE_TYPE = 'BASE TABLE';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 构建 Schema 名称
    SET v_schema_name = CONCAT(p_schema_suffix, '_schema');
    
    -- 创建 Schema
    SET @sql = CONCAT('CREATE SCHEMA IF NOT EXISTS ', v_schema_name, 
                     ' DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    
    -- 复制表结构
    OPEN table_cursor;
    read_loop: LOOP
        FETCH table_cursor INTO v_table_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        SET @sql = CONCAT('CREATE TABLE IF NOT EXISTS ', v_schema_name, '.', v_table_name, 
                        ' LIKE coldchain.', v_table_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE table_cursor;
    
    -- 更新租户配置
    UPDATE tenant
    SET isolation_type = 2,
        schema_name = v_schema_name
    WHERE tenant_id = p_tenant_id;
    
    -- 返回结果
    SELECT CONCAT('租户 Schema 创建成功: ', v_schema_name) AS result;
END$$

DELIMITER ;

-- ============================================
-- 使用存储过程创建租户 Schema
-- ============================================

-- 示例：为租户 corp002 创建 Schema
CALL create_tenant_schema('corp002', 'corp002');

-- ============================================
-- 删除租户 Schema（慎用）
-- ============================================

DROP PROCEDURE IF EXISTS drop_tenant_schema$$

CREATE PROCEDURE drop_tenant_schema(
    IN p_schema_name VARCHAR(128)
)
BEGIN
    -- 删除 Schema（会删除 Schema 下的所有表）
    SET @sql = CONCAT('DROP SCHEMA IF EXISTS ', p_schema_name);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    
    -- 更新租户配置：恢复为共享库表
    UPDATE tenant
    SET isolation_type = 3,
        schema_name = NULL
    WHERE schema_name = p_schema_name;
    
    SELECT CONCAT('租户 Schema 已删除: ', p_schema_name) AS result;
END$$

DELIMITER ;

-- ============================================
-- 查看所有租户 Schema
-- ============================================

SELECT 
    t.tenant_id,
    t.tenant_name,
    t.isolation_type,
    t.schema_name,
    CASE t.isolation_type
        WHEN 1 THEN '独立数据库'
        WHEN 2 THEN '独立Schema'
        WHEN 3 THEN '共享库表'
        ELSE '未知'
    END AS isolation_type_name
FROM tenant t
WHERE schema_name IS NOT NULL
ORDER BY t.id;

-- ============================================
-- 查看各 Schema 的表和数据量
-- ============================================

SELECT 
    TABLE_SCHEMA AS schema_name,
    TABLE_NAME,
    TABLE_ROWS AS row_count
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA LIKE '%_schema'
ORDER BY TABLE_SCHEMA, TABLE_NAME;

-- ============================================
-- 迁移数据：从共享库表迁移到 Schema
-- ============================================

DROP PROCEDURE IF EXISTS migrate_to_schema$$

CREATE PROCEDURE migrate_to_schema(
    IN p_tenant_id VARCHAR(64),
    IN p_schema_name VARCHAR(128)
)
BEGIN
    DECLARE v_table_name VARCHAR(64);
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_cursor CURSOR FOR 
        SELECT TABLE_NAME 
        FROM INFORMATION_SCHEMA.TABLES 
        WHERE TABLE_SCHEMA = 'coldchain' 
        AND TABLE_TYPE = 'BASE TABLE';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 清空 Schema 中的表
    OPEN table_cursor;
    read_loop: LOOP
        FETCH table_cursor INTO v_table_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        SET @sql = CONCAT('TRUNCATE TABLE ', p_schema_name, '.', v_table_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE table_cursor;
    
    -- 复制租户数据到 Schema
    SET @sql = CONCAT('INSERT INTO ', p_schema_name, '.user SELECT * FROM coldchain.user WHERE tenant_id = ''', p_tenant_id, '''');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
    
    -- 复制其他表数据（根据需要扩展）
    -- SET @sql = CONCAT('INSERT INTO ', p_schema_name, '.role SELECT * FROM coldchain.role WHERE tenant_id = ''', p_tenant_id, '''');
    -- PREPARE stmt FROM @sql;
    -- EXECUTE stmt;
    -- DEALLOCATE PREPARE stmt;
    
    SELECT CONCAT('数据迁移完成: tenantId=', p_tenant_id, ', schema=', p_schema_name) AS result;
END$$

DELIMITER ;

-- 使用示例：
-- 1. 先创建 Schema
-- CALL create_tenant_schema('corp001', 'corp001');
-- 2. 迁移数据
-- CALL migrate_to_schema('corp001', 'corp001_schema');
