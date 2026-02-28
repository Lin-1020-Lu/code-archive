package com.coldchain.user.filter;

import com.coldchain.user.config.TenantContext;
import com.coldchain.user.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 租户过滤器
 * 
 * 功能：从请求中提取租户 ID，设置到 TenantContext
 * 
 * 租户识别方式优先级：
 * 1. JWT Token 中提取（最推荐）
 * 2. Header: X-Tenant-Id
 * 3. 子域名: corp001.coldchain.com
 * 4. URL 路径: /api/tenant/corp001/users
 * 
 * 设计要点：
 * 1. 使用 OncePerRequestFilter 确保每个请求只执行一次
 * 2. 在 finally 块中清除上下文，防止内存泄漏
 * 3. 支持多种租户识别方式，提高灵活性
 * 4. 开发环境支持默认租户
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(1) // 最高优先级，优先执行
public class TenantFilter extends OncePerRequestFilter {
    
    @Autowired(required = false)
    private JwtService jwtService;
    
    /**
     * 默认租户 ID（开发环境使用）
     */
    private static final String DEFAULT_TENANT_ID = "corp001";
    
    /**
     * 不需要租户信息的路径（白名单）
     * 如：登录、注册、租户注册等接口
     */
    private static final String[] WHITE_LIST = {
        "/api/auth/login",
        "/api/auth/register",
        "/api/tenants/register",
        "/api/health",
        "/actuator"
    };
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        String tenantId = null;
        String requestUri = request.getRequestURI();
        
        try {
            // 检查是否在白名单中
            if (isWhiteList(requestUri)) {
                log.debug("请求在白名单中，跳过租户检查: {}", requestUri);
                filterChain.doFilter(request, response);
                return;
            }
            
            // 方式1: 从 JWT Token 中提取租户 ID（推荐方式）
            String authorization = request.getHeader("Authorization");
            if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                if (jwtService != null) {
                    try {
                        tenantId = jwtService.extractTenantId(token);
                        log.debug("从 JWT Token 提取租户 ID: {}", tenantId);
                    } catch (Exception e) {
                        log.warn("从 JWT Token 提取租户 ID 失败: {}", e.getMessage());
                    }
                }
            }
            
            // 方式2: 从 Header 获取租户 ID
            if (!StringUtils.hasText(tenantId)) {
                tenantId = request.getHeader("X-Tenant-Id");
                if (StringUtils.hasText(tenantId)) {
                    log.debug("从 Header 提取租户 ID: {}", tenantId);
                }
            }
            
            // 方式3: 从子域名获取
            if (!StringUtils.hasText(tenantId)) {
                String host = request.getServerName();
                String[] parts = host.split("\\.");
                if (parts.length > 0 && !parts[0].equals("www") && !parts[0].equals("api")) {
                    tenantId = parts[0];
                    log.debug("从子域名提取租户 ID: {}", tenantId);
                }
            }
            
            // 方式4: 从 URL 路径获取
            if (!StringUtils.hasText(tenantId) && requestUri.matches("^/api/tenant/[^/]+/.*")) {
                String[] pathParts = requestUri.split("/");
                if (pathParts.length >= 4) {
                    tenantId = pathParts[3];
                    log.debug("从 URL 路径提取租户 ID: {}", tenantId);
                }
            }
            
            // 设置租户上下文
            if (StringUtils.hasText(tenantId)) {
                TenantContext.setTenantId(tenantId);
                log.debug("设置租户 ID: {}", tenantId);
            } else {
                // 开发环境：未检测到租户 ID，使用默认租户
                TenantContext.setTenantId(DEFAULT_TENANT_ID);
                log.warn("未检测到租户 ID，使用默认租户: {} (开发环境)", DEFAULT_TENANT_ID);
            }
            
            filterChain.doFilter(request, response);
            
        } finally {
            // 请求结束清除上下文，防止内存泄漏
            TenantContext.clear();
        }
    }
    
    /**
     * 检查请求是否在白名单中
     * 
     * @param requestUri 请求 URI
     * @return true 如果在白名单中，false 否则
     */
    private boolean isWhiteList(String requestUri) {
        for (String pattern : WHITE_LIST) {
            if (requestUri.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }
}
