package com.coldchain.user.service;

import com.coldchain.user.config.JwtConfig;

/**
 * JWT 服务接口
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public interface JwtService {
    
    /**
     * 从 Token 中提取租户 ID
     * 
     * @param token JWT Token
     * @return 租户 ID
     */
    String extractTenantId(String token);
    
    /**
     * 验证 Token 是否有效
     * 
     * @param token JWT Token
     * @return true 如果有效，false 否则
     */
    boolean validateToken(String token);
}
