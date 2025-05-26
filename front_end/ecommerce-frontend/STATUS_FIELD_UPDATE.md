# 商家状态字段移除更新

## 更新说明

根据后端数据库表结构，商家信息表中没有status字段，因此前端代码已进行相应调整。

## 修改内容

### 1. 认证状态管理 (`src/store/modules/auth.ts`)

**修改前：**
```typescript
interface MerchantInfo {
  merchantId: number
  merchantName: string
  email: string
  status: number  // ❌ 移除此字段
}

interface LoginResponse {
  // ...
  status: number  // ❌ 移除此字段
}
```

**修改后：**
```typescript
interface MerchantInfo {
  merchantId: number
  merchantName: string
  email: string
}

interface LoginResponse {
  // ...
  // 移除了status字段
}
```

### 2. Dashboard页面 (`src/views/merchant/dashboard/index.vue`)

**移除的内容：**
- 商家状态显示UI
- 状态徽章样式

**修改前：**
```vue
<div class="detail-item">
  <label>状态：</label>
  <span class="status-badge">正常</span>  <!-- ❌ 移除 -->
</div>
```

**修改后：**
```vue
<!-- 完全移除状态显示 -->
```

### 3. 文档更新

- 更新了 `MERCHANT_LOGIN_README.md` 中的接口定义
- 移除了状态相关的说明

## 影响范围

✅ **不受影响的功能：**
- 邮箱验证码发送
- 验证码登录
- JWT令牌管理
- 登录状态保持
- 退出登录

❌ **移除的功能：**
- 商家状态显示
- 基于状态的权限控制

## 数据结构对比

### 后端返回的登录响应
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
  "email": "merchant@example.com"
  // 注意：没有status字段
}
```

### 前端存储的商家信息
```typescript
{
  merchantId: 1,
  merchantName: "示例商家",
  email: "merchant@example.com"
  // 注意：没有status字段
}
```

## 兼容性说明

此更新确保前端代码与后端数据库表结构完全一致，避免了以下问题：
- 前端尝试访问不存在的字段
- TypeScript类型错误
- 运行时错误

## 后续开发建议

如果将来需要商家状态管理功能，建议：

1. **后端修改：**
   - 在数据库表中添加status字段
   - 更新相关的API响应

2. **前端修改：**
   - 恢复MerchantInfo和LoginResponse接口中的status字段
   - 在Dashboard页面重新添加状态显示
   - 根据状态实现相应的权限控制逻辑

## 测试验证

请确保以下功能正常工作：
- [x] 商家登录流程
- [x] Dashboard页面显示
- [x] 退出登录功能
- [x] 页面刷新后状态保持
- [x] JWT令牌自动管理 