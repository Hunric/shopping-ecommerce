package com.hunric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 电商平台商家服务应用程序启动类
 * 
 * @description 商家服务微服务的主启动类，负责启动Spring Boot应用程序。
 *              该服务处理商家相关的业务逻辑，包括商家注册、登录、店铺管理、
 *              商品管理等核心功能。
 * 
 * @features
 * - 商家注册和认证
 * - 店铺信息管理
 * - 商品（SPU/SKU）管理
 * - 商品分类和属性管理
 * - JWT认证和授权
 * - 跨域请求支持
 * 
 * @architecture
 * - 基于Spring Boot 3.x构建
 * - 采用微服务架构设计
 * - 支持组件自动扫描和依赖注入
 * - 集成Spring Security安全框架
 * - 使用MySQL数据库存储
 * 
 * @components_scan
 * - com.hunric: 当前服务包
 * - com.hunric.common: 公共组件包
 * 
 * @port 默认端口: 8081
 * 
 * @dependencies
 * - Spring Boot Starter Web
 * - Spring Boot Starter Data JPA
 * - Spring Boot Starter Security
 * - MySQL Connector
 * - JWT Library
 * - Common Module
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link org.springframework.boot.SpringApplication} Spring Boot启动类
 * @see {@link com.hunric.common} 公共组件模块
 */
@SpringBootApplication(exclude = {FlywayAutoConfiguration.class})
@ComponentScan(basePackages = {"com.hunric", "com.hunric.common"})
public class MerchantApplication {
    
    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }
} 