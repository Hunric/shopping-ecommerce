package com.ecommerce.config; // 替换为您的实际包名

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 配置类
 */
@Configuration // 标记为配置类
@EnableWebSecurity // 启用 Spring Web Security
public class SecurityConfig {

    /**
     * 配置密码编码器 Bean
     * 使用 BCrypt 强哈希算法
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤器链 (基础配置)
     * - 允许访问注册和登录接口
     * - 其他请求需要认证
     * - 禁用 CSRF (因为我们使用 JWT 或无状态 API)
     * - TODO: 后续需要添加 JWT 过滤器配置
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // 允许匿名访问注册和登录 API (后续根据实际 Controller 路径调整)
                        .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 暂时使用 HTTP Basic 或表单登录 (后续会被 JWT 替代)
                .httpBasic(withDefaults()) // 或者 .formLogin(withDefaults())
                // 禁用 CSRF 防护，因为 REST API 通常是无状态的
                .csrf(csrf -> csrf.disable());
        // TODO: 添加 JWT 认证过滤器

        return http.build();
    }

    // TODO: 后续需要配置 AuthenticationManager, UserDetailsService 等用于实际登录认证
}
