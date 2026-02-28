package com.coldchain.user.config;

import com.coldchain.user.entity.TenantConfig;
import com.coldchain.user.enums.IsolationType;
import com.coldchain.user.service.TenantConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多租户动态数据源路由
 *
 * 功能：
 * 1. 支持三种数据隔离策略：
 *    - DATABASE: 独立数据库
 *    - SCHEMA: 独立 Schema
 *    - DISCRIMINATOR: 共享库表（通过 tenant_id 区分）
 * 2. 根据当前租户 ID 动态选择数据源
 * 3. 支持租户配置缓存
 *
 * 设计要点：
 * 1. 继承 AbstractRoutingDataSource 实现动态路由
 * 2. 使用 ThreadLocal 存储当前租户 ID
 * 3. 数据源缓存提高性能
 * 4. 支持运行时添加新租户数据源
 *
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
public class TenantDataSourceRouter extends AbstractRoutingDataSource {

    /**
     * 默认数据源 Key
     */
    public static final String DEFAULT_DATASOURCE_KEY = "default";

    /**
     * 租户数据源映射（缓存）
     */
    private final Map<String, DataSource> tenantDataSources = new ConcurrentHashMap<>();

    /**
     * 租户配置缓存
     */
    private final Map<String, TenantConfig> tenantConfigs = new ConcurrentHashMap<>();

    /**
     * 默认数据源引用
     */
    private DataSource defaultDataSource;

    @Autowired(required = false)
    @Lazy
    private TenantConfigService tenantConfigService;

    /**
     * 默认构造函数（无参）
     */
    public TenantDataSourceRouter() {
        // 无参构造函数，数据源在 setDefaultDataSource 方法中设置
    }

    /**
     * 设置默认数据源
     *
     * @param defaultDataSource 默认数据源
     */
    public void setDefaultDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DEFAULT_DATASOURCE_KEY, defaultDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }
    
    /**
     * 确定当前使用的数据源 Key
     * 
     * @return 数据源 Key
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getTenantId();
        
        if (tenantId == null) {
            log.debug("未获取到租户 ID，使用默认数据源");
            return DEFAULT_DATASOURCE_KEY;
        }
        
        // 获取租户配置
        TenantConfig config = tenantConfigs.get(tenantId);
        if (config == null && tenantConfigService != null) {
            config = tenantConfigService.getTenantConfig(tenantId);
            tenantConfigs.put(tenantId, config);
        }
        
        if (config == null) {
            log.debug("租户配置不存在，使用默认数据源: tenantId={}", tenantId);
            return DEFAULT_DATASOURCE_KEY;
        }
        
        // 根据隔离类型返回数据源 Key
        switch (config.getIsolationTypeEnum()) {
            case DATABASE:
                // 独立数据库
                String dbKey = "db_" + tenantId;
                if (tenantDataSources.containsKey(dbKey)) {
                    return dbKey;
                }
                log.warn("租户独立数据库不存在，使用默认数据源: tenantId={}", tenantId);
                return DEFAULT_DATASOURCE_KEY;

            case SCHEMA:
                // 独立 Schema（在 SQL 中动态切换）
                return DEFAULT_DATASOURCE_KEY;

            case DISCRIMINATOR:
            default:
                // 共享库表，使用默认数据源
                return DEFAULT_DATASOURCE_KEY;
        }
    }
    
    /**
     * 添加租户数据源
     *
     * @param tenantId 租户 ID
     * @param dataSource 数据源
     */
    public void addTenantDataSource(String tenantId, DataSource dataSource) {
        String key = "db_" + tenantId;
        tenantDataSources.put(key, dataSource);

        // 更新目标数据源映射
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DEFAULT_DATASOURCE_KEY, defaultDataSource);
        targetDataSources.putAll(super.getResolvedDataSources());
        targetDataSources.put(key, dataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();

        log.info("添加租户数据源: tenantId={}", tenantId);
    }
    
    /**
     * 移除租户数据源
     *
     * @param tenantId 租户 ID
     */
    public void removeTenantDataSource(String tenantId) {
        String key = "db_" + tenantId;
        DataSource removed = tenantDataSources.remove(key);

        if (removed != null) {
            // 更新目标数据源映射
            Map<Object, Object> targetDataSources = new HashMap<>();
            targetDataSources.put(DEFAULT_DATASOURCE_KEY, defaultDataSource);
            targetDataSources.putAll(super.getResolvedDataSources());
            targetDataSources.remove(key);
            super.setTargetDataSources(targetDataSources);
            super.afterPropertiesSet();

            log.info("移除租户数据源: tenantId={}", tenantId);
        }
    }
    
    /**
     * 清除租户配置缓存
     * 
     * @param tenantId 租户 ID，如果为 null 则清除所有
     */
    public void clearTenantConfigCache(String tenantId) {
        if (tenantId == null) {
            tenantConfigs.clear();
            log.info("清除所有租户配置缓存");
        } else {
            tenantConfigs.remove(tenantId);
            log.info("清除租户配置缓存: tenantId={}", tenantId);
        }
    }
    
    /**
     * 获取租户数据源数量
     * 
     * @return 数据源数量
     */
    public int getTenantDataSourceCount() {
        return tenantDataSources.size();
    }
}
