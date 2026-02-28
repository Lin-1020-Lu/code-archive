package com.coldchain.user.enums;

/**
 * 用户状态枚举
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public enum UserStatus {
    
    /**
     * 禁用
     */
    DISABLED(0, "禁用"),
    
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    
    /**
     * 锁定
     * 多次登录失败后被锁定
     */
    LOCKED(2, "锁定"),
    
    /**
     * 未激活
     * 新注册用户未激活
     */
    UNACTIVATED(3, "未激活");
    
    private final int code;
    private final String description;
    
    UserStatus(int code, String description) {
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
    public static UserStatus fromCode(int code) {
        for (UserStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 检查用户是否允许登录
     * 
     * @return true 如果允许登录，false 否则
     */
    public boolean isLoginable() {
        return this == NORMAL;
    }
}
