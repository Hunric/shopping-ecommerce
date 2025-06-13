package com.hunric.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * 商家模块安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（因为我们使用JWT）
            .csrf(csrf -> csrf.disable())
            
            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置会话管理为无状态
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置授权规则
            .authorizeHttpRequests(authz -> authz
                // 允许访问的公开接口
                .requestMatchers(
                    "/api/merchant/register",                   // 商家注册
                    "/api/merchant/send-login-code",            // 发送登录验证码
                    "/api/merchant/login",                      // 验证码登录
                    "/api/merchant/login/password",             // 密码登录
                    "/api/merchant/send-reset-password-code",   // 发送重置密码验证码
                    "/api/merchant/verify-reset-password-code", // 验证重置密码验证码
                    "/api/merchant/reset-password",             // 重置密码
                    "/api/verification/**",                     // 验证码相关接口
                    "/api/test/**",                             // 测试接口
                    "/api/merchant/spu/template/download",      // 下载商品Excel模板
                    "/actuator/**",                             // 监控
                    "/error"                                    // 错误页面
                ).permitAll()
                
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            )
            
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源 - 支持开发环境和Docker环境
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",           // 开发环境前端
            "http://localhost",                // Docker环境Nginx
            "http://127.0.0.1:3000",          // 本地IP
            "http://127.0.0.1",               // 本地IP Nginx
            "http://frontend",                 // Docker容器名
            "http://shopping-frontend",        // Docker容器名
            "*"                                // 允许所有源（生产环境可以限制）
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type",
            "X-Requested-With",
            "accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Authorization",
            "Cache-Control",
            "Pragma"
        ));
        
        // 允许发送凭证
        configuration.setAllowCredentials(true);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization",
            "Content-Disposition"
        ));
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 