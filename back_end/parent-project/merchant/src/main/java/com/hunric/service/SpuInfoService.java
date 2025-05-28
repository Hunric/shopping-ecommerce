package com.hunric.service;

import com.hunric.model.SpuInfo;
import com.hunric.model.dto.SpuCreateDTO;
import com.hunric.model.dto.SpuStatsDTO;

import java.util.List;
import java.util.Map;

/**
 * SPU信息服务接口
 */
public interface SpuInfoService {

    /**
     * 创建SPU
     */
    SpuInfo createSpu(SpuCreateDTO spuCreateDTO);

    /**
     * 根据ID获取SPU信息
     */
    SpuInfo getSpuById(Integer spuId);

    /**
     * 更新SPU信息
     */
    SpuInfo updateSpu(Integer spuId, SpuCreateDTO spuCreateDTO);

    /**
     * 删除SPU
     */
    boolean deleteSpu(Integer spuId);

    /**
     * 批量删除SPU
     */
    boolean batchDeleteSpu(List<Integer> spuIds);

    /**
     * 分页查询SPU列表
     */
    Map<String, Object> getSpuList(Integer storeId, Integer categoryId, String keyword, 
                                   String status, Integer page, Integer pageSize, 
                                   String sortBy, String sortOrder);

    /**
     * 获取SPU统计信息
     */
    SpuStatsDTO getSpuStats(Integer storeId);

    /**
     * 上架SPU
     */
    boolean publishSpu(Integer spuId);

    /**
     * 下架SPU
     */
    boolean unpublishSpu(Integer spuId);

    /**
     * 批量上架SPU
     */
    boolean batchPublishSpu(List<Integer> spuIds);

    /**
     * 批量下架SPU
     */
    boolean batchUnpublishSpu(List<Integer> spuIds);

    /**
     * 复制SPU
     */
    SpuInfo copySpu(Integer spuId, String newName);

    /**
     * 生成SKU组合
     */
    List<Map<String, Object>> generateSkuCombinations(Map<String, Object> basicAttributes);
} 