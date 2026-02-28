package com.coldchain.user.entity;

import com.coldchain.user.enums.UserStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 
 * 功能：存储用户的基本信息和认证信息
 * 
 * 设计要点：
 * 1. tenant_id 是核心字段，实现多租户隔离
 * 2. username 在租户内唯一（tenant_id + username 唯一索引）
 * 3. 密码使用 BCrypt 加密存储
 * 4. 支持用户状态管理（禁用/正常/锁定/未激活）
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private Long id;
    
    /**
     * 租户 ID（核心字段，多租户隔离）
     * 
     * 约束：
     * - 必须存在且有效
     * - 所有操作都在租户范围内进行
     * - 用户名在租户内唯一
     */
    private String tenantId;
    
    /**
     * 用户名
     * 
     * 约束：
     * - 租户内唯一
     * - 长度 3-50 字符
     * - 只允许字母、数字、下划线
     */
    private String username;
    
    /**
     * 密码（BCrypt 加密）
     * 
     * 安全要求：
     * - 使用 BCrypt 算法加密
     * - 密码强度要求（8位以上，包含大小写字母、数字）
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 邮箱
     * 
     * 约束：
     * - 租户内唯一（可选）
     * - 必须符合邮箱格式
     */
    private String email;
    
    /**
     * 手机号
     * 
     * 约束：
     * - 租户内唯一（可选）
     * - 必须符合手机号格式
     */
    private String phone;
    
    /**
     * 角色 ID
     * 关联 role 表
     */
    private Long roleId;
    
    /**
     * 角色代码（冗余字段，提高查询效率）
     */
    private String roleCode;
    
    /**
     * 角色名称（冗余字段，提高查询效率）
     */
    private String roleName;
    
    /**
     * 用户状态
     * 0=禁用, 1=正常, 2=锁定, 3=未激活
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登录 IP
     */
    private String lastLoginIp;
    
    /**
     * 登录失败次数
     * 用于登录失败锁定策略
     */
    private Integer loginFailCount;
    
    /**
     * 锁定时间
     * 登录失败次数过多时锁定
     */
    private LocalDateTime lockTime;
    
    /**
     * 头像 URL
     */
    private String avatar;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建者 ID
     */
    private Long createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新者 ID
     */
    private Long updatedBy;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 获取用户状态枚举
     * 
     * @return 用户状态枚举
     */
    public UserStatus getStatusEnum() {
        return UserStatus.fromCode(this.status);
    }
    
    /**
     * 检查用户是否被锁定
     * 
     * @return true 如果被锁定，false 否则
     */
    public boolean isLocked() {
        if (lockTime == null) {
            return false;
        }
        // 检查锁定是否已过期（默认锁定 30 分钟）
        return lockTime.plusMinutes(30).isAfter(LocalDateTime.now());
    }
    
    /**
     * 检查用户是否允许登录
     * 
     * @return true 如果允许登录，false 否则
     */
    public boolean isLoginable() {
        UserStatus status = getStatusEnum();
        return status != null && status.isLoginable() && !isLocked();
    }
    
    /**
     * 检查是否为租户管理员
     * 
     * @return true 如果是租户管理员，false 否则
     */
    public boolean isTenantAdmin() {
        return "admin".equals(this.roleCode);
    }
    
    /**
     * 增加登录失败次数
     */
    public void increaseLoginFailCount() {
        this.loginFailCount = (this.loginFailCount == null ? 0 : this.loginFailCount) + 1;
        
        // 连续失败 5 次则锁定
        if (this.loginFailCount >= 5) {
            this.lockTime = LocalDateTime.now();
            this.status = UserStatus.LOCKED.getCode();
        }
    }
    
    /**
     * 清除登录失败次数
     */
    public void clearLoginFailCount() {
        this.loginFailCount = 0;
        this.lockTime = null;
        if (this.status != null && this.status == UserStatus.LOCKED.getCode()) {
            this.status = UserStatus.NORMAL.getCode();
        }
    }
}
