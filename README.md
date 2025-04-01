# 校内研学参观 APP - 后端

**版本：1.0.0**  
**更新日期：2025-03-24**  

## 项目简介
本项目后端基于 Spring Boot 框架，提供 RESTful API 以支持校内研学参观 APP 的功能。

## 功能介绍

### 1. 校内资源管理 API
- 提供 **主楼**、**中楼** 教室的占用情况（仅查询）。
- 提供 **讲座**、**实验室** 资源的查询与预约。
- 通过 MySQL 存储资源信息，并提供 API 供前端访问。

### 2. 以图搜景 API
- 接收用户上传的图片。
- 对比数据库中的景点信息。
- 返回匹配的景点介绍。

### 3. 图像导航 API
- 接收用户上传的图片和目标地点。
- 结合地图 API 生成导航路线。

## 技术栈
- **框架：** Spring Boot 3.x
- **数据库：** MySQL 8.x
- **ORM：** JPA / MyBatis
- **API 交互：** RESTful API + JSON
- **地图服务：** 高德地图 API

## 安装与运行
```bash
git clone https://github.com/your-repo-name.git
cd your-repo-name/backend
mvn clean install
mvn spring-boot:run
```

## API 端点示例
```http
GET /api/resources/buildings  # 获取楼宇资源列表
GET /api/resources/classrooms?building=main  # 查询主楼教室使用情况
POST /api/bookings  # 预约实验室/讲座
POST /api/image/search  # 以图搜景
POST /api/navigation  # 进行导航
```

## 贡献
@NFan

---

# Campus Study Tour APP - Backend

**Version: 1.0.0**  
**Last Updated: 2025-03-24**  

## Introduction
This backend is built using the Spring Boot framework and provides RESTful APIs to support the Campus Study Tour APP.

## Features

### 1. Campus Resource Management API
- Provides occupancy status of **Main Building** and **Middle Building** classrooms (view-only).
- Allows querying and booking for **Lectures** and **Laboratories**.
- Stores resource data in MySQL and serves API endpoints for the frontend.

### 2. Image-Based Spot Recognition API
- Receives user-uploaded images.
- Compares with the scenic spots database.
- Returns the matching scenic spot details.

### 3. Image-Based Navigation API
- Receives an uploaded image and destination.
- Generates a navigation route using a map API.

## Tech Stack
- **Framework:** Spring Boot 3.x
- **Database:** MySQL 8.x
- **ORM:** JPA / MyBatis
- **API Interaction:** RESTful API + JSON
- **Map Services:** AMap (Gaode) API

## Installation & Running
```bash
git clone https://github.com/your-repo-name.git
cd your-repo-name/backend
mvn clean install
mvn spring-boot:run
```

## API Endpoints Example
```http
GET /api/resources/buildings  # Get building resources list
GET /api/resources/classrooms?building=main  # Check Main Building classroom usage
POST /api/bookings  # Book a lecture/lab
POST /api/image/search  # Image-based spot recognition
POST /api/navigation  # Get navigation route
```

## Contribution
@NFan
