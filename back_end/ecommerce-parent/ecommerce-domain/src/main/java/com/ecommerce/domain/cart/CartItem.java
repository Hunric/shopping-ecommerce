package com.ecommerce.domain.cart;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private Integer selected; // 0-未选中 1-已选中
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 