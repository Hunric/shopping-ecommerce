package com.ecommerce.service.impl;

import com.ecommerce.domain.dto.CategoryCreateDTO;
import com.ecommerce.domain.dto.CategoryDTO;
import com.ecommerce.domain.dto.CategoryUpdateDTO;
import com.ecommerce.domain.entity.Category;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.mapper.ProductMapper;
// Import custom exceptions
import com.ecommerce.exception.CategoryNotFoundException;
import com.ecommerce.exception.DuplicateCategoryNameException;
import com.ecommerce.exception.InvalidCategoryOperationException;
// Import base service interface
import com.ecommerce.service.CategoryService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryCreateDTO createDTO) {
        log.info("Attempting to create category: {}", createDTO.getName());

        // 1. Validate parentId if provided
        if (createDTO.getParentId() != null && !categoryMapper.existsById(createDTO.getParentId())) {
            log.warn("Parent category not found for id: {}", createDTO.getParentId());
            // Throw specific exception
            throw new CategoryNotFoundException("父分类不存在: ID = " + createDTO.getParentId());
        }

        // 2. Check for duplicate name at the same level
        Optional<Category> existingCategory = categoryMapper.findByNameAndParentIdExcludingId(
                createDTO.getName(), createDTO.getParentId(), null);
        if (existingCategory.isPresent()) {
            log.warn("Duplicate category name '{}' found under parentId {}", createDTO.getName(), createDTO.getParentId());
            // Throw specific exception
            throw new DuplicateCategoryNameException("同级分类下已存在同名分类: " + createDTO.getName());
        }

        // 3. Create and save the new category
        Category newCategory = new Category();
        BeanUtils.copyProperties(createDTO, newCategory);
        newCategory.setCreatedAt(LocalDateTime.now());
        newCategory.setUpdatedAt(LocalDateTime.now());
        if (newCategory.getSortOrder() == null) {
            newCategory.setSortOrder(0);
        }

        categoryMapper.insert(newCategory);
        log.info("Category created successfully with id: {}", newCategory.getId());

        // 4. Convert to DTO and return
        return convertToDTO(newCategory);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        log.debug("Fetching category by id: {}", id);
        Category category = categoryMapper.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found for id: {}", id);
                    // Throw specific exception
                    return new CategoryNotFoundException("分类未找到: ID = " + id);
                });
        return convertToDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategoriesAsTree() {
        log.debug("Fetching all categories as tree");
        List<Category> allCategories = categoryMapper.findAll();
        List<CategoryDTO> allCategoryDTOs = allCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<Long, CategoryDTO> dtoMap = allCategoryDTOs.stream()
                .collect(Collectors.toMap(CategoryDTO::getId, dto -> dto));

        List<CategoryDTO> rootCategories = new ArrayList<>();
        for (CategoryDTO dto : allCategoryDTOs) {
            if (dto.getParentId() == null) {
                rootCategories.add(dto);
            } else {
                CategoryDTO parentDTO = dtoMap.get(dto.getParentId());
                if (parentDTO != null) {
                    if (parentDTO.getChildren() == null) {
                        parentDTO.setChildren(new ArrayList<>());
                    }
                    parentDTO.getChildren().add(dto);
                } else {
                    log.warn("Parent category DTO not found in map for child id {}, parentId {}", dto.getId(), dto.getParentId());
                    rootCategories.add(dto);
                }
            }
        }
        log.debug("Built category tree with {} root nodes", rootCategories.size());
        return rootCategories;
    }

    @Override
    public List<CategoryDTO> getSubCategories(Long parentId) {
        log.debug("Fetching subcategories for parentId: {}", parentId);
        List<Category> subCategories = categoryMapper.findByParentId(parentId);
        return subCategories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryUpdateDTO updateDTO) {
        log.info("Attempting to update category id: {}", id);
        Category category = categoryMapper.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found for update, id: {}", id);
                    // Throw specific exception
                    return new CategoryNotFoundException("分类未找到: ID = " + id);
                });

        boolean parentIdMaybeChanged = false;

        // 1. Validate and update parentId if provided
        if (updateDTO.getParentId() != null || category.getParentId() != null) {
            Long newParentId = updateDTO.getParentId();
            if (newParentId != null && !newParentId.equals(category.getParentId()) || newParentId == null && category.getParentId() != null ) {
                parentIdMaybeChanged = true;
                // Prevent setting parent to self
                if (id.equals(newParentId)) {
                    log.warn("Attempted to set category id {} as its own parent.", id);
                    // Throw specific exception
                    throw new InvalidCategoryOperationException("不能将分类设置为自身的父分类");
                }
                // Check if new parent exists (only if not null)
                if (newParentId != null && !categoryMapper.existsById(newParentId)) {
                    log.warn("New parent category not found for id: {}", newParentId);
                    // Throw specific exception
                    throw new CategoryNotFoundException("设置的父分类不存在: ID = " + newParentId);
                }
                // TODO: Add cycle detection logic if necessary
                category.setParentId(newParentId);
            }
        }

        // 2. Check for duplicate name if name is provided and changed OR if parentId changed
        String newName = updateDTO.getName();
        boolean nameChanged = StringUtils.hasText(newName) && !newName.equals(category.getName());

        if (nameChanged || (StringUtils.hasText(newName) && parentIdMaybeChanged)) {
            String nameToCheck = nameChanged ? newName : category.getName();
            Long parentIdToCheck = category.getParentId();

            Optional<Category> existingCategory = categoryMapper.findByNameAndParentIdExcludingId(
                    nameToCheck, parentIdToCheck, id);
            if (existingCategory.isPresent()) {
                log.warn("Duplicate category name '{}' found under parentId {} while updating id {}", nameToCheck, parentIdToCheck, id);
                // Throw specific exception
                throw new DuplicateCategoryNameException("同级分类下已存在同名分类: " + nameToCheck);
            }
            if (nameChanged) {
                category.setName(newName);
            }
        }

        // 3. Update other fields if provided
        if (updateDTO.getSortOrder() != null) {
            if (!updateDTO.getSortOrder().equals(category.getSortOrder())) {
                category.setSortOrder(updateDTO.getSortOrder());
            }
        }

        // 4. Set update time and save
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.update(category);
        log.info("Category updated successfully for id: {}", id);

        // 5. Convert to DTO and return
        return convertToDTO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Attempting to delete category id: {}", id);
        // 1. Check if category exists
        if (!categoryMapper.existsById(id)) {
            log.warn("Category not found for deletion, id: {}", id);
            // Throw specific exception
            throw new CategoryNotFoundException("分类未找到: ID = " + id);
        }

        // 2. Check if category has children
        if (categoryMapper.countChildrenById(id) > 0) {
            log.warn("Cannot delete category id {} because it has subcategories.", id);
            // Throw specific exception
            throw new InvalidCategoryOperationException("无法删除，该分类下存在子分类");
        }

        // 3. Check if category has associated products
        int productCount = productMapper.countByCategoryId(id);
        if (productCount > 0) {
            log.warn("Cannot delete category id {} because it has {} associated products.", id, productCount);
            // Throw specific exception
            throw new InvalidCategoryOperationException("无法删除，该分类下存在商品 (数量: " + productCount + ")");
        }

        // 4. Delete the category
        int deletedRows = categoryMapper.deleteById(id);
        if (deletedRows > 0) {
            log.info("Category deleted successfully for id: {}", id);
        } else {
            log.error("Failed to delete category id {} even though it exists.", id);
            // Keep as RuntimeException for unexpected DB/system errors during delete
            throw new RuntimeException("删除分类失败，数据库操作未返回成功");
        }
    }

    // --- Helper Methods ---

    private CategoryDTO convertToDTO(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}