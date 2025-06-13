package com.hunric.service;

import com.hunric.model.Merchant;
import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;
import com.hunric.model.dto.MerchantLoginDTO;
import com.hunric.model.dto.MerchantLoginResponseDTO;
import com.hunric.model.dto.MerchantPasswordLoginDTO;
import com.hunric.model.dto.MerchantResetPasswordDTO;
import com.hunric.model.dto.MerchantProfileDTO;
import com.hunric.model.dto.MerchantPasswordChangeDTO;
import com.hunric.common.model.ApiResponse;

/**
 * 商家服务接口
 */
public interface MerchantService {
    
    /**
     * 注册新商家
     * 
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    MerchantResponseDTO register(MerchantRegisterDTO registerDTO);
    
    /**
     * 根据ID查询商家
     * 
     * @param merchantId 商家ID
     * @return 商家信息
     */
    Merchant getMerchantById(Long merchantId);
    
    /**
     * 根据邮箱查询商家
     * 
     * @param email 邮箱
     * @return 商家信息
     */
    Merchant getMerchantByEmail(String email);
    
    /**
     * 发送登录验证码
     * 
     * @param email 商家邮箱
     * @return 发送结果
     */
    ApiResponse<String> sendLoginVerificationCode(String email);
    
    /**
     * 验证码登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    MerchantLoginResponseDTO loginWithVerificationCode(MerchantLoginDTO loginDTO);
    
    /**
     * 密码登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    MerchantLoginResponseDTO loginWithPassword(MerchantPasswordLoginDTO loginDTO);
    
    /**
     * 获取商家信息
     * 
     * @param merchantId 商家ID
     * @return 商家信息
     */
    ApiResponse<Object> getMerchantInfo(Integer merchantId);
    
    /**
     * 更新商家信息
     * 
     * @param profileDTO 商家信息
     * @return 更新结果
     */
    ApiResponse<Object> updateMerchantInfo(MerchantProfileDTO profileDTO);
    
    /**
     * 修改商家密码
     * 
     * @param passwordChangeDTO 密码修改信息
     * @return 修改结果
     */
    ApiResponse<Object> changePassword(MerchantPasswordChangeDTO passwordChangeDTO);
    
    /**
     * 发送重置密码验证码
     * 
     * @param email 商家邮箱
     * @return 发送结果
     */
    ApiResponse<String> sendResetPasswordVerificationCode(String email);
    
    /**
     * 验证重置密码验证码
     * 
     * @param email 商家邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    ApiResponse<Boolean> verifyResetPasswordCode(String email, String verificationCode);
    
    /**
     * 重置密码
     * 
     * @param resetPasswordDTO 重置密码信息
     * @return 重置结果
     */
    ApiResponse<Boolean> resetPassword(MerchantResetPasswordDTO resetPasswordDTO);
} 