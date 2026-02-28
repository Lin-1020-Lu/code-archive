package com.coldchain.user.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 配置类
 * 
 * 功能：提供 JWT Token 的生成、解析、验证功能
 * 
 * 设计要点：
 * 1. 使用 HS256 算法（对称加密）
 * 2. Token 中包含：用户 ID、用户名、租户 ID、角色代码
 * 3. 支持访问 Token（短期）和刷新 Token（长期）
 * 4. 密钥从配置文件读取，支持不同环境
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class JwtConfig {
    
    /**
     * JWT 密钥（Base64 编码）
     * 生产环境应该使用加密配置中心存储
     */
    @Value("${jwt.secret:coldchain-mqtt-user-service-secret-key-2024}")
    private String jwtSecret;
    
    /**
     * 访问 Token 有效期（毫秒）
     * 默认 2 小时
     */
    @Value("${jwt.access-token-expiration:7200000}")
    private long accessTokenExpiration;
    
    /**
     * 刷新 Token 有效期（毫秒）
     * 默认 7 天
     */
    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;
    
    /**
     * Token 头部前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * Token 头部名称
     */
    public static final String TOKEN_HEADER = "Authorization";
    
    /**
     * 获取签名密钥
     * 
     * @return SecretKey 对象
     */
    public SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成访问 Token
     * 
     * @param userId 用户 ID
     * @param username 用户名
     * @param tenantId 租户 ID
     * @param roleCode 角色代码
     * @return JWT Token
     */
    public String generateAccessToken(Long userId, String username, String tenantId, String roleCode) {
        return generateToken(userId, username, tenantId, roleCode, accessTokenExpiration);
    }
    
    /**
     * 生成刷新 Token
     * 
     * @param userId 用户 ID
     * @param username 用户名
     * @param tenantId 租户 ID
     * @param roleCode 角色代码
     * @return JWT Token
     */
    public String generateRefreshToken(Long userId, String username, String tenantId, String roleCode) {
        return generateToken(userId, username, tenantId, roleCode, refreshTokenExpiration);
    }
    
    /**
     * 生成 Token（通用方法）
     * 
     * @param userId 用户 ID
     * @param username 用户名
     * @param tenantId 租户 ID
     * @param roleCode 角色代码
     * @param expiration 过期时间（毫秒）
     * @return JWT Token
     */
    private String generateToken(Long userId, String username, String tenantId, String roleCode, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("tenantId", tenantId);
        claims.put("roleCode", roleCode);
        claims.put("type", "access");
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 从 Token 中提取所有 Claims
     * 
     * @param token JWT Token
     * @return Claims 对象
     * @throws Exception Token 无效时抛出异常
     */
    public Claims extractAllClaims(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 从 Token 中提取用户 ID
     * 
     * @param token JWT Token
     * @return 用户 ID
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return Long.valueOf(claims.get("userId").toString());
        } catch (Exception e) {
            log.error("提取用户 ID 失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从 Token 中提取用户名
     * 
     * @param token JWT Token
     * @return 用户名
     */
    public String extractUsername(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("提取用户名失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从 Token 中提取租户 ID
     * 
     * @param token JWT Token
     * @return 租户 ID
     */
    public String extractTenantId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("tenantId", String.class);
        } catch (Exception e) {
            log.error("提取租户 ID 失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从 Token 中提取角色代码
     * 
     * @param token JWT Token
     * @return 角色代码
     */
    public String extractRoleCode(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("roleCode", String.class);
        } catch (Exception e) {
            log.error("提取角色代码失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 验证 Token 是否过期
     * 
     * @param token JWT Token
     * @return true 如果 Token 已过期，false 否则
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("验证 Token 过期时间失败: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 验证 Token 是否有效
     * 
     * @param token JWT Token
     * @return true 如果 Token 有效，false 否则
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取访问 Token 有效期
     * 
     * @return 有效期（毫秒）
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
    
    /**
     * 获取刷新 Token 有效期
     * 
     * @return 有效期（毫秒）
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
