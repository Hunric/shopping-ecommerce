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
 * 文件上传控制器
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
     */
    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }
} 