package com.hunric.mapper;

import com.hunric.model.StoreExtended;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 扩展店铺数据访问层接口
 */
@Mapper
public interface StoreExtendedMapper {
    
    /**
     * 根据商家ID查询店铺列表（扩展版）
     * 
     * @param merchantId 商家ID
     * @return 店铺列表
     */
    @Select("SELECT store_id, merchant_id, store_name, store_logo, store_description, " +
            "category, status, service_promise, service_phone, service_email, business_hours, " +
            "credit_score, create_time, update_time " +
            "FROM store_info WHERE merchant_id = #{merchantId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "storeName", column = "store_name"),
        @Result(property = "storeLogo", column = "store_logo"),
        @Result(property = "storeDescription", column = "store_description"),
        @Result(property = "category", column = "category"),
        @Result(property = "status", column = "status"),
        @Result(property = "servicePromise", column = "service_promise", typeHandler = com.hunric.config.JsonListTypeHandler.class),
        @Result(property = "servicePhone", column = "service_phone"),
        @Result(property = "serviceEmail", column = "service_email"),
        @Result(property = "businessHours", column = "business_hours"),
        @Result(property = "creditScore", column = "credit_score"),
        @Result(property = "openTime", column = "create_time"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<StoreExtended> selectByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 根据店铺ID查询店铺信息（扩展版）
     * 
     * @param storeId 店铺ID
     * @return 店铺信息
     */
    @Select("SELECT store_id, merchant_id, store_name, store_logo, store_description, " +
            "category, status, service_promise, service_phone, service_email, business_hours, " +
            "credit_score, create_time, update_time " +
            "FROM store_info WHERE store_id = #{storeId}")
    @Results({
        @Result(property = "storeId", column = "store_id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "storeName", column = "store_name"),
        @Result(property = "storeLogo", column = "store_logo"),
        @Result(property = "storeDescription", column = "store_description"),
        @Result(property = "category", column = "category"),
        @Result(property = "status", column = "status"),
        @Result(property = "servicePromise", column = "service_promise", typeHandler = com.hunric.config.JsonListTypeHandler.class),
        @Result(property = "servicePhone", column = "service_phone"),
        @Result(property = "serviceEmail", column = "service_email"),
        @Result(property = "businessHours", column = "business_hours"),
        @Result(property = "creditScore", column = "credit_score"),
        @Result(property = "openTime", column = "create_time"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    StoreExtended selectById(@Param("storeId") Integer storeId);
    
    /**
     * 插入新店铺（扩展版）
     * 
     * @param store 店铺信息
     * @return 影响行数
     */
    @Insert("INSERT INTO store_info (merchant_id, store_name, store_logo, store_description, category, " +
            "status, service_promise, service_phone, service_email, business_hours, credit_score) " +
            "VALUES (#{merchantId}, #{storeName}, #{storeLogo}, #{storeDescription}, #{category}, " +
            "#{status}, #{servicePromise, typeHandler=com.hunric.config.JsonListTypeHandler}, " +
            "#{servicePhone}, #{serviceEmail}, #{businessHours}, #{creditScore})")
    @Options(useGeneratedKeys = true, keyProperty = "storeId")
    int insert(StoreExtended store);
    
    /**
     * 更新店铺信息（扩展版）
     * 
     * @param store 店铺信息
     * @return 影响行数
     */
    @Update("UPDATE store_info SET " +
            "store_name = #{storeName}, " +
            "store_logo = #{storeLogo}, " +
            "store_description = #{storeDescription}, " +
            "category = #{category}, " +
            "status = #{status}, " +
            "service_promise = #{servicePromise, typeHandler=com.hunric.config.JsonListTypeHandler}, " +
            "service_phone = #{servicePhone}, " +
            "service_email = #{serviceEmail}, " +
            "business_hours = #{businessHours} " +
            "WHERE store_id = #{storeId}")
    int update(StoreExtended store);
    
    /**
     * 删除店铺
     * 
     * @param storeId 店铺ID
     * @return 影响行数
     */
    @Delete("DELETE FROM store_info WHERE store_id = #{storeId}")
    int deleteById(@Param("storeId") Integer storeId);
    
    /**
     * 根据商家ID统计店铺数量
     * 
     * @param merchantId 商家ID
     * @return 店铺数量
     */
    @Select("SELECT COUNT(*) FROM store_info WHERE merchant_id = #{merchantId}")
    int countByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 检查店铺名称是否已存在（同一商家下）
     * 
     * @param merchantId 商家ID
     * @param storeName 店铺名称
     * @param excludeStoreId 排除的店铺ID（用于更新时检查）
     * @return 存在的店铺数量
     */
    @Select("SELECT COUNT(*) FROM store_info WHERE merchant_id = #{merchantId} " +
            "AND store_name = #{storeName} " +
            "AND (#{excludeStoreId} IS NULL OR store_id != #{excludeStoreId})")
    int countByMerchantIdAndStoreName(@Param("merchantId") Integer merchantId, 
                                     @Param("storeName") String storeName,
                                     @Param("excludeStoreId") Integer excludeStoreId);
} 