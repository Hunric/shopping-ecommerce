package com.hunric.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计数据传输对象
 */
@Data
public class OrderStatisticsDTO {
    /**
     * 总订单数
     */
    private Long totalOrders;
    
    /**
     * 待付款订单数
     */
    private Long pendingPayment;
    
    /**
     * 待发货订单数
     */
    private Long pendingShipment;
    
    /**
     * 待收货订单数
     */
    private Long pendingReceipt;
    
    /**
     * 已完成订单数
     */
    private Long completed;
    
    /**
     * 今日订单数
     */
    private Long todayOrders;
    
    /**
     * 今日销售额
     */
    private BigDecimal todaySales;
} 