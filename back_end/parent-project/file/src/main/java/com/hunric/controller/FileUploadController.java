package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 电商平台文件上传控制器
 * 
 * @description 处理文件上传相关的HTTP请求，提供图片上传、文件删除等功能。
 *              支持多种图片格式，包含文件验证、大小限制、类型检查等安全措施。
 * 
 * @features
 * - 图片文件上传（JPG、PNG、GIF、WebP）
 * - 文件大小限制（最大5MB）
 * - 文件类型验证和安全检查
 * - 文件删除功能
 * - 详细的日志记录
 * - 统一的错误处理和响应格式
 * - 支持不同类型的文件分类存储
 * 
 * @endpoints
 * - POST /api/upload/image: 上传图片文件
 * - DELETE /api/upload/delete: 删除已上传的文件
 * 
 * @request_parameters
 * - file: MultipartFile类型的文件对象
 * - type: 文件类型标识（logo、product、avatar等）
 * - url: 要删除的文件URL（删除接口）
 * 
 * @response_format
 * 所有接口返回统一的ApiResponse格式，包含：
 * - code: 状态码
 * - message: 响应消息
 * - data: 响应数据
 * - success: 操作是否成功
 * 
 * @file_validation
 * - 文件不能为空
 * - 支持的MIME类型：image/jpeg, image/jpg, image/png, image/gif, image/webp
 * - 文件大小限制：最大5MB
 * - 文件名安全性检查
 * 
 * @security_measures
 * - 文件类型白名单验证
 * - 文件大小限制
 * - 文件名UUID重命名防止冲突
 * - 异常捕获和安全错误信息返回
 * 
 * @dependencies
 * - FileUploadService: 文件上传业务逻辑服务
 * - ApiResponse: 统一响应格式
 * - Spring Web: Web层框架
 * - Lombok: 日志注解支持
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link com.hunric.service.FileUploadService} 文件上传服务接口
 * @see {@link com.hunric.common.model.ApiResponse} 统一响应格式
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传图片
     * 
     * @param file 图片文件
     * @param type 上传类型 (logo, product, avatar等)
     * @return 上传结果
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "general") String type) {
        
        log.info("收到图片上传请求: 文件名={}, 大小={}, 类型={}", 
                file.getOriginalFilename(), file.getSize(), type);
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("文件不能为空"));
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !isValidImageType(contentType)) {
                return ResponseEntity.ok(ApiResponse.error("不支持的文件格式，请上传 JPG、PNG、GIF 或 WebP 格式的图片"));
            }

            // 验证文件大小 (5MB)
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return ResponseEntity.ok(ApiResponse.error("文件大小不能超过 5MB"));
            }

            // 调用服务上传文件
            ApiResponse<Map<String, Object>> response = fileUploadService.uploadImage(file, type);
            
            if (response.isSuccess()) {
                log.info("图片上传成功: {}", response.getData());
            } else {
                log.warn("图片上传失败: {}", response.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("图片上传异常", e);
            return ResponseEntity.ok(ApiResponse.error("图片上传失败: " + e.getMessage()));
        }
    }

    /**
     * 删除已上传的文件
     * 
     * @param requestBody 包含文件URL的请求体
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteFile(@RequestBody Map<String, String> requestBody) {
        String url = requestBody.get("url");
        
        log.info("收到文件删除请求: url={}", url);
        
        try {
            if (url == null || url.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("文件URL不能为空"));
            }

            ApiResponse<String> response = fileUploadService.deleteFile(url);
            
            if (response.isSuccess()) {
                log.info("文件删除成功: {}", url);
            } else {
                log.warn("文件删除失败: {}", response.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("文件删除异常", e);
            return ResponseEntity.ok(ApiResponse.error("文件删除失败: " + e.getMessage()));
        }
    }

    /**
     * 验证是否为有效的图片类型
     * 
     * @param contentType 文件的MIME类型
     * @return 是否为支持的图片类型
     */
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }
} 