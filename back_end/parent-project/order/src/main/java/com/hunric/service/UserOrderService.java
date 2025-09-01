package com.hunric.service;

import com.hunric.model.dto.PageResult;
import com.hunric.model.dto.CreateOrderDTO;
import com.hunric.model.dto.OrderDTO;
import com.hunric.model.dto.OrderDetailDTO;

/**
 * 用户订单服务接口
 */
public interface UserOrderService {

    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单DTO
     * @return 订单ID
     */
    Long createOrder(CreateOrderDTO createOrderDTO);

    /**
     * 获取用户订单列表
     *
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @param orderNo     订单号（可选）
     * @param orderStatus 订单状态（可选）
     * @return 订单列表
     */
    PageResult<OrderDTO> getUserOrders(Integer pageNum, Integer pageSize, String orderNo, String orderStatus);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailDTO getOrderDetail(Long orderId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);
    
    /**
     * 确认收货
     *
     * @param orderId 订单ID
     */
    void confirmReceipt(Long orderId);
    
    /**
     * 发起支付
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 支付URL
     */
    String initiatePayment(Long orderId, String paymentMethod);
    
    /**
     * 处理支付成功
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     */
    void processPaymentSuccess(Long orderId, String paymentMethod);
} 