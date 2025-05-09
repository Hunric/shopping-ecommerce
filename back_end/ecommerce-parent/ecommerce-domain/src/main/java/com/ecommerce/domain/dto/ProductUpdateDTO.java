package com.ecommerce.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 商品更新数据传输对象 (DTO)
 * 所有字段都是可选的，只更新提供的字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

    @Size(max = 255, message = "商品名称不能超过255个字符")
    // NotBlank only if provided? Validation logic might be complex here.
    // Service layer should handle: if name is present, it must not be blank.
    private String name;

    private String description;

    @Positive(message = "分类ID必须是正数")
    private Long categoryId;

    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Digits(integer=10, fraction=2, message = "价格格式无效 (最多10位整数, 2位小数)")
    private BigDecimal price;

    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stockQuantity;

    @Size(max = 512, message = "图片URL不能超过512个字符")
    private String imageUrl;

    @Min(value = 0, message = "状态值无效") // 0: 下架, 1: 上架
    @Max(value = 1, message = "状态值无效") // 不允许直接更新为删除状态(2)
    private Integer status;
}