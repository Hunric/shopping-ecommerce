package com.hunric.model.dto;

import lombok.Data;

/**
 * 用户注册DTO，用于接收前端提交的注册数据
 */
@Data
public class UserRegisterDTO {
    private String username;      // 用户名
    private String email;         // 用户邮箱
    private String password;      // 密码
    private String gender;        // 性别：M-男，F-女
    private String avatarUrl;     // 头像URL（可选）
}