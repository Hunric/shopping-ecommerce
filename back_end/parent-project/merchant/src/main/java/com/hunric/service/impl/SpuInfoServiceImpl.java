package com.hunric.service.impl;

import com.hunric.mapper.SpuInfoMapper;
import com.hunric.mapper.SkuInfoMapper;
import com.hunric.model.SpuInfo;
import com.hunric.model.SkuInfo;
import com.hunric.model.dto.SpuCreateDTO;
import com.hunric.model.dto.SpuStatsDTO;
import com.hunric.model.dto.SkuCreateDTO;
import com.hunric.service.SpuInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * SPU信息服务实现类
 */
@Service
public class SpuInfoServiceImpl implements SpuInfoService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Override
    @Transactional
    public SpuInfo createSpu(SpuCreateDTO spuCreateDTO) {
        // 检查SPU名称是否已存在
        if (spuInfoMapper.checkSpuNameExists(spuCreateDTO.getStoreId(), spuCreateDTO.getSpuName(), null) > 0) {
            throw new RuntimeException("商品名称已存在");
        }
        
        // 验证必要字段
        if (spuCreateDTO.getStoreId() == null || spuCreateDTO.getMerchantId() == null
                || spuCreateDTO.getCategoryId() == null || spuCreateDTO.getSpuName() == null
                || spuCreateDTO.getDisplayPrice() == null) {
            throw new IllegalArgumentException("必要字段不能为空");
        }

        // 创建SPU
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(spuCreateDTO, spuInfo);
        
        // 使用前端传递的status，如果没有则默认为draft
        if (spuCreateDTO.getStatus() == null || spuCreateDTO.getStatus().isEmpty()) {
            spuInfo.setStatus("draft");
        } else {
            spuInfo.setStatus(spuCreateDTO.getStatus());
        }
        
        int result = spuInfoMapper.insertSpu(spuInfo);
        if (result <= 0) {
            throw new RuntimeException("创建SPU失败");
        }

        // 创建SKU
        if (spuCreateDTO.getSkus() != null && !spuCreateDTO.getSkus().isEmpty()) {
            for (SkuCreateDTO skuCreateDTO : spuCreateDTO.getSkus()) {
                // 检查SKU名称是否已存在（在同一SPU内）
                if (skuInfoMapper.checkSkuNameExists(skuCreateDTO.getSkuName(), spuInfo.getSpuId(), null) > 0) {
                    throw new RuntimeException("SKU名称已存在: " + skuCreateDTO.getSkuName());
                }

                SkuInfo skuInfo = new SkuInfo();
                BeanUtils.copyProperties(skuCreateDTO, skuInfo);
                skuInfo.setSpuId(spuInfo.getSpuId());
                
                // 设置默认值
                if (skuInfo.getWarnStock() == null) {
                    skuInfo.setWarnStock(10);
                }
                if (skuInfo.getStatus() == null) {
                    skuInfo.setStatus(2); // 默认状态
                }

                int skuResult = skuInfoMapper.insertSku(skuInfo);
                if (skuResult <= 0) {
                    throw new RuntimeException("创建SKU失败: " + skuCreateDTO.getSkuName());
                }
            }
        }
        
        return spuInfo;
    }
    
    @Override
    @Transactional
    public List<SpuInfo> batchCreateSpu(List<SpuCreateDTO> spuCreateDTOList) {
        if (spuCreateDTOList == null || spuCreateDTOList.isEmpty()) {
            throw new IllegalArgumentException("商品列表不能为空");
        }
        
        List<SpuInfo> createdSpus = new ArrayList<>();
        
        for (SpuCreateDTO spuCreateDTO : spuCreateDTOList) {
            try {
                SpuInfo spuInfo = createSpu(spuCreateDTO);
                createdSpus.add(spuInfo);
            } catch (Exception e) {
                // 记录错误但继续处理其他商品
                System.err.println("创建商品失败: " + spuCreateDTO.getSpuName() + ", 错误: " + e.getMessage());
            }
        }
        
        return createdSpus;
    }

    @Override
    public SpuInfo getSpuById(Integer spuId) {
        return spuInfoMapper.selectSpuById(spuId);
    }

    @Override
    @Transactional
    public SpuInfo updateSpu(Integer spuId, SpuCreateDTO spuCreateDTO) {
        SpuInfo existingSpu = spuInfoMapper.selectSpuById(spuId);
        if (existingSpu == null) {
            throw new RuntimeException("SPU不存在");
        }

        // 检查SPU名称是否已存在（排除当前SPU）
        if (spuInfoMapper.checkSpuNameExists(spuCreateDTO.getStoreId(), spuCreateDTO.getSpuName(), spuId) > 0) {
            throw new RuntimeException("商品名称已存在");
        }

        // 更新SPU
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(spuCreateDTO, spuInfo);
        spuInfo.setSpuId(spuId);
        
        int result = spuInfoMapper.updateSpu(spuInfo);
        if (result <= 0) {
            throw new RuntimeException("更新SPU失败");
        }

        // 删除原有SKU
        skuInfoMapper.deleteSkuBySpuId(spuId);

        // 创建新的SKU
        if (spuCreateDTO.getSkus() != null && !spuCreateDTO.getSkus().isEmpty()) {
            for (SkuCreateDTO skuCreateDTO : spuCreateDTO.getSkus()) {
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

                int skuResult = skuInfoMapper.insertSku(skuInfo);
                if (skuResult <= 0) {
                    throw new RuntimeException("创建SKU失败: " + skuCreateDTO.getSkuName());
                }
            }
        }

        return spuInfoMapper.selectSpuById(spuId);
    }

    @Override
    @Transactional
    public boolean deleteSpu(Integer spuId) {
        // 先删除相关SKU
        skuInfoMapper.deleteSkuBySpuId(spuId);
        
        // 再删除SPU
        int result = spuInfoMapper.deleteSpu(spuId);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean batchDeleteSpu(List<Integer> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return false;
        }

        // 删除相关SKU
        for (Integer spuId : spuIds) {
            skuInfoMapper.deleteSkuBySpuId(spuId);
        }
        
        // 批量删除SPU
        int result = spuInfoMapper.batchDeleteSpu(spuIds);
        return result > 0;
    }

    @Override
    public Map<String, Object> getSpuList(Integer storeId, Integer categoryId, String keyword, 
                                          String status, Integer page, Integer pageSize, 
                                          String sortBy, String sortOrder) {
        Map<String, Object> params = new HashMap<>();
        params.put("storeId", storeId);
        params.put("categoryId", categoryId);
        params.put("keyword", keyword);
        params.put("status", status);
        params.put("sortBy", sortBy);
        params.put("sortOrder", sortOrder);
        
        // 分页参数
        int offset = (page - 1) * pageSize;
        params.put("offset", offset);
        params.put("pageSize", pageSize);

        List<SpuInfo> spuList = spuInfoMapper.selectSpuList(params);
        int total = spuInfoMapper.countSpu(params);

        Map<String, Object> result = new HashMap<>();
        result.put("list", spuList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return result;
    }

    @Override
    public SpuStatsDTO getSpuStats(Integer storeId) {
        SpuStatsDTO stats = spuInfoMapper.getSpuStats(storeId);
        if (stats == null) {
            stats = new SpuStatsDTO(0, 0, 0, 0, 0, null);
        }
        
        // 获取库存不足数量
        int lowStockCount = skuInfoMapper.getLowStockCount(storeId);
        stats.setLowStockCount(lowStockCount);
        
        return stats;
    }

    @Override
    public boolean publishSpu(Integer spuId) {
        SpuInfo spuInfo = spuInfoMapper.selectSpuById(spuId);
        if (spuInfo == null) {
            throw new RuntimeException("SPU不存在");
        }
        
        spuInfo.setStatus("on_shelf");
        int result = spuInfoMapper.updateSpu(spuInfo);
        return result > 0;
    }

    @Override
    public boolean unpublishSpu(Integer spuId) {
        SpuInfo spuInfo = spuInfoMapper.selectSpuById(spuId);
        if (spuInfo == null) {
            throw new RuntimeException("SPU不存在");
        }
        
        spuInfo.setStatus("off_shelf");
        int result = spuInfoMapper.updateSpu(spuInfo);
        return result > 0;
    }

    @Override
    public boolean batchPublishSpu(List<Integer> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return false;
        }
        
        int result = spuInfoMapper.batchUpdateSpuStatus(spuIds, "on_shelf");
        return result > 0;
    }

    @Override
    public boolean batchUnpublishSpu(List<Integer> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return false;
        }
        
        int result = spuInfoMapper.batchUpdateSpuStatus(spuIds, "off_shelf");
        return result > 0;
    }

    @Override
    @Transactional
    public SpuInfo copySpu(Integer spuId, String newName) {
        SpuInfo originalSpu = spuInfoMapper.selectSpuById(spuId);
        if (originalSpu == null) {
            throw new RuntimeException("原SPU不存在");
        }

        // 检查新名称是否已存在
        if (spuInfoMapper.checkSpuNameExists(originalSpu.getStoreId(), newName, null) > 0) {
            throw new RuntimeException("商品名称已存在");
        }

        // 复制SPU
        SpuInfo newSpu = new SpuInfo();
        BeanUtils.copyProperties(originalSpu, newSpu);
        newSpu.setSpuId(null);
        newSpu.setSpuName(newName);
        newSpu.setStatus("draft"); // 复制的商品默认为草稿状态
        
        int result = spuInfoMapper.insertSpu(newSpu);
        if (result <= 0) {
            throw new RuntimeException("复制SPU失败");
        }

        // 复制SKU
        List<SkuInfo> originalSkus = skuInfoMapper.selectSkuListBySpuId(spuId);
        for (SkuInfo originalSku : originalSkus) {
            SkuInfo newSku = new SkuInfo();
            BeanUtils.copyProperties(originalSku, newSku);
            newSku.setSkuId(null);
            newSku.setSpuId(newSpu.getSpuId());
            newSku.setSkuName(newName + "-" + originalSku.getSkuName().substring(originalSku.getSkuName().indexOf("-") + 1));
            newSku.setStatus(2); // 复制的SKU默认下架
            
            skuInfoMapper.insertSku(newSku);
        }

        return newSpu;
    }

    @Override
    public List<Map<String, Object>> generateSkuCombinations(Map<String, Object> basicAttributes) {
        System.out.println("[SKU_DEBUG] --- generateSkuCombinations START ---");
        if (basicAttributes == null || basicAttributes.isEmpty()) {
            System.out.println("[SKU_DEBUG] Input basicAttributes is null or empty. Returning empty list.");
            return new ArrayList<>();
        }
        System.out.println("[SKU_DEBUG] Input basicAttributes: " + basicAttributes);

        List<String> attributeNames = new ArrayList<>(basicAttributes.keySet());
        List<List<String>> attributeValuesList = new ArrayList<>();

        for (String key : attributeNames) {
            Object value = basicAttributes.get(key);
            System.out.println("[SKU_DEBUG] Processing attribute '" + key + "' with value: " + value);
            if (value instanceof List) {
                List<?> rawList = (List<?>) value;
                if (rawList.isEmpty()) {
                    System.out.println("[SKU_DEBUG] Attribute '" + key + "' has an empty list of values. No combinations possible. Returning empty list.");
                    return new ArrayList<>();
                }
                List<String> values = new ArrayList<>();
                for (Object item : rawList) {
                    values.add(String.valueOf(item));
                }
                attributeValuesList.add(values);
                System.out.println("[SKU_DEBUG] Successfully processed attribute '" + key + "'. Values: " + values);
            } else {
                System.out.println("[SKU_DEBUG] Attribute '" + key + "' has invalid format (not a List). Value type: " + (value != null ? value.getClass().getName() : "null") + ". Returning empty list.");
                return new ArrayList<>();
            }
        }

        List<Map<String, Object>> combinations = new ArrayList<>();
        System.out.println("[SKU_DEBUG] Starting recursion with " + attributeNames.size() + " attributes.");
        generateCombinationsRecursive(0, new LinkedHashMap<>(), attributeNames, attributeValuesList, combinations);
        System.out.println("[SKU_DEBUG] --- generateSkuCombinations END --- Total combinations generated: " + combinations.size());
        return combinations;
    }

    private void generateCombinationsRecursive(int index,
                                               Map<String, String> currentCombination,
                                               final List<String> attributeNames,
                                               final List<List<String>> attributeValuesList,
                                               final List<Map<String, Object>> totalCombinations) {

        String indent = String.join("", Collections.nCopies(index, "  "));
        System.out.println(indent + "[SKU_DEBUG] Entering recursion at index: " + index + ". Current combination: " + currentCombination);

        if (index == attributeNames.size()) {
            System.out.println(indent + "[SKU_DEBUG] Base case reached. Building final SKU object.");
            Map<String, Object> skuObject = new HashMap<>();
            Map<String, String> attributeCombinationMap = new LinkedHashMap<>(currentCombination);

            StringBuilder skuNameBuilder = new StringBuilder();
            boolean first = true;
            for (String value : attributeCombinationMap.values()) {
                if (!first) {
                    skuNameBuilder.append(" ");
                }
                skuNameBuilder.append(value);
                first = false;
            }

            skuObject.put("skuName", skuNameBuilder.toString());
            skuObject.put("attributeCombination", attributeCombinationMap);
            skuObject.put("salePrice", 0.0);
            skuObject.put("stockQuantity", 0);
            skuObject.put("warnStock", 10);

            totalCombinations.add(skuObject);
            System.out.println(indent + "[SKU_DEBUG] Added new SKU to list: " + skuObject);
            return;
        }

        String currentAttributeName = attributeNames.get(index);
        List<String> currentAttributeValues = attributeValuesList.get(index);
        System.out.println(indent + "[SKU_DEBUG] Looping through values for attribute '" + currentAttributeName + "': " + currentAttributeValues);

        for (String value : currentAttributeValues) {
            currentCombination.put(currentAttributeName, value);
            System.out.println(indent + "[SKU_DEBUG] Recursing with index " + (index + 1) + " and combination: " + currentCombination);
            generateCombinationsRecursive(index + 1, currentCombination, attributeNames, attributeValuesList, totalCombinations);
            currentCombination.remove(currentAttributeName); // Backtrack
            System.out.println(indent + "[SKU_DEBUG] Backtracked. Removed '" + currentAttributeName + "'. Current combination: " + currentCombination);
        }
         System.out.println(indent + "[SKU_DEBUG] Finished loop for index " + index);
    }
    
    @Override
    public Integer getStoreMerchantId(Integer storeId) {
        // 从数据库中获取店铺对应的商家ID
        return spuInfoMapper.getStoreMerchantId(storeId);
    }
} 