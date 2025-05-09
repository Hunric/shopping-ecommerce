package com.ecommerce.product.service;

import com.ecommerce.domain.product.Category;
import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
    Category getCategoryById(Long id);
    List<Category> listCategories(Long parentId);
    List<Category> listCategoriesWithChildren();
} 