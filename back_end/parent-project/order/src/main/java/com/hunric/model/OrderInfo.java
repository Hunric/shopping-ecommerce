package com.hunric.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息实体类
 */
@Data
public class OrderInfo {
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 店铺ID
     */
    private Long storeId;
    
    /**
     * 收货信息ID
     */
    private Long shippingId;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal actualAmount;
    
    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    
    /**
     * 订单状态
     * pending_payment - 待付款
     * pending_shipment - 待发货
     * pending_receipt - 待收货
     * completed - 已完成
     * cancelled - 已取消
     * refunded - 已退款
     */
    private String orderStatus;
    
    /**
     * 支付方式
     * alipay - 支付宝
     * wechat - 微信支付
     * credit_card - 信用卡
     * cod - 货到付款
     */
    private String paymentMethod;
    
    /**
     * 订单备注
     */
    private String orderNote;
    
    /**
     * 订单项（JSON格式）
     */
    private String orderItems;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 发货时间
     */
    private LocalDateTime shipTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 