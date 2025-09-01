package com.hunric.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单项数据传输对象
 */
@Data
public class OrderItemDTO {
    /**
     * SKU ID
     */
    private Long skuId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * SKU名称
     */
    private String skuName;
    
    /**
     * 商品单价
     */
    private BigDecimal price;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
    /**
     * 商品属性
     */
    private Map<String, String> attributes;
} 