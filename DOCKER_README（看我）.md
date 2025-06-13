# 购物电商系统 Docker 部署指南

## 📋 概述

本项目使用 Docker 和 Docker Compose 进行容器化部署，支持一键启动完整的电商系统环境。

## 🏗️ 架构说明

### 服务组件
- **MySQL 8.0**: 数据库服务（华为云镜像源）
- **Redis 5.0.6**: 缓存服务（华为云镜像源）
- **商户服务**: Spring Boot 后端服务（使用您的JDK17镜像）
- **文件服务**: 文件上传下载服务（使用您的JDK17镜像）
- **前端服务**: Nginx + 前端应用

### 镜像源配置
- **MySQL**: `swr.cn-north-4.myhuaweicloud.com/library/mysql:8.0`
- **Redis**: `swr.cn-north-4.myhuaweicloud.com/library/redis:latest` (Redis 5.0.6)
- **JDK17**: `swr.cn-north-4.myhuaweicloud.com/shopping-ecommerce/openjdk:17-jdk`

## 🚀 快速开始

### 前置要求
- Docker Desktop 已安装并运行
- Docker Compose 已安装
- 至少 4GB 可用内存
- 至少 10GB 可用磁盘空间

### 一键启动
```bash
# Windows
start-docker.bat

# Linux/Mac
./start-docker.sh
```

### 一键停止
```bash
# Windows
stop-docker.bat

# Linux/Mac
./stop-docker.sh
```

## 📁 目录结构

```
shopping-ecommerce/
├── docker-compose.yml          # 主配置文件
├── start-docker.bat           # 启动脚本
├── stop-docker.bat            # 停止脚本
├── docker/                    # Docker配置目录
│   ├── Dockerfile.backend     # 后端服务Dockerfile模板
│   ├── mysql/                 # MySQL配置
│   │   ├── conf/my.cnf       # MySQL配置文件
│   │   └── init/01-init.sql  # 数据库初始化脚本
│   ├── redis/                 # Redis配置
│   │   └── redis.conf        # Redis配置文件
│   └── nginx/                 # Nginx配置
├── logs/                      # 日志目录
│   ├── mysql/
│   ├── nginx/
│   ├── merchant-service/
│   └── file-service/
└── uploads/                   # 文件上传目录
```

## 🔧 配置说明

### 数据库配置
- **数据库名**: `shopping_ecommerce`
- **用户名**: `shopping`
- **密码**: `shopping123`
- **端口**: `3306`

### Redis配置
- **版本**: `5.0.6` (华为云SWR镜像)
- **端口**: `6379`
- **最大内存**: `512MB`
- **持久化**: 启用AOF和RDB
- **兼容性**: 完全支持项目中的Redis操作

### 服务端口
- **前端**: `80` (HTTP), `443` (HTTPS)
- **商户服务**: `8081`
- **文件服务**: `8082`
- **MySQL**: `3306`
- **Redis**: `6379`

## 🛠️ 常用命令

### 查看服务状态
```bash
docker-compose ps
```

### 查看服务日志
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs mysql
docker-compose logs redis
docker-compose logs merchant-service
docker-compose logs file-service
docker-compose logs frontend

# 实时查看日志
docker-compose logs -f merchant-service
```

### 重启服务
```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart mysql
docker-compose restart merchant-service
```

### 进入容器
```bash
# 进入MySQL容器
docker-compose exec mysql bash

# 进入Redis容器
docker-compose exec redis sh

# 进入应用容器
docker-compose exec merchant-service bash
```

### 数据库操作
```bash
# 连接MySQL
docker-compose exec mysql mysql -u shopping -p shopping_ecommerce

# 备份数据库
docker-compose exec mysql mysqldump -u shopping -p shopping_ecommerce > backup.sql

# 恢复数据库
docker-compose exec -T mysql mysql -u shopping -p shopping_ecommerce < backup.sql
```

### Redis操作
```bash
# 连接Redis
docker-compose exec redis redis-cli

# 查看Redis信息
docker-compose exec redis redis-cli info

# 查看Redis配置
docker-compose exec redis redis-cli config get "*"
```

## 🔍 故障排除

### 常见问题

#### 1. 端口冲突
如果遇到端口冲突，请检查以下端口是否被占用：
- 80, 443 (前端)
- 8081, 8082 (后端服务)
- 3306 (MySQL)
- 6379 (Redis)

解决方案：修改 `docker-compose.yml` 中的端口映射。

#### 2. 内存不足
如果系统内存不足，可以调整以下配置：
- MySQL: 减少 `innodb_buffer_pool_size`
- Redis: 减少 `maxmemory`

#### 3. 镜像拉取失败
如果华为云镜像拉取失败，可以：
1. 检查网络连接
2. 使用Docker Hub镜像作为备选
3. 配置镜像加速器

#### 4. 服务启动失败
检查步骤：
1. 查看服务日志：`docker-compose logs [服务名]`
2. 检查配置文件语法
3. 确认依赖服务已启动
4. 检查磁盘空间

#### 5. Redis版本兼容性
项目使用Redis 5.0.6（华为云SWR镜像），完全兼容项目需求：
- 支持所有使用的Redis命令
- 支持TTL过期时间
- 支持String数据类型操作
- 与Spring Boot 3.4.5完全兼容

### 健康检查
所有服务都配置了健康检查，可以通过以下命令查看：
```bash
docker-compose ps
```

### 性能监控
```bash
# 查看资源使用情况
docker stats

# 查看特定容器资源使用
docker stats shopping-mysql shopping-redis
```

## 🔒 安全配置

### 生产环境建议
1. **修改默认密码**：
   - MySQL root密码
   - Redis密码（取消注释并设置）
   - 应用数据库密码

2. **网络安全**：
   - 使用防火墙限制端口访问
   - 配置SSL证书
   - 启用HTTPS

3. **数据备份**：
   - 定期备份MySQL数据
   - 备份Redis数据（如需要）
   - 备份上传文件
   - 备份配置文件

## 📊 监控和日志

### 日志管理
- 所有服务日志都存储在 `logs/` 目录下
- 支持日志轮转，避免磁盘空间不足
- 可以集成ELK或其他日志分析系统

### 监控指标
- 容器资源使用率
- 服务响应时间
- 数据库连接数
- Redis内存使用率

## 🚀 扩展和优化

### 水平扩展
```bash
# 扩展后端服务实例
docker-compose up -d --scale merchant-service=3
docker-compose up -d --scale file-service=2
```

### 性能优化
1. **数据库优化**：
   - 调整MySQL配置参数
   - 添加适当的索引
   - 启用查询缓存

2. **缓存优化**：
   - 调整Redis内存分配
   - 配置合适的淘汰策略
   - 监控Redis性能

3. **应用优化**：
   - 调整JVM参数
   - 启用应用缓存
   - 优化数据库连接池

## 📞 技术支持

如遇到问题，请：
1. 查看本文档的故障排除部分
2. 检查服务日志
3. 提交Issue并附上详细的错误信息和环境信息

---

**注意**: 
- 本项目使用华为云SWR镜像源，国内访问速度更快
- Redis版本为5.0.6，与项目完全兼容
- 本文档会随着项目更新而持续维护，请定期查看最新版本 