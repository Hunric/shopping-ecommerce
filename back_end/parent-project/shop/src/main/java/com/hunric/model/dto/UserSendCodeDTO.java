package com.hunric.model.dto;

import lombok.Data;

/**
 * 用户发送验证码请求DTO
 */
@Data
public class UserSendCodeDTO {
    /**
     * 邮箱
     */
    private String email;
}