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
     */
    ApiResponse<List<StoreDTO>> getStoresByMerchantId(Integer merchantId);
    
    /**
     * 根据店铺ID获取店铺信息
     */
    ApiResponse<StoreDTO> getStoreById(Integer storeId);
    
    /**
     * 创建店铺
     */
    ApiResponse<StoreDTO> createStore(Integer merchantId, String storeName, String storeLogo, String storeDescription);
    
    /**
     * 更新店铺信息
     */
    ApiResponse<StoreDTO> updateStore(Integer storeId, String storeName, String storeLogo, String storeDescription, String status);
    
    /**
     * 删除店铺
     */
    ApiResponse<String> deleteStore(Integer storeId);
} 