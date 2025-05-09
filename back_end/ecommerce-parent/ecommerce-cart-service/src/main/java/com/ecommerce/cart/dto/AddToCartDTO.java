package com.ecommerce.cart.dto; // 假设 DTO 在 cart 服务的 dto 包下

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 添加商品到购物车的 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "添加数量至少为1")
    private Integer quantity;
}
