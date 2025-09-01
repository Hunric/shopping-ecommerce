package com.hunric.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 用户登录响应DTO
 */
@Data
@Builder
public class UserLoginResponseDTO {
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 访问令牌
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
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
}