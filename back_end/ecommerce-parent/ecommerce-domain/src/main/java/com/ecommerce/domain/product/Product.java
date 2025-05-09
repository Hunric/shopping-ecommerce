package com.ecommerce.domain.product;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String mainImage;
    private String subImages;
    private Long categoryId;
    private Integer status; // 0-下架 1-上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 