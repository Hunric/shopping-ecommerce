package com.ecommerce.domain.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 商品分类更新数据传输对象 (DTO)
 * 用于接收前端传递的更新分类信息
 * 注意：所有字段都是可选的，只更新提供的字段
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {

    /**
     * 分类名称
     * - 如果提供，则不能为空白字符串
     * - 最大长度为 100 [cite: 63]
     */
    @Size(max = 100, message = "分类名称不能超过100个字符")
    // 注意：不能加 @NotBlank，因为更新时可能只想更新其他字段
    private String name;

    /**
     * 父分类ID
     * - 可以为空，表示更新为顶级分类或不改变层级关系
     * - 0 通常不用作有效的parentId，但如果需要更新为顶级，可以考虑传null或特定值
     */
    private Long parentId;

    /**
     * 排序值
     * - 如果提供，必须是非负整数
     */
    @PositiveOrZero(message = "排序值必须是非负整数")
    private Integer sortOrder;
}