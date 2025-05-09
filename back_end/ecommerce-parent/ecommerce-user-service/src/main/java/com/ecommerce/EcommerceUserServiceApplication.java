package com.ecommerce;

import org.mybatis.spring.annotation.MapperScan; // 导入 MapperScan 注解
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.geminishopping.ecommerce.mapper") // 指定 Mapper 接口所在的包
public class EcommerceUserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceUserServiceApplication.class, args);
    }
}