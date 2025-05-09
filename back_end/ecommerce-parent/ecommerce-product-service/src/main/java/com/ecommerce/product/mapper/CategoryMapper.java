package com.ecommerce.product.mapper;

import com.ecommerce.domain.product.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    void insert(Category category);
    void update(Category category);
    void delete(@Param("id") Long id);
    Category findById(@Param("id") Long id);
    List<Category> findByParentId(@Param("parentId") Long parentId);
    List<Category> findAll();
} 