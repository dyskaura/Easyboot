<div align="center">

# 🚀 EasyBoot

**一套专为 Java 初学者、大学生和毕设开发者设计的 Spring Boot 快速开发后端**

少配一点环境，多写一点业务。

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![CI](https://github.com/dyskaura/easyboot/actions/workflows/ci.yml/badge.svg)](https://github.com/dyskaura/easyboot/actions)

</div>

## ✨ 为什么是 EasyBoot？

第一次写 Spring Boot 项目，常常不是卡在业务，而是卡在登录鉴权、项目分层、异常处理和数据库配置。

EasyBoot 把这些通用能力提前准备好。克隆项目、运行一个命令，就能得到可登录、有权限、带接口文档的后端服务。代码保持清晰和适量中文注释，适合课程设计、毕业设计，也适合作为个人项目的起点。

> EasyBoot 不是复杂的企业级“大而全”平台，而是一套能看懂、改得动、跑得起来的学习型后端。

## 🎯 适合谁？

- 正在学习 Java / Spring Boot 的同学
- 需要快速完成课程设计或毕业设计的大学生
- 第一次独立搭建前后端分离项目的开发者
- 想练习 Vue、React、小程序但不想重复写后端基础功能的人

## ✅ 已有功能

- [x] 用户注册、登录、当前用户信息
- [x] JWT 无状态身份认证
- [x] USER / ADMIN 角色权限
- [x] 用户查询、分页、编辑和删除
- [x] BCrypt 密码加密
- [x] 统一 JSON 响应结构
- [x] 全局异常与参数校验
- [x] Swagger / OpenAPI 在线接口文档
- [x] H2 开发数据库，下载即运行
- [x] MySQL 生产配置
- [x] Docker Compose 启动 MySQL
- [x] JUnit 5 + MockMvc 接口测试
- [x] GitHub Actions 自动测试

## 🧰 技术栈

| 技术 | 用途 |
| --- | --- |
| Java 21 | 长期支持版本 |
| Spring Boot 3.5 | 项目基础框架 |
| Spring Security | 登录认证与权限控制 |
| JWT | 无状态 Token |
| Spring Data JPA | 数据访问与分页 |
| H2 / MySQL | 开发与生产数据库 |
| springdoc-openapi | Swagger 接口文档 |
| JUnit 5 / MockMvc | 自动化测试 |
| Maven Wrapper | 无需单独安装 Maven |

## 🚦 5 分钟启动

### 1. 准备环境

只需要安装 **JDK 21 或更高版本**，无需单独安装 Maven。

### 2. 克隆并启动

```bash
git clone https://github.com/dyskaura/easyboot.git
cd easyboot
```

Windows：

```powershell
.\mvnw.cmd spring-boot:run
```

macOS / Linux：

```bash
./mvnw spring-boot:run
```

启动成功后访问：

- Swagger 文档：http://localhost:8080/swagger-ui.html
- H2 控制台：http://localhost:8080/h2-console
- API 地址：http://localhost:8080/api

H2 控制台连接信息：

```text
JDBC URL: jdbc:h2:file:./data/easyboot
用户名: sa
密码: 留空
```

### 3. 使用默认管理员

```text
用户名：admin
密码：EasyBoot123
```

> 默认账号仅用于本地学习。部署前请修改初始化逻辑和 JWT 密钥。

## 📮 接口示例

### 登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "EasyBoot123"
}
```

响应中的 `accessToken` 用于调用需要登录的接口：

```http
GET /api/users?page=0&size=10
Authorization: Bearer <accessToken>
```

### 注册普通用户

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "student",
  "password": "123456",
  "nickname": "小明同学",
  "email": "student@example.com"
}
```

## 📁 项目结构

```text
src/main/java/io/github/dyskaura/easyboot
├── auth/       # 注册、登录
├── common/     # 统一响应、分页、异常处理
├── config/     # OpenAPI、初始化数据
├── security/   # JWT 与 Spring Security
└── user/       # 用户管理示例模块
```

建议新增业务时复制 `user` 模块的组织方式，例如：

```text
course/
├── Course.java
├── CourseRepository.java
├── CourseService.java
├── CourseController.java
├── CourseResponse.java
└── UpdateCourseRequest.java
```

## 🐬 切换 MySQL

启动数据库：

```bash
docker compose up -d
```

构建并以生产配置启动：

```bash
./mvnw clean package
java -jar target/easyboot-0.1.0.jar --spring.profiles.active=prod
```

可以通过环境变量修改连接信息：

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION_SECONDS
```

## 🧪 运行测试

```bash
./mvnw clean verify
```

## 🗺️ 后续计划

- [ ] 菜单和按钮级权限
- [ ] 操作日志与登录日志
- [ ] 文件上传
- [ ] 字典管理
- [ ] Redis 缓存
- [ ] 数据库表生成 CRUD
- [ ] 配套 Vue 管理端
- [ ] 从零入门教程

欢迎提交 Issue、功能建议和 Pull Request。觉得项目对你有帮助的话，可以点一个 ⭐ Star，让更多刚学 Java 的同学看到它。

## 📄 开源协议

[MIT License](LICENSE) — 可以自由学习、修改和用于个人项目。
