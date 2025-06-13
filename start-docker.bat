@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

echo ========================================
echo Starting E-commerce Platform Docker Services
echo ========================================
echo.

:: Step 1: Call port manager to clean ports (requires admin privileges)
echo Step 1: Cleaning ports...
if exist "admin-port-manager.bat" (
    echo Calling port manager script...
    call admin-port-manager.bat
    echo Port management completed, continuing with Docker startup...
    echo.
) else (
    echo [WARNING] admin-port-manager.bat not found, skipping port cleanup
    echo If you encounter port conflicts, please run admin-port-manager.bat manually
    echo.
)

:: Step 2: Start Docker services
echo Step 2: Starting Docker services...
echo Current working directory: %CD%

:: Check if Docker is running
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not running or not installed!
    echo Please start Docker Desktop first
    echo.
    pause
    exit /b 1
)

:: Check docker-compose file
echo Checking for docker-compose.yml file...
if exist "docker-compose.yml" (
    echo [OK] docker-compose.yml file found
) else (
    echo [ERROR] docker-compose.yml file not found!
    echo.
    echo Current directory: %CD%
    echo.
    echo Please ensure you are running this script in the correct project directory.
    echo The script should be placed in the same directory as docker-compose.yml
    echo.
    pause
    exit /b 1
)

:: Stop existing containers
echo Stopping existing containers...
docker-compose down --remove-orphans >nul 2>&1

:: Clean system and remove old images
echo Cleaning Docker system and removing old images...
docker system prune -f >nul 2>&1
docker rmi shopping-frontend:latest >nul 2>&1
docker rmi shopping-merchant-service:latest >nul 2>&1
docker rmi shopping-file-service:latest >nul 2>&1

:: Clean frontend build cache
echo Cleaning frontend build cache...
if exist "front_end\ecommerce-frontend\dist" (
    rmdir /s /q "front_end\ecommerce-frontend\dist" >nul 2>&1
)
if exist "front_end\ecommerce-frontend\node_modules\.vite" (
    rmdir /s /q "front_end\ecommerce-frontend\node_modules\.vite" >nul 2>&1
)

:: Start services with no cache
echo Building and starting all services (this may take several minutes)...
docker-compose build --no-cache
docker-compose up -d

if %errorlevel% neq 0 (
    echo [ERROR] Docker service startup failed!
    echo Please check if Docker Desktop is running properly
    echo.
    pause
    exit /b 1
)

echo [OK] Docker service startup command executed successfully
echo.

:: Step 3: Check Docker service status
echo Step 3: Checking service status...
echo Waiting for service initialization (30 seconds)...
timeout /t 30 /nobreak >nul

echo Current container status:
docker-compose ps
echo.

:: Step 4: Confirm all services are running normally
echo Step 4: Confirming service health status...

:: Check MySQL
echo Checking MySQL service...
set mysql_ready=0
for /l %%i in (1,1,5) do (
    docker-compose exec -T mysql mysqladmin ping -h localhost -u root -proot123456 >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] MySQL service is running
        set mysql_ready=1
        goto :mysql_done
    ) else (
        echo Waiting for MySQL to start... (attempt %%i/5)
        timeout /t 10 /nobreak >nul
    )
)
:mysql_done

:: Check Redis
echo Checking Redis service...
set redis_ready=0
for /l %%i in (1,1,3) do (
    docker-compose exec -T redis redis-cli ping >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] Redis service is running
        set redis_ready=1
        goto :redis_done
    ) else (
        echo Waiting for Redis to start... (attempt %%i/3)
        timeout /t 5 /nobreak >nul
    )
)
:redis_done

:: Check backend services
echo Checking merchant service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/merchant/actuator/health' -TimeoutSec 10 -UseBasicParsing; if ($response.StatusCode -eq 200) { Write-Host '[OK] Merchant service is running' } else { Write-Host '[WARNING] Merchant service response abnormal' } } catch { Write-Host '[WARNING] Merchant service not ready' }" 2>nul

echo Checking file service...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8082/actuator/health' -TimeoutSec 10 -UseBasicParsing; if ($response.StatusCode -eq 200) { Write-Host '[OK] File service is running' } else { Write-Host '[WARNING] File service response abnormal' } } catch { Write-Host '[WARNING] File service not ready' }" 2>nul

:: Check frontend service
echo Checking frontend service...
set frontend_ready=0
for /l %%i in (1,1,3) do (
    powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:3000' -TimeoutSec 10 -UseBasicParsing; if ($response.StatusCode -eq 200) { exit 0 } else { exit 1 } } catch { exit 1 }" >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] Frontend service is running
        set frontend_ready=1
        goto :frontend_done
    ) else (
        echo Waiting for frontend service to start... (attempt %%i/3)
        timeout /t 10 /nobreak >nul
    )
)
:frontend_done

echo.

:: Step 5: Open browser
if %frontend_ready% equ 1 (
    echo Step 5: Opening browser...
    echo ========================================
    echo All services started successfully!
    echo ========================================
    echo Frontend URL: http://localhost:3000
    echo Merchant API: http://localhost:8081/merchant
    echo File Service API: http://localhost:8082
    echo ========================================
    echo.
    
    echo Opening browser...
    timeout /t 15 /nobreak >nul
    start http://localhost:3000
    echo Browser opened successfully
) else (
    echo [WARNING] Frontend service not fully ready, please manually visit http://localhost:3000 later
)

echo.
echo Common management commands:
echo - View logs: docker-compose logs -f [service-name]
echo - Restart service: docker-compose restart [service-name]
echo - Stop services: docker-compose down
echo - Check status: docker-compose ps

echo.
echo Press any key to exit...
pause >nul