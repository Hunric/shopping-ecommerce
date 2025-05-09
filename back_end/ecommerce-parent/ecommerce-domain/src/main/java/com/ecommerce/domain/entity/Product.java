package com.ecommerce.domain.entity;

import jakarta.persistence.*; // Using Jakarta Persistence API annotations
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类，映射数据库中的 t_product 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_product")
public class Product {

    /**
     * 商品唯一标识，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品名称，不能为空
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * 商品详细描述，可以为空
     */
    @Lob // Use @Lob for potentially large text fields (maps to TEXT type)
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 商品所属分类 ID，不能为空
     * - 直接存储 categoryId，符合 MyBatis 常用模式
     */
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    /**
     * 商品销售价格，不能为空，必须大于 0
     */
    @Column(nullable = false, precision = 10, scale = 2) // DECIMAL(10, 2)
    private BigDecimal price;

    /**
     * 当前库存数量，不能为空，必须大于等于 0
     */
    @Column(name = "stock_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stockQuantity;

    /**
     * 商品主图片 URL，可以为空
     */
    @Column(name = "image_url", length = 512)
    private String imageUrl;

    /**
     * 商品状态 (1: 上架, 0: 下架, 2: 删除)
     * - 建议使用枚举类型配合转换器，但简单起见，先用 Integer
     */
    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 1: On Sale, 0: Off Sale, 2: Deleted

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

    // --- Optional: JPA Relationship Mapping (for reference or if using JPA features) ---
    /**
     * 商品所属分类
     * - 注意：如果使用此映射，通常不在 Product 类中直接暴露 categoryId 字段。
     * MyBatis 使用时，可能需要调整 Mapper 的 resultMap。
     */
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading recommended
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false) // Maps the category_id column
    private Category category;

    // --- Lifecycle Callbacks (Optional) ---
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (status == null) {
            status = 1; // Default to On Sale
        }
        if (stockQuantity == null) {
            stockQuantity = 0; // Ensure default value
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // You might add convenience methods here, e.g., to check if the product is on sale
    public boolean isOnSale() {
        return this.status != null && this.status == 1;
    }
}