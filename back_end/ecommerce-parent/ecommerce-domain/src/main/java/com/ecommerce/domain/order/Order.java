package com.ecommerce.domain.order;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer paymentType; // 1-支付宝 2-微信
    private Integer status; // 0-已取消 1-未支付 2-已支付 3-已发货 4-已完成
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 