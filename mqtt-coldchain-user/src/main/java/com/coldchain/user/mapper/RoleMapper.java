package com.coldchain.user.mapper;

import com.coldchain.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper {
    
    /**
     * 根据主键 ID 查询角色（自动过滤租户）
     * 
     * @param id 主键 ID
     * @param tenantId 租户 ID
     * @return 角色对象，如果不存在则返回 null
     */
    Role selectByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
    
    /**
     * 根据角色代码查询角色（自动过滤租户）
     * 
     * @param roleCode 角色代码
     * @param tenantId 租户 ID
     * @return 角色对象，如果不存在则返回 null
     */
    Role selectByCodeAndTenantId(@Param("roleCode") String roleCode, @Param("tenantId") String tenantId);
    
    /**
     * 查询租户的角色列表
     * 
     * @param tenantId 租户 ID
     * @param status 状态（可选）
     * @return 角色列表
     */
    List<Role> selectListByTenantId(@Param("tenantId") String tenantId, @Param("status") Integer status);
    
    /**
     * 插入角色
     * 
     * @param role 角色对象
     * @return 影响行数
     */
    int insert(Role role);
    
    /**
     * 更新角色
     * 
     * @param role 角色对象
     * @return 影响行数
     */
    int updateById(Role role);
    
    /**
     * 删除角色（自动过滤租户）
     * 
     * @param id 主键 ID
     * @param tenantId 租户 ID
     * @return 影响行数
     */
    int deleteByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
}
