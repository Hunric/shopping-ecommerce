package com.hunric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商家服务应用程序入口
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.hunric", "com.hunric.common"})
public class MerchantApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }
} 