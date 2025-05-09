package com.ecommerce.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private List<CartItemDTO> items;          // 购物车商品项列表
    private Integer totalQuantity;    // 购物车商品总数量
    private BigDecimal totalPrice;       // 购物车商品总价
    private Integer totalItems;       // 购物车商品种类数量
}
