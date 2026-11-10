@echo off
echo ========================================================
echo   Digital Library Management System - Launch Script
echo ========================================================
echo.

REM Set Java if available in typical paths
where java >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" (
        set "PATH=C:\Program Files\Java\jdk-26.0.1\bin;%PATH%"
    )
)

echo [1/2] Compiling StandaloneServer.java...
javac StandaloneServer.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed. Please check your JDK installation.
    pause
    exit /b %errorlevel%
)

echo [2/2] Starting Digital Library Server on http://localhost:8080 ...
echo.
echo Admin Portal:    http://localhost:8080/admin.html
echo Member Dashboard: http://localhost:8080/user.html
echo Book Catalogue:  http://localhost:8080/index.html
echo.
echo Press Ctrl+C to stop the server.
echo.

java StandaloneServer
pause
