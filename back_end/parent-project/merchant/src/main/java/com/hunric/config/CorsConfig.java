package com.hunric.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS跨域配置
 * 
 * @description 为商家服务提供跨域资源共享配置，支持开发环境和Docker生产环境
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    // 开发环境
                    "http://localhost:3000",           // 前端开发服务器
                    "http://127.0.0.1:3000",          // 本地IP
                    
                    // Docker环境
                    "http://localhost",                // Docker Nginx
                    "http://127.0.0.1",               // 本地IP Nginx
                    "http://frontend",                 // Docker容器名
                    "http://shopping-frontend",        // Docker容器名
                    
                    // 生产环境（根据实际域名配置）
                    "https://your-domain.com",         // 生产域名
                    "http://your-domain.com",          // 生产域名
                    
                    // 临时允许所有源（生产环境建议移除）
                    "*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders(
                    "Content-Type", 
                    "X-Requested-With", 
                    "accept", 
                    "Origin", 
                    "Access-Control-Request-Method",
                    "Access-Control-Request-Headers", 
                    "Authorization", 
                    "Cache-Control", 
                    "Pragma",
                    "X-Forwarded-For",
                    "X-Forwarded-Proto",
                    "X-Real-IP"
                )
                .exposedHeaders(
                    "Access-Control-Allow-Origin", 
                    "Access-Control-Allow-Credentials", 
                    "Authorization", 
                    "Content-Disposition",
                    "Content-Length",
                    "Content-Range"
                )
                .allowCredentials(true)
                .maxAge(3600);
    }
} 