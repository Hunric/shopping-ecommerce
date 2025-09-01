package com.hunric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 用户端购物应用启动类
 * 启用JPA和数据库连接
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.hunric.repository")
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}