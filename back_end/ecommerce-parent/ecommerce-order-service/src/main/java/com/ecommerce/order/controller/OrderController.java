package com.ecommerce.order.controller; // 假设在 order 服务的 controller 包下

import com.ecommerce.common.ApiResponse;
import com.ecommerce.domain.dto.OrderCreateDTO;
import com.ecommerce.domain.dto.OrderDTO;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Management", description = "订单管理接口")
@Validated
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // --- 模拟获取用户ID ---
    // TODO: 替换为从 Spring Security 上下文获取真实用户ID的逻辑
    private Long getCurrentUserId() {
        return 1L; // 临时返回用户ID 1
    }
    // --- End of 模拟获取用户ID ---

    @PostMapping
    @Operation(summary = "创建订单", description = "根据购物车或选定商品创建新订单")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody OrderCreateDTO createDTO) {
        Long userId = getCurrentUserId();
        OrderDTO createdOrder = orderService.createOrder(userId, createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdOrder, "订单创建成功"));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @Parameter(description = "要查询的订单ID") @PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        OrderDTO order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping("/byOrderNo/{orderNo}")
    @Operation(summary = "根据订单号获取订单详情", description = "根据订单号获取订单详细信息")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByOrderNo(
            @Parameter(description = "要查询的订单号") @PathVariable String orderNo) {
        Long userId = getCurrentUserId();
        OrderDTO order = orderService.getOrderByOrderNo(userId, orderNo);
        return ResponseEntity.ok(ApiResponse.success(order));
    }


    @GetMapping
    @Operation(summary = "获取我的订单列表", description = "获取当前登录用户的所有订单列表")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> listMyOrders() {
        // TODO: 添加分页参数支持 (Pageable 或 自定义分页 DTO)
        Long userId = getCurrentUserId();
        List<OrderDTO> orders = orderService.listOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消处于待支付状态的订单")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @Parameter(description = "要取消的订单ID") @PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "订单已取消"));
    }

    // --- 模拟支付回调接口 ---
    // 注意：实际支付回调通常由支付网关发起，需要做安全校验（验签）
    // 这里仅作模拟，方便测试订单状态流转
    @PostMapping("/notify/payment-success")
    @Operation(summary = "模拟支付成功回调", description = "模拟第三方支付成功后通知接口")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam String orderNo,
            @RequestParam(defaultValue = "SIMULATED_PAY") String paymentMethod) {
        try {
            orderService.processPaymentSuccess(orderNo, paymentMethod);
            // 实际回调通常返回特定格式的字符串给支付网关，如 "success" 或 XML
            return ResponseEntity.ok("SUCCESS");
        } catch (Exception e) {
            log.error("处理支付回调失败: orderNo={}", orderNo, e);
            // 返回错误信息给支付网关（或记录日志等待重试）
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("FAIL");
        }
    }

}
