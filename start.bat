@echo off
cd /d "%~dp0"
echo Starting application...
mvn cargo:run
pause
