package com.coldchain.user.config;

/**
 * 租户上下文管理器
 * 
 * 功能：使用 ThreadLocal 存储当前请求的租户 ID 和用户 ID
 * 实现：所有 Service 层通过 TenantContext.getTenantId() 获取当前租户
 * 
 * 设计要点：
 * 1. 使用 ThreadLocal 保证线程安全
 * 2. 请求结束时必须调用 clear() 清除上下文，防止内存泄漏
 * 3. 配合 TenantFilter 使用，自动管理上下文生命周期
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public class TenantContext {
    
    /**
     * 租户 ID 线程本地存储
     * 存储当前请求所属的租户 ID（如 corp001, corp002）
     */
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    
    /**
     * 用户 ID 线程本地存储
     * 存储当前登录用户的 ID
     */
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    
    /**
     * 用户名线程本地存储
     * 存储当前登录用户的用户名（用于审计日志）
     */
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    
    /**
     * 设置租户 ID
     * 
     * @param tenantId 租户 ID，如 "corp001"
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }
    
    /**
     * 获取租户 ID
     * 
     * @return 当前租户 ID，如果未设置则返回 null
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }
    
    /**
     * 设置用户 ID
     * 
     * @param userId 用户 ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }
    
    /**
     * 获取用户 ID
     * 
     * @return 当前用户 ID，如果未设置则返回 null
     */
    public static Long getUserId() {
        return USER_ID.get();
    }
    
    /**
     * 设置用户名
     * 
     * @param username 用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }
    
    /**
     * 获取用户名
     * 
     * @return 当前用户名，如果未设置则返回 null
     */
    public static String getUsername() {
        return USERNAME.get();
    }
    
    /**
     * 清除上下文
     * 
     * 必须在请求结束时调用，防止 ThreadLocal 内存泄漏
     * 通常在 Filter 的 finally 块中调用
     */
    public static void clear() {
        TENANT_ID.remove();
        USER_ID.remove();
        USERNAME.remove();
    }
    
    /**
     * 检查是否已设置租户 ID
     * 
     * @return true 如果租户 ID 已设置，false 否则
     */
    public static boolean hasTenantId() {
        return TENANT_ID.get() != null;
    }
    
    /**
     * 获取所有上下文信息（用于调试）
     * 
     * @return 上下文信息字符串
     */
    public static String getContextInfo() {
        return String.format("TenantContext{tenantId='%s', userId=%s, username='%s'}",
            getTenantId(), getUserId(), getUsername());
    }
}
