package com.coldchain.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.coldchain.user.dto.TenantRegisterDTO;
import com.coldchain.user.entity.Tenant;
import com.coldchain.user.enums.TenantStatus;
import com.coldchain.user.entity.User;
import com.coldchain.user.enums.UserStatus;
import com.coldchain.user.mapper.TenantMapper;
import com.coldchain.user.mapper.UserMapper;
import com.coldchain.user.service.UserService;
import com.coldchain.user.service.TenantService;
import com.coldchain.user.vo.TenantInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 租户服务实现
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class TenantServiceImpl implements TenantService {
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 租户 ID 序号（用于生成唯一租户 ID）
     */
    private static final AtomicInteger TENANT_ID_SEQUENCE = new AtomicInteger(1000);
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TenantInfoVO register(TenantRegisterDTO dto) {
        // 1. 生成租户 ID
        String tenantId = generateTenantId();
        
        // 2. 创建租户
        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        tenant.setTenantName(dto.getTenantName());
        tenant.setTenantType(dto.getTenantType());
        tenant.setStatus(TenantStatus.NORMAL.getCode());
        
        // 根据租户类型设置默认配置
        switch (dto.getTenantType()) {
            case 1: // 试用版
                tenant.setExpireDate(LocalDateTime.now().plusDays(30));
                tenant.setMaxUsers(5);
                break;
            case 2: // 标准版
                tenant.setExpireDate(LocalDateTime.now().plusYears(1));
                tenant.setMaxUsers(50);
                break;
            case 3: // 企业版
                tenant.setExpireDate(LocalDateTime.now().plusYears(1));
                tenant.setMaxUsers(1000);
                break;
            default:
                tenant.setExpireDate(LocalDateTime.now().plusDays(30));
                tenant.setMaxUsers(5);
        }
        
        tenant.setCurrentUsers(0);
        tenant.setContactName(dto.getContactName());
        tenant.setContactPhone(dto.getContactPhone());
        tenant.setContactEmail(dto.getContactEmail());
        tenant.setIsolationType(3); // 默认使用共享库表
        tenant.setCreatedAt(LocalDateTime.now());
        
        tenantMapper.insert(tenant);
        
        // 3. 创建初始管理员
        User admin = new User();
        admin.setTenantId(tenantId);
        admin.setUsername(dto.getAdminUsername());
        admin.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(dto.getAdminPassword()));
        admin.setRealName(dto.getAdminRealName() != null ? dto.getAdminRealName() : dto.getContactName());
        admin.setRoleId(1L); // 默认角色 ID
        admin.setRoleCode("admin");
        admin.setRoleName("租户管理员");
        admin.setStatus(UserStatus.NORMAL.getCode());
        admin.setCreatedAt(LocalDateTime.now());
        
        userMapper.insert(admin);
        
        // 4. 更新租户用户数
        tenantMapper.updateCurrentUsers(tenantId, 1);
        
        log.info("租户注册成功: tenantId={}, tenantName={}", tenantId, dto.getTenantName());
        
        return convertToVO(tenant);
    }
    
    @Override
    public TenantInfoVO getCurrentTenant() {
        String tenantId = com.coldchain.user.config.TenantContext.getTenantId();
        Tenant tenant = tenantMapper.selectByTenantId(tenantId);
        if (tenant == null) {
            throw new RuntimeException("租户不存在");
        }
        return convertToVO(tenant);
    }
    
    @Override
    public TenantInfoVO getTenantByTenantId(String tenantId) {
        Tenant tenant = tenantMapper.selectByTenantId(tenantId);
        if (tenant == null) {
            throw new RuntimeException("租户不存在");
        }
        return convertToVO(tenant);
    }
    
    @Override
    public List<TenantInfoVO> list(Integer status, Integer tenantType) {
        List<Tenant> tenants = tenantMapper.selectList(status, tenantType);
        return tenants.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        tenantMapper.updateStatus(id, status);
        log.info("租户状态更新成功: id={}, status={}", id, status);
    }
    
    @Override
    public boolean exists(String tenantId) {
        return tenantMapper.existsByTenantId(tenantId);
    }
    
    @Override
    public String generateTenantId() {
        // 生成格式：corp{序号}
        int seq = TENANT_ID_SEQUENCE.incrementAndGet();
        return "corp" + seq;
    }
    
    /**
     * 转换为 VO
     */
    private TenantInfoVO convertToVO(Tenant tenant) {
        TenantInfoVO vo = new TenantInfoVO();
        BeanUtil.copyProperties(tenant, vo);
        return vo;
    }
}
