# 商家管理模块 API 测试文档

## 测试环境
- 后端服务地址：http://localhost:8080
- 前端服务地址：http://localhost:5173

## 1. 健康检查接口

### 请求
```
GET /api/test/health
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": "商家管理服务运行正常"
}
```

## 2. Dashboard统计数据接口

### 测试接口
```
GET /api/test/dashboard/{merchantId}
```

### 正式接口
```
GET /api/merchant/dashboard/stats/{merchantId}
```

### 请求示例
```
GET /api/test/dashboard/1
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "storeCount": 3,
    "productCount": 156,
    "orderCount": 89,
    "totalRevenue": 12580.50,
    "pendingOrderCount": 15,
    "shippedOrderCount": 25,
    "monthlyRevenue": 3250.80,
    "monthlyOrderCount": 28
  }
}
```

## 3. 店铺列表接口

### 测试接口
```
GET /api/test/stores/{merchantId}
```

### 正式接口
```
GET /api/merchant/store/list/{merchantId}
```

### 请求示例
```
GET /api/test/stores/1
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "storeId": 1,
      "merchantId": 1,
      "storeName": "时尚服装店",
      "storeLogo": "https://via.placeholder.com/200x200/409EFF/FFFFFF?text=Logo1",
      "storeDescription": "专营时尚女装，品质保证，款式新颖",
      "openTime": "2024-06-01T10:00:00",
      "status": "open",
      "creditScore": 98
    },
    {
      "storeId": 2,
      "merchantId": 1,
      "storeName": "数码科技馆",
      "storeLogo": "https://via.placeholder.com/200x200/67C23A/FFFFFF?text=Logo2",
      "storeDescription": "最新数码产品，正品保障，售后无忧",
      "openTime": "2024-09-01T10:00:00",
      "status": "open",
      "creditScore": 95
    }
  ]
}
```

## 4. 创建店铺接口

### 测试接口
```
POST /api/test/store/create
```

### 正式接口
```
POST /api/merchant/store/create
```

### 请求示例
```json
{
  "merchantId": 1,
  "storeName": "新店铺",
  "storeLogo": "https://example.com/logo.jpg",
  "storeDescription": "这是一个新创建的店铺"
}
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "storeId": 999,
    "merchantId": 1,
    "storeName": "测试店铺",
    "storeLogo": "https://via.placeholder.com/200x200/F56C6C/FFFFFF?text=New",
    "storeDescription": "这是一个测试创建的店铺",
    "openTime": "2024-12-19T10:00:00",
    "status": "open",
    "creditScore": 100
  }
}
```

## 5. 商家信息接口

### 正式接口
```
GET /api/merchant/info/{merchantId}
```

### 请求示例
```
GET /api/merchant/info/1
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "merchantId": 1,
    "merchantName": "测试商家",
    "email": "test@example.com",
    "merchantType": "enterprise",
    "businessLicenseNo": "123456789012345678",
    "legalPersonName": "张三",
    "contactPersonName": "李四",
    "contactPhone": "13800138000",
    "contactEmail": "contact@example.com",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "detailedAddress": "科技园南区"
  }
}
```

## 6. 更新店铺接口

### 正式接口
```
PUT /api/merchant/store/{storeId}
```

### 请求示例
```json
{
  "storeName": "更新后的店铺名称",
  "storeLogo": "https://example.com/new-logo.jpg",
  "storeDescription": "更新后的店铺描述",
  "status": "open"
}
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "storeId": 1,
    "merchantId": 1,
    "storeName": "更新后的店铺名称",
    "storeLogo": "https://example.com/new-logo.jpg",
    "storeDescription": "更新后的店铺描述",
    "openTime": "2024-06-01T10:00:00",
    "status": "open",
    "creditScore": 98
  }
}
```

## 7. 删除店铺接口

### 正式接口
```
DELETE /api/merchant/store/{storeId}
```

### 请求示例
```
DELETE /api/merchant/store/1
```

### 响应示例
```json
{
  "success": true,
  "message": "操作成功",
  "data": "删除店铺成功"
}
```

## 测试步骤

### 1. 启动后端服务
```bash
cd back_end/parent-project
mvn spring-boot:run -pl merchant
```

### 2. 启动前端服务
```bash
cd front_end/ecommerce-frontend
npm run dev
```

### 3. 使用curl测试接口

#### 测试健康检查
```bash
curl -X GET http://localhost:8080/api/test/health
```

#### 测试Dashboard统计
```bash
curl -X GET http://localhost:8080/api/test/dashboard/1
```

#### 测试店铺列表
```bash
curl -X GET http://localhost:8080/api/test/stores/1
```

#### 测试创建店铺
```bash
curl -X POST http://localhost:8080/api/test/store/create \
  -H "Content-Type: application/json" \
  -d '{"merchantId":1,"storeName":"测试店铺","storeDescription":"测试描述"}'
```

### 4. 使用Postman测试
1. 导入API接口到Postman
2. 设置环境变量：`baseUrl = http://localhost:8080`
3. 依次测试各个接口

### 5. 前端集成测试
1. 访问 http://localhost:5173/merchant/dashboard
2. 登录商家账号
3. 查看Dashboard统计数据
4. 测试店铺管理功能

## 注意事项

1. **数据库连接**：确保MySQL数据库已启动并正确配置
2. **跨域设置**：所有接口已配置`@CrossOrigin(origins = "*")`
3. **错误处理**：所有接口都有统一的错误处理机制
4. **日志记录**：所有操作都会记录详细日志
5. **数据验证**：表单数据会进行严格验证

## 常见问题

### 1. 接口返回404
- 检查服务是否正常启动
- 确认请求路径是否正确

### 2. 数据库连接失败
- 检查数据库配置
- 确认数据库服务是否启动

### 3. 跨域问题
- 确认`@CrossOrigin`注解已添加
- 检查前端请求配置

### 4. 数据格式错误
- 确认请求头`Content-Type: application/json`
- 检查请求体JSON格式是否正确 