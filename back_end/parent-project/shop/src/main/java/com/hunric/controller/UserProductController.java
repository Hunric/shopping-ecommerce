package com.hunric.controller;

import com.hunric.model.SpuInfo;
import com.hunric.model.ProductCategory;
import com.hunric.service.UserProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端商品控制器
 * 提供用户浏览商品的相关接口
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin(origins = "*")
public class UserProductController {

    @Autowired
    private UserProductService userProductService;

    /**
     * 获取商品分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductCategory> categories = userProductService.getAllCategories();
            response.put("success", true);
            response.put("data", categories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取商品分类失败", e);
            response.put("success", false);
            response.put("message", "获取商品分类失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取推荐商品列表
     */
    @GetMapping("/products/recommended")
    public ResponseEntity<Map<String, Object>> getRecommendedProducts(
            @RequestParam(defaultValue = "50") Integer limit) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SpuInfo> products = userProductService.getRecommendedProducts(limit);
            response.put("success", true);
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            response.put("success", false);
            response.put("message", "获取推荐商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取热门商品列表
     */
    @GetMapping("/products/hot")
    public ResponseEntity<Map<String, Object>> getHotProducts(
            @RequestParam(defaultValue = "20") Integer limit) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SpuInfo> products = userProductService.getHotProducts(limit);
            response.put("success", true);
            response.put("data", products);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取热门商品失败", e);
            response.put("success", false);
            response.put("message", "获取热门商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 搜索商品
     */
    @GetMapping("/products/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String brandName,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = userProductService.searchProducts(
                keyword, page, pageSize, categoryId, minPrice, maxPrice, 
                brandName, sortBy, sortOrder);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            response.put("success", false);
            response.put("message", "搜索商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据分类获取商品
     */
    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = userProductService.getProductsByCategory(
                categoryId, page, pageSize, sortBy, sortOrder);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取分类商品失败", e);
            response.put("success", false);
            response.put("message", "获取分类商品失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/products/{spuId}")
    public ResponseEntity<Map<String, Object>> getProductDetail(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo product = userProductService.getProductDetail(spuId);
            if (product != null) {
                response.put("success", true);
                response.put("data", product);
            } else {
                response.put("success", false);
                response.put("message", "商品不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            response.put("success", false);
            response.put("message", "获取商品详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取商品列表（分页）
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String brandName,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = userProductService.getProducts(
                page, pageSize, categoryId, storeId, keyword, minPrice, maxPrice,
                brandName, sortBy, sortOrder);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            response.put("success", false);
            response.put("message", "获取商品列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}