package com.hunric.repository;

import com.hunric.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品分类数据访问层
 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    
    /**
     * 查询可见的分类，按排序字段排序
     */
    List<ProductCategory> findByIsVisibleTrueOrderBySortOrder();
    
    /**
     * 根据父分类ID查询子分类
     */
    List<ProductCategory> findByParentIdAndIsVisibleTrueOrderBySortOrder(Integer parentId);
    
    /**
     * 根据分类级别查询分类
     */
    List<ProductCategory> findByCategoryLevelAndIsVisibleTrueOrderBySortOrder(Integer categoryLevel);
    
    /**
     * 根据店铺ID查询分类
     */
    List<ProductCategory> findByStoreIdAndIsVisibleTrueOrderBySortOrder(Integer storeId);
    
    /**
     * 查询去重的分类名称（用于用户端显示）
     */
    @Query("SELECT DISTINCT c.categoryName FROM ProductCategory c WHERE c.isVisible = true ORDER BY c.categoryName")
    List<String> findDistinctCategoryNames();
    
    /**
     * 根据分类名称查询第一个匹配的分类（用于用户端）
     */
    ProductCategory findFirstByCategoryNameAndIsVisibleTrueOrderByCategoryId(String categoryName);
}