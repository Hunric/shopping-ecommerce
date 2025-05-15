CREATE TABLE cart_info (
    cart_item_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项id',
    user_id INT UNSIGNED NOT NULL COMMENT '用户id',
    product_id INT UNSIGNED NOT NULL COMMENT '商品id',
    quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '商品数量',
    product_name_snapshot VARCHAR(50) COMMENT '商品名快照',
    product_image_snapshot VARCHAR(255) COMMENT '商品图片链接快照',
    product_attr_snapshot VARCHAR(50) COMMENT '商品关键属性快照',
    PRIMARY KEY (cart_item_id),
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    FOREIGN KEY (product_id) REFERENCES spu_info(product_id)
) COMMENT '购物车信息表';