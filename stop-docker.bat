@echo off
chcp 65001 >nul
echo ========================================
echo    购物电商系统 Docker 停止脚本
echo ========================================

echo.
echo 检查Docker服务状态...
docker version >nul 2>&1
if errorlevel 1 (
    echo 错误: Docker服务未运行
    pause
    exit /b 1
)
echo ✓ Docker服务正常运行

echo.
echo 停止所有服务...
docker-compose down

echo.
echo 检查是否需要清理数据...
set /p cleanup=是否删除所有数据（包括数据库数据）？(y/N): 
if /i "%cleanup%"=="y" (
    echo 警告: 即将删除所有数据，包括数据库数据！
    set /p confirm=确认删除？(y/N): 
    if /i "%confirm%"=="y" (
        echo 删除数据卷...
        docker-compose down -v
        echo ✓ 数据已清理
    ) else (
        echo 取消数据清理
    )
) else (
    echo 保留数据
)

echo.
echo 检查是否需要清理镜像...
set /p cleanup_images=是否清理未使用的镜像？(y/N): 
if /i "%cleanup_images%"=="y" (
    echo 清理未使用的镜像...
    docker image prune -f
    echo ✓ 镜像清理完成
)

echo.
echo 显示当前容器状态...
docker-compose ps

echo.
echo ========================================
echo           服务已停止！
echo ========================================
echo.
echo 如需重新启动，请运行: start-docker.bat
echo.
pause 