package com.coldchain.user.service.impl;

import com.coldchain.user.config.JwtConfig;
import com.coldchain.user.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JWT 服务实现
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Override
    public String extractTenantId(String token) {
        return jwtConfig.extractTenantId(token);
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtConfig.validateToken(token);
    }
}
