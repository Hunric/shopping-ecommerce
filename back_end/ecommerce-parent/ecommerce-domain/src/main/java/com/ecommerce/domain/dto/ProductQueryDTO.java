package com.ecommerce.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 商品查询数据传输对象 (DTO)
 * 用于接收商品列表查询/搜索的参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQueryDTO {

    // Filtering fields
    private String keyword; // Search keyword (e.g., for product name)
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer status; // Filter by status (e.g., only On Sale)

    // Pagination fields
    @Min(value = 1, message = "页码必须从1开始")
    private Integer page = 1; // Default page is 1

    @Min(value = 1, message = "每页数量至少为1")
    @Max(value = 100, message = "每页数量最多为100") // Limit page size
    private Integer pageSize = 10; // Default page size is 10

    // Sorting fields
    private String sortBy = "updatedAt"; // Default sort field (e.g., id, name, price, createdAt, updatedAt)
    private String sortOrder = "desc"; // Default sort order (asc/desc)

    // Helper method to calculate offset for pagination
    public int getOffset() {
        return (this.page - 1) * this.pageSize;
    }
}