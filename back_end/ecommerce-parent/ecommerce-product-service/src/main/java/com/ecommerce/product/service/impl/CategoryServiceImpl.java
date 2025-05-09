package com.ecommerce.product.service.impl;

import com.ecommerce.domain.product.Category;
import com.ecommerce.product.mapper.CategoryMapper;
import com.ecommerce.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public Category createCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
        return category;
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.delete(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryMapper.findById(id);
    }

    @Override
    public List<Category> listCategories(Long parentId) {
        return categoryMapper.findByParentId(parentId);
    }

    @Override
    public List<Category> listCategoriesWithChildren() {
        List<Category> allCategories = categoryMapper.findAll();
        
        // 获取所有一级分类
        List<Category> rootCategories = allCategories.stream()
            .filter(category -> category.getParentId() == 0)
            .collect(Collectors.toList());

        // 为一级分类设置子分类
        rootCategories.forEach(root -> {
            setChildCategories(root, allCategories);
        });

        return rootCategories;
    }

    private void setChildCategories(Category parent, List<Category> allCategories) {
        List<Category> children = allCategories.stream()
            .filter(category -> category.getParentId().equals(parent.getId()))
            .collect(Collectors.toList());

        if (!children.isEmpty()) {
            children.forEach(child -> setChildCategories(child, allCategories));
        }
    }
} 