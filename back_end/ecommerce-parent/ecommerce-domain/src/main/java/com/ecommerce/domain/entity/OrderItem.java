package com.ecommerce.domain.entity; // 确保在 domain 模块的 entity 包下

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单项实体类，映射数据库中的 t_order_item 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_order_item")
public class OrderItem {

    /**
     * 订单项唯一标识，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属订单 ID，不能为空
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * 关联的商品 ID (用于追溯原始商品)，不能为空
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 下单时的商品名称 (快照)，不能为空
     */
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    /**
     * 下单时的商品图片 URL (快照)，可以为空
     */
    @Column(name = "product_image_url", length = 512)
    private String productImageUrl;

    /**
     * 下单时的商品单价 (快照)，不能为空
     */
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * 购买数量，不能为空，必须大于0
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 该项商品总价 (quantity * unitPrice)，不能为空
     */
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    // --- JPA 关系映射 (可选) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product; // 关联原始商品

}
