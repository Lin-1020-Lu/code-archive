package com.coldchain.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审计日志实体
 * 
 * 功能：记录系统中的关键操作，满足合规和安全审计要求
 * 
 * 设计要点：
 * 1. 记录操作者、操作时间、操作内容
 * 2. 记录 IP 地址和 User-Agent
 * 3. 记录操作结果和耗时
 * 4. 支持租户级别的审计日志
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class AuditLog implements Serializable {
    
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
     * 操作模块
     * 如：user, tenant, device, vehicle, alert 等
     */
    private String module;
    
    /**
     * 操作类型
     * 如：create, update, delete, view, login, logout 等
     */
    private String operation;
    
    /**
     * 操作描述
     */
    private String description;
    
    /**
     * 请求方法
     * 如：GET, POST, PUT, DELETE
     */
    private String method;
    
    /**
     * 请求路径
     * 如：/api/users/1
     */
    private String requestPath;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 响应结果
     */
    private String response;
    
    /**
     * 操作状态
     * SUCCESS, FAILURE
     */
    private String status;
    
    /**
     * 错误信息
     * 操作失败时记录异常信息
     */
    private String errorMessage;
    
    /**
     * IP 地址
     */
    private String ip;
    
    /**
     * 地理位置
     * 根据 IP 解析的地理位置
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
     * 请求耗时（毫秒）
     */
    private Long duration;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 检查操作是否成功
     * 
     * @return true 如果操作成功，false 否则
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(this.status);
    }
    
    /**
     * 检查操作是否失败
     * 
     * @return true 如果操作失败，false 否则
     */
    public boolean isFailure() {
        return "FAILURE".equals(this.status);
    }
}
