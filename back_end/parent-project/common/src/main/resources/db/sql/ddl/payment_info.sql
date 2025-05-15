CREATE TABLE payment_info (
    payment_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '支付信息id',
    order_id INT UNSIGNED NOT NULL UNIQUE COMMENT '订单id',
    payment_status ENUM('unpaid', 'paid', 'partially_paid', 'payment_failed', 'refunding', 'refunded') NOT NULL COMMENT '支付状态 (unpaid:未支付, paid:已支付, partially_paid:部分支付, payment_failed:支付失败, refunding:退款中, refunded:已退款)',
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    payment_method VARCHAR(50) COMMENT '支付方式',
    actual_amount DECIMAL(10,2) COMMENT '实际支付金额',
    transaction_id VARCHAR(255) UNIQUE COMMENT '支付平台交易流水号',
    PRIMARY KEY (payment_id),
    FOREIGN KEY (order_id) REFERENCES order_info(order_id)
) COMMENT '支付信息表';