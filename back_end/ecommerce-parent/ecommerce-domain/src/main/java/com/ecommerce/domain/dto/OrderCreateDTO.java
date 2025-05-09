package com.ecommerce.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建订单的请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO {
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    @NotEmpty(message = "必须至少包含一个商品项")
    @Valid // 嵌套校验 List 中的 OrderItemCreateDTO
    private List<com.ecommerce.domain.dto.OrderItemCreateDTO> items;

    private String paymentMethod = "SIMULATED_PAY"; // 支付方式，可以有默认值

    // 可选：优惠券ID、备注等
    // private Long couponId;
    // private String remark;
}