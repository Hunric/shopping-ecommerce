CREATE TABLE shipping_info (
    address_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址id',
    user_id INT UNSIGNED NOT NULL COMMENT '用户id',
    recipient_name VARCHAR(16) NOT NULL COMMENT '收货人姓名',
    contact_phone VARCHAR(11) NOT NULL COMMENT '联系电话',
    province VARCHAR(15) NOT NULL COMMENT '省',
    city VARCHAR(10) NOT NULL COMMENT '市',
    county VARCHAR(10) NOT NULL COMMENT '县',
    district VARCHAR(10) NOT NULL COMMENT '区',
    detailed_address VARCHAR(30) NOT NULL COMMENT '具体地址',
    PRIMARY KEY (address_id),
    FOREIGN KEY (user_id) REFERENCES user_info(user_id)
) COMMENT '收货信息表';