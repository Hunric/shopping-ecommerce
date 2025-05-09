package com.ecommerce.domain.entity;

import jakarta.persistence.*; // Using Jakarta Persistence API annotations
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// Optional: Import List for relationship mapping if using JPA relationships fully
// import java.util.List;

/**
 * 商品分类实体类，映射数据库中的 t_category 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_category")
public class Category {

    /**
     * 分类唯一标识，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分类名称，不能为空
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 父分类ID，可以为空（表示顶级分类）
     * - 对应数据库中的 parent_id 字段
     * - 如果使用纯 JPA 管理关系，这里通常会是 @ManyToOne Category parent;
     * 但根据 MyBatis 的使用习惯和数据库设计，直接用 parentId 更常见。
     */
    @Column(name = "parent_id") // Explicitly map to parent_id column
    private Long parentId;

    /**
     * 同级分类下的排序值，默认为 0，越小越靠前
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0") // Specify default value in DB
    private Integer sortOrder;

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

    // --- Optional: JPA Relationship Mappings (for reference or if using JPA features) ---
    // Self-referencing relationship for parent category (Lazy loaded)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false) // Join on parent_id column, read-only for this mapping
    private Category parent;

    // Self-referencing relationship for child categories (Lazy loaded)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Mapped by the 'parent' field in the child Category entity
    private List<Category> children;

    // Relationship to Products (if needed in Category entity)
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY) // Assuming Product entity has 'category' field mapped back
    private List<Product> products;

    // --- Lifecycle Callbacks (Optional) ---
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0; // Ensure default value if not set
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}