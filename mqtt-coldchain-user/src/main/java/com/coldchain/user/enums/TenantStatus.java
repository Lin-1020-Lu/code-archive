package com.coldchain.user.enums;

/**
 * 租户状态枚举
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public enum TenantStatus {
    
    /**
     * 禁用
     * 租户被管理员禁用，无法访问系统
     */
    DISABLED(0, "禁用"),
    
    /**
     * 正常
     * 租户正常使用中
     */
    NORMAL(1, "正常"),
    
    /**
     * 过期
     * 租户订阅已过期，需要续费
     */
    EXPIRED(2, "过期"),
    
    /**
     * 审核中
     * 新注册租户等待管理员审核
     */
    PENDING(3, "审核中"),
    
    /**
     * 已删除
     * 租户已被删除（软删除）
     */
    DELETED(4, "已删除");
    
    private final int code;
    private final String description;
    
    TenantStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据代码获取枚举
     * 
     * @param code 状态代码
     * @return 枚举值，如果不存在则返回 null
     */
    public static TenantStatus fromCode(int code) {
        for (TenantStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 检查租户是否允许访问
     * 
     * @return true 如果允许访问，false 否则
     */
    public boolean isAccessible() {
        return this == NORMAL;
    }
}
