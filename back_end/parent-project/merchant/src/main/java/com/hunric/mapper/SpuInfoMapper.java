package com.hunric.mapper;

import com.hunric.model.SpuInfo;
import com.hunric.model.dto.SpuStatsDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * SPU信息Mapper接口
 */
@Mapper
public interface SpuInfoMapper {

    /**
     * 插入SPU信息
     */
    @Insert("INSERT INTO spu_info (merchant_id, store_id, category_id, product_name, description, " +
            "image_url, display_price, basic_attributes, non_basic_attributes, brand_name, " +
            "selling_point, unit, status, create_time, update_time) " +
            "VALUES (#{merchantId}, #{storeId}, #{categoryId}, #{spuName}, #{spuDescription}, " +
            "#{productImage}, #{displayPrice}, #{basicAttributes,typeHandler=com.hunric.config.JsonTypeHandler}, " +
            "#{nonBasicAttributes,typeHandler=com.hunric.config.JsonTypeHandler}, #{brandName}, " +
            "#{sellingPoint}, #{unit}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "spuId", keyColumn = "product_id")
    int insertSpu(SpuInfo spuInfo);

    /**
     * 根据ID查询SPU信息
     */
    @Select("SELECT product_id, merchant_id, store_id, category_id, product_name, description, " +
            "image_url, display_price, basic_attributes, non_basic_attributes, brand_name, " +
            "selling_point, unit, status, create_time, update_time " +
            "FROM spu_info WHERE product_id = #{spuId}")
    @Results({
        @Result(property = "spuId", column = "product_id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "spuName", column = "product_name"),
        @Result(property = "spuDescription", column = "description"),
        @Result(property = "productImage", column = "image_url"),
        @Result(property = "displayPrice", column = "display_price"),
        @Result(property = "basicAttributes", column = "basic_attributes", typeHandler = com.hunric.config.JsonTypeHandler.class),
        @Result(property = "nonBasicAttributes", column = "non_basic_attributes", typeHandler = com.hunric.config.JsonTypeHandler.class),
        @Result(property = "brandName", column = "brand_name"),
        @Result(property = "sellingPoint", column = "selling_point"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    SpuInfo selectSpuById(Integer spuId);

    /**
     * 更新SPU信息
     */
    @Update("UPDATE spu_info SET product_name = #{spuName}, description = #{spuDescription}, " +
            "image_url = #{productImage}, display_price = #{displayPrice}, " +
            "basic_attributes = #{basicAttributes,typeHandler=com.hunric.config.JsonTypeHandler}, " +
            "non_basic_attributes = #{nonBasicAttributes,typeHandler=com.hunric.config.JsonTypeHandler}, " +
            "brand_name = #{brandName}, selling_point = #{sellingPoint}, unit = #{unit}, " +
            "status = #{status}, update_time = NOW() WHERE product_id = #{spuId}")
    int updateSpu(SpuInfo spuInfo);

    /**
     * 删除SPU信息
     */
    @Delete("DELETE FROM spu_info WHERE product_id = #{spuId}")
    int deleteSpu(Integer spuId);

    /**
     * 批量删除SPU信息
     */
    @Delete("<script>" +
            "DELETE FROM spu_info WHERE product_id IN " +
            "<foreach collection='spuIds' item='spuId' open='(' separator=',' close=')'>" +
            "#{spuId}" +
            "</foreach>" +
            "</script>")
    int batchDeleteSpu(@Param("spuIds") List<Integer> spuIds);

    /**
     * 分页查询SPU列表
     */
    @Select("<script>" +
            "SELECT product_id, merchant_id, store_id, category_id, product_name, description, " +
            "image_url, display_price, basic_attributes, non_basic_attributes, brand_name, " +
            "selling_point, unit, status, create_time, update_time " +
            "FROM spu_info WHERE 1=1 " +
            "<if test='storeId != null'>AND store_id = #{storeId}</if> " +
            "<if test='categoryId != null'>AND category_id = #{categoryId}</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND product_name LIKE CONCAT('%', #{keyword}, '%')</if> " +
            "<if test='status != null and status != \"\"'>AND status = #{status}</if> " +
            "ORDER BY " +
            "<choose>" +
            "<when test='sortBy != null and sortBy != \"\"'>" +
            "#{sortBy} " +
            "<if test='sortOrder != null and sortOrder != \"\"'>#{sortOrder}</if>" +
            "</when>" +
            "<otherwise>create_time DESC</otherwise>" +
            "</choose> " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    @Results({
        @Result(property = "spuId", column = "product_id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "spuName", column = "product_name"),
        @Result(property = "spuDescription", column = "description"),
        @Result(property = "productImage", column = "image_url"),
        @Result(property = "displayPrice", column = "display_price"),
        @Result(property = "basicAttributes", column = "basic_attributes", typeHandler = com.hunric.config.JsonTypeHandler.class),
        @Result(property = "nonBasicAttributes", column = "non_basic_attributes", typeHandler = com.hunric.config.JsonTypeHandler.class),
        @Result(property = "brandName", column = "brand_name"),
        @Result(property = "sellingPoint", column = "selling_point"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<SpuInfo> selectSpuList(Map<String, Object> params);

    /**
     * 查询SPU总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM spu_info WHERE 1=1 " +
            "<if test='storeId != null'>AND store_id = #{storeId}</if> " +
            "<if test='categoryId != null'>AND category_id = #{categoryId}</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND product_name LIKE CONCAT('%', #{keyword}, '%')</if> " +
            "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</script>")
    int countSpu(Map<String, Object> params);

    /**
     * 获取SPU统计信息
     */
    @Select("SELECT " +
            "COUNT(*) as totalCount, " +
            "SUM(CASE WHEN status = 'on_shelf' THEN 1 ELSE 0 END) as onShelfCount, " +
            "SUM(CASE WHEN status = 'off_shelf' THEN 1 ELSE 0 END) as offShelfCount, " +
            "SUM(CASE WHEN status = 'draft' THEN 1 ELSE 0 END) as draftCount, " +
            "SUM(display_price) as totalValue " +
            "FROM spu_info WHERE store_id = #{storeId}")
    @Results({
        @Result(property = "totalCount", column = "totalCount"),
        @Result(property = "onShelfCount", column = "onShelfCount"),
        @Result(property = "offShelfCount", column = "offShelfCount"),
        @Result(property = "draftCount", column = "draftCount"),
        @Result(property = "totalValue", column = "totalValue")
    })
    SpuStatsDTO getSpuStats(Integer storeId);

    /**
     * 批量更新SPU状态
     */
    @Update("<script>" +
            "UPDATE spu_info SET status = #{status}, update_time = NOW() WHERE product_id IN " +
            "<foreach collection='spuIds' item='spuId' open='(' separator=',' close=')'>" +
            "#{spuId}" +
            "</foreach>" +
            "</script>")
    int batchUpdateSpuStatus(@Param("spuIds") List<Integer> spuIds, @Param("status") String status);

    /**
     * 检查SPU名称是否存在
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM spu_info WHERE store_id = #{storeId} AND product_name = #{spuName} " +
            "<if test='spuId != null'>AND product_id != #{spuId}</if>" +
            "</script>")
    int checkSpuNameExists(@Param("storeId") Integer storeId, @Param("spuName") String spuName, @Param("spuId") Integer spuId);
} 