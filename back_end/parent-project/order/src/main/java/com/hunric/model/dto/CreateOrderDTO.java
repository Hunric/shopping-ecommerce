package com.hunric.model.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单数据传输对象
 */
@Data
public class CreateOrderDTO {
    /**
     * 用户ID（实际项目中应从安全上下文获取，这里暂时作为参数传递）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;
    
    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址ID不能为空")
    private Long shippingId;
    
    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    @Min(value = 0, message = "订单总金额必须大于等于0")
    private BigDecimal totalAmount;
    
    /**
     * 实付金额
     */
    @NotNull(message = "实付金额不能为空")
    @Min(value = 0, message = "实付金额必须大于等于0")
    private BigDecimal actualAmount;
    
    /**
     * 优惠金额
     */
    @Min(value = 0, message = "优惠金额必须大于等于0")
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    /**
     * 运费
     */
    @Min(value = 0, message = "运费必须大于等于0")
    private BigDecimal shippingFee = BigDecimal.ZERO;
    
    /**
     * 订单备注
     */
    private String orderNote;
    
    /**
     * 订单项列表
     */
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemDTO> orderItems;
} 