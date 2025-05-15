CREATE TABLE attribute_option (
    option_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '可选值id',
    attribute_id INT UNSIGNED NOT NULL COMMENT '分类属性id',
    option_value VARCHAR(20) NOT NULL COMMENT '可选值',
    PRIMARY KEY (option_id),
    FOREIGN KEY (attribute_id) REFERENCES category_attribute(attribute_id)
) COMMENT '属性可选值表';