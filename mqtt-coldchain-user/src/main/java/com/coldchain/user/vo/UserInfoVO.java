package com.coldchain.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息 VO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class UserInfoVO {
    
    /**
     * 用户 ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 角色 ID
     */
    private Long roleId;
    
    /**
     * 角色代码
     */
    private String roleCode;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 是否为租户管理员
     */
    private Boolean isAdmin;
    
    /**
     * 状态
     */
    private Integer status;
    
    /**
     * 头像 URL
     */
    private String avatar;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
