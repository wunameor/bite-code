# AGENTS.md

本文件是 `lottery-system` 项目面向 AI 编码助手的工作约定。

通用行为规则见仓库根目录：

```text
D:\learn\bite\bite-code\AGENTS.md
```

通用代码规范见 `coding-conventions` skill。

## 项目概览

当前项目是 Spring Boot 抽奖系统，包含：

- 用户登录和 JWT 鉴权。
- 奖品管理和图片上传。
- 活动管理和活动详情缓存。
- 异步抽奖。
- MQ 命令消费和抽奖事务。
- 抽奖状态翻转链。
- 中奖记录和任务状态轮询。
- 中奖通知异步邮件发送。
- 抽奖任务补偿。

主技术栈：

```text
Java 17
Spring Boot 4.0.8
MyBatis-Plus 3.5.18.1
MySQL 8
Redis
RabbitMQ
JWT
静态 HTML + jQuery
```

## 必读文档

修改前优先阅读：

```text
docs/project-structure.md
```

该文档记录目录树、包职责、MQ 分层、状态更新链和主流程。

## 常用命令

编译：

```bash
mvn -q -DskipTests compile
```

全量测试，排除依赖 Redis 的 `EmailCodeServiceTest`：

```bash
mvn -q clean test "-Dtest=!EmailCodeServiceTest"
```

只跑抽奖服务相关测试：

```bash
mvn -q test "-Dtest=DrawPrizeServiceImplTest,DrawRequestServiceImplTest,DrawStateUpdaterChainTest"
```

只跑状态更新链测试：

```bash
mvn -q "-Dtest=DrawStateUpdaterChainTest" test
```

只跑邮箱验证码校验测试：

```bash
mvn -q "-Dtest=EmailCodeServiceTest#verify" test
```

本地启动：

```bash
mvn spring-boot:run
```

E2E 独立端口：

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"
```

注意：

- `EmailCodeServiceTest#send` 可能真实发送邮件，不要默认运行。
- 如果 RabbitMQ 或 Redis 不可用，相关集成测试可能失败。
- 8080 上的旧实例需要重启后才能加载最新代码。

## 目录边界

源码主目录：

```text
src/main/java/com/wunamor/lotterysystem
```

当前主要分层：

```text
LotterySystemApplication.java   # 启动类，启用定时任务
common                          # 公共基础设施
controller                      # HTTP 接口层
mapper                          # 数据访问层
service                         # 业务服务层
```

## 分层规则

### Controller

负责：

- 接收 HTTP 参数。
- 使用 `@Valid` 做格式校验。
- Request 转 Command。
- 调用 Service。
- DTO 转 Response。

禁止：

- 写核心业务规则。
- 直接访问 Mapper。
- 手工拼接统一响应结构。
- 在 Controller 中处理抽奖事务。

### Service

负责：

- 业务规则。
- 事务边界。
- MQ 业务编排。
- Redis 缓存。
- 文件上传等业务动作。

禁止：

- 依赖 `controller/request` 或 `controller/response`。
- 直接返回 Controller Response。
- 在 Service 中拼 HTTP 错误结构。
- 把抽奖状态更新链做成吞异常的组件。

### Mapper

负责：

- 数据库访问。
- DO 与 SQL 映射。

禁止：

- 承载业务规则。
- 做跨服务编排。
- 决定抽奖是否合法。

### common

`common` 只放基础设施能力。

允许：

- `common/config`：Spring / MyBatis / RabbitMQ / Web / 本地配置。
- `common/constants`：公共常量。
- `common/enums`：跨层枚举。
- `common/errorcode`：错误码。
- `common/exception`：异常。
- `common/interceptor`：登录拦截。
- `common/pojo`：统一响应、分页对象。
- `common/utils`：通用工具。

禁止：

- 把抽奖业务消费者放在 `common`。
- 把抽奖消息体放在 `common`。
- 把业务错误判断散落在 `common/utils`。
- 让 `common` 依赖 `service` 或 `controller`。

## MQ 规则

MQ 公共配置：

```text
common/config/RabbitMqConfig.java
```

MQ 名称常量：

```text
common/constants/RabbitMqConstants.java
```

MQ 业务适配：

```text
service/mq/message
service/mq/producer
service/mq/consumer
```

必须保持：

- 消费者只做消息适配和委托。
- 消费者不写抽奖资格判断。
- 消费者不直接更新活动、奖品、人员中奖状态。
- 生产者可被应用服务调用，不承载业务规则。
- 消息体保持简单 DTO，不塞业务逻辑。

当前命令消息链路：

```text
DrawCommandListener
  -> DrawRequestService.handleCommand
  -> DrawPrizeService.drawPrize
```

当前通知消息链路：

```text
DrawPrizeServiceImpl afterCommit
  -> DrawMessageProducer.sendNotify
  -> DrawNotifyListener
  -> MailUtil
```

## 抽奖主流程规则

抽奖入口：

```text
service/impl/DrawPrizeServiceImpl.java
```

`drawPrize()` 必须负责：

- 基础命令校验。
- 锁定活动行。
- 校验活动和奖品状态。
- 校验奖品顺序。
- 校验中奖人数。
- 校验活动人员状态和联系方式。
- 构建 `DrawContext`。
- 调用 `DrawStateUpdaterChain.update()`。
- 注册事务提交后动作。
- 返回抽奖结果。

事务边界：

```java
@Transactional(rollbackFor = Exception.class)
public DrawPrizeDTO drawPrize(DrawPrizeCommand command)
```

不要在状态更新步骤中另开事务。

## 状态更新链规则

状态更新链位于：

```text
service/draw/state
service/draw/state/impl

state 根包放接口、上下文、阶段和执行链；
state/impl 放具体状态更新实现。
```

阶段：

```text
RECORD        写中奖记录
PRIZE         更新活动奖品状态
ACTIVITY_USER 更新活动人员状态
ACTIVITY      判断并更新活动状态；活动结束时收尾未中奖人员
DRAW_REQUEST  更新抽奖异步任务状态
```

接口：

```java
DrawStatePhase phase();
int order();
boolean shouldExecute(DrawContext context);
void execute(DrawContext context);
```

规则：

- `phase` 表示大阶段依赖。
- `order` 表示同阶段内顺序。
- 同 `phase` + 同 `order` 会被启动校验拒绝。
- 状态更新器只做事务内状态落库。
- 状态更新器不要做资格校验。
- 状态更新器不要发送 MQ。
- 状态更新器不要清缓存。
- 状态更新器不要 catch 异常后继续执行后续状态。
- 状态更新失败必须抛出，让抽奖事务整体回滚。

## 异步任务规则

异步任务服务：

```text
service/DrawRequestService.java
service/impl/DrawRequestServiceImpl.java
```

负责：

- `submit()`：校验请求格式、拒绝同奖品未完成任务、写 `draw_request(PENDING)`、投递命令消息。
- `getStatus()`：查询任务状态和结果。
- `handleCommand()`：消费命令时抢占任务、调用抽奖事务、处理业务失败。
- `markProcessing()`：`PENDING -> PROCESSING`。
- `markFailed()`：写业务失败。
- `compensate()`：定时补偿。

任务状态：

```text
PENDING
PROCESSING
SUCCESS
FAILED
```

补偿开关：

```yaml
lottery:
  draw:
    compensation:
      enabled: false
```

本地默认关闭补偿扫描，避免每 30 秒查询 `draw_request`。

如果测试 MQ 投递失败、超时重投或补偿重投，需要临时开启：

```yaml
enabled: true
```

## 数据库约定

DO 包：

```text
mapper/dataobject
```

公共字段：

```text
BaseDO：id、gmtCreate、gmtModified
```

核心表：

```text
user
prize
activity
activity_prize
activity_user
winning_record
draw_request
```

重要约束：

- `draw_request.id` 同时作为 `requestId`。
- `winning_record` 有 `(winner_id, activity_id)` 唯一键。
- 一个用户在一个活动内最多中一个奖品。
- 手机号通过 `EncryptTypeHandler` 加密存储。
- 图片只保存相对 URL，例如 `/uploads/prize/yyyy/MM/dd/xxx.png`。

## 鉴权约定

JWT token 位于请求头：

```text
Authorization: Bearer <token>
```

也兼容：

```text
token
user_token
```

公开接口：

```text
/user/login
/user/email-code
静态资源
```

管理员接口：

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

未登录返回 401，非管理员访问管理员接口返回 403。

## 文件上传约定

统一走：

```text
FileService.uploadImage(FileSceneEnum scene, MultipartFile file)
```

存储目录：

```text
uploads/{scene}/yyyy/MM/dd/{uuid}.{ext}
```

数据库只存 URL：

```text
/uploads/{scene}/yyyy/MM/dd/{uuid}.{ext}
```

新增场景时在：

```text
service/enums/FileSceneEnum.java
```

添加枚举值。

## 修改检查清单

改代码前：

1. 先确认改动属于哪一层。
2. 先读相关类和对应测试。
3. 不移动不认识的目录。
4. 不顺手重构无关模块。

改代码后：

1. 至少跑编译。
2. 跑相关测试。
3. 涉及主流程时跑：

```bash
mvn -q clean test "-Dtest=!EmailCodeServiceTest"
```

4. 涉及文档结构、包职责、MQ、状态更新链时同步更新：

```text
docs/project-structure.md
AGENTS.md
```

## 禁止事项

- 不要把 MQ 消费者放回 `common/mq`。
- 不要把业务常量放在普通 `config` 包。
- 不要在消费者里直接完成抽奖事务。
- 不要把状态更新链做成可吞异常的链。
- 不要让一个 Draw 流程重新膨胀出过多接口。
- 不要新增不必要的 Service 接口，除非职责确实分离。
- 不要运行会真实发送邮件的测试，除非用户明确要求。
- 不要提交密码、token、密钥、邮箱授权码。
