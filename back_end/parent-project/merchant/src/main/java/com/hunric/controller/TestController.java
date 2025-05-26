package com.hunric.controller;

import com.hunric.model.dto.DashboardStatsDTO;
import com.hunric.model.dto.StoreDTO;
import com.hunric.service.DashboardService;
import com.hunric.service.StoreService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 测试控制器 - 用于验证接口功能
 */
@RestController
@RequestMapping("/api/test")
@Slf4j
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private StoreService storeService;
    
    /**
     * 测试Dashboard统计接口
     */
    @GetMapping("/dashboard/{merchantId}")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> testDashboard(@PathVariable Integer merchantId) {
        log.info("测试Dashboard接口: merchantId={}", merchantId);
        
        try {
            // 创建模拟数据
            DashboardStatsDTO mockStats = DashboardStatsDTO.builder()
                    .storeCount(3)
                    .productCount(156)
                    .orderCount(89)
                    .totalRevenue(new BigDecimal("12580.50"))
                    .pendingOrderCount(15)
                    .shippedOrderCount(25)
                    .monthlyRevenue(new BigDecimal("3250.80"))
                    .monthlyOrderCount(28)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(mockStats));
        } catch (Exception e) {
            log.error("测试Dashboard接口异常", e);
            return ResponseEntity.ok(ApiResponse.error("测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 测试店铺列表接口
     */
    @GetMapping("/stores/{merchantId}")
    public ResponseEntity<ApiResponse<List<StoreDTO>>> testStoreList(@PathVariable Integer merchantId) {
        log.info("测试店铺列表接口: merchantId={}", merchantId);
        
        try {
            // 创建模拟店铺数据
            List<StoreDTO> mockStores = Arrays.asList(
                StoreDTO.builder()
                        .storeId(1)
                        .merchantId(merchantId)
                        .storeName("时尚服装店")
                        .storeLogo("https://via.placeholder.com/200x200/409EFF/FFFFFF?text=Logo1")
                        .storeDescription("专营时尚女装，品质保证，款式新颖")
                        .openTime(LocalDateTime.now().minusMonths(6))
                        .status("open")
                        .creditScore(98)
                        .build(),
                StoreDTO.builder()
                        .storeId(2)
                        .merchantId(merchantId)
                        .storeName("数码科技馆")
                        .storeLogo("https://via.placeholder.com/200x200/67C23A/FFFFFF?text=Logo2")
                        .storeDescription("最新数码产品，正品保障，售后无忧")
                        .openTime(LocalDateTime.now().minusMonths(3))
                        .status("open")
                        .creditScore(95)
                        .build(),
                StoreDTO.builder()
                        .storeId(3)
                        .merchantId(merchantId)
                        .storeName("美食天地")
                        .storeLogo("https://via.placeholder.com/200x200/E6A23C/FFFFFF?text=Logo3")
                        .storeDescription("精选美食，新鲜直达，品味生活")
                        .openTime(LocalDateTime.now().minusMonths(1))
                        .status("suspended")
                        .creditScore(92)
                        .build()
            );
            
            return ResponseEntity.ok(ApiResponse.success(mockStores));
        } catch (Exception e) {
            log.error("测试店铺列表接口异常", e);
            return ResponseEntity.ok(ApiResponse.error("测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 测试创建店铺接口
     */
    @PostMapping("/store/create")
    public ResponseEntity<ApiResponse<StoreDTO>> testCreateStore(@RequestBody Object requestBody) {
        log.info("测试创建店铺接口: {}", requestBody);
        
        try {
            // 创建模拟响应数据
            StoreDTO mockStore = StoreDTO.builder()
                    .storeId(999)
                    .merchantId(1)
                    .storeName("测试店铺")
                    .storeLogo("https://via.placeholder.com/200x200/F56C6C/FFFFFF?text=New")
                    .storeDescription("这是一个测试创建的店铺")
                    .openTime(LocalDateTime.now())
                    .status("open")
                    .creditScore(100)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(mockStore));
        } catch (Exception e) {
            log.error("测试创建店铺接口异常", e);
            return ResponseEntity.ok(ApiResponse.error("测试失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("商家管理服务运行正常"));
    }
} 