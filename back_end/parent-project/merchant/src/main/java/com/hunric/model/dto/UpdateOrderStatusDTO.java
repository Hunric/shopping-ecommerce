package com.hunric.model.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 更新订单状态的数据传输对象
 */
@Data
public class UpdateOrderStatusDTO {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 订单状态
     * pending_payment - 待付款
     * pending_shipment - 待发货
     * pending_receipt - 待收货
     * completed - 已完成
     * cancelled - 已取消
     * refunded - 已退款
     */
    @NotEmpty(message = "订单状态不能为空")
    private String orderStatus;
    
    /**
     * 备注信息（可选）
     */
    private String remark;
} 