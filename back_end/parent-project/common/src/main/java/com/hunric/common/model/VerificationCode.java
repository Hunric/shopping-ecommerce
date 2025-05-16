package com.hunric.common.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码实体类
 */
@Data
public class VerificationCode implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String email;            // 邮箱地址
    private String code;             // 验证码
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime; // 过期时间
    
    private Boolean used;            // 是否已使用
    private String purpose;          // 用途（登录/注册）
    
    /**
     * 检查验证码是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
    
    /**
     * 构造函数
     */
    public VerificationCode(String email, String code, String purpose, int expirationMinutes) {
        this.email = email;
        this.code = code;
        this.purpose = purpose;
        this.createTime = LocalDateTime.now();
        this.expireTime = createTime.plusMinutes(expirationMinutes);
        this.used = false;
    }
    
    public VerificationCode() {
        // 默认构造函数
    }
} 