# 冷链运输系统

基于 MQTT + Kafka + Spring Cloud 微服务架构的冷链运输监控系统。

## 技术栈

| 层级 | 技术选型 | 版本 |
|------|----------|------|
| Java | JDK | 17 |
| 微服务框架 | Spring Boot + Spring Cloud Alibaba | 2.7.18 / 2021.0.5.0 |
| 服务注册/配置 | Nacos | 2.2.3 |
| API 网关 | Spring Cloud Gateway | - |
| MQTT Broker | EMQX | latest |
| 消息队列 | Kafka | 3.7.0 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| ORM | MyBatis | 2.3.0 |
| 反向代理 | Nginx | alpine |

## 项目结构

```
mqtt-coldchain-parent/              # 父项目
├── mqtt-coldchain-common/          # 公共模块
│   └── common-core/                # 公共基础类（结果、枚举、常量、模型）
├── mqtt-coldchain-gateway/         # API 网关服务 (8080)
├── mqtt-coldchain-device/          # 设备管理服务 (8081)
├── mqtt-coldchain-mqtt-gateway/    # MQTT 网关服务 (8082)
├── mqtt-coldchain-alert/           # 告警服务 (8083)
├── docker/                         # Docker 部署配置
└── sql/                            # 数据库初始化脚本
```

## 服务说明

### mqtt-coldchain-gateway (API 网关)
- 统一入口，路由转发
- 跨域处理
- 负载均衡

### mqtt-coldchain-device (设备服务)
- 设备管理（CRUD）
- 车辆管理
- 温度数据存储
- 消费 Kafka 设备数据

### mqtt-coldchain-mqtt-gateway (MQTT 网关)
- 订阅 MQTT Topic（EMQX）
- 解析设备上报数据
- 发送数据到 Kafka

### mqtt-coldchain-alert (告警服务)
- 告警规则管理
- 告警规则引擎
- 告警记录管理

## 快速开始

### 1. 环境要求
- JDK 17
- Maven 3.6+
- Docker & Docker Compose

### 2. 修改配置

将所有 `application.yml` 中的 `111.231.53.57` 替换为实际服务器 IP：
```bash
mqtt-coldchain-gateway/src/main/resources/application.yml
mqtt-coldchain-device/src/main/resources/application.yml
mqtt-coldchain-mqtt-gateway/src/main/resources/application.yml
mqtt-coldchain-alert/src/main/resources/application.yml
docker/docker-compose.yml
```

### 3. 启动基础组件

```bash
cd docker
docker-compose up -d
```

启动的服务包括：
- MySQL (3306)
- Redis (6379)
- Nacos (8848)
- Kafka (9092)
- EMQX (1883)
- Kafka UI (8085)

### 4. 初始化数据库

数据库会自动初始化，包含以下表：
- device（设备表）
- vehicle（车辆表）
- alert_rule（告警规则表）
- alert_record（告警记录表）
- temperature_data（温度数据表）

初始测试数据：
- 车辆：V001、V002
- 设备：sensor001、sensor002
- 告警规则：冷冻车低温告警、冷藏车高温告警

### 5. 编译项目

```bash
mvn clean install
```

### 6. 启动微服务

```bash
# 按顺序启动
java -jar mqtt-coldchain-gateway/target/mqtt-coldchain-gateway-1.0.0.jar
java -jar mqtt-coldchain-mqtt-gateway/target/mqtt-coldchain-mqtt-gateway-1.0.0.jar
java -jar mqtt-coldchain-device/target/mqtt-coldchain-device-1.0.0.jar
java -jar mqtt-coldchain-alert/target/mqtt-coldchain-alert-1.0.0.jar
```

## 访问地址

| 服务 | 地址 | 账号/密码 |
|------|------|-----------|
| API Gateway | http://你的IP:80 | - |
| Nacos | http://你的IP:8848/nacos | nacos/nacos |
| EMQX Dashboard | http://你的IP:18083 | admin/public |
| Kafka UI | http://你的IP:8085 | - |

## MQTT Topic 规范

```
格式: coldchain/{corpId}/{vehicleId}/{dataType}

示例:
coldchain/corp001/V001/temperature   # 温度数据
coldchain/corp001/V001/humidity      # 湿度数据
coldchain/corp001/V001/location      # GPS定位数据
coldchain/corp001/V001/status        # 设备状态
```

## 消息 Payload 示例

```json
{
  "deviceId": "sensor001",
  "vehicleId": "1",
  "corpId": "corp001",
  "type": "temperature",
  "value": -18.5,
  "unit": "℃",
  "timestamp": 1708924800000,
  "location": {
    "lat": 39.9042,
    "lng": 116.4074
  }
}
```

## Kafka Topic

| Topic | 说明 |
|-------|------|
| coldchain.device.data | 设备数据上报 |
| coldchain.alert.event | 告警事件 |

## 端口分配

| 服务 | 端口 |
|------|------|
| API Gateway | 8080 |
| device-service | 8081 |
| mqtt-gateway | 8082 |
| alert-service | 8083 |
| MySQL | 3306 |
| Redis | 6379 |
| Nacos | 8848 |
| Kafka | 9092 |
| EMQX | 1883 |
| Nginx | 80 |

## 停止服务

```bash
# 停止 Docker 服务
cd docker
docker-compose down

# 停止微服务（Ctrl+C 或 kill 进程）
```

## 开发计划

- [ ] 完善 API 接口
- [ ] 增加设备状态实时监控
- [ ] 实现告警规则的时间窗口判断
- [ ] 添加告警通知功能
- [ ] 扩展轨迹追踪服务
- [ ] 添加运单管理

## License

MIT

