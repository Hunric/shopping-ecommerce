package com.hunric.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
@Slf4j
public class JwtUtil {
    
    // JWT密钥
    @Value("${jwt.secret:hunric-shopping-ecommerce-jwt-secret-key-2024-very-long-secure-key-for-hs512-algorithm-minimum-512-bits-required}")
    private String jwtSecret;
    
    // JWT过期时间（毫秒）- 默认24小时
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    // 刷新令牌过期时间（毫秒）- 默认7天
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;
    
    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * 生成商家访问令牌
     * @param merchantId 商家ID
     * @param email 商家邮箱
     * @return JWT令牌
     */
    public String generateAccessToken(Long merchantId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("merchantId", merchantId);
        claims.put("email", email);
        claims.put("type", "access");
        claims.put("userType", "merchant");
        
        return createToken(claims, email, jwtExpiration);
    }
    
    /**
     * 生成商家刷新令牌
     * @param merchantId 商家ID
     * @param email 商家邮箱
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long merchantId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("merchantId", merchantId);
        claims.put("email", email);
        claims.put("type", "refresh");
        claims.put("userType", "merchant");
        
        return createToken(claims, email, refreshExpiration);
    }
    
    /**
     * 生成用户访问令牌
     * @param userId 用户ID
     * @param email 用户邮箱
     * @return JWT令牌
     */
    public String generateUserAccessToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "access");
        claims.put("userType", "user");
        
        return createToken(claims, email, jwtExpiration);
    }
    
    /**
     * 生成用户刷新令牌
     * @param userId 用户ID
     * @param email 用户邮箱
     * @return 刷新令牌
     */
    public String generateUserRefreshToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "refresh");
        claims.put("userType", "user");
        
        return createToken(claims, email, refreshExpiration);
    }
    
    /**
     * 创建令牌
     * @param claims 声明
     * @param subject 主题
     * @param expiration 过期时间
     * @return JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * 从令牌中获取邮箱
     * @param token JWT令牌
     * @return 邮箱
     */
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    /**
     * 从令牌中获取商家ID
     * @param token JWT令牌
     * @return 商家ID
     */
    public Long getMerchantIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("merchantId", Long.class);
    }
    
    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * 从令牌中获取用户类型
     * @param token JWT令牌
     * @return 用户类型 (user/merchant)
     */
    public String getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userType", String.class);
    }
    
    /**
     * 从令牌中获取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }
    
    /**
     * 从令牌中获取声明
     * @param token JWT令牌
     * @return 声明
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 检查令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 验证令牌
     * @param token JWT令牌
     * @param email 邮箱
     * @return 是否有效
     */
    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = getEmailFromToken(token);
            return (email.equals(tokenEmail) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证令牌（不检查邮箱）
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取令牌剩余有效时间（秒）
     * @param token JWT令牌
     * @return 剩余有效时间
     */
    public long getTokenRemainingTime(String token) {
        Date expiration = getExpirationDateFromToken(token);
        long now = System.currentTimeMillis();
        return Math.max(0, (expiration.getTime() - now) / 1000);
    }
} 