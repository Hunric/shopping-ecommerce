package com.hunric.model;

import lombok.Data;
import java.util.List;

/**
 * 分类属性实体类
 */
@Data
public class CategoryAttribute {
    private Integer attributeId;
    private Integer storeId;
    private Integer categoryId;
    private String attributeName;
    private String attributeType; // 文本/枚举/数字/布尔
    private Boolean isBasicAttribute; // 是否基础属性
    private Boolean isRequired; // 是否必填
    
    // 非数据库字段
    private List<AttributeOption> options; // 属性可选值列表（枚举类型时使用）
} 