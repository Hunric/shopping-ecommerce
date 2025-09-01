package com.hunric.controller;

import com.hunric.model.dto.DashboardStatsDTO;
import com.hunric.service.DashboardService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Dashboard控制器
 */
@RestController
@RequestMapping("/api/merchant/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 获取Dashboard统计数据
     * 
     * @param merchantId 商家ID
     * @param request HTTP请求对象，用于获取JWT中的商家信息
     * @return 统计数据
     */
    @GetMapping("/stats/{merchantId}")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats(
            @PathVariable Integer merchantId, 
            HttpServletRequest request) {
        log.info("获取商家Dashboard统计数据: merchantId={}", merchantId);
        
        try {
            // 参数验证
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
            // 权限验证：检查JWT中的商家ID是否与请求的商家ID一致
            Long jwtMerchantId = (Long) request.getAttribute("merchantId");
            if (jwtMerchantId == null) {
                log.warn("未找到JWT中的商家信息，可能未登录或token无效");
                return ResponseEntity.ok(ApiResponse.error("用户未登录或登录已过期"));
            }
            
            if (!jwtMerchantId.equals(merchantId.longValue())) {
                log.warn("权限验证失败: JWT中的商家ID={}, 请求的商家ID={}", jwtMerchantId, merchantId);
                return ResponseEntity.ok(ApiResponse.error("无权限访问其他商家的数据"));
            }
            
            log.debug("权限验证通过: merchantId={}", merchantId);
            
            ApiResponse<DashboardStatsDTO> response = dashboardService.getDashboardStats(merchantId);
            
            if (response.isSuccess()) {
                log.info("获取Dashboard统计数据成功: merchantId={}", merchantId);
            } else {
                log.warn("获取Dashboard统计数据失败: merchantId={}, 原因: {}", merchantId, response.getMessage());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取Dashboard统计数据异常", e);
            return ResponseEntity.ok(ApiResponse.error("获取统计数据过程中发生异常: " + e.getMessage()));
        }
    }
}