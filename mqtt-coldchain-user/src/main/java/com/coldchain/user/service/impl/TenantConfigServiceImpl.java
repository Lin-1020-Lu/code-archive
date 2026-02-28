package com.coldchain.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coldchain.user.entity.Tenant;
import com.coldchain.user.entity.TenantConfig;
import com.coldchain.user.mapper.TenantMapper;
import com.coldchain.user.service.TenantConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 租户配置服务实现
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class TenantConfigServiceImpl implements TenantConfigService {
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Override
    public TenantConfig getTenantConfig(String tenantId) {
        Tenant tenant = tenantMapper.selectByTenantId(tenantId);
        if (tenant == null) {
            throw new RuntimeException("租户不存在");
        }
        
        TenantConfig config = new TenantConfig();
        config.setTenantId(tenantId);
        config.setIsolationType(tenant.getIsolationType());
        config.setSchemaName(tenant.getSchemaName());
        config.setDatabaseName(tenant.getDatabaseName());
        config.setMaxConnections(50);
        config.setMaxApiQps(100);
        config.setMaxUsers(tenant.getMaxUsers());
        config.setMaxDevices(100);
        config.setMaxVehicles(10);
        config.setMaxAlertRules(20);
        config.setDataRetentionDays(30L);

        // 解析功能开关
        String features = buildDefaultFeatures(tenant.getTenantType());
        config.setFeatures(features);
        Map<String, Boolean> featuresMap = parseFeaturesMap(features);
        config.setFeaturesMap(featuresMap);

        // 解析定价配置
        String pricing = buildDefaultPricing(tenant.getTenantType());
        config.setPricing(pricing);
        config.setPricingMap(JSON.parseObject(pricing, JSONObject.class).getInnerMap());
        
        return config;
    }
    
    @Override
    public void updateTenantConfig(String tenantId, TenantConfig config) {
        // 实现配置更新逻辑
        log.info("更新租户配置: tenantId={}", tenantId);
    }
    
    /**
     * 解析功能开关 Map
     */
    private Map<String, Boolean> parseFeaturesMap(String features) {
        JSONObject jsonObject = JSON.parseObject(features, JSONObject.class);
        Map<String, Boolean> result = new HashMap<>();
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            result.put(key, value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString()));
        }
        return result;
    }

    /**
     * 构建默认功能开关配置
     */
    private String buildDefaultFeatures(Integer tenantType) {
        JSONObject features = new JSONObject();
        features.put("deviceManagement", true);
        features.put("alertManagement", true);
        features.put("dataExport", true);
        features.put("apiAccess", true);
        
        // 不同类型租户的功能差异
        switch (tenantType) {
            case 1: // 试用版
                features.put("reportAnalysis", false);
                features.put("advancedFeatures", false);
                break;
            case 2: // 标准版
                features.put("reportAnalysis", true);
                features.put("advancedFeatures", false);
                break;
            case 3: // 企业版
                features.put("reportAnalysis", true);
                features.put("advancedFeatures", true);
                break;
            default:
                features.put("reportAnalysis", false);
                features.put("advancedFeatures", false);
        }
        
        return features.toJSONString();
    }
    
    /**
     * 构建默认定价配置
     */
    private String buildDefaultPricing(Integer tenantType) {
        JSONObject pricing = new JSONObject();
        pricing.put("billingCycle", "monthly");
        pricing.put("currency", "CNY");
        pricing.put("freeTrialDays", 30);
        
        // 不同类型租户的定价
        switch (tenantType) {
            case 1: // 试用版
                pricing.put("price", 0);
                pricing.put("plan", "trial");
                break;
            case 2: // 标准版
                pricing.put("price", 999);
                pricing.put("plan", "standard");
                break;
            case 3: // 企业版
                pricing.put("price", 2999);
                pricing.put("plan", "enterprise");
                break;
            default:
                pricing.put("price", 0);
                pricing.put("plan", "trial");
        }
        
        return pricing.toJSONString();
    }
}
