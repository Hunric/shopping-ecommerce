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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

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
                // 无需认证的路径
                .requestMatchers("/api/user/register").permitAll()
                .requestMatchers("/api/user/auth/register").permitAll()
                .requestMatchers("/api/user/send-login-code").permitAll()
                .requestMatchers("/api/user/auth/send-login-code").permitAll()
                .requestMatchers("/api/user/login").permitAll()
                .requestMatchers("/api/user/auth/login").permitAll()
                .requestMatchers("/api/user/login/password").permitAll()
                .requestMatchers("/api/user/auth/login/password").permitAll()
                .requestMatchers("/api/user/send-reset-password-code").permitAll()
                .requestMatchers("/api/user/auth/send-reset-password-code").permitAll()
                .requestMatchers("/api/user/verify-reset-password-code").permitAll()
                .requestMatchers("/api/user/auth/verify-reset-password-code").permitAll()
                .requestMatchers("/api/user/reset-password").permitAll()
                .requestMatchers("/api/user/auth/reset-password").permitAll()
                .requestMatchers("/api/user/products/**").permitAll()
                .requestMatchers("/api/user/categories/**").permitAll()
.requestMatchers("/api/user/shipping/**").permitAll()
                .requestMatchers("/api/user/orders/**").permitAll()
                .requestMatchers("/api/user/cart/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )

            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}