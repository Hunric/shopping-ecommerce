# 商家邮箱验证码登录API文档

## 概述
商家登录采用邮箱验证码的方式，确保安全性。登录流程分为两步：
1. 发送验证码到商家邮箱
2. 使用邮箱和验证码进行登录

## API接口

### 1. 发送登录验证码

**接口地址：** `POST /api/merchant/send-login-code`

**请求参数：**
```json
{
    "email": "merchant@example.com"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "验证码发送成功，请查收邮件",
    "data": null,
    "success": true
}
```

**错误响应示例：**
```json
{
    "code": 500,
    "message": "商家不存在，请先注册",
    "data": null,
    "success": false
}
```

### 2. 验证码登录

**接口地址：** `POST /api/merchant/login`

**请求参数：**
```json
{
    "email": "merchant@example.com",
    "verificationCode": "123456"
}
```

**成功响应示例：**
```json
{
    "success": true,
    "message": "登录成功",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "merchantId": 1,
    "merchantName": "示例商家",
    "email": "merchant@example.com",
    "status": 1
}
```

**错误响应示例：**
```json
{
    "success": false,
    "message": "验证码错误或已过期",
    "accessToken": null,
    "refreshToken": null,
    "tokenType": null,
    "expiresIn": null,
    "merchantId": null,
    "merchantName": null,
    "email": null,
    "status": null
}
```

## 使用流程

1. **发送验证码**
   - 调用 `/api/merchant/send-login-code` 接口
   - 系统会验证商家是否存在
   - 验证码有效期为5分钟

2. **验证码登录**
   - 调用 `/api/merchant/login` 接口
   - 系统验证验证码的有效性
   - 登录成功后返回JWT令牌

3. **使用JWT令牌**
   - 在后续请求的Header中添加：`Authorization: Bearer {accessToken}`
   - accessToken有效期为24小时
   - refreshToken有效期为7天

## 错误码说明

| 错误信息 | 说明 |
|---------|------|
| 邮箱不能为空 | 请求参数中邮箱字段为空 |
| 邮箱格式不正确 | 邮箱格式不符合规范 |
| 商家不存在，请先注册 | 该邮箱未注册商家账号 |
| 验证码不能为空 | 登录时验证码字段为空 |
| 验证码错误或已过期 | 验证码不正确或已超过有效期 |

## 安全特性

1. **验证码有效期**：验证码有效期为5分钟，过期后需重新获取
2. **验证码一次性**：验证码使用后立即失效，不可重复使用
3. **JWT令牌**：使用JWT进行身份认证，支持无状态认证
4. **邮箱验证**：确保只有邮箱所有者才能登录

## 注意事项

1. 验证码发送有频率限制，请勿频繁请求
2. JWT令牌请妥善保管，避免泄露
3. 建议在令牌即将过期时使用refreshToken刷新
4. 所有接口都支持CORS跨域请求
5. 由于数据库表中没有状态字段，所有已注册的商家都可以正常登录 