package com.ecommerce.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 商品创建数据传输对象 (DTO)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 255, message = "商品名称不能超过255个字符")
    private String name;

    private String description; // 商品描述，可以为空

    @NotNull(message = "必须指定商品分类ID")
    @Positive(message = "分类ID必须是正数")
    private Long categoryId;

    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0") // 价格必须大于0
    @Digits(integer=10, fraction=2, message = "价格格式无效 (最多10位整数, 2位小数)") // 对应 DECIMAL(10, 2)
    private BigDecimal price;

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能为负数") // 库存可以为0
    private Integer stockQuantity;

    @Size(max = 512, message = "图片URL不能超过512个字符")
    private String imageUrl; // 图片URL，可以为空

    @NotNull(message = "必须指定商品状态")
    @Min(value = 0, message = "状态值无效") // 假设 0: 下架, 1: 上架
    @Max(value = 1, message = "状态值无效") // 初始创建不允许直接设为删除状态(2)
    private Integer status; // 1: On Sale, 0: Off Sale
}