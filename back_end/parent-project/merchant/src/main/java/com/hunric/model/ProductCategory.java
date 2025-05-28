package com.hunric.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类实体类
 */
@Data
public class ProductCategory {
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
    
    // 非数据库字段
    private List<ProductCategory> children; // 子分类列表
    private Boolean hasChildren; // 是否有子分类
    private Boolean hasProducts; // 是否有商品
    private Integer productCount; // 商品数量
} 