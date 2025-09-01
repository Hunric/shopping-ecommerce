package com.hunric.controller;

import com.hunric.model.dto.*;
import com.hunric.service.UserAuthService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/user/auth")
@Slf4j
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;
    
    /**
     * 发送注册验证码
     *
     * @param sendCodeDTO 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/send-register-code")
    public ResponseEntity<ApiResponse<String>> sendRegisterVerificationCode(@RequestBody UserSendCodeDTO sendCodeDTO) {
        log.info("接收到用户发送注册验证码请求: {}", sendCodeDTO.getEmail());
        
        try {
            // 参数校验
            if (sendCodeDTO.getEmail() == null || sendCodeDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
            }
            
            // 调用服务发送验证码
            ApiResponse<String> response = userAuthService.sendRegisterVerificationCode(sendCodeDTO.getEmail());
            
            // 记录发送结果
            if (response.isSuccess()) {
                log.info("用户注册验证码发送成功: {}", sendCodeDTO.getEmail());
            } else {
                log.warn("用户注册验证码发送失败: {}, 原因: {}", sendCodeDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送用户注册验证码异常", e);
            return ResponseEntity.ok(ApiResponse.error("发送验证码过程中发生异常: " + e.getMessage()));
        }
    }

    /**
     * 验证注册验证码
     *
     * @param params 请求参数
     * @return 验证结果
     */
    @PostMapping("/verify-register-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyRegisterCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String verificationCode = params.get("verificationCode");
        log.info("接收到验证注册验证码请求: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
        }
        
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("验证码不能为空"));
        }
        
        ApiResponse<Boolean> response = userAuthService.verifyRegisterCode(email, verificationCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册
     *
     * @param params 注册信息和验证码
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody Map<String, Object> params) {
        // 从Map中提取数据
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername((String) params.get("username"));
        registerDTO.setEmail((String) params.get("email"));
        registerDTO.setPassword((String) params.get("password"));
        registerDTO.setGender((String) params.get("gender"));
        registerDTO.setAvatarUrl((String) params.get("avatarUrl"));
        
        String verificationCode = (String) params.get("verificationCode");
        
        log.info("接收到用户注册请求: {}", registerDTO.getUsername());
        
        try {
            // 数据校验
            if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
                return ResponseEntity.ok(UserResponseDTO.builder()
                        .success(false)
                        .message("用户名不能为空")
                        .build());
            }
            
            if (registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(UserResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            // 邮箱格式验证
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!registerDTO.getEmail().matches(emailRegex)) {
                return ResponseEntity.ok(UserResponseDTO.builder()
                        .success(false)
                        .message("邮箱格式不正确")
                        .build());
            }
            
            // 密码验证
            if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.ok(UserResponseDTO.builder()
                        .success(false)
                        .message("密码不能为空")
                        .build());
            }
            
            if (registerDTO.getPassword().length() < 6) {
                return ResponseEntity.ok(UserResponseDTO.builder()
                        .success(false)
                        .message("密码长度至少为6位")
                        .build());
            }
            
            // 验证码验证
            if (verificationCode == null || verificationCode.trim().isEmpty()) {
                return ResponseEntity.ok(UserResponseDTO.builder()
                        .success(false)
                        .message("验证码不能为空")
                        .build());
            }
            
            // 调用服务进行注册
            UserResponseDTO response = userAuthService.register(registerDTO, verificationCode);
            
            // 记录注册结果
            if (response.getSuccess()) {
                log.info("用户注册成功: {}, ID: {}", registerDTO.getUsername(), response.getUserId());
            } else {
                log.warn("用户注册失败: {}, 原因: {}", registerDTO.getUsername(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return ResponseEntity.ok(UserResponseDTO.builder()
                    .success(false)
                    .message("注册过程中发生异常: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * 发送登录验证码
     * 
     * @param sendCodeDTO 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/send-login-code")
    public ResponseEntity<ApiResponse<String>> sendLoginVerificationCode(@RequestBody UserSendCodeDTO sendCodeDTO) {
        log.info("接收到用户发送登录验证码请求: {}", sendCodeDTO.getEmail());
        
        try {
            // 参数校验
            if (sendCodeDTO.getEmail() == null || sendCodeDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
            }
            
            // 调用服务发送验证码
            ApiResponse<String> response = userAuthService.sendLoginVerificationCode(sendCodeDTO.getEmail());
            
            // 记录发送结果
            if (response.isSuccess()) {
                log.info("用户登录验证码发送成功: {}", sendCodeDTO.getEmail());
            } else {
                log.warn("用户登录验证码发送失败: {}, 原因: {}", sendCodeDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送用户登录验证码异常", e);
            return ResponseEntity.ok(ApiResponse.error("发送验证码过程中发生异常: " + e.getMessage()));
        }
    }
    
    /**
     * 验证码登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> loginWithVerificationCode(@RequestBody UserLoginDTO loginDTO) {
        log.info("接收到用户验证码登录请求: {}", loginDTO.getEmail());
        
        try {
            // 参数校验
            if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(UserLoginResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            if (loginDTO.getVerificationCode() == null || loginDTO.getVerificationCode().trim().isEmpty()) {
                return ResponseEntity.ok(UserLoginResponseDTO.builder()
                        .success(false)
                        .message("验证码不能为空")
                        .build());
            }
            
            // 调用服务进行登录
            UserLoginResponseDTO response = userAuthService.loginWithVerificationCode(loginDTO);
            
            // 记录登录结果
            if (response.getSuccess()) {
                log.info("用户登录成功: {}, ID: {}", loginDTO.getEmail(), response.getUserId());
            } else {
                log.warn("用户登录失败: {}, 原因: {}", loginDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return ResponseEntity.ok(UserLoginResponseDTO.builder()
                    .success(false)
                    .message("登录过程中发生异常: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * 密码登录
     * 
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login/password")
    public ResponseEntity<UserLoginResponseDTO> loginWithPassword(@RequestBody UserPasswordLoginDTO loginDTO) {
        log.info("接收到用户密码登录请求: {}", loginDTO.getEmail());
        
        try {
            // 参数校验
            if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(UserLoginResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.ok(UserLoginResponseDTO.builder()
                        .success(false)
                        .message("密码不能为空")
                        .build());
            }
            
            // 调用服务进行登录
            UserLoginResponseDTO response = userAuthService.loginWithPassword(loginDTO);
            
            // 记录登录结果
            if (response.getSuccess()) {
                log.info("用户密码登录成功: {}, ID: {}", loginDTO.getEmail(), response.getUserId());
            } else {
                log.warn("用户密码登录失败: {}, 原因: {}", loginDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("用户密码登录异常", e);
            return ResponseEntity.ok(UserLoginResponseDTO.builder()
                    .success(false)
                    .message("登录过程中发生异常: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/info/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserInfo(@PathVariable Integer userId) {
        log.info("获取用户信息请求: userId={}", userId);
        
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("用户ID不能为空"));
            }
            
            // 调用服务获取用户信息
            ApiResponse<Object> response = userAuthService.getUserInfo(userId);
            
            if (response.isSuccess()) {
                log.info("获取用户信息成功: userId={}", userId);
            } else {
                log.warn("获取用户信息失败: userId={}, 原因: {}", userId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return ResponseEntity.ok(ApiResponse.error("获取用户信息过程中发生异常: " + e.getMessage()));
        }
    }

    /**
     * 发送重置密码验证码
     * 
     * @param params 请求参数
     * @return 发送结果
     */
    @PostMapping("/send-reset-password-code")
    public ResponseEntity<ApiResponse<String>> sendResetPasswordCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        log.info("接收到发送重置密码验证码请求: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
        }
        
        ApiResponse<String> response = userAuthService.sendResetPasswordVerificationCode(email);
        return ResponseEntity.ok(response);
    }

    /**
     * 验证重置密码验证码
     * 
     * @param params 请求参数
     * @return 验证结果
     */
    @PostMapping("/verify-reset-password-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyResetPasswordCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String verificationCode = params.get("verificationCode");
        log.info("接收到验证重置密码验证码请求: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
        }
        
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("验证码不能为空"));
        }
        
        ApiResponse<Boolean> response = userAuthService.verifyResetPasswordCode(email, verificationCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 重置密码
     * 
     * @param params 请求参数
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String newPassword = params.get("newPassword");
        String verificationCode = params.get("verificationCode");
        log.info("接收到重置密码请求: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("新密码不能为空"));
        }
        
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("验证码不能为空"));
        }
        
        // 验证密码长度
        if (newPassword.length() < 6) {
            return ResponseEntity.ok(ApiResponse.error("密码长度至少为6位"));
        }
        
        ApiResponse<Boolean> response = userAuthService.resetPassword(email, newPassword, verificationCode);
        return ResponseEntity.ok(response);
    }
}