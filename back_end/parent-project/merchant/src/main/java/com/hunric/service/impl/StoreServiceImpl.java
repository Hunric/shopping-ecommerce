package com.hunric.service.impl;

import com.hunric.common.model.ApiResponse;
import com.hunric.mapper.StoreMapper;
import com.hunric.model.Store;
import com.hunric.model.dto.StoreDTO;
import com.hunric.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 店铺服务实现类
 */
@Slf4j
@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    /**
     * 根据商家ID获取店铺列表
     */
    @Override
    public ApiResponse<List<StoreDTO>> getStoresByMerchantId(Integer merchantId) {
        try {
            if (merchantId == null || merchantId <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }

            List<Store> stores = storeMapper.selectByMerchantId(merchantId);
            List<StoreDTO> storeDTOs = stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("获取商家店铺列表成功: merchantId={}, 店铺数量={}", merchantId, storeDTOs.size());
            return ApiResponse.success(storeDTOs);

        } catch (Exception e) {
            log.error("获取商家店铺列表失败: merchantId={}", merchantId, e);
            return ApiResponse.error("获取店铺列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据店铺ID获取店铺信息
     */
    @Override
    public ApiResponse<StoreDTO> getStoreById(Integer storeId) {
        try {
            if (storeId == null || storeId <= 0) {
                return ApiResponse.error("店铺ID不能为空");
            }

            Store store = storeMapper.selectById(storeId);
            if (store == null) {
                return ApiResponse.error("店铺不存在");
            }

            StoreDTO storeDTO = convertToDTO(store);
            log.info("获取店铺信息成功: storeId={}", storeId);
            return ApiResponse.success(storeDTO);

        } catch (Exception e) {
            log.error("获取店铺信息失败: storeId={}", storeId, e);
            return ApiResponse.error("获取店铺信息失败: " + e.getMessage());
        }
    }

    /**
     * 创建店铺
     */
    @Override
    @Transactional
    public ApiResponse<StoreDTO> createStore(Integer merchantId, String storeName, String storeLogo, String storeDescription) {
        try {
            // 参数验证
            if (merchantId == null || merchantId <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }
            if (storeName == null || storeName.trim().isEmpty()) {
                return ApiResponse.error("店铺名称不能为空");
            }
            if (storeName.length() > 50) {
                return ApiResponse.error("店铺名称不能超过50个字符");
            }

            // 检查店铺名称是否已存在（同一商家下）
            int existCount = storeMapper.countByMerchantIdAndStoreName(merchantId, storeName.trim(), null);
            if (existCount > 0) {
                return ApiResponse.error("店铺名称已存在，请使用其他名称");
            }

            // 创建店铺对象
            Store store = Store.builder()
                    .merchantId(merchantId)
                    .storeName(storeName.trim())
                    .storeLogo(storeLogo)
                    .storeDescription(storeDescription)
                    .openTime(LocalDateTime.now())
                    .status("open")
                    .creditScore(100)
                    .build();

            // 插入数据库
            int result = storeMapper.insert(store);
            if (result <= 0) {
                return ApiResponse.error("创建店铺失败");
            }

            // 查询完整信息并返回
            Store createdStore = storeMapper.selectById(store.getStoreId());
            StoreDTO storeDTO = convertToDTO(createdStore);

            log.info("创建店铺成功: merchantId={}, storeId={}, storeName={}", 
                    merchantId, store.getStoreId(), storeName);
            return ApiResponse.success(storeDTO);

        } catch (Exception e) {
            log.error("创建店铺失败: merchantId={}, storeName={}", merchantId, storeName, e);
            return ApiResponse.error("创建店铺失败: " + e.getMessage());
        }
    }

    /**
     * 更新店铺信息
     */
    @Override
    @Transactional
    public ApiResponse<StoreDTO> updateStore(Integer storeId, String storeName, String storeLogo, 
                                           String storeDescription, String status) {
        try {
            // 参数验证
            if (storeId == null || storeId <= 0) {
                return ApiResponse.error("店铺ID不能为空");
            }

            // 检查店铺是否存在
            Store existingStore = storeMapper.selectById(storeId);
            if (existingStore == null) {
                return ApiResponse.error("店铺不存在");
            }

            // 验证店铺名称
            if (storeName != null && !storeName.trim().isEmpty()) {
                if (storeName.length() > 50) {
                    return ApiResponse.error("店铺名称不能超过50个字符");
                }
                
                // 检查店铺名称是否已存在（排除当前店铺）
                int existCount = storeMapper.countByMerchantIdAndStoreName(
                        existingStore.getMerchantId(), storeName.trim(), storeId);
                if (existCount > 0) {
                    return ApiResponse.error("店铺名称已存在，请使用其他名称");
                }
            }

            // 验证状态
            if (status != null && !status.isEmpty()) {
                if (!"open".equals(status) && !"closed".equals(status) && !"suspended".equals(status)) {
                    return ApiResponse.error("无效的店铺状态");
                }
            }

            // 构建更新对象
            Store updateStore = Store.builder()
                    .storeId(storeId)
                    .storeName(storeName != null && !storeName.trim().isEmpty() ? storeName.trim() : existingStore.getStoreName())
                    .storeLogo(storeLogo != null ? storeLogo : existingStore.getStoreLogo())
                    .storeDescription(storeDescription != null ? storeDescription : existingStore.getStoreDescription())
                    .status(status != null && !status.isEmpty() ? status : existingStore.getStatus())
                    .build();

            // 更新数据库
            int result = storeMapper.update(updateStore);
            if (result <= 0) {
                return ApiResponse.error("更新店铺失败");
            }

            // 查询最新信息并返回
            Store updatedStore = storeMapper.selectById(storeId);
            StoreDTO storeDTO = convertToDTO(updatedStore);

            log.info("更新店铺成功: storeId={}, storeName={}", storeId, updateStore.getStoreName());
            return ApiResponse.success(storeDTO);

        } catch (Exception e) {
            log.error("更新店铺失败: storeId={}", storeId, e);
            return ApiResponse.error("更新店铺失败: " + e.getMessage());
        }
    }

    /**
     * 删除店铺
     */
    @Override
    @Transactional
    public ApiResponse<String> deleteStore(Integer storeId) {
        try {
            if (storeId == null || storeId <= 0) {
                return ApiResponse.error("店铺ID不能为空");
            }

            // 检查店铺是否存在
            Store store = storeMapper.selectById(storeId);
            if (store == null) {
                return ApiResponse.error("店铺不存在");
            }

            // TODO: 这里可以添加业务规则检查
            // 例如：检查店铺是否有未完成的订单、是否有商品在售等

            // 删除店铺
            int result = storeMapper.deleteById(storeId);
            if (result <= 0) {
                return ApiResponse.error("删除店铺失败");
            }

            log.info("删除店铺成功: storeId={}, storeName={}", storeId, store.getStoreName());
            return ApiResponse.success("删除店铺成功");

        } catch (Exception e) {
            log.error("删除店铺失败: storeId={}", storeId, e);
            return ApiResponse.error("删除店铺失败: " + e.getMessage());
        }
    }

    /**
     * 获取商家的店铺统计信息
     */
    @Override
    public ApiResponse<Integer> getStoreCount(Integer merchantId) {
        try {
            if (merchantId == null || merchantId <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }

            int count = storeMapper.countByMerchantId(merchantId);
            log.info("获取商家店铺数量成功: merchantId={}, count={}", merchantId, count);
            return ApiResponse.success(count);

        } catch (Exception e) {
            log.error("获取商家店铺数量失败: merchantId={}", merchantId, e);
            return ApiResponse.error("获取店铺数量失败: " + e.getMessage());
        }
    }

    /**
     * 将Store实体转换为StoreDTO
     */
    private StoreDTO convertToDTO(Store store) {
        if (store == null) {
            return null;
        }

        return StoreDTO.builder()
                .storeId(store.getStoreId())
                .merchantId(store.getMerchantId())
                .storeName(store.getStoreName())
                .storeLogo(store.getStoreLogo())
                .storeDescription(store.getStoreDescription())
                .openTime(store.getOpenTime())
                .status(store.getStatus())
                .creditScore(store.getCreditScore())
                .createTime(store.getCreateTime())
                .updateTime(store.getUpdateTime())
                .build();
    }
} 