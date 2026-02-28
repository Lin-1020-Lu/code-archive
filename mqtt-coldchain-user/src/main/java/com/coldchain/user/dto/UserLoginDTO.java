package com.coldchain.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录 DTO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class UserLoginDTO {
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    
    /**
     * 租户 ID
     * 必须提供，用于多租户隔离
     */
    @NotBlank(message = "租户 ID 不能为空")
    private String tenantId;
    
    /**
     * 验证码（可选）
     */
    private String captcha;
    
    /**
     * 验证码 Key（可选）
     */
    private String captchaKey;
}
