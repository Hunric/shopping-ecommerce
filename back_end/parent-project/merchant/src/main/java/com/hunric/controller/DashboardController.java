package com.hunric.controller;

import com.hunric.model.dto.DashboardStatsDTO;
import com.hunric.service.DashboardService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @return 统计数据
     */
    @GetMapping("/stats/{merchantId}")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats(@PathVariable Integer merchantId) {
        log.info("获取商家Dashboard统计数据: merchantId={}", merchantId);
        
        try {
            if (merchantId == null || merchantId <= 0) {
                return ResponseEntity.ok(ApiResponse.error("商家ID不能为空"));
            }
            
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