package com.coldchain.user.service;

import com.coldchain.user.dto.UserLoginDTO;
import com.coldchain.user.vo.LoginVO;

/**
 * 认证服务接口
 * 
 * 功能：
 * - 用户登录
 * - Token 刷新
 * - 用户登出
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
public interface AuthService {
    
    /**
     * 用户登录
     * 
     * @param dto 登录 DTO
     * @return 登录响应 VO
     */
    LoginVO login(UserLoginDTO dto);
    
    /**
     * 刷新 Token
     * 
     * @param refreshToken 刷新 Token
     * @return 登录响应 VO
     */
    LoginVO refresh(String refreshToken);
    
    /**
     * 用户登出
     */
    void logout();
}
