package com.hunric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * 电商平台文件服务应用程序启动类
 * 
 * @description 文件服务微服务的主启动类，专门负责处理文件上传、下载、
 *              存储管理等功能。该服务独立于其他业务服务，提供统一的文件处理能力。
 * 
 * @features
 * - 图片文件上传和存储
 * - 文件访问URL生成
 * - 文件删除和管理
 * - 多种文件格式支持（JPG、PNG、GIF、WebP）
 * - 文件大小限制和验证
 * - 静态资源访问配置
 * - 跨域请求支持
 * 
 * @architecture
 * - 基于Spring Boot 3.x构建
 * - 无数据库依赖（排除DataSource自动配置）
 * - 无安全认证（排除Security自动配置）
 * - 独立的文件存储服务
 * - RESTful API设计
 * 
 * @exclusions
 * - DataSourceAutoConfiguration: 不需要数据库连接
 * - SecurityAutoConfiguration: 不需要安全认证
 * 
 * @storage
 * - 本地文件系统存储
 * - 可配置的上传目录
 * - 按类型分类存储
 * - 自动生成唯一文件名
 * 
 * @port 默认端口: 8082
 * 
 * @dependencies
 * - Spring Boot Starter Web
 * - Spring Boot Starter Validation
 * - Common Module (API响应模型)
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link org.springframework.boot.SpringApplication} Spring Boot启动类
 * @see {@link com.hunric.service.FileUploadService} 文件上传服务接口
 * @see {@link com.hunric.controller.FileUploadController} 文件上传控制器
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class FileServiceApplication {
    
    /**
     * 应用程序主入口方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(FileServiceApplication.class, args);
    }
}