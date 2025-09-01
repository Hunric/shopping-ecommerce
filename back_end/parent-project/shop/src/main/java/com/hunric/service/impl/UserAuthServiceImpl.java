package com.hunric.service.impl;

import com.hunric.model.UserInfo;
import com.hunric.model.dto.*;
import com.hunric.repository.UserInfoRepository;
import com.hunric.service.UserAuthService;
import com.hunric.common.model.ApiResponse;
import com.hunric.common.service.EmailService;
import com.hunric.common.util.JwtUtil;
import com.hunric.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户认证服务实现类
 */
@Service
@Slf4j
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    
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
     * 发送注册验证码
     *
     * @param email 用户邮箱
     * @return 发送结果
     */
    @Override
    public ApiResponse<String> sendRegisterVerificationCode(String email) {
        log.info("用户请求发送注册验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查邮箱是否已被注册
        if (userInfoRepository.existsByEmail(email)) {
            return ApiResponse.error("邮箱已被注册");
        }
        
        try {
            // 发送验证码到邮箱并获取生成的验证码
            log.info("准备向用户 {} 发送注册验证码", email);
            String verificationCode = emailService.sendVerificationCode(email, "register");
            log.info("向用户 {} 发送的注册验证码: {}", email, verificationCode);
            
            return ApiResponse.success("验证码已发送到您的邮箱");
        } catch (Exception e) {
            log.error("发送用户注册验证码失败: {}", email, e);
            return ApiResponse.error("验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证注册验证码
     *
     * @param email 用户邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    @Override
    public ApiResponse<Boolean> verifyRegisterCode(String email, String verificationCode) {
        log.info("验证用户注册验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查验证码是否为空
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ApiResponse.error("验证码不能为空");
        }
        
        // 使用EmailService验证验证码
        try {
            boolean isValid = emailService.verifyCode(email, verificationCode, "register");
            
            if (isValid) {
                return ApiResponse.success(true, "验证成功");
            } else {
                return ApiResponse.error("验证码错误或已过期");
            }
        } catch (Exception e) {
            log.error("验证用户注册验证码失败: {}", email, e);
            return ApiResponse.error("验证码验证失败");
        }
    }
    
    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     * @param verificationCode 验证码
     * @return 注册结果
     */
    @Override
    @Transactional
    public UserResponseDTO register(UserRegisterDTO registerDTO, String verificationCode) {
        log.info("注册新用户: {}", registerDTO.getUsername());
        
        // 验证密码
        if (!validatePassword(registerDTO.getPassword())) {
            return UserResponseDTO.builder()
                    .success(false)
                    .message("密码长度至少为6位")
                    .build();
        }
        
        // 验证验证码
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return UserResponseDTO.builder()
                    .success(false)
                    .message("验证码不能为空")
                    .build();
        }
        
        // 使用EmailService验证验证码
        try {
            boolean isValid = emailService.verifyCode(registerDTO.getEmail(), verificationCode, "register");
            if (!isValid) {
                return UserResponseDTO.builder()
                        .success(false)
                        .message("验证码错误或已过期")
                        .build();
            }
        } catch (Exception e) {
            log.error("验证用户注册验证码失败: {}", registerDTO.getEmail(), e);
            return UserResponseDTO.builder()
                    .success(false)
                    .message("验证码验证失败")
                    .build();
        }
        
        // 检查邮箱是否已被注册
        if (userInfoRepository.existsByEmail(registerDTO.getEmail())) {
            return UserResponseDTO.builder()
                    .success(false)
                    .message("邮箱已被注册")
                    .build();
        }
        
        // 检查用户名是否已存在
        if (userInfoRepository.existsByUsername(registerDTO.getUsername())) {
            return UserResponseDTO.builder()
                    .success(false)
                    .message("用户名已存在")
                    .build();
        }
        
        try {
            // 创建新用户
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(registerDTO.getUsername());
            userInfo.setEmail(registerDTO.getEmail());
            userInfo.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            userInfo.setGender(registerDTO.getGender());
            userInfo.setAvatarUrl(registerDTO.getAvatarUrl() != null ? 
                registerDTO.getAvatarUrl() : "default_avatar_url");
            
            // 保存用户
            UserInfo savedUser = userInfoRepository.save(userInfo);
            
            // 验证码已在EmailService.verifyCode中自动删除，无需手动删除
            
            log.info("用户注册成功: {}, ID: {}", registerDTO.getUsername(), savedUser.getUserId());
            
            return UserResponseDTO.builder()
                    .success(true)
                    .message("注册成功")
                    .userId(savedUser.getUserId())
                    .username(savedUser.getUsername())
                    .email(savedUser.getEmail())
                    .gender(savedUser.getGender())
                    .avatarUrl(savedUser.getAvatarUrl())
                    .build();
                    
        } catch (Exception e) {
            log.error("注册用户失败", e);
            return UserResponseDTO.builder()
                    .success(false)
                    .message("注册失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 发送登录验证码
     * 
     * @param email 用户邮箱
     * @return 发送结果
     */
    @Override
    public ApiResponse<String> sendLoginVerificationCode(String email) {
        log.info("用户请求发送登录验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查用户是否存在
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("用户不存在，请先注册");
        }
        
        try {
            // 发送验证码到邮箱并获取生成的验证码
            log.info("准备向用户 {} 发送登录验证码", email);
            String verificationCode = emailService.sendVerificationCode(email, "login");
            log.info("向用户 {} 发送的登录验证码: {}", email, verificationCode);
            
            // 将验证码存入Redis，设置5分钟过期
            String redisKey = "user:login:code:" + email;
            redisUtil.setEx(redisKey, verificationCode, 300);
            
            return ApiResponse.success("验证码已发送");
        } catch (Exception e) {
            log.error("发送用户登录验证码失败: {}", email, e);
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
    public UserLoginResponseDTO loginWithVerificationCode(UserLoginDTO loginDTO) {
        log.info("用户验证码登录: {}", loginDTO.getEmail());
        
        // 参数校验
        if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱不能为空")
                    .build();
        }
        
        if (loginDTO.getVerificationCode() == null || loginDTO.getVerificationCode().trim().isEmpty()) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("验证码不能为空")
                    .build();
        }
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!loginDTO.getEmail().matches(emailRegex)) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱格式不正确")
                    .build();
        }
        
        // 检查用户是否存在
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(loginDTO.getEmail());
        if (userOptional.isEmpty()) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("用户不存在，请先注册")
                    .build();
        }
        
        UserInfo userInfo = userOptional.get();
        
        // 验证验证码
        String redisKey = "user:login:code:" + loginDTO.getEmail();
        String storedCode = redisUtil.get(redisKey);
        
        if (storedCode == null || !storedCode.equals(loginDTO.getVerificationCode())) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("验证码错误或已过期")
                    .build();
        }
        
        // 删除已使用的验证码
        redisUtil.delete(redisKey);
        
        try {
            // 生成JWT令牌
            String accessToken = jwtUtil.generateUserAccessToken(userInfo.getUserId().longValue(), userInfo.getEmail());
            String refreshToken = jwtUtil.generateUserRefreshToken(userInfo.getUserId().longValue(), userInfo.getEmail());
            long expiresIn = jwtUtil.getTokenRemainingTime(accessToken);
            
            log.info("用户登录成功: {}, ID: {}", userInfo.getEmail(), userInfo.getUserId());
            
            return UserLoginResponseDTO.builder()
                    .success(true)
                    .message("登录成功")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .userId(userInfo.getUserId())
                    .username(userInfo.getUsername())
                    .email(userInfo.getEmail())
                    .gender(userInfo.getGender())
                    .avatarUrl(userInfo.getAvatarUrl())
                    .build();
                    
        } catch (Exception e) {
            log.error("用户登录过程中发生异常: {}", loginDTO.getEmail(), e);
            return UserLoginResponseDTO.builder()
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
    public UserLoginResponseDTO loginWithPassword(UserPasswordLoginDTO loginDTO) {
        log.info("用户密码登录: {}", loginDTO.getEmail());
        
        // 参数校验
        if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱不能为空")
                    .build();
        }
        
        if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("密码不能为空")
                    .build();
        }
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!loginDTO.getEmail().matches(emailRegex)) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("邮箱格式不正确")
                    .build();
        }
        
        // 检查用户是否存在
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(loginDTO.getEmail());
        if (userOptional.isEmpty()) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("用户不存在，请先注册")
                    .build();
        }
        
        UserInfo userInfo = userOptional.get();
        
        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), userInfo.getPassword())) {
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("密码错误")
                    .build();
        }
        
        try {
            // 生成JWT令牌
            String accessToken = jwtUtil.generateUserAccessToken(userInfo.getUserId().longValue(), userInfo.getEmail());
            String refreshToken = jwtUtil.generateUserRefreshToken(userInfo.getUserId().longValue(), userInfo.getEmail());
            long expiresIn = jwtUtil.getTokenRemainingTime(accessToken);
            
            log.info("用户密码登录成功: {}, ID: {}", userInfo.getEmail(), userInfo.getUserId());
            
            return UserLoginResponseDTO.builder()
                    .success(true)
                    .message("登录成功")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .userId(userInfo.getUserId())
                    .username(userInfo.getUsername())
                    .email(userInfo.getEmail())
                    .gender(userInfo.getGender())
                    .avatarUrl(userInfo.getAvatarUrl())
                    .build();
                    
        } catch (Exception e) {
            log.error("用户登录过程中发生异常: {}", loginDTO.getEmail(), e);
            return UserLoginResponseDTO.builder()
                    .success(false)
                    .message("登录过程中发生异常，请稍后重试")
                    .build();
        }
    }
    
    /**
     * 发送重置密码验证码
     * 
     * @param email 用户邮箱
     * @return 发送结果
     */
    @Override
    public ApiResponse<String> sendResetPasswordVerificationCode(String email) {
        log.info("用户请求发送重置密码验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查用户是否存在
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("账号不存在，请先注册");
        }
        
        try {
            // 发送验证码到邮箱并获取生成的验证码
            log.info("准备向用户 {} 发送重置密码验证码", email);
            String verificationCode = emailService.sendVerificationCode(email, "resetPassword");
            log.info("向用户 {} 发送的重置密码验证码: {}", email, verificationCode);
            
            // 将验证码存入Redis，设置10分钟过期
            String redisKey = "user:reset:code:" + email;
            redisUtil.setEx(redisKey, verificationCode, 600);
            
            return ApiResponse.success("验证码已发送到您的邮箱");
        } catch (Exception e) {
            log.error("发送用户重置密码验证码失败: {}", email, e);
            return ApiResponse.error("验证码发送失败，请稍后重试");
        }
    }
    
    /**
     * 验证重置密码验证码
     * 
     * @param email 用户邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    @Override
    public ApiResponse<Boolean> verifyResetPasswordCode(String email, String verificationCode) {
        log.info("验证用户重置密码验证码: {}", email);
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 检查验证码是否为空
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ApiResponse.error("验证码不能为空");
        }
        
        // 检查用户是否存在
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("账号不存在，请先注册");
        }
        
        // 验证验证码
        String redisKey = "user:reset:code:" + email;
        String storedCode = redisUtil.get(redisKey);
        
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            return ApiResponse.error("验证码错误或已过期");
        }
        
        return ApiResponse.success(true, "验证成功");
    }
    
    /**
     * 重置密码
     * 
     * @param email 用户邮箱
     * @param newPassword 新密码
     * @param verificationCode 验证码
     * @return 重置结果
     */
    @Override
    @Transactional
    public ApiResponse<Boolean> resetPassword(String email, String newPassword, String verificationCode) {
        log.info("用户重置密码: {}", email);
        
        // 参数校验
        if (email == null || email.trim().isEmpty()) {
            return ApiResponse.error("邮箱不能为空");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ApiResponse.error("新密码不能为空");
        }
        
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ApiResponse.error("验证码不能为空");
        }
        
        // 验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            return ApiResponse.error("邮箱格式不正确");
        }
        
        // 验证密码格式
        if (!validatePassword(newPassword)) {
            return ApiResponse.error("密码长度至少为6位");
        }
        
        // 检查用户是否存在
        Optional<UserInfo> userOptional = userInfoRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("账号不存在，请先注册");
        }
        
        UserInfo userInfo = userOptional.get();
        
        // 验证验证码
        String redisKey = "user:reset:code:" + email;
        String storedCode = redisUtil.get(redisKey);
        
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            return ApiResponse.error("验证码错误或已过期");
        }
        
        try {
            // 更新密码
            userInfo.setPassword(passwordEncoder.encode(newPassword));
            userInfoRepository.save(userInfo);
            
            // 删除已使用的验证码
            redisUtil.delete(redisKey);
            
            log.info("用户密码重置成功: {}", email);
            return ApiResponse.success(true, "密码重置成功");
        } catch (Exception e) {
            log.error("用户密码重置失败: {}", email, e);
            return ApiResponse.error("密码重置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public ApiResponse<Object> getUserInfo(Integer userId) {
        try {
            if (userId == null || userId <= 0) {
                return ApiResponse.error("用户ID不能为空");
            }
            
            Optional<UserInfo> userOptional = userInfoRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("用户不存在");
            }
            
            UserInfo userInfo = userOptional.get();
            
            // 构建返回的用户信息（不包含敏感信息如密码）
            Map<String, Object> userInfoMap = new HashMap<>();
            userInfoMap.put("userId", userInfo.getUserId());
            userInfoMap.put("username", userInfo.getUsername());
            userInfoMap.put("email", userInfo.getEmail());
            userInfoMap.put("gender", userInfo.getGender());
            userInfoMap.put("avatarUrl", userInfo.getAvatarUrl());
            
            return ApiResponse.success(userInfoMap);
        } catch (Exception e) {
            log.error("获取用户信息失败: userId={}", userId, e);
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }
}