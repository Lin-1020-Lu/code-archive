package com.coldchain.user.service;

import com.coldchain.user.dto.TenantRegisterDTO;
import com.coldchain.user.entity.Tenant;
import com.coldchain.user.vo.TenantInfoVO;

import java.util.List;

/**
 * 租户服务接口
 * 
 * 功能：
 * - 租户注册/注销
 * - 租户信息查询
 * - 租户状态管理
 * - 租户配置管理
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public interface TenantService {
    
    /**
     * 租户注册
     * 
     * @param dto 租户注册 DTO
     * @return 租户信息 VO
     */
    TenantInfoVO register(TenantRegisterDTO dto);
    
    /**
     * 查询当前租户信息
     * 
     * @return 租户信息 VO
     */
    TenantInfoVO getCurrentTenant();
    
    /**
     * 根据租户 ID 查询租户信息
     * 
     * @param tenantId 租户 ID
     * @return 租户信息 VO
     */
    TenantInfoVO getTenantByTenantId(String tenantId);
    
    /**
     * 查询所有租户列表
     * 
     * @param status 状态（可选）
     * @param tenantType 租户类型（可选）
     * @return 租户列表
     */
    List<TenantInfoVO> list(Integer status, Integer tenantType);
    
    /**
     * 更新租户状态
     * 
     * @param id 主键 ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 检查租户是否存在
     * 
     * @param tenantId 租户 ID
     * @return 存在返回 true，否则返回 false
     */
    boolean exists(String tenantId);
    
    /**
     * 生成唯一租户 ID
     * 
     * @return 租户 ID
     */
    String generateTenantId();
}
