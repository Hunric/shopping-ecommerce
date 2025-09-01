package com.hunric.model.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 订单项数据传输对象
 */
@Data
public class OrderItemDTO {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片URL
     */
    private String productImage;
    
    /**
     * SKU ID
     */
    private Long skuId;
    
    /**
     * SKU规格（JSON格式，如颜色、尺寸等）
     */
    private String skuSpecs;
    
    /**
     * 商品单价
     */
    @NotNull(message = "商品单价不能为空")
    @Min(value = 0, message = "商品单价必须大于等于0")
    private BigDecimal productPrice;
    
    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于等于1")
    private Integer quantity;
    
    /**
     * 小计金额
     */
    @NotNull(message = "小计金额不能为空")
    @Min(value = 0, message = "小计金额必须大于等于0")
    private BigDecimal subtotal;
} 