package com.coldchain.user.enums;

/**
 * 数据隔离类型枚举
 * 
 * 多租户数据隔离策略：
 * 1. DATABASE: 独立数据库（最高隔离级别）
 *    - 每个租户有独立的数据库
 *    - 适合对数据安全要求极高的企业客户
 *    - 迁移方便，备份独立
 *    - 缺点：资源消耗大
 * 
 * 2. SCHEMA: 独立 Schema（中等隔离级别）
 *    - 租户共享数据库，但使用独立的 Schema
 *    - 适合中型客户
 *    - 资源消耗适中
 *    - 缺点：Schema 级别隔离不够彻底
 * 
 * 3. DISCRIMINATOR: 共享库表（最低隔离级别）
 *    - 所有租户共享数据库和表，通过 tenant_id 区分
 *    - 适合小型客户或 SaaS 试用版
 *    - 资源消耗最小
 *    - 缺点：数据隔离依赖应用层，安全性相对较低
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public enum IsolationType {
    
    /**
     * 独立数据库
     */
    DATABASE("独立数据库", "每个租户使用独立的数据库实例"),
    
    /**
     * 独立 Schema
     */
    SCHEMA("独立Schema", "租户共享数据库，但使用独立的 Schema"),
    
    /**
     * 共享库表（区分列）
     */
    DISCRIMINATOR("共享库表", "所有租户共享数据库和表，通过 tenant_id 区分");
    
    /**
     * 类型描述
     */
    private final String description;
    
    /**
     * 详细说明
     */
    private final String detail;
    
    IsolationType(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getDetail() {
        return detail;
    }
    
    /**
     * 根据名称获取枚举
     * 
     * @param name 名称
     * @return 枚举值，如果不存在则返回 null
     */
    public static IsolationType fromName(String name) {
        for (IsolationType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取枚举（兼容数据库存储的数字）
     * 
     * @param code 代码（1=DATABASE, 2=SCHEMA, 3=DISCRIMINATOR）
     * @return 枚举值
     */
    public static IsolationType fromCode(int code) {
        switch (code) {
            case 1:
                return DATABASE;
            case 2:
                return SCHEMA;
            case 3:
                return DISCRIMINATOR;
            default:
                return DISCRIMINATOR; // 默认值
        }
    }
    
    /**
     * 获取代码（用于数据库存储）
     * 
     * @return 代码
     */
    public int getCode() {
        return ordinal() + 1;
    }
}
