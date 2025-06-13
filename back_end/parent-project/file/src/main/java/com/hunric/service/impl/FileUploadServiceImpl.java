package com.hunric.service.impl;

import com.hunric.common.model.ApiResponse;
import com.hunric.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 电商平台文件上传服务实现类
 * 
 * @description 文件上传服务的具体实现，负责处理文件的存储、管理和删除操作。
 *              采用本地文件系统存储，支持按类型和日期分类组织文件结构。
 * 
 * @features
 * - 本地文件系统存储
 * - 按类型和日期自动分类存储
 * - UUID文件名生成防止冲突
 * - 文件访问URL自动生成
 * - 文件删除和清理功能
 * - 详细的操作日志记录
 * - 异常处理和错误恢复
 * 
 * @storage_structure
 * 文件存储目录结构：
 * <pre>
 * /uploads/
 * ├── logo/
 * │   └── 2024/01/15/
 * │       └── uuid-filename.jpg
 * ├── product/
 * │   └── 2024/01/15/
 * │       └── uuid-filename.png
 * └── avatar/
 *     └── 2024/01/15/
 *         └── uuid-filename.gif
 * </pre>
 * 
 * @configuration
 * - file.upload.path: 文件上传根目录（默认：/uploads）
 * - file.access.base-url: 文件访问基础URL（默认：http://localhost:8080）
 * 
 * @file_naming
 * - 使用UUID生成唯一文件名
 * - 保留原始文件扩展名
 * - 格式：{UUID}.{extension}
 * 
 * @url_generation
 * 生成的文件访问URL格式：
 * {baseUrl}/uploads/{type}/{yyyy/MM/dd}/{filename}
 * 
 * @error_handling
 * - IOException: 文件I/O操作异常
 * - SecurityException: 文件权限异常
 * - IllegalArgumentException: 参数验证异常
 * - 其他运行时异常的统一处理
 * 
 * @thread_safety
 * 该服务是线程安全的，支持并发文件上传操作。
 * 使用UUID确保文件名唯一性，避免并发冲突。
 * 
 * @dependencies
 * - FileUploadService: 文件上传服务接口
 * - ApiResponse: 统一响应格式
 * - Spring Framework: 依赖注入和配置管理
 * - Java NIO: 文件操作API
 * - Lombok: 日志注解支持
 * 
 * @author 开发团队
 * @version 1.0.0
 * @since 2024
 * 
 * @see {@link com.hunric.service.FileUploadService} 文件上传服务接口
 * @see {@link java.nio.file.Files} Java NIO文件操作API
 * @see {@link java.util.UUID} UUID生成器
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    // 文件上传根目录
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    // 文件访问基础URL
    @Value("${file.access.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public ApiResponse<Map<String, Object>> uploadImage(MultipartFile file, String type) {
        try {
            // 创建上传目录
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String typeDir = type != null ? type : "general";
            String fullPath = uploadPath + "/" + typeDir + "/" + datePath;
            
            Path uploadDir = Paths.get(fullPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(fileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 构建文件访问URL
            String fileUrl = baseUrl + "/uploads/" + typeDir + "/" + datePath + "/" + fileName;

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());
            result.put("type", type);
            result.put("uploadTime", LocalDateTime.now().toString());

            log.info("文件上传成功: 原文件名={}, 新文件名={}, 大小={}, URL={}", 
                    originalFilename, fileName, file.getSize(), fileUrl);

            return ApiResponse.success(result);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return ApiResponse.error("文件上传异常: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> deleteFile(String url) {
        try {
            if (url == null || url.trim().isEmpty()) {
                return ApiResponse.error("文件URL不能为空");
            }

            // 从URL中提取文件路径
            String filePath = extractFilePathFromUrl(url);
            if (filePath == null) {
                return ApiResponse.error("无效的文件URL");
            }

            Path file = Paths.get(uploadPath, filePath);
            
            if (Files.exists(file)) {
                Files.delete(file);
                log.info("文件删除成功: {}", url);
                return ApiResponse.success("文件删除成功");
            } else {
                log.warn("文件不存在: {}", url);
                return ApiResponse.error("文件不存在");
            }

        } catch (IOException e) {
            log.error("文件删除失败", e);
            return ApiResponse.error("文件删除失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件删除异常", e);
            return ApiResponse.error("文件删除异常: " + e.getMessage());
        }
    }

    /**
     * 从URL中提取文件路径
     * 
     * @param url 完整的文件访问URL
     * @return 相对于上传根目录的文件路径，失败时返回null
     */
    private String extractFilePathFromUrl(String url) {
        try {
            // 假设URL格式为: http://localhost:8080/uploads/type/yyyy/MM/dd/filename.ext
            String uploadsPrefix = "/uploads/";
            int index = url.indexOf(uploadsPrefix);
            if (index != -1) {
                return url.substring(index + uploadsPrefix.length());
            }
            return null;
        } catch (Exception e) {
            log.error("提取文件路径失败", e);
            return null;
        }
    }
} 