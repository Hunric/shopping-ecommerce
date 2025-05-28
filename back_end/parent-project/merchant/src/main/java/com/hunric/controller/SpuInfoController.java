package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.SpuInfo;
import com.hunric.model.dto.SpuCreateDTO;
import com.hunric.model.dto.SpuStatsDTO;
import com.hunric.service.SpuInfoService;
import com.hunric.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SPU信息控制器
 */
@RestController
@RequestMapping("/api/merchant/spu")
@Slf4j
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 创建SPU
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSpu(@RequestBody SpuCreateDTO spuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo spuInfo = spuInfoService.createSpu(spuCreateDTO);
            response.put("success", true);
            response.put("message", "创建SPU成功");
            response.put("data", spuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据ID获取SPU信息
     */
    @GetMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> getSpuById(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo spuInfo = spuInfoService.getSpuById(spuId);
            if (spuInfo != null) {
                response.put("success", true);
                response.put("data", spuInfo);
            } else {
                response.put("success", false);
                response.put("message", "SPU不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新SPU信息
     */
    @PutMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> updateSpu(@PathVariable Integer spuId, 
                                                         @RequestBody SpuCreateDTO spuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuInfo spuInfo = spuInfoService.updateSpu(spuId, spuCreateDTO);
            response.put("success", true);
            response.put("message", "更新SPU成功");
            response.put("data", spuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除SPU
     */
    @DeleteMapping("/{spuId}")
    public ResponseEntity<Map<String, Object>> deleteSpu(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = spuInfoService.deleteSpu(spuId);
            response.put("success", success);
            response.put("message", success ? "删除SPU成功" : "删除SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量删除SPU
     */
    @DeleteMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteSpu(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> spuIds = (List<Integer>) request.get("spuIds");
            boolean success = spuInfoService.batchDeleteSpu(spuIds);
            response.put("success", success);
            response.put("message", success ? "批量删除SPU成功" : "批量删除SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量删除SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 分页查询SPU列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getSpuList(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = spuInfoService.getSpuList(storeId, categoryId, keyword, 
                                                                   status, page, pageSize, sortBy, sortOrder);
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SPU列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 分页查询SPU列表（简化路径）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpuListSimple(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        return getSpuList(storeId, categoryId, keyword, status, page, pageSize, sortBy, sortOrder);
    }

    /**
     * 获取SPU统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSpuStats(@RequestParam Integer storeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            SpuStatsDTO stats = spuInfoService.getSpuStats(storeId);
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取SPU统计失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 上架SPU
     */
    @PostMapping("/{spuId}/publish")
    public ResponseEntity<Map<String, Object>> publishSpu(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = spuInfoService.publishSpu(spuId);
            response.put("success", success);
            response.put("message", success ? "上架SPU成功" : "上架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "上架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 下架SPU
     */
    @PostMapping("/{spuId}/unpublish")
    public ResponseEntity<Map<String, Object>> unpublishSpu(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = spuInfoService.unpublishSpu(spuId);
            response.put("success", success);
            response.put("message", success ? "下架SPU成功" : "下架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "下架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量上架SPU
     */
    @PostMapping("/batch-publish")
    public ResponseEntity<Map<String, Object>> batchPublishSpu(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> spuIds = (List<Integer>) request.get("spuIds");
            boolean success = spuInfoService.batchPublishSpu(spuIds);
            response.put("success", success);
            response.put("message", success ? "批量上架SPU成功" : "批量上架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量上架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量下架SPU
     */
    @PostMapping("/batch-unpublish")
    public ResponseEntity<Map<String, Object>> batchUnpublishSpu(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> spuIds = (List<Integer>) request.get("spuIds");
            boolean success = spuInfoService.batchUnpublishSpu(spuIds);
            response.put("success", success);
            response.put("message", success ? "批量下架SPU成功" : "批量下架SPU失败");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量下架SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 复制SPU
     */
    @PostMapping("/{spuId}/copy")
    public ResponseEntity<Map<String, Object>> copySpu(@PathVariable Integer spuId, 
                                                       @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String newName = (String) request.get("newName");
            SpuInfo spuInfo = spuInfoService.copySpu(spuId, newName);
            response.put("success", true);
            response.put("message", "复制SPU成功");
            response.put("data", spuInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "复制SPU失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 生成SKU组合
     */
    @PostMapping("/generate-sku-combinations")
    public ResponseEntity<Map<String, Object>> generateSkuCombinations(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> basicAttributes = (Map<String, Object>) request.get("basicAttributes");
            List<Map<String, Object>> combinations = spuInfoService.generateSkuCombinations(basicAttributes);
            response.put("success", true);
            response.put("data", combinations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "生成SKU组合失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // SKU相关API（通过SPU路径访问）
    
    /**
     * 获取指定SPU的SKU列表
     */
    @GetMapping("/{spuId}/sku")
    public ResponseEntity<Map<String, Object>> getSkuListBySpuId(@PathVariable Integer spuId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 这里需要注入SkuInfoService
            List<com.hunric.model.SkuInfo> skuList = skuInfoService.getSkuListBySpuId(spuId);
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
     * 为指定SPU创建SKU
     */
    @PostMapping("/{spuId}/sku")
    public ResponseEntity<Map<String, Object>> createSkuForSpu(@PathVariable Integer spuId, 
                                                               @RequestBody com.hunric.model.dto.SkuCreateDTO skuCreateDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.hunric.model.SkuInfo skuInfo = skuInfoService.createSku(spuId, skuCreateDTO);
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
} 