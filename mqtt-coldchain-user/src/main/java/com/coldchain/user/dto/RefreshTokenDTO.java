package com.coldchain.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 刷新 Token DTO
 * 
 * @author ColdChain Team
 * @since 1.0.0
 */
@Data
public class RefreshTokenDTO {
    
    /**
     * 刷新 Token
     */
    @NotBlank(message = "刷新 Token 不能为空")
    private String refreshToken;
}
