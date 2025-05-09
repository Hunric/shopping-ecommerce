package com.ecommerce.order.service.impl; // 假设在 order 服务的 service.impl 包下

import com.ecommerce.domain.dto.*; // 引入所有 domain DTOs
import com.ecommerce.domain.entity.Address; // 引入 Address 实体
import com.ecommerce.domain.entity.Order;
import com.ecommerce.domain.entity.OrderItem;
import com.ecommerce.domain.entity.Product; // 引入 Product 实体
import com.ecommerce.order.exception.OrderCreationException;
import com.ecommerce.order.mapper.OrderItemMapper;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.service.OrderService;
// TODO: 引入服务客户端或 Service 接口
// import com.ecommerce.user.service.AddressService;
// import com.ecommerce.product.service.ProductService;
// import com.ecommerce.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils; // 引入 CollectionUtils

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    // TODO: 注入所需的服务客户端/接口
    // @Autowired private AddressService addressService;
    // @Autowired private ProductService productService;
    // @Autowired private CartService cartService;

    // 订单状态常量 (建议定义在公共枚举类中)
    private static final String STATUS_PENDING_PAYMENT = "PENDING_PAYMENT";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CANCELLED = "CANCELLED";
    // ... 其他状态

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional // 开启事务
    public OrderDTO createOrder(Long userId, OrderCreateDTO dto) {
        log.info("Attempting to create order for userId: {}", userId);

        // 1. 校验输入 DTO
        if (dto == null || CollectionUtils.isEmpty(dto.getItems()) || dto.getAddressId() == null) {
            throw new OrderCreationException("创建订单参数无效");
        }

        // 2. 校验收货地址
        // TODO: 调用 UserService 获取并校验地址
        Address address = validateAddress(userId, dto.getAddressId());

        // 3. 准备商品信息 (获取商品快照并校验库存)
        List<OrderItem> orderItems = prepareOrderItems(dto.getItems());

        // 4. 计算订单金额
        BigDecimal totalAmount = calculateTotalAmount(orderItems);
        BigDecimal payableAmount = totalAmount; // 简单处理，未考虑运费和折扣

        // 5. 创建订单实体
        Order order = buildOrderEntity(userId, dto, address, totalAmount, payableAmount);

        // 6. 插入订单主表
        orderMapper.insert(order);
        Long orderId = order.getId(); // 获取生成的订单ID
        if (orderId == null) {
            throw new OrderCreationException("无法创建订单记录");
        }
        log.info("Order record created with id: {}, orderNo: {}", orderId, order.getOrderNo());

        // 7. 设置订单项的 orderId 并批量插入
        orderItems.forEach(item -> item.setOrderId(orderId));
        orderItemMapper.insertBatch(orderItems);
        log.info("Inserted {} order items for orderId: {}", orderItems.size(), orderId);

        // 8. 扣减商品库存 (重要：需要在事务内)
        decreaseProductStock(orderItems);

        // 9. 清理购物车中已下单的商品
        clearCartItems(userId, orderItems);

        // 10. 构建并返回 OrderDTO
        OrderDTO createdOrderDTO = convertToOrderDTO(order, orderItems);
        log.info("Order created successfully: orderNo={}", createdOrderDTO.getOrderNo());
        return createdOrderDTO;
    }

    // --- Helper methods for createOrder ---

    /** 模拟地址校验 */
    private Address validateAddress(Long userId, Long addressId) {
        log.debug("Validating addressId: {} for userId: {}", addressId, userId);
        // TODO: 调用 UserService 的 AddressService.getAddressByIdAndUserId(addressId, userId)
        // 模拟返回一个地址对象
        Address mockAddress = new Address();
        mockAddress.setId(addressId);
        mockAddress.setUserId(userId);
        mockAddress.setReceiverName("模拟收货人");
        mockAddress.setPhoneNumber("13800000000");
        mockAddress.setProvince("模拟省");
        mockAddress.setCity("模拟市");
        mockAddress.setDistrict("模拟区");
        mockAddress.setDetailedAddress("模拟详细地址");
        if (mockAddress == null) { // 假设服务调用返回 null 表示未找到或无权访问
            throw new OrderCreationException("收货地址无效或不存在");
        }
        return mockAddress;
    }

    /** 准备订单项列表，包含商品信息获取和校验 */
    private List<OrderItem> prepareOrderItems(List<OrderItemCreateDTO> itemCreateDTOs) {
        List<OrderItem> orderItems = new ArrayList<>();
        Set<Long> productIds = itemCreateDTOs.stream()
                .map(OrderItemCreateDTO::getProductId)
                .collect(Collectors.toSet());

        // TODO: 调用 ProductService 批量获取商品信息 Map<Long, ProductDTO>
        Map<Long, ProductDTO> productMap = getMockProductInfo(productIds); // 使用模拟数据

        for (OrderItemCreateDTO itemDto : itemCreateDTOs) {
            ProductDTO product = productMap.get(itemDto.getProductId());
            if (product == null) {
                throw new OrderCreationException("商品不存在: ID = " + itemDto.getProductId());
            }
            if (product.getStatus() == null || product.getStatus() != 1) { // 假设 1 为上架
                throw new OrderCreationException("商品已下架: " + product.getName());
            }
            if (product.getStockQuantity() == null || product.getStockQuantity() < itemDto.getQuantity()) {
                throw new OrderCreationException("商品库存不足: " + product.getName());
            }
            if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new OrderCreationException("商品价格异常: " + product.getName());
            }

            // 创建 OrderItem 实体 (商品快照)
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setUnitPrice(product.getPrice()); // 使用实时获取的价格
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    /** 模拟批量获取商品信息 */
    private Map<Long, ProductDTO> getMockProductInfo(Set<Long> productIds) {
        log.debug("Fetching mock product info for ids: {}", productIds);
        // TODO: 调用 ProductService.getProductsByIds(productIds)
        Map<Long, ProductDTO> productMap = new HashMap<>();
        for (Long id : productIds) {
            // 模拟商品信息
            ProductDTO mockProduct = new ProductDTO();
            mockProduct.setId(id);
            mockProduct.setName("模拟商品 " + id);
            mockProduct.setPrice(BigDecimal.valueOf(10.0 + id % 50)); // 模拟价格
            mockProduct.setStockQuantity(100); // 假设库存充足
            mockProduct.setStatus(1); // 假设上架
            mockProduct.setImageUrl("https://placehold.co/100x100/eee/ccc?text=Prod" + id);
            productMap.put(id, mockProduct);
        }
        return productMap;
    }

    /** 计算订单商品总额 */
    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** 构建订单实体 */
    private Order buildOrderEntity(Long userId, OrderCreateDTO dto, Address address,
                                   BigDecimal totalAmount, BigDecimal payableAmount) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo(userId)); // 生成订单号
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayableAmount(payableAmount); // 实际应考虑运费、折扣等
        order.setStatus(STATUS_PENDING_PAYMENT); // 初始状态
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverAddress(String.join(" ", address.getProvince(), address.getCity(), address.getDistrict(), address.getDetailedAddress()));
        order.setPaymentMethod(dto.getPaymentMethod());
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        return order;
    }

    /** 生成唯一订单号 (简单示例) */
    private String generateOrderNo(Long userId) {
        // 规则: yyyyMMddHHmmss + 用户ID后4位 + 3位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdSuffix = String.format("%04d", userId % 10000);
        String randomSuffix = String.format("%03d", new Random().nextInt(1000));
        return timestamp + userIdSuffix + randomSuffix;
    }

    /** 扣减库存 */
    private void decreaseProductStock(List<OrderItem> orderItems) {
        log.debug("Decreasing stock for order items...");
        // TODO: 调用 ProductService.decreaseStock(productId, quantity)
        for (OrderItem item : orderItems) {
            log.debug("Mock decrease stock for productId: {}, quantity: {}", item.getProductId(), item.getQuantity());
            // try {
            //     productService.decreaseStock(item.getProductId(), item.getQuantity());
            // } catch (Exception e) { // 捕获 ProductService 可能抛出的异常
            //     log.error("Failed to decrease stock for productId: {}", item.getProductId(), e);
            //     // 抛出 OrderCreationException，触发事务回滚
            //     throw new OrderCreationException("扣减商品库存失败: " + item.getProductName(), e);
            // }
        }
        log.info("Mock stock decreased successfully for {} items.", orderItems.size());
    }

    /** 清理购物车 */
    private void clearCartItems(Long userId, List<OrderItem> orderItems) {
        Set<Long> productIdsToRemove = orderItems.stream()
                .map(OrderItem::getProductId)
                .collect(Collectors.toSet());
        log.debug("Clearing cart items for userId: {}, productIds: {}", userId, productIdsToRemove);
        // TODO: 调用 CartService.removeCartItems(userId, productIdsToRemove)
        log.info("Mock cart items cleared for userId: {}", userId);
    }

    /** 将 Order 和 OrderItem 转换为 OrderDTO */
    private OrderDTO convertToOrderDTO(Order order, List<OrderItem> items) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);
        if (items != null) {
            dto.setItems(items.stream().map(this::convertOrderItemToDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    /** 将 OrderItem 转换为 OrderItemDTO */
    private OrderItemDTO convertOrderItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        BeanUtils.copyProperties(item, dto);
        return dto;
    }

    // --- End of helper methods for createOrder ---


    @Override
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: ID = " + orderId)); // TODO: 使用 ResourceNotFoundException

        // 权限校验
        if (!order.getUserId().equals(userId)) {
            // TODO: 使用更合适的权限异常
            throw new RuntimeException("无权查看此订单");
        }

        List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
        return convertToOrderDTO(order, items);
    }

    @Override
    public OrderDTO getOrderByOrderNo(Long userId, String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在: OrderNo = " + orderNo)); // TODO: 使用 ResourceNotFoundException

        // 权限校验
        if (!order.getUserId().equals(userId)) {
            // TODO: 使用更合适的权限异常
            throw new RuntimeException("无权查看此订单");
        }

        List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
        return convertToOrderDTO(order, items);
    }


    @Override
    public List<OrderDTO> listOrdersByUserId(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        // N+1 问题：需要优化，例如一次性查询所有相关订单项
        // 简单实现：
        return orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.findByOrderId(order.getId());
                    return convertToOrderDTO(order, items);
                })
                .collect(Collectors.toList());
        // 优化方案：
        // 1. 获取所有订单 ID
        // 2. 一次性查询所有这些订单的订单项 Map<Long, List<OrderItem>>
        // 3. 组装 OrderDTO
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: ID = " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }

        // 只有待支付状态的订单可以取消
        if (!STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }

        order.setStatus(STATUS_CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        int updatedRows = orderMapper.updateStatus(orderId, STATUS_CANCELLED); // 使用只更新状态的方法

        if (updatedRows > 0) {
            // TODO: 订单取消后，需要将扣减的库存加回去 (调用 ProductService.increaseStock)
            List<OrderItem> items = orderItemMapper.findByOrderId(orderId);
            increaseProductStock(items); // 调用库存归还方法
            log.info("Order cancelled successfully and stock restored: orderId={}", orderId);
        } else {
            log.error("Failed to cancel order: orderId={}", orderId);
            throw new RuntimeException("取消订单失败");
        }
    }

    /** 归还库存 */
    private void increaseProductStock(List<OrderItem> orderItems) {
        log.debug("Restoring stock for cancelled order items...");
        // TODO: 调用 ProductService.increaseStock(productId, quantity)
        for (OrderItem item : orderItems) {
            log.debug("Mock restore stock for productId: {}, quantity: {}", item.getProductId(), item.getQuantity());
            // try {
            //     productService.increaseStock(item.getProductId(), item.getQuantity());
            // } catch (Exception e) {
            //     // 库存归还失败通常是严重问题，需要记录错误并可能需要人工干预
            //     log.error("CRITICAL: Failed to restore stock for productId: {} after order cancellation!", item.getProductId(), e);
            // }
        }
        log.info("Mock stock restored successfully for {} items.", orderItems.size());
    }


    @Override
    @Transactional
    public void processPaymentSuccess(String orderNo, String paymentMethod) {
        Order order = orderMapper.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("支付回调处理失败：订单不存在, OrderNo = " + orderNo));

        // 幂等性检查：如果订单已经是支付成功或后续状态，则直接返回
        if (!STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            log.warn("Order {} already processed or not in pending payment state. Current status: {}", orderNo, order.getStatus());
            return;
        }

        order.setStatus(STATUS_PAID);
        order.setPaymentMethod(paymentMethod); // 更新支付方式
        order.setPaidAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        int updatedRows = orderMapper.update(order); // 更新状态、支付时间和方式
        if (updatedRows > 0) {
            log.info("Order payment processed successfully: orderNo={}", orderNo);
            // TODO: 发送支付成功通知 (例如：短信、邮件、站内信)
        } else {
            log.error("Failed to update order status after payment: orderNo={}", orderNo);
            throw new RuntimeException("更新订单支付状态失败");
        }
    }
}
