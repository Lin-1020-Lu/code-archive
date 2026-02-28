package com.coldchain.user.filter;

import com.coldchain.user.config.JwtConfig;
import com.coldchain.user.config.TenantContext;
import com.coldchain.user.service.UserService;
import com.coldchain.user.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器
 * 
 * 功能：
 * 1. 从请求头中提取 JWT Token
 * 2. 验证 Token 有效性
 * 3. 从 Token 中提取用户信息
 * 4. 设置 Spring Security 上下文
 * 5. 设置租户上下文
 * 
 * 设计要点：
 * 1. 使用 OncePerRequestFilter 确保每个请求只执行一次
 * 2. 在 TenantFilter 之后执行（Order=2）
 * 3. 验证失败时直接放行，由后续拦截器处理
 * 4. 设置用户信息到 Security 上下文，供后续使用
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(2) // 在 TenantFilter 之后执行
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private UserService userService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 从请求头中提取 Token
            String token = extractToken(request);
            
            if (StringUtils.hasText(token) && jwtConfig.validateToken(token)) {
                // Token 有效，提取用户信息
                Long userId = jwtConfig.extractUserId(token);
                String username = jwtConfig.extractUsername(token);
                String tenantId = jwtConfig.extractTenantId(token);
                String roleCode = jwtConfig.extractRoleCode(token);
                
                log.debug("JWT Token 验证成功: userId={}, username={}, tenantId={}, roleCode={}",
                    userId, username, tenantId, roleCode);
                
                // 设置租户上下文
                if (StringUtils.hasText(tenantId)) {
                    TenantContext.setTenantId(tenantId);
                }
                TenantContext.setUserId(userId);
                TenantContext.setUsername(username);
                
                // 查询用户信息
                UserInfoVO user = userService.getById(userId);
                if (user != null && user.getStatus() == 1) {
                    // 用户有效，创建认证对象
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            null // 可以后续添加权限
                        );
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 设置到 Security 上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("用户认证成功: {}", username);
                } else {
                    log.warn("用户不存在或已被禁用: userId={}", userId);
                }
            } else {
                log.debug("未提供有效的 JWT Token");
            }
        } catch (Exception e) {
            log.error("JWT 认证失败: {}", e.getMessage());
            // 不抛出异常，让请求继续，由后续拦截器处理未认证情况
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求头中提取 Token
     * 
     * @param request HTTP 请求
     * @return Token 字符串，如果不存在则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConfig.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConfig.TOKEN_PREFIX.length());
        }
        return null;
    }
}
