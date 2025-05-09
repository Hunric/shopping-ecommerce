package com.ecommerce.cart.dto; // 假设 DTO 在 cart 服务的 dto 包下

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 更新购物车商品数量的 DTO
 * 用于 PUT /api/v1/cart/items/{productId} 请求体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemQuantityDTO {

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "更新数量至少为1")
    private Integer quantity;
}
