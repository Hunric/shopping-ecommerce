package com.ecommerce.domain.entity; // 替换为您的实际包名

import jakarta.persistence.*; // 使用 Jakarta Persistence API 注解
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户实体类，映射数据库中的 t_user 表
 */
@Data // Lombok 注解：自动生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok 注解：自动生成无参构造函数
@AllArgsConstructor // Lombok 注解：自动生成全参构造函数
@Entity // JPA 注解：表示这是一个实体类
@Table(name = "t_user") // JPA 注解：指定映射的数据库表名
public class User {

    /**
     * 用户唯一标识，主键，自增
     */
    @Id // JPA 注解：标记为主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA 注解：指定主键生成策略为自增
    private Long id;

    /**
     * 用户名，唯一且不能为空
     */
    @Column(unique = true, nullable = false, length = 50) // JPA 注解：映射列属性
    private String username;

    /**
     * 哈希后的密码，不能为空
     */
    @Column(name = "password_hash", nullable = false, length = 60) // 指定列名并设置约束
    private String passwordHash; // 注意：实体类属性名使用驼峰命名法

    /**
     * 电子邮箱，唯一且不能为空
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 手机号码，唯一，可以为空
     */
    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    /**
     * 账户状态 (1: Active, 0: Inactive/Disabled)
     * 使用 Integer 或 Short 映射 TINYINT
     */
    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1") // columnDefinition 用于指定数据库默认值
    private Integer status; // 也可以使用 Short

    /**
     * 记录创建时间，不能为空，数据库自动设置默认值
     */
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * 记录最后更新时间，不能为空，数据库自动更新
     */
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    // 如果需要，可以在这里添加与其他实体（如 Address, Order）的关系映射注解
    // 例如：@OneToMany(mappedBy = "user") private List<Address> addresses;
    // 但由于我们主要使用 MyBatis，这些关系映射注解主要是为了清晰表达结构，
    // 实际的数据加载通常由 Mapper 处理。

}
