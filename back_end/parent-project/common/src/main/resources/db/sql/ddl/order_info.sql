CREATE TABLE order_info (
    order_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单id',
    order_sn VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    user_id INT UNSIGNED NOT NULL COMMENT '买家(用户)id',
    merchant_id INT UNSIGNED NOT NULL COMMENT '商家id',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺id',
    total_product_amount DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    discount_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    order_status ENUM('pending_payment', 'pending_shipment', 'pending_receipt', 'completed', 'cancelled', 'refunded') NOT NULL COMMENT '订单状态 (pending_payment:待付款, pending_shipment:待发货, pending_receipt:待收货, completed:已完成, cancelled:已取消, refunded:已退款)',
    -- 商品信息相关字段 (如商品名快照、基础属性快照等)可以考虑存入订单商品表 (order_item) 以支持一个订单多个商品
    -- 这里简化处理，假设一个订单只包含一种主要商品信息快照
    product_name_snapshot VARCHAR(20) NOT NULL COMMENT '商品名快照',
    basic_attributes_snapshot VARCHAR(50) COMMENT '基础属性快照',
    PRIMARY KEY (order_id),
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    FOREIGN KEY (merchant_id) REFERENCES merchant_info(merchant_id),
    FOREIGN KEY (store_id) REFERENCES store_info(store_id)
    -- 快照字段不应作为外键引用
) COMMENT '订单信息表';
-- 订单的状态信息由后端管理