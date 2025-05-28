package com.hunric.service;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.StoreExtendedDTO;

import java.util.List;

/**
 * 扩展店铺服务接口
 */
public interface StoreExtendedService {
    
    /**
     * 根据商家ID获取店铺列表（扩展版）
     * 
     * @param merchantId 商家ID
     * @return 店铺列表
     */
    ApiResponse<List<StoreExtendedDTO>> getStoresByMerchantId(Integer merchantId);
    
    /**
     * 根据店铺ID获取店铺信息（扩展版）
     * 
     * @param storeId 店铺ID
     * @return 店铺信息
     */
    ApiResponse<StoreExtendedDTO> getStoreById(Integer storeId);
    
    /**
     * 创建店铺（扩展版）
     * 
     * @param merchantId 商家ID
     * @param storeName 店铺名称
     * @param storeLogo 店铺Logo
     * @param storeDescription 店铺描述
     * @param category 经营类目
     * @param status 店铺状态
     * @param servicePromise 服务承诺
     * @param servicePhone 客服电话
     * @param serviceEmail 客服邮箱
     * @param businessHours 营业时间
     * @return 创建结果
     */
    ApiResponse<StoreExtendedDTO> createStore(Integer merchantId, String storeName, String storeLogo, 
                                            String storeDescription, String category, String status,
                                            List<String> servicePromise, String servicePhone, 
                                            String serviceEmail, String businessHours);
    
    /**
     * 更新店铺信息（扩展版）
     * 
     * @param storeId 店铺ID
     * @param storeName 店铺名称
     * @param storeLogo 店铺Logo
     * @param storeDescription 店铺描述
     * @param category 经营类目
     * @param status 店铺状态
     * @param servicePromise 服务承诺
     * @param servicePhone 客服电话
     * @param serviceEmail 客服邮箱
     * @param businessHours 营业时间
     * @return 更新结果
     */
    ApiResponse<StoreExtendedDTO> updateStore(Integer storeId, String storeName, String storeLogo, 
                                            String storeDescription, String category, String status,
                                            List<String> servicePromise, String servicePhone, 
                                            String serviceEmail, String businessHours);
    
    /**
     * 删除店铺
     * 
     * @param storeId 店铺ID
     * @return 删除结果
     */
    ApiResponse<String> deleteStore(Integer storeId);
    
    /**
     * 获取商家店铺数量
     * 
     * @param merchantId 商家ID
     * @return 店铺数量
     */
    ApiResponse<Integer> getStoreCount(Integer merchantId);
} 