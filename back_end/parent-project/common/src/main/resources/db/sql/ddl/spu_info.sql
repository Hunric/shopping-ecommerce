CREATE TABLE spu_info (
    product_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品id (SPU id)',
    merchant_id INT UNSIGNED NOT NULL COMMENT '商家id',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺id',
    category_id INT UNSIGNED NOT NULL COMMENT '分类id',
    product_name VARCHAR(20) NOT NULL COMMENT '商品名',
    description VARCHAR(100) COMMENT '商品描述',
    image_url TEXT COMMENT '商品图片链接或Base64数据',
    display_price DECIMAL(10,2) NOT NULL COMMENT '商品展示价格 (非实际价格)',
    basic_attributes JSON NOT NULL COMMENT '基础属性值',
    non_basic_attributes JSON NOT NULL COMMENT '非基础属性值',
    brand_name VARCHAR(50) COMMENT '品牌名称',
    selling_point VARCHAR(200) COMMENT '卖点描述',
    unit VARCHAR(10) NOT NULL DEFAULT '件' COMMENT '计量单位',
    status ENUM('draft', 'pending', 'approved', 'rejected', 'on_shelf', 'off_shelf') NOT NULL DEFAULT 'draft' COMMENT '商品状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (product_id),
    UNIQUE KEY uk_store_product_name (store_id, product_name), -- 店铺内商品名称唯一
    FOREIGN KEY (merchant_id) REFERENCES merchant_info(merchant_id),
    FOREIGN KEY (store_id) REFERENCES store_info(store_id),
    FOREIGN KEY (category_id) REFERENCES product_category(category_id)
) COMMENT '商品信息表 (SPU)';
-- 后端检查: category_id 必须为叶子节点