package com.hunric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * 数据库迁移应用程序
 * 使用Flyway自动执行SQL脚本完成数据库初始化和迁移
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class DbMigrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbMigrationApplication.class, args);
        System.out.println("数据库迁移完成！");
    }
} 