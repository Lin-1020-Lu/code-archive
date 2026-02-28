package com.coldchain.user.vo;

import lombok.Data;

/**
 * 登录响应 VO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class LoginVO {
    
    /**
     * 访问 Token
     * 有效期：2 小时
     */
    private String token;
    
    /**
     * 刷新 Token
     * 有效期：7 天
     */
    private String refreshToken;
    
    /**
     * Token 类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 访问 Token 过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 用户信息
     */
    private UserInfoVO userInfo;
}
