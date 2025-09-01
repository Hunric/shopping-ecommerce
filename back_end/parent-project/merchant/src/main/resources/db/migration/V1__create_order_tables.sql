-- 订单信息表
CREATE TABLE IF NOT EXISTS order_info (
    order_id INT UNSIGNED AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id INT UNSIGNED NOT NULL COMMENT '用户ID',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺ID',
    shipping_id INT UNSIGNED NOT NULL COMMENT '收货信息ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10, 2) NOT NULL COMMENT '实付金额',
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    shipping_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    order_status ENUM('pending_payment', 'pending_shipment', 'pending_receipt', 'completed', 'cancelled', 'refunded') NOT NULL DEFAULT 'pending_payment' COMMENT '订单状态',
    payment_method ENUM('alipay', 'wechat', 'credit_card', 'cod') NULL COMMENT '支付方式',
    order_note VARCHAR(255) NULL COMMENT '订单备注',
    order_items JSON NOT NULL COMMENT '订单项(JSON格式)',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time TIMESTAMP NULL COMMENT '支付时间',
    ship_time TIMESTAMP NULL COMMENT '发货时间',
    complete_time TIMESTAMP NULL COMMENT '完成时间',
    cancel_time TIMESTAMP NULL COMMENT '取消时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_store_id (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单信息表';

-- 收货信息表
CREATE TABLE IF NOT EXISTS shipping_info (
    shipping_id INT UNSIGNED AUTO_INCREMENT COMMENT '收货信息ID',
    user_id INT UNSIGNED NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区/县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (shipping_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货信息表';

-- 物流信息表
CREATE TABLE IF NOT EXISTS logistics_info (
    logistics_id INT UNSIGNED AUTO_INCREMENT COMMENT '物流ID',
    order_id INT UNSIGNED NOT NULL COMMENT '订单ID',
    shipping_company VARCHAR(50) NOT NULL COMMENT '快递公司',
    tracking_number VARCHAR(50) NOT NULL COMMENT '快递单号',
    ship_time TIMESTAMP NOT NULL COMMENT '发货时间',
    logistics_status ENUM('not_shipped', 'waiting_pickup', 'in_transit', 'delivered', 'rejected') NOT NULL DEFAULT 'waiting_pickup' COMMENT '物流状态',
    logistics_track JSON NULL COMMENT '物流轨迹(JSON格式)',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (logistics_id),
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_number (tracking_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流信息表';

-- 用户信息表（如果不存在）
CREATE TABLE IF NOT EXISTS user_info (
    user_id INT UNSIGNED AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) NULL COMMENT '昵称',
    phone VARCHAR(20) NULL COMMENT '手机号',
    email VARCHAR(100) NULL COMMENT '邮箱',
    avatar VARCHAR(255) NULL COMMENT '头像',
    gender TINYINT(1) NULL DEFAULT 0 COMMENT '性别（0-未知，1-男，2-女）',
    birthday TIMESTAMP NULL COMMENT '生日',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户状态（0-禁用，1-启用）',
    last_login_time TIMESTAMP NULL COMMENT '最后登录时间',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表'; 