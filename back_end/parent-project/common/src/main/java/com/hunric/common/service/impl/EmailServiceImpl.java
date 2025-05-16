package com.hunric.common.service.impl;

import com.hunric.common.model.VerificationCode;
import com.hunric.common.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 邮件服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    // Redis键前缀
    private static final String EMAIL_CODE_PREFIX = "email:code:";
    
    // 验证码过期时间（分钟）
    private static final int CODE_EXPIRATION_MINUTES = 5;
    
    // 日期时间格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, RedisTemplate<String, Object> redisTemplate) {
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String sendVerificationCode(String to, String purpose) {
        try {
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
            System.out.println("生成的验证码: " + code);
            
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
                // 继续执行，至少尝试发送邮件
            }
            
            // 发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(getPurposeSubject(purpose));
            message.setText(getEmailContent(code, purpose));
            
            System.out.println("准备发送邮件");
            try {
                mailSender.send(message);
                System.out.println("邮件发送成功");
            } catch (Exception e) {
                System.err.println("邮件发送失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("邮件发送失败", e);
            }
            
            return code;
        } catch (Exception e) {
            System.err.println("验证码发送过程中出错: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常以便控制器能够捕获
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
     * 生成邮件内容
     */
    private String getEmailContent(String code, String purpose) {
        String purposeText = switch (purpose) {
            case "login" -> "登录";
            case "register" -> "注册";
            case "resetPassword" -> "重置密码";
            default -> "验证";
        };
        
        return "您的" + purposeText + "验证码是：" + code + "\n\n" +
               "验证码" + CODE_EXPIRATION_MINUTES + "分钟内有效，请勿泄露给他人。\n\n" +
               "如非本人操作，请忽略此邮件。";
    }
    
    /**
     * 生成随机验证码
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
} 