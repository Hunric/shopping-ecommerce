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
 * 文件上传服务实现类
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