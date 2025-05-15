CREATE TABLE logistics_info (
    logistics_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物流信息id',
    order_id INT UNSIGNED NOT NULL UNIQUE COMMENT '订单id',
    logistics_status ENUM('not_shipped', 'waiting_pickup', 'in_transit', 'delivered', 'rejected') COMMENT '物流状态 (not_shipped:未发货, waiting_pickup:待揽件, in_transit:运输中, delivered:已收货, rejected:拒收)',
    shipping_time TIMESTAMP NULL COMMENT '发货时间',
    delivery_time TIMESTAMP NULL COMMENT '签收时间',
    logistics_company VARCHAR(100) COMMENT '物流公司',
    tracking_number VARCHAR(100) UNIQUE COMMENT '物流单号',
    shipping_address_id INT UNSIGNED NOT NULL COMMENT '收货地址id',
    PRIMARY KEY (logistics_id),
    FOREIGN KEY (order_id) REFERENCES order_info(order_id),
    FOREIGN KEY (shipping_address_id) REFERENCES shipping_info(address_id)
) COMMENT '物流信息表';