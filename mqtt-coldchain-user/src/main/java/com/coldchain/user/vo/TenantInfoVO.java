package com.coldchain.user.vo;

import com.coldchain.user.enums.IsolationType;
import com.coldchain.user.enums.TenantStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户信息 VO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class TenantInfoVO {
    
    /**
     * 租户 ID
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
     * 租户类型名称
     */
    private String tenantTypeName;
    
    /**
     * 租户状态
     */
    private Integer status;
    
    /**
     * 租户状态名称
     */
    private String statusName;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireDate;
    
    /**
     * 是否已过期
     */
    private Boolean isExpired;
    
    /**
     * 最大用户数
     */
    private Integer maxUsers;
    
    /**
     * 当前用户数
     */
    private Integer currentUsers;
    
    /**
     * 用户使用率
     */
    private Double userUsageRate;
    
    /**
     * 数据隔离类型
     */
    private Integer isolationType;
    
    /**
     * 数据隔离类型名称
     */
    private String isolationTypeName;
    
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
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 获取租户类型名称
     */
    public String getTenantTypeName() {
        if (tenantType == null) {
            return "未知";
        }
        switch (tenantType) {
            case 1:
                return "试用版";
            case 2:
                return "标准版";
            case 3:
                return "企业版";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取租户状态名称
     */
    public String getStatusName() {
        if (status == null) {
            return "未知";
        }
        TenantStatus tenantStatus = TenantStatus.fromCode(status);
        return tenantStatus != null ? tenantStatus.getDescription() : "未知";
    }
    
    /**
     * 检查是否已过期
     */
    public Boolean getIsExpired() {
        return expireDate != null && LocalDateTime.now().isAfter(expireDate);
    }
    
    /**
     * 计算用户使用率
     */
    public Double getUserUsageRate() {
        if (maxUsers == null || maxUsers == 0) {
            return 0.0;
        }
        if (currentUsers == null) {
            return 0.0;
        }
        return (double) currentUsers / maxUsers * 100;
    }
    
    /**
     * 获取数据隔离类型名称
     */
    public String getIsolationTypeName() {
        if (isolationType == null) {
            return "未知";
        }
        IsolationType type = IsolationType.fromCode(isolationType);
        return type != null ? type.getDescription() : "未知";
    }
}
