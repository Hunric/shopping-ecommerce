package com.hunric.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 文件服务安全配置
 * 
 * @description 为文件服务提供基本的安全配置，允许所有请求访问
 * @author 开发团队
 * @version 1.0.0
 * @since 2026
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤器链
     * 允许所有请求访问，包括actuator健康检查端点
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // 允许所有请求
            )
            .csrf(csrf -> csrf.disable())  // 禁用CSRF保护
            .headers(headers -> headers.frameOptions().disable());  // 允许iframe嵌入
        
        return http.build();
    }
} 