package com.hunric.service;

import com.hunric.model.SkuInfo;
import com.hunric.model.dto.SkuCreateDTO;
import com.hunric.mapper.SkuInfoMapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU信息服务接口
 */
public interface SkuInfoService {

    /**
     * 创建SKU
     */
    SkuInfo createSku(Integer spuId, SkuCreateDTO skuCreateDTO);

    /**
     * 根据ID获取SKU信息
     */
    SkuInfo getSkuById(Integer skuId);

    /**
     * 根据SPU ID获取SKU列表
     */
    List<SkuInfo> getSkuListBySpuId(Integer spuId);

    /**
     * 更新SKU信息
     */
    SkuInfo updateSku(Integer skuId, SkuCreateDTO skuCreateDTO);

    /**
     * 删除SKU
     */
    boolean deleteSku(Integer skuId);

    /**
     * 批量删除SKU
     */
    boolean batchDeleteSku(List<Integer> skuIds);

    /**
     * 批量更新SKU价格
     */
    boolean batchUpdateSkuPrice(List<SkuInfoMapper.SkuPriceUpdate> updates);

    /**
     * 批量更新SKU库存
     */
    boolean batchUpdateSkuStock(List<SkuInfoMapper.SkuStockUpdate> updates);

    /**
     * 批量更新SKU状态
     */
    boolean batchUpdateSkuStatus(List<Integer> skuIds, Integer status);
} 