package com.coldchain.user.aspect;

import com.coldchain.user.config.TenantContext;
import com.coldchain.user.enums.IsolationType;
import com.coldchain.user.service.TenantConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;

/**
 * 多租户 Schema 切换拦截器
 * 
 * 功能：当租户使用 SCHEMA 隔离策略时，在执行 SQL 前自动切换到租户的 Schema
 * 
 * 工作原理：
 * 1. 拦截 MyBatis 执行器，在 SQL 执行前获取当前租户配置
 * 2. 如果租户使用 SCHEMA 隔离策略，执行 SET SCHEMA 语句
 * 3. SQL 执行完成后恢复默认 Schema（可选）
 * 
 * 设计要点：
 * 1. 使用 MyBatis Interceptor 机制实现 SQL 拦截
 * 2. 只在 isolation_type = 2 (SCHEMA) 时生效
 * 3. 缓存租户 Schema 配置，避免每次查询数据库
 * 4. 优雅处理异常，确保不影响正常业务流程
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Component
@Intercepts({
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
    ),
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
    )
})
public class TenantSchemaInterceptor implements Interceptor {
    
    /**
     * 租户配置服务
     * 使用 @Lazy 延迟注入，避免循环依赖
     */
    @Autowired
    @Lazy
    private TenantConfigService tenantConfigService;
    
    /**
     * 是否启用 Schema 拦截
     */
    private boolean enabled = true;
    
    /**
     * 是否在每次查询前切换 Schema
     * true: 每次查询前都执行 SET SCHEMA（更安全，性能稍低）
     * false: 缓存当前 Schema，只有租户变化时才切换（性能更好）
     */
    private boolean switchAlways = false;
    
    /**
     * 当前租户 Schema 缓存
     */
    private String currentSchema = null;
    
    /**
     * 当前租户 ID 缓存
     */
    private String currentTenantId = null;
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!enabled) {
            return invocation.proceed();
        }
        
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            // 没有租户 ID，跳过
            return invocation.proceed();
        }
        
        try {
            // 获取数据库连接
            Executor executor = (Executor) invocation.getTarget();
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Connection connection = mappedStatement.getConfiguration()
                .getEnvironment()
                .getDataSource()
                .getConnection();
            
            // 检查是否需要切换 Schema
            if (shouldSwitchSchema(tenantId)) {
                String schemaName = getTenantSchemaName(tenantId);
                if (schemaName != null) {
                    // 切换 Schema
                    switchSchema(connection, schemaName, tenantId);
                    currentSchema = schemaName;
                    currentTenantId = tenantId;
                }
            }
            
            return invocation.proceed();
            
        } catch (Exception e) {
            log.error("Schema 切换失败: tenantId={}, error={}", tenantId, e.getMessage());
            // 即使切换失败，也继续执行 SQL（使用默认 Schema）
            return invocation.proceed();
        }
    }
    
    /**
     * 判断是否需要切换 Schema
     * 
     * @param tenantId 租户 ID
     * @return true 如果需要切换，false 否则
     */
    private boolean shouldSwitchSchema(String tenantId) {
        // 检查租户是否使用 SCHEMA 隔离策略
        IsolationType isolationType = getTenantIsolationType(tenantId);
        if (isolationType != IsolationType.SCHEMA) {
            return false;
        }
        
        // 如果是每次都切换模式，返回 true
        if (switchAlways) {
            return true;
        }
        
        // 如果租户发生了变化，需要切换
        return !tenantId.equals(currentTenantId);
    }
    
    /**
     * 获取租户的 Schema 名称
     * 
     * @param tenantId 租户 ID
     * @return Schema 名称，如果获取失败则返回 null
     */
    private String getTenantSchemaName(String tenantId) {
        try {
            var config = tenantConfigService.getTenantConfig(tenantId);
            if (config != null && config.getSchemaName() != null) {
                return config.getSchemaName();
            }
        } catch (Exception e) {
            log.error("获取租户 Schema 配置失败: tenantId={}, error={}", tenantId, e.getMessage());
        }
        return null;
    }
    
    /**
     * 获取租户的数据隔离类型
     * 
     * @param tenantId 租户 ID
     * @return 数据隔离类型
     */
    private IsolationType getTenantIsolationType(String tenantId) {
        try {
            var config = tenantConfigService.getTenantConfig(tenantId);
            if (config != null) {
                return config.getIsolationTypeEnum();
            }
        } catch (Exception e) {
            log.error("获取租户隔离类型失败: tenantId={}, error={}", tenantId, e.getMessage());
        }
        return IsolationType.DISCRIMINATOR; // 默认值
    }
    
    /**
     * 切换到指定的 Schema
     * 
     * @param connection 数据库连接
     * @param schemaName Schema 名称
     * @param tenantId 租户 ID
     */
    private void switchSchema(Connection connection, String schemaName, String tenantId) {
        try {
            // MySQL 使用 SET SCHEMA 或 USE 语句
            // PostgreSQL 使用 SET search_path TO 语句
            // 这里使用通用的 SQL 语句
            
            // MySQL/PostgreSQL 兼容写法
            String switchSql = "SET SCHEMA '" + schemaName + "'";
            
            try (var stmt = connection.createStatement()) {
                stmt.execute(switchSql);
                log.debug("切换到租户 Schema: tenantId={}, schema={}", tenantId, schemaName);
            }
            
        } catch (Exception e) {
            log.error("执行 Schema 切换 SQL 失败: tenantId={}, schema={}, error={}", 
                tenantId, schemaName, e.getMessage());
            throw new RuntimeException("Schema 切换失败", e);
        }
    }
    
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        if (properties != null) {
            String enabled = properties.getProperty("enabled");
            if (enabled != null) {
                this.enabled = Boolean.parseBoolean(enabled);
            }
            
            String switchAlways = properties.getProperty("switchAlways");
            if (switchAlways != null) {
                this.switchAlways = Boolean.parseBoolean(switchAlways);
            }
        }
    }
    
    /**
     * 清除当前 Schema 缓存
     * 在租户切换或租户配置更新时调用
     */
    public void clearCurrentSchema() {
        this.currentSchema = null;
        this.currentTenantId = null;
        log.debug("清除当前 Schema 缓存");
    }
}
