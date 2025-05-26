# 商家邮箱验证码登录功能

## 功能概述

本模块实现了商家通过邮箱验证码进行登录的功能，提供了安全可靠的身份认证机制。

## 技术栈

- **Spring Boot 3.4.5** - 主框架
- **Spring Security** - 安全框架
- **JWT (JJWT 0.12.3)** - 令牌认证
- **MyBatis** - 数据访问层
- **Redis** - 验证码存储
- **Spring Mail** - 邮件发送
- **MySQL** - 数据存储

## 核心功能

### 1. 邮箱验证码登录
- 商家输入邮箱地址
- 系统发送6位数字验证码到邮箱
- 商家使用邮箱+验证码登录
- 登录成功后返回JWT令牌

### 2. JWT令牌管理
- 生成访问令牌（24小时有效）
- 生成刷新令牌（7天有效）
- 令牌包含商家基本信息
- 支持令牌验证和解析

### 3. 安全控制
- 验证码5分钟有效期
- 验证码一次性使用
- 邮箱格式验证

## 项目结构

```
merchant/
├── src/main/java/com/hunric/
│   ├── controller/
│   │   └── MerchantController.java          # 控制器层
│   ├── service/
│   │   ├── MerchantService.java             # 服务接口
│   │   └── impl/
│   │       └── MerchantServiceImpl.java     # 服务实现
│   ├── model/
│   │   ├── Merchant.java                    # 商家实体
│   │   └── dto/
│   │       ├── MerchantSendCodeDTO.java     # 发送验证码DTO
│   │       ├── MerchantLoginDTO.java        # 登录请求DTO
│   │       └── MerchantLoginResponseDTO.java # 登录响应DTO
│   ├── mapper/
│   │   └── MerchantMapper.java              # 数据访问层
│   ├── config/
│   │   └── SecurityConfig.java              # 安全配置
│   └── MerchantApplication.java             # 启动类
├── src/main/resources/
│   └── application.yml                      # 配置文件
├── API_LOGIN.md                             # API文档
├── Merchant_Login_API.postman_collection.json # Postman测试集合
└── README_LOGIN.md                          # 本文档
```

## 核心类说明

### 1. JwtUtil (common模块)
JWT工具类，提供令牌的生成、验证、解析功能。

**主要方法：**
- `generateAccessToken()` - 生成访问令牌
- `generateRefreshToken()` - 生成刷新令牌
- `validateToken()` - 验证令牌有效性
- `getEmailFromToken()` - 从令牌获取邮箱
- `getMerchantIdFromToken()` - 从令牌获取商家ID

### 2. MerchantService
商家服务接口，定义登录相关的业务方法。

**主要方法：**
- `sendLoginVerificationCode()` - 发送登录验证码
- `loginWithVerificationCode()` - 验证码登录

### 3. MerchantController
商家控制器，提供登录相关的REST API。

**主要接口：**
- `POST /api/merchant/send-login-code` - 发送验证码
- `POST /api/merchant/login` - 验证码登录

### 4. SecurityConfig
Spring Security配置类，定义安全策略。

**配置内容：**
- 禁用CSRF保护
- 配置CORS跨域
- 设置无状态会话
- 定义公开接口权限

## API接口

### 发送登录验证码
```http
POST /api/merchant/send-login-code
Content-Type: application/json

{
    "email": "merchant@example.com"
}
```

### 验证码登录
```http
POST /api/merchant/login
Content-Type: application/json

{
    "email": "merchant@example.com",
    "verificationCode": "123456"
}
```

## 配置说明

### application.yml 关键配置
```yaml
# JWT配置
jwt:
  secret: hunric-shopping-ecommerce-merchant-jwt-secret-key-2024-very-long-secret
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天

# 邮件配置
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-email-password

# Redis配置
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

## 部署说明

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 2. 配置步骤
1. 修改 `application.yml` 中的数据库连接信息
2. 配置邮件服务器信息
3. 配置Redis连接信息
4. 确保商家表已创建且有测试数据

### 3. 启动服务
```bash
cd back_end/parent-project/merchant
mvn spring-boot:run
```

服务将在 `http://localhost:8081/merchant` 启动。

## 测试说明

### 1. 使用Postman测试
导入 `Merchant_Login_API.postman_collection.json` 文件到Postman，包含完整的测试用例。

### 2. 测试流程
1. 先注册一个测试商家账号
2. 发送登录验证码
3. 使用验证码登录
4. 使用返回的JWT令牌访问受保护的接口

### 3. 注意事项
- 确保邮件服务配置正确
- 验证码有5分钟有效期
- 由于数据库表中没有状态字段，所有已注册的商家都可以正常登录

## 安全特性

1. **验证码机制**：使用6位随机数字验证码，5分钟有效期
2. **一次性使用**：验证码使用后立即失效
3. **JWT令牌**：使用HS256算法签名，包含过期时间
4. **CORS配置**：支持跨域请求，便于前端集成
5. **无状态认证**：基于JWT的无状态认证机制

## 扩展功能

后续可以考虑添加以下功能：
1. 令牌刷新接口
2. 登录日志记录
3. 多设备登录管理
4. 登录频率限制
5. 短信验证码登录
6. 双因子认证

## 常见问题

### Q: 验证码收不到怎么办？
A: 检查邮件服务配置，确认邮箱地址正确，查看垃圾邮件文件夹。

### Q: 登录提示"商家不存在"？
A: 确认邮箱地址已注册。

### Q: JWT令牌如何使用？
A: 在请求头中添加：`Authorization: Bearer {accessToken}`

### Q: 如何刷新令牌？
A: 当前版本暂未实现令牌刷新功能，可使用refreshToken重新登录。 