package com.coldchain.user.service;

import com.coldchain.user.entity.TenantConfig;

/**
 * 租户配置服务接口
 * 
 * 功能：
 * - 获取租户配置
 * - 更新租户配置
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public interface TenantConfigService {
    
    /**
     * 获取租户配置
     * 
     * @param tenantId 租户 ID
     * @return 租户配置
     */
    TenantConfig getTenantConfig(String tenantId);
    
    /**
     * 更新租户配置
     * 
     * @param tenantId 租户 ID
     * @param config 租户配置
     */
    void updateTenantConfig(String tenantId, TenantConfig config);
}
