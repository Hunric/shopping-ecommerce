package com.hunric.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物流信息实体类
 */
@Data
public class LogisticsInfo {
    /**
     * 物流ID
     */
    private Long logisticsId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 快递公司
     */
    private String shippingCompany;
    
    /**
     * 快递单号
     */
    private String trackingNumber;
    
    /**
     * 发货时间
     */
    private LocalDateTime shipTime;
    
    /**
     * 物流状态
     * not_shipped - 未发货
     * waiting_pickup - 待揽收
     * in_transit - 运输中
     * delivered - 已送达
     * rejected - 已拒收
     */
    private String logisticsStatus;
    
    /**
     * 物流轨迹（JSON格式）
     */
    private String logisticsTrack;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 