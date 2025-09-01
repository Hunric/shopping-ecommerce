package com.hunric.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 订单发货的数据传输对象
 */
@Data
public class ShipOrderDTO {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 快递公司
     */
    @NotEmpty(message = "快递公司不能为空")
    private String shippingCompany;
    
    /**
     * 快递单号
     */
    @NotEmpty(message = "快递单号不能为空")
    private String trackingNumber;
} 