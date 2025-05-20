package com.hunric.service.impl;

import com.hunric.mapper.MerchantMapper;
import com.hunric.model.Merchant;
import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;
import com.hunric.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家服务实现类
 */
@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 注册新商家
     * 
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @Override
    @Transactional
    public MerchantResponseDTO register(MerchantRegisterDTO registerDTO) {
        log.info("注册新商家: {}", registerDTO.getMerchantName());
        
        // 检查邮箱是否已被注册
        Merchant existingMerchant = merchantMapper.selectByEmail(registerDTO.getEmail());
        if (existingMerchant != null) {
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("邮箱已被注册")
                    .build();
        }
        
        // 检查商家名称是否已存在
        existingMerchant = merchantMapper.selectByMerchantName(registerDTO.getMerchantName());
        if (existingMerchant != null) {
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("商家名称已存在")
                    .build();
        }
        
        // 检查营业执照编号是否已存在
        existingMerchant = merchantMapper.selectByLicenseNumber(registerDTO.getLicenseNumber());
        if (existingMerchant != null) {
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("营业执照编号已被注册")
                    .build();
        }
        
        // 检查法人身份证号是否已存在
        existingMerchant = merchantMapper.selectByLegalPersonId(registerDTO.getLegalPersonId());
        if (existingMerchant != null) {
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("法人身份证号已被注册")
                    .build();
        }
        
        // 转换DTO到实体对象
        Merchant merchant = new Merchant();
        merchant.setMerchantName(registerDTO.getMerchantName());
        merchant.setMerchantType(registerDTO.getMerchantType());
        merchant.setEmail(registerDTO.getEmail());
        // 加密密码
        merchant.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        merchant.setLicenseNumber(registerDTO.getLicenseNumber());
        merchant.setLegalPersonName(registerDTO.getLegalPersonName());
        merchant.setLegalPersonId(registerDTO.getLegalPersonId());
        merchant.setContactName(registerDTO.getContactName());
        merchant.setContactPhone(registerDTO.getContactPhone());
        merchant.setContactEmail(registerDTO.getContactEmail());
        
        // 处理地址信息
        if (registerDTO.getAddressCodes() != null && registerDTO.getAddressCodes().size() >= 3) {
            merchant.setProvinceCode(registerDTO.getAddressCodes().get(0));
            merchant.setCityCode(registerDTO.getAddressCodes().get(1));
            merchant.setDistrictCode(registerDTO.getAddressCodes().get(2));
        }
        
        merchant.setDetailAddress(registerDTO.getDetailAddress());
        // 设置初始状态为未审核
        merchant.setStatus(0);
        
        try {
            // 插入新商家记录
            merchantMapper.insert(merchant);
            
            // 构建响应结果
            return MerchantResponseDTO.builder()
                    .success(true)
                    .message("注册成功，等待审核")
                    .merchantId(merchant.getId())
                    .merchantName(merchant.getMerchantName())
                    .merchantType(merchant.getMerchantType())
                    .email(merchant.getEmail())
                    .status(merchant.getStatus())
                    .build();
            
        } catch (Exception e) {
            log.error("注册商家失败", e);
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("注册失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 根据ID查询商家
     * 
     * @param id 商家ID
     * @return 商家信息
     */
    @Override
    public Merchant getMerchantById(Long id) {
        return merchantMapper.selectById(id);
    }
    
    /**
     * 根据邮箱查询商家
     * 
     * @param email 邮箱
     * @return 商家信息
     */
    @Override
    public Merchant getMerchantByEmail(String email) {
        return merchantMapper.selectByEmail(email);
    }
    
    /**
     * 更新商家状态
     * 
     * @param id 商家ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    @Transactional
    public boolean updateMerchantStatus(Long id, Integer status) {
        int rows = merchantMapper.updateStatus(id, status);
        return rows > 0;
    }
} 