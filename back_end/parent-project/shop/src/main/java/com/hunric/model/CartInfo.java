package com.hunric.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 购物车信息实体类
 */
@Entity
@Table(name = "cart_info",
       uniqueConstraints = @UniqueConstraint(name = "uk_user_sku", columnNames = {"user_id", "sku_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartInfo {
    
    /**
     * 购物车ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    /**
     * SKU ID
     */
    @Column(name = "sku_id", nullable = false)
    private Integer skuId;
    
    /**
     * 数量
     */
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;
    
    /**
     * 是否选中
     */
    @Column(name = "is_selected", nullable = false)
    @Builder.Default
    private Boolean isSelected = true;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
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