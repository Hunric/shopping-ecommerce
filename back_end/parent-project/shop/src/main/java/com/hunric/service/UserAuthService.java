package com.hunric.service;

import com.hunric.model.dto.*;
import com.hunric.common.model.ApiResponse;

/**
 * 用户认证服务接口
 */
public interface UserAuthService {
    
    /**
     * 发送注册验证码
     * @param email 用户邮箱
     * @return 发送结果
     */
    ApiResponse<String> sendRegisterVerificationCode(String email);
    
    /**
     * 验证注册验证码
     * @param email 用户邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    ApiResponse<Boolean> verifyRegisterCode(String email, String verificationCode);
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @param verificationCode 验证码
     * @return 注册结果
     */
    UserResponseDTO register(UserRegisterDTO registerDTO, String verificationCode);
    
    /**
     * 发送登录验证码
     * @param email 用户邮箱
     * @return 发送结果
     */
    ApiResponse<String> sendLoginVerificationCode(String email);
    
    /**
     * 验证码登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    UserLoginResponseDTO loginWithVerificationCode(UserLoginDTO loginDTO);
    
    /**
     * 密码登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    UserLoginResponseDTO loginWithPassword(UserPasswordLoginDTO loginDTO);
    
    /**
     * 发送重置密码验证码
     * @param email 用户邮箱
     * @return 发送结果
     */
    ApiResponse<String> sendResetPasswordVerificationCode(String email);
    
    /**
     * 验证重置密码验证码
     * @param email 用户邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    ApiResponse<Boolean> verifyResetPasswordCode(String email, String verificationCode);
    
    /**
     * 重置密码
     * @param email 用户邮箱
     * @param newPassword 新密码
     * @param verificationCode 验证码
     * @return 重置结果
     */
    ApiResponse<Boolean> resetPassword(String email, String newPassword, String verificationCode);
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    ApiResponse<Object> getUserInfo(Integer userId);
}