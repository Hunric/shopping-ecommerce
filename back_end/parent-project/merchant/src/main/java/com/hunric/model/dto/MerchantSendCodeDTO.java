package com.hunric.model.dto;

import lombok.Data;

/**
 * 商家发送验证码请求DTO
 */
@Data
public class MerchantSendCodeDTO {
    /**
     * 邮箱
     */
    private String email;
} 