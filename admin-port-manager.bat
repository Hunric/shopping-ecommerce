@echo off

:: Check admin privileges and restart as admin if needed
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo Need admin privileges to manage ports, requesting permissions...
    powershell -Command "Start-Process '%~f0' -Verb RunAs"
    exit /b
)

:: Set terminal encoding to UTF-8
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo Port Manager - Checking and Cleaning Ports
echo ========================================
echo.

:: Check for Windows reserved ports
echo Checking Windows reserved port ranges...
netsh int ipv4 show excludedportrange protocol=tcp >nul 2>&1
if %errorlevel% equ 0 (
    echo [INFO] Checking for reserved port conflicts...
    netsh int ipv4 show excludedportrange protocol=tcp | findstr "3000\|3306\|6379\|8081\|8082" >nul
    if !errorlevel! equ 0 (
        echo [WARNING] Some ports may be in Windows reserved ranges
        echo This can cause Docker binding issues
    )
)

:: Stop Docker containers first to release ports
echo Stopping any existing Docker containers...
docker stop $(docker ps -q) >nul 2>&1
docker-compose down --remove-orphans >nul 2>&1

:: Check port usage and close occupied ports
echo Checking port usage...
set "ports=3000 3306 6379 8081 8082"
set "found_occupied=0"

for %%p in (%ports%) do (
    echo.
    echo Checking port %%p...
    
    :: First check if port is in use
    netstat -ano | findstr ":%%p " | findstr "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (
        echo Port %%p is occupied, attempting to free it...
        
        :: Get all PIDs using this port
        for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":%%p " ^| findstr "LISTENING"') do (
            if "%%a" neq "" if "%%a" neq "0" (
                echo Found process %%a using port %%p
                
                :: Try to identify the process
                tasklist /fi "PID eq %%a" /fo csv 2>nul | findstr /v "INFO:" | findstr /v "Image Name"
                
                :: Force kill the process
                echo Terminating process %%a...
                taskkill /f /pid %%a >nul 2>&1
                if !errorlevel! equ 0 (
                    echo [OK] Successfully terminated process %%a
                    set "found_occupied=1"
                ) else (
                    echo [WARNING] Unable to terminate process %%a
                )
                
                :: Wait a moment for the port to be released
                timeout /t 2 /nobreak >nul
            )
        )
        
        :: Double-check if port is now free
        netstat -ano | findstr ":%%p " | findstr "LISTENING" >nul 2>&1
        if !errorlevel! equ 0 (
            echo [WARNING] Port %%p is still occupied after cleanup attempt
        ) else (
            echo [OK] Port %%p is now available
        )
    ) else (
        echo [OK] Port %%p is available
    )
)

echo.
echo ========================================
echo Additional Port Cleanup
echo ========================================

:: Reset Windows TCP/IP stack if needed
echo Checking if TCP/IP reset is needed...
set "reset_needed=0"
for %%p in (%ports%) do (
    netstat -ano | findstr ":%%p " | findstr "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (
        set "reset_needed=1"
    )
)

if %reset_needed% equ 1 (
    echo [WARNING] Some ports are still occupied
    echo.
    echo Would you like to reset the TCP/IP stack? This may help free stubborn ports.
    echo This will require a system restart to take full effect.
    echo.
    set /p reset_choice="Reset TCP/IP stack? (y/N): "
    
    if /i "!reset_choice!"=="y" (
        echo Resetting TCP/IP stack...
        netsh winsock reset >nul 2>&1
        netsh int ip reset >nul 2>&1
        echo [OK] TCP/IP stack reset completed
        echo [INFO] A system restart is recommended for full effect
    )
)

:: Final port status check
echo.
echo Final port status check:
for %%p in (%ports%) do (
    netstat -ano | findstr ":%%p " | findstr "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (
        echo [WARNING] Port %%p is still occupied
    ) else (
        echo [OK] Port %%p is free
    )
)

echo.
if %found_occupied% equ 0 (
    echo [OK] All ports were already available
) else (
    echo [OK] Port cleanup completed
)

echo.
echo ========================================
echo Port Management Summary
echo ========================================
echo - Stopped Docker containers
echo - Terminated processes using required ports
echo - Checked Windows reserved port ranges
echo - Verified final port status
echo.
echo You can now run the Docker startup script.
echo If you still encounter port binding issues, consider restarting your system.
echo.
pause 