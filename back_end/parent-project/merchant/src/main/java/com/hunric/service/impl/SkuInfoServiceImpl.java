package com.hunric.service.impl;

import com.hunric.mapper.SkuInfoMapper;
import com.hunric.model.SkuInfo;
import com.hunric.model.dto.SkuCreateDTO;
import com.hunric.service.SkuInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SKU信息服务实现类
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Override
    @Transactional
    public SkuInfo createSku(Integer spuId, SkuCreateDTO skuCreateDTO) {
        // 检查SKU名称是否已存在（在同一SPU内）
        if (skuInfoMapper.checkSkuNameExists(skuCreateDTO.getSkuName(), spuId, null) > 0) {
            throw new RuntimeException("SKU名称已存在");
        }

        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuCreateDTO, skuInfo);
        skuInfo.setSpuId(spuId);
        
        // 设置默认值
        if (skuInfo.getWarnStock() == null) {
            skuInfo.setWarnStock(10);
        }
        if (skuInfo.getStatus() == null) {
            skuInfo.setStatus(2); // 默认下架
        }

        int result = skuInfoMapper.insertSku(skuInfo);
        if (result <= 0) {
            throw new RuntimeException("创建SKU失败");
        }

        return skuInfo;
    }

    @Override
    public SkuInfo getSkuById(Integer skuId) {
        return skuInfoMapper.selectSkuById(skuId);
    }

    @Override
    public List<SkuInfo> getSkuListBySpuId(Integer spuId) {
        return skuInfoMapper.selectSkuListBySpuId(spuId);
    }

    @Override
    @Transactional
    public SkuInfo updateSku(Integer skuId, SkuCreateDTO skuCreateDTO) {
        SkuInfo existingSku = skuInfoMapper.selectSkuById(skuId);
        if (existingSku == null) {
            throw new RuntimeException("SKU不存在");
        }

        // 检查SKU名称是否已存在（在同一SPU内，排除当前SKU）
        if (skuInfoMapper.checkSkuNameExists(skuCreateDTO.getSkuName(), existingSku.getSpuId(), skuId) > 0) {
            throw new RuntimeException("SKU名称已存在");
        }

        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuCreateDTO, skuInfo);
        skuInfo.setSkuId(skuId);
        skuInfo.setSpuId(existingSku.getSpuId()); // 保持原有的SPU ID

        int result = skuInfoMapper.updateSku(skuInfo);
        if (result <= 0) {
            throw new RuntimeException("更新SKU失败");
        }

        return skuInfoMapper.selectSkuById(skuId);
    }

    @Override
    @Transactional
    public boolean deleteSku(Integer skuId) {
        int result = skuInfoMapper.deleteSku(skuId);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchDeleteSku(List<Integer> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return false;
        }

        int result = skuInfoMapper.batchDeleteSku(skuIds);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchUpdateSkuPrice(List<SkuInfoMapper.SkuPriceUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            return false;
        }

        int result = skuInfoMapper.batchUpdateSkuPrice(updates);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchUpdateSkuStock(List<SkuInfoMapper.SkuStockUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            return false;
        }

        int result = skuInfoMapper.batchUpdateSkuStock(updates);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchUpdateSkuStatus(List<Integer> skuIds, Integer status) {
        if (skuIds == null || skuIds.isEmpty()) {
            return false;
        }

        int result = skuInfoMapper.batchUpdateSkuStatus(skuIds, status);
        return result > 0;
    }
} 