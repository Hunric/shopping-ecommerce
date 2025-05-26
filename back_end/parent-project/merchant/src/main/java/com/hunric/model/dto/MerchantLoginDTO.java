package com.hunric.model.dto;

import lombok.Data;

/**
 * 商家验证码登录请求DTO
 */
@Data
public class MerchantLoginDTO {
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 验证码
     */
    private String verificationCode;
} 