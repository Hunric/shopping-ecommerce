package com.hunric.service.impl;

import com.hunric.mapper.StoreMapper;
import com.hunric.model.Store;
import com.hunric.model.dto.StoreDTO;
import com.hunric.service.StoreService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 店铺服务实现类
 */
@Service
@Slf4j
public class StoreServiceImpl implements StoreService {
    
    @Autowired
    private StoreMapper storeMapper;
    
    @Override
    public ApiResponse<List<StoreDTO>> getStoresByMerchantId(Integer merchantId) {
        try {
            List<Store> stores = storeMapper.findByMerchantId(merchantId);
            List<StoreDTO> storeDTOs = stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ApiResponse.success(storeDTOs);
        } catch (Exception e) {
            log.error("获取店铺列表失败", e);
            return ApiResponse.error("获取店铺列表失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<StoreDTO> getStoreById(Integer storeId) {
        try {
            Store store = storeMapper.findById(storeId);
            if (store == null) {
                return ApiResponse.error("店铺不存在");
            }
            
            return ApiResponse.success(convertToDTO(store));
        } catch (Exception e) {
            log.error("获取店铺信息失败", e);
            return ApiResponse.error("获取店铺信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<StoreDTO> createStore(Integer merchantId, String storeName, String storeLogo, String storeDescription) {
        try {
            // 参数校验
            if (storeName == null || storeName.trim().isEmpty()) {
                return ApiResponse.error("店铺名称不能为空");
            }
            
            Store store = Store.builder()
                    .merchantId(merchantId)
                    .storeName(storeName.trim())
                    .storeLogo(storeLogo)
                    .storeDescription(storeDescription)
                    .build();
            
            int result = storeMapper.insert(store);
            if (result > 0) {
                return ApiResponse.success(convertToDTO(store));
            } else {
                return ApiResponse.error("创建店铺失败");
            }
        } catch (Exception e) {
            log.error("创建店铺失败", e);
            return ApiResponse.error("创建店铺失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<StoreDTO> updateStore(Integer storeId, String storeName, String storeLogo, String storeDescription, String status) {
        try {
            Store existingStore = storeMapper.findById(storeId);
            if (existingStore == null) {
                return ApiResponse.error("店铺不存在");
            }
            
            Store store = Store.builder()
                    .storeId(storeId)
                    .storeName(storeName != null ? storeName.trim() : existingStore.getStoreName())
                    .storeLogo(storeLogo != null ? storeLogo : existingStore.getStoreLogo())
                    .storeDescription(storeDescription != null ? storeDescription : existingStore.getStoreDescription())
                    .status(status != null ? status : existingStore.getStatus())
                    .build();
            
            int result = storeMapper.update(store);
            if (result > 0) {
                Store updatedStore = storeMapper.findById(storeId);
                return ApiResponse.success(convertToDTO(updatedStore));
            } else {
                return ApiResponse.error("更新店铺失败");
            }
        } catch (Exception e) {
            log.error("更新店铺失败", e);
            return ApiResponse.error("更新店铺失败: " + e.getMessage());
        }
    }
    
    @Override
    public ApiResponse<String> deleteStore(Integer storeId) {
        try {
            Store existingStore = storeMapper.findById(storeId);
            if (existingStore == null) {
                return ApiResponse.error("店铺不存在");
            }
            
            int result = storeMapper.deleteById(storeId);
            if (result > 0) {
                return ApiResponse.success("删除店铺成功");
            } else {
                return ApiResponse.error("删除店铺失败");
            }
        } catch (Exception e) {
            log.error("删除店铺失败", e);
            return ApiResponse.error("删除店铺失败: " + e.getMessage());
        }
    }
    
    /**
     * 将Store实体转换为StoreDTO
     */
    private StoreDTO convertToDTO(Store store) {
        return StoreDTO.builder()
                .storeId(store.getStoreId())
                .merchantId(store.getMerchantId())
                .storeName(store.getStoreName())
                .storeLogo(store.getStoreLogo())
                .storeDescription(store.getStoreDescription())
                .openTime(store.getOpenTime())
                .status(store.getStatus())
                .creditScore(store.getCreditScore())
                .build();
    }
} 