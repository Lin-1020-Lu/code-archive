package com.coldchain.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 租户注册 DTO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class TenantRegisterDTO {
    
    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    @Size(min = 2, max = 128, message = "租户名称长度必须在 2-128 字符之间")
    private String tenantName;
    
    /**
     * 租户类型
     * 1=试用版, 2=标准版, 3=企业版
     * 默认为 1（试用版）
     */
    private Integer tenantType = 1;
    
    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    @Size(max = 64, message = "联系人不能超过 64 字符")
    private String contactName;
    
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    @Email(message = "联系邮箱格式不正确")
    private String contactEmail;
    
    /**
     * 初始管理员用户名
     */
    @NotBlank(message = "初始管理员用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String adminUsername;
    
    /**
     * 初始管理员密码
     */
    @NotBlank(message = "初始管理员密码不能为空")
    @Size(min = 8, message = "密码长度至少 8 个字符")
    private String adminPassword;
    
    /**
     * 初始管理员真实姓名
     */
    @Size(max = 64, message = "真实姓名不能超过 64 字符")
    private String adminRealName;
}
