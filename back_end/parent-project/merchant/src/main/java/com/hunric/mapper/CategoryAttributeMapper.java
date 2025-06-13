package com.hunric.mapper;

import com.hunric.model.CategoryAttribute;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 分类属性数据访问层接口
 */
@Mapper
public interface CategoryAttributeMapper {
    
    /**
     * 根据分类ID查询属性列表
     */
    @Select("SELECT * FROM category_attribute WHERE category_id = #{categoryId} ORDER BY attribute_id")
    List<CategoryAttribute> selectByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 根据属性ID查询
     */
    @Select("SELECT * FROM category_attribute WHERE attribute_id = #{attributeId}")
    CategoryAttribute selectById(@Param("attributeId") Integer attributeId);
    
    /**
     * 插入属性
     */
    @Insert("INSERT INTO category_attribute (store_id, category_id, attribute_name, attribute_type, is_basic_attribute, is_required) " +
            "VALUES (#{storeId}, #{categoryId}, #{attributeName}, #{attributeType}, #{isBasicAttribute}, #{isRequired})")
    @Options(useGeneratedKeys = true, keyProperty = "attributeId")
    int insert(CategoryAttribute attribute);
    
    /**
     * 更新属性
     */
    @Update("UPDATE category_attribute SET attribute_name = #{attributeName}, attribute_type = #{attributeType}, " +
            "is_basic_attribute = #{isBasicAttribute}, is_required = #{isRequired} " +
            "WHERE attribute_id = #{attributeId}")
    int update(CategoryAttribute attribute);
    
    /**
     * 删除属性
     */
    @Delete("DELETE FROM category_attribute WHERE attribute_id = #{attributeId}")
    int deleteById(@Param("attributeId") Integer attributeId);
    
    /**
     * 根据分类ID删除所有属性
     */
    @Delete("DELETE FROM category_attribute WHERE category_id = #{categoryId}")
    int deleteByCategoryId(@Param("categoryId") Integer categoryId);
    
    /**
     * 检查同一分类下属性名是否已存在
     */
    @Select("SELECT COUNT(*) FROM category_attribute WHERE category_id = #{categoryId} AND attribute_name = #{attributeName} " +
            "AND (#{excludeId} IS NULL OR attribute_id != #{excludeId})")
    int countByCategoryIdAndName(@Param("categoryId") Integer categoryId, 
                                @Param("attributeName") String attributeName,
                                @Param("excludeId") Integer excludeId);
    
    /**
     * 批量插入属性
     */
    @Insert("<script>" +
            "INSERT INTO category_attribute (store_id, category_id, attribute_name, attribute_type, is_basic_attribute, is_required) VALUES " +
            "<foreach collection='attributes' item='attr' separator=','>" +
            "(#{attr.storeId}, #{attr.categoryId}, #{attr.attributeName}, #{attr.attributeType}, #{attr.isBasicAttribute}, #{attr.isRequired})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("attributes") List<CategoryAttribute> attributes);
    
    /**
     * 根据店铺ID和分类ID查询属性列表
     */
    @Select("SELECT * FROM category_attribute WHERE store_id = #{storeId} AND category_id = #{categoryId}")
    List<CategoryAttribute> selectByStoreIdAndCategoryId(@Param("storeId") Integer storeId, 
                                                        @Param("categoryId") Integer categoryId);
    
    /**
     * 根据店铺ID查询属性列表
     */
    @Select("SELECT * FROM category_attribute WHERE store_id = #{storeId}")
    List<CategoryAttribute> selectByStoreId(@Param("storeId") Integer storeId);
    
    /**
     * 根据店铺ID统计属性数量
     */
    @Select("SELECT COUNT(*) FROM category_attribute WHERE store_id = #{storeId}")
    int countByStoreId(@Param("storeId") Integer storeId);
} 