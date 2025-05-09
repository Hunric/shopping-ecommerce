package com.ecommerce.domain.dto;

// --- OrderItemDTO.java ---

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 返回订单详情时，表示单个订单项的 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String productName; // 商品名称快照
    private String productImageUrl; // 商品图片快照
    private BigDecimal unitPrice; // 下单时单价快照
    private Integer quantity;
    private BigDecimal totalPrice; // 该项总价
}