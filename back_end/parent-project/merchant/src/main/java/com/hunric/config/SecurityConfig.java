package com.hunric.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 安全配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())  // 禁用CSRF，前后端分离项目通常不需要
            .authorizeHttpRequests(auth -> auth
                // 允许访问注册和验证相关接口
                .requestMatchers("/api/merchant/register").permitAll()
                .requestMatchers("/api/verification/**").permitAll()
                .anyRequest().authenticated()  // 其他请求需要认证
            );
        
        return http.build();
    }
    
    /**
     * 配置跨域源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));  // 允许所有源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // 允许的HTTP方法
        configuration.setAllowedHeaders(Arrays.asList("*"));  // 允许所有头
        configuration.setAllowCredentials(false);  // 不允许发送凭证
        configuration.setMaxAge(3600L);  // 预检请求的有效期

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 对所有URL应用CORS配置
        
        return source;
    }
} 