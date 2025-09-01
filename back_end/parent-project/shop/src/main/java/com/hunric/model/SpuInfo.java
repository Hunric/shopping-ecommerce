package com.hunric.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * SPU信息实体类（用户端）
 */
@Data
@Entity
@Table(name = "spu_info")
public class SpuInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer spuId;
    
    @Column(name = "merchant_id")
    private Integer merchantId;
    
    @Column(name = "store_id")
    private Integer storeId;
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "product_name")
    private String spuName;
    
    @Column(name = "description")
    private String spuDescription;
    
    @Column(name = "image_url")
    private String productImage;
    
    @Column(name = "display_price")
    private BigDecimal displayPrice;
    
    @Column(name = "basic_attributes")
    @Transient
    private Map<String, Object> basicAttributes;
    
    @Column(name = "non_basic_attributes")
    @Transient
    private Map<String, Object> nonBasicAttributes;
    
    @Column(name = "brand_name")
    private String brandName;
    
    @Column(name = "selling_point")
    private String sellingPoint;
    
    @Column(name = "unit")
    private String unit;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    // 扩展字段 - 不映射到数据库
    @Transient
    private String storeName;
    
    @Transient
    private String categoryName;
}