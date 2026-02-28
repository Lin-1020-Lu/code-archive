# 多租户用户管理微服务

## 概述

这是一个**生产级**的多租户用户管理微服务，可作为独立组件复用于其他项目中。

## 核心特性

### 1. 多租户支持

- **数据隔离**: 支持 3 种数据隔离策略
  - `DATABASE`: 独立数据库（最高隔离）
  - `SCHEMA`: 独立 Schema（中等隔离）
  - `DISCRIMINATOR`: 共享库表（最低隔离）
- **租户识别**: 支持多种租户识别方式
  - JWT Token 中携带
  - Header: `X-Tenant-Id`
  - 子域名: `corp001.coldchain.com`
  - URL 路径: `/api/tenant/corp001/users`

### 2. 认证授权

- **JWT 认证**: 使用 HS256 算法
- **双 Token**: 访问 Token（2小时）+ 刷新 Token（7天）
- **密码加密**: BCrypt 算法
- **登录保护**: 
  - 登录失败 5 次自动锁定
  - 锁定时间 30 分钟

### 3. 用户管理

- **CRUD 操作**: 用户增删改查
- **状态管理**: 禁用/正常/锁定/未激活
- **密码管理**: 修改密码/重置密码
- **角色分配**: 支持多角色

### 4. 租户管理

- **租户注册**: 自动生成租户 ID
- **租户类型**: 试用版/标准版/企业版
- **资源限制**: 用户数/设备数/车辆数限制
- **过期管理**: 支持订阅过期时间

### 5. 审计日志

- **登录日志**: 记录登录成功/失败
- **审计日志**: 记录所有关键操作
- **IP 地理位置**: 记录访问来源

## 技术栈

- **Spring Boot**: 2.7.18
- **Spring Cloud**: 2021.0.8
- **Spring Security**: 认证授权
- **MyBatis**: 数据库访问
- **JWT**: Token 生成与验证
- **MySQL**: 数据存储
- **Redis**: 缓存（可选）

## 快速开始

### 1. 数据库初始化

```bash
# 执行数据库脚本
mysql -u root -p coldchain < sql/user_init.sql
```

### 2. 配置文件修改

修改 `application.yml` 中的数据库和 Redis 配置。

### 3. 启动服务

```bash
mvn clean install -DskipTests
java -jar mqtt-coldchain-user/target/mqtt-coldchain-user-1.0.0.jar
```

### 4. 默认账户

```
租户 ID: corp001
用户名: admin
密码: admin123
```

## API 接口

### 认证接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/refresh` | POST | 刷新 Token |
| `/api/auth/logout` | POST | 用户登出 |

### 用户管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/users` | GET | 查询用户列表 |
| `/api/users/{id}` | GET | 查询用户详情 |
| `/api/users/register` | POST | 创建用户 |
| `/api/users/{id}` | PUT | 更新用户 |
| `/api/users/{id}` | DELETE | 删除用户 |
| `/api/users/{id}/status` | PUT | 更新用户状态 |
| `/api/users/{id}/password` | PUT | 修改密码 |

### 租户管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/tenants/register` | POST | 租户注册 |
| `/api/tenants/current` | GET | 查询当前租户 |
| `/api/tenants/{tenantId}` | GET | 查询租户信息 |
| `/api/tenants` | GET | 查询所有租户 |

## 使用示例

### 登录示例

```bash
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "tenantId": "corp001"
  }'
```

### 创建用户示例

```bash
curl -X POST http://localhost:8084/api/users/register \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: corp001" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "username": "zhangsan",
    "password": "12345678",
    "confirmPassword": "12345678",
    "realName": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "roleId": 2
  }'
```

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                     多租户用户管理服务                        │
├─────────────────────────────────────────────────────────────┤
│  ┌───────────────────────────────────────────────────────┐  │
│  │           租户管理层 (Tenant Management)             │  │
│  │  - 租户注册/注销                                       │  │
│  │  - 租户状态管理                                       │  │
│  │  - 租户配置管理                                       │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↓                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           认证授权层 (Authentication & Authorization) │  │
│  │  - JWT 认证                                           │  │
│  │  - RBAC 权限控制                                      │  │
│  │  - 租户隔离鉴权                                       │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↓                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           业务逻辑层 (Business Logic)                 │  │
│  │  - 用户管理                                           │  │
│  │  - 角色管理                                           │  │
│  │  - 租户管理                                           │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ↓                                  │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           审计日志层 (Audit Log)                      │  │
│  │  - 操作日志                                           │  │
│  │  - 登录日志                                           │  │
│  │  - 数据变更日志                                       │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 数据库表结构

| 表名 | 说明 |
|------|------|
| `tenant` | 租户表 |
| `user` | 用户表 |
| `role` | 角色表 |
| `permission` | 权限表 |
| `login_log` | 登录日志表 |
| `audit_log` | 审计日志表 |

## 许可证

MIT License
