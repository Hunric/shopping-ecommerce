# 多阶段构建
# 第一阶段：构建应用
FROM node:18-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 第二阶段：运行应用
FROM node:18-alpine AS runtime

# 安装serve来提供静态文件服务
RUN npm install -g serve

# 设置工作目录
WORKDIR /app

# 从构建阶段复制构建结果
COPY --from=builder /app/dist ./dist

# 暴露端口
EXPOSE 3000

# 启动应用
CMD ["serve", "-s", "dist", "-l", "3000"] 