package com.ecommerce.service;

import com.ecommerce.domain.dto.CategoryCreateDTO;
import com.ecommerce.domain.dto.CategoryDTO;
import com.ecommerce.domain.dto.CategoryUpdateDTO;
// 导入可能需要抛出的自定义异常 (如果已定义或计划定义)
// import com.geminishopping.ecommerce.exception.ResourceNotFoundException;
// import com.geminishopping.ecommerce.exception.DuplicateResourceException;
// import com.geminishopping.ecommerce.exception.InvalidOperationException;

import java.util.List;

/**
 * 商品分类服务接口
 * 定义商品分类相关的业务操作
 */
public interface CategoryService {

    /**
     * 创建一个新的商品分类
     *
     * @param createDTO 包含新分类信息的 DTO
     * @return 创建成功的分类信息 (CategoryDTO)
     * @throws RuntimeException 如果同级分类下名称已存在 (可以替换为更具体的 DuplicateResourceException)
     * @throws RuntimeException 如果父分类 ID 无效 (可以替换为更具体的 ResourceNotFoundException)
     */
    CategoryDTO createCategory(CategoryCreateDTO createDTO);

    /**
     * 根据 ID 获取分类详情
     *
     * @param id 分类 ID
     * @return 分类详情 (CategoryDTO)
     * @throws RuntimeException 如果分类未找到 (可以替换为更具体的 ResourceNotFoundException)
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * 获取所有分类，并以树状结构返回
     *
     * @return 顶级分类列表，每个 CategoryDTO 包含其子分类列表 (children)
     */
    List<CategoryDTO> getAllCategoriesAsTree();

    /**
     * 获取指定父分类下的直接子分类列表
     *
     * @param parentId 父分类 ID (如果为 null，则获取所有顶级分类)
     * @return 直接子分类列表 (List<CategoryDTO>)
     */
    List<CategoryDTO> getSubCategories(Long parentId);

    /**
     * 更新商品分类信息
     *
     * @param id        要更新的分类 ID
     * @param updateDTO 包含更新信息的 DTO
     * @return 更新后的分类信息 (CategoryDTO)
     * @throws RuntimeException 如果分类未找到 (ResourceNotFoundException)
     * @throws RuntimeException 如果更新后的名称在同级下已存在 (DuplicateResourceException)
     * @throws RuntimeException 如果设置的父分类 ID 无效或导致循环引用 (InvalidOperationException)
     */
    CategoryDTO updateCategory(Long id, CategoryUpdateDTO updateDTO);

    /**
     * 删除商品分类
     *
     * @param id 要删除的分类 ID
     * @throws RuntimeException 如果分类未找到 (ResourceNotFoundException)
     * @throws RuntimeException 如果分类下存在子分类或关联了商品，不允许删除 (InvalidOperationException)
     */
    void deleteCategory(Long id);

}