package com.ecommerce.domain.entity; // 替换为您的实际包名

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户收货地址实体类，映射数据库中的 t_address 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_address")
public class Address {

    /**
     * 地址唯一标识，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的用户 ID，不能为空
     * 注意：这里不直接使用 @ManyToOne，因为主要用 MyBatis。
     * 但保留 userId 字段用于数据传递。
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 收货人姓名，不能为空
     */
    @Column(name = "receiver_name", nullable = false, length = 50)
    private String receiverName;

    /**
     * 收货人联系电话，不能为空
     */
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    /**
     * 省份，不能为空
     */
    @Column(nullable = false, length = 50)
    private String province;

    /**
     * 城市，不能为空
     */
    @Column(nullable = false, length = 50)
    private String city;

    /**
     * 区/县，不能为空
     */
    @Column(nullable = false, length = 50)
    private String district;

    /**
     * 详细街道门牌号，不能为空
     */
    @Column(name = "detailed_address", nullable = false, length = 255)
    private String detailedAddress;

    /**
     * 是否为默认地址 (1: 是, 0: 否)
     * 使用 Boolean 映射 TINYINT(1)
     */
    @Column(name = "is_default", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDefault;

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

    // 如果使用 JPA 管理关系，可以添加如下注解：
    // @ManyToOne(fetch = FetchType.LAZY) // LAZY 表示延迟加载用户信息
    // @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false) // 仅用于查询关联，不用于插入/更新
    // private User user;
}
