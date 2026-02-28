-- 多租户用户管理服务数据库初始化脚本
-- 版本: 1.0.0
-- 作者: ColdChain Team

-- ============================================
-- 1. 租户表
-- ============================================
CREATE TABLE IF NOT EXISTS tenant (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tenant_id VARCHAR(64) UNIQUE NOT NULL COMMENT '租户ID（业务标识）',
  tenant_name VARCHAR(128) NOT NULL COMMENT '租户名称',
  tenant_type TINYINT DEFAULT 1 COMMENT '租户类型: 1试用 2标准 3企业',
  status TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常 2过期 3审核中 4已删除',
  expire_date DATETIME COMMENT '过期时间',
  max_users INT DEFAULT 10 COMMENT '最大用户数',
  current_users INT DEFAULT 0 COMMENT '当前用户数',
  contact_name VARCHAR(64) COMMENT '联系人',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  contact_email VARCHAR(128) COMMENT '联系邮箱',
  isolation_type TINYINT DEFAULT 3 COMMENT '数据隔离类型: 1独立数据库 2独立Schema 3共享库表',
  database_name VARCHAR(64) COMMENT '独立数据库名称',
  schema_name VARCHAR(64) COMMENT '独立Schema名称',
  max_api_qps INT DEFAULT 100 COMMENT '最大API QPS',
  data_retention_days INT DEFAULT 30 COMMENT '数据保留天数',
  extra_config JSON COMMENT '扩展配置',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_status (status),
  INDEX idx_tenant_type (tenant_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ============================================
-- 2. 用户表（多租户隔离）
-- ============================================
CREATE TABLE IF NOT EXISTS user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
  username VARCHAR(64) NOT NULL COMMENT '用户名',
  password VARCHAR(256) NOT NULL COMMENT '密码（BCrypt加密）',
  real_name VARCHAR(64) COMMENT '真实姓名',
  email VARCHAR(128) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  role_id BIGINT COMMENT '角色ID',
  role_code VARCHAR(64) COMMENT '角色代码',
  role_name VARCHAR(64) COMMENT '角色名称',
  status TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常 2锁定 3未激活',
  last_login_time DATETIME COMMENT '最后登录时间',
  last_login_ip VARCHAR(64) COMMENT '最后登录IP',
  login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
  lock_time DATETIME COMMENT '锁定时间',
  avatar VARCHAR(512) COMMENT '头像URL',
  remark VARCHAR(500) COMMENT '备注',
  created_by BIGINT COMMENT '创建者ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_by BIGINT COMMENT '更新者ID',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_tenant_username (tenant_id, username),
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_username (username),
  INDEX idx_status (status),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 3. 角色表（多租户隔离）
-- ============================================
CREATE TABLE IF NOT EXISTS role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  role_code VARCHAR(64) NOT NULL COMMENT '角色代码',
  permissions JSON COMMENT '权限列表',
  description VARCHAR(256) COMMENT '描述',
  is_built_in TINYINT DEFAULT 0 COMMENT '是否预置角色: 0否 1是',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  created_by BIGINT COMMENT '创建者ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_by BIGINT COMMENT '更新者ID',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_tenant_role (tenant_id, role_code),
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ============================================
-- 4. 权限表
-- ============================================
CREATE TABLE IF NOT EXISTS permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tenant_id VARCHAR(64) COMMENT '租户ID（null表示全局权限）',
  permission_code VARCHAR(128) UNIQUE NOT NULL COMMENT '权限代码',
  permission_name VARCHAR(64) NOT NULL COMMENT '权限名称',
  permission_type TINYINT DEFAULT 2 COMMENT '权限类型: 1菜单 2按钮 3接口',
  parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
  level TINYINT DEFAULT 1 COMMENT '层级: 1模块 2功能 3操作',
  path VARCHAR(256) COMMENT '路径',
  component VARCHAR(256) COMMENT '组件',
  icon VARCHAR(64) COMMENT '图标',
  sort_order INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态: 0禁用 1正常',
  remark VARCHAR(500) COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_parent_id (parent_id),
  INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ============================================
-- 5. 登录日志表
-- ============================================
CREATE TABLE IF NOT EXISTS login_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
  user_id BIGINT COMMENT '用户ID',
  username VARCHAR(64) COMMENT '用户名',
  real_name VARCHAR(64) COMMENT '真实姓名',
  status TINYINT NOT NULL COMMENT '登录状态: 0失败 1成功',
  failure_reason VARCHAR(256) COMMENT '失败原因',
  ip VARCHAR(64) COMMENT 'IP地址',
  location VARCHAR(128) COMMENT '地理位置',
  user_agent VARCHAR(512) COMMENT 'User-Agent',
  browser VARCHAR(64) COMMENT '浏览器',
  os VARCHAR(64) COMMENT '操作系统',
  login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_user_id (user_id),
  INDEX idx_login_time (login_time),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================
-- 6. 审计日志表
-- ============================================
CREATE TABLE IF NOT EXISTS audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
  user_id BIGINT COMMENT '用户ID',
  username VARCHAR(64) COMMENT '用户名',
  real_name VARCHAR(64) COMMENT '真实姓名',
  module VARCHAR(64) COMMENT '操作模块',
  operation VARCHAR(64) COMMENT '操作类型',
  description VARCHAR(256) COMMENT '操作描述',
  method VARCHAR(8) COMMENT '请求方法',
  request_path VARCHAR(512) COMMENT '请求路径',
  request_params TEXT COMMENT '请求参数',
  response TEXT COMMENT '响应结果',
  status VARCHAR(16) NOT NULL COMMENT '操作状态: SUCCESS FAILURE',
  error_message TEXT COMMENT '错误信息',
  ip VARCHAR(64) COMMENT 'IP地址',
  location VARCHAR(128) COMMENT '地理位置',
  user_agent VARCHAR(512) COMMENT 'User-Agent',
  browser VARCHAR(64) COMMENT '浏览器',
  os VARCHAR(64) COMMENT '操作系统',
  duration BIGINT COMMENT '请求耗时（毫秒）',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_user_id (user_id),
  INDEX idx_created_at (created_at),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化默认租户
INSERT INTO tenant (tenant_id, tenant_name, tenant_type, status, expire_date, max_users, current_users, contact_name, contact_phone, contact_email, isolation_type)
VALUES 
('corp001', 'XX冷链物流', 2, 1, DATE_ADD(NOW(), INTERVAL 1 YEAR), 100, 0, '张三', '13800138000', 'zhangsan@example.com', 3)
ON DUPLICATE KEY UPDATE tenant_name=tenant_name;

-- 初始化默认角色
INSERT INTO role (tenant_id, role_name, role_code, permissions, is_built_in) VALUES
('corp001', '租户管理员', 'admin', '["*:*"]', 1),
('corp001', '普通用户', 'user', '["device:view", "temperature:view", "alert:view"]', 1)
ON DUPLICATE KEY UPDATE role_name=role_name;

-- 初始化默认管理员（密码: admin123）
-- BCrypt 加密后的 admin123
INSERT INTO user (tenant_id, username, password, real_name, role_id, role_code, role_name, status) VALUES
('corp001', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 
 (SELECT id FROM role WHERE tenant_id='corp001' AND role_code='admin' LIMIT 1),
 'admin', '租户管理员', 1)
ON DUPLICATE KEY UPDATE password=password;

-- 初始化全局权限（可选）
INSERT INTO permission (tenant_id, permission_code, permission_name, permission_type, parent_id, level, sort_order) VALUES
-- 用户管理模块
(NULL, 'user', '用户管理', 1, 0, 1, 1),
(NULL, 'user:create', '创建用户', 2, (SELECT id FROM permission WHERE permission_code='user'), 2, 1),
(NULL, 'user:view', '查看用户', 2, (SELECT id FROM permission WHERE permission_code='user'), 2, 2),
(NULL, 'user:update', '更新用户', 2, (SELECT id FROM permission WHERE permission_code='user'), 2, 3),
(NULL, 'user:delete', '删除用户', 2, (SELECT id FROM permission WHERE permission_code='user'), 2, 4),

-- 设备管理模块
(NULL, 'device', '设备管理', 1, 0, 1, 2),
(NULL, 'device:create', '创建设备', 2, (SELECT id FROM permission WHERE permission_code='device'), 2, 1),
(NULL, 'device:view', '查看设备', 2, (SELECT id FROM permission WHERE permission_code='device'), 2, 2),
(NULL, 'device:update', '更新设备', 2, (SELECT id FROM permission WHERE permission_code='device'), 2, 3),
(NULL, 'device:delete', '删除设备', 2, (SELECT id FROM permission WHERE permission_code='device'), 2, 4),

-- 告警管理模块
(NULL, 'alert', '告警管理', 1, 0, 1, 3),
(NULL, 'alert:view', '查看告警', 2, (SELECT id FROM permission WHERE permission_code='alert'), 2, 1),
(NULL, 'alert:manage', '管理告警', 2, (SELECT id FROM permission WHERE permission_code='alert'), 2, 2),
(NULL, 'alert:confirm', '确认告警', 2, (SELECT id FROM permission WHERE permission_code='alert'), 2, 3),

-- 温度数据模块
(NULL, 'temperature', '温度数据', 1, 0, 1, 4),
(NULL, 'temperature:view', '查看温度数据', 2, (SELECT id FROM permission WHERE permission_code='temperature'), 2, 1),
(NULL, 'temperature:export', '导出数据', 2, (SELECT id FROM permission WHERE permission_code='temperature'), 2, 2),

-- 租户管理模块（超级管理员）
(NULL, 'tenant', '租户管理', 1, 0, 1, 5),
(NULL, 'tenant:create', '创建租户', 2, (SELECT id FROM permission WHERE permission_code='tenant'), 2, 1),
(NULL, 'tenant:view', '查看租户', 2, (SELECT id FROM permission WHERE permission_code='tenant'), 2, 2),
(NULL, 'tenant:update', '更新租户', 2, (SELECT id FROM permission WHERE permission_code='tenant'), 2, 3),
(NULL, 'tenant:delete', '删除租户', 2, (SELECT id FROM permission WHERE permission_code='tenant'), 2, 4)
ON DUPLICATE KEY UPDATE permission_name=permission_name;
