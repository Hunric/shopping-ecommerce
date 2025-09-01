package com.hunric.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 用户注册响应DTO
 */
@Data
@Builder
public class UserResponseDTO {
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
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