CREATE TABLE merchant_info (
    merchant_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商家id',
    merchant_name VARCHAR(20) NOT NULL COMMENT '商家名称',
    phone_number VARCHAR(11) UNIQUE COMMENT '手机号',
    email VARCHAR(255) UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码(哈希和加盐后的密文)',
    merchant_type ENUM('enterprise', 'individual') NOT NULL COMMENT '商家类型: 企业/个体',
    business_license_no CHAR(18) NOT NULL UNIQUE COMMENT '营业执照编号(统一社会信用代码)',
    legal_person_name VARCHAR(16) NOT NULL COMMENT '法人姓名',
    legal_person_id_card CHAR(18) NOT NULL UNIQUE COMMENT '法人身份证号',
    contact_person_name VARCHAR(16) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(11) UNIQUE COMMENT '联系电话',
    contact_email VARCHAR(255) UNIQUE COMMENT '邮箱地址',
    province VARCHAR(15) NOT NULL COMMENT '省',
    city VARCHAR(10) NOT NULL COMMENT '市',
    county VARCHAR(10) NOT NULL COMMENT '县',
    district VARCHAR(10) NOT NULL COMMENT '区',
    detailed_address VARCHAR(30) NOT NULL COMMENT '具体地址',
    PRIMARY KEY (merchant_id),
    UNIQUE KEY uk_merchant_name (merchant_name)
) COMMENT '商家基础信息表';
-- 触发器或后端代码校验: phone_number 和 email 至少一个非空
-- 营业执照编号验证规则(后端): ^[0-9A-Z]{17}[0-9A-Z]$
-- 法人身份证号前端初步校验+后端算法校验