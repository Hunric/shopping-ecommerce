package com.hunric.controller;

import com.hunric.model.dto.StoreDTO;
import com.hunric.service.StoreService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 店铺控制器
 */
@RestController
@RequestMapping("/api/merchant/store")
@Slf4j
@CrossOrigin(origins = "*")
public class StoreController {

    @Autowired
    private StoreService storeService;
    
    /**
     * 根据商家ID获取店铺列表
     * 
     * @param merchantId 商家ID
     * @return 店铺列表
     */
    @GetMapping("/list/{merchantId}")
    public ResponseEntity<ApiResponse<List<StoreDTO>>> getStoresByMerchantId(@PathVariable Integer merchantId) {
        log.info("获取商家店铺列表: merchantId={}", merchantId);
        
        try {
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
            ApiResponse<List<StoreDTO>> response = storeService.getStoresByMerchantId(merchantId);
            
            if (response.isSuccess()) {
                log.info("获取店铺列表成功: merchantId={}, 店铺数量={}", merchantId, response.getData().size());
            } else {
                log.warn("获取店铺列表失败: merchantId={}, 原因: {}", merchantId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取店铺列表异常", e);
            return ResponseEntity.ok(ApiResponse.error("获取店铺列表过程中发生异常: " + e.getMessage()));
        }
    }
    
    /**
     * 根据店铺ID获取店铺信息
     * 
     * @param storeId 店铺ID
     * @return 店铺信息
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreDTO>> getStoreById(@PathVariable Integer storeId) {
        log.info("获取店铺信息: storeId={}", storeId);
        
        try {
            if (storeId == null || storeId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("店铺ID不能为空"));
            }
            
            ApiResponse<StoreDTO> response = storeService.getStoreById(storeId);
            
            if (response.isSuccess()) {
                log.info("获取店铺信息成功: storeId={}", storeId);
            } else {
                log.warn("获取店铺信息失败: storeId={}, 原因: {}", storeId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取店铺信息异常", e);
            return ResponseEntity.ok(ApiResponse.error("获取店铺信息过程中发生异常: " + e.getMessage()));
        }
    }
    
    /**
     * 创建店铺
     * 
     * @param requestBody 创建店铺请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StoreDTO>> createStore(@RequestBody Map<String, Object> requestBody) {
        log.info("创建店铺请求: {}", requestBody);
        
        try {
            Integer merchantId = (Integer) requestBody.get("merchantId");
            String storeName = (String) requestBody.get("storeName");
            String storeLogo = (String) requestBody.get("storeLogo");
            String storeDescription = (String) requestBody.get("storeDescription");
            
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
            if (storeName == null || storeName.trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("店铺名称不能为空"));
            }
            
            ApiResponse<StoreDTO> response = storeService.createStore(merchantId, storeName, storeLogo, storeDescription);
            
            if (response.isSuccess()) {
                log.info("创建店铺成功: merchantId={}, storeName={}", merchantId, storeName);
            } else {
                log.warn("创建店铺失败: merchantId={}, storeName={}, 原因: {}", merchantId, storeName, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("创建店铺异常", e);
            return ResponseEntity.ok(ApiResponse.error("创建店铺过程中发生异常: " + e.getMessage()));
        }
    }
    
    /**
     * 更新店铺信息
     * 
     * @param storeId 店铺ID
     * @param requestBody 更新店铺请求
     * @return 更新结果
     */
    @PutMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreDTO>> updateStore(@PathVariable Integer storeId, @RequestBody Map<String, Object> requestBody) {
        log.info("更新店铺请求: storeId={}, data={}", storeId, requestBody);
        
        try {
            if (storeId == null || storeId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("店铺ID不能为空"));
            }
            
            String storeName = (String) requestBody.get("storeName");
            String storeLogo = (String) requestBody.get("storeLogo");
            String storeDescription = (String) requestBody.get("storeDescription");
            String status = (String) requestBody.get("status");
            
            ApiResponse<StoreDTO> response = storeService.updateStore(storeId, storeName, storeLogo, storeDescription, status);
            
            if (response.isSuccess()) {
                log.info("更新店铺成功: storeId={}", storeId);
            } else {
                log.warn("更新店铺失败: storeId={}, 原因: {}", storeId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新店铺异常", e);
            return ResponseEntity.ok(ApiResponse.error("更新店铺过程中发生异常: " + e.getMessage()));
        }
    }
    
    /**
     * 删除店铺
     * 
     * @param storeId 店铺ID
     * @return 删除结果
     */
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<String>> deleteStore(@PathVariable Integer storeId) {
        log.info("删除店铺请求: storeId={}", storeId);
        
        try {
            if (storeId == null || storeId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("店铺ID不能为空"));
            }
            
            ApiResponse<String> response = storeService.deleteStore(storeId);
            
            if (response.isSuccess()) {
                log.info("删除店铺成功: storeId={}", storeId);
            } else {
                log.warn("删除店铺失败: storeId={}, 原因: {}", storeId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("删除店铺异常", e);
            return ResponseEntity.ok(ApiResponse.error("删除店铺过程中发生异常: " + e.getMessage()));
        }
    }
} 