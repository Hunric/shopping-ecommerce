package com.hunric.controller;

import com.hunric.common.model.ApiResponse;
import com.hunric.model.dto.StoreExtendedDTO;
import com.hunric.service.StoreExtendedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 扩展店铺控制器
 */
@RestController
@RequestMapping("/api/merchant/store-extended")
@Slf4j
public class StoreExtendedController {

    @Autowired
    private StoreExtendedService storeExtendedService;

    /**
     * 根据商家ID获取店铺列表（扩展版）
     * 
     * @param merchantId 商家ID
     * @param request HTTP请求对象，用于获取JWT中的商家信息
     * @return 店铺列表
     */
    @GetMapping("/merchant/{merchantId}")
    public ApiResponse<List<StoreExtendedDTO>> getStoresByMerchantId(
            @PathVariable Integer merchantId, 
            HttpServletRequest request) {
        log.info("获取商家店铺列表（扩展版），merchantId: {}", merchantId);
        
        try {
            // 参数验证
            if (merchantId == null || merchantId <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }
            
            // 权限验证：检查JWT中的商家ID是否与请求的商家ID一致
            Long jwtMerchantId = (Long) request.getAttribute("merchantId");
            if (jwtMerchantId == null) {
                log.warn("未找到JWT中的商家信息，可能未登录或token无效");
                return ApiResponse.error("用户未登录或登录已过期");
            }
            
            if (!jwtMerchantId.equals(merchantId.longValue())) {
                log.warn("权限验证失败: JWT中的商家ID={}, 请求的商家ID={}", jwtMerchantId, merchantId);
                return ApiResponse.error("无权限访问其他商家的数据");
            }
            
            log.debug("权限验证通过: merchantId={}", merchantId);
            
            return storeExtendedService.getStoresByMerchantId(merchantId);
        } catch (Exception e) {
            log.error("获取商家店铺列表异常", e);
            return ApiResponse.error("获取店铺列表过程中发生异常: " + e.getMessage());
        }
    }

    /**
     * 根据店铺ID获取店铺信息（扩展版）
     */
    @GetMapping("/{storeId}")
    public ApiResponse<StoreExtendedDTO> getStoreById(@PathVariable Integer storeId) {
        log.info("获取店铺信息（扩展版），storeId: {}", storeId);
        return storeExtendedService.getStoreById(storeId);
    }

    /**
     * 创建店铺（扩展版）
     */
    @PostMapping
    public ApiResponse<StoreExtendedDTO> createStore(@RequestBody StoreExtendedCreateRequest request) {
        log.info("创建店铺（扩展版），merchantId: {}, storeName: {}", request.getMerchantId(), request.getStoreName());
        
        return storeExtendedService.createStore(
                request.getMerchantId(),
                request.getStoreName(),
                request.getStoreLogo(),
                request.getStoreDescription(),
                request.getCategory(),
                request.getStatus(),
                request.getServicePromise(),
                request.getServicePhone(),
                request.getServiceEmail(),
                request.getBusinessHours()
        );
    }

    /**
     * 更新店铺信息（扩展版）
     */
    @PutMapping("/{storeId}")
    public ApiResponse<StoreExtendedDTO> updateStore(@PathVariable Integer storeId, 
                                                   @RequestBody StoreExtendedUpdateRequest request) {
        log.info("更新店铺信息（扩展版），storeId: {}, storeName: {}", storeId, request.getStoreName());
        
        return storeExtendedService.updateStore(
                storeId,
                request.getStoreName(),
                request.getStoreLogo(),
                request.getStoreDescription(),
                request.getCategory(),
                request.getStatus(),
                request.getServicePromise(),
                request.getServicePhone(),
                request.getServiceEmail(),
                request.getBusinessHours()
        );
    }

    /**
     * 删除店铺
     */
    @DeleteMapping("/{storeId}")
    public ApiResponse<String> deleteStore(@PathVariable Integer storeId) {
        log.info("删除店铺，storeId: {}", storeId);
        return storeExtendedService.deleteStore(storeId);
    }

    /**
     * 获取商家店铺数量
     * 
     * @param merchantId 商家ID
     * @param request HTTP请求对象，用于获取JWT中的商家信息
     * @return 店铺数量
     */
    @GetMapping("/count/{merchantId}")
    public ApiResponse<Integer> getStoreCount(
            @PathVariable Integer merchantId, 
            HttpServletRequest request) {
        log.info("获取商家店铺数量，merchantId: {}", merchantId);
        
        try {
            // 参数验证
            if (merchantId == null || merchantId <= 0) {
                return ApiResponse.error("商家ID不能为空");
            }
            
            // 权限验证：检查JWT中的商家ID是否与请求的商家ID一致
            Long jwtMerchantId = (Long) request.getAttribute("merchantId");
            if (jwtMerchantId == null) {
                log.warn("未找到JWT中的商家信息，可能未登录或token无效");
                return ApiResponse.error("用户未登录或登录已过期");
            }
            
            if (!jwtMerchantId.equals(merchantId.longValue())) {
                log.warn("权限验证失败: JWT中的商家ID={}, 请求的商家ID={}", jwtMerchantId, merchantId);
                return ApiResponse.error("无权限访问其他商家的数据");
            }
            
            log.debug("权限验证通过: merchantId={}", merchantId);
            
            return storeExtendedService.getStoreCount(merchantId);
        } catch (Exception e) {
            log.error("获取商家店铺数量异常", e);
            return ApiResponse.error("获取店铺数量过程中发生异常: " + e.getMessage());
        }
    }

    /**
     * 创建店铺请求DTO
     */
    public static class StoreExtendedCreateRequest {
        private Integer merchantId;
        private String storeName;
        private String storeLogo;
        private String storeDescription;
        private String category;
        private String status;
        private List<String> servicePromise;
        private String servicePhone;
        private String serviceEmail;
        private String businessHours;

        // Getters and Setters
        public Integer getMerchantId() { return merchantId; }
        public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
        
        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
        
        public String getStoreLogo() { return storeLogo; }
        public void setStoreLogo(String storeLogo) { this.storeLogo = storeLogo; }
        
        public String getStoreDescription() { return storeDescription; }
        public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public List<String> getServicePromise() { return servicePromise; }
        public void setServicePromise(List<String> servicePromise) { this.servicePromise = servicePromise; }
        
        public String getServicePhone() { return servicePhone; }
        public void setServicePhone(String servicePhone) { this.servicePhone = servicePhone; }
        
        public String getServiceEmail() { return serviceEmail; }
        public void setServiceEmail(String serviceEmail) { this.serviceEmail = serviceEmail; }
        
        public String getBusinessHours() { return businessHours; }
        public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    }

    /**
     * 更新店铺请求DTO
     */
    public static class StoreExtendedUpdateRequest {
        private String storeName;
        private String storeLogo;
        private String storeDescription;
        private String category;
        private String status;
        private List<String> servicePromise;
        private String servicePhone;
        private String serviceEmail;
        private String businessHours;

        // Getters and Setters
        public String getStoreName() { return storeName; }
        public void setStoreName(String storeName) { this.storeName = storeName; }
        
        public String getStoreLogo() { return storeLogo; }
        public void setStoreLogo(String storeLogo) { this.storeLogo = storeLogo; }
        
        public String getStoreDescription() { return storeDescription; }
        public void setStoreDescription(String storeDescription) { this.storeDescription = storeDescription; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public List<String> getServicePromise() { return servicePromise; }
        public void setServicePromise(List<String> servicePromise) { this.servicePromise = servicePromise; }
        
        public String getServicePhone() { return servicePhone; }
        public void setServicePhone(String servicePhone) { this.servicePhone = servicePhone; }
        
        public String getServiceEmail() { return serviceEmail; }
        public void setServiceEmail(String serviceEmail) { this.serviceEmail = serviceEmail; }
        
        public String getBusinessHours() { return businessHours; }
        public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    }
}