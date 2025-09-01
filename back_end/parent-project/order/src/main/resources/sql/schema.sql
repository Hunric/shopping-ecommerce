-- 订单信息表
CREATE TABLE IF NOT EXISTS order_info (
    order_id BIGINT AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    store_id BIGINT NOT NULL COMMENT '店铺ID',
    shipping_id BIGINT NOT NULL COMMENT '收货信息ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '优惠金额',
    shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '运费',
    order_status VARCHAR(20) NOT NULL COMMENT '订单状态',
    payment_method VARCHAR(20) COMMENT '支付方式',
    order_note VARCHAR(255) COMMENT '订单备注',
    order_items TEXT NOT NULL COMMENT '订单项（JSON格式）',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    complete_time DATETIME COMMENT '完成时间',
    cancel_time DATETIME COMMENT '取消时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_store_id (store_id),
    INDEX idx_order_no (order_no),
    INDEX idx_create_time (create_time),
    INDEX idx_order_status (order_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- 收货信息表
CREATE TABLE IF NOT EXISTS shipping_info (
    shipping_id BIGINT AUTO_INCREMENT COMMENT '收货信息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区/县',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    post_code VARCHAR(20) COMMENT '邮政编码',
    is_default BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为默认地址',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (shipping_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货信息表';

-- 订单物流信息表
CREATE TABLE IF NOT EXISTS logistics_info (
    logistics_id BIGINT AUTO_INCREMENT COMMENT '物流ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    company_name VARCHAR(100) NOT NULL COMMENT '物流公司名称',
    tracking_number VARCHAR(100) NOT NULL COMMENT '物流单号',
    status VARCHAR(20) NOT NULL COMMENT '物流状态',
    ship_time DATETIME NOT NULL COMMENT '发货时间',
    estimated_delivery_time DATETIME COMMENT '预计送达时间',
    delivery_time DATETIME COMMENT '实际送达时间',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (logistics_id),
    INDEX idx_order_id (order_id),
    INDEX idx_tracking_number (tracking_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流信息表';

-- 物流轨迹表
CREATE TABLE IF NOT EXISTS logistics_track (
    track_id BIGINT AUTO_INCREMENT COMMENT '轨迹ID',
    logistics_id BIGINT NOT NULL COMMENT '物流ID',
    track_time DATETIME NOT NULL COMMENT '轨迹时间',
    location VARCHAR(255) NOT NULL COMMENT '轨迹位置',
    description VARCHAR(500) NOT NULL COMMENT '轨迹详情',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (track_id),
    INDEX idx_logistics_id (logistics_id),
    INDEX idx_track_time (track_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表'; 