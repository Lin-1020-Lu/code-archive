package com.coldchain.user.mapper;

import com.coldchain.user.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户 Mapper
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Mapper
public interface TenantMapper {
    
    /**
     * 根据租户 ID 查询租户
     * 
     * @param tenantId 租户 ID
     * @return 租户对象，如果不存在则返回 null
     */
    Tenant selectByTenantId(@Param("tenantId") String tenantId);
    
    /**
     * 根据主键 ID 查询租户
     * 
     * @param id 主键 ID
     * @return 租户对象，如果不存在则返回 null
     */
    Tenant selectById(@Param("id") Long id);
    
    /**
     * 插入租户
     * 
     * @param tenant 租户对象
     * @return 影响行数
     */
    int insert(Tenant tenant);
    
    /**
     * 更新租户
     * 
     * @param tenant 租户对象
     * @return 影响行数
     */
    int updateById(Tenant tenant);
    
    /**
     * 更新租户状态
     * 
     * @param id 主键 ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 更新用户数量
     * 
     * @param tenantId 租户 ID
     * @param currentUsers 当前用户数
     * @return 影响行数
     */
    int updateCurrentUsers(@Param("tenantId") String tenantId, @Param("currentUsers") Integer currentUsers);
    
    /**
     * 查询所有租户列表
     * 
     * @param status 状态（可选）
     * @param tenantType 租户类型（可选）
     * @return 租户列表
     */
    List<Tenant> selectList(@Param("status") Integer status, @Param("tenantType") Integer tenantType);
    
    /**
     * 检查租户 ID 是否存在
     * 
     * @param tenantId 租户 ID
     * @return 存在返回 true，否则返回 false
     */
    boolean existsByTenantId(@Param("tenantId") String tenantId);
}
