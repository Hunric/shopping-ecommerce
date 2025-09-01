package com.hunric.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单详情数据传输对象
 * 继承自OrderDTO，并添加物流等额外信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailDTO extends OrderDTO {
    
    /**
     * 物流信息
     */
    private LogisticsInfoDTO logisticsInfo;
    
    /**
     * 支付状态
     * not_paid - 未支付
     * paid - 已支付
     * refunded - 已退款
     */
    private String paymentStatus;
    
    /**
     * 支付渠道流水号
     */
    private String transactionId;
} 