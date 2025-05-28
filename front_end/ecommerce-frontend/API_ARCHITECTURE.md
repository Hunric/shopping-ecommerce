# API架构说明

## 概述

本项目采用多服务架构设计，支持未来向微服务架构演进。通过统一的配置管理和智能的请求路由，实现了前端对多个后端服务的无缝访问。

## 架构特点

### 1. 多服务支持
- **商家服务** (Merchant Service): 端口 8081，context-path: `/merchant`
- **文件服务** (File Service): 端口 8082
- **用户服务** (User Service): 端口 8083 (预留)

### 2. 智能路由
前端请求工具会根据API路径自动选择正确的服务：
- `/api/merchant/*` → 商家服务
- `/api/upload/*` → 文件服务  
- `/api/user/*` → 用户服务
- 其他 `/api/*` → 默认服务

### 3. 环境配置
通过环境变量灵活配置各服务地址：

**开发环境** (`.env.development`):
```
VITE_API_BASE_URL=http://localhost:8081
VITE_MERCHANT_API_BASE_URL=http://localhost:8081/merchant
VITE_FILE_API_BASE_URL=http://localhost:8082
VITE_USER_API_BASE_URL=http://localhost:8083
```

**生产环境** (`.env.production`):
```
VITE_API_BASE_URL=https://api.yourdomain.com
VITE_MERCHANT_API_BASE_URL=https://merchant-api.yourdomain.com
VITE_FILE_API_BASE_URL=https://file-api.yourdomain.com
VITE_USER_API_BASE_URL=https://user-api.yourdomain.com
```

## 核心文件

### 1. API配置 (`src/config/api.ts`)
- 统一管理所有服务的基础URL
- 定义API路径常量
- 提供工具函数构建完整URL

### 2. 请求工具 (`src/utils/request.ts`)
- 智能识别服务类型
- 自动设置正确的baseURL
- 统一的错误处理和日志记录
- 自动添加认证token

### 3. Vite代理配置 (`vite.config.ts`)
- 开发环境代理设置
- 支持跨域请求
- 可根据需要配置特定服务代理

## 使用方式

### 1. 基本API调用
```typescript
import request from '@/utils/request'

// 自动路由到商家服务
const response = await request({
  url: '/api/merchant/spu',
  method: 'get'
})

// 自动路由到文件服务
const uploadResponse = await request({
  url: '/api/upload/image',
  method: 'post',
  data: formData
})
```

### 2. 使用API常量
```typescript
import { MERCHANT_API } from '@/config/api'
import request from '@/utils/request'

// 使用预定义的API路径
const response = await request({
  url: MERCHANT_API.SPU_LIST,
  method: 'get'
})
```

## 优势

### 1. 解决重复配置问题
- 前端不需要重复配置 `/merchant` 路径
- 后端可以保持 context-path 设置
- 配置更加清晰和灵活

### 2. 支持微服务演进
- 可以轻松添加新服务
- 服务地址变更只需修改环境变量
- 支持服务拆分和独立部署

### 3. 开发体验优化
- 统一的错误处理
- 详细的请求日志
- 自动的认证处理

### 4. 生产环境友好
- 支持不同环境配置
- 可以使用网关或负载均衡
- 支持域名和端口的灵活配置

## 扩展指南

### 添加新服务
1. 在 `.env.development` 和 `.env.production` 中添加服务URL
2. 在 `src/config/api.ts` 中添加服务配置
3. 在 `src/utils/request.ts` 中添加路由规则
4. 定义新服务的API路径常量

### 修改服务地址
只需修改对应环境的配置文件，无需修改代码。

### 使用API网关
在生产环境中，可以将所有服务配置为同一个网关地址：
```
VITE_API_BASE_URL=https://gateway.yourdomain.com
VITE_MERCHANT_API_BASE_URL=https://gateway.yourdomain.com
VITE_FILE_API_BASE_URL=https://gateway.yourdomain.com
VITE_USER_API_BASE_URL=https://gateway.yourdomain.com
```

这样既保持了代码的灵活性，又支持了统一网关的架构。 