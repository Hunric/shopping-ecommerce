-- 用户表
CREATE TABLE `t_user` (
                          `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                          `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名 (5-20位字母/数字/下划线)',
                          `password_hash` VARCHAR(60) NOT NULL COMMENT 'BCrypt哈希后的密码',
                          `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱 (RFC 5322)',
                          `phone_number` VARCHAR(20) NULL UNIQUE COMMENT '手机号',
                          `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (1:Active, 0:Inactive)',
                          `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          INDEX `idx_phone_number` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 地址表
CREATE TABLE `t_address` (
                             `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                             `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
                             `phone_number` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
                             `province` VARCHAR(50) NOT NULL COMMENT '省份',
                             `city` VARCHAR(50) NOT NULL COMMENT '城市',
                             `district` VARCHAR(50) NOT NULL COMMENT '区/县',
                             `detailed_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
                             `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址 (1:是, 0:否)',
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             INDEX `idx_user_id` (`user_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收货地址表';

-- 商品分类表
CREATE TABLE `t_category` (
                              `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
                              `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
                              `parent_id` BIGINT NULL COMMENT '父分类ID',
                              `sort_order` INT DEFAULT 0 COMMENT '排序值 (越小越靠前)',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              INDEX `idx_parent_id` (`parent_id`),
                              FOREIGN KEY (`parent_id`) REFERENCES `t_category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE `t_product` (
                             `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
                             `name` VARCHAR(255) NOT NULL COMMENT '商品名称',
                             `description` TEXT NULL COMMENT '商品描述',
                             `category_id` BIGINT NOT NULL COMMENT '分类ID',
                             `price` DECIMAL(10, 2) NOT NULL COMMENT '价格 (需>0)',
                             `stock_quantity` INT NOT NULL DEFAULT 0 COMMENT '库存数量 (需>=0)',
                             `image_url` VARCHAR(512) NULL COMMENT '商品主图URL',
                             `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (1:On Sale, 0:Off Sale, 2:Deleted)',
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             INDEX `idx_category_id` (`category_id`),
                             INDEX `idx_status` (`status`),
                             INDEX `idx_name` (`name`(191)) COMMENT '商品名称索引，用于搜索',
                             FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`),
                             CHECK (`price` > 0),
                             CHECK (`stock_quantity` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品信息表';

-- 订单表
CREATE TABLE `t_order` (
                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
                           `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                           `total_amount` DECIMAL(12, 2) NOT NULL COMMENT '商品总金额',
                           `payable_amount` DECIMAL(12, 2) NOT NULL COMMENT '应付总额',
                           `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
                           `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名 (冗余)',
                           `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话 (冗余)',
                           `receiver_address` VARCHAR(500) NOT NULL COMMENT '收货地址 (冗余)',
                           `payment_method` VARCHAR(50) NULL COMMENT '支付方式',
                           `paid_at` DATETIME NULL COMMENT '支付时间',
                           `shipped_at` DATETIME NULL COMMENT '发货时间',
                           `completed_at` DATETIME NULL COMMENT '完成时间',
                           `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX `idx_user_id_status` (`user_id`, `status`),
                           INDEX `idx_created_at` (`created_at`),
                           FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单信息表';

-- 订单项表
CREATE TABLE `t_order_item` (
                                `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
                                `order_id` BIGINT NOT NULL COMMENT '订单ID',
                                `product_id` BIGINT NOT NULL COMMENT '商品ID (快照关联)',
                                `product_name` VARCHAR(255) NOT NULL COMMENT '商品名称 (快照)',
                                `product_image_url` VARCHAR(512) NULL COMMENT '商品图片URL (快照)',
                                `unit_price` DECIMAL(10, 2) NOT NULL COMMENT '下单时单价 (快照)',
                                `quantity` INT NOT NULL COMMENT '购买数量 (需>0)',
                                `total_price` DECIMAL(12, 2) NOT NULL COMMENT '该项总价',
                                INDEX `idx_order_id` (`order_id`),
                                INDEX `idx_product_id` (`product_id`),
                                FOREIGN KEY (`order_id`) REFERENCES `t_order` (`id`) ON DELETE CASCADE,
                                FOREIGN KEY (`product_id`) REFERENCES `t_product` (`id`) ON DELETE RESTRICT,
                                CHECK (`quantity` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项信息表';