package com.ecommerce.order.service; // 假设在 order 服务的 service 包下

import com.ecommerce.domain.dto.OrderCreateDTO;
import com.ecommerce.domain.dto.OrderDTO;
import com.ecommerce.order.exception.OrderCreationException; // 引入自定义异常

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建新订单
     *
     * @param userId 用户ID
     * @param dto    创建订单的 DTO
     * @return 创建成功的订单 DTO
     * @throws OrderCreationException 如果订单创建失败（例如库存不足、地址无效等）
     */
    OrderDTO createOrder(Long userId, OrderCreateDTO dto);

    /**
     * 根据订单ID获取订单详情
     *
     * @param userId  用户ID (用于权限校验)
     * @param orderId 订单ID
     * @return 订单 DTO
     * @throws RuntimeException 如果订单不存在或用户无权查看
     */
    OrderDTO getOrderById(Long userId, Long orderId);

    /**
     * 根据订单号获取订单详情
     *
     * @param userId  用户ID (用于权限校验)
     * @param orderNo 订单号
     * @return 订单 DTO
     * @throws RuntimeException 如果订单不存在或用户无权查看
     */
    OrderDTO getOrderByOrderNo(Long userId, String orderNo);


    /**
     * 获取指定用户的订单列表 (可添加分页参数)
     *
     * @param userId 用户ID
     * @return 订单 DTO 列表
     */
    List<OrderDTO> listOrdersByUserId(Long userId);

    /**
     * 取消订单 (例如：用户在待支付状态下取消)
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @throws RuntimeException 如果订单状态不允许取消或用户无权操作
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 模拟支付成功回调处理
     *
     * @param orderNo 支付成功的订单号
     * @param paymentMethod 支付方式
     */
    void processPaymentSuccess(String orderNo, String paymentMethod);

    // TODO: 添加其他订单操作接口，如确认收货、申请退款等
}
