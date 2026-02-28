package com.coldchain.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.coldchain.user.config.TenantContext;
import com.coldchain.user.dto.UserRegisterDTO;
import com.coldchain.user.dto.UserUpdateDTO;
import com.coldchain.user.entity.User;
import com.coldchain.user.enums.UserStatus;
import com.coldchain.user.mapper.UserMapper;
import com.coldchain.user.service.UserService;
import com.coldchain.user.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserInfoVO getById(Long id) {
        String tenantId = TenantContext.getTenantId();
        User user = userMapper.selectByIdAndTenantId(id, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }
    
    @Override
    public User getByUsername(String username) {
        String tenantId = TenantContext.getTenantId();
        return userMapper.selectByUsernameAndTenantId(username, tenantId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO register(UserRegisterDTO dto) {
        // 1. 验证确认密码
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        String tenantId = TenantContext.getTenantId();
        
        // 2. 检查用户名是否已存在
        User existUser = userMapper.selectByUsernameAndTenantId(dto.getUsername(), tenantId);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 3. 创建用户
        User user = new User();
        user.setTenantId(tenantId);
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRoleId(dto.getRoleId());
        user.setStatus(UserStatus.NORMAL.getCode());
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(TenantContext.getUserId());
        
        userMapper.insert(user);
        
        log.info("用户注册成功: tenantId={}, username={}", tenantId, dto.getUsername());
        
        return convertToVO(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UserUpdateDTO dto) {
        String tenantId = TenantContext.getTenantId();
        
        // 1. 查询用户
        User user = userMapper.selectByIdAndTenantId(id, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 更新字段
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getRoleId() != null) {
            user.setRoleId(dto.getRoleId());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getRemark() != null) {
            user.setRemark(dto.getRemark());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(TenantContext.getUserId());
        
        userMapper.updateByIdAndTenantId(user, tenantId);
        
        log.info("用户更新成功: userId={}", id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        String tenantId = TenantContext.getTenantId();
        
        // 1. 查询用户
        User user = userMapper.selectByIdAndTenantId(id, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 删除用户（软删除）
        userMapper.deleteByIdAndTenantId(id, tenantId);
        
        log.info("用户删除成功: userId={}", id);
    }
    
    @Override
    public List<UserInfoVO> list(String username, Integer status) {
        String tenantId = TenantContext.getTenantId();
        List<User> users = userMapper.selectList(tenantId, username, status, null);
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        String tenantId = TenantContext.getTenantId();
        
        // 1. 查询用户
        User user = userMapper.selectByIdAndTenantId(userId, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 验证旧密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }
        
        // 3. 更新密码
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(userId);
        
        userMapper.updateByIdAndTenantId(user, tenantId);
        
        log.info("用户修改密码成功: userId={}", userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, String newPassword) {
        String tenantId = TenantContext.getTenantId();
        
        // 1. 查询用户
        User user = userMapper.selectByIdAndTenantId(userId, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 重置密码
        user.setPassword(BCrypt.hashpw(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(TenantContext.getUserId());
        
        userMapper.updateByIdAndTenantId(user, tenantId);
        
        log.info("管理员重置用户密码成功: userId={}", userId);
    }
    
    @Override
    public void updateStatus(Long userId, Integer status) {
        String tenantId = TenantContext.getTenantId();
        
        // 1. 查询用户
        User user = userMapper.selectByIdAndTenantId(userId, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 更新状态
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(TenantContext.getUserId());
        
        userMapper.updateByIdAndTenantId(user, tenantId);
        
        log.info("用户状态更新成功: userId={}, status={}", userId, status);
    }
    
    @Override
    public void updateLastLoginInfo(Long userId, String ip) {
        String tenantId = TenantContext.getTenantId();
        userMapper.updateLastLoginInfo(userId, tenantId, ip);
    }
    
    @Override
    public void increaseLoginFailCount(Long userId) {
        String tenantId = TenantContext.getTenantId();
        User user = userMapper.selectByIdAndTenantId(userId, tenantId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.increaseLoginFailCount();
        
        userMapper.updateLoginFailCount(
            userId,
            tenantId,
            user.getLoginFailCount(),
            user.getLockTime() != null ? user.getLockTime().toString() : null
        );
    }
    
    @Override
    public void resetLoginFailCount(Long userId) {
        String tenantId = TenantContext.getTenantId();
        userMapper.resetLoginFailCount(userId, tenantId);
    }
    
    /**
     * 转换为 VO
     */
    private UserInfoVO convertToVO(User user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtil.copyProperties(user, vo);
        vo.setIsAdmin(user.isTenantAdmin());
        return vo;
    }
}
