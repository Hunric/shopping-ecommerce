package com.ecommerce.domain.entity; // 确保在 domain 模块的 entity 包下

import jakarta.persistence.*; // 使用 Jakarta Persistence API 注解
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List; // 用于关联 OrderItem

/**
 * 订单实体类，映射数据库中的 t_order 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order")
public class Order {

    /**
     * 订单唯一标识，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 订单号，唯一且不能为空
     */
    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;

    /**
     * 关联的用户 ID，不能为空
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 商品总金额 (所有订单项原价之和)
     */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /**
     * 应付总额 (实际支付金额，可能包含运费、折扣等)
     */
    @Column(name = "payable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal payableAmount;

    /**
     * 订单状态
     */
    @Column(nullable = false, length = 20)
    private String status; // 例如: PENDING_PAYMENT, PAID, SHIPPED, COMPLETED, CANCELLED

    /**
     * 收货人姓名 (冗余快照)
     */
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    /**
     * 收货人电话 (冗余快照)
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * 完整收货地址 (冗余快照)
     */
    @Column(name = "receiver_address", nullable = false, length = 500)
    private String receiverAddress;

    /**
     * 支付方式
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * 支付时间
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * 发货时间
     */
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    /**
     * 完成时间 (用户确认收货或系统自动确认)
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间
     */
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // --- JPA 关系映射 (可选，主要用于结构清晰，MyBatis 可能不直接使用) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // mappedBy = "order" 指向 OrderItem 实体中名为 "order" 的 @ManyToOne 字段
    private List<OrderItem> items;

    // --- 生命周期回调 (可选) ---
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING_PAYMENT"; // 设置默认初始状态
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
