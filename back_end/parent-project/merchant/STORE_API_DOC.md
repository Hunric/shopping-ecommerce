# 店铺管理API文档

## 概述
店铺管理模块提供完整的店铺CRUD操作，包括创建、查询、更新、删除店铺，以及店铺统计功能。

## API接口列表

### 1. 获取商家店铺列表
**接口地址：** `GET /api/merchant/store/list/{merchantId}`

**请求参数：**
- `merchantId` (路径参数): 商家ID

**响应示例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "storeId": 1,
      "merchantId": 1,
      "storeName": "时尚服装店",
      "storeLogo": "http://localhost:8082/uploads/store-logo/2024/01/15/uuid-logo.jpg",
      "storeDescription": "专营时尚女装，品质保证",
      "openTime": "2024-01-15T10:00:00",
      "status": "open",
      "creditScore": 98,
      "createTime": "2024-01-15T10:00:00",
      "updateTime": "2024-01-15T10:00:00"
    }
  ]
}
```

### 2. 获取店铺详情
**接口地址：** `GET /api/merchant/store/{storeId}`

**请求参数：**
- `storeId` (路径参数): 店铺ID

**响应示例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "storeId": 1,
    "merchantId": 1,
    "storeName": "时尚服装店",
    "storeLogo": "http://localhost:8082/uploads/store-logo/2024/01/15/uuid-logo.jpg",
    "storeDescription": "专营时尚女装，品质保证",
    "openTime": "2024-01-15T10:00:00",
    "status": "open",
    "creditScore": 98,
    "createTime": "2024-01-15T10:00:00",
    "updateTime": "2024-01-15T10:00:00"
  }
}
```

### 3. 创建店铺
**接口地址：** `POST /api/merchant/store/create`

**请求体：**
```json
{
  "merchantId": 1,
  "storeName": "新店铺",
  "storeLogo": "http://localhost:8082/uploads/store-logo/2024/01/15/uuid-logo.jpg",
  "storeDescription": "这是一个新创建的店铺"
}
```

**响应示例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "storeId": 2,
    "merchantId": 1,
    "storeName": "新店铺",
    "storeLogo": "http://localhost:8082/uploads/store-logo/2024/01/15/uuid-logo.jpg",
    "storeDescription": "这是一个新创建的店铺",
    "openTime": "2024-01-15T14:30:00",
    "status": "open",
    "creditScore": 100,
    "createTime": "2024-01-15T14:30:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

### 4. 更新店铺信息
**接口地址：** `PUT /api/merchant/store/{storeId}`

**请求参数：**
- `storeId` (路径参数): 店铺ID

**请求体：**
```json
{
  "storeName": "更新后的店铺名称",
  "storeLogo": "http://localhost:8082/uploads/store-logo/2024/01/15/new-logo.jpg",
  "storeDescription": "更新后的店铺描述",
  "status": "open"
}
```

**响应示例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "storeId": 1,
    "merchantId": 1,
    "storeName": "更新后的店铺名称",
    "storeLogo": "http://localhost:8082/uploads/store-logo/2024/01/15/new-logo.jpg",
    "storeDescription": "更新后的店铺描述",
    "openTime": "2024-01-15T10:00:00",
    "status": "open",
    "creditScore": 98,
    "createTime": "2024-01-15T10:00:00",
    "updateTime": "2024-01-15T14:35:00"
  }
}
```

### 5. 删除店铺
**接口地址：** `DELETE /api/merchant/store/{storeId}`

**请求参数：**
- `storeId` (路径参数): 店铺ID

**响应示例：**
```json
{
  "success": true,
  "message": "删除店铺成功",
  "data": "删除店铺成功"
}
```

### 6. 获取商家店铺统计
**接口地址：** `GET /api/merchant/store/count/{merchantId}`

**请求参数：**
- `merchantId` (路径参数): 商家ID

**响应示例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": 3
}
```

## 数据字段说明

### StoreDTO字段说明
| 字段名 | 类型 | 说明 |
|--------|------|------|
| storeId | Integer | 店铺ID |
| merchantId | Integer | 商家ID |
| storeName | String | 店铺名称（最大50字符） |
| storeLogo | String | 店铺Logo URL |
| storeDescription | String | 店铺描述 |
| openTime | LocalDateTime | 开店时间 |
| status | String | 店铺状态：open-营业中, closed-已关闭, suspended-已暂停 |
| creditScore | Integer | 店铺信用分（默认100） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

## 业务规则

### 创建店铺
1. 商家ID不能为空且必须大于0
2. 店铺名称不能为空，最大长度50字符
3. 同一商家下店铺名称不能重复
4. 新创建的店铺默认状态为"open"，信用分为100

### 更新店铺
1. 店铺ID不能为空且必须大于0
2. 店铺必须存在
3. 如果更新店铺名称，不能与同一商家下其他店铺重复
4. 状态只能是：open、closed、suspended

### 删除店铺
1. 店铺ID不能为空且必须大于0
2. 店铺必须存在
3. 可以扩展业务规则：检查是否有未完成订单、在售商品等

## 错误码说明

| 错误信息 | 说明 |
|----------|------|
| 商家ID不能为空 | merchantId参数无效 |
| 店铺ID不能为空 | storeId参数无效 |
| 店铺名称不能为空 | storeName为空或null |
| 店铺名称不能超过50个字符 | storeName长度超限 |
| 店铺名称已存在，请使用其他名称 | 同一商家下店铺名称重复 |
| 店铺不存在 | 指定的店铺ID不存在 |
| 无效的店铺状态 | status不在允许的值范围内 |

## 测试用例

### 创建店铺测试
```bash
curl -X POST http://localhost:8081/merchant/api/merchant/store/create \
  -H "Content-Type: application/json" \
  -d '{
    "merchantId": 1,
    "storeName": "测试店铺",
    "storeLogo": "http://localhost:8082/uploads/store-logo/test.jpg",
    "storeDescription": "这是一个测试店铺"
  }'
```

### 获取店铺列表测试
```bash
curl -X GET http://localhost:8081/merchant/api/merchant/store/list/1
```

### 更新店铺测试
```bash
curl -X PUT http://localhost:8081/merchant/api/merchant/store/1 \
  -H "Content-Type: application/json" \
  -d '{
    "storeName": "更新后的店铺",
    "status": "open"
  }'
```

### 删除店铺测试
```bash
curl -X DELETE http://localhost:8081/merchant/api/merchant/store/1
``` 