CREATE TABLE sku_info (
    sku_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SKU id',
    spu_id INT UNSIGNED NOT NULL COMMENT '商品(SPU)id',
    sku_name VARCHAR(255) NOT NULL COMMENT 'SKU名称 (通过基础属性值生成)',
    sale_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格',
    stock_quantity INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
    attribute_combination JSON NOT NULL COMMENT '具体属性值组合 (由后端生成)',
    status TINYINT NOT NULL DEFAULT 2 COMMENT '状态 (1:上架, 2:下架, 3:库存不足)',
    image_url VARCHAR(255) COMMENT '专属图片链接 (默认:所属SPU图片链接)',
    PRIMARY KEY (sku_id),
    UNIQUE KEY uk_sku_name_spu (spu_id, sku_name), -- 同一个SPU下的SKU名称唯一
    FOREIGN KEY (spu_id) REFERENCES spu_info(product_id)
) COMMENT 'SKU表';