-- 插入示例用户数据
INSERT INTO user_info (user_id, username, password, nickname, phone, email, avatar, gender, status)
VALUES
(1, 'customer1', '$2a$10$dYRcVzb5mNNFn4eOkr9RvOJrYO3RMXg5jD9yW9BjcJ6kNTPAZHYfS', '张三', '13800138001', 'customer1@example.com', 'https://via.placeholder.com/100', 1, 1),
(2, 'customer2', '$2a$10$dYRcVzb5mNNFn4eOkr9RvOJrYO3RMXg5jD9yW9BjcJ6kNTPAZHYfS', '李四', '13800138002', 'customer2@example.com', 'https://via.placeholder.com/100', 1, 1),
(3, 'customer3', '$2a$10$dYRcVzb5mNNFn4eOkr9RvOJrYO3RMXg5jD9yW9BjcJ6kNTPAZHYfS', '王五', '13800138003', 'customer3@example.com', 'https://via.placeholder.com/100', 1, 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

-- 插入示例收货地址数据
INSERT INTO shipping_info (shipping_id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default)
VALUES
(1, 1, '张三', '13800138001', '北京市', '北京市', '朝阳区', '三里屯SOHO 101室', 1),
(2, 1, '张三', '13800138001', '上海市', '上海市', '浦东新区', '陆家嘴金融中心 2201室', 0),
(3, 2, '李四', '13800138002', '广东省', '深圳市', '南山区', '科技园南区 B栋 1001室', 1),
(4, 3, '王五', '13800138003', '浙江省', '杭州市', '西湖区', '西溪路 101号', 1)
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- 插入示例订单数据
INSERT INTO order_info (order_id, order_no, user_id, store_id, shipping_id, total_amount, actual_amount, discount_amount, shipping_fee, order_status, payment_method, order_note, order_items, create_time, pay_time, ship_time)
VALUES
(1, 'ORD20230601001', 1, 1, 1, 299.00, 279.00, 20.00, 0.00, 'pending_shipment', 'alipay', '请尽快发货', 
'[{"skuId":101,"productId":1,"productName":"时尚连衣裙","skuName":"白色 M码","price":299.00,"quantity":1,"imageUrl":"https://via.placeholder.com/100"}]', 
'2023-06-01 10:00:00', '2023-06-01 10:05:00', NULL),

(2, 'ORD20230602001', 2, 1, 3, 598.00, 578.00, 20.00, 0.00, 'pending_receipt', 'wechat', NULL, 
'[{"skuId":102,"productId":2,"productName":"休闲裤","skuName":"黑色 XL码","price":299.00,"quantity":2,"imageUrl":"https://via.placeholder.com/100"}]', 
'2023-06-02 14:30:00', '2023-06-02 14:35:00', '2023-06-03 09:00:00'),

(3, 'ORD20230603001', 3, 1, 4, 1596.00, 1546.00, 50.00, 0.00, 'completed', 'credit_card', NULL, 
'[{"skuId":103,"productId":3,"productName":"运动鞋","skuName":"白色 42码","price":799.00,"quantity":2,"imageUrl":"https://via.placeholder.com/100"}]', 
'2023-06-03 16:20:00', '2023-06-03 16:25:00', '2023-06-04 10:00:00'),

(4, 'ORD20230604001', 1, 1, 1, 399.00, 379.00, 20.00, 0.00, 'pending_payment', NULL, NULL, 
'[{"skuId":104,"productId":4,"productName":"时尚背包","skuName":"黑色 均码","price":399.00,"quantity":1,"imageUrl":"https://via.placeholder.com/100"}]', 
'2023-06-04 11:00:00', NULL, NULL),

(5, 'ORD20230605001', 2, 1, 3, 499.00, 479.00, 20.00, 0.00, 'cancelled', 'alipay', '用户申请取消', 
'[{"skuId":105,"productId":5,"productName":"太阳镜","skuName":"黑色 均码","price":499.00,"quantity":1,"imageUrl":"https://via.placeholder.com/100"}]', 
'2023-06-05 09:30:00', '2023-06-05 09:35:00', NULL)
ON DUPLICATE KEY UPDATE order_no = VALUES(order_no);

-- 插入示例物流数据
INSERT INTO logistics_info (logistics_id, order_id, shipping_company, tracking_number, ship_time, logistics_status, logistics_track)
VALUES
(1, 2, '顺丰速运', 'SF1234567890', '2023-06-03 09:00:00', 'in_transit', 
'[
  {"time":"2023-06-03 09:00:00","content":"商家已发货"},
  {"time":"2023-06-03 12:30:00","content":"快件已到达【北京转运中心】"},
  {"time":"2023-06-04 08:15:00","content":"快件已到达【广州转运中心】"},
  {"time":"2023-06-04 16:30:00","content":"快件已到达【广州市南山区营业点】，正在派送中"}
]'),

(2, 3, '中通快递', 'ZT9876543210', '2023-06-04 10:00:00', 'delivered', 
'[
  {"time":"2023-06-04 10:00:00","content":"商家已发货"},
  {"time":"2023-06-04 14:20:00","content":"快件已到达【杭州转运中心】"},
  {"time":"2023-06-05 09:45:00","content":"快件已到达【杭州市西湖区营业点】，正在派送中"},
  {"time":"2023-06-05 14:30:00","content":"快件已签收，签收人：王五，感谢使用中通快递"}
]')
ON DUPLICATE KEY UPDATE order_id = VALUES(order_id); 