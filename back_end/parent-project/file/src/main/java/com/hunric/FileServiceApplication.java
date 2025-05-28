package com.hunric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * 文件服务启动类
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class FileServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}