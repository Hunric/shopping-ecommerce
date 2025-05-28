package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.CategoryAttributeDTO;
import com.hunric.service.CategoryAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类属性控制器
 */
@RestController
@RequestMapping("/api/merchant/category-attribute")
@Slf4j
public class CategoryAttributeController {
    
    @Autowired
    private CategoryAttributeService attributeService;
    
    /**
     * 获取分类的所有属性
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<CategoryAttributeDTO>> getCategoryAttributes(@PathVariable Integer categoryId) {
        log.info("获取分类属性，categoryId: {}", categoryId);
        return attributeService.getCategoryAttributes(categoryId);
    }
    
    /**
     * 获取属性详情
     */
    @GetMapping("/{attributeId}")
    public ApiResponse<CategoryAttributeDTO> getAttributeById(@PathVariable Integer attributeId) {
        log.info("获取属性详情，attributeId: {}", attributeId);
        return attributeService.getAttributeById(attributeId);
    }
    
    /**
     * 创建分类属性
     */
    @PostMapping
    public ApiResponse<CategoryAttributeDTO> createAttribute(@RequestBody CreateAttributeRequest request) {
        log.info("创建分类属性，categoryId: {}, attributeName: {}", 
                request.getCategoryId(), request.getAttributeName());
        return attributeService.createAttribute(
                request.getStoreId(),
                request.getCategoryId(),
                request.getAttributeName(),
                request.getAttributeType(),
                request.getIsBasicAttribute(),
                request.getIsRequired(),
                request.getOptions()
        );
    }
    
    /**
     * 更新分类属性
     */
    @PutMapping("/{attributeId}")
    public ApiResponse<CategoryAttributeDTO> updateAttribute(@PathVariable Integer attributeId,
                                                           @RequestBody UpdateAttributeRequest request) {
        log.info("更新分类属性，attributeId: {}", attributeId);
        return attributeService.updateAttribute(
                attributeId,
                request.getAttributeName(),
                request.getAttributeType(),
                request.getIsBasicAttribute(),
                request.getIsRequired(),
                request.getOptions()
        );
    }
    
    /**
     * 删除分类属性
     */
    @DeleteMapping("/{attributeId}")
    public ApiResponse<String> deleteAttribute(@PathVariable Integer attributeId) {
        log.info("删除分类属性，attributeId: {}", attributeId);
        return attributeService.deleteAttribute(attributeId);
    }
    
    /**
     * 批量创建分类属性
     */
    @PostMapping("/batch")
    public ApiResponse<List<CategoryAttributeDTO>> batchCreateAttributes(@RequestBody BatchCreateAttributesRequest request) {
        log.info("批量创建分类属性，categoryId: {}, count: {}", 
                request.getCategoryId(), request.getAttributes().size());
        return attributeService.batchCreateAttributes(
                request.getStoreId(),
                request.getCategoryId(),
                request.getAttributes()
        );
    }
    
    /**
     * 检查分类是否可以管理属性
     */
    @GetMapping("/category/{categoryId}/can-manage")
    public ApiResponse<Boolean> canManageAttributes(@PathVariable Integer categoryId) {
        return attributeService.canManageAttributes(categoryId);
    }
    
    /**
     * 获取属性类型列表
     */
    @GetMapping("/types")
    public ApiResponse<List<String>> getAttributeTypes() {
        return attributeService.getAttributeTypes();
    }
    
    /**
     * 创建属性请求DTO
     */
    public static class CreateAttributeRequest {
        private Integer storeId;
        private Integer categoryId;
        private String attributeName;
        private String attributeType;
        private Boolean isBasicAttribute;
        private Boolean isRequired;
        private List<String> options;
        
        // Getters and Setters
        public Integer getStoreId() { return storeId; }
        public void setStoreId(Integer storeId) { this.storeId = storeId; }
        
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        
        public String getAttributeName() { return attributeName; }
        public void setAttributeName(String attributeName) { this.attributeName = attributeName; }
        
        public String getAttributeType() { return attributeType; }
        public void setAttributeType(String attributeType) { this.attributeType = attributeType; }
        
        public Boolean getIsBasicAttribute() { return isBasicAttribute; }
        public void setIsBasicAttribute(Boolean isBasicAttribute) { this.isBasicAttribute = isBasicAttribute; }
        
        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
        
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }
    
    /**
     * 更新属性请求DTO
     */
    public static class UpdateAttributeRequest {
        private String attributeName;
        private String attributeType;
        private Boolean isBasicAttribute;
        private Boolean isRequired;
        private List<String> options;
        
        // Getters and Setters
        public String getAttributeName() { return attributeName; }
        public void setAttributeName(String attributeName) { this.attributeName = attributeName; }
        
        public String getAttributeType() { return attributeType; }
        public void setAttributeType(String attributeType) { this.attributeType = attributeType; }
        
        public Boolean getIsBasicAttribute() { return isBasicAttribute; }
        public void setIsBasicAttribute(Boolean isBasicAttribute) { this.isBasicAttribute = isBasicAttribute; }
        
        public Boolean getIsRequired() { return isRequired; }
        public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
        
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }
    
    /**
     * 批量创建属性请求DTO
     */
    public static class BatchCreateAttributesRequest {
        private Integer storeId;
        private Integer categoryId;
        private List<CategoryAttributeService.CreateAttributeRequest> attributes;
        
        // Getters and Setters
        public Integer getStoreId() { return storeId; }
        public void setStoreId(Integer storeId) { this.storeId = storeId; }
        
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        
        public List<CategoryAttributeService.CreateAttributeRequest> getAttributes() { return attributes; }
        public void setAttributes(List<CategoryAttributeService.CreateAttributeRequest> attributes) { this.attributes = attributes; }
    }
} 