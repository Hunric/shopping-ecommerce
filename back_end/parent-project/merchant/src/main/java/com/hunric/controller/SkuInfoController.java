package com.hunric.controller;

import com.hunric.model.SkuInfo;
import com.hunric.model.dto.SkuCreateDTO;
import com.hunric.service.SkuInfoService;
import com.hunric.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SKU信息控制器
 */
@RestController
@RequestMapping("/api/merchant/sku")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 创建SKU
     */
    @PostMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> createSku(@PathVariable Integer spuId, 
                                                         @RequestBody SkuCreateDTO skuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            SkuInfo skuInfo = skuInfoService.createSku(spuId, skuCreateDTO);
            response.put("success", true);
            response.put("message", "创建SKU成功");
            response.put("data", skuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建SKU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取SKU信息
     */
    @GetMapping("/{skuId}")
    public ResponseEntity<Map<String, Object>> getSkuById(@PathVariable Integer skuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SkuInfo skuInfo = skuInfoService.getSkuById(skuId);
            if (skuInfo != null) {
                response.put("success", true);
                response.put("data", skuInfo);
            } else {
                response.put("success", false);
                response.put("message", "SKU不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SKU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据SPU ID获取SKU列表
     */
    @GetMapping("/spu/{spuId}")
    public ResponseEntity<Map<String, Object>> getSkuListBySpuId(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SkuInfo> skuList = skuInfoService.getSkuListBySpuId(spuId);
            response.put("success", true);
            response.put("data", skuList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SKU列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新SKU信息
     */
    @PutMapping("/{skuId}")
    public ResponseEntity<Map<String, Object>> updateSku(@PathVariable Integer skuId, 
                                                         @RequestBody SkuCreateDTO skuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("=== 更新SKU请求开始 ===");
            System.out.println("SKU ID: " + skuId);
            System.out.println("请求数据: " + skuCreateDTO.toString());
            
            SkuInfo skuInfo = skuInfoService.updateSku(skuId, skuCreateDTO);
            
            System.out.println("更新成功，返回数据: " + skuInfo.toString());
            System.out.println("=== 更新SKU请求结束 ===");
            
            response.put("success", true);
            response.put("message", "更新SKU成功");
            response.put("data", skuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== 更新SKU失败 ===");
            System.err.println("SKU ID: " + skuId);
            System.err.println("请求数据: " + skuCreateDTO.toString());
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== 错误信息结束 ===");
            
            response.put("success", false);
            response.put("message", "更新SKU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除SKU
     */
    @DeleteMapping("/{skuId}")
    public ResponseEntity<Map<String, Object>> deleteSku(@PathVariable Integer skuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = skuInfoService.deleteSku(skuId);
            response.put("success", success);
            response.put("message", success ? "删除SKU成功" : "删除SKU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除SKU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量删除SKU
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchDeleteSku(@RequestBody List<Integer> skuIds) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = skuInfoService.batchDeleteSku(skuIds);
            response.put("success", success);
            response.put("message", success ? "批量删除SKU成功" : "批量删除SKU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量删除SKU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量更新SKU价格
     */
    @PostMapping("/batch-update-price")
    public ResponseEntity<Map<String, Object>> batchUpdateSkuPrice(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> updatesList = (List<Map<String, Object>>) request.get("updates");
            List<SkuInfoMapper.SkuPriceUpdate> updates = updatesList.stream()
                .map(update -> new SkuInfoMapper.SkuPriceUpdate(
                    (Integer) update.get("skuId"),
                    new java.math.BigDecimal(update.get("salePrice").toString())
                ))
                .collect(java.util.stream.Collectors.toList());
            
            boolean success = skuInfoService.batchUpdateSkuPrice(updates);
            response.put("success", success);
            response.put("message", success ? "批量更新SKU价格成功" : "批量更新SKU价格失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量更新SKU价格失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量更新SKU库存
     */
    @PostMapping("/batch-update-stock")
    public ResponseEntity<Map<String, Object>> batchUpdateSkuStock(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> updatesList = (List<Map<String, Object>>) request.get("updates");
            List<SkuInfoMapper.SkuStockUpdate> updates = updatesList.stream()
                .map(update -> new SkuInfoMapper.SkuStockUpdate(
                    (Integer) update.get("skuId"),
                    (Integer) update.get("stockQuantity")
                ))
                .collect(java.util.stream.Collectors.toList());
            
            boolean success = skuInfoService.batchUpdateSkuStock(updates);
            response.put("success", success);
            response.put("message", success ? "批量更新SKU库存成功" : "批量更新SKU库存失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量更新SKU库存失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量更新SKU状态
     */
    @PutMapping("/batch/status")
    public ResponseEntity<Map<String, Object>> batchUpdateSkuStatus(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> skuIds = (List<Integer>) request.get("skuIds");
            Integer status = (Integer) request.get("status");
            
            boolean success = skuInfoService.batchUpdateSkuStatus(skuIds, status);
            response.put("success", success);
            response.put("message", success ? "批量更新SKU状态成功" : "批量更新SKU状态失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量更新SKU状态失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 