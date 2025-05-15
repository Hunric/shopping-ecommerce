CREATE TABLE settlement_info (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '结算信息id',
    merchant_id INT UNSIGNED NOT NULL COMMENT '商家id',
    bank_account_name VARCHAR(255) NOT NULL COMMENT '银行账户名',
    bank_account_number VARCHAR(255) NOT NULL COMMENT '银行账号',
    bank_name VARCHAR(255) NOT NULL COMMENT '开户银行名称',
    taxpayer_id VARCHAR(255) NOT NULL UNIQUE COMMENT '纳税人识别号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_merchant_id_settlement (merchant_id), -- 一个商家通常只有一套结算信息
    FOREIGN KEY (merchant_id) REFERENCES merchant_info(merchant_id)
) COMMENT '认证与结算信息表';