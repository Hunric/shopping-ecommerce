package com.hunric.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
@Entity
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "store_id")
    private Integer storeId;
    
    @Column(name = "parent_id")
    private Integer parentId;
    
    @Column(name = "category_name")
    private String categoryName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "category_level")
    private Integer categoryLevel;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "is_visible")
    private Boolean isVisible;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}