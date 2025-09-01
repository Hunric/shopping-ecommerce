package com.hunric.controller;

import com.hunric.model.dto.PageResult;
import com.hunric.model.dto.ResponseResult;
import com.hunric.model.dto.CreateOrderDTO;
import com.hunric.model.dto.OrderDTO;
import com.hunric.model.dto.OrderDetailDTO;
import com.hunric.service.UserOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user/orders")
@Tag(name = "用户订单接口", description = "用户订单相关操作")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单DTO
     * @return 订单ID
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "根据购物车商品创建新订单")
    public ResponseResult<Long> createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        log.info("创建订单: {}", createOrderDTO);
        return ResponseResult.success(userOrderService.createOrder(createOrderDTO));
    }

    /**
     * 获取用户订单列表
     *
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @param orderNo     订单号（可选）
     * @param orderStatus 订单状态（可选）
     * @return 订单列表
     */
    @GetMapping
    @Operation(summary = "获取用户订单列表", description = "分页查询用户订单列表，支持按订单号和状态筛选")
    public ResponseResult<PageResult<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String orderStatus) {
        
        log.info("获取用户订单列表: pageNum={}, pageSize={}, orderNo={}, orderStatus={}", 
                pageNum, pageSize, orderNo, orderStatus);
        return ResponseResult.success(userOrderService.getUserOrders(pageNum, pageSize, orderNo, orderStatus));
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID查询订单详细信息")
    public ResponseResult<OrderDetailDTO> getOrderDetail(
            @PathVariable @Parameter(description = "订单ID") Long orderId) {
        log.info("获取订单详情: orderId={}", orderId);
        return ResponseResult.success(userOrderService.getOrderDetail(orderId));
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定ID的订单")
    public ResponseResult<Void> cancelOrder(@PathVariable @Parameter(description = "订单ID") Long orderId) {
        log.info("取消订单: orderId={}", orderId);
        userOrderService.cancelOrder(orderId);
        return ResponseResult.success();
    }
    
    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @PostMapping("/{orderId}/confirm-receipt")
    @Operation(summary = "确认收货", description = "确认指定ID的订单收货")
    public ResponseResult<Void> confirmReceipt(@PathVariable @Parameter(description = "订单ID") Long orderId) {
        log.info("确认收货: orderId={}", orderId);
        userOrderService.confirmReceipt(orderId);
        return ResponseResult.success();
    }
    
    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 结果
     */
    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单", description = "处理订单支付成功")
    public ResponseResult<String> payOrder(
            @PathVariable @Parameter(description = "订单ID") Long orderId,
            @RequestParam @Parameter(description = "支付方式") String paymentMethod) {
        log.info("处理订单支付: orderId={}, paymentMethod={}", orderId, paymentMethod);
        
        // 直接处理支付成功（模拟支付）
        userOrderService.processPaymentSuccess(orderId, paymentMethod);
        
        return ResponseResult.success("支付成功");
    }
} 