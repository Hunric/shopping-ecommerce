CREATE TABLE user_info (
    user_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
    username VARCHAR(20) NOT NULL COMMENT '用户名',
    phone_number VARCHAR(11) NOT NULL COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码(哈希和加盐后的密文)',
    gender CHAR(1) COMMENT '性别',
    avatar_url VARCHAR(255) DEFAULT 'default_avatar_url' COMMENT '头像图片链接',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone_number (phone_number)
) COMMENT '个人信息表';