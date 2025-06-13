package com.hunric.service.impl;

import com.hunric.mapper.MerchantMapper;
import com.hunric.model.Merchant;
import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;
import com.hunric.model.dto.MerchantLoginDTO;
import com.hunric.model.dto.MerchantLoginResponseDTO;
import com.hunric.model.dto.MerchantResetPasswordDTO;
import com.hunric.model.dto.MerchantPasswordLoginDTO;
import com.hunric.model.dto.MerchantProfileDTO;
import com.hunric.model.dto.MerchantPasswordChangeDTO;
import com.hunric.service.MerchantService;
import com.hunric.common.model.ApiResponse;
import com.hunric.common.service.EmailService;
import com.hunric.common.util.JwtUtil;
import com.hunric.common.utils.RedisUtil;
import com.hunric.common.utils.CustomPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private RedisUtil redisUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // 密码最小长度
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    /**
     * 验证密码是否符合要求
     * 
     * @param password 密码
     * @return 验证结果
     */
    private boolean validatePassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
    
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
        
        // 验证密码
        if (!validatePassword(registerDTO.getPassword())) {
            return MerchantResponseDTO.builder()
                    .success(false)
                    .message("密码长度至少为6位")
                    .build();
        }
        
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
            // 第三个地址码作为区县(district)
            merchant.setDistrict(registerDTO.getAddressCodes().get(2));
            // county字段设置为空字符串或与district相同
            merchant.setCounty(registerDTO.getAddressCodes().get(2));
        } else {
            // 如果地址信息不完整，设置默认值
            merchant.setProvince("");
            merchant.setCity("");
            merchant.setCounty("");
            merchant.setDistrict("");
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
            // 生成6位随机验证码
            String verificationCode = String.format("%06d", (int)(Math.random() * 1000000));
            
            // 将验证码存入Redis，设置5分钟过期
            String redisKey = "merchant:login:code:" + email;
            redisUtil.setEx(redisKey, verificationCode, 300);
            
            // TODO: 发送验证码到邮箱
            log.info("向商家 {} 发送登录验证码: {}", email, verificationCode);
            
            return ApiResponse.success("验证码已发送");
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
        String redisKey = "merchant:login:code:" + loginDTO.getEmail();
        String storedCode = redisUtil.get(redisKey);
        
        if (storedCode == null || !storedCode.equals(loginDTO.getVerificationCode())) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("验证码错误或已过期")
                    .build();
        }
        
        // 删除已使用的验证码
        redisUtil.delete(redisKey);
        
        try {
            // 生成JWT令牌
            String accessToken = jwtUtil.generateAccessToken(merchant.getMerchantId(), merchant.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(merchant.getMerchantId(), merchant.getEmail());
            long expiresIn = jwtUtil.getTokenRemainingTime(accessToken);
            
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
     * 密码登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @Override
    public MerchantLoginResponseDTO loginWithPassword(MerchantPasswordLoginDTO loginDTO) {
        log.info("商家密码登录: {}", loginDTO.getEmail());
        
        // 参数校验
        if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱不能为空")
                    .build();
        }
        
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("密码不能为空")
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
        
        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), merchant.getPassword())) {
            return MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("密码错误")
                    .build();
        }
        
        try {
            // 生成JWT令牌
            String accessToken = jwtUtil.generateAccessToken(merchant.getMerchantId(), merchant.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(merchant.getMerchantId(), merchant.getEmail());
            long expiresIn = jwtUtil.getTokenRemainingTime(accessToken);
            
            log.info("商家密码登录成功: {}, ID: {}", merchant.getEmail(), merchant.getMerchantId());
            
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
            
            // 清除敏感信息
            merchant.setPassword(null);
            merchant.setLegalPersonIdCard(null);
            
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
    
    /**
     * 发送重置密码验证码
     * 
     * @param email 商家邮箱
     * @return 发送结果
     */
    @Override
    public ApiResponse<String> sendResetPasswordVerificationCode(String email) {
        log.info("商家请求发送重置密码验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查商家是否存在
        Merchant merchant = merchantMapper.selectByEmail(email);
        if (merchant == null) {
            return ApiResponse.error("账号不存在，请先注册");
        }
        
        try {
            // 发送验证码到邮箱并获取生成的验证码
            log.info("准备向商家 {} 发送重置密码验证码", email);
            String verificationCode = emailService.sendVerificationCode(email, "resetPassword");
            log.info("向商家 {} 发送的重置密码验证码: {}", email, verificationCode);
            
            // 将验证码存入Redis，设置10分钟过期
            String redisKey = "merchant:reset:code:" + email;
            redisUtil.setEx(redisKey, verificationCode, 600);
            
            return ApiResponse.success("验证码已发送到您的邮箱");
        } catch (Exception e) {
            log.error("发送商家重置密码验证码失败: {}", email, e);
            return ApiResponse.error("验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证重置密码验证码
     * 
     * @param email 商家邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    @Override
    public ApiResponse<Boolean> verifyResetPasswordCode(String email, String verificationCode) {
        log.info("验证商家重置密码验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查验证码是否为空
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ApiResponse.error("验证码不能为空");
        }
        
        // 检查商家是否存在
        Merchant merchant = merchantMapper.selectByEmail(email);
        if (merchant == null) {
            return ApiResponse.error("账号不存在，请先注册");
        }
        
        // 验证验证码
        String redisKey = "merchant:reset:code:" + email;
        String storedCode = redisUtil.get(redisKey);
        
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            return ApiResponse.error("验证码错误或已过期");
        }
        
        return ApiResponse.success(true, "验证成功");
    }
    
    /**
     * 重置密码
     * 
     * @param resetPasswordDTO 重置密码信息
     * @return 重置结果
     */
    @Override
    @Transactional
    public ApiResponse<Boolean> resetPassword(MerchantResetPasswordDTO resetPasswordDTO) {
        log.info("商家重置密码: {}", resetPasswordDTO.getEmail());
        
        // 参数校验
        if (resetPasswordDTO.getEmail() == null || resetPasswordDTO.getEmail().trim().isEmpty()) {
            return ApiResponse.error("邮箱不能为空");
        }
        
        if (resetPasswordDTO.getNewPassword() == null || resetPasswordDTO.getNewPassword().trim().isEmpty()) {
            return ApiResponse.error("新密码不能为空");
        }
        
        if (resetPasswordDTO.getVerificationCode() == null || resetPasswordDTO.getVerificationCode().trim().isEmpty()) {
            return ApiResponse.error("验证码不能为空");
        }
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!resetPasswordDTO.getEmail().matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 验证密码格式
        if (!validatePassword(resetPasswordDTO.getNewPassword())) {
            return ApiResponse.error("密码长度至少为6位");
        }
        
        // 检查商家是否存在
        Merchant merchant = merchantMapper.selectByEmail(resetPasswordDTO.getEmail());
        if (merchant == null) {
            return ApiResponse.error("账号不存在，请先注册");
        }
        
        // 验证验证码
        String redisKey = "merchant:reset:code:" + resetPasswordDTO.getEmail();
        String storedCode = redisUtil.get(redisKey);
        
        if (storedCode == null || !storedCode.equals(resetPasswordDTO.getVerificationCode())) {
            return ApiResponse.error("验证码错误或已过期");
        }
        
        try {
            // 更新密码
            merchant.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
            merchantMapper.updatePassword(merchant);
            
            // 删除已使用的验证码
            redisUtil.delete(redisKey);
            
            log.info("商家密码重置成功: {}", resetPasswordDTO.getEmail());
            return ApiResponse.success(true, "密码重置成功");
        } catch (Exception e) {
            log.error("商家密码重置失败: {}", resetPasswordDTO.getEmail(), e);
            return ApiResponse.error("密码重置失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Object> updateMerchantInfo(MerchantProfileDTO profileDTO) {
        try {
            if (profileDTO.getMerchantId() == null || profileDTO.getMerchantId() <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }
            
            // 校验是否存在
            Merchant existingMerchant = merchantMapper.selectById(profileDTO.getMerchantId());
            if (existingMerchant == null) {
                return ApiResponse.error("商家不存在");
            }
            
            // 如果修改了商家名称，需要校验唯一性
            if (!profileDTO.getMerchantName().equals(existingMerchant.getMerchantName())) {
                Merchant merchantByName = merchantMapper.selectByMerchantName(profileDTO.getMerchantName());
                if (merchantByName != null && !merchantByName.getMerchantId().equals(profileDTO.getMerchantId())) {
                    return ApiResponse.error("商家名称已存在");
                }
            }
            
            // 如果修改了营业执照编号，需要校验唯一性
            if (!profileDTO.getBusinessLicenseNo().equals(existingMerchant.getBusinessLicenseNo())) {
                Merchant merchantByLicense = merchantMapper.selectByBusinessLicenseNo(profileDTO.getBusinessLicenseNo());
                if (merchantByLicense != null && !merchantByLicense.getMerchantId().equals(profileDTO.getMerchantId())) {
                    return ApiResponse.error("营业执照编号已存在");
                }
            }
            
            // 构建更新对象
            Merchant merchant = new Merchant();
            merchant.setMerchantId(profileDTO.getMerchantId());
            merchant.setMerchantName(profileDTO.getMerchantName());
            merchant.setMerchantType(profileDTO.getMerchantType());
            merchant.setBusinessLicenseNo(profileDTO.getBusinessLicenseNo());
            merchant.setLegalPersonName(profileDTO.getLegalPersonName());
            merchant.setContactPersonName(profileDTO.getContactPersonName());
            merchant.setContactPhone(profileDTO.getContactPhone());
            merchant.setContactEmail(profileDTO.getContactEmail());
            merchant.setProvince(profileDTO.getProvince());
            merchant.setCity(profileDTO.getCity());
            merchant.setDistrict(profileDTO.getDistrict());
            merchant.setDetailedAddress(profileDTO.getDetailedAddress());
            
            // 邮箱不允许修改，确保原始邮箱保持不变
            merchant.setEmail(existingMerchant.getEmail());
            
            // 执行更新
            int result = merchantMapper.update(merchant);
            if (result > 0) {
                return ApiResponse.success("商家信息更新成功");
            } else {
                return ApiResponse.error("商家信息更新失败");
            }
        } catch (Exception e) {
            log.error("更新商家信息失败: merchantId={}", profileDTO.getMerchantId(), e);
            return ApiResponse.error("更新商家信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Object> changePassword(MerchantPasswordChangeDTO passwordChangeDTO) {
        try {
            if (passwordChangeDTO.getMerchantId() == null || passwordChangeDTO.getMerchantId() <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }
            
            // 校验商家是否存在
            Merchant merchant = merchantMapper.selectById(passwordChangeDTO.getMerchantId());
            if (merchant == null) {
                return ApiResponse.error("商家不存在");
            }
            
            // 校验当前密码是否正确
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (!encoder.matches(passwordChangeDTO.getCurrentPassword(), merchant.getPassword())) {
                return ApiResponse.error("当前密码不正确");
            }
            
            // 校验新密码强度
            if (passwordChangeDTO.getNewPassword().length() < 8) {
                return ApiResponse.error("新密码长度至少8位");
            }
            
            // 加密新密码
            String encodedPassword = encoder.encode(passwordChangeDTO.getNewPassword());
            
            // 更新密码
            merchant.setPassword(encodedPassword);
            int result = merchantMapper.updatePassword(merchant);
            
            if (result > 0) {
                return ApiResponse.success("密码修改成功");
            } else {
                return ApiResponse.error("密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改密码失败: merchantId={}", passwordChangeDTO.getMerchantId(), e);
            return ApiResponse.error("修改密码失败: " + e.getMessage());
        }
    }
} 