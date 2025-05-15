CREATE TABLE product_category (
    category_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类id',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺id',
    parent_category_id INT UNSIGNED DEFAULT NULL COMMENT '父分类id',
    category_name VARCHAR(20) NOT NULL COMMENT '分类名称',
    description VARCHAR(50) COMMENT '分类描述信息',
    PRIMARY KEY (category_id),
    UNIQUE KEY uk_category_name_store_id (store_id, category_name), -- 通常一个店铺下的分类名唯一
    FOREIGN KEY (store_id) REFERENCES store_info(store_id),
    FOREIGN KEY (parent_category_id) REFERENCES product_category(category_id)
) COMMENT '商品分类信息表';