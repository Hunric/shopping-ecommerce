package com.hunric.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    
    /**
     * 购物车ID
     */
    private Integer cartId;
    
    /**
     * SKU ID
     */
    private Integer skuId;
    
    /**
     * 商品数量
     */
    private Integer quantity;
    
    /**
     * 是否选中
     */
    private Boolean isSelected;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * SKU属性
     */
    private String skuAttr;
    
    /**
     * SKU价格
     */
    private BigDecimal skuPrice;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}