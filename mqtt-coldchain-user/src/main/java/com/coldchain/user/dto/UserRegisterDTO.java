package com.coldchain.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册 DTO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class UserRegisterDTO {
    
    /**
     * 用户名
     * 
     * 约束：
     * - 长度 3-50 字符
     * - 只允许字母、数字、下划线
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    /**
     * 密码
     * 
     * 约束：
     * - 至少 8 个字符
     * - 必须包含大小写字母和数字
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少 8 个字符")
    private String password;
    
    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 64, message = "真实姓名不能超过 64 字符")
    private String realName;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 角色 ID
     */
    private Long roleId;
}
