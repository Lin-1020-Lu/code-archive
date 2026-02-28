package com.coldchain.user.entity;

import com.coldchain.user.enums.IsolationType;
import com.coldchain.user.enums.TenantStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 租户配置实体
 * 
 * 功能：存储租户的详细配置信息，包括功能开关、资源限制、定价等
 * 
 * 设计要点：
 * 1. 与 tenant 表一对一关联
 * 2. 支持功能开关配置
 * 3. 支持资源限制配置
 * 4. 支持自定义扩展配置
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class TenantConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private Long id;
    
    /**
     * 租户 ID
     */
    private String tenantId;
    
    /**
     * 数据隔离类型
     * 1=独立数据库, 2=独立Schema, 3=共享库表
     */
    private Integer isolationType;
    
    /**
     * 数据隔离类型枚举（运行时使用）
     */
    private IsolationType isolationTypeEnum;
    
    /**
     * 独立数据库名称
     */
    private String databaseName;
    
    /**
     * 独立 Schema 名称
     */
    private String schemaName;
    
    /**
     * 数据库连接池最大连接数
     */
    private Integer maxConnections;
    
    /**
     * 最大 API QPS
     */
    private Integer maxApiQps;
    
    /**
     * 最大用户数
     */
    private Integer maxUsers;
    
    /**
     * 最大设备数
     */
    private Integer maxDevices;
    
    /**
     * 最大车辆数
     */
    private Integer maxVehicles;
    
    /**
     * 最大告警规则数
     */
    private Integer maxAlertRules;
    
    /**
     * 数据保留天数
     */
    private Long dataRetentionDays;
    
    /**
     * 功能开关配置（JSON 格式）
     * 
     * 示例：
     * {
     *   "deviceManagement": true,
     *   "alertManagement": true,
     *   "dataExport": true,
     *   "apiAccess": true,
     *   "reportAnalysis": false,
     *   "advancedFeatures": false
     * }
     */
    private String features;
    
    /**
     * 功能开关配置解析后的对象（运行时使用）
     */
    private Map<String, Boolean> featuresMap;
    
    /**
     * 定价配置（JSON 格式）
     * 
     * 示例：
     * {
     *   "billingCycle": "monthly",
     *   "price": 999.00,
     *   "currency": "CNY",
     *   "freeTrialDays": 30
     * }
     */
    private String pricing;
    
    /**
     * 定价配置解析后的对象（运行时使用）
     */
    private Map<String, Object> pricingMap;
    
    /**
     * 自定义配置（JSON 格式）
     * 用于存储租户特定的自定义配置
     */
    private String customConfig;
    
    /**
     * 自定义配置解析后的对象（运行时使用）
     */
    private Map<String, Object> customConfigMap;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 获取数据隔离类型枚举
     * 
     * @return 数据隔离类型枚举
     */
    public IsolationType getIsolationTypeEnum() {
        if (isolationTypeEnum == null && isolationType != null) {
            isolationTypeEnum = IsolationType.fromCode(isolationType);
        }
        return isolationTypeEnum;
    }
    
    /**
     * 检查功能是否启用
     * 
     * @param featureKey 功能键名
     * @return true 如果功能已启用，false 否则
     */
    public boolean isFeatureEnabled(String featureKey) {
        return featuresMap != null && featuresMap.containsKey(featureKey)
            && featuresMap.get(featureKey);
    }
    
    /**
     * 获取自定义配置值
     * 
     * @param key 配置键
     * @return 配置值，如果不存在则返回 null
     */
    public Object getCustomConfigValue(String key) {
        return customConfigMap != null ? customConfigMap.get(key) : null;
    }
    
    /**
     * 获取自定义配置值（带默认值）
     * 
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public Object getCustomConfigValue(String key, Object defaultValue) {
        Object value = getCustomConfigValue(key);
        return value != null ? value : defaultValue;
    }
}
