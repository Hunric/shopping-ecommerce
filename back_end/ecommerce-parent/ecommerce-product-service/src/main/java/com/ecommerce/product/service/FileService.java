package com.ecommerce.product.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {
    String uploadImage(MultipartFile file);
    List<String> uploadImages(List<MultipartFile> files);
    void deleteImage(String imageUrl);
} 