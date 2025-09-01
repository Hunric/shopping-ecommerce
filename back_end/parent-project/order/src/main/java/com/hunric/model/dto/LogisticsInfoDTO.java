package com.hunric.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物流信息数据传输对象
 */
@Data
public class LogisticsInfoDTO {
    
    /**
     * 物流ID
     */
    private Long logisticsId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 物流公司名称
     */
    private String companyName;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 物流状态
     * pending - 待发货
     * shipped - 已发货
     * in_transit - 运输中
     * delivered - 已送达
     * returned - 已退回
     * exception - 异常
     */
    private String status;
    
    /**
     * 发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shipTime;
    
    /**
     * 预计送达时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedDeliveryTime;
    
    /**
     * 实际送达时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryTime;
    
    /**
     * 物流跟踪信息列表
     */
    private List<LogisticsTrackDTO> trackingList;
} 