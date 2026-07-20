<div align="center">

# EasyBoot

一个适合 Java 初学者直接上手的 Spring Boot 后端项目。

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![CI](https://github.com/dyskaura/Easyboot/actions/workflows/ci.yml/badge.svg)](https://github.com/dyskaura/Easyboot/actions)

</div>

## 项目介绍

学 Spring Boot 时，我们经常会把时间花在重复配置上：登录怎么写、Token 放在哪里、接口报错怎么统一返回、分页应该怎么处理。

EasyBoot 把这些常用部分整理成了一个可以直接运行的项目。它目前不追求“大而全”，代码也没有做过度封装，主要希望刚接触后端的同学能够看懂目录、找到入口，并在现有模块上继续添加自己的业务。

你可以用它练习前后端分离，也可以把它作为课程设计、毕业设计或个人项目的后端起点。

## 已有功能

- 用户注册、登录和当前用户信息
- JWT 身份认证
- USER、ADMIN 两种角色
- 用户列表、分页查询、编辑和删除
- 根据角色返回菜单，管理员可维护菜单
- 一次性图形验证码，验证码默认 3 分钟过期
- 文件上传与下载，限制大小和文件类型
- Redis 可选配置；不启用 Redis 时自动使用内存存储
- 修改、删除、上传和代码生成等操作自动记入操作日志
- 字典类型和字典项管理
- 根据字段定义生成 Entity、Repository、DTO、Service、Controller
- BCrypt 密码加密
- 参数校验和统一返回格式
- 全局异常处理
- Swagger / OpenAPI 接口文档
- H2 本地数据库
- MySQL 生产环境配置
- Docker Compose 启动 MySQL
- MockMvc 接口测试
- GitHub Actions 自动构建

## 技术选型

| 技术 | 说明 |
| --- | --- |
| Java 21 | 项目使用的 Java 版本 |
| Spring Boot 3.5 | 基础框架 |
| Spring Security | 认证和权限控制 |
| JJWT | 生成与解析 Token |
| Spring Data JPA | 数据访问和分页 |
| Spring Data Redis | Redis 存储 |
| H2 | 默认开发数据库 |
| MySQL | 生产环境数据库 |
| springdoc-openapi | 在线接口文档 |
| JUnit 5、MockMvc | 自动化测试 |
| Maven Wrapper | 无需提前安装 Maven |

## 快速开始

### 环境要求

安装 JDK 21 或更高版本即可。项目自带 Maven Wrapper，不需要另外配置 Maven。

### 获取项目

```bash
git clone https://github.com/dyskaura/Easyboot.git
cd Easyboot
```

Windows：

```powershell
.\mvnw.cmd spring-boot:run
```

macOS 或 Linux：

```bash
./mvnw spring-boot:run
```

第一次启动需要下载依赖，等待时间取决于网络情况。看到 `Started EasyBootApplication` 后说明启动成功。

### 常用地址

| 内容 | 地址 |
| --- | --- |
| Swagger 接口文档 | http://localhost:8080/swagger-ui.html |
| H2 数据库控制台 | http://localhost:8080/h2-console |
| 接口根路径 | http://localhost:8080/api |

H2 控制台连接信息：

```text
JDBC URL: jdbc:h2:file:./data/easyboot
User Name: sa
Password: 留空
```

### 默认管理员

```text
用户名：admin
密码：EasyBoot123
```

这个账号是为了方便本地体验而保留的。准备部署项目时，请先修改默认密码和 JWT 密钥。

## 调用接口

### 登录

登录前先获取验证码：

```http
GET /api/captcha
```

响应里包含 `captchaId` 和一张 Base64 SVG 图片。显示图片后，把用户填写的内容和 `captchaId` 一起提交：

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "EasyBoot123",
  "captchaId": "获取验证码时返回的 ID",
  "captchaCode": "图片中的四位字符"
}
```

登录成功后，响应数据中会包含 `accessToken`。访问需要登录的接口时，将它放到请求头中：

```http
GET /api/users?page=0&size=10
Authorization: Bearer 这里填写 accessToken
```

### 注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "student",
  "password": "123456",
  "nickname": "小明",
  "email": "student@example.com"
}
```

注册得到的账号默认是 `USER` 角色，不能访问管理员用户列表。

## 代码结构

```text
src/main/java/io/github/dyskaura/easyboot
├── auth        注册和登录
├── common      统一响应、分页、业务异常
├── config      OpenAPI 和初始化数据
├── security    JWT 与 Spring Security
└── user        用户管理
```

项目按业务模块放置代码。比如需要增加“课程管理”，可以新建一个 `course` 包：

```text
course
├── Course.java
├── CourseRepository.java
├── CourseService.java
├── CourseController.java
├── CourseResponse.java
└── UpdateCourseRequest.java
```

可以先参考 `user` 模块完成一个简单 CRUD，再逐步增加查询条件、业务校验和权限。

## 主要管理接口

| 模块 | 地址 | 权限 |
| --- | --- | --- |
| 当前用户菜单 | `GET /api/menus/current` | 已登录 |
| 菜单维护 | `/api/menus` | ADMIN |
| 文件上传、下载 | `/api/files` | 已登录 |
| 字典查询与维护 | `/api/dictionaries` | 已登录 / ADMIN |
| 操作日志 | `GET /api/operation-logs` | ADMIN |
| CRUD 代码生成 | `POST /api/code-generator` | ADMIN |

代码生成接口接收包名、类名、表名和字段列表，返回一个 ZIP。ZIP 里是五个 Java 源文件，解压到自己的项目后再根据业务调整。生成器只接受预设的 Java 字段类型，不会执行传入的代码。

## 使用 MySQL

本地学习默认使用 H2，不安装 MySQL也能启动。如果需要换成 MySQL，可先运行：

```bash
docker compose up -d
```

然后构建项目：

```bash
./mvnw clean package
```

使用生产配置启动：

```bash
java -jar target/easyboot-0.2.0.jar --spring.profiles.active=prod
```

数据库连接可以通过以下环境变量修改：

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION_SECONDS
```

示例值可以参考项目根目录下的 `.env.example`。

### 启用 Redis

Redis 不是默认依赖，因此没有 Redis 也能运行。先通过 Docker 启动 Redis：

```bash
docker compose up -d redis
```

再同时启用开发和 Redis Profile：

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,redis
```

启用后，验证码等临时数据会保存到 Redis；普通开发模式下则保存在当前进程内存中。

## 运行测试

```bash
./mvnw clean verify
```

测试使用独立的 H2 内存数据库，不会修改开发环境的数据。

## 当前不足

EasyBoot 还处于早期阶段。当前菜单权限以 USER、ADMIN 两种角色为基础，尚未细分到每个按钮；文件默认保存在本地目录，代码生成器也只负责生成基础 CRUD。如果你需要完整的工作流、多租户或微服务能力，若依、JeecgBoot 等成熟项目会更合适。

后续准备逐步补充：

- 独立的登录日志
- 按钮级权限标识
- 对象存储（MinIO、S3）
- 从现有数据库表读取字段并生成 CRUD
- 一个简单的 Vue 管理端

如果你在使用时遇到问题，可以提交 Issue。也欢迎通过 Pull Request 一起完善项目。

## License

本项目使用 [MIT License](LICENSE)。
