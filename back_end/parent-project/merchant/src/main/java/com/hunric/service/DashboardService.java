package com.hunric.service;

import com.hunric.model.dto.DashboardStatsDTO;
import com.hunric.common.model.ApiResponse;

/**
 * Dashboard服务接口
 */
public interface DashboardService {
    
    /**
     * 获取商家Dashboard统计数据
     */
    ApiResponse<DashboardStatsDTO> getDashboardStats(Integer merchantId);
} 