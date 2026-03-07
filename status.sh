#!/bin/bash

# Toolkit Hub 状态检查脚本

echo "======================================"
echo "Toolkit Hub - 服务状态"
echo "======================================"
echo ""

# 检查后端
echo "🔍 后端服务状态:"
BACKEND_PIDS=$(ps aux | grep -i "java.*toolkit-hub" | grep -v grep)
if [ -z "$BACKEND_PIDS" ]; then
    echo "   ❌ 后端服务未运行"
else
    echo "   ✅ 后端服务运行中"
    echo "$BACKEND_PIDS" | awk '{print "   进程ID: " $2 " | CPU: " $3 "% | 内存: " $4 "%"}'
    echo "   🌐 API地址: http://localhost:8080/api"
    echo "   📖 接口文档: http://localhost:8080/doc.html"
fi

echo ""

# 检查前端
echo "🔍 前端服务状态:"
FRONTEND_PIDS=$(ps aux | grep "node.*vite" | grep -v grep)
if [ -z "$FRONTEND_PIDS" ]; then
    echo "   ❌ 前端服务未运行"
else
    echo "   ✅ 前端服务运行中"
    echo "$FRONTEND_PIDS" | awk '{print "   进程ID: " $2 " | CPU: " $3 "% | 内存: " $4 "%"}'
    echo "   🌐 前端地址: http://localhost:5173"
fi

echo ""
echo "======================================"
