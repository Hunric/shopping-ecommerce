package com.hunric.service.impl;

import com.hunric.mapper.MerchantMapper;
import com.hunric.model.Merchant;
import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;
import com.hunric.model.dto.MerchantLoginDTO;
import com.hunric.model.dto.MerchantLoginResponseDTO;
import com.hunric.service.MerchantService;
import com.hunric.common.model.ApiResponse;
import com.hunric.common.service.EmailService;
import com.hunric.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
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
        existingMerchant = merchantMapper.selectByBusinessLicenseNo(registerDTO.getLicenseNumber());
        if (existingMerchant != null) {
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("营业执照编号已被注册")
                    .build();
        }
        
        // 检查法人身份证号是否已存在
        existingMerchant = merchantMapper.selectByLegalPersonIdCard(registerDTO.getLegalPersonId());
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
        merchant.setBusinessLicenseNo(registerDTO.getLicenseNumber());
        merchant.setLegalPersonName(registerDTO.getLegalPersonName());
        merchant.setLegalPersonIdCard(registerDTO.getLegalPersonId());
        merchant.setContactPersonName(registerDTO.getContactName());
        merchant.setContactPhone(registerDTO.getContactPhone());
        merchant.setContactEmail(registerDTO.getContactEmail());
        
        // 处理地址信息
        if (registerDTO.getAddressCodes() != null && registerDTO.getAddressCodes().size() >= 3) {
            merchant.setProvince(registerDTO.getAddressCodes().get(0));
            merchant.setCity(registerDTO.getAddressCodes().get(1));
            merchant.setDistrict(registerDTO.getAddressCodes().get(2));
        }
        
        merchant.setDetailedAddress(registerDTO.getDetailAddress());
        
        try {
            // 插入新商家记录
            merchantMapper.insert(merchant);
            
            // 构建响应结果
            return MerchantResponseDTO.builder()
                    .success(true)
                    .message("注册成功")
                    .merchantId(merchant.getMerchantId())
                    .merchantName(merchant.getMerchantName())
                    .merchantType(merchant.getMerchantType())
                    .email(merchant.getEmail())
                    .status(1) // 默认状态为正常，因为数据库表中没有状态字段
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
     * @param merchantId 商家ID
     * @return 商家信息
     */
    @Override
    public Merchant getMerchantById(Long merchantId) {
        return merchantMapper.selectById(merchantId);
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
     * 发送登录验证码
     * 
     * @param email 商家邮箱
     * @return 发送结果
     */
    @Override
    public ApiResponse<String> sendLoginVerificationCode(String email) {
        log.info("商家请求发送登录验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查商家是否存在
        Merchant merchant = merchantMapper.selectByEmail(email);
        if (merchant == null) {
            return ApiResponse.error("商家不存在，请先注册");
        }
        
        try {
            // 发送验证码
            String verificationCode = emailService.sendVerificationCode(email, "login");
            log.info("商家登录验证码发送成功: {}", email);
            return ApiResponse.success("验证码发送成功，请查收邮件");
        } catch (Exception e) {
            log.error("发送商家登录验证码失败: {}", email, e);
            return ApiResponse.error("验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证码登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @Override
    public MerchantLoginResponseDTO loginWithVerificationCode(MerchantLoginDTO loginDTO) {
        log.info("商家验证码登录: {}", loginDTO.getEmail());
        
        // 参数校验
        if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱不能为空")
                    .build();
        }
        
        if (loginDTO.getVerificationCode() == null || loginDTO.getVerificationCode().trim().isEmpty()) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("验证码不能为空")
                    .build();
        }
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!loginDTO.getEmail().matches(emailRegex)) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱格式不正确")
                    .build();
        }
        
        // 检查商家是否存在
        Merchant merchant = merchantMapper.selectByEmail(loginDTO.getEmail());
        if (merchant == null) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("商家不存在，请先注册")
                    .build();
        }
        

        
        // 验证验证码
        boolean isCodeValid = emailService.verifyCode(loginDTO.getEmail(), loginDTO.getVerificationCode(), "login");
        if (!isCodeValid) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("验证码错误或已过期")
                    .build();
        }
        
        try {
            // 生成JWT令牌
            String accessToken = jwtUtil.generateAccessToken(merchant.getMerchantId(), merchant.getEmail(), merchant.getMerchantName());
            String refreshToken = jwtUtil.generateRefreshToken(merchant.getMerchantId(), merchant.getEmail());
            long expiresIn = jwtUtil.getTokenRemainingTime(accessToken);
            
            // 标记验证码为已使用
            emailService.markCodeAsUsed(loginDTO.getEmail(), "login");
            
            log.info("商家登录成功: {}, ID: {}", merchant.getEmail(), merchant.getMerchantId());
            
            return MerchantLoginResponseDTO.builder()
                    .success(true)
                    .message("登录成功")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .merchantId(merchant.getMerchantId())
                    .merchantName(merchant.getMerchantName())
                    .email(merchant.getEmail())
                    .status(1) // 默认状态为正常，因为数据库表中没有状态字段
                    .build();
                    
        } catch (Exception e) {
            log.error("商家登录过程中发生异常: {}", loginDTO.getEmail(), e);
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("登录过程中发生异常，请稍后重试")
                    .build();
        }
    }
    
    /**
     * 获取商家信息
     * 
     * @param merchantId 商家ID
     * @return 商家信息
     */
    @Override
    public ApiResponse<Object> getMerchantInfo(Integer merchantId) {
        try {
            if (merchantId == null || merchantId <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }
            
            Merchant merchant = merchantMapper.selectById(merchantId.longValue());
            if (merchant == null) {
                return ApiResponse.error("商家不存在");
            }
            
            // 构建返回的商家信息（不包含敏感信息如密码）
            Map<String, Object> merchantInfo = new HashMap<>();
            merchantInfo.put("merchantId", merchant.getMerchantId());
            merchantInfo.put("merchantName", merchant.getMerchantName());
            merchantInfo.put("email", merchant.getEmail());
            merchantInfo.put("merchantType", merchant.getMerchantType());
            merchantInfo.put("businessLicenseNo", merchant.getBusinessLicenseNo());
            merchantInfo.put("legalPersonName", merchant.getLegalPersonName());
            merchantInfo.put("contactPersonName", merchant.getContactPersonName());
            merchantInfo.put("contactPhone", merchant.getContactPhone());
            merchantInfo.put("contactEmail", merchant.getContactEmail());
            merchantInfo.put("province", merchant.getProvince());
            merchantInfo.put("city", merchant.getCity());
            merchantInfo.put("district", merchant.getDistrict());
            merchantInfo.put("detailedAddress", merchant.getDetailedAddress());
            
            return ApiResponse.success(merchantInfo);
        } catch (Exception e) {
            log.error("获取商家信息失败: merchantId={}", merchantId, e);
            return ApiResponse.error("获取商家信息失败: " + e.getMessage());
        }
    }
} 