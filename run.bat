@echo off
cd /d "%~dp0"
echo ================================================
echo Starting Spring Shop Application
echo ================================================
echo.
echo Current directory: %CD%
echo.
echo Building project...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! Press any key to exit...
    pause
    exit /b 1
)
echo.
echo Starting Tomcat server on http://localhost:8080
echo Press Ctrl+C to stop
echo.
call mvn cargo:run
