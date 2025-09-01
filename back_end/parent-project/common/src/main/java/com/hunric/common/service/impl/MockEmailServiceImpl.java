package com.hunric.common.service.impl;

import com.hunric.common.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 模拟邮件服务实现类 - 用于开发环境
 * 不发送真实邮件，而是在控制台输出验证码
 */
@Service
@Profile("test")  // 只在测试环境使用
public class MockEmailServiceImpl implements EmailService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis键前缀
    private static final String EMAIL_CODE_PREFIX = "email:code:";
    
    // 验证码过期时间（分钟）
    private static final int CODE_EXPIRATION_MINUTES = 5;
    
    // 日期时间格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    public MockEmailServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String sendVerificationCode(String to, String purpose) {
        try {
            System.out.println("=== 模拟邮件服务 ===");
            System.out.println("开始发送验证码: email=" + to + ", purpose=" + purpose);
            
            // 参数验证
            if (to == null || to.isEmpty()) {
                throw new IllegalArgumentException("邮箱地址不能为空");
            }
            if (purpose == null || purpose.isEmpty()) {
                throw new IllegalArgumentException("验证码用途不能为空");
            }
            
            // 生成6位验证码
            String code = generateRandomCode();
            
            // 准备当前时间和过期时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expireTime = now.plusMinutes(CODE_EXPIRATION_MINUTES);
            
            // 使用Map存储验证码信息，避免对象序列化问题
            Map<String, String> codeMap = new HashMap<>();
            codeMap.put("email", to);
            codeMap.put("code", code);
            codeMap.put("purpose", purpose);
            codeMap.put("createTime", now.format(DATE_FORMATTER));
            codeMap.put("expireTime", expireTime.format(DATE_FORMATTER));
            codeMap.put("used", "false");
            
            // 存储到Redis
            String redisKey = getRedisKey(to, purpose);
            System.out.println("Redis键: " + redisKey);
            
            try {
                redisTemplate.opsForValue().set(redisKey, codeMap, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
                System.out.println("验证码已存储到Redis");
            } catch (Exception e) {
                System.err.println("Redis存储失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Redis存储失败", e);
            }
            
            // 模拟发送邮件 - 在控制台输出
            System.out.println("==========================================");
            System.out.println("📧 模拟邮件发送");
            System.out.println("收件人: " + to);
            System.out.println("主题: " + getPurposeSubject(purpose));
            System.out.println("验证码: " + code);
            System.out.println("有效期: " + CODE_EXPIRATION_MINUTES + " 分钟");
            System.out.println("过期时间: " + expireTime.format(DATE_FORMATTER));
            System.out.println("==========================================");
            
            return code;
        } catch (Exception e) {
            System.err.println("验证码发送过程中出错: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean verifyCode(String email, String code, String purpose) {
        try {
            System.out.println("验证验证码: email=" + email + ", code=" + code + ", purpose=" + purpose);
            
            // 获取Redis中的验证码
            String redisKey = getRedisKey(email, purpose);
            Map<String, String> codeMap = (Map<String, String>) redisTemplate.opsForValue().get(redisKey);
            
            if (codeMap == null) {
                System.out.println("验证码不存在: " + redisKey);
                return false; // 验证码不存在
            }
            
            // 检查是否已使用
            if (Boolean.parseBoolean(codeMap.get("used"))) {
                System.out.println("验证码已使用: " + redisKey);
                return false; // 验证码已使用
            }
            
            // 检查是否过期
            String expireTimeStr = codeMap.get("expireTime");
            if (expireTimeStr != null) {
                LocalDateTime expireTime = LocalDateTime.parse(expireTimeStr, DATE_FORMATTER);
                if (LocalDateTime.now().isAfter(expireTime)) {
                    System.out.println("验证码已过期: " + redisKey);
                    redisTemplate.delete(redisKey);
                    return false; // 验证码已过期
                }
            }
            
            // 验证码匹配
            String storedCode = codeMap.get("code");
            boolean isValid = storedCode != null && storedCode.equals(code);
            System.out.println("验证码匹配结果: " + isValid + " for " + redisKey);
            
            // 如果验证成功，标记为已使用
            if (isValid) {
                markCodeAsUsed(email, purpose);
                System.out.println("验证码验证成功，已标记为已使用: " + redisKey);
            }
            
            return isValid;
        } catch (Exception e) {
            System.err.println("验证验证码时出错: " + e.getMessage());
            e.printStackTrace();
            return false; // 出错时返回验证失败
        }
    }

    @Override
    public void markCodeAsUsed(String email, String purpose) {
        try {
            String redisKey = getRedisKey(email, purpose);
            Map<String, String> codeMap = (Map<String, String>) redisTemplate.opsForValue().get(redisKey);
            
            if (codeMap != null) {
                codeMap.put("used", "true");
                redisTemplate.opsForValue().set(redisKey, codeMap, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
                System.out.println("验证码已标记为已使用: " + redisKey);
            }
        } catch (Exception e) {
            System.err.println("标记验证码为已使用时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 生成Redis键
     */
    private String getRedisKey(String email, String purpose) {
        return EMAIL_CODE_PREFIX + purpose + ":" + email;
    }
    
    /**
     * 根据用途获取邮件主题
     */
    private String getPurposeSubject(String purpose) {
        return switch (purpose) {
            case "login" -> "登录验证码";
            case "register" -> "注册验证码";
            case "resetPassword" -> "重置密码验证码";
            default -> "验证码";
        };
    }
    
    /**
     * 生成6位随机验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 生成100000-999999之间的数字
        return String.valueOf(code);
    }
} 