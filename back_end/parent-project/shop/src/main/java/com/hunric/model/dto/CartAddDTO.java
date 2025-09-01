package com.hunric.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加购物车DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddDTO {
    
    /**
     * SKU ID
     */
    private Integer skuId;
    
    /**
     * 商品数量
     */
    private Integer quantity;
}