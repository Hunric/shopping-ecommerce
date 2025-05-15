CREATE TABLE category_attribute (
    attribute_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类属性id',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺id',
    category_id INT UNSIGNED NOT NULL COMMENT '分类id',
    attribute_name VARCHAR(20) NOT NULL COMMENT '属性名称',
    attribute_type VARCHAR(10) NOT NULL COMMENT '属性类型 (如文本/枚举/数字/布尔等)',
    is_basic_attribute BOOLEAN DEFAULT FALSE COMMENT '是否基础属性',
    is_required BOOLEAN DEFAULT FALSE COMMENT '是否必填 (若是基础属性,则是必填)',
    PRIMARY KEY (attribute_id),
    UNIQUE KEY uk_attr_name_category_store (store_id, category_id, attribute_name), -- 同一店铺同一分类下的属性名唯一
    FOREIGN KEY (store_id) REFERENCES store_info(store_id),
    FOREIGN KEY (category_id) REFERENCES product_category(category_id)
) COMMENT '分类属性信息表';
-- 后端检查: category_id 必须为叶子节点
-- 后端逻辑: 若 is_basic_attribute 为 true, 则 is_required 也为 true