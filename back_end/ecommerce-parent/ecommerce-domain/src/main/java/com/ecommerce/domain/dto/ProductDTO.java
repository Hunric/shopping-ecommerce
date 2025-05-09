package com.ecommerce.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品数据传输对象 (DTO)
 * 用于向前端返回商品信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    // Optional: Add categoryName if needed, requires join or separate query in service layer
    // private String categoryName;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Integer status; // 1: On Sale, 0: Off Sale, 2: Deleted
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Can add derived fields, e.g., status description
    public String getStatusDescription() {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "上架";
            case 0: return "下架";
            case 2: return "已删除";
            default: return "无效状态";
        }
    }
}