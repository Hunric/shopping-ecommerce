package com.hunric.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:3000",           // 开发环境前端
                    "http://localhost",                // Docker环境Nginx
                    "http://127.0.0.1:3000",          // 本地IP
                    "http://127.0.0.1",               // 本地IP Nginx
                    "http://frontend",                 // Docker容器名
                    "http://shopping-frontend",        // Docker容器名
                    "*"                                // 允许所有源
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition", "Content-Length", "Content-Range")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 额外的CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",           // 开发环境前端
            "http://localhost",                // Docker环境Nginx
            "http://127.0.0.1:3000",          // 本地IP
            "http://127.0.0.1",               // 本地IP Nginx
            "http://frontend",                 // Docker容器名
            "http://shopping-frontend",        // Docker容器名
            "*"                                // 允许所有源
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许发送凭证
        configuration.setAllowCredentials(true);
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Disposition",
            "Content-Length", 
            "Content-Range",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 