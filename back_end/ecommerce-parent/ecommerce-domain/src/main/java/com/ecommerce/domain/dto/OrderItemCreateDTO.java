package com.ecommerce.domain.dto; // 确保在 domain 模块的 dto 包下

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 创建订单时，表示单个商品项的 DTO
 * 可以直接从购物车项转换，或由前端直接构造
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;

    // 注意：创建订单时，单价应从后端实时获取，不应由前端传入
}







