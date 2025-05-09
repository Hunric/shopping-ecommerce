package com.ecommerce.product.controller;

import com.ecommerce.product.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(fileService.uploadImage(file));
    }

    @PostMapping("/upload/images")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(fileService.uploadImages(files));
    }

    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteImage(@RequestParam("url") String imageUrl) {
        fileService.deleteImage(imageUrl);
        return ResponseEntity.ok().build();
    }
} 