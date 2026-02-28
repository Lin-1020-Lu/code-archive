package com.coldchain.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.coldchain.user.config.JwtConfig;
import com.coldchain.user.config.TenantContext;
import com.coldchain.user.dto.UserLoginDTO;
import com.coldchain.user.entity.LoginLog;
import com.coldchain.user.entity.Tenant;
import com.coldchain.user.entity.User;
import com.coldchain.user.enums.UserStatus;
import com.coldchain.user.enums.TenantStatus;
import com.coldchain.user.mapper.LoginLogMapper;
import com.coldchain.user.mapper.TenantMapper;
import com.coldchain.user.service.AuthService;
import com.coldchain.user.service.UserService;
import com.coldchain.user.vo.LoginVO;
import com.coldchain.user.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 认证服务实现
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Autowired
    private LoginLogMapper loginLogMapper;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private HttpServletRequest request;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(UserLoginDTO dto) {
        String tenantId = dto.getTenantId();
        String username = dto.getUsername();
        String password = dto.getPassword();
        String ip = getClientIp();
        
        log.info("用户登录: tenantId={}, username={}, ip={}", tenantId, username, ip);
        
        // 1. 检查租户是否存在
        Tenant tenant = tenantMapper.selectByTenantId(tenantId);
        if (tenant == null) {
            recordLoginLog(tenantId, null, username, ip, 0, "租户不存在");
            throw new RuntimeException("租户不存在");
        }
        
        // 2. 检查租户状态
        if (!TenantStatus.fromCode(tenant.getStatus()).isAccessible() || tenant.isExpired()) {
            recordLoginLog(tenantId, null, username, ip, 0, "租户已禁用或已过期");
            throw new RuntimeException("租户已禁用或已过期");
        }
        
        // 3. 设置租户上下文
        TenantContext.setTenantId(tenantId);
        
        // 4. 查询用户
        User user = userService.getByUsername(username);
        if (user == null) {
            recordLoginLog(tenantId, null, username, ip, 0, "用户不存在");
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 5. 检查用户状态
        if (user.getStatus() != UserStatus.NORMAL.getCode()) {
            UserStatus status = UserStatus.fromCode(user.getStatus());
            if (status == UserStatus.DISABLED) {
                recordLoginLog(tenantId, user.getId(), username, ip, 0, "用户已被禁用");
                throw new RuntimeException("用户已被禁用");
            } else if (status == UserStatus.LOCKED) {
                recordLoginLog(tenantId, user.getId(), username, ip, 0, "用户已被锁定");
                throw new RuntimeException("用户已被锁定，请联系管理员");
            } else if (status == UserStatus.UNACTIVATED) {
                recordLoginLog(tenantId, user.getId(), username, ip, 0, "用户未激活");
                throw new RuntimeException("用户未激活");
            }
        }
        
        // 6. 验证密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            // 增加登录失败次数
            userService.increaseLoginFailCount(user.getId());
            recordLoginLog(tenantId, user.getId(), username, ip, 0, "密码错误");
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 7. 重置登录失败次数
        userService.resetLoginFailCount(user.getId());
        
        // 8. 更新最后登录信息
        userService.updateLastLoginInfo(user.getId(), ip);
        
        // 9. 生成 Token
        String token = jwtConfig.generateAccessToken(user.getId(), username, tenantId, user.getRoleCode());
        String refreshToken = jwtConfig.generateRefreshToken(user.getId(), username, tenantId, user.getRoleCode());
        
        // 10. 记录登录日志
        recordLoginLog(tenantId, user.getId(), username, ip, 1, null);
        
        // 11. 构建响应
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setRefreshToken(refreshToken);
        vo.setTokenType("Bearer");
        vo.setExpiresIn(jwtConfig.getAccessTokenExpiration() / 1000);
        
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setRoleCode(user.getRoleCode());
        userInfo.setRoleName(user.getRoleName());
        userInfo.setIsAdmin(user.isTenantAdmin());
        userInfo.setStatus(user.getStatus());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        userInfo.setCreatedAt(user.getCreatedAt());
        
        vo.setUserInfo(userInfo);
        
        log.info("用户登录成功: tenantId={}, username={}, userId={}", tenantId, username, user.getId());
        
        return vo;
    }
    
    @Override
    public LoginVO refresh(String refreshToken) {
        // 验证刷新 Token
        if (!jwtConfig.validateToken(refreshToken)) {
            throw new RuntimeException("刷新 Token 无效");
        }
        
        // 从 Token 中提取信息
        Long userId = jwtConfig.extractUserId(refreshToken);
        String username = jwtConfig.extractUsername(refreshToken);
        String tenantId = jwtConfig.extractTenantId(refreshToken);
        String roleCode = jwtConfig.extractRoleCode(refreshToken);
        
        log.info("刷新 Token: userId={}, username={}, tenantId={}", userId, username, tenantId);
        
        // 生成新的 Token
        String newToken = jwtConfig.generateAccessToken(userId, username, tenantId, roleCode);
        String newRefreshToken = jwtConfig.generateRefreshToken(userId, username, tenantId, roleCode);
        
        // 构建响应
        LoginVO vo = new LoginVO();
        vo.setToken(newToken);
        vo.setRefreshToken(newRefreshToken);
        vo.setTokenType("Bearer");
        vo.setExpiresIn(jwtConfig.getAccessTokenExpiration() / 1000);
        
        log.info("Token 刷新成功: userId={}", userId);
        
        return vo;
    }
    
    @Override
    public void logout() {
        Long userId = TenantContext.getUserId();
        String username = TenantContext.getUsername();
        
        log.info("用户登出: userId={}, username={}", userId, username);
        
        // 如果使用 Redis 存储 Token，可以在这里删除
        // 当前实现使用 JWT，无需服务器端登出
    }
    
    /**
     * 记录登录日志
     */
    private void recordLoginLog(String tenantId, Long userId, String username, 
                               String ip, int status, String failureReason) {
        LoginLog log = new LoginLog();
        log.setTenantId(tenantId);
        log.setUserId(userId);
        log.setUsername(username);
        log.setStatus(status);
        log.setFailureReason(failureReason);
        log.setIp(ip);
        log.setLocation(getLocationByIp(ip));
        log.setUserAgent(getUserAgent());
        log.setBrowser(getBrowser());
        log.setOs(getOs());
        log.setLoginTime(LocalDateTime.now());
        
        loginLogMapper.insert(log);
    }
    
    /**
     * 获取客户端 IP
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个 IP 的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    /**
     * 根据 IP 获取地理位置（简化版）
     */
    private String getLocationByIp(String ip) {
        // 实际项目中应该调用 IP 地理位置查询服务
        return "未知";
    }
    
    /**
     * 获取 User-Agent
     */
    private String getUserAgent() {
        return request.getHeader("User-Agent");
    }
    
    /**
     * 获取浏览器（简化版）
     */
    private String getBrowser() {
        String userAgent = getUserAgent();
        if (userAgent == null) {
            return "未知";
        }
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else {
            return "未知";
        }
    }
    
    /**
     * 获取操作系统（简化版）
     */
    private String getOs() {
        String userAgent = getUserAgent();
        if (userAgent == null) {
            return "未知";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "Mac";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iOS")) {
            return "iOS";
        } else {
            return "未知";
        }
    }
}
