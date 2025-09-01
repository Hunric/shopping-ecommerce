package com.hunric.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU信息实体类
 */
@Entity
@Table(name = "sku_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuInfo {
    
    /**
     * SKU ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Integer skuId;
    
    /**
     * 商品（SPU）ID
     */
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    /**
     * SKU名称
     */
    @Column(name = "sku_name", nullable = false)
    private String skuName;
    
    /**
     * 销售价格
     */
    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;
    
    /**
     * 库存数量
     */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    /**
     * 具体属性值组合
     */
    @Column(name = "attribute_combination", nullable = false, columnDefinition = "JSON")
    private String attributeCombination;
    
    /**
     * 状态 (1:上架, 2:下架, 3:库存不足)
     */
    @Column(name = "status", nullable = false)
    private Integer status;
    
    /**
     * 专属图片链接
     */
    @Column(name = "exclusive_image_url", columnDefinition = "LONGTEXT")
    private String exclusiveImageUrl;
    
    /**
     * 库存警告阈值
     */
    @Column(name = "warn_stock", nullable = false)
    private Integer warnStock;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}