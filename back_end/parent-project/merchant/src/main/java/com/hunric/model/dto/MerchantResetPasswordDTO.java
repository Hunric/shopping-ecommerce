package com.hunric.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商家重置密码DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantResetPasswordDTO {
    
    /**
     * 商家邮箱
     */
    private String email;
    
    /**
     * 新密码
     */
    private String newPassword;
    
    /**
     * 验证码
     */
    private String verificationCode;
} 