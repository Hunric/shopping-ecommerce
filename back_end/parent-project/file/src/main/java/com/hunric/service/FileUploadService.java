package com.hunric.service;

import com.hunric.common.model.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传图片
     * 
     * @param file 图片文件
     * @param type 上传类型
     * @return 上传结果，包含文件URL、文件名、文件大小等信息
     */
    ApiResponse<Map<String, Object>> uploadImage(MultipartFile file, String type);
    
    /**
     * 删除文件
     * 
     * @param url 文件URL
     * @return 删除结果
     */
    ApiResponse<String> deleteFile(String url);
} 