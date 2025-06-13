package com.hunric.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 商家密码登录DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPasswordLoginDTO {
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码
     */
    private String password;
} 