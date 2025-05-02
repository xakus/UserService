#!/bin/bash

# Проверка и установка docker и docker-compose, запуск docker-compose.yml
if ! command -v docker &> /dev/null; then
    echo "Docker не установлен. Устанавливаю..."
    apt-get update && apt-get install -y docker.io
    systemctl start docker
    systemctl enable docker
fi
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose не установлен. Устанавливаю..."
    apt-get update && apt-get install -y docker-compose
fi
# Запуск docker-compose
cd "$(dirname "$0")/docker-file" && docker-compose up -d
cd "$(dirname "$0")"

PROJECT_NAME=UserService

# Определить имя сервиса
SERVICE_NAME=${PROJECT_NAME}
systemctl stop ${SERVICE_NAME}
systemctl disable ${SERVICE_NAME}

# Определить директорию проекта
PROJECT_DIR=$(dirname "$(realpath "$0")")

# Перейти в директорию проекта
# shellcheck disable=SC2164
cd "${PROJECT_DIR}"
# Собрать проект с помощью Maven
mvn clean package -DskipTests

# Определить путь к файлу JAR
JAR_FILE=target/${PROJECT_NAME}-0.0.1.jar

# Определить имя сервиса
SERVICE_NAME=${PROJECT_NAME}
systemctl stop ${SERVICE_NAME}
rm -R /etc/systemd/system/${SERVICE_NAME}.service
# Определить путь к файлу конфигурации
#SERVICE_CONFIG=${PROJECT_DIR}/myapp.conf

# Создать службу systemd для сервиса
echo "[Unit]
Description=My Java Spring Boot App
After=syslog.target

[Service]
User=root
WorkingDirectory=${PROJECT_DIR}
ExecStart=java -Xms1g -Xmx2g -XX:+UseG1GC -XX:InitiatingHeapOccupancyPercent=30 -XX:NewRatio=2 -verbose:gc -Xlog:gc*:file=gc.log:time,uptime,level,tags  -jar ${JAR_FILE}
SuccessExitStatus=143
Restart=always

[Install]
WantedBy=multi-user.target" > /etc/systemd/system/${SERVICE_NAME}.service

# Обновить службу systemd и запустить сервис
systemctl daemon-reload
systemctl start ${SERVICE_NAME}
systemctl enable ${SERVICE_NAME}