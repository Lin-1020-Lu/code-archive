package com.coldchain.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体
 * 
 * 功能：记录用户登录信息，用于安全审计和异常检测
 * 
 * 设计要点：
 * 1. 记录登录成功和失败
 * 2. 记录登录时间和 IP
     * 3. 记录设备信息
     * 4. 支持租户级别的登录日志
     * 
     * @author ColdChain Team
     * @since 1.0.0
     */
@Data
public class LoginLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private Long id;
    
    /**
     * 租户 ID
     */
    private String tenantId;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 登录状态
     * 0=失败, 1=成功
     */
    private Integer status;
    
    /**
     * 失败原因
     * 登录失败时记录原因
     */
    private String failureReason;
    
    /**
     * IP 地址
     */
    private String ip;
    
    /**
     * 地理位置
     */
    private String location;
    
    /**
     * User-Agent
     */
    private String userAgent;
    
    /**
     * 浏览器
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    
    /**
     * 检查登录是否成功
     * 
     * @return true 如果登录成功，false 否则
     */
    public boolean isSuccess() {
        return Integer.valueOf(1).equals(this.status);
    }
    
    /**
     * 检查登录是否失败
     * 
     * @return true 如果登录失败，false 否则
     */
    public boolean isFailure() {
        return Integer.valueOf(0).equals(this.status);
    }
}
