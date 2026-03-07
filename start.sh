#!/bin/bash

# Toolkit Hub 启动脚本

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
BACKEND_JAR="$BACKEND_DIR/target/toolkit-hub-1.0.0.jar"
BACKEND_LOG="$BACKEND_DIR/backend.log"

echo "======================================"
echo "Toolkit Hub - 项目启动"
echo "======================================"
echo ""

start_backend() {
    echo "📦 检查后端 JAR 文件..."
    if [ ! -f "$BACKEND_JAR" ]; then
        echo "⚠️  未找到 JAR 文件，正在编译后端..."
        cd "$BACKEND_DIR"
        mvn clean package -DskipTests
        if [ $? -ne 0 ]; then
            echo "❌ 后端编译失败！"
            exit 1
        fi
    fi

    echo "🚀 启动后端服务..."
    cd "$BACKEND_DIR"
    nohup java -jar "$BACKEND_JAR" > "$BACKEND_LOG" 2>&1 &
    BACKEND_PID=$!
    echo "✅ 后端已启动，进程ID: $BACKEND_PID"
    echo "📄 日志文件: $BACKEND_LOG"
    sleep 3

    # 检查后端是否启动成功
    if ps -p $BACKEND_PID > /dev/null; then
        echo "✅ 后端运行正常"
        echo "🌐 API地址: http://localhost:8080/api"
        echo "📖 接口文档: http://localhost:8080/doc.html"
    else
        echo "❌ 后端启动失败，请查看日志: $BACKEND_LOG"
        exit 1
    fi
}

start_frontend() {
    echo "🚀 启动前端服务..."
    cd "$FRONTEND_DIR"

    # 检查是否存在 node_modules
    if [ ! -d "node_modules" ]; then
        echo "📦 安装依赖..."
        npm install
    fi

    npm run dev
}

# 检查参数
if [ "$1" = "backend" ]; then
    start_backend
elif [ "$1" = "frontend" ]; then
    start_frontend
elif [ "$1" = "all" ]; then
    echo "🚀 同时启动前后端..."
    echo ""
    start_backend
    echo ""
    start_frontend
else
    echo "用法: ./start.sh [backend|frontend|all]"
    echo ""
    echo "选项:"
    echo "  backend   - 仅启动后端服务"
    echo "  frontend  - 仅启动前端服务"
    echo "  all       - 同时启动前后端"
    echo ""
    echo "示例:"
    echo "  ./start.sh backend"
    echo "  ./start.sh frontend"
    echo "  ./start.sh all"
fi
