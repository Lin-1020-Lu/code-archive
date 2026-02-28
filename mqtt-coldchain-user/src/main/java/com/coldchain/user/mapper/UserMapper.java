package com.coldchain.user.mapper;

import com.coldchain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 * 
 * 功能：提供用户数据的数据库访问
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户 ID 查询用户（自动过滤租户）
     * 
     * @param id 用户 ID
     * @param tenantId 租户 ID
     * @return 用户对象，如果不存在则返回 null
     */
    User selectByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
    
    /**
     * 根据用户名查询用户（自动过滤租户）
     * 
     * @param username 用户名
     * @param tenantId 租户 ID
     * @return 用户对象，如果不存在则返回 null
     */
    User selectByUsernameAndTenantId(@Param("username") String username, @Param("tenantId") String tenantId);
    
    /**
     * 插入用户
     * 
     * @param user 用户对象
     * @return 影响行数
     */
    int insert(User user);
    
    /**
     * 更新用户
     * 
     * @param user 用户对象
     * @return 影响行数
     */
    int updateById(User user);
    
    /**
     * 更新用户（自动过滤租户）
     * 
     * @param user 用户对象
     * @param tenantId 租户 ID
     * @return 影响行数
     */
    int updateByIdAndTenantId(@Param("user") User user, @Param("tenantId") String tenantId);
    
    /**
     * 删除用户（软删除）
     * 
     * @param id 用户 ID
     * @param tenantId 租户 ID
     * @return 影响行数
     */
    int deleteByIdAndTenantId(@Param("id") Long id, @Param("tenantId") String tenantId);
    
    /**
     * 查询用户列表（自动过滤租户）
     * 
     * @param tenantId 租户 ID
     * @param username 用户名（模糊查询，可选）
     * @param status 状态（可选）
     * @param roleId 角色 ID（可选）
     * @return 用户列表
     */
    List<User> selectList(@Param("tenantId") String tenantId,
                         @Param("username") String username,
                         @Param("status") Integer status,
                         @Param("roleId") Long roleId);
    
    /**
     * 统计用户数量（自动过滤租户）
     * 
     * @param tenantId 租户 ID
     * @return 用户数量
     */
    int countByTenantId(@Param("tenantId") String tenantId);
    
    /**
     * 更新最后登录信息
     * 
     * @param userId 用户 ID
     * @param tenantId 租户 ID
     * @param ip IP 地址
     * @return 影响行数
     */
    int updateLastLoginInfo(@Param("userId") Long userId,
                           @Param("tenantId") String tenantId,
                           @Param("ip") String ip);
    
    /**
     * 更新登录失败次数
     * 
     * @param userId 用户 ID
     * @param tenantId 租户 ID
     * @param loginFailCount 登录失败次数
     * @param lockTime 锁定时间
     * @return 影响行数
     */
    int updateLoginFailCount(@Param("userId") Long userId,
                            @Param("tenantId") String tenantId,
                            @Param("loginFailCount") Integer loginFailCount,
                            @Param("lockTime") String lockTime);
    
    /**
     * 重置登录失败次数
     * 
     * @param userId 用户 ID
     * @param tenantId 租户 ID
     * @return 影响行数
     */
    int resetLoginFailCount(@Param("userId") Long userId, @Param("tenantId") String tenantId);
}
