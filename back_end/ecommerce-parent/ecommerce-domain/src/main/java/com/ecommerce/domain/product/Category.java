package com.ecommerce.domain.product;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer level;
    private Integer sort;
    private Integer status; // 0-禁用 1-正常
    private String icon;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 