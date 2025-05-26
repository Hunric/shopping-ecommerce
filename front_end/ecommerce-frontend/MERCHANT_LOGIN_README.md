# 商家登录功能前端实现

## 功能概述

本项目实现了商家邮箱验证码登录功能，包含以下特性：

- 邮箱验证码发送
- 验证码登录
- JWT令牌管理
- 自动登录状态保持
- 登录状态检查
- 退出登录

## 技术栈

- **Vue 3** - 前端框架
- **TypeScript** - 类型安全
- **Pinia** - 状态管理
- **Vue Router** - 路由管理
- **Element Plus** - UI组件库
- **Axios** - HTTP请求
- **Vite** - 构建工具

## 项目结构

```
src/
├── api/
│   └── merchant.ts              # 商家API接口
├── store/
│   └── modules/
│       └── auth.ts              # 认证状态管理
├── utils/
│   └── request.ts               # Axios请求拦截器
├── views/
│   └── merchant/
│       ├── login/
│       │   └── index.vue        # 登录页面
│       └── dashboard/
│           └── index.vue        # 商家后台首页
└── router/
    └── index.ts                 # 路由配置
```

## 核心功能

### 1. 认证状态管理 (auth.ts)

- 管理登录状态、用户信息、JWT令牌
- 提供登录、登出、状态检查等方法
- 自动保存到localStorage，支持页面刷新后状态恢复

### 2. API接口 (merchant.ts)

- `sendLoginCode(email)` - 发送登录验证码
- `login(email, verificationCode)` - 验证码登录

### 3. 请求拦截器 (request.ts)

- 自动添加JWT认证头
- 统一错误处理
- 401错误自动跳转登录页

### 4. 登录页面 (login/index.vue)

- 邮箱格式验证
- 验证码发送（60秒倒计时）
- 登录表单验证
- 加载状态管理
- 错误提示

### 5. Dashboard页面 (dashboard/index.vue)

- 显示商家信息
- 退出登录功能
- 登录状态检查

## 使用说明

### 1. 安装依赖

```bash
cd front_end/ecommerce-frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

项目将在 `http://localhost:3000` 启动。

### 3. 登录流程

1. 访问 `http://localhost:3000/merchant/login`
2. 输入商家邮箱地址
3. 点击"发送验证码"按钮
4. 查收邮件获取6位数字验证码
5. 输入验证码并点击"SIGN IN"
6. 登录成功后自动跳转到Dashboard页面

### 4. 环境配置

项目默认连接到 `http://localhost:8081/merchant` 后端服务。

如需修改API地址，可以：
- 修改 `src/utils/request.ts` 中的 `baseURL`
- 或者设置环境变量 `VITE_API_BASE_URL`

## API接口说明

### 发送验证码

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

## 状态管理

### 认证状态

```typescript
interface MerchantInfo {
  merchantId: number
  merchantName: string
  email: string
}

interface AuthStore {
  isLoggedIn: boolean
  merchantInfo: MerchantInfo | null
  accessToken: string
  refreshToken: string
}
```

### 主要方法

- `initializeAuth()` - 初始化认证状态
- `setAuthData(loginResponse)` - 设置登录数据
- `logout()` - 退出登录
- `isTokenExpired()` - 检查令牌是否过期

## 安全特性

1. **JWT令牌认证** - 使用JWT进行身份验证
2. **自动令牌管理** - 自动在请求头中添加认证令牌
3. **状态持久化** - 登录状态保存到localStorage
4. **路由守卫** - 未登录自动跳转到登录页
5. **错误处理** - 统一的错误提示和处理

## 注意事项

1. 确保后端服务已启动并运行在 `http://localhost:8081/merchant`
2. 验证码有效期为5分钟
3. 验证码发送有60秒倒计时限制
4. JWT令牌过期后会自动跳转到登录页
5. Dashboard页面为临时页面，具体功能将在后续开发

## 开发说明

### 添加新的API接口

1. 在 `src/api/merchant.ts` 中添加新的API方法
2. 使用 `request` 实例发送请求，会自动添加认证头

### 添加新的页面

1. 在 `src/views/merchant/` 下创建新的页面组件
2. 在 `src/router/index.ts` 中添加路由配置
3. 如需认证，在页面的 `onMounted` 中检查登录状态

### 状态管理

使用Pinia进行状态管理，可以在任何组件中通过 `useAuthStore()` 访问认证状态。

## 构建部署

```bash
# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

构建产物将生成在 `dist/` 目录中。 