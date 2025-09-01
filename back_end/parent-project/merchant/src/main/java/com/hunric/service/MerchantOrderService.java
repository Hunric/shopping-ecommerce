package com.hunric.service;

import com.hunric.model.dto.OrderDTO;
import com.hunric.model.dto.OrderStatisticsDTO;
import com.hunric.model.dto.PageResult;
import com.hunric.model.dto.ShipOrderDTO;
import com.hunric.model.dto.UpdateOrderStatusDTO;

/**
 * 商家订单服务接口
 */
public interface MerchantOrderService {
    
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
    PageResult<OrderDTO> getOrdersByMerchant(Integer pageNum, Integer pageSize, String orderNo, 
                                           String orderStatus, String startDate, String endDate);
    
    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDTO getOrderDetail(Long orderId);
    
    /**
     * 更新订单状态
     *
     * @param dto 更新订单状态DTO
     */
    void updateOrderStatus(UpdateOrderStatusDTO dto);
    
    /**
     * 订单发货
     *
     * @param dto 发货信息DTO
     */
    void shipOrder(ShipOrderDTO dto);
    
    /**
     * 获取订单统计数据
     *
     * @return 订单统计数据
     */
    OrderStatisticsDTO getOrderStatistics();
} 