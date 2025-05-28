package com.hunric.service;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.ProductCategoryDTO;
import java.util.List;

/**
 * 商品分类服务接口
 */
public interface ProductCategoryService {
    
    /**
     * 获取店铺的分类树
     */
    ApiResponse<List<ProductCategoryDTO>> getCategoryTree(Integer storeId);
    
    /**
     * 获取店铺的顶级分类列表
     */
    ApiResponse<List<ProductCategoryDTO>> getTopCategories(Integer storeId);
    
    /**
     * 根据父分类ID获取子分类列表
     */
    ApiResponse<List<ProductCategoryDTO>> getChildCategories(Integer storeId, Integer parentId);
    
    /**
     * 获取分类详情
     */
    ApiResponse<ProductCategoryDTO> getCategoryById(Integer categoryId);
    
    /**
     * 创建分类
     */
    ApiResponse<ProductCategoryDTO> createCategory(Integer storeId, Integer parentId, String categoryName, 
                                                   String description, Integer sortOrder, Boolean isVisible, String iconUrl);
    
    /**
     * 更新分类
     */
    ApiResponse<ProductCategoryDTO> updateCategory(Integer categoryId, String categoryName, String description, 
                                                   Integer sortOrder, Boolean isVisible, String iconUrl);
    
    /**
     * 删除分类
     */
    ApiResponse<String> deleteCategory(Integer categoryId);
    
    /**
     * 移动分类（修改父分类）
     */
    ApiResponse<String> moveCategory(Integer categoryId, Integer newParentId);
    
    /**
     * 获取叶子分类列表（可以添加商品的分类）
     */
    ApiResponse<List<ProductCategoryDTO>> getLeafCategories(Integer storeId);
    
    /**
     * 检查分类是否为叶子节点
     */
    ApiResponse<Boolean> isLeafCategory(Integer categoryId);
} 