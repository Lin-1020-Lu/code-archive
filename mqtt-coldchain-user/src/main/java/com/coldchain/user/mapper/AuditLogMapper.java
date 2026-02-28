package com.coldchain.user.mapper;

import com.coldchain.user.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志 Mapper
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Mapper
public interface AuditLogMapper {
    
    /**
     * 插入审计日志
     * 
     * @param auditLog 审计日志对象
     * @return 影响行数
     */
    int insert(AuditLog auditLog);
    
    /**
     * 查询审计日志列表（自动过滤租户）
     * 
     * @param tenantId 租户 ID
     * @param userId 用户 ID（可选）
     * @param module 操作模块（可选）
     * @param operation 操作类型（可选）
     * @param status 操作状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param limit 限制数量（可选）
     * @param offset 偏移量（可选）
     * @return 审计日志列表
     */
    List<AuditLog> selectList(@Param("tenantId") String tenantId,
                              @Param("userId") Long userId,
                              @Param("module") String module,
                              @Param("operation") String operation,
                              @Param("status") String status,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime,
                              @Param("limit") Integer limit,
                              @Param("offset") Integer offset);
    
    /**
     * 统计审计日志数量
     * 
     * @param tenantId 租户 ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 审计日志数量
     */
    int count(@Param("tenantId") String tenantId,
              @Param("startTime") LocalDateTime startTime,
              @Param("endTime") LocalDateTime endTime);
}
