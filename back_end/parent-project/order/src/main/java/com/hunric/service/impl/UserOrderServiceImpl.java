package com.hunric.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunric.exception.BusinessException;
import com.hunric.model.dto.PageResult;
import com.hunric.mapper.OrderInfoMapper;
import com.hunric.mapper.ShippingInfoMapper;
import com.hunric.model.OrderInfo;
import com.hunric.model.ShippingInfo;
import com.hunric.model.dto.CreateOrderDTO;
import com.hunric.model.dto.OrderDTO;
import com.hunric.model.dto.OrderDetailDTO;
import com.hunric.model.dto.OrderItemDTO;
import com.hunric.model.dto.ShippingInfoDTO;
import com.hunric.service.UserOrderService;
import com.hunric.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户订单服务实现类
 */
@Slf4j
@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    
    @Autowired
    private ShippingInfoMapper shippingInfoMapper;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 创建订单
     *
     * @param createOrderDTO 创建订单DTO
     * @return 订单ID
     */
    @Override
    @Transactional
    public Long createOrder(CreateOrderDTO createOrderDTO) {
        log.info("开始创建订单: {}", createOrderDTO);
        
        // 1. 查询收货信息
        ShippingInfo shippingInfo = shippingInfoMapper.findById(createOrderDTO.getShippingId());
        if (shippingInfo == null) {
            throw new BusinessException("收货地址不存在");
        }
        
        // 2. 查询商品信息并计算金额（此处省略，实际项目中需要调用商品服务）
        
        // 3. 创建订单信息
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(generateOrderNo());
        orderInfo.setUserId(createOrderDTO.getUserId());
        orderInfo.setStoreId(createOrderDTO.getStoreId());
        orderInfo.setShippingId(createOrderDTO.getShippingId());
        
        // 设置金额
        orderInfo.setTotalAmount(createOrderDTO.getTotalAmount());
        orderInfo.setActualAmount(createOrderDTO.getActualAmount());
        orderInfo.setDiscountAmount(createOrderDTO.getDiscountAmount());
        orderInfo.setShippingFee(createOrderDTO.getShippingFee());
        
        // 设置订单状态和备注
        orderInfo.setOrderStatus("pending_payment");  // 默认为待付款
        orderInfo.setOrderNote(createOrderDTO.getOrderNote());
        
        // 序列化订单项
        try {
            orderInfo.setOrderItems(objectMapper.writeValueAsString(createOrderDTO.getOrderItems()));
        } catch (JsonProcessingException e) {
            log.error("订单项序列化失败", e);
            throw new BusinessException("订单创建失败");
        }
        
        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        orderInfo.setCreateTime(now);
        orderInfo.setUpdateTime(now);
        
        // 4. 保存订单
        orderInfoMapper.insert(orderInfo);
        
        // 5. 减少库存（此处省略，实际项目中需要调用库存服务）
        
        log.info("订单创建成功: {}", orderInfo.getOrderId());
        return orderInfo.getOrderId();
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
    @Override
    public PageResult<OrderDTO> getUserOrders(Integer pageNum, Integer pageSize, String orderNo, String orderStatus) {
        log.info("查询用户订单列表: pageNum={}, pageSize={}, orderNo={}, orderStatus={}", 
                pageNum, pageSize, orderNo, orderStatus);
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 构建查询条件
        OrderInfo condition = new OrderInfo();
        
        // 添加当前登录用户ID
        Long currentUserId = getCurrentUserId();
        condition.setUserId(currentUserId);
        log.info("查询用户订单列表，当前用户ID: {}", currentUserId);
        
        if (StringUtils.hasText(orderNo)) {
            condition.setOrderNo(orderNo);
        }
        if (StringUtils.hasText(orderStatus)) {
            condition.setOrderStatus(orderStatus);
        }
        
        // 查询订单
        List<OrderInfo> orderInfoList = orderInfoMapper.findByCondition(condition, offset, pageSize);
        long total = orderInfoMapper.countByCondition(condition);
        
        // 转换为DTO
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (OrderInfo orderInfo : orderInfoList) {
            OrderDTO orderDTO = convertToDTO(orderInfo);
            orderDTOList.add(orderDTO);
        }
        
        return new PageResult<>(orderDTOList, total, pageNum, pageSize);
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Override
    public OrderDetailDTO getOrderDetail(Long orderId) {
        log.info("查询订单详情: orderId={}", orderId);
        
        // 查询订单
        OrderInfo orderInfo = orderInfoMapper.findById(orderId);
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        log.info("查询到订单信息: orderId={}, userId={}", orderInfo.getOrderId(), orderInfo.getUserId());
        
        // 验证当前用户是否有权限查看此订单
        validateOrderAccess(orderInfo);
        
        // 转换为DTO
        OrderDetailDTO detailDTO = new OrderDetailDTO();
        
        // 复制基本信息
        OrderDTO orderDTO = convertToDTO(orderInfo);
        BeanUtils.copyProperties(orderDTO, detailDTO);
        
        // 设置物流信息（此处省略，实际项目中需要调用物流服务）
        
        return detailDTO;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     */
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        log.info("取消订单: orderId={}", orderId);
        
        // 查询订单
        OrderInfo orderInfo = orderInfoMapper.findById(orderId);
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证当前用户是否有权限操作此订单
        validateOrderAccess(orderInfo);
        
        // 验证订单状态，只有待付款状态才能取消
        if (!"pending_payment".equals(orderInfo.getOrderStatus())) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        // 更新订单状态
        orderInfo.setOrderStatus("cancelled");
        orderInfo.setCancelTime(LocalDateTime.now());
        orderInfo.setUpdateTime(LocalDateTime.now());
        orderInfoMapper.update(orderInfo);
        
        // 恢复库存（此处省略，实际项目中需要调用库存服务）
        
        log.info("订单取消成功: {}", orderId);
    }
    
    /**
     * 确认收货
     *
     * @param orderId 订单ID
     */
    @Override
    @Transactional
    public void confirmReceipt(Long orderId) {
        log.info("确认收货: orderId={}", orderId);
        
        // 查询订单
        OrderInfo orderInfo = orderInfoMapper.findById(orderId);
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证当前用户是否有权限操作此订单
        validateOrderAccess(orderInfo);
        
        // 验证订单状态，只有待收货状态才能确认收货
        if (!"pending_receipt".equals(orderInfo.getOrderStatus())) {
            throw new BusinessException("订单状态不允许确认收货");
        }
        
        // 更新订单状态
        orderInfo.setOrderStatus("completed");
        orderInfo.setCompleteTime(LocalDateTime.now());
        orderInfo.setUpdateTime(LocalDateTime.now());
        orderInfoMapper.update(orderInfo);
        
        log.info("确认收货成功: {}", orderId);
    }
    
    /**
     * 发起支付
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 支付URL
     */
    @Override
    public String initiatePayment(Long orderId, String paymentMethod) {
        log.info("发起支付: orderId={}, paymentMethod={}", orderId, paymentMethod);
        
        // 查询订单
        OrderInfo orderInfo = orderInfoMapper.findById(orderId);
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证当前用户是否有权限操作此订单
        validateOrderAccess(orderInfo);
        
        // 验证订单状态，只有待付款状态才能发起支付
        if (!"pending_payment".equals(orderInfo.getOrderStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }
        
        // 调用支付服务获取支付链接（此处省略，实际项目中需要调用支付服务）
        // 这里模拟返回一个支付链接
        return "/api/payment/pay?orderId=" + orderId + "&method=" + paymentMethod;
    }
    
    /**
     * 处理支付成功
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     */
    @Override
    @Transactional
    public void processPaymentSuccess(Long orderId, String paymentMethod) {
        log.info("处理支付成功: orderId={}, paymentMethod={}", orderId, paymentMethod);
        
        // 查询订单
        OrderInfo orderInfo = orderInfoMapper.findById(orderId);
        if (orderInfo == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 验证当前用户是否有权限操作此订单
        validateOrderAccess(orderInfo);
        
        // 验证订单状态，只有待付款状态才能支付成功
        if (!"pending_payment".equals(orderInfo.getOrderStatus())) {
            log.warn("订单状态不是待付款，当前状态: {}", orderInfo.getOrderStatus());
            throw new BusinessException("订单状态不允许支付");
        }
        
        // 更新订单状态为待发货
        orderInfo.setOrderStatus("pending_shipment");
        orderInfo.setPaymentMethod(paymentMethod);
        orderInfo.setPayTime(LocalDateTime.now());
        orderInfo.setUpdateTime(LocalDateTime.now());
        
        // 保存订单更新
        orderInfoMapper.update(orderInfo);
        
        // 这里可以添加支付记录到payment_info表（如果需要的话）
        // createPaymentRecord(orderId, paymentMethod);
        
        log.info("订单支付成功，状态已更新: orderId={}, newStatus={}", orderId, orderInfo.getOrderStatus());
    }
    
    /**
     * 生成订单号
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        // 生成格式：年月日时分秒+随机数
        String timestamp = LocalDateTime.now().toString().replace("-", "")
                .replace("T", "").replace(":", "").substring(0, 14);
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return timestamp + random;
    }
    
    /**
     * 获取当前登录用户ID
     * 从安全上下文中获取
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("从SecurityUtils获取当前用户ID: {}", userId);
        return userId;
    }
    
    /**
     * 验证用户是否有权限操作订单
     *
     * @param orderInfo 订单信息
     */
    private void validateOrderAccess(OrderInfo orderInfo) {
        Long currentUserId = getCurrentUserId();
        log.info("验证订单访问权限: 当前用户ID={}, 订单用户ID={}", currentUserId, orderInfo.getUserId());
        
        if (currentUserId == null) {
            throw new BusinessException("用户未登录");
        }
        
        if (!currentUserId.equals(orderInfo.getUserId())) {
            throw new BusinessException("无权操作此订单");
        }
        
        log.info("订单访问权限验证通过");
    }
    
    /**
     * 将OrderInfo实体转换为OrderDTO
     *
     * @param orderInfo 订单信息
     * @return OrderDTO
     */
    private OrderDTO convertToDTO(OrderInfo orderInfo) {
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderInfo, orderDTO);
        
        // 获取收货信息
        ShippingInfo shippingInfo = shippingInfoMapper.findById(orderInfo.getShippingId());
        if (shippingInfo != null) {
            ShippingInfoDTO shippingInfoDTO = new ShippingInfoDTO();
            BeanUtils.copyProperties(shippingInfo, shippingInfoDTO);
            orderDTO.setShippingInfo(shippingInfoDTO);
        }
        
        // 获取店铺名称（此处省略，实际项目中需要调用商家服务）
        orderDTO.setStoreName("店铺" + orderInfo.getStoreId());
        
        // 获取用户名称（此处省略，实际项目中需要调用用户服务）
        orderDTO.setUserName("用户" + orderInfo.getUserId());
        
        // 解析订单项
        try {
            if (StringUtils.hasText(orderInfo.getOrderItems())) {
                List<OrderItemDTO> orderItems = objectMapper.readValue(
                        orderInfo.getOrderItems(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, OrderItemDTO.class));
                orderDTO.setOrderItems(orderItems);
            }
        } catch (JsonProcessingException e) {
            log.error("订单项解析失败", e);
            // 解析失败时设置空列表
            orderDTO.setOrderItems(new ArrayList<>());
        }
        
        return orderDTO;
    }
}