@echo off
REM Автоматический запуск docker-compose и сборка UserService для Windows

REM Проверка наличия docker
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo Docker не найден. Пожалуйста, установите Docker Desktop и убедитесь, что он запущен.
    exit /b 1
)

REM Проверка наличия docker-compose
where docker-compose >nul 2>nul
if %errorlevel% neq 0 (
    echo Docker Compose не найден. Пожалуйста, установите Docker Desktop (он включает docker-compose).
    exit /b 1
)

cd /d %~dp0\docker-file
call docker-compose up -d
cd /d %~dp0

REM Сборка и запуск Java-сервиса
call mvnw.cmd clean package -DskipTests
REM Здесь можно добавить запуск jar-файла, если требуется
REM java -jar target\UserService-0.0.1.jar

echo UserService успешно запущен!
pause
