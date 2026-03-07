#!/bin/bash

# Toolkit Hub 停止脚本

echo "======================================"
echo "Toolkit Hub - 停止服务"
echo "======================================"
echo ""

stop_backend() {
    echo "🛑 停止后端服务..."
    BACKEND_PIDS=$(ps aux | grep -i "java.*toolkit-hub" | grep -v grep | awk '{print $2}')

    if [ -z "$BACKEND_PIDS" ]; then
        echo "ℹ️  后端服务未运行"
    else
        for PID in $BACKEND_PIDS; do
            echo "🔪 终止后端进程: $PID"
            kill $PID
        done
        sleep 2
        # 如果还在运行，强制终止
        BACKEND_PIDS=$(ps aux | grep -i "java.*toolkit-hub" | grep -v grep | awk '{print $2}')
        if [ ! -z "$BACKEND_PIDS" ]; then
            for PID in $BACKEND_PIDS; do
                echo "🔪 强制终止后端进程: $PID"
                kill -9 $PID
            done
        fi
        echo "✅ 后端服务已停止"
    fi
}

stop_frontend() {
    echo "🛑 停止前端服务..."
    FRONTEND_PIDS=$(ps aux | grep "node.*vite" | grep -v grep | awk '{print $2}')

    if [ -z "$FRONTEND_PIDS" ]; then
        echo "ℹ️  前端服务未运行"
    else
        for PID in $FRONTEND_PIDS; do
            echo "🔪 终止前端进程: $PID"
            kill $PID
        done
        echo "✅ 前端服务已停止"
    fi
}

# 检查参数
if [ "$1" = "backend" ]; then
    stop_backend
elif [ "$1" = "frontend" ]; then
    stop_frontend
elif [ "$1" = "all" ] || [ -z "$1" ]; then
    stop_backend
    echo ""
    stop_frontend
else
    echo "用法: ./stop.sh [backend|frontend|all]"
    echo ""
    echo "选项:"
    echo "  backend   - 仅停止后端服务"
    echo "  frontend  - 仅停止前端服务"
    echo "  all       - 停止前后端服务（默认）"
    echo ""
    echo "示例:"
    echo "  ./stop.sh backend"
    echo "  ./stop.sh frontend"
    echo "  ./stop.sh all"
    echo "  ./stop.sh          # 等同于 'all'"
fi
