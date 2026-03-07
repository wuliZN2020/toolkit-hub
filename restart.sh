#!/bin/bash

# Toolkit Hub 重启脚本 - 修改代码后使用此脚本

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_DIR/backend"
FRONTEND_DIR="$PROJECT_DIR/frontend"
BACKEND_JAR="$BACKEND_DIR/target/toolkit-hub-1.0.0.jar"
BACKEND_LOG="$BACKEND_DIR/backend.log"

echo "======================================"
echo "Toolkit Hub - 重启服务"
echo "======================================"
echo ""

restart_backend() {
    echo "🔄 重启后端服务..."
    echo ""

    # 停止后端
    echo "1️⃣ 停止后端服务..."
    BACKEND_PIDS=$(ps aux | grep -i "java.*toolkit-hub" | grep -v grep | awk '{print $2}')
    if [ ! -z "$BACKEND_PIDS" ]; then
        for PID in $BACKEND_PIDS; do
            echo "   🔪 终止进程: $PID"
            kill $PID
        done
        sleep 2
    else
        echo "   ℹ️  后端服务未运行"
    fi

    # 编译后端
    echo ""
    echo "2️⃣ 编译后端代码..."
    cd "$BACKEND_DIR"
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "   ❌ 后端编译失败！"
        exit 1
    fi
    echo "   ✅ 后端编译成功"

    # 启动后端
    echo ""
    echo "3️⃣ 启动后端服务..."
    nohup java -jar "$BACKEND_JAR" > "$BACKEND_LOG" 2>&1 &
    BACKEND_PID=$!
    echo "   🚀 后端启动中，进程ID: $BACKEND_PID"
    sleep 5

    # 检查后端是否启动成功
    if ps -p $BACKEND_PID > /dev/null; then
        echo "   ✅ 后端运行正常"
        echo "   🌐 API地址: http://localhost:8080/api"
        echo "   📖 接口文档: http://localhost:8080/doc.html"
        echo "   📄 日志文件: $BACKEND_LOG"
    else
        echo "   ❌ 后端启动失败，请查看日志: $BACKEND_LOG"
        tail -30 "$BACKEND_LOG"
        exit 1
    fi
}

restart_frontend() {
    echo "🔄 重启前端服务..."
    echo ""

    # 停止前端
    echo "1️⃣ 停止前端服务..."
    FRONTEND_PIDS=$(ps aux | grep "node.*vite" | grep -v grep | awk '{print $2}')
    if [ ! -z "$FRONTEND_PIDS" ]; then
        for PID in $FRONTEND_PIDS; do
            echo "   🔪 终止进程: $PID"
            kill $PID
        done
        echo "   ✅ 前端服务已停止"
    else
        echo "   ℹ️  前端服务未运行"
    fi

    # 启动前端
    echo ""
    echo "2️⃣ 启动前端服务..."
    cd "$FRONTEND_DIR"

    # 检查是否存在 node_modules
    if [ ! -d "node_modules" ]; then
        echo "   📦 安装依赖..."
        npm install
    fi

    npm run dev
}

# 检查参数
if [ "$1" = "backend" ]; then
    restart_backend
elif [ "$1" = "frontend" ]; then
    restart_frontend
elif [ "$1" = "all" ]; then
    restart_backend
    echo ""
    echo "======================================"
    echo ""
    restart_frontend
else
    echo "用法: ./restart.sh [backend|frontend|all]"
    echo ""
    echo "选项:"
    echo "  backend   - 重新编译并重启后端服务"
    echo "  frontend  - 重启前端服务"
    echo "  all       - 重启前后端服务"
    echo ""
    echo "💡 提示: 修改代码后使用此脚本应用更改"
    echo ""
    echo "示例:"
    echo "  ./restart.sh backend    # 最常用 - 修改后端代码后"
    echo "  ./restart.sh frontend"
    echo "  ./restart.sh all"
fi
