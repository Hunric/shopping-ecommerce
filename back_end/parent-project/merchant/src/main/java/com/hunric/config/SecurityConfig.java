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
                    "/error",                                   // 错误页面
                    "/dashboard/**",                            // 前端静态资源路由 - 仪表盘
                    "/merchant/dashboard/**",                   // 前端静态资源路由 - 仪表盘（带前缀）
                    "/merchant/order-detail/**",                // 前端静态资源路由 - 订单详情
                    "/merchant/product/**",                     // 前端静态资源路由 - 商品
                    "/merchant/store/**",                       // 前端静态资源路由 - 店铺
                    "/merchant/category/**",                    // 前端静态资源路由 - 分类
                    "/*.js", "/*.css", "/*.ico", "/*.png",      // 静态资源文件
                    "/assets/**",                               // 静态资源目录
                    "/"                                         // 根路径
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
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 