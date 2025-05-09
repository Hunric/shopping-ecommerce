package com.ecommerce.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 商品分类创建数据传输对象 (DTO)
 * 用于接收前端传递的创建分类信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {

    /**
     * 分类名称
     * - 不能为空白字符串
     * - 最大长度为 100 (与数据库定义一致 [cite: 63])
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称不能超过100个字符")
    private String name;

    /**
     * 父分类ID
     * - 可以为空，表示创建顶级分类
     * - 如果提供，必须是一个有效的分类ID (这个校验通常在 Service 层进行)
     */
    private Long parentId; // 允许为 null，表示顶级分类

    /**
     * 排序值
     * - 可以为空，默认为 0
     * - 必须是非负整数
     */
    @PositiveOrZero(message = "排序值必须是非负整数")
    private Integer sortOrder = 0; // 提供默认值
}