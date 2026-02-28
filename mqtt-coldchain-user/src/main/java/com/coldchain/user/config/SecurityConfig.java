package com.coldchain.user.config;

import com.coldchain.user.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 * 
 * 功能：
 * 1. 配置 JWT 认证过滤器
 * 2. 配置 URL 访问权限
 * 3. 配置会话管理策略（无状态）
 * 4. 启用方法级权限控制
 * 
 * 设计要点：
 * 1. 禁用 CSRF（使用 JWT 不需要）
 * 2. 无状态会话（JWT 不依赖会话）
 * 3. 白名单路径无需认证
 * 4. 其他所有请求需要认证
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级权限控制
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    /**
     * 白名单路径（无需认证）
     */
    private static final String[] WHITE_LIST = {
        "/api/auth/login",
        "/api/auth/register",
        "/api/tenants/register",
        "/api/health",
        "/actuator/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/v2/api-docs",
        "/webjars/**",
        "/error"
    };
    
    /**
     * 密码编码器
     * 使用 BCrypt 加密算法
     * 
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置 HTTP 安全规则
     * 
     * @param http HttpSecurity 对象
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（使用 JWT 不需要）
            .csrf().disable()
            
            // 无状态会话（JWT 不依赖会话）
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            
            // 配置 URL 访问权限
            .authorizeRequests()
                // 白名单路径允许匿名访问
                .antMatchers(WHITE_LIST).permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            .and()
            
            // 添加 JWT 认证过滤器
            // 在 UsernamePasswordAuthenticationFilter 之前执行
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
