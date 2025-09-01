package com.hunric.model.dto;

import lombok.Data;

/**
 * 用户验证码登录请求DTO
 */
@Data
public class UserLoginDTO {
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 验证码
     */
    private String verificationCode;
}