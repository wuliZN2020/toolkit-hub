#!/bin/bash

# Toolkit Hub 启动脚本

echo "======================================"
echo "Toolkit Hub - Project Launcher"
echo "======================================"
echo ""

# Check if backend or frontend argument is provided
if [ "$1" = "backend" ]; then
    echo "Starting backend..."
    cd backend
    mvn spring-boot:run
elif [ "$1" = "frontend" ]; then
    echo "Starting frontend..."
    cd frontend
    npm run dev
elif [ "$1" = "all" ]; then
    echo "Starting both backend and frontend..."
    echo "Starting backend in background..."
    cd backend
    mvn spring-boot:run > ../backend.log 2>&1 &
    BACKEND_PID=$!
    echo "Backend started with PID: $BACKEND_PID"

    cd ../frontend
    echo "Starting frontend..."
    npm run dev
else
    echo "Usage: ./start.sh [backend|frontend|all]"
    echo ""
    echo "Options:"
    echo "  backend   - Start Spring Boot backend only"
    echo "  frontend  - Start React frontend only"
    echo "  all       - Start both backend and frontend"
    echo ""
    echo "Example:"
    echo "  ./start.sh backend"
    echo "  ./start.sh frontend"
    echo "  ./start.sh all"
fi
