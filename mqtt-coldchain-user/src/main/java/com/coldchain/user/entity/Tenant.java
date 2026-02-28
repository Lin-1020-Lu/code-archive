package com.coldchain.user.entity;

import com.coldchain.user.enums.IsolationType;
import com.coldchain.user.enums.TenantStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户实体
 * 
 * 功能：存储租户的基本信息和配置
 * 
 * 设计要点：
 * 1. tenant_id 是业务唯一标识，用于多租户隔离
 * 2. 支持多种租户类型（试用/标准/企业）
 * 3. 支持资源限制（最大用户数/设备数等）
 * 4. 支持过期时间管理
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class Tenant implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private Long id;
    
    /**
     * 租户 ID（业务标识，如 corp001）
     * 
     * 约束：
     * - 全局唯一
     * - 用于多租户数据隔离
     * - 一旦创建不可修改
     * - 格式建议：{类型}{序号}，如 corp001, corp002
     */
    private String tenantId;
    
    /**
     * 租户名称
     */
    private String tenantName;
    
    /**
     * 租户类型
     * 1=试用版, 2=标准版, 3=企业版
     */
    private Integer tenantType;
    
    /**
     * 租户状态
     * 0=禁用, 1=正常, 2=过期, 3=审核中, 4=已删除
     */
    private Integer status;
    
    /**
     * 过期时间
     * 试用版默认 30 天，标准版/企业版根据订阅时间
     */
    private LocalDateTime expireDate;
    
    /**
     * 最大用户数
     * 不同版本有不同的限制
     */
    private Integer maxUsers;
    
    /**
     * 当前用户数
     */
    private Integer currentUsers;
    
    /**
     * 联系人
     */
    private String contactName;
    
    /**
     * 联系电话
     */
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    private String contactEmail;
    
    /**
     * 数据隔离类型
     * 1=独立数据库, 2=独立Schema, 3=共享库表
     */
    private Integer isolationType;
    
    /**
     * 独立数据库名称（当 isolationType=1 时使用）
     */
    private String databaseName;
    
    /**
     * 独立 Schema 名称（当 isolationType=2 时使用）
     */
    private String schemaName;
    
    /**
     * 最大 API QPS
     */
    private Integer maxApiQps;
    
    /**
     * 数据保留天数
     */
    private Integer dataRetentionDays;
    
    /**
     * 扩展配置（JSON 格式）
     * 用于存储租户级别的自定义配置
     */
    private String extraConfig;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 获取租户状态枚举
     * 
     * @return 租户状态枚举
     */
    public TenantStatus getStatusEnum() {
        return TenantStatus.fromCode(this.status);
    }
    
    /**
     * 检查租户是否过期
     * 
     * @return true 如果已过期，false 否则
     */
    public boolean isExpired() {
        return expireDate != null && LocalDateTime.now().isAfter(expireDate);
    }
    
    /**
     * 检查租户是否允许访问
     * 
     * @return true 如果允许访问，false 否则
     */
    public boolean isAccessible() {
        TenantStatus status = getStatusEnum();
        return status != null && status.isAccessible() && !isExpired();
    }
    
    /**
     * 检查用户数是否已达到上限
     * 
     * @return true 如果已达到上限，false 否则
     */
    public boolean isUserLimitReached() {
        return maxUsers != null && currentUsers != null && currentUsers >= maxUsers;
    }
}
