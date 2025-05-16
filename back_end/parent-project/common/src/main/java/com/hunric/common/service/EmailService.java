package com.hunric.common.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param purpose 用途（登录/注册）
     * @return 验证码
     */
    String sendVerificationCode(String to, String purpose);
    
    /**
     * 验证验证码
     * @param email 邮箱
     * @param code 验证码
     * @param purpose 用途（登录/注册）
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code, String purpose);
    
    /**
     * 标记验证码为已使用
     * @param email 邮箱
     * @param purpose 用途（登录/注册）
     */
    void markCodeAsUsed(String email, String purpose);
} 