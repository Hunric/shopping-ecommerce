package com.ecommerce.cart.dto; // 假设 DTO 在 cart 服务的 dto 包下

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    @NotNull
    private Long productId;
    @NotNull
    @Min(value = 1)
    private Integer quantity;

    // --- 商品快照信息 (需要从 ProductService 获取) ---
    private String name;        // 商品名称
    private BigDecimal price;     // 商品当前单价
    private String imageUrl;    // 商品图片URL
    private Integer stockQuantity; // 商品当前库存 (可选，用于前端提示)
    private Integer status;      // 商品状态 (可选，用于判断是否有效)
    // --- End of 商品快照信息 ---

    private BigDecimal itemTotalPrice; // 该项商品总价 (price * quantity)

    // 构造函数，方便从 Redis 数据转换
    public CartItemDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}

