package com.hunric.service;

import com.hunric.model.Merchant;
import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;

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
     * @param id 商家ID
     * @return 商家信息
     */
    Merchant getMerchantById(Long id);
    
    /**
     * 根据邮箱查询商家
     * 
     * @param email 邮箱
     * @return 商家信息
     */
    Merchant getMerchantByEmail(String email);
    
    /**
     * 更新商家状态
     * 
     * @param id 商家ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateMerchantStatus(Long id, Integer status);
} 