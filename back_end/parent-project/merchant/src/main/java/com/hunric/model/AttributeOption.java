package com.hunric.model;

import lombok.Data;

/**
 * 属性可选值实体类
 */
@Data
public class AttributeOption {
    private Integer optionId;
    private Integer attributeId;
    private String optionValue;
} 