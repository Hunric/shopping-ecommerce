package com.hunric.service;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.CategoryAttributeDTO;
import java.util.List;

/**
 * 分类属性服务接口
 */
public interface CategoryAttributeService {
    
    /**
     * 获取分类的所有属性
     */
    ApiResponse<List<CategoryAttributeDTO>> getCategoryAttributes(Integer categoryId);
    
    /**
     * 获取属性详情
     */
    ApiResponse<CategoryAttributeDTO> getAttributeById(Integer attributeId);
    
    /**
     * 创建分类属性
     */
    ApiResponse<CategoryAttributeDTO> createAttribute(Integer storeId, Integer categoryId, 
                                                     String attributeName, String attributeType, 
                                                     Boolean isBasicAttribute, Boolean isRequired, 
                                                     List<String> options);
    
    /**
     * 更新分类属性
     */
    ApiResponse<CategoryAttributeDTO> updateAttribute(Integer attributeId, String attributeName, 
                                                     String attributeType, Boolean isBasicAttribute, 
                                                     Boolean isRequired, List<String> options);
    
    /**
     * 删除分类属性
     */
    ApiResponse<String> deleteAttribute(Integer attributeId);
    
    /**
     * 批量创建分类属性
     */
    ApiResponse<List<CategoryAttributeDTO>> batchCreateAttributes(Integer storeId, Integer categoryId, 
                                                                 List<CreateAttributeRequest> attributes);
    
    /**
     * 检查分类是否为叶子节点（只有叶子节点才能管理属性）
     */
    ApiResponse<Boolean> canManageAttributes(Integer categoryId);
    
    /**
     * 获取属性类型列表
     */
    ApiResponse<List<String>> getAttributeTypes();
    
    /**
     * 创建属性请求DTO
     */
    class CreateAttributeRequest {
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
} 