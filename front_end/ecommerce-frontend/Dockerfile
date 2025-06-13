# 多阶段构建 - 构建阶段
FROM swr.cn-north-4.myhuaweicloud.com/shopping-ecommerce/node:18-alpine AS build

# 设置工作目录
WORKDIR /app

# 设置npm镜像源为淘宝镜像
RUN npm config set registry https://registry.npmmirror.com

# 复制package文件
COPY package*.json ./

# 安装所有依赖（包括开发依赖）
RUN npm install

# 复制源代码
COPY . .

# 构建项目（跳过TypeScript检查）
RUN npx vite build

# 生产阶段 - Nginx
FROM swr.cn-north-4.myhuaweicloud.com/shopping-ecommerce/nginx:alpine

# 复制构建产物到nginx目录
COPY --from=build /app/dist /usr/share/nginx/html

# 复制nginx配置文件（如果存在）
# COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"] 