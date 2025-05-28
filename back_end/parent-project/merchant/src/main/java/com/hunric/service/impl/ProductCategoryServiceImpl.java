package com.hunric.service.impl;

import com.hunric.common.model.ApiResponse;
import com.hunric.mapper.ProductCategoryMapper;
import com.hunric.mapper.CategoryAttributeMapper;
import com.hunric.model.ProductCategory;
import com.hunric.model.dto.ProductCategoryDTO;
import com.hunric.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Slf4j
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    
    @Autowired
    private ProductCategoryMapper categoryMapper;
    
    @Autowired
    private CategoryAttributeMapper attributeMapper;
    
    @Override
    public ApiResponse<List<ProductCategoryDTO>> getCategoryTree(Integer storeId) {
        try {
            List<ProductCategory> allCategories = categoryMapper.selectByStoreId(storeId);
            List<ProductCategoryDTO> categoryTree = buildCategoryTree(allCategories);
            return ApiResponse.success(categoryTree);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return ApiResponse.error("获取分类树失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<List<ProductCategoryDTO>> getTopCategories(Integer storeId) {
        try {
            List<ProductCategory> topCategories = categoryMapper.selectTopLevelByStoreId(storeId);
            List<ProductCategoryDTO> dtoList = topCategories.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtoList);
        } catch (Exception e) {
            log.error("获取顶级分类失败", e);
            return ApiResponse.error("获取顶级分类失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<List<ProductCategoryDTO>> getChildCategories(Integer storeId, Integer parentId) {
        try {
            List<ProductCategory> childCategories = categoryMapper.selectByParentId(storeId, parentId);
            List<ProductCategoryDTO> dtoList = childCategories.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(dtoList);
        } catch (Exception e) {
            log.error("获取子分类失败", e);
            return ApiResponse.error("获取子分类失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<ProductCategoryDTO> getCategoryById(Integer categoryId) {
        try {
            ProductCategory category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.error("分类不存在");
            }
            
            ProductCategoryDTO dto = convertToDTO(category);
            
            // 获取父分类名称
            if (category.getParentId() != null) {
                ProductCategory parent = categoryMapper.selectById(category.getParentId());
                if (parent != null) {
                    dto.setParentName(parent.getCategoryName());
                }
            }
            
            return ApiResponse.success(dto);
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return ApiResponse.error("获取分类详情失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<ProductCategoryDTO> createCategory(Integer storeId, Integer parentId, String categoryName, 
                                                         String description, Integer sortOrder, Boolean isVisible, String iconUrl) {
        try {
            // 检查分类名是否已存在
            int count = categoryMapper.countByStoreIdAndName(storeId, categoryName, null);
            if (count > 0) {
                return ApiResponse.error("分类名称已存在");
            }
            
            // 计算分类层级
            Integer categoryLevel = 1;
            if (parentId != null) {
                ProductCategory parent = categoryMapper.selectById(parentId);
                if (parent == null) {
                    return ApiResponse.error("父分类不存在");
                }
                if (!parent.getStoreId().equals(storeId)) {
                    return ApiResponse.error("父分类不属于当前店铺");
                }
                categoryLevel = parent.getCategoryLevel() + 1;
            }
            
            // 创建分类
            ProductCategory category = new ProductCategory();
            category.setStoreId(storeId);
            category.setParentId(parentId);
            category.setCategoryName(categoryName);
            category.setDescription(description);
            category.setCategoryLevel(categoryLevel);
            category.setSortOrder(sortOrder != null ? sortOrder : 0);
            category.setIsVisible(isVisible != null ? isVisible : true);
            category.setIconUrl(iconUrl);
            
            categoryMapper.insert(category);
            
            log.info("创建分类成功: categoryId={}, categoryName={}", category.getCategoryId(), categoryName);
            return ApiResponse.success(convertToDTO(category));
            
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return ApiResponse.error("创建分类失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<ProductCategoryDTO> updateCategory(Integer categoryId, String categoryName, String description, 
                                                         Integer sortOrder, Boolean isVisible, String iconUrl) {
        try {
            ProductCategory category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.error("分类不存在");
            }
            
            // 检查分类名是否已存在
            if (categoryName != null && !categoryName.equals(category.getCategoryName())) {
                int count = categoryMapper.countByStoreIdAndName(category.getStoreId(), categoryName, categoryId);
                if (count > 0) {
                    return ApiResponse.error("分类名称已存在");
                }
            }
            
            // 更新分类信息
            if (categoryName != null) category.setCategoryName(categoryName);
            if (description != null) category.setDescription(description);
            if (sortOrder != null) category.setSortOrder(sortOrder);
            if (isVisible != null) category.setIsVisible(isVisible);
            if (iconUrl != null) category.setIconUrl(iconUrl);
            
            categoryMapper.update(category);
            
            log.info("更新分类成功: categoryId={}", categoryId);
            return ApiResponse.success(convertToDTO(category));
            
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return ApiResponse.error("更新分类失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> deleteCategory(Integer categoryId) {
        try {
            ProductCategory category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.error("分类不存在");
            }
            
            // 检查是否有子分类
            int childCount = categoryMapper.countByParentId(categoryId);
            if (childCount > 0) {
                return ApiResponse.error("该分类下存在子分类，无法删除");
            }
            
            // 检查是否有商品
            int productCount = categoryMapper.countProductsByCategoryId(categoryId);
            if (productCount > 0) {
                return ApiResponse.error("该分类下存在商品，无法删除");
            }
            
            // 删除分类属性
            attributeMapper.deleteByCategoryId(categoryId);
            
            // 删除分类
            categoryMapper.deleteById(categoryId);
            
            log.info("删除分类成功: categoryId={}", categoryId);
            return ApiResponse.success("删除成功");
            
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return ApiResponse.error("删除分类失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ApiResponse<String> moveCategory(Integer categoryId, Integer newParentId) {
        try {
            ProductCategory category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.error("分类不存在");
            }
            
            // 检查新父分类
            ProductCategory newParent = null;
            Integer newLevel = 1;
            if (newParentId != null) {
                newParent = categoryMapper.selectById(newParentId);
                if (newParent == null) {
                    return ApiResponse.error("目标父分类不存在");
                }
                if (!newParent.getStoreId().equals(category.getStoreId())) {
                    return ApiResponse.error("不能移动到其他店铺的分类下");
                }
                
                // 检查是否形成循环
                if (isDescendant(categoryId, newParentId)) {
                    return ApiResponse.error("不能将分类移动到其子分类下");
                }
                
                newLevel = newParent.getCategoryLevel() + 1;
            }
            
            category.setParentId(newParentId);
            category.setCategoryLevel(newLevel);
            categoryMapper.update(category);
            
            // 更新所有子分类的层级
            updateChildrenLevel(categoryId, newLevel);
            
            log.info("移动分类成功: categoryId={}, newParentId={}", categoryId, newParentId);
            return ApiResponse.success("移动成功");
            
        } catch (Exception e) {
            log.error("移动分类失败", e);
            return ApiResponse.error("移动分类失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<List<ProductCategoryDTO>> getLeafCategories(Integer storeId) {
        try {
            List<ProductCategory> allCategories = categoryMapper.selectByStoreId(storeId);
            List<ProductCategoryDTO> leafCategories = allCategories.stream()
                    .filter(category -> {
                        int childCount = categoryMapper.countByParentId(category.getCategoryId());
                        return childCount == 0;
                    })
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ApiResponse.success(leafCategories);
        } catch (Exception e) {
            log.error("获取叶子分类失败", e);
            return ApiResponse.error("获取叶子分类失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<Boolean> isLeafCategory(Integer categoryId) {
        try {
            int childCount = categoryMapper.countByParentId(categoryId);
            return ApiResponse.success(childCount == 0);
        } catch (Exception e) {
            log.error("检查是否为叶子分类失败", e);
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 构建分类树
     */
    private List<ProductCategoryDTO> buildCategoryTree(List<ProductCategory> categories) {
        Map<Integer, ProductCategoryDTO> categoryMap = new HashMap<>();
        List<ProductCategoryDTO> rootCategories = new ArrayList<>();
        
        // 转换为DTO并建立映射
        for (ProductCategory category : categories) {
            ProductCategoryDTO dto = convertToDTO(category);
            categoryMap.put(category.getCategoryId(), dto);
        }
        
        // 构建树结构
        for (ProductCategory category : categories) {
            ProductCategoryDTO dto = categoryMap.get(category.getCategoryId());
            if (category.getParentId() == null) {
                rootCategories.add(dto);
            } else {
                ProductCategoryDTO parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dto);
                    parent.setHasChildren(true);
                }
            }
        }
        
        return rootCategories;
    }
    
    /**
     * 转换为DTO
     */
    private ProductCategoryDTO convertToDTO(ProductCategory category) {
        ProductCategoryDTO dto = new ProductCategoryDTO();
        BeanUtils.copyProperties(category, dto);
        
        // 设置扩展字段
        int childCount = categoryMapper.countByParentId(category.getCategoryId());
        dto.setHasChildren(childCount > 0);
        dto.setIsLeaf(childCount == 0);
        
        int productCount = categoryMapper.countProductsByCategoryId(category.getCategoryId());
        dto.setProductCount(productCount);
        dto.setHasProducts(productCount > 0);
        
        return dto;
    }
    
    /**
     * 检查是否为后代分类
     */
    private boolean isDescendant(Integer ancestorId, Integer descendantId) {
        if (ancestorId.equals(descendantId)) {
            return true;
        }
        
        List<ProductCategory> children = categoryMapper.selectByParentId(null, ancestorId);
        for (ProductCategory child : children) {
            if (isDescendant(child.getCategoryId(), descendantId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 更新子分类层级
     */
    private void updateChildrenLevel(Integer parentId, Integer parentLevel) {
        List<ProductCategory> children = categoryMapper.selectByParentId(null, parentId);
        for (ProductCategory child : children) {
            child.setCategoryLevel(parentLevel + 1);
            categoryMapper.update(child);
            updateChildrenLevel(child.getCategoryId(), child.getCategoryLevel());
        }
    }
} 