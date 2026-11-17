# AGENTS.md

本文件是 `lottery-system` 的项目约定。通用行为规则见全局 `AGENTS.md`，通用代码规范见 `coding-conventions` skill。

## 代码规范

- 方法不超过 50 行，超过就抽方法。
- 公共方法、非自解释私有方法写注释，说明用途、参数、返回值、副作用。
- 方法内部按逻辑块写注释，说明每一块处理什么逻辑；不要逐行复述代码。
- 其余通用规范见 `coding-conventions` skill。

## 项目概览

- 项目目录：`project/lottery-system`
- 技术栈：Java 17、Spring Boot 4.0.8、MyBatis-Plus 3.5.18.1、MySQL 8、Redis、JWT、静态 HTML + jQuery
- 数据库：`lottery_system`
- 前端：`src/main/resources/static`，CDN 已本地化

## 常用命令

```bash
mvn test
mvn spring-boot:run
mvn "-Dtest=PrizeServiceImplTest" test
mvn "-Dtest=!EmailCodeServiceTest" test
```

- 本地默认端口：8080
- E2E 使用 18080：
  `mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"`
- Redis 在云服务器，通过 Tabby SSH 端口转发到 `127.0.0.1:6379`；未连接时 `EmailCodeServiceTest` 会失败。

## 目录结构

- `controller/request`：Controller 入参
- `controller/response`：Controller 出参
- `controller/advice`：统一响应、异常处理
- `service/command`：Service 入参
- `service/dto`：Service 出参
- `service/enums`：Service 内部枚举
- `service/impl`：Service 实现
- `mapper/dataobject`：数据库对象，继承 `BaseDO`
- `mapper/handler`：TypeHandler，例如手机号加密
- `common/enums`：跨层枚举
- `common/config`：配置类
- `common/errorcode`：错误码
- `common/exception`：自定义异常
- `common/utils`：工具类

## 数据库

- `BaseDO` 提供 `id`、`gmtCreate`、`gmtModified`。
- DO 用 `@TableName("表名")`，字段驼峰，MyBatis-Plus 自动转下划线。
- 手机号用 `EncryptTypeHandler` 加密存储。
- 上传图片只在数据库存相对 URL，例如 `/uploads/prize/2026/11/12/xxx.png`。

## 鉴权

- `POST /user/login` 返回 JWT。
- 前端把 token 存 `localStorage.user_token`；`js/auth.js` 通过 `$.ajaxSetup` 自动加 `Authorization: Bearer <token>`。
- `LoginInterceptor` 校验 token；`WebConfig` 配置白名单。
- 仅管理员接口：`/user/register`、`/user/list`、`/file/upload`、`/prize/create`。
- 普通用户访问管理员接口返回 403；未登录返回 401。
- 默认管理员：`admin / 12345678`（仅开发环境）。

## 文件上传

- 统一走 `FileService.uploadImage(FileSceneEnum scene, MultipartFile file)`。
- 存储路径：`uploads/{scene}/yyyy/MM/dd/{uuid}.{ext}`。
- 访问路径：`/uploads/**` 映射到项目根目录 `uploads/`。
- 限制：5MB；jpg/jpeg/png/gif/webp；校验文件头。
- 业务落库失败时调用 `FileService.deleteByUrl` 清理孤儿文件。
- 新增场景时在 `FileSceneEnum` 加枚举值。

## 现有接口

- `POST /user/email-code`
- `POST /user/register`（管理员）
- `POST /user/login`
- `GET /user/list`（管理员）
- `POST /file/upload/{scene}`（管理员）
- `POST /prize/create`（管理员）
- `GET /prize/list`（管理员，分页：`currentPage` / `pageSize`）

`/prize/create` 使用 `multipart/form-data`，字段平铺绑定到 `PrizeCreateRequest`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `prizeName` | Text | 奖品名称，必填 |
| `description` | Text | 奖品描述 |
| `price` | Text | 奖品价格，如 `6999.00` |
| `prizePic` | File | 奖品图片，必填 |

- 校验失败返回 400；缺图返回 1031；非管理员返回 403。
- 图片由 `PrizeServiceImpl` 内部调用 `FileService.uploadImage` 保存。

## 错误码

- Service：1000+
- Controller：500+
- 全局：400/401/403/404/500

## 测试

- 改完先编译，再跑相关测试；Redis 可用时跑全量。
- E2E 使用 18080，测试后清理临时数据、临时用户、上传文件。
- 文件测试使用 `@TempDir`，不要往项目目录写测试文件。

## 前端

- 静态文件在 `src/main/resources/static`。
- CDN 已本地化，不要改回外链。
- 后台页面统一读 `user_token`。
- iframe 页面跳转用 `window.parent.location.href` / `postMessage`。

## 注意

- 8080 上的旧实例需要重启后才能加载最新代码。
