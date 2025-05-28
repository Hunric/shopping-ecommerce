package com.hunric.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 分类属性DTO
 */
@Data
public class CategoryAttributeDTO {
    private Integer attributeId;
    private Integer storeId;
    private Integer categoryId;
    private String attributeName;
    private String attributeType;
    private Boolean isBasicAttribute;
    private Boolean isRequired;
    private List<String> options; // 可选值列表
}

/**
 * 创建属性请求DTO
 */
@Data
class CreateAttributeRequest {
    private Integer categoryId;
    private String attributeName;
    private String attributeType;
    private Boolean isBasicAttribute;
    private Boolean isRequired;
    private List<String> options; // 可选值列表（枚举类型时使用）
}

/**
 * 更新属性请求DTO
 */
@Data
class UpdateAttributeRequest {
    private String attributeName;
    private String attributeType;
    private Boolean isBasicAttribute;
    private Boolean isRequired;
    private List<String> options; // 可选值列表（枚举类型时使用）
}

/**
 * 批量创建属性请求DTO
 */
@Data
class BatchCreateAttributesRequest {
    private Integer categoryId;
    private List<CreateAttributeRequest> attributes;
} 