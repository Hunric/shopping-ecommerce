package com.hunric.service;

import com.hunric.model.Store;
import com.hunric.model.dto.StoreDTO;
import com.hunric.common.model.ApiResponse;

import java.util.List;

/**
 * 店铺服务接口
 */
public interface StoreService {
    
    /**
     * 根据商家ID获取店铺列表
     * 
     * @param merchantId 商家ID
     * @return 店铺列表
     */
    ApiResponse<List<StoreDTO>> getStoresByMerchantId(Integer merchantId);
    
    /**
     * 根据店铺ID获取店铺信息
     * 
     * @param storeId 店铺ID
     * @return 店铺信息
     */
    ApiResponse<StoreDTO> getStoreById(Integer storeId);
    
    /**
     * 创建店铺
     * 
     * @param merchantId 商家ID
     * @param storeName 店铺名称
     * @param storeLogo 店铺Logo
     * @param storeDescription 店铺描述
     * @return 创建结果
     */
    ApiResponse<StoreDTO> createStore(Integer merchantId, String storeName, String storeLogo, String storeDescription);
    
    /**
     * 更新店铺信息
     * 
     * @param storeId 店铺ID
     * @param storeName 店铺名称
     * @param storeLogo 店铺Logo
     * @param storeDescription 店铺描述
     * @param status 店铺状态
     * @return 更新结果
     */
    ApiResponse<StoreDTO> updateStore(Integer storeId, String storeName, String storeLogo, String storeDescription, String status);
    
    /**
     * 删除店铺
     * 
     * @param storeId 店铺ID
     * @return 删除结果
     */
    ApiResponse<String> deleteStore(Integer storeId);
    
    /**
     * 获取商家的店铺统计信息
     * 
     * @param merchantId 商家ID
     * @return 统计信息
     */
    ApiResponse<Integer> getStoreCount(Integer merchantId);
} 