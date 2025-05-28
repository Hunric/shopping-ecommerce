package com.hunric.mapper;

import com.hunric.model.ProductCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 商品分类数据访问层接口
 */
@Mapper
public interface ProductCategoryMapper {
    
    /**
     * 根据店铺ID查询所有分类
     */
    @Select("SELECT * FROM product_category WHERE store_id = #{storeId} ORDER BY sort_order, category_id")
    List<ProductCategory> selectByStoreId(@Param("storeId") Integer storeId);
    
    /**
     * 根据父分类ID查询子分类
     */
    @Select("SELECT * FROM product_category WHERE store_id = #{storeId} AND parent_id = #{parentId} ORDER BY sort_order, category_id")
    List<ProductCategory> selectByParentId(@Param("storeId") Integer storeId, @Param("parentId") Integer parentId);
    
    /**
     * 根据分类ID查询
     */
    @Select("SELECT * FROM product_category WHERE category_id = #{categoryId}")
    ProductCategory selectById(@Param("categoryId") Integer categoryId);
    
    /**
     * 检查分类下是否有子分类
     */
    @Select("SELECT COUNT(*) FROM product_category WHERE parent_id = #{categoryId}")
    int countByParentId(@Param("categoryId") Integer categoryId);
    
    /**
     * 检查分类下是否有商品
     */
    @Select("SELECT COUNT(*) FROM spu_info WHERE category_id = #{categoryId}")
    int countProductsByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 插入分类
     */
    @Insert("INSERT INTO product_category (store_id, parent_id, category_name, description, category_level, sort_order, is_visible, icon_url) " +
            "VALUES (#{storeId}, #{parentId}, #{categoryName}, #{description}, #{categoryLevel}, #{sortOrder}, #{isVisible}, #{iconUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "categoryId")
    int insert(ProductCategory category);
    
    /**
     * 更新分类
     */
    @Update("UPDATE product_category SET category_name = #{categoryName}, description = #{description}, " +
            "sort_order = #{sortOrder}, is_visible = #{isVisible}, icon_url = #{iconUrl} " +
            "WHERE category_id = #{categoryId}")
    int update(ProductCategory category);
    
    /**
     * 删除分类
     */
    @Delete("DELETE FROM product_category WHERE category_id = #{categoryId}")
    int deleteById(@Param("categoryId") Integer categoryId);
    
    /**
     * 检查同一店铺下分类名是否已存在
     */
    @Select("SELECT COUNT(*) FROM product_category WHERE store_id = #{storeId} AND category_name = #{categoryName} " +
            "AND (#{excludeId} IS NULL OR category_id != #{excludeId})")
    int countByStoreIdAndName(@Param("storeId") Integer storeId, 
                             @Param("categoryName") String categoryName,
                             @Param("excludeId") Integer excludeId);
    
    /**
     * 获取分类层级
     */
    @Select("SELECT CASE WHEN parent_id IS NULL THEN 1 ELSE " +
            "(SELECT category_level + 1 FROM product_category WHERE category_id = pc.parent_id) END AS level " +
            "FROM product_category pc WHERE category_id = #{categoryId}")
    Integer getCategoryLevel(@Param("categoryId") Integer categoryId);
    
    /**
     * 根据店铺ID查询顶级分类
     */
    @Select("SELECT * FROM product_category WHERE store_id = #{storeId} AND parent_id IS NULL ORDER BY sort_order, category_id")
    List<ProductCategory> selectTopLevelByStoreId(@Param("storeId") Integer storeId);
    
    /**
     * 批量查询分类
     */
    @Select("<script>" +
            "SELECT * FROM product_category WHERE category_id IN " +
            "<foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<ProductCategory> selectByIds(@Param("categoryIds") List<Integer> categoryIds);
} 