package com.hunric.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新购物车DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartUpdateDTO {
    
    /**
     * 购物车ID
     */
    private Integer cartId;
    
    /**
     * 商品数量
     */
    private Integer quantity;
    
    /**
     * 是否选中
     */
    private Boolean isSelected;
}