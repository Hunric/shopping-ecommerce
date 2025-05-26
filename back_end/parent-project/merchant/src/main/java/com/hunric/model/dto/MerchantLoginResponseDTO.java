package com.hunric.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 商家登录响应DTO
 */
@Data
@Builder
public class MerchantLoginResponseDTO {
    /**
     * 是否登录成功
     */
    private Boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * JWT访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 令牌类型
     */
    private String tokenType;
    
    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;
    
    /**
     * 商家ID
     */
    private Long merchantId;
    
    /**
     * 商家名称
     */
    private String merchantName;
    
    /**
     * 商家邮箱
     */
    private String email;
    
    /**
     * 商家状态
     */
    private Integer status;
} 