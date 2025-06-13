package com.hunric.controller;

import com.hunric.model.dto.MerchantRegisterDTO;
import com.hunric.model.dto.MerchantResponseDTO;
import com.hunric.model.dto.MerchantSendCodeDTO;
import com.hunric.model.dto.MerchantLoginDTO;
import com.hunric.model.dto.MerchantLoginResponseDTO;
import com.hunric.model.dto.MerchantPasswordLoginDTO;
import com.hunric.model.dto.MerchantResetPasswordDTO;
import com.hunric.model.dto.MerchantPasswordChangeDTO;
import com.hunric.model.dto.MerchantProfileDTO;
import com.hunric.service.MerchantService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商家控制器
 */
@RestController
@RequestMapping("/api/merchant")
@Slf4j
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    
    /**
     * 商家注册
     * 
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<MerchantResponseDTO> register(@RequestBody MerchantRegisterDTO registerDTO) {
        log.info("接收到商家注册请求: {}", registerDTO.getMerchantName());
        
        try {
            // 数据校验
            if (registerDTO.getMerchantName() == null || registerDTO.getMerchantName().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("商家名称不能为空")
                        .build());
            }
            
            if (registerDTO.getEmail() == null || registerDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            // 邮箱格式验证
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            if (!registerDTO.getEmail().matches(emailRegex)) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("邮箱格式不正确")
                        .build());
            }
            
            // 密码验证
            if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("密码不能为空")
                        .build());
            }
            
            if (registerDTO.getPassword().length() < 6) {
                return ResponseEntity.ok(MerchantResponseDTO.builder()
                        .success(false)
                        .message("密码长度至少为6位")
                        .build());
            }
            
            // 调用服务进行注册
            MerchantResponseDTO response = merchantService.register(registerDTO);
            
            // 记录注册结果
            if (response.getSuccess()) {
                log.info("商家注册成功: {}, ID: {}", registerDTO.getMerchantName(), response.getMerchantId());
            } else {
                log.warn("商家注册失败: {}, 原因: {}", registerDTO.getMerchantName(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("商家注册异常", e);
            return ResponseEntity.ok(MerchantResponseDTO.builder()
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
    public ResponseEntity<ApiResponse<String>> sendLoginVerificationCode(@RequestBody MerchantSendCodeDTO sendCodeDTO) {
        log.info("接收到商家发送登录验证码请求: {}", sendCodeDTO.getEmail());
        
        try {
            // 参数校验
            if (sendCodeDTO.getEmail() == null || sendCodeDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
            }
            
            // 调用服务发送验证码
            ApiResponse<String> response = merchantService.sendLoginVerificationCode(sendCodeDTO.getEmail());
            
            // 记录发送结果
            if (response.isSuccess()) {
                log.info("商家登录验证码发送成功: {}", sendCodeDTO.getEmail());
            } else {
                log.warn("商家登录验证码发送失败: {}, 原因: {}", sendCodeDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("发送商家登录验证码异常", e);
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
    public ResponseEntity<MerchantLoginResponseDTO> loginWithVerificationCode(@RequestBody MerchantLoginDTO loginDTO) {
        log.info("接收到商家验证码登录请求: {}", loginDTO.getEmail());
        
        try {
            // 参数校验
            if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantLoginResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            if (loginDTO.getVerificationCode() == null || loginDTO.getVerificationCode().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantLoginResponseDTO.builder()
                        .success(false)
                        .message("验证码不能为空")
                        .build());
            }
            
            // 调用服务进行登录
            MerchantLoginResponseDTO response = merchantService.loginWithVerificationCode(loginDTO);
            
            // 记录登录结果
            if (response.getSuccess()) {
                log.info("商家登录成功: {}, ID: {}", loginDTO.getEmail(), response.getMerchantId());
            } else {
                log.warn("商家登录失败: {}, 原因: {}", loginDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("商家登录异常", e);
            return ResponseEntity.ok(MerchantLoginResponseDTO.builder()
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
    public ResponseEntity<MerchantLoginResponseDTO> loginWithPassword(@RequestBody MerchantPasswordLoginDTO loginDTO) {
        log.info("接收到商家密码登录请求: {}", loginDTO.getEmail());
        
        try {
            // 参数校验
            if (loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantLoginResponseDTO.builder()
                        .success(false)
                        .message("邮箱不能为空")
                        .build());
            }
            
            if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
                return ResponseEntity.ok(MerchantLoginResponseDTO.builder()
                        .success(false)
                        .message("密码不能为空")
                        .build());
            }
            
            // 调用服务进行登录
            MerchantLoginResponseDTO response = merchantService.loginWithPassword(loginDTO);
            
            // 记录登录结果
            if (response.getSuccess()) {
                log.info("商家密码登录成功: {}, ID: {}", loginDTO.getEmail(), response.getMerchantId());
            } else {
                log.warn("商家密码登录失败: {}, 原因: {}", loginDTO.getEmail(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("商家密码登录异常", e);
            return ResponseEntity.ok(MerchantLoginResponseDTO.builder()
                    .success(false)
                    .message("登录过程中发生异常: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * 获取商家信息
     * 
     * @param merchantId 商家ID
     * @return 商家信息
     */
    @GetMapping("/info/{merchantId}")
    public ResponseEntity<ApiResponse<Object>> getMerchantInfo(@PathVariable Integer merchantId) {
        log.info("获取商家信息请求: merchantId={}", merchantId);
        
        try {
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
            // 调用服务获取商家信息
            ApiResponse<Object> response = merchantService.getMerchantInfo(merchantId);
            
            if (response.isSuccess()) {
                log.info("获取商家信息成功: merchantId={}", merchantId);
            } else {
                log.warn("获取商家信息失败: merchantId={}, 原因: {}", merchantId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取商家信息异常", e);
            return ResponseEntity.ok(ApiResponse.error("获取商家信息过程中发生异常: " + e.getMessage()));
        }
    }

    /**
     * 更新商家信息
     * 
     * @param merchantId 商家ID
     * @param profileDTO 更新的商家信息
     * @return 更新结果
     */
    @PutMapping("/info/{merchantId}")
    public ResponseEntity<ApiResponse<Object>> updateMerchantInfo(
            @PathVariable Integer merchantId,
            @RequestBody MerchantProfileDTO profileDTO) {
        
        log.info("更新商家信息请求: merchantId={}", merchantId);
        
        try {
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
            // 确保DTO中的ID与路径参数一致
            profileDTO.setMerchantId(merchantId.longValue());
            
            // 调用服务更新商家信息
            ApiResponse<Object> response = merchantService.updateMerchantInfo(profileDTO);
            
            if (response.isSuccess()) {
                log.info("更新商家信息成功: merchantId={}", merchantId);
            } else {
                log.warn("更新商家信息失败: merchantId={}, 原因: {}", merchantId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新商家信息异常", e);
            return ResponseEntity.ok(ApiResponse.error("更新商家信息过程中发生异常: " + e.getMessage()));
        }
    }
    
    /**
     * 修改商家密码
     * 
     * @param passwordChangeDTO 密码修改信息
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(
            @RequestBody MerchantPasswordChangeDTO passwordChangeDTO) {
        
        log.info("修改商家密码请求: merchantId={}", passwordChangeDTO.getMerchantId());
        
        try {
            if (passwordChangeDTO.getMerchantId() == null || passwordChangeDTO.getMerchantId() <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
            if (passwordChangeDTO.getCurrentPassword() == null || passwordChangeDTO.getCurrentPassword().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("当前密码不能为空"));
            }
            
            if (passwordChangeDTO.getNewPassword() == null || passwordChangeDTO.getNewPassword().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("新密码不能为空"));
            }
            
            // 调用服务修改密码
            ApiResponse<Object> response = merchantService.changePassword(passwordChangeDTO);
            
            if (response.isSuccess()) {
                log.info("修改商家密码成功: merchantId={}", passwordChangeDTO.getMerchantId());
            } else {
                log.warn("修改商家密码失败: merchantId={}, 原因: {}", 
                        passwordChangeDTO.getMerchantId(), response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("修改商家密码异常", e);
            return ResponseEntity.ok(ApiResponse.error("修改商家密码过程中发生异常: " + e.getMessage()));
        }
    }

    /**
     * 发送重置密码验证码
     * 
     * @param email 商家邮箱
     * @return 发送结果
     */
    @PostMapping("/send-reset-password-code")
    public ResponseEntity<ApiResponse<String>> sendResetPasswordCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        log.info("接收到发送重置密码验证码请求: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
        }
        
        ApiResponse<String> response = merchantService.sendResetPasswordVerificationCode(email);
        return ResponseEntity.ok(response);
    }

    /**
     * 验证重置密码验证码
     * 
     * @param email 商家邮箱
     * @param verificationCode 验证码
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
        
        ApiResponse<Boolean> response = merchantService.verifyResetPasswordCode(email, verificationCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 重置密码
     * 
     * @param resetPasswordDTO 重置密码信息
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(@RequestBody MerchantResetPasswordDTO resetPasswordDTO) {
        log.info("接收到重置密码请求: {}", resetPasswordDTO.getEmail());
        
        if (resetPasswordDTO.getEmail() == null || resetPasswordDTO.getEmail().trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("邮箱不能为空"));
        }
        
        if (resetPasswordDTO.getNewPassword() == null || resetPasswordDTO.getNewPassword().trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("新密码不能为空"));
        }
        
        if (resetPasswordDTO.getVerificationCode() == null || resetPasswordDTO.getVerificationCode().trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("验证码不能为空"));
        }
        
        // 验证密码长度
        if (resetPasswordDTO.getNewPassword().length() < 6) {
            return ResponseEntity.ok(ApiResponse.error("密码长度至少为6位"));
        }
        
        ApiResponse<Boolean> response = merchantService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(response);
    }
} 