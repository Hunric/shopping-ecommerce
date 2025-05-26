package com.hunric.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Dashboard统计数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    
    /**
     * 店铺数量
     */
    private Integer storeCount;
    
    /**
     * 商品数量
     */
    private Integer productCount;
    
    /**
     * 订单数量
     */
    private Integer orderCount;
    
    /**
     * 总收入
     */
    private BigDecimal totalRevenue;
    
    /**
     * 待处理订单数量
     */
    private Integer pendingOrderCount;
    
    /**
     * 已发货订单数量
     */
    private Integer shippedOrderCount;
    
    /**
     * 本月收入
     */
    private BigDecimal monthlyRevenue;
    
    /**
     * 本月订单数量
     */
    private Integer monthlyOrderCount;
} 