package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.ProductCategoryDTO;
import com.hunric.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@RestController
@RequestMapping("/api/merchant/product-category")
@Slf4j
public class ProductCategoryController {
    
    @Autowired
    private ProductCategoryService categoryService;
    
    /**
     * 获取店铺的分类树
     */
    @GetMapping("/tree/{storeId}")
    public ApiResponse<List<ProductCategoryDTO>> getCategoryTree(@PathVariable Integer storeId) {
        log.info("获取分类树，storeId: {}", storeId);
        return categoryService.getCategoryTree(storeId);
    }
    
    /**
     * 获取顶级分类
     */
    @GetMapping("/top/{storeId}")
    public ApiResponse<List<ProductCategoryDTO>> getTopCategories(@PathVariable Integer storeId) {
        log.info("获取顶级分类，storeId: {}", storeId);
        return categoryService.getTopCategories(storeId);
    }
    
    /**
     * 获取子分类
     */
    @GetMapping("/children/{storeId}/{parentId}")
    public ApiResponse<List<ProductCategoryDTO>> getChildCategories(@PathVariable Integer storeId, 
                                                                   @PathVariable Integer parentId) {
        log.info("获取子分类，storeId: {}, parentId: {}", storeId, parentId);
        return categoryService.getChildCategories(storeId, parentId);
    }
    
    /**
     * 获取叶子分类（可添加商品的分类）
     */
    @GetMapping("/leaf/{storeId}")
    public ApiResponse<List<ProductCategoryDTO>> getLeafCategories(@PathVariable Integer storeId) {
        log.info("获取叶子分类，storeId: {}", storeId);
        return categoryService.getLeafCategories(storeId);
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{categoryId}")
    public ApiResponse<ProductCategoryDTO> getCategoryById(@PathVariable Integer categoryId) {
        log.info("获取分类详情，categoryId: {}", categoryId);
        return categoryService.getCategoryById(categoryId);
    }
    
    /**
     * 创建分类
     */
    @PostMapping
    public ApiResponse<ProductCategoryDTO> createCategory(@RequestBody CreateCategoryRequest request) {
        log.info("创建分类，storeId: {}, categoryName: {}", request.getStoreId(), request.getCategoryName());
        return categoryService.createCategory(
                request.getStoreId(),
                request.getParentId(),
                request.getCategoryName(),
                request.getDescription(),
                request.getSortOrder(),
                request.getIsVisible(),
                request.getIconUrl()
        );
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    public ApiResponse<ProductCategoryDTO> updateCategory(@PathVariable Integer categoryId,
                                                         @RequestBody UpdateCategoryRequest request) {
        log.info("更新分类，categoryId: {}", categoryId);
        return categoryService.updateCategory(
                categoryId,
                request.getCategoryName(),
                request.getDescription(),
                request.getSortOrder(),
                request.getIsVisible(),
                request.getIconUrl()
        );
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public ApiResponse<String> deleteCategory(@PathVariable Integer categoryId) {
        log.info("删除分类，categoryId: {}", categoryId);
        return categoryService.deleteCategory(categoryId);
    }
    
    /**
     * 移动分类
     */
    @PutMapping("/{categoryId}/move")
    public ApiResponse<String> moveCategory(@PathVariable Integer categoryId,
                                          @RequestParam Integer newParentId) {
        log.info("移动分类，categoryId: {}, newParentId: {}", categoryId, newParentId);
        return categoryService.moveCategory(categoryId, newParentId);
    }
    
    /**
     * 检查是否为叶子分类
     */
    @GetMapping("/{categoryId}/is-leaf")
    public ApiResponse<Boolean> isLeafCategory(@PathVariable Integer categoryId) {
        return categoryService.isLeafCategory(categoryId);
    }
    
    /**
     * 创建分类请求DTO
     */
    public static class CreateCategoryRequest {
        private Integer storeId;
        private Integer parentId;
        private String categoryName;
        private String description;
        private Integer sortOrder;
        private Boolean isVisible;
        private String iconUrl;
        
        // Getters and Setters
        public Integer getStoreId() { return storeId; }
        public void setStoreId(Integer storeId) { this.storeId = storeId; }
        
        public Integer getParentId() { return parentId; }
        public void setParentId(Integer parentId) { this.parentId = parentId; }
        
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
        
        public Boolean getIsVisible() { return isVisible; }
        public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
        
        public String getIconUrl() { return iconUrl; }
        public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    }
    
    /**
     * 更新分类请求DTO
     */
    public static class UpdateCategoryRequest {
        private String categoryName;
        private String description;
        private Integer sortOrder;
        private Boolean isVisible;
        private String iconUrl;
        
        // Getters and Setters
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
        
        public Boolean getIsVisible() { return isVisible; }
        public void setIsVisible(Boolean isVisible) { this.isVisible = isVisible; }
        
        public String getIconUrl() { return iconUrl; }
        public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    }
} 