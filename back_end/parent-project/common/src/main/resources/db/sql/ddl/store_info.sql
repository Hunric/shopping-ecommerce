CREATE TABLE store_info (
    store_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '店铺id',
    merchant_id INT UNSIGNED NOT NULL COMMENT '商家id',
    store_name VARCHAR(20) NOT NULL COMMENT '店铺名称',
    logo_url VARCHAR(255) COMMENT '店铺LOGO链接',
    description VARCHAR(50) COMMENT '店铺简介',
    status ENUM('open', 'closed', 'pending') NOT NULL COMMENT '店铺状态 (审核中/正常/停用)',
    created_at DATETIME NOT NULL COMMENT '店铺入驻时间',
    PRIMARY KEY (store_id),
    UNIQUE KEY uk_store_name (store_name),
    FOREIGN KEY (merchant_id) REFERENCES merchant_info(merchant_id)
) COMMENT '店铺基础信息表';