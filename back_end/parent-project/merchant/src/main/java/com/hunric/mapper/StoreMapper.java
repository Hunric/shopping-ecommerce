package com.hunric.mapper;

import com.hunric.model.Store;
import com.hunric.model.dto.StoreDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 店铺信息Mapper接口
 */
@Mapper
public interface StoreMapper {
    
    /**
     * 根据商家ID查询店铺列表
     */
    @Select("SELECT * FROM store_info WHERE merchant_id = #{merchantId}")
    List<Store> findByMerchantId(@Param("merchantId") Integer merchantId);
    
    /**
     * 根据店铺ID查询店铺信息
     */
    @Select("SELECT * FROM store_info WHERE store_id = #{storeId}")
    Store findById(@Param("storeId") Integer storeId);
    
    /**
     * 创建店铺
     */
    @Insert("INSERT INTO store_info (merchant_id, store_name, store_logo, store_description, open_time, status, credit_score) " +
            "VALUES (#{merchantId}, #{storeName}, #{storeLogo}, #{storeDescription}, NOW(), 'open', 100)")
    @Options(useGeneratedKeys = true, keyProperty = "storeId")
    int insert(Store store);
    
    /**
     * 更新店铺信息
     */
    @Update("UPDATE store_info SET store_name = #{storeName}, store_logo = #{storeLogo}, " +
            "store_description = #{storeDescription}, status = #{status} WHERE store_id = #{storeId}")
    int update(Store store);
    
    /**
     * 删除店铺
     */
    @Delete("DELETE FROM store_info WHERE store_id = #{storeId}")
    int deleteById(@Param("storeId") Integer storeId);
    
    /**
     * 统计商家的店铺数量
     */
    @Select("SELECT COUNT(*) FROM store_info WHERE merchant_id = #{merchantId}")
    int countByMerchantId(@Param("merchantId") Integer merchantId);
} 