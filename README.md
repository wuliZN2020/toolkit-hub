# Toolkit Hub

个人工具集网站，集成各种实用小工具。

## 项目结构

```
toolkit-hub/
├── backend/          # Spring Boot 后端
└── frontend/         # React + TypeScript 前端
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- MySQL 8.0
- Redis
- MyBatis Plus
- Jsoup (网页爬虫)
- Knife4j (API文档)

### 前端
- React 18
- TypeScript
- Vite
- Ant Design
- React Router
- Axios

## 功能模块

### 1. 读书摘抄排版工具
- 爬取豆瓣书籍信息和摘抄
- 支持微信公众号排版
- 支持小红书排版
- 多种模板样式

## 开发指南

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.x
- Maven 3.8+

### 后端启动

1. 创建数据库
```sql
CREATE DATABASE toolkit_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置
编辑 `backend/src/main/resources/application.yml`，配置数据库和Redis连接信息。

3. 启动后端
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端运行在：http://localhost:8080
API文档：http://localhost:8080/api/doc.html

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端运行在：http://localhost:5173

## API说明

### 健康检查
- `GET /api/health` - 系统健康检查
- `GET /api/health/test` - API测试

### 书籍相关（待开发）
- `GET /api/book/search?keyword={keyword}` - 搜索书籍
- `GET /api/book/{id}/quotes` - 获取书籍摘抄

### 模板相关（待开发）
- `GET /api/template/list` - 获取模板列表
- `GET /api/template/{id}` - 获取模板详情

## 开发计划

- [x] 项目框架搭建
- [ ] 豆瓣爬虫实现
- [ ] 数据库设计和实现
- [ ] 模板系统
- [ ] 排版功能
- [ ] 图片生成功能
- [ ] 历史记录管理

## 许可证

MIT License
