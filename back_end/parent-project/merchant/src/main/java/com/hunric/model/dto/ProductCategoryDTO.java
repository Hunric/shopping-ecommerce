package com.hunric.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类DTO
 */
@Data
public class ProductCategoryDTO {
    private Integer categoryId;
    private Integer storeId;
    private Integer parentId;
    private String categoryName;
    private String description;
    private Integer categoryLevel;
    private Integer sortOrder;
    private Boolean isVisible;
    private String iconUrl;
    private LocalDateTime createTime;
    
    // 扩展字段
    private List<ProductCategoryDTO> children;
    private Boolean hasChildren;
    private Boolean hasProducts;
    private Integer productCount;
    private Boolean isLeaf; // 是否叶子节点
    private String parentName; // 父分类名称
}

/**
 * 创建分类请求DTO
 */
@Data
class CreateCategoryRequest {
    private Integer storeId;
    private Integer parentId;
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private Boolean isVisible;
    private String iconUrl;
}

/**
 * 更新分类请求DTO
 */
@Data
class UpdateCategoryRequest {
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private Boolean isVisible;
    private String iconUrl;
} 