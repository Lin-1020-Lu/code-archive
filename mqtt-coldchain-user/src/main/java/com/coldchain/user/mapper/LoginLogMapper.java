package com.coldchain.user.mapper;

import com.coldchain.user.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志 Mapper
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Mapper
public interface LoginLogMapper {
    
    /**
     * 插入登录日志
     * 
     * @param loginLog 登录日志对象
     * @return 影响行数
     */
    int insert(LoginLog loginLog);
    
    /**
     * 查询登录日志列表（自动过滤租户）
     * 
     * @param tenantId 租户 ID
     * @param userId 用户 ID（可选）
     * @param status 登录状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @param offset 偏移量（可选）
     * @return 登录日志列表
     */
    List<LoginLog> selectList(@Param("tenantId") String tenantId,
                              @Param("userId") Long userId,
                              @Param("status") Integer status,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("limit") Integer limit,
                              @Param("offset") Integer offset);
    
    /**
     * 统计登录失败次数
     * 
     * @param tenantId 租户 ID
     * @param userId 用户 ID
     * @param startTime 开始时间
     * @return 登录失败次数
     */
    int countLoginFail(@Param("tenantId") String tenantId,
                      @Param("userId") Long userId,
                      @Param("startTime") LocalDateTime startTime);
}
