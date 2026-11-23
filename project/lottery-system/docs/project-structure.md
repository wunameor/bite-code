# lottery-system 项目结构文档

> 文档范围：`project/lottery-system` 抽奖系统。  
> 目录树重点展开源码、SQL、文档、配置和测试目录。`target`、`logs`、`uploads` 属于构建产物或运行期数据，只说明用途，不逐个展开内部文件。

## 1. 项目概览

`lottery-system` 是一个基于 Spring Boot 的抽奖系统后端项目，同时包含一组静态 HTML 前端页面。项目支持：

- 用户登录与 JWT 鉴权。
- 管理员注册用户、查看人员列表。
- 奖品创建、奖品图片上传、奖品列表查询。
- 活动创建、活动列表、活动详情查询。
- 异步抽奖：抽奖请求先落任务表，再通过 RabbitMQ 消费执行。
- 抽奖状态轮询：前端通过 `requestId` 查询任务状态和中奖结果。
- 中奖记录查询。
- 中奖通知异步投递：抽奖事务提交后发送通知消息，消费者异步发邮件。
- 抽奖任务补偿：处理 MQ 投递失败、消费超时、任务长期停留在 `PENDING` 或 `PROCESSING` 的情况。

当前项目采用传统分层结构：

```text
Controller  ->  Service  ->  Mapper  ->  MySQL
                 ↑
              RabbitMQ / Redis / Local Upload
```

## 2. 技术栈

```text
Java                 17
Spring Boot          4.0.8
Web                  spring-boot-starter-webmvc
Validation           spring-boot-starter-validation
JSON                 spring-boot-starter-jackson / JacksonUtil
Database             MySQL 8
ORM                  MyBatis-Plus 3.5.18.1
Redis                spring-boot-starter-data-redis
MQ                   spring-boot-starter-amqp
Mail                 spring-boot-starter-mail
JWT                  jjwt-api / jjwt-impl / jjwt-jackson 0.11.5
Tool Library         Hutool
Boilerplate          Lombok
Frontend             静态 HTML + jQuery + Bootstrap + Toastr
Build                Maven
```

## 3. 常用命令

```bash
# 编译
mvn -q -DskipTests compile

# 跑全量测试，排除依赖 Redis 的 EmailCodeServiceTest
mvn -q clean test "-Dtest=!EmailCodeServiceTest"

# 只跑抽奖状态更新链测试
mvn -q "-Dtest=DrawStateUpdaterChainTest" test

# 只跑抽奖服务测试
mvn -q "-Dtest=DrawPrizeServiceImplTest" test

# 本地启动，默认端口 8080
mvn spring-boot:run

# E2E 推荐使用独立端口，避免和 8080 旧实例冲突
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"
```

说明：

- `EmailCodeServiceTest` 依赖 Redis，部分测试还可能发送邮件。
- RabbitMQ 和 Redis 需要本地端口转发或对应服务可用。
- 当前 `application.yaml` 中本地默认关闭抽奖补偿扫描，避免定时 SQL 日志刷屏。

## 4. 根目录结构

```text
project/lottery-system
├── .gitignore                         # Git 忽略规则：target、logs、uploads、IDE 配置等
├── docs                               # 项目文档目录
│   └── project-structure.md           # 本文档：项目结构和职责说明
├── logs                               # 运行日志目录，由 Spring Boot 日志配置产生
├── pom.xml                            # Maven 工程配置和依赖管理
├── sql                                # 数据库脚本目录
│   ├── draw-async.sql                 # 抽奖异步任务表、winning_record 唯一键脚本
│   └── init-admin.sql                 # 默认管理员账号初始化脚本，仅用于开发环境
├── src                                # 源码目录
│   ├── main                           # 主代码与主资源
│   └── test                           # 测试代码
├── target                             # Maven 构建产物目录
└── uploads                            # 本地图片上传目录
```

## 5. 源码包总体结构

```text
src/main/java/com/wunamor/lotterysystem
├── LotterySystemApplication.java      # Spring Boot 启动类，启用定时任务
├── common                             # 公共基础设施包，不承载抽奖业务主流程
│   ├── config                         # Spring / MyBatis / RabbitMQ / Web / 本地配置
│   ├── constants                      # 公共常量，例如 RabbitMQ 名称常量
│   ├── enums                          # 跨层公共枚举，例如活动状态、奖品等级、登录类型
│   ├── errorcode                      # 错误码对象和各层错误码常量
│   ├── exception                      # ControllerException / ServiceException
│   ├── filter                         # Logback 过滤器
│   ├── interceptor                    # JWT 登录拦截器和管理员接口拦截
│   ├── pojo                           # CommonResult / PageQuery / PageResult
│   └── utils                          # JWT、JSON、Mail、Redis、Regex 等工具
├── controller                         # HTTP 接口层
│   ├── advice                         # 全局响应包装、全局异常处理、响应包装开关注解
│   ├── request                        # Controller 入参对象
│   ├── response                       # Controller 出参对象
│   ├── ActivityController.java        # 活动创建、活动列表、活动详情
│   ├── DrawPrizeController.java       # 抽奖任务提交、抽奖状态轮询
│   ├── FileController.java            # 图片上传
│   ├── PrizeController.java           # 奖品创建、奖品列表
│   ├── UserController.java            # 用户注册、登录、验证码、人员列表、个人资料
│   └── WinningRecordController.java   # 中奖记录查询
├── mapper                             # 数据访问层
│   ├── dataobject                     # 数据库对象 DO
│   ├── handler                        # MyBatis TypeHandler，例如手机号加密处理
│   ├── ActivityMapper.java            # activity 表
│   ├── ActivityPrizeMapper.java       # activity_prize 表
│   ├── ActivityUserMapper.java        # activity_user 表
│   ├── DrawRequestMapper.java         # draw_request 表
│   ├── PrizeMapper.java               # prize 表
│   ├── UserMapper.java                # user 表
│   └── WinningRecordMapper.java       # winning_record 表
└── service                            # 业务服务层
    ├── command                        # Service 入参对象
    ├── dto                            # Service 出参对象
    ├── draw                           # 抽奖业务内部结构
    │   └── state                      # 抽奖状态翻转阶段、上下文、接口和执行链
    │       └── impl                   # 六个状态更新步骤实现
    ├── enums                          # Service 内部枚举，例如奖品状态、人员状态、任务状态
    ├── impl                           # Service 实现类
    ├── mq                             # 抽奖业务 MQ 适配层
    │   ├── consumer                   # MQ 消费者
    │   ├── message                    # MQ 消息体
    │   └── producer                   # MQ 生产者接口与实现
    ├── ActivityCacheService.java      # 活动详情 Redis 快照缓存服务
    ├── ActivityService.java           # 活动服务接口
    ├── DrawPrizeService.java          # 抽奖事务服务接口
    ├── DrawRequestService.java        # 抽奖异步任务服务接口
    ├── EmailCodeService.java          # 邮箱验证码服务接口
    ├── FileService.java               # 文件上传服务接口
    ├── PrizeService.java              # 奖品服务接口
    ├── UserService.java               # 用户服务接口
    └── WinningRecordService.java      # 中奖记录查询服务接口
```

## 6. `src/main/resources` 目录结构

```text
src/main/resources
├── application.yaml                   # 应用主配置：数据源、Redis、RabbitMQ、邮件、上传、补偿任务、日志、MyBatis-Plus
├── mapper                             # MyBatis XML Mapper 文件目录
│   ├── ActivityPrizeMapper.xml        # activity_prize 批量插入
│   ├── ActivityUserMapper.xml         # activity_user 批量插入
│   └── WinningRecordMapper.xml        # winning_record 批量插入
└── static                             # 静态前端页面目录
    ├── favicon.ico                    # 浏览器图标
    ├── admin.html                     # 后台框架页
    ├── blogin.html                    # 后台登录页，唯一登录入口
    ├── login.html                     # 兼容跳转页，自动跳转 blogin.html
    ├── register.html                  # 用户注册页
    ├── user-list.html                 # 人员列表页
    ├── prizes-list.html               # 奖品列表页
    ├── create-prizes.html             # 创建奖品页
    ├── activities-list.html           # 活动列表页
    ├── create-activity.html           # 创建活动页
    ├── draw.html                      # 抽奖页：两次点击抽一人、500ms 防连点、异步任务轮询、中奖展示
    ├── profile.html                   # 当前登录用户个人信息编辑页
    ├── forgot-password.html           # 忘记密码页，通过邮箱验证码重置密码
    ├── css                            # 页面样式目录
    ├── js                             # 页面脚本目录
    └── pic                            # 页面图片资源目录
```

## 7. `src/test/java` 目录结构

```text
src/test/java/com/wunamor/lotterysystem
├── LogTest.java                         # 日志相关测试
├── LotterySystemApplicationTests.java   # Spring Boot 上下文启动测试
├── common                               # 公共层测试
│   ├── interceptor
│   │   └── LoginInterceptorTest.java    # 登录拦截器、未登录、越权等测试
│   ├── pojo
│   │   └── PageResultTest.java          # 通用分页结果测试
│   └── utils
│       └── JacksonUtilTest.java         # JSON 序列化/反序列化测试
├── mapper
│   └── UserMapperTest.java              # 用户 Mapper 与手机号加密相关测试
└── service                              # 服务层测试
    ├── ActivityCacheServiceImplTest.java
    ├── ActivityServiceImplTest.java
    ├── DrawPrizeServiceImplTest.java
    ├── DrawRequestServiceImplTest.java
    ├── EmailCodeServiceTest.java
    ├── FileServiceImplTest.java
    ├── PrizeServiceImplTest.java
    ├── WinningRecordServiceImplTest.java
    ├── draw
    │   └── state
    │       └── DrawStateUpdaterChainTest.java
    └── mq
        └── consumer
            ├── DrawCommandListenerTest.java
            └── DrawNotifyListenerTest.java
```

## 8. `common` 包说明

`common` 包用于放置跨业务复用的基础设施代码。它不应该承载抽奖主业务流程，也不应该依赖 `controller` 层的请求/响应对象。

```text
common
├── config
├── constants
├── enums
├── errorcode
├── exception
├── filter
├── interceptor
├── pojo
└── utils
```

### 8.1 `common/config`

Spring 配置和本地配置属性类。

```text
common/config
├── DrawCompensationProperties.java    # 抽奖异步任务补偿配置，前缀为 lottery.draw.compensation
├── MybatisPlusConfig.java             # MyBatis-Plus 相关配置
├── RabbitMqConfig.java                # RabbitMQ exchange、queue、binding、DLQ 配置
├── UploadProperties.java              # 本地上传配置，前缀为 lottery.upload
└── WebConfig.java                     # 登录拦截器注册、静态资源映射
```

职责边界：

- `RabbitMqConfig` 负责 MQ 基础设施 bean：交换机、队列、绑定、死信队列。
- `DrawCompensationProperties` 负责补偿扫描参数：开关、重试上限、超时时间、扫描间隔。
- `UploadProperties` 负责上传目录、访问前缀、文件大小和扩展名限制。
- `WebConfig` 负责：
  - 注册 `LoginInterceptor`。
  - 放行登录、验证码、静态资源、中奖名单分享链接等公开路径。
  - 把 `/uploads/**` 映射到本地 `uploads/` 目录。

### 8.2 `common/constants`

跨层常量。

```text
common/constants
└── RabbitMqConstants.java             # RabbitMQ exchange、queue、routing key、DLQ 名称常量
```

`RabbitMqConstants` 只保存 MQ 名称，不包含业务处理逻辑。

### 8.3 `common/enums`

跨层公共枚举。

```text
common/enums
├── ActivityStatusEnum.java            # 活动状态：RUNNING / FINISHED
├── EmailSceneEnum.java                # 邮箱验证码场景：注册、登录、重置密码、修改资料
├── LoginTypeEnum.java                 # 登录方式：密码、邮箱验证码
└── PrizeTierEnum.java                 # 奖品等级：FIRST_PRIZE / SECOND_PRIZE / THIRD_PRIZE
```

`PrizeTierEnum` 除了奖品等级本身，还提供排序能力，用于保证一等奖、二等奖、三等奖的抽取顺序。

### 8.4 `common/errorcode`

错误码体系。

```text
common/errorcode
├── ControllerErrorCodeConstants.java  # Controller 层错误码
├── ErrorCode.java                     # 错误码基础对象
├── GlobalErrorCodeConstants.java      # 全局错误码，例如 400、401、403、404、500
└── ServiceErrorCodeConstants.java     # Service 层业务错误码，当前主要抽奖错误码在这里
```

约定：

```text
全局错误码       400 / 401 / 403 / 404 / 500
Controller 错误码 500+
Service 错误码   1000+
```

### 8.5 `common/exception`

自定义异常。

```text
common/exception
├── ControllerException.java           # Controller 层异常
└── ServiceException.java              # 业务异常，携带错误码和错误信息
```

业务层通常抛 `ServiceException`，由 `GlobalExceptionHandler` 转成统一响应。

### 8.6 `common/filter`

日志过滤。

```text
common/filter
└── InfoLevelFilter.java               # Logback 过滤器，只接受 INFO 级别日志
```

### 8.7 `common/interceptor`

Web 鉴权拦截。

```text
common/interceptor
└── LoginInterceptor.java              # JWT 解析、登录校验、管理员接口校验
```

`LoginInterceptor` 当前处理：

- 放行 CORS 预检请求。
- 放行静态资源处理器请求。
- 支持从 `Authorization: Bearer <token>`、`token`、`user_token` 中读取 token。
- token 有效时把 `userId` 和 `identity` 写入 request attribute，供 Controller 读取当前登录用户。
- 未登录返回 `401`。
- 管理员接口越权访问返回 `403`。

管理员接口包括：

```text
/user/register
/user/list
/file/upload
/file/upload/{scene}
/prize/create
/prize/list
/activity/create
/activity/list
/activity/detail
/draw-prize
/draw-prize/status
```

公开接口主要由 `WebConfig` 排除：

```text
/user/login
/user/email-code
/user/reset-password
/winning-records/show
/*.html
/css/**
/js/**
/pic/**
/uploads/**
/favicon.ico
/error
```

### 8.8 `common/pojo`

通用返回结构。

```text
common/pojo
├── CommonResult.java                  # 统一接口返回结构：code / message / data
├── PageQuery.java                     # 分页请求基类
└── PageResult.java                    # 通用分页结果
```

`CommonResult` 被 `GlobalResponseAdvice` 用于包装 Controller 返回值。

### 8.9 `common/utils`

通用工具类。

```text
common/utils
├── JacksonUtil.java                   # JSON 序列化和反序列化
├── JWTUtil.java                       # JWT 生成和解析
├── MailUtil.java                      # 邮件发送工具，只负责发送，不承载业务
├── RedisKeyUtil.java                  # Redis key 统一生成；活动详情 key 为 lottery:activity:detail:v3:{id}
├── RedisUtil.java                     # Redis 操作封装
└── RegexUtil.java                     # 正则工具
```

边界：

- `MailUtil` 不知道“中奖通知”是什么，只提供邮件发送能力。
- `RedisUtil` 不知道业务 key 含义，只提供通用 Redis 操作。
- `RedisKeyUtil` 负责 key 命名规则。

## 9. `controller` 包说明

`controller` 包负责 HTTP 接口层。

职责：

- 接收请求参数。
- 使用 `@Valid` 做基础参数校验。
- 将 Controller 请求对象转换为 Service 命令对象。
- 调用 Service。
- 将 Service DTO 转换为 Controller Response。
- 不承载核心业务逻辑。

```text
controller
├── advice
├── request
├── response
├── ActivityController.java
├── DrawPrizeController.java
├── FileController.java
├── PrizeController.java
├── UserController.java
└── WinningRecordController.java
```

### 9.1 `controller/advice`

```text
controller/advice
├── GlobalExceptionHandler.java        # 全局异常处理器
├── GlobalResponseAdvice.java          # 统一响应包装器
└── IgnoreResponseWrap.java            # 标记某些 Controller 方法或类不被统一包装
```

`GlobalResponseAdvice`：

- 只处理 `com.wunamor.lotterysystem.controller` 包下 Controller。
- 对 JSON 返回值自动包装为 `CommonResult.success(data)`。
- 已返回 `CommonResult`、`String`、`byte[]`、`Resource`、`ProblemDetail`、`HttpEntity`、`void` 的方法不重复包装。

`GlobalExceptionHandler`：

- `ControllerException` / `ServiceException` 转成 `CommonResult.error(code, message)`。
- `BindException` 转成 400 并拼接字段错误信息。
- 请求体 JSON 解析失败转成 400。
- 上传文件过大转成文件错误码。
- 静态资源不存在返回 404。
- 其他异常返回 500。

### 9.2 `controller/request`

Controller 入参对象。

```text
controller/request
├── ActivityCreateRequest.java         # 创建活动请求：基本信息 + 奖品列表 + 人员列表
├── ActivityDetailRequest.java         # 活动详情查询请求
├── ActivityListRequest.java           # 活动列表分页请求
├── ActivityPrizeRequest.java          # 创建活动时的单个奖品圈选项
├── ActivityUserRequest.java           # 创建活动时的单个参与人员圈选项
├── DrawPrizeRequest.java              # 抽奖提交请求
├── DrawWinnerRequest.java             # 抽奖请求中的单个中奖人
├── EmailCodeRequest.java              # 邮箱验证码请求
├── PrizeCreateRequest.java            # 创建奖品请求，multipart 表单字段
├── PrizeListRequest.java              # 奖品列表分页请求
├── UserLoginRequest.java              # 登录请求，支持密码登录和邮箱验证码登录
├── UserRegisterRequest.java           # 注册请求
└── WinningRecordShowRequest.java      # 中奖记录查询请求
```

### 9.3 `controller/response`

Controller 出参对象。

```text
controller/response
├── ActivityCreateResponse.java        # 创建活动响应
├── ActivityDetailPrizeResponse.java   # 活动详情中的奖品项
├── ActivityDetailResponse.java        # 活动详情响应
├── ActivityDetailUserResponse.java    # 活动详情中的参与人员项
├── ActivityListResponse.java          # 活动列表项
├── DrawPrizeResponse.java             # 抽奖任务提交响应
├── DrawStatusResponse.java            # 抽奖任务状态响应
├── DrawWinnerResponse.java            # 中奖人员响应
├── FileUploadResponse.java            # 图片上传响应
├── PrizeCreateResponse.java           # 创建奖品响应
├── PrizeResponse.java                 # 奖品列表项响应
├── UserListResponse.java              # 人员列表响应
├── UserLoginResponse.java             # 登录响应
├── UserRegisterResponse.java          # 注册响应
└── WinningRecordResponse.java         # 中奖记录响应
```

### 9.4 Controller 文件说明

#### `UserController.java`

```text
路径：/user
```

接口：

```text
POST /user/email-code                  # 发送邮箱验证码，公开接口
POST /user/register                    # 管理员注册用户
POST /user/login                       # 用户登录，返回 JWT
POST /user/reset-password              # 通过邮箱验证码重置密码，公开接口
GET  /user/list                        # 管理员查看人员列表
GET  /user/profile                     # 当前登录用户查看个人资料，敏感字段脱敏
PUT  /user/profile                     # 当前登录用户修改个人资料
```

#### `PrizeController.java`

```text
路径：/prize
```

接口：

```text
POST /prize/create                     # 管理员创建奖品，multipart/form-data
GET  /prize/list                       # 管理员分页查询奖品
```

#### `ActivityController.java`

```text
路径：/activity
```

接口：

```text
POST /activity/create                  # 管理员创建活动，圈选奖品和人员
GET  /activity/list                    # 管理员分页查询活动
GET  /activity/detail                  # 管理员查询活动详情；默认优先读 Redis，fresh=true 强制回源数据库
```

#### `DrawPrizeController.java`

```text
路径：/draw-prize
```

接口：

```text
POST /draw-prize                       # 管理员提交抽奖任务，返回 requestId
GET  /draw-prize/status                # 管理员轮询抽奖任务状态
```

抽奖接口不直接执行抽奖，只提交异步任务。

#### `WinningRecordController.java`

```text
路径：/winning-records
```

接口：

```text
POST /winning-records/show             # 公开分享接口：按 activityId 查询中奖记录，可选 prizeId
```

#### `FileController.java`

```text
路径：/file
```

接口：

```text
POST /file/upload/{scene}              # 管理员上传图片，scene 由 FileSceneEnum 决定
```

## 10. `mapper` 包说明

`mapper` 包负责数据访问，不承载业务规则。

```text
mapper
├── dataobject
├── handler
├── ActivityMapper.java
├── ActivityPrizeMapper.java
├── ActivityUserMapper.java
├── DrawRequestMapper.java
├── PrizeMapper.java
├── UserMapper.java
└── WinningRecordMapper.java
```

### 10.1 `mapper/dataobject`

数据库对象，继承 `BaseDO` 的表通常包含公共字段。

```text
mapper/dataobject
├── BaseDO.java                        # id、gmt_create、gmt_modified 公共字段
├── ActivityDO.java                    # 对应 activity 表
├── ActivityPrizeDO.java               # 对应 activity_prize 表，活动与奖品的关联和状态
├── ActivityUserDO.java                # 对应 activity_user 表，活动与人员的关联和状态
├── DrawRequestDO.java                 # 对应 draw_request 表，抽奖异步任务
├── Encrypt.java                       # 手机号加密值对象，配合 EncryptTypeHandler 使用
├── PrizeDO.java                       # 对应 prize 表
├── UserDO.java                        # 对应 user 表
└── WinningRecordDO.java               # 对应 winning_record 表，中奖快照
```

### 10.2 `mapper/handler`

```text
mapper/handler
└── EncryptTypeHandler.java            # MyBatis TypeHandler，负责手机号加密写入和解密读取
```

### 10.3 Mapper 文件说明

```text
ActivityMapper.java                    # 活动表访问，包含活动行锁查询
ActivityPrizeMapper.java               # 活动奖品关联表访问，包含批量插入
ActivityUserMapper.java                # 活动人员关联表访问，包含批量插入
DrawRequestMapper.java                 # 抽奖异步任务表访问
PrizeMapper.java                       # 奖品表访问
UserMapper.java                        # 用户表访问
WinningRecordMapper.java               # 中奖记录表访问，包含批量插入
```

核心表关系：

```text
user
  └── activity_user
        ├── activity
        └── activity_prize
              └── prize

activity / activity_prize
  └── draw_request

activity + prize + winner
  └── winning_record
```

约束说明：

- `draw_request.id` 同时作为前端轮询的 `requestId`。
- `winning_record` 有 `(winner_id, activity_id)` 唯一键，保证一个用户在一个活动内最多中一个奖品。
- 奖品抽取状态在 `activity_prize.status`，不是 `prize` 表本身。

## 11. `service` 包说明

`service` 包承载业务逻辑。

```text
service
├── command
├── dto
├── draw/state
├── enums
├── impl
├── mq
├── ActivityCacheService.java
├── ActivityService.java
├── DrawPrizeService.java
├── DrawRequestService.java
├── EmailCodeService.java
├── FileService.java
├── PrizeService.java
├── UserService.java
└── WinningRecordService.java
```

Service 层约定：

- 不依赖 Controller 的 request/response 类。
- 入参使用 `service/command`。
- 出参使用 `service/dto`。
- 业务失败抛 `ServiceException`。
- 数据库访问通过 `mapper`。
- 抽奖状态落库通过 `service/draw/state` 及其 `impl` 实现。
- MQ 业务适配通过 `service/mq`。

### 11.1 `service/command`

```text
service/command
├── ActivityCreateCommand.java         # 创建活动命令
├── ActivityListCommand.java           # 活动列表查询命令
├── ActivityPrizeCommand.java          # 创建活动时的奖品命令项
├── ActivityUserCommand.java           # 创建活动时的人员命令项
├── DrawPrizeCommand.java              # 抽奖命令：requestId、activityId、prizeId、winnerIds
├── EmailCodeCommand.java              # 邮箱验证码命令
├── PrizeCreateCommand.java            # 创建奖品命令
├── PrizeListCommand.java              # 奖品列表命令
├── UserLoginCommand.java              # 登录命令
└── UserRegisterCommand.java           # 注册命令
```

### 11.2 `service/dto`

```text
service/dto
├── ActivityCreateDTO.java             # 创建活动结果
├── ActivityDetailDTO.java             # 活动详情快照，可写入 Redis，包含奖品、候选人和中奖名单
├── ActivityDetailPrizeDTO.java        # 活动详情中的奖品快照，list 为中奖名单
├── ActivityDetailUserDTO.java         # 活动详情中的参与人快照
├── ActivityListDTO.java               # 活动列表项
├── DrawPrizeDTO.java                  # 抽奖事务结果：中奖人数、奖品是否抽满、剩余名额
├── DrawRequestDTO.java                # 抽奖任务提交结果
├── DrawStatusDTO.java                 # 抽奖任务状态查询结果，包含奖品剩余名额和是否抽满
├── DrawWinnerDTO.java                 # 中奖人员结果项
├── FileUploadDTO.java                 # 文件上传结果
├── PrizeCreateDTO.java                # 创建奖品结果
├── PrizeDTO.java                      # 奖品列表项
├── UserListDTO.java                   # 人员列表项
├── UserLoginDTO.java                  # 登录结果
├── UserRegisterDTO.java               # 注册结果
└── WinningRecordDTO.java              # 中奖记录查询结果
```

### 11.3 `service/enums`

```text
service/enums
├── ActivityPrizeStatusEnum.java       # 活动奖品状态：PENDING / DRAWN
├── ActivityUserStatusEnum.java        # 活动人员状态：PENDING / WINNER / NOT_WON
├── DrawRequestStatusEnum.java         # 抽奖任务状态：PENDING / PROCESSING / SUCCESS / FAILED
├── FileSceneEnum.java                 # 文件业务场景，决定上传目录
└── UserIdentityEnum.java              # 用户身份，例如管理员、普通用户
```

### 11.4 `service/impl`

```text
service/impl
├── ActivityCacheServiceImpl.java      # 活动详情 Redis 快照缓存实现
├── ActivityServiceImpl.java           # 活动创建、列表、详情；详情快照包含各奖品中奖名单
├── DrawPrizeServiceImpl.java          # 抽奖事务：校验、锁定活动、状态更新、重建缓存、提交后通知
├── DrawRequestServiceImpl.java        # 抽奖任务提交、状态查询、命令编排、补偿扫描
├── EmailCodeServiceImpl.java          # 邮箱验证码生成、发送、校验
├── FileServiceImpl.java               # 本地图片上传、删除
├── PrizeServiceImpl.java              # 奖品创建、奖品列表
├── UserServiceImpl.java               # 注册、登录、验证码、人员列表
└── WinningRecordServiceImpl.java      # 中奖记录查询
```

### 11.5 `service/draw/state`

该包用于承载抽奖状态翻转链。

`state` 根包放接口、上下文、阶段和执行链；`state/impl` 放具体状态更新实现。

```text
service/draw/state
├── DrawContext.java                       # 状态更新链共享上下文
├── DrawStatePhase.java                    # 状态阶段：RECORD、PRIZE、ACTIVITY_USER、ACTIVITY、DRAW_REQUEST
├── DrawStateUpdater.java                  # 状态更新步骤接口，包含 phase、order、shouldExecute、execute
├── DrawStateUpdaterChain.java             # 按 phase + order 执行状态更新步骤
└── impl
    ├── ActivityFinishedUpdater.java       # 没有待抽取奖品时，把活动标记为 FINISHED
    ├── ActivityUserNotWonUpdater.java     # 活动结束时，把剩余 PENDING 人员标记为 NOT_WON
    ├── ActivityUserWinnerUpdater.java     # 把中奖人员从 PENDING 更新为 WINNER
    ├── DrawRequestSuccessUpdater.java     # 把 draw_request 从 PROCESSING 更新为 SUCCESS，并保存中奖快照
    ├── PrizeDrawnUpdater.java             # 统计奖品已中奖人数，抽满后才把 activity_prize 从 PENDING 更新为 DRAWN
    └── WinningRecordUpdater.java          # 批量写入 winning_record
```

设计原则：

```text
DrawPrizeServiceImpl 负责事务边界和主流程编排。
DrawStateUpdaterChain 负责事务内的状态更新步骤排序和执行。
每个 Updater 只负责状态落库，不负责资格校验、缓存清理、MQ 投递。
```

阶段顺序：

```text
RECORD        -> 写中奖记录
PRIZE         -> 统计已中奖人数；抽满后才更新活动奖品状态
ACTIVITY_USER -> 更新活动人员状态
ACTIVITY      -> 判断并更新活动状态；活动结束时收尾未中奖人员
DRAW_REQUEST  -> 更新抽奖任务状态
```

### 11.6 `service/mq`

该包是抽奖业务对 RabbitMQ 的适配层。

```text
service/mq
├── consumer
│   ├── DrawCommandListener.java       # 抽奖命令消费者，只做消息解析和委托
│   └── DrawNotifyListener.java        # 中奖通知消费者，解析通知并发送邮件
├── message
│   ├── DrawCommandMessage.java        # 抽奖命令消息
│   └── DrawNotifyMessage.java         # 中奖通知消息
└── producer
    ├── DrawMessageProducer.java       # 统一 MQ 生产者接口
    └── DrawMessageProducerImpl.java   # RabbitMQ 投递实现
```

职责边界：

- MQ 公共配置在 `common/config/RabbitMqConfig.java`。
- MQ 名称常量在 `common/constants/RabbitMqConstants.java`。
- MQ 业务消息和消费者放在 `service/mq`，不放在 `common`。
- 消费者只做 transport adapter，不承载抽奖主业务规则。
- `DrawCommandListener` 解析消息后委托 `DrawRequestService.handleCommand()`。
- `DrawNotifyListener` 解析通知消息后发邮件。

## 12. 资源目录文件说明

### 12.1 `application.yaml`

`application.yaml` 是项目主配置文件。

主要配置区域：

```text
spring.datasource                      # MySQL 连接
spring.data.redis                      # Redis 连接
spring.rabbitmq                        # RabbitMQ 连接、消费重试、DLQ 基础策略
spring.mail                            # SMTP 邮件配置
lottery.draw.compensation              # 抽奖异步任务补偿配置
lottery.upload                         # 文件上传配置
logging                                # 日志文件配置
mybatis-plus                           # MyBatis-Plus 配置
```

抽奖补偿配置：

```yaml
lottery:
  draw:
    compensation:
      enabled: false
      max-attempts: 3
      pending-delay: 10s
      processing-timeout: 5m
      scan-interval: 30s
```

说明：

- `enabled: false` 是本地默认值，用于避免补偿任务每 30 秒查询 `draw_request`。
- 要验证补偿逻辑时改成 `true`，或者启动时覆盖：

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--lottery.draw.compensation.enabled=true"
```

文件上传配置：

```yaml
lottery:
  upload:
    dir: ${user.dir}/uploads
    url-prefix: /uploads
    max-size: 5MB
    allowed-extensions:
      - jpg
      - jpeg
      - png
      - gif
      - webp
```

### 12.2 `src/main/resources/mapper`

```text
mapper
├── ActivityPrizeMapper.xml            # 批量写入活动奖品关联
├── ActivityUserMapper.xml             # 批量写入活动人员关联
└── WinningRecordMapper.xml            # 批量写入中奖记录
```

这些 XML 中的 SQL 主要服务于批量插入。普通单表 CRUD 优先通过 MyBatis-Plus 基础能力完成。

### 12.3 `src/main/resources/static`

静态页面目录。

```text
static
├── admin.html                         # 后台框架页
├── blogin.html                        # 后台登录页，唯一登录入口
├── login.html                         # 兼容跳转页，自动跳转 blogin.html
├── register.html                      # 用户注册页
├── user-list.html                     # 人员列表页
├── prizes-list.html                   # 奖品列表页
├── create-prizes.html                 # 创建奖品页
├── activities-list.html               # 活动列表页
├── create-activity.html               # 创建活动页
├── draw.html                          # 抽奖页和抽奖任务轮询页
├── profile.html                       # 当前登录用户个人信息编辑页
├── forgot-password.html               # 忘记密码页，通过邮箱验证码重置密码
├── css                                # 页面样式
├── js                                 # 页面脚本
└── pic                                # 页面图片资源
```

`js/auth.js`：

- 读取 `localStorage.user_token`。
- 通过 jQuery `$.ajaxSetup` 给请求自动加 `Authorization: Bearer <token>`。
- 未登录时前端接口请求通常收到 401。

`css`：

```text
base.css                               # 基础样式
login.css                              # 登录页样式
bootstrap.min.css                      # Bootstrap
bootstrap.min.css.map                  # source map
toastr.min.css                         # Toastr 样式
```

`js`：

```text
auth.js                                # 前端登录态和请求头注入
bootstrap.min.js                       # Bootstrap
bootstrap.min.js.map
jquery.min.js                          # jQuery
jquery.validate.min.js                 # 表单校验
toastr.min.js                          # 页面提示
toastr.js.map
```

`pic`：

```text
bg.png                                 # 背景资源
beijing.png                            # 背景资源
defaultPrizeImg.png                    # 默认奖品图片
huodong.png                            # 活动菜单图标
huodong-0.png                          # 活动菜单未选中图标
jiangpin.png                           # 奖品菜单图标
jiangpin-0.png                         # 奖品菜单未选中图标
login-left.png                         # 登录页左侧图
logo.png                               # logo
logout.svg                             # 退出图标
renyuan.png                            # 人员菜单图标
renyuan-0.png                          # 人员菜单未选中图标
```

## 13. SQL 脚本说明

```text
sql
├── draw-async.sql
└── init-admin.sql
```

### 13.1 `sql/draw-async.sql`

用途：

- 创建 `draw_request` 表。
- 为 `winning_record` 增加唯一约束：

```text
uk_w_a_id (winner_id, activity_id)
```

`draw_request` 的核心字段：

```text
id               # 主键，同时作为 requestId
activity_id      # 活动 id
prize_id         # 奖品 id
winner_ids       # 请求中的中奖人员 id 列表，JSON 数组
status           # PENDING / PROCESSING / SUCCESS / FAILED
error_code       # 业务失败错误码
error_message    # 业务失败原因
result_json      # 成功后的中奖人员快照，JSON 数组
retry_count      # 补偿重试次数
```

约束：

```text
idx_status_modified       (status, gmt_modified)
idx_activity_prize        (activity_id, prize_id)
```

### 13.2 `sql/init-admin.sql`

用途：

- 初始化默认管理员账号。
- 仅适合开发环境。

默认账号：

```text
用户名：admin
密码：12345678
邮箱：admin@lottery.com
手机号：13800000000
```

说明：

- `password` 字段存储 SHA-256 后的值。
- `phone_number` 字段存储项目 AES key 加密后的 hex 值。
- 脚本使用 `INSERT IGNORE`，可重复执行。

## 14. 测试目录说明

```text
src/test/java/com/wunamor/lotterysystem
```

### 14.1 根测试

```text
LotterySystemApplicationTests.java     # 验证 Spring Boot 上下文是否能正常加载
LogTest.java                           # 日志相关测试
```

### 14.2 `common` 测试

```text
common/interceptor/LoginInterceptorTest.java
# 验证未登录、token 无效、管理员越权、管理员放行等拦截逻辑

common/pojo/PageResultTest.java
# 验证通用分页结果封装

common/utils/JacksonUtilTest.java
# 验证 JacksonUtil JSON 转换能力
```

### 14.3 `mapper` 测试

```text
mapper/UserMapperTest.java
# 验证用户数据访问，重点关注手机号加密字段读写
```

### 14.4 `service` 测试

```text
service/ActivityCacheServiceImplTest.java
# 验证活动详情 Redis 快照缓存

service/ActivityServiceImplTest.java
# 验证活动创建、活动列表、活动详情

service/DrawPrizeServiceImplTest.java
# 验证抽奖事务：中奖记录、奖品状态、人员状态、活动状态、任务状态

service/DrawRequestServiceImplTest.java
# 验证抽奖任务提交、状态查询、命令编排、补偿开关、补偿重投

service/EmailCodeServiceTest.java
# 验证邮箱验证码；依赖 Redis，部分测试可能发送邮件

service/FileServiceImplTest.java
# 验证图片上传、格式校验、删除逻辑

service/PrizeServiceImplTest.java
# 验证奖品创建和奖品列表

service/WinningRecordServiceImplTest.java
# 验证中奖记录查询
```

### 14.5 `service/draw/state` 测试

```text
service/draw/state/DrawStateUpdaterChainTest.java
# 验证状态更新链：
# 1. 按 phase 排序
# 2. 同 phase 按 order 排序
# 3. 重复 phase + order 被拒绝
# 4. shouldExecute=false 时跳过
```

### 14.6 `service/mq/consumer` 测试

```text
service/mq/consumer/DrawCommandListenerTest.java
# 验证抽奖命令消费者只解析消息并委托 DrawRequestService

service/mq/consumer/DrawNotifyListenerTest.java
# 验证中奖通知消费者发送邮件；邮件失败时拒绝消息进入死信队列
```

## 15. 抽奖异步主链路

### 15.1 前端到提交任务

```text
draw.html
  → POST /draw-prize
  → DrawPrizeController.drawPrize()
  → DrawRequestService.submit()
```

`DrawRequestService.submit()` 负责：

- 做基础请求格式校验。
- 检查奖品剩余名额：已中奖人数 + 未完成任务占用人数 + 本次人数不能超过奖品数量。
- 同一奖品允许连续提交多个单人中奖任务，不再因为存在未完成任务就拒绝。
- 写 `draw_request(PENDING)`。
- 投递抽奖命令消息到 RabbitMQ。
- 返回 `requestId` 给前端。

抽奖页交互：

- 第一次点击只开始滚动，不创建任务；第二次点击才提交一个中奖人。
- 两次有效点击至少间隔 500ms；请求在途期间按钮保持禁用，避免并发。
- 奖品没抽满前停留在当前奖品，抽满后才允许进入下一奖品。
- 活动详情默认优先读 Redis；只有状态异常需要恢复时才传 `fresh=true` 回源数据库。

### 15.2 MQ 命令消费

```text
RabbitMQ draw command queue
  → DrawCommandListener.onMessage()
  → DrawRequestService.handleCommand()
```

`DrawCommandListener` 只负责：

- 解析 JSON。
- 校验消息是否有 `requestId`。
- 把消息委托给 `DrawRequestService.handleCommand()`。

`DrawRequestService.handleCommand()` 负责：

- `PENDING -> PROCESSING` 抢占任务。
- 调用 `DrawPrizeService.drawPrize()`。
- 业务失败写 `FAILED`。
- 技术失败抛出，交给 Spring AMQP 重试、DLQ 和补偿。

### 15.3 抽奖事务

```text
DrawPrizeService.drawPrize()
  → 校验命令
  → 锁定活动
  → 校验奖品
  → 校验奖品顺序
  → 校验剩余中奖名额（已中奖人数 + 本次人数 <= 奖品数量）
  → 校验活动人员状态
  → 校验用户联系方式
  → 构建 DrawContext
  → DrawStateUpdaterChain.update()
  → 注册事务提交后动作
```

抽奖按人中奖：

- `drawPrize()` 允许一次只写一个中奖人，不再要求本次人数等于奖品数量。
- `PrizeDrawnUpdater` 写记录后统计该奖品已中奖人数：未满时奖品保持 `PENDING`，抽满时才更新为 `DRAWN`。
- `DrawPrizeDTO` 返回 `prizeFinished` 和 `remainingCount`，前端据此决定是否继续当前奖品。
- `(winner_id, activity_id)` 唯一键保证一个用户在一个活动内最多中一个奖。

状态更新链：

```text
RECORD        WinningRecordUpdater
PRIZE         PrizeDrawnUpdater
ACTIVITY_USER ActivityUserWinnerUpdater
ACTIVITY      ActivityFinishedUpdater        (order 0)
ACTIVITY      ActivityUserNotWonUpdater      (order 10)
DRAW_REQUEST  DrawRequestSuccessUpdater
```

### 15.4 提交后缓存重建和通知

```text
DrawPrizeServiceImpl.afterCommit()
  → ActivityService.getActivityDetail(activityId, true)
  → 重建活动详情快照并写回 Redis（key: lottery:activity:detail:v3:{id}）
  → DrawMessageProducer.sendNotify()
  → RabbitMQ notify queue
  → DrawNotifyListener
  → MailUtil
```

设计原因：

- 抽奖提交后重建快照，后续查询继续优先命中 Redis，并且能看到最新中奖名单。
- 快照重建失败时兜底删除旧 key，避免返回过期数据。
- 缓存重建和邮件通知不能导致抽奖事务回滚。
- 邮件发送失败不影响抽奖结果。
- 通知消息在抽奖事务提交后投递。

## 16. 补偿任务链路

补偿逻辑在：

```text
service/impl/DrawRequestServiceImpl.java
```

入口：

```java
@Scheduled(fixedDelayString = "#{@drawCompensationProperties.scanInterval.toMillis()}")
public void compensate() {
    if (!compensationProperties.isEnabled()) {
        return;
    }

    failExhaustedTasks();
    republishPendingTasks();
    republishProcessingTasks();
}
```

处理对象：

```text
PENDING
- MQ 投递失败后长期停留在待处理的任务

PROCESSING
- 消费者宕机或处理超时后长期停留在处理中的任务
```

处理策略：

```text
1. 超过最大重试次数的任务标记为 FAILED
2. PENDING 超过 pending-delay 的任务尝试重新投递
3. PROCESSING 超过 processing-timeout 的任务重置为 PENDING 并重新投递
```

## 17. MQ 配置和常量分层

### 17.1 公共配置

```text
common/config/RabbitMqConfig.java
```

负责：

- exchange。
- queue。
- binding。
- dead-letter exchange。
- dead-letter queue。

### 17.2 公共常量

```text
common/constants/RabbitMqConstants.java
```

负责：

- queue 名称。
- exchange 名称。
- routing key。
- DLQ 名称。

### 17.3 业务适配

```text
service/mq/producer
service/mq/message
service/mq/consumer
```

负责：

- 抽奖命令消息。
- 中奖通知消息。
- 抽奖业务生产者。
- 抽奖业务消费者。

## 18. 运行期目录说明

这些目录不是项目源码结构的核心内容，文档不展开每个文件。

```text
target/
# Maven 构建产物。
# 包含 classes、test-classes、surefire-reports、maven-status 等。
# 每次 mvn clean 或 mvn test 都可能变化。

logs/
# 应用运行日志。
# 当前 logging.file.name=logs/springboot.log。

uploads/
# 本地上传文件目录。
# 当前主要存奖品图片。
# 路径形如：uploads/prize/yyyy/MM/dd/uuid.png。
# 数据库只保存访问 URL，例如：/uploads/prize/yyyy/MM/dd/uuid.png。

.idea/
# IntelliJ IDEA 本地工程配置。
# 不属于源码，通常由 .gitignore 忽略。
```

## 19. 分层依赖规则

当前项目推荐的依赖方向：

```text
controller -> service -> mapper -> mysql
             service -> common/utils
             service -> service/draw/state
             service -> service/mq
common/config    -> MQ infrastructure
common/constants -> MQ names
```

不要这样做：

```text
controller 直接写业务规则
service 依赖 controller/request
common 依赖具体抽奖业务
mapper 承载抽奖资格判断
consumer 承载抽奖事务和核心状态流转
状态更新链承载资格校验、缓存清理、邮件发送
```

## 20. 文档维护建议

后续如果新增包或目录，建议按下面顺序更新本文：

1. 更新总体树。
2. 更新对应包的文件说明。
3. 如果影响主链路，更新第 15～17 节。
4. 如果新增配置项，更新第 12.1 节。
5. 如果新增 SQL 表，更新第 10 和第 13 节。




