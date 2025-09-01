package com.hunric.controller;

import com.hunric.model.dto.OrderDTO;
import com.hunric.model.dto.OrderStatisticsDTO;
import com.hunric.model.dto.PageResult;
import com.hunric.model.dto.ResponseResult;
import com.hunric.model.dto.ShipOrderDTO;
import com.hunric.model.dto.UpdateOrderStatusDTO;
import com.hunric.service.MerchantOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商家订单管理控制器
 */
@RestController
@RequestMapping("/api/merchant/orders")
@PreAuthorize("hasRole('MERCHANT')")
public class MerchantOrderController {

    @Autowired
    private MerchantOrderService merchantOrderService;

    /**
     * 获取商家订单列表（分页）
     *
     * @param pageNum     页码
     * @param pageSize    每页大小
     * @param orderNo     订单编号（可选）
     * @param orderStatus 订单状态（可选）
     * @param startDate   开始日期（可选）
     * @param endDate     结束日期（可选）
     * @return 订单列表分页结果
     */
    @GetMapping
    public ResponseResult<PageResult<OrderDTO>> getOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        return ResponseResult.success(merchantOrderService.getOrdersByMerchant(
                pageNum, pageSize, orderNo, orderStatus, startDate, endDate));
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseResult<OrderDTO> getOrderDetail(@PathVariable Long orderId) {
        return ResponseResult.success(merchantOrderService.getOrderDetail(orderId));
    }

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param dto     更新订单状态DTO
     * @return 更新结果
     */
    @PutMapping("/{orderId}/status")
    public ResponseResult<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusDTO dto) {
        
        dto.setOrderId(orderId);
        merchantOrderService.updateOrderStatus(dto);
        return ResponseResult.success();
    }

    /**
     * 订单发货
     *
     * @param orderId 订单ID
     * @param dto     发货信息DTO
     * @return 发货结果
     */
    @PostMapping("/{orderId}/ship")
    public ResponseResult<Void> shipOrder(
            @PathVariable Long orderId,
            @RequestBody ShipOrderDTO dto) {
        
        dto.setOrderId(orderId);
        merchantOrderService.shipOrder(dto);
        return ResponseResult.success();
    }

    /**
     * 获取订单统计数据
     *
     * @return 订单统计数据
     */
    @GetMapping("/statistics")
    public ResponseResult<OrderStatisticsDTO> getOrderStatistics() {
        return ResponseResult.success(merchantOrderService.getOrderStatistics());
    }
} 