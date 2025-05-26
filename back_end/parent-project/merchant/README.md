# 商家管理模块 (Merchant Module)

## 概述
商家管理模块提供商家注册、登录、店铺管理和Dashboard统计等功能。

## 主要功能

### 1. 商家管理
- 商家注册
- 验证码登录
- 获取商家信息

### 2. 店铺管理
- 创建店铺
- 查询店铺列表
- 更新店铺信息
- 删除店铺

### 3. Dashboard统计
- 店铺数量统计
- 商品数量统计
- 订单数量统计
- 收入统计
- 本月数据统计

## API接口

### 商家相关接口

#### 1. 商家注册
```
POST /api/merchant/register
```

#### 2. 发送登录验证码
```
POST /api/merchant/send-login-code
```

#### 3. 验证码登录
```
POST /api/merchant/login
```

#### 4. 获取商家信息
```
GET /api/merchant/info/{merchantId}
```

### 店铺相关接口

#### 1. 获取店铺列表
```
GET /api/merchant/store/list/{merchantId}
```

#### 2. 获取店铺详情
```
GET /api/merchant/store/{storeId}
```

#### 3. 创建店铺
```
POST /api/merchant/store/create
```

#### 4. 更新店铺
```
PUT /api/merchant/store/{storeId}
```

#### 5. 删除店铺
```
DELETE /api/merchant/store/{storeId}
```

### Dashboard统计接口

#### 1. 获取统计数据
```
GET /api/merchant/dashboard/stats/{merchantId}
```

## 数据库表

### 1. merchant_info - 商家信息表
- merchant_id: 商家ID
- merchant_name: 商家名称
- email: 邮箱
- password: 密码
- merchant_type: 商家类型
- business_license_no: 营业执照编号
- legal_person_name: 法人姓名
- contact_person_name: 联系人姓名
- 等等...

### 2. store_info - 店铺信息表
- store_id: 店铺ID
- merchant_id: 商家ID
- store_name: 店铺名称
- store_logo: 店铺Logo
- store_description: 店铺描述
- open_time: 开店时间
- status: 店铺状态
- credit_score: 信用分

## 技术栈
- Spring Boot 3.x
- MyBatis
- MySQL
- JWT认证
- 邮件服务

## 启动方式
1. 确保MySQL数据库已启动
2. 运行数据库迁移脚本
3. 启动应用：`mvn spring-boot:run`

## 测试接口
为了方便测试，提供了以下测试接口：
- `GET /api/test/health` - 健康检查
- `GET /api/test/dashboard/{merchantId}` - Dashboard统计数据
- `GET /api/test/stores/{merchantId}` - 店铺列表
- `POST /api/test/store/create` - 创建店铺

详细测试文档请参考：[API_TEST.md](./API_TEST.md)

## 前端集成
前端组件已实现：
- **StoreList.vue** - 店铺列表管理组件
  - 店铺搜索和筛选
  - 店铺状态管理
  - 店铺信息编辑
  - 店铺删除功能
- **StoreForm.vue** - 店铺创建表单组件
  - 基本信息填写
  - Logo预览
  - 经营类目选择
  - 表单验证

## 功能特性
### 后端功能
- ✅ 商家注册和登录
- ✅ 店铺CRUD操作
- ✅ Dashboard统计数据
- ✅ 统一异常处理
- ✅ 跨域支持
- ✅ JWT认证
- ✅ 数据验证

### 前端功能
- ✅ 响应式Dashboard界面
- ✅ 店铺管理功能
- ✅ 实时数据加载
- ✅ 表单验证
- ✅ 错误处理
- ✅ 用户友好的交互

## 注意事项
- 所有接口都支持跨域访问
- 需要配置邮件服务用于发送验证码
- JWT Token用于身份验证
- 密码使用BCrypt加密存储
- 前端组件支持TypeScript类型检查 