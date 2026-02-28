package com.coldchain.user.controller;

import com.coldchain.common.result.Result;
import com.coldchain.user.dto.RefreshTokenDTO;
import com.coldchain.user.dto.UserLoginDTO;
import com.coldchain.user.service.AuthService;
import com.coldchain.user.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 
 * 功能：
 * - 用户登录
 * - Token 刷新
 * - 用户登出
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户登录
     * 
     * POST /api/auth/login
     * 
     * 请求体示例：
     * {
     *   "username": "admin",
     *   "password": "123456",
     *   "tenantId": "corp001"
     * }
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "登录成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIs...",
     *     "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
     *     "tokenType": "Bearer",
     *     "expiresIn": 7200,
     *     "userInfo": {
     *       "id": 1,
     *       "username": "admin",
     *       "realName": "管理员",
     *       "roleCode": "admin",
     *       "isAdmin": true
     *     }
     *   }
     * }
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody UserLoginDTO dto) {
        log.info("用户登录请求: username={}, tenantId={}", dto.getUsername(), dto.getTenantId());
        LoginVO vo = authService.login(dto);
        return Result.success("登录成功", vo);
    }
    
    /**
     * 刷新 Token
     * 
     * POST /api/auth/refresh
     * 
     * 请求体示例：
     * {
     *   "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
     * }
     * 
     * 响应示例：
     * {
     *   "code": 200,
     *   "message": "刷新成功",
     *   "data": {
     *     "token": "eyJhbGciOiJIUzI1NiIs...",
     *     "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
     *     "tokenType": "Bearer",
     *     "expiresIn": 7200
     *   }
     * }
     */
    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@Valid @RequestBody RefreshTokenDTO dto) {
        log.info("刷新 Token 请求");
        LoginVO vo = authService.refresh(dto.getRefreshToken());
        return Result.success("刷新成功", vo);
    }
    
    /**
     * 用户登出
     * <p>
     * POST /api/auth/logout
     * <p>
     * Header: Authorization: Bearer {token}
     * <p>
     * 响应示例：
     * {
     * "code": 200,
     * "message": "登出成功"
     * }
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        log.info("用户登出请求");
        authService.logout();
        return Result.success("登出成功");
    }
}
