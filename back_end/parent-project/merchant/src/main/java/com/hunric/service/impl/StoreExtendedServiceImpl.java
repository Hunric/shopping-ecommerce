package com.hunric.service.impl;

import com.hunric.common.model.ApiResponse;
import com.hunric.mapper.StoreExtendedMapper;
import com.hunric.mapper.SpuInfoMapper;
import com.hunric.mapper.CategoryAttributeMapper;
import com.hunric.model.StoreExtended;
import com.hunric.model.dto.StoreExtendedDTO;
import com.hunric.service.StoreExtendedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 扩展店铺服务实现类
 */
@Slf4j
@Service
public class StoreExtendedServiceImpl implements StoreExtendedService {

    @Autowired
    private StoreExtendedMapper storeExtendedMapper;
    
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    
    @Autowired
    private CategoryAttributeMapper categoryAttributeMapper;

    @Override
    public ApiResponse<List<StoreExtendedDTO>> getStoresByMerchantId(Integer merchantId) {
        try {
            log.info("获取商家店铺列表，merchantId: {}", merchantId);
            
            List<StoreExtended> stores = storeExtendedMapper.selectByMerchantId(merchantId);
            List<StoreExtendedDTO> storeDTOs = stores.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            log.info("成功获取商家店铺列表，数量: {}", storeDTOs.size());
            return ApiResponse.success(storeDTOs);
            
        } catch (Exception e) {
            log.error("获取商家店铺列表失败，merchantId: {}", merchantId, e);
            return ApiResponse.error("获取店铺列表失败");
        }
    }

    @Override
    public ApiResponse<StoreExtendedDTO> getStoreById(Integer storeId) {
        try {
            log.info("获取店铺信息，storeId: {}", storeId);
            
            StoreExtended store = storeExtendedMapper.selectById(storeId);
            if (store == null) {
                return ApiResponse.error("店铺不存在");
            }
            
            StoreExtendedDTO storeDTO = convertToDTO(store);
            log.info("成功获取店铺信息，storeName: {}", store.getStoreName());
            return ApiResponse.success(storeDTO);
            
        } catch (Exception e) {
            log.error("获取店铺信息失败，storeId: {}", storeId, e);
            return ApiResponse.error("获取店铺信息失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<StoreExtendedDTO> createStore(Integer merchantId, String storeName, String storeLogo,
                                                   String storeDescription, String category, String status,
                                                   List<String> servicePromise, String servicePhone,
                                                   String serviceEmail, String businessHours) {
        try {
            log.info("创建店铺，merchantId: {}, storeName: {}", merchantId, storeName);
            
            // 检查店铺名称是否已存在
            int existingCount = storeExtendedMapper.countByMerchantIdAndStoreName(merchantId, storeName, null);
            if (existingCount > 0) {
                return ApiResponse.error("店铺名称已存在");
            }
            
            // 创建店铺实体
            StoreExtended store = StoreExtended.builder()
                    .merchantId(merchantId)
                    .storeName(storeName)
                    .storeLogo(storeLogo)
                    .storeDescription(storeDescription)
                    .category(category)
                    .status(status != null ? status : "open")
                    .servicePromise(servicePromise)
                    .servicePhone(servicePhone)
                    .serviceEmail(serviceEmail)
                    .businessHours(businessHours != null ? businessHours : "周一至周日 9:00-22:00")
                    .creditScore(100)
                    .build();
            
            // 插入数据库
            int result = storeExtendedMapper.insert(store);
            if (result > 0) {
                StoreExtendedDTO storeDTO = convertToDTO(store);
                log.info("店铺创建成功，storeId: {}, storeName: {}", store.getStoreId(), storeName);
                return ApiResponse.success(storeDTO);
            } else {
                return ApiResponse.error("创建店铺失败");
            }
            
        } catch (Exception e) {
            log.error("创建店铺失败，merchantId: {}, storeName: {}", merchantId, storeName, e);
            return ApiResponse.error("创建店铺失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<StoreExtendedDTO> updateStore(Integer storeId, String storeName, String storeLogo,
                                                   String storeDescription, String category, String status,
                                                   List<String> servicePromise, String servicePhone,
                                                   String serviceEmail, String businessHours) {
        try {
            log.info("更新店铺信息，storeId: {}, storeName: {}", storeId, storeName);
            
            // 检查店铺是否存在
            StoreExtended existingStore = storeExtendedMapper.selectById(storeId);
            if (existingStore == null) {
                return ApiResponse.error("店铺不存在");
            }
            
            // 检查店铺名称是否已被其他店铺使用
            int existingCount = storeExtendedMapper.countByMerchantIdAndStoreName(
                    existingStore.getMerchantId(), storeName, storeId);
            if (existingCount > 0) {
                return ApiResponse.error("店铺名称已存在");
            }
            
            // 更新店铺信息
            StoreExtended store = StoreExtended.builder()
                    .storeId(storeId)
                    .storeName(storeName)
                    .storeLogo(storeLogo)
                    .storeDescription(storeDescription)
                    .category(category)
                    .status(status)
                    .servicePromise(servicePromise)
                    .servicePhone(servicePhone)
                    .serviceEmail(serviceEmail)
                    .businessHours(businessHours)
                    .build();
            
            // 更新数据库
            int result = storeExtendedMapper.update(store);
            if (result > 0) {
                // 重新查询更新后的数据
                StoreExtended updatedStore = storeExtendedMapper.selectById(storeId);
                StoreExtendedDTO storeDTO = convertToDTO(updatedStore);
                log.info("店铺更新成功，storeId: {}, storeName: {}", storeId, storeName);
                return ApiResponse.success(storeDTO);
            } else {
                return ApiResponse.error("更新店铺失败");
            }
            
        } catch (Exception e) {
            log.error("更新店铺失败，storeId: {}, storeName: {}", storeId, storeName, e);
            return ApiResponse.error("更新店铺失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteStore(Integer storeId) {
        try {
            log.info("删除店铺，storeId: {}", storeId);
            
            // 检查店铺是否存在
            StoreExtended store = storeExtendedMapper.selectById(storeId);
            if (store == null) {
                return ApiResponse.error("店铺不存在");
            }
            
            // 检查店铺是否有商品
            int productCount = spuInfoMapper.countSpuByStoreId(storeId);
            if (productCount > 0) {
                log.warn("尝试删除存在商品的店铺: storeId={}, productCount={}", storeId, productCount);
                return ApiResponse.error("无法删除店铺，该店铺中还有" + productCount + "个商品，请先删除所有商品");
            }
            
            // 检查是否有关联的分类属性
            int attributeCount = categoryAttributeMapper.countByStoreId(storeId);
            if (attributeCount > 0) {
                log.warn("尝试删除存在分类属性的店铺: storeId={}, attributeCount={}", storeId, attributeCount);
                return ApiResponse.error("无法删除店铺，该店铺关联了" + attributeCount + "个分类属性，请先删除所有分类属性");
            }
            
            // 删除店铺
            int result = storeExtendedMapper.deleteById(storeId);
            if (result > 0) {
                log.info("店铺删除成功，storeId: {}", storeId);
                return ApiResponse.success("删除成功");
            } else {
                return ApiResponse.error("删除店铺失败");
            }
            
        } catch (Exception e) {
            log.error("删除店铺失败，storeId: {}", storeId, e);
            return ApiResponse.error("删除店铺失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<Integer> getStoreCount(Integer merchantId) {
        try {
            log.info("获取商家店铺数量，merchantId: {}", merchantId);
            
            int count = storeExtendedMapper.countByMerchantId(merchantId);
            log.info("成功获取商家店铺数量，count: {}", count);
            return ApiResponse.success(count);
            
        } catch (Exception e) {
            log.error("获取商家店铺数量失败，merchantId: {}", merchantId, e);
            return ApiResponse.error("获取店铺数量失败");
        }
    }

    /**
     * 将实体转换为DTO
     */
    private StoreExtendedDTO convertToDTO(StoreExtended store) {
        StoreExtendedDTO dto = new StoreExtendedDTO();
        BeanUtils.copyProperties(store, dto);
        return dto;
    }
} 