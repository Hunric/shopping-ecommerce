package com.hunric.model.dto;

import lombok.Data;

/**
 * 用户密码登录请求DTO
 */
@Data
public class UserPasswordLoginDTO {
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码
     */
    private String password;
}