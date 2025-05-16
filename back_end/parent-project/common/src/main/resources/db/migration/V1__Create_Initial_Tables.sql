-- 用户基础表
-- 引用自user_info.sql
CREATE TABLE IF NOT EXISTS user_info (
    user_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
    username VARCHAR(20) NOT NULL COMMENT '用户名',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码(哈希和加盐后的密文)',
    gender CHAR(1) COMMENT '性别',
    avatar_url VARCHAR(255) DEFAULT 'default_avatar_url' COMMENT '头像图片链接',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone_number (email)
) COMMENT '个人信息表';

-- 收货信息表
-- 引用自shipping_info.sql
CREATE TABLE IF NOT EXISTS shipping_info (
    shipping_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收货信息id',
    user_id INT UNSIGNED NOT NULL COMMENT '用户id',
    receiver_name VARCHAR(20) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(11) NOT NULL COMMENT '收货人手机号',
    province VARCHAR(20) NOT NULL COMMENT '省份',
    city VARCHAR(20) NOT NULL COMMENT '城市',
    district VARCHAR(20) NOT NULL COMMENT '区/县',
    detail_address VARCHAR(100) NOT NULL COMMENT '详细地址',
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址',
    PRIMARY KEY (shipping_id),
    INDEX idx_user_id (user_id)
) COMMENT '收货地址表';

-- 商家信息表
-- 引用自merchant_info.sql
CREATE TABLE IF NOT EXISTS merchant_info (
    merchant_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商家id',
    merchant_name VARCHAR(20) NOT NULL COMMENT '商家名称',
    phone_number VARCHAR(11) UNIQUE COMMENT '手机号',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱',
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

-- 店铺信息表
-- 引用自store_info.sql
CREATE TABLE IF NOT EXISTS store_info (
    store_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '店铺id',
    merchant_id INT UNSIGNED NOT NULL COMMENT '商家id',
    store_name VARCHAR(50) NOT NULL COMMENT '店铺名称',
    store_logo VARCHAR(255) COMMENT '店铺Logo URL',
    store_description TEXT COMMENT '店铺描述',
    open_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开店时间',
    status ENUM('open', 'closed', 'suspended') NOT NULL DEFAULT 'open' COMMENT '店铺状态',
    credit_score INT NOT NULL DEFAULT 100 COMMENT '店铺信用分',
    PRIMARY KEY (store_id),
    INDEX idx_merchant_id (merchant_id)
) COMMENT '店铺信息表';

-- 结算信息表
-- 引用自settlement_info.sql
CREATE TABLE IF NOT EXISTS settlement_info (
    settlement_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '结算id',
    merchant_id INT UNSIGNED NOT NULL COMMENT '商家id',
    bank_name VARCHAR(50) NOT NULL COMMENT '开户银行',
    bank_account VARCHAR(20) NOT NULL COMMENT '银行账号',
    account_name VARCHAR(50) NOT NULL COMMENT '开户名',
    settlement_cycle ENUM('daily', 'weekly', 'monthly') NOT NULL DEFAULT 'monthly' COMMENT '结算周期',
    commission_rate DECIMAL(5,2) NOT NULL DEFAULT 5.00 COMMENT '佣金比例(%)',
    PRIMARY KEY (settlement_id),
    INDEX idx_merchant_id (merchant_id)
) COMMENT '结算信息表';

-- 商品分类表
-- 引用自product_category.sql
CREATE TABLE IF NOT EXISTS product_category (
    category_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类id',
    parent_id INT UNSIGNED DEFAULT NULL COMMENT '父分类id',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_level TINYINT NOT NULL COMMENT '分类层级',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序字段',
    is_visible TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否可见',
    icon_url VARCHAR(255) COMMENT '分类图标URL',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (category_id),
    INDEX idx_parent_id (parent_id)
) COMMENT '商品分类表';

-- 分类属性表
-- 引用自category_attribute.sql
CREATE TABLE IF NOT EXISTS category_attribute (
    attribute_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '属性id',
    category_id INT UNSIGNED NOT NULL COMMENT '分类id',
    attribute_name VARCHAR(50) NOT NULL COMMENT '属性名称',
    attribute_type ENUM('TEXT', 'NUMBER', 'DATE', 'BOOLEAN', 'ENUM') NOT NULL COMMENT '属性类型',
    is_required TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否必填',
    is_spec TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为规格属性(影响SKU生成)',
    is_searchable TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否可搜索',
    input_type ENUM('INPUT', 'SELECT', 'RADIO', 'CHECKBOX', 'TEXTAREA') NOT NULL DEFAULT 'INPUT' COMMENT '输入类型',
    attribute_unit VARCHAR(10) COMMENT '属性单位(如：kg, cm)',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序字段',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (attribute_id),
    INDEX idx_category_id (category_id),
    INDEX idx_attribute_name (attribute_name)
) COMMENT '分类属性表';

-- 属性可选值表
-- 引用自attribute_option.sql
CREATE TABLE IF NOT EXISTS attribute_option (
    option_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '选项id',
    attribute_id INT UNSIGNED NOT NULL COMMENT '属性id',
    option_value VARCHAR(50) NOT NULL COMMENT '选项值',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序字段',
    PRIMARY KEY (option_id),
    INDEX idx_attribute_id (attribute_id)
) COMMENT '属性可选值表';

-- 商品SPU表(标准化产品单元)
-- 引用自spu_info.sql
CREATE TABLE IF NOT EXISTS spu_info (
    spu_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SPU ID',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺ID',
    category_id INT UNSIGNED NOT NULL COMMENT '分类ID',
    spu_name VARCHAR(100) NOT NULL COMMENT 'SPU名称',
    spu_description TEXT COMMENT 'SPU描述',
    brand_name VARCHAR(50) COMMENT '品牌名称',
    main_image_url VARCHAR(255) NOT NULL COMMENT '主图URL',
    image_urls JSON COMMENT '图片URLs(JSON数组)',
    selling_point TEXT COMMENT '卖点描述',
    unit VARCHAR(10) NOT NULL DEFAULT '件' COMMENT '单位(如：件/台/套)',
    attributes JSON COMMENT '商品属性(JSON格式)',
    status ENUM('draft', 'pending', 'approved', 'rejected', 'on_shelf', 'off_shelf') NOT NULL DEFAULT 'draft' COMMENT '状态',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (spu_id),
    INDEX idx_store_id (store_id),
    INDEX idx_category_id (category_id),
    INDEX idx_spu_name (spu_name)
) COMMENT '商品SPU表';

-- 商品SKU表(库存量单位)
-- 引用自sku_info.sql
CREATE TABLE IF NOT EXISTS sku_info (
    sku_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    spu_id INT UNSIGNED NOT NULL COMMENT 'SPU ID',
    sku_code VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    specifications JSON NOT NULL COMMENT '规格(JSON格式,如{"颜色":"红色","尺寸":"XL"})',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    warn_stock INT NOT NULL DEFAULT 10 COMMENT '库存警告阈值',
    sku_image_url VARCHAR(255) COMMENT 'SKU图片URL',
    sku_status ENUM('available', 'unavailable') NOT NULL DEFAULT 'available' COMMENT 'SKU状态',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (sku_id),
    INDEX idx_spu_id (spu_id),
    UNIQUE KEY uk_sku_code (sku_code)
) COMMENT '商品SKU表';

-- 购物车表
-- 引用自cart_info.sql
CREATE TABLE IF NOT EXISTS cart_info (
    cart_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    user_id INT UNSIGNED NOT NULL COMMENT '用户ID',
    sku_id INT UNSIGNED NOT NULL COMMENT 'SKU ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    is_selected TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否选中',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (cart_id),
    UNIQUE KEY uk_user_sku (user_id, sku_id),
    INDEX idx_user_id (user_id)
) COMMENT '购物车表';

-- 订单信息表
-- 引用自order_info.sql
CREATE TABLE IF NOT EXISTS order_info (
    order_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单编号',
    user_id INT UNSIGNED NOT NULL COMMENT '用户ID',
    store_id INT UNSIGNED NOT NULL COMMENT '店铺ID',
    shipping_id INT UNSIGNED NOT NULL COMMENT '收货信息ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
    shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    order_status ENUM('pending_payment', 'pending_shipment', 'pending_receipt', 'completed', 'cancelled', 'refunded') NOT NULL DEFAULT 'pending_payment' COMMENT '订单状态',
    payment_method ENUM('alipay', 'wechat', 'credit_card', 'cod') COMMENT '支付方式',
    order_note VARCHAR(255) COMMENT '订单备注',
    order_items JSON NOT NULL COMMENT '订单项(JSON格式)',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time TIMESTAMP NULL COMMENT '支付时间',
    ship_time TIMESTAMP NULL COMMENT '发货时间',
    complete_time TIMESTAMP NULL COMMENT '完成时间',
    cancel_time TIMESTAMP NULL COMMENT '取消时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_store_id (store_id)
) COMMENT '订单信息表';

-- 物流信息表
-- 引用自logistics_info.sql
CREATE TABLE IF NOT EXISTS logistics_info (
    logistics_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物流ID',
    order_id INT UNSIGNED NOT NULL COMMENT '订单ID',
    shipping_company VARCHAR(50) NOT NULL COMMENT '快递公司',
    tracking_number VARCHAR(50) NOT NULL COMMENT '快递单号',
    ship_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发货时间',
    logistics_status ENUM('not_shipped', 'waiting_pickup', 'in_transit', 'delivered', 'rejected') NOT NULL DEFAULT 'not_shipped' COMMENT '物流状态',
    logistics_info JSON COMMENT '物流轨迹(JSON格式)',
    delivery_time TIMESTAMP NULL COMMENT '送达时间',
    sign_name VARCHAR(50) COMMENT '签收人',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (logistics_id),
    UNIQUE KEY uk_order_id (order_id),
    INDEX idx_tracking_number (tracking_number)
) COMMENT '物流信息表';

-- 支付信息表
-- 引用自payment_info.sql
CREATE TABLE IF NOT EXISTS payment_info (
    payment_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '支付信息id',
    order_id INT UNSIGNED NOT NULL UNIQUE COMMENT '订单id',
    payment_status ENUM('unpaid', 'paid', 'partially_paid', 'payment_failed', 'refunding', 'refunded') NOT NULL COMMENT '支付状态 (unpaid:未支付, paid:已支付, partially_paid:部分支付, payment_failed:支付失败, refunding:退款中, refunded:已退款)',
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    payment_method VARCHAR(50) COMMENT '支付方式',
    actual_amount DECIMAL(10,2) COMMENT '实际支付金额',
    transaction_id VARCHAR(255) UNIQUE COMMENT '支付平台交易流水号',
    PRIMARY KEY (payment_id)
) COMMENT '支付信息表'; 