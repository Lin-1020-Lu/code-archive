package com.coldchain.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户更新 DTO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class UserUpdateDTO {
    
    /**
     * 真实姓名
     */
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
    
    /**
     * 状态
     * 0=禁用, 1=正常, 2=锁定
     */
    private Integer status;
    
    /**
     * 头像 URL
     */
    private String avatar;
    
    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过 500 字符")
    private String remark;
}
