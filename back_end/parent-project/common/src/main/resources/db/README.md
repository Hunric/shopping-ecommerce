# 数据库脚本目录

本目录用于存放项目所有的数据库相关脚本，包括建表语句、初始化数据和数据库升级脚本等。

## 目录结构

```
db/
├── sql/                      # SQL脚本主目录
│   ├── ddl/                  # 数据定义语言(DDL)脚本，包含建表、修改表结构等
│   │   ├── user_info.sql     # 用户基本信息表
│   │   ├── merchant_info.sql # 商家基本信息表
│   │   ├── store_info.sql    # 店铺基本信息表
│   │   ├── product_category.sql # 商品分类表
│   │   ├── category_attribute.sql # 分类属性表
│   │   ├── attribute_option.sql # 属性可选值表
│   │   ├── spu_info.sql      # 商品SPU表
│   │   ├── sku_info.sql      # 商品SKU表
│   │   ├── cart_info.sql     # 购物车表
│   │   ├── order_info.sql    # 订单信息表
│   │   ├── shipping_info.sql # 收货信息表
│   │   ├── logistics_info.sql # 物流信息表
│   │   ├── payment_info.sql  # 支付信息表
│   │   └── settlement_info.sql # 结算信息表
│   │
│   ├── dml/                  # 数据操作语言(DML)脚本，包含初始化数据
│   │   ├── init_data/        # 初始化数据脚本
│   │   └── test_data/        # 测试数据脚本
│   │
│   └── version/              # 版本化的数据库升级脚本
│       ├── v1.0/             # 1.0版本的升级脚本
│       ├── v1.1/             # 1.1版本的升级脚本
│       └── ...               # 其他版本升级脚本
│
└── docs/                     # 数据库设计文档
    ├── ER图/                 # 实体关系图
    └── 数据字典/             # 数据字典文档
```

## 数据库表结构概述

### 用户相关
- **user_info**: 存储用户基本信息，包括用户名、手机号、密码等
- **shipping_info**: 存储用户收货地址信息

### 商家相关
- **merchant_info**: 存储商家基本信息，包括商家名称、联系方式、营业执照等
- **store_info**: 存储店铺基本信息，关联到商家
- **settlement_info**: 存储商家结算信息，包括银行账户等

### 商品相关
- **product_category**: 商品分类表，支持多级分类
- **category_attribute**: 分类属性表，定义各分类下的属性
- **attribute_option**: 属性可选值表，存储枚举类型属性的可选值
- **spu_info**: 标准化产品单元(SPU)表，存储商品基本信息
- **sku_info**: 库存量单位(SKU)表，存储具体销售规格信息

### 交易相关
- **cart_info**: 购物车表，存储用户加入购物车的商品
- **order_info**: 订单信息表，存储订单基本信息
- **payment_info**: 支付信息表，存储订单支付相关信息
- **logistics_info**: 物流信息表，存储订单物流状态和信息

## 表状态枚举值

### 订单状态 (order_status)
- `pending_payment`: 待付款
- `pending_shipment`: 待发货
- `pending_receipt`: 待收货
- `completed`: 已完成
- `cancelled`: 已取消
- `refunded`: 已退款

### 物流状态 (logistics_status)
- `not_shipped`: 未发货
- `waiting_pickup`: 待揽件
- `in_transit`: 运输中
- `delivered`: 已收货
- `rejected`: 拒收

### 支付状态 (payment_status)
- `unpaid`: 未支付
- `paid`: 已支付
- `partially_paid`: 部分支付
- `payment_failed`: 支付失败
- `refunding`: 退款中
- `refunded`: 已退款

## 命名规范

1. 建表脚本命名: `create_表名.sql`
2. 修改表脚本命名: `alter_表名_YYYYMMDD.sql`
3. 数据初始化脚本: `init_模块名_YYYYMMDD.sql`
4. 版本升级脚本: `upgrade_vX.Y.Z_to_vA.B.C.sql`

## 使用说明

1. 所有新建表和修改表结构的操作，都应该先在此目录下创建相应的SQL脚本
2. 确保脚本可以重复执行而不产生错误（使用 `IF NOT EXISTS` 等语句）
3. 所有脚本应包含详细注释，说明脚本的作用和影响的表
4. 重要的数据变更应附带回滚脚本

## 数据类型注意事项

- 所有ID类型统一使用 `INT UNSIGNED`，自增主键
- JSON数据使用MySQL的 `JSON` 类型存储
- 货币金额使用 `DECIMAL(10,2)` 类型
- 字符串根据实际需要选择合适长度，避免过长浪费空间
- 使用有意义的枚举值代替数字编码，提高代码可读性

## 示例

建表语句示例:

```sql
-- 用户表
CREATE TABLE IF NOT EXISTS user_info (
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
``` 