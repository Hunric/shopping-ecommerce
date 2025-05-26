package com.hunric.service.impl;

import com.hunric.mapper.DashboardMapper;
import com.hunric.model.dto.DashboardStatsDTO;
import com.hunric.service.DashboardService;
import com.hunric.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Dashboard服务实现类
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private DashboardMapper dashboardMapper;
    
    @Override
    public ApiResponse<DashboardStatsDTO> getDashboardStats(Integer merchantId) {
        try {
            // 获取各项统计数据
            int storeCount = dashboardMapper.countStoresByMerchantId(merchantId);
            int productCount = dashboardMapper.countProductsByMerchantId(merchantId);
            int orderCount = dashboardMapper.countOrdersByMerchantId(merchantId);
            BigDecimal totalRevenue = dashboardMapper.getTotalRevenueByMerchantId(merchantId);
            int pendingOrderCount = dashboardMapper.countPendingOrdersByMerchantId(merchantId);
            int shippedOrderCount = dashboardMapper.countShippedOrdersByMerchantId(merchantId);
            BigDecimal monthlyRevenue = dashboardMapper.getMonthlyRevenueByMerchantId(merchantId);
            int monthlyOrderCount = dashboardMapper.countMonthlyOrdersByMerchantId(merchantId);
            
            DashboardStatsDTO stats = DashboardStatsDTO.builder()
                    .storeCount(storeCount)
                    .productCount(productCount)
                    .orderCount(orderCount)
                    .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                    .pendingOrderCount(pendingOrderCount)
                    .shippedOrderCount(shippedOrderCount)
                    .monthlyRevenue(monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO)
                    .monthlyOrderCount(monthlyOrderCount)
                    .build();
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取Dashboard统计数据失败", e);
            return ApiResponse.error("获取统计数据失败: " + e.getMessage());
        }
    }
} 