# Toolkit Hub 脚本使用说明

本项目提供了一套完整的 Shell 脚本来管理前后端服务。

## 📋 脚本列表

### 1. `start.sh` - 启动服务
首次启动或需要手动启动服务时使用。

```bash
# 仅启动后端
./start.sh backend

# 仅启动前端
./start.sh frontend

# 同时启动前后端
./start.sh all
```

### 2. `stop.sh` - 停止服务
停止正在运行的服务。

```bash
# 仅停止后端
./stop.sh backend

# 仅停止前端
./stop.sh frontend

# 停止所有服务（默认）
./stop.sh
./stop.sh all
```

### 3. `restart.sh` - 重启服务 ⭐️ 最常用
**修改代码后使用此脚本重启服务。**

```bash
# 重启后端（修改后端代码后最常用）
./restart.sh backend

# 重启前端
./restart.sh frontend

# 重启所有服务
./restart.sh all
```

**重要提示：**
- 后端重启会自动重新编译代码（`mvn clean package`）
- 前端修改通常会自动热重载，无需重启

### 4. `status.sh` - 查看状态
检查前后端服务的运行状态。

```bash
./status.sh
```

## 🚀 快速开始

### 首次启动
```bash
# 1. 启动后端
./start.sh backend

# 2. 启动前端（新开一个终端）
./start.sh frontend
```

### 修改代码后
```bash
# 修改后端代码后
./restart.sh backend

# 前端代码会自动热重载，通常不需要重启
# 如果需要重启前端
./restart.sh frontend
```

### 查看服务状态
```bash
./status.sh
```

### 停止所有服务
```bash
./stop.sh
```

## 📍 服务地址

启动成功后可以访问：

- **前端**: http://localhost:5173
- **后端 API**: http://localhost:8080/api
- **接口文档**: http://localhost:8080/doc.html

## 💡 常见使用场景

### 场景1: 每天开始工作
```bash
./start.sh backend     # 启动后端
./start.sh frontend    # 启动前端（新终端）
```

### 场景2: 修改了后端代码
```bash
./restart.sh backend   # 自动编译并重启
```

### 场景3: 检查服务是否运行
```bash
./status.sh
```

### 场景4: 下班关闭所有服务
```bash
./stop.sh
```

## 🔧 故障排查

### 后端启动失败
```bash
# 查看后端日志
tail -f backend/backend.log
```

### 端口被占用
```bash
# 检查 8080 端口
lsof -i :8080

# 检查 5173 端口
lsof -i :5173

# 强制停止并重启
./stop.sh
./start.sh all
```

### 编译失败
```bash
# 手动清理并重新编译
cd backend
mvn clean
mvn package -DskipTests
```

## 📝 脚本功能说明

### `restart.sh backend` 执行流程
1. 停止正在运行的后端服务
2. 重新编译后端代码（`mvn clean package -DskipTests`）
3. 启动后端服务
4. 检查启动状态
5. 显示访问地址

### 安全特性
- 自动检测服务是否运行
- 编译失败时自动终止
- 启动失败时显示日志
- 优雅关闭进程（先 `kill` 后 `kill -9`）

## 🎯 推荐工作流

```bash
# 每天开始
./start.sh backend
./start.sh frontend  # 新终端

# 开发中（修改后端代码）
./restart.sh backend

# 开发结束
./stop.sh
```

---

**提示**: 所有脚本都已添加执行权限，可以直接运行。如果遇到权限问题，执行：
```bash
chmod +x *.sh
```
