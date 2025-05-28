package com.hunric.service.impl;

import com.hunric.common.model.ApiResponse;
import com.hunric.mapper.CategoryAttributeMapper;
import com.hunric.mapper.AttributeOptionMapper;
import com.hunric.mapper.ProductCategoryMapper;
import com.hunric.model.CategoryAttribute;
import com.hunric.model.AttributeOption;
import com.hunric.model.dto.CategoryAttributeDTO;
import com.hunric.service.CategoryAttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类属性服务实现类
 */
@Slf4j
@Service
public class CategoryAttributeServiceImpl implements CategoryAttributeService {
    
    @Autowired
    private CategoryAttributeMapper attributeMapper;
    
    @Autowired
    private AttributeOptionMapper optionMapper;
    
    @Autowired
    private ProductCategoryMapper categoryMapper;
    
    @Override
    public ApiResponse<List<CategoryAttributeDTO>> getCategoryAttributes(Integer categoryId) {
        try {
            // 检查分类是否存在
            if (categoryMapper.selectById(categoryId) == null) {
                return ApiResponse.error("分类不存在");
            }
            
            List<CategoryAttribute> attributes = attributeMapper.selectByCategoryId(categoryId);
            List<CategoryAttributeDTO> dtoList = attributes.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(dtoList);
        } catch (Exception e) {
            log.error("获取分类属性失败", e);
            return ApiResponse.error("获取分类属性失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<CategoryAttributeDTO> getAttributeById(Integer attributeId) {
        try {
            CategoryAttribute attribute = attributeMapper.selectById(attributeId);
            if (attribute == null) {
                return ApiResponse.error("属性不存在");
            }
            
            CategoryAttributeDTO dto = convertToDTO(attribute);
            return ApiResponse.success(dto);
        } catch (Exception e) {
            log.error("获取属性详情失败", e);
            return ApiResponse.error("获取属性详情失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<CategoryAttributeDTO> createAttribute(Integer storeId, Integer categoryId, 
                                                           String attributeName, String attributeType, 
                                                           Boolean isBasicAttribute, Boolean isRequired, 
                                                           List<String> options) {
        try {
            // 验证参数
            if (!StringUtils.hasText(attributeName)) {
                return ApiResponse.error("属性名称不能为空");
            }
            if (!StringUtils.hasText(attributeType)) {
                return ApiResponse.error("属性类型不能为空");
            }
            
            // 检查分类是否存在且为叶子节点
            ApiResponse<Boolean> canManageResult = canManageAttributes(categoryId);
            if (!canManageResult.isSuccess() || !canManageResult.getData()) {
                return ApiResponse.error("只有叶子分类才能管理属性");
            }
            
            // 检查属性名是否已存在
            int count = attributeMapper.countByCategoryIdAndName(categoryId, attributeName, null);
            if (count > 0) {
                return ApiResponse.error("属性名称已存在");
            }
            
            // 验证属性类型
            if (!isValidAttributeType(attributeType)) {
                return ApiResponse.error("无效的属性类型");
            }
            
            // 如果是基础属性，则必须为枚举类型且必填
            if (Boolean.TRUE.equals(isBasicAttribute)) {
                if (!"ENUM".equals(attributeType)) {
                    return ApiResponse.error("基础属性只能是枚举类型");
                }
                if (options == null || options.isEmpty()) {
                    return ApiResponse.error("枚举类型的基础属性必须提供可选值");
                }
                isRequired = true;
            }
            
            // 创建属性
            CategoryAttribute attribute = new CategoryAttribute();
            attribute.setStoreId(storeId);
            attribute.setCategoryId(categoryId);
            attribute.setAttributeName(attributeName);
            attribute.setAttributeType(attributeType);
            attribute.setIsBasicAttribute(isBasicAttribute != null ? isBasicAttribute : false);
            attribute.setIsRequired(isRequired != null ? isRequired : false);
            
            attributeMapper.insert(attribute);
            
            // 如果是枚举类型，保存可选值
            if ("ENUM".equals(attributeType) && options != null && !options.isEmpty()) {
                List<AttributeOption> optionList = options.stream()
                        .filter(StringUtils::hasText)
                        .map(optionValue -> {
                            AttributeOption option = new AttributeOption();
                            option.setAttributeId(attribute.getAttributeId());
                            option.setOptionValue(optionValue.trim());
                            return option;
                        })
                        .collect(Collectors.toList());
                
                if (!optionList.isEmpty()) {
                    optionMapper.batchInsert(optionList);
                }
            }
            
            log.info("创建分类属性成功: attributeId={}, attributeName={}", 
                    attribute.getAttributeId(), attributeName);
            
            return ApiResponse.success(convertToDTO(attribute));
            
        } catch (Exception e) {
            log.error("创建分类属性失败", e);
            return ApiResponse.error("创建分类属性失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<CategoryAttributeDTO> updateAttribute(Integer attributeId, String attributeName, 
                                                           String attributeType, Boolean isBasicAttribute, 
                                                           Boolean isRequired, List<String> options) {
        try {
            CategoryAttribute attribute = attributeMapper.selectById(attributeId);
            if (attribute == null) {
                return ApiResponse.error("属性不存在");
            }
            
            // 验证参数
            if (StringUtils.hasText(attributeName)) {
                // 检查属性名是否已存在（排除当前属性）
                int count = attributeMapper.countByCategoryIdAndName(
                        attribute.getCategoryId(), attributeName, attributeId);
                if (count > 0) {
                    return ApiResponse.error("属性名称已存在");
                }
                attribute.setAttributeName(attributeName);
            }
            
            if (StringUtils.hasText(attributeType)) {
                if (!isValidAttributeType(attributeType)) {
                    return ApiResponse.error("无效的属性类型");
                }
                attribute.setAttributeType(attributeType);
            }
            
            if (isBasicAttribute != null) {
                // 如果要设置为基础属性，必须是枚举类型
                if (isBasicAttribute && !"ENUM".equals(attribute.getAttributeType())) {
                    return ApiResponse.error("基础属性只能是枚举类型");
                }
                attribute.setIsBasicAttribute(isBasicAttribute);
                // 如果是基础属性，则必须为必填
                if (isBasicAttribute) {
                    attribute.setIsRequired(true);
                }
            }
            
            if (isRequired != null && !Boolean.TRUE.equals(attribute.getIsBasicAttribute())) {
                attribute.setIsRequired(isRequired);
            }
            
            // 更新属性
            attributeMapper.update(attribute);
            
            // 更新可选值（如果是枚举类型）
            if ("ENUM".equals(attribute.getAttributeType())) {
                // 删除原有可选值
                optionMapper.deleteByAttributeId(attributeId);
                
                // 添加新的可选值
                if (options != null && !options.isEmpty()) {
                    List<AttributeOption> optionList = options.stream()
                            .filter(StringUtils::hasText)
                            .map(optionValue -> {
                                AttributeOption option = new AttributeOption();
                                option.setAttributeId(attributeId);
                                option.setOptionValue(optionValue.trim());
                                return option;
                            })
                            .collect(Collectors.toList());
                    
                    if (!optionList.isEmpty()) {
                        optionMapper.batchInsert(optionList);
                    }
                }
            }
            
            log.info("更新分类属性成功: attributeId={}", attributeId);
            return ApiResponse.success(convertToDTO(attribute));
            
        } catch (Exception e) {
            log.error("更新分类属性失败", e);
            return ApiResponse.error("更新分类属性失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> deleteAttribute(Integer attributeId) {
        try {
            CategoryAttribute attribute = attributeMapper.selectById(attributeId);
            if (attribute == null) {
                return ApiResponse.error("属性不存在");
            }
            
            // 删除可选值
            optionMapper.deleteByAttributeId(attributeId);
            
            // 删除属性
            attributeMapper.deleteById(attributeId);
            
            log.info("删除分类属性成功: attributeId={}", attributeId);
            return ApiResponse.success("删除成功");
            
        } catch (Exception e) {
            log.error("删除分类属性失败", e);
            return ApiResponse.error("删除分类属性失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<List<CategoryAttributeDTO>> batchCreateAttributes(Integer storeId, Integer categoryId, 
                                                                        List<CreateAttributeRequest> attributes) {
        try {
            // 检查分类是否存在且为叶子节点
            ApiResponse<Boolean> canManageResult = canManageAttributes(categoryId);
            if (!canManageResult.isSuccess() || !canManageResult.getData()) {
                return ApiResponse.error("只有叶子分类才能管理属性");
            }
            
            List<CategoryAttributeDTO> resultList = new ArrayList<>();
            
            for (CreateAttributeRequest request : attributes) {
                ApiResponse<CategoryAttributeDTO> result = createAttribute(
                        storeId, categoryId, request.getAttributeName(), 
                        request.getAttributeType(), request.getIsBasicAttribute(), 
                        request.getIsRequired(), request.getOptions());
                
                if (result.isSuccess()) {
                    resultList.add(result.getData());
                } else {
                    log.warn("批量创建属性时失败: {}", result.getMessage());
                }
            }
            
            return ApiResponse.success(resultList);
            
        } catch (Exception e) {
            log.error("批量创建分类属性失败", e);
            return ApiResponse.error("批量创建分类属性失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Boolean> canManageAttributes(Integer categoryId) {
        try {
            // 检查分类是否存在
            if (categoryMapper.selectById(categoryId) == null) {
                return ApiResponse.error("分类不存在");
            }
            
            // 检查是否为叶子节点
            int childCount = categoryMapper.countByParentId(categoryId);
            return ApiResponse.success(childCount == 0);
            
        } catch (Exception e) {
            log.error("检查分类是否可管理属性失败", e);
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<List<String>> getAttributeTypes() {
        List<String> types = Arrays.asList("TEXT", "NUMBER", "DATE", "BOOLEAN", "ENUM");
        return ApiResponse.success(types);
    }
    
    /**
     * 转换为DTO
     */
    private CategoryAttributeDTO convertToDTO(CategoryAttribute attribute) {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        BeanUtils.copyProperties(attribute, dto);
        
        // 如果是枚举类型，获取可选值
        if ("ENUM".equals(attribute.getAttributeType())) {
            List<AttributeOption> options = optionMapper.selectByAttributeId(attribute.getAttributeId());
            List<String> optionValues = options.stream()
                    .map(AttributeOption::getOptionValue)
                    .collect(Collectors.toList());
            dto.setOptions(optionValues);
        }
        
        return dto;
    }
    
    /**
     * 验证属性类型是否有效
     */
    private boolean isValidAttributeType(String attributeType) {
        return Arrays.asList("TEXT", "NUMBER", "DATE", "BOOLEAN", "ENUM").contains(attributeType);
    }
} 