package com.hunric.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import com.hunric.exception.BusinessException;
import com.hunric.mapper.LogisticsInfoMapper;
import com.hunric.mapper.OrderInfoMapper;
import com.hunric.mapper.ShippingInfoMapper;
import com.hunric.mapper.UserInfoMapper;
import com.hunric.model.LogisticsInfo;
import com.hunric.model.OrderInfo;
import com.hunric.model.ShippingInfo;
import com.hunric.model.dto.OrderDTO;
import com.hunric.model.dto.OrderItemDTO;
import com.hunric.model.dto.OrderStatisticsDTO;
import com.hunric.model.dto.PageResult;
import com.hunric.model.dto.ShipOrderDTO;
import com.hunric.model.dto.ShippingInfoDTO;
import com.hunric.model.dto.UpdateOrderStatusDTO;
import com.hunric.service.MerchantOrderService;
import com.hunric.util.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 商家订单服务实现类
 */
@Service
public class MerchantOrderServiceImpl implements MerchantOrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    
    @Autowired
    private ShippingInfoMapper shippingInfoMapper;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    @Autowired
    private LogisticsInfoMapper logisticsInfoMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 初始化方法，配置ObjectMapper
     */
    @PostConstruct
    public void init() {
        // 配置ObjectMapper忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public PageResult<OrderDTO> getOrdersByMerchant(Integer pageNum, Integer pageSize, String orderNo, 
                                                 String orderStatus, String startDate, String endDate) {
        // 获取当前商家ID
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        
        // 构建查询条件
        OrderInfo condition = new OrderInfo();
        // 通过storeId关联查询（假设订单表中有商家ID或店铺ID字段）
        // 这里使用storeId作为关联条件
        condition.setStoreId(merchantId);
        
        if (orderNo != null && !orderNo.isEmpty()) {
            condition.setOrderNo(orderNo);
        }
        
        if (orderStatus != null && !orderStatus.isEmpty()) {
            condition.setOrderStatus(orderStatus);
        }
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询订单列表
        List<OrderInfo> orderInfoList;
        Long total;
        
        // 如果有日期范围，需要特殊处理
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            LocalDateTime endDateTime = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
            
            orderInfoList = orderInfoMapper.findByMerchantIdAndDateRange(
                    merchantId, orderNo, orderStatus, startDateTime, endDateTime, offset, pageSize);
            total = orderInfoMapper.countByMerchantIdAndDateRange(
                    merchantId, orderNo, orderStatus, startDateTime, endDateTime);
        } else {
            orderInfoList = orderInfoMapper.findByCondition(condition, offset, pageSize);
            total = orderInfoMapper.countByCondition(condition);
        }
        
        // 转换为DTO
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (OrderInfo orderInfo : orderInfoList) {
            OrderDTO orderDTO = convertToDTO(orderInfo);
            orderDTOList.add(orderDTO);
        }
        
        return new PageResult<>(orderDTOList, total, pageNum, pageSize);
    }

    @Override
    public OrderDTO getOrderDetail(Long orderId) {
        // 获取当前商家ID
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        
        // 查询订单信息
        OrderInfo orderInfo = orderInfoMapper.findById(orderId);
        
        // 验证订单是否存在且属于当前商家
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!Objects.equals(orderInfo.getStoreId(), merchantId)) {
            throw new BusinessException("无权访问该订单");
        }
        
        return convertToDTO(orderInfo);
    }

    @Override
    @Transactional
    public void updateOrderStatus(UpdateOrderStatusDTO dto) {
        // 获取当前商家ID
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        
        // 查询订单信息
        OrderInfo orderInfo = orderInfoMapper.findById(dto.getOrderId());
        
        // 验证订单是否存在且属于当前商家
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!Objects.equals(orderInfo.getStoreId(), merchantId)) {
            throw new BusinessException("无权操作该订单");
        }
        
        // 验证状态变更是否合法
        validateStatusChange(orderInfo.getOrderStatus(), dto.getOrderStatus());
        
        // 更新订单状态
        orderInfo.setOrderStatus(dto.getOrderStatus());
        
        // 根据状态设置相应的时间字段
        switch (dto.getOrderStatus()) {
            case "completed":
                orderInfo.setCompleteTime(LocalDateTime.now());
                break;
            case "cancelled":
                orderInfo.setCancelTime(LocalDateTime.now());
                break;
            // 其他状态可以根据需要添加
        }
        
        // 保存更新
        orderInfoMapper.update(orderInfo);
    }

    @Override
    @Transactional
    public void shipOrder(ShipOrderDTO dto) {
        // 获取当前商家ID
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        
        // 查询订单信息
        OrderInfo orderInfo = orderInfoMapper.findById(dto.getOrderId());
        
        // 验证订单是否存在且属于当前商家
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!Objects.equals(orderInfo.getStoreId(), merchantId)) {
            throw new BusinessException("无权操作该订单");
        }
        
        // 验证订单状态是否为待发货
        if (!"pending_shipment".equals(orderInfo.getOrderStatus())) {
            throw new BusinessException("只有待发货状态的订单才能执行发货操作");
        }
        
        // 更新订单状态为待收货
        orderInfo.setOrderStatus("pending_receipt");
        orderInfo.setShipTime(LocalDateTime.now());
        orderInfoMapper.update(orderInfo);
        
        // 创建物流信息
        LogisticsInfo logisticsInfo = new LogisticsInfo();
        logisticsInfo.setOrderId(dto.getOrderId());
        logisticsInfo.setShippingCompany(dto.getShippingCompany());
        logisticsInfo.setTrackingNumber(dto.getTrackingNumber());
        logisticsInfo.setShipTime(LocalDateTime.now());
        logisticsInfo.setLogisticsStatus("waiting_pickup"); // 初始状态为待揽收
        
        // 保存物流信息
        logisticsInfoMapper.insert(logisticsInfo);
    }

    @Override
    public OrderStatisticsDTO getOrderStatistics() {
        // 获取当前商家ID
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        
        // 创建统计结果对象
        OrderStatisticsDTO statistics = new OrderStatisticsDTO();
        
        // 查询各种状态的订单数量
        statistics.setTotalOrders(orderInfoMapper.countByMerchantId(merchantId));
        statistics.setPendingPayment(orderInfoMapper.countByMerchantIdAndStatus(merchantId, "pending_payment"));
        statistics.setPendingShipment(orderInfoMapper.countByMerchantIdAndStatus(merchantId, "pending_shipment"));
        statistics.setPendingReceipt(orderInfoMapper.countByMerchantIdAndStatus(merchantId, "pending_receipt"));
        statistics.setCompleted(orderInfoMapper.countByMerchantIdAndStatus(merchantId, "completed"));
        
        // 查询今日订单数量和销售额
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
        
        statistics.setTodayOrders(orderInfoMapper.countByMerchantIdAndDateRange(
                merchantId, null, null, todayStart, todayEnd));
        
        BigDecimal todaySales = orderInfoMapper.sumActualAmountByMerchantIdAndDateRange(
                merchantId, todayStart, todayEnd);
        statistics.setTodaySales(todaySales != null ? todaySales : BigDecimal.ZERO);
        
        return statistics;
    }
    
    /**
     * 将OrderInfo实体转换为OrderDTO
     *
     * @param orderInfo 订单信息实体
     * @return 订单DTO
     */
    private OrderDTO convertToDTO(OrderInfo orderInfo) {
        if (orderInfo == null) {
            return null;
        }
        
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderInfo, orderDTO);
        
        // 查询并设置收货信息
        ShippingInfo shippingInfo = shippingInfoMapper.findById(orderInfo.getShippingId());
        if (shippingInfo != null) {
            ShippingInfoDTO shippingInfoDTO = new ShippingInfoDTO();
            BeanUtils.copyProperties(shippingInfo, shippingInfoDTO);
            
            // 构建完整地址
            String fullAddress = shippingInfo.getProvince() + 
                                shippingInfo.getCity() + 
                                shippingInfo.getDistrict() + 
                                shippingInfo.getDetailAddress();
            shippingInfoDTO.setAddress(fullAddress);
            
            orderDTO.setShippingInfo(shippingInfoDTO);
        }
        
        // 查询并设置用户名称
        String userName = userInfoMapper.findUsernameById(orderInfo.getUserId());
        orderDTO.setUserName(userName);
        
        // 设置店铺名称（临时使用简单格式，后续可以从店铺表查询真实名称）
        orderDTO.setStoreName("店铺" + orderInfo.getStoreId());
        
        // 解析订单项JSON
        try {
            String orderItemsJson = orderInfo.getOrderItems();
            // 如果JSON字符串为空或null，设置空列表
            if (orderItemsJson == null || orderItemsJson.trim().isEmpty()) {
                orderDTO.setOrderItems(new ArrayList<>());
            } else {
                List<OrderItemDTO> orderItems = objectMapper.readValue(
                        orderItemsJson, new TypeReference<List<OrderItemDTO>>() {});
                orderDTO.setOrderItems(orderItems);
            }
        } catch (JsonProcessingException e) {
            // 设置空列表，避免整个请求失败
            orderDTO.setOrderItems(new ArrayList<>());
            System.err.println("解析订单项数据失败: " + e.getMessage());
        }
        
        return orderDTO;
    }
    
    /**
     * 验证订单状态变更是否合法
     *
     * @param currentStatus 当前状态
     * @param newStatus     新状态
     */
    private void validateStatusChange(String currentStatus, String newStatus) {
        // 如果状态没有变化，直接返回
        if (currentStatus.equals(newStatus)) {
            return;
        }
        
        // 定义状态转换规则
        boolean isValid = false;
        
        switch (currentStatus) {
            case "pending_payment":
                // 待付款 -> 已取消
                isValid = "cancelled".equals(newStatus);
                break;
            case "pending_shipment":
                // 待发货 -> 待收货 或 已取消
                isValid = "pending_receipt".equals(newStatus) || "cancelled".equals(newStatus);
                break;
            case "pending_receipt":
                // 待收货 -> 已完成
                isValid = "completed".equals(newStatus);
                break;
            case "completed":
                // 已完成 -> 已退款
                isValid = "refunded".equals(newStatus);
                break;
            // 其他状态不允许变更
        }
        
        if (!isValid) {
            throw new BusinessException("订单状态变更不合法");
        }
    }
} 