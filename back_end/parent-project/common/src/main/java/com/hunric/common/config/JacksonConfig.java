package com.hunric.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson配置类
 * 用于配置JSON序列化/反序列化的行为
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper以支持Java 8日期/时间类型
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java 8时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期表示为时间戳的功能
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
} 