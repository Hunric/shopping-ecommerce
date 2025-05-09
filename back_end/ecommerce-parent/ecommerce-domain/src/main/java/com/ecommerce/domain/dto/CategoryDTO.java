package com.ecommerce.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 商品分类数据传输对象 (DTO)
 * 用于向前端返回分类信息，可能包含子分类列表以形成树状结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称 [cite: 63]
     */
    private String name;

    /**
     * 父分类ID (顶级分类为 null) [cite: 63]
     */
    private Long parentId;

    /**
     * 排序值 [cite: 63]
     */
    private Integer sortOrder;

    /**
     * 子分类列表 (用于构建树状结构)
     * - 在 Service 层需要递归组装
     */
    private List<CategoryDTO> children;

    // 可以根据需要添加其他字段，如层级、完整路径等
}