package com.hunric.mapper;

import com.hunric.model.AttributeOption;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 属性可选值数据访问层接口
 */
@Mapper
public interface AttributeOptionMapper {
    
    /**
     * 根据属性ID查询可选值列表
     */
    @Select("SELECT * FROM attribute_option WHERE attribute_id = #{attributeId} ORDER BY option_id")
    List<AttributeOption> selectByAttributeId(@Param("attributeId") Integer attributeId);
    
    /**
     * 插入可选值
     */
    @Insert("INSERT INTO attribute_option (attribute_id, option_value) VALUES (#{attributeId}, #{optionValue})")
    @Options(useGeneratedKeys = true, keyProperty = "optionId")
    int insert(AttributeOption option);
    
    /**
     * 批量插入可选值
     */
    @Insert("<script>" +
            "INSERT INTO attribute_option (attribute_id, option_value) VALUES " +
            "<foreach collection='options' item='option' separator=','>" +
            "(#{option.attributeId}, #{option.optionValue})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("options") List<AttributeOption> options);
    
    /**
     * 根据属性ID删除所有可选值
     */
    @Delete("DELETE FROM attribute_option WHERE attribute_id = #{attributeId}")
    int deleteByAttributeId(@Param("attributeId") Integer attributeId);
    
    /**
     * 删除可选值
     */
    @Delete("DELETE FROM attribute_option WHERE option_id = #{optionId}")
    int deleteById(@Param("optionId") Integer optionId);
    
    /**
     * 更新可选值
     */
    @Update("UPDATE attribute_option SET option_value = #{optionValue} WHERE option_id = #{optionId}")
    int update(AttributeOption option);
    
    /**
     * 批量根据属性ID删除可选值
     */
    @Delete("<script>" +
            "DELETE FROM attribute_option WHERE attribute_id IN " +
            "<foreach collection='attributeIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDeleteByAttributeIds(@Param("attributeIds") List<Integer> attributeIds);
} 