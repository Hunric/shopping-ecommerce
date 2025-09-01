package com.hunric.common.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.common.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 */
@RestController
@RequestMapping("/api/verification")
@Slf4j
@ConditionalOnProperty(name = "spring.mail.host")
public class VerificationController {

    private final EmailService emailService;

    @Autowired
    public VerificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 发送验证码
     * @param email 邮箱地址
     * @param purpose 用途（login/register）
     * @return 响应结果
     */
    @GetMapping("/send")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(
            @RequestParam("email") String email,
            @RequestParam("purpose") String purpose) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 打印请求参数用于调试
            System.out.println("收到验证码请求: email=" + email + ", purpose=" + purpose);
            
            // 发送验证码，但不获取返回值
            emailService.sendVerificationCode(email, purpose);
            
            // 构建简单的响应，不包含LocalDateTime字段
            response.put("success", true);
            response.put("message", "验证码已发送到您的邮箱");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 打印完整的异常堆栈用于调试
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "发送验证码失败: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param purpose 用途（login/register）
     * @return 响应结果
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code,
            @RequestParam("purpose") String purpose) {
        
        Map<String, Object> response = new HashMap<>();
        System.out.println("验证码校验请求: email=" + email + ", code=" + code + ", purpose=" + purpose);
        
        try {
            boolean isValid = emailService.verifyCode(email, code, purpose);
            
            if (isValid) {
                // 验证成功后标记验证码为已使用
                emailService.markCodeAsUsed(email, purpose);
                
                response.put("success", true);
                response.put("message", "验证码验证成功");
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "验证码无效或已过期");
                
                return ResponseEntity.ok(response); // 返回200而不是400，让前端处理错误信息
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "验证失败: " + e.getMessage());
            
            return ResponseEntity.ok(response); // 返回200而不是400，让前端处理错误信息
        }
    }
} 