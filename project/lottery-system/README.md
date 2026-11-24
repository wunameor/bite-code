# lottery-system 抽奖系统

`lottery-system` 是一个基于 Spring Boot 的抽奖系统，支持用户登录、奖品管理、活动管理、异步抽奖、中奖记录查询和中奖邮件通知。

## 核心功能

- 用户登录：支持用户名/邮箱 + 密码登录，也支持邮箱验证码登录。
- 管理员注册用户和查看人员列表。
- 奖品创建、奖品图片上传、奖品列表查询。
- 活动创建：为活动圈选奖品和参与人员。
- 活动列表和活动详情查询。
- 异步抽奖：提交抽奖请求后返回 `requestId`，后台通过 MQ 执行抽奖。
- 抽奖结果轮询：前端通过 `/draw-prize/status` 查询最终状态和中奖名单。
- 中奖记录查询。
- 中奖通知：抽奖事务提交后发送通知消息，由 MQ 消费者异步发邮件。

## 技术栈

```text
Java 17
Spring Boot 4.0.8
MySQL 8
MyBatis-Plus 3.5.18.1
Redis
RabbitMQ
JWT
静态 HTML + jQuery + Bootstrap
Docker Compose（云服务器部署，应用在服务器上构建）
```

## 环境要求

### 必需

- JDK 17。
- Maven。
- MySQL 8。
- Redis。
- RabbitMQ。

### 可选

如果需要使用邮箱验证码和中奖邮件，需要配置 SMTP 邮箱账号和授权码。

### 容器部署

不使用本机 JDK / Maven / MySQL 时，只需要：

```text
Docker Engine
Docker Compose v2
```

详见「Docker Compose 部署（云服务器）」。

## 配置说明

配置按环境拆成三份：

```text
src/main/resources/application.yaml         # 公共配置：邮件、上传、MyBatis、日志滚动
src/main/resources/application-local.yaml   # 本地开发：127.0.0.1，打印 SQL，补偿默认关闭
src/main/resources/application-cloud.yaml   # 云服务器：容器服务名，关闭 SQL 日志，补偿默认开启
```

默认启用 `local`。容器里由 compose 注入 `SPRING_PROFILES_ACTIVE=cloud`，不需要改代码。

### 数据库

本地默认连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3306}/${DB_NAME:lottery_system}?characterEncoding=utf8&useSSL=true
    username: ${DB_USERNAME:root}
    password: "123456"
```

请先确保 MySQL 中存在数据库：

```text
lottery_system
```

当前仓库内的 SQL 脚本：

```text
sql/schema.sql             # 完整建表，只创建缺失的表，可重复执行
sql/demo-data.sql          # 演示数据：20 个普通用户、6 个奖品、3 个活动、15 条中奖记录
sql/reset-demo-data.sql    # 重置用：删除普通用户和业务表，管理员不动
sql/verify-demo-data.sql   # 校验用：结果为空表示演示数据有效
sql/draw-async.sql         # 历史库升级脚本：补 draw_request 表和中奖唯一约束
sql/init-admin.sql         # 仅本地开发：固定密码 admin/12345678，不要在服务器上使用
sql/lottery_system.sql     # 早期整库导出，含 DROP DATABASE，仅留档，不要用于重置
```

说明：

- 新项目初始化：`mysql -u root -p -D lottery_system < sql/schema.sql`。
- 云服务器首次部署由 MySQL 容器自动执行 `sql/schema.sql`。
- 重置演示数据统一使用 `deploy/reset-demo-data.sh`，不要用 `lottery_system.sql`。

### Redis

本地默认连接：

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:127.0.0.1}
      port: ${REDIS_PORT:6379}
```

### RabbitMQ

默认连接本地 RabbitMQ：

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:127.0.0.1}
    port: ${RABBITMQ_PORT:5672}
    username: ${RABBITMQ_USERNAME:lottery}
    password: ${RABBITMQ_PASSWORD:lottery}
    virtual-host: ${RABBITMQ_VHOST:lottery}
```

如果 RabbitMQ 不在本地，可以通过环境变量覆盖：

```bash
RABBITMQ_HOST=your-host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your-user
RABBITMQ_PASSWORD=your-password
RABBITMQ_VHOST=your-vhost
```

### 邮箱

邮箱配置位于：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 465
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
```

启动前建议通过环境变量配置：

```bash
MAIL_USERNAME=你的邮箱
MAIL_PASSWORD=你的授权码
```

## 快速启动

### 1. 启动依赖服务

确保以下服务可访问：

```text
MySQL: 127.0.0.1:3306
Redis: 127.0.0.1:6379
RabbitMQ: 127.0.0.1:5672
```

### 2. 初始化数据库

建议至少执行：

```bash
mysql -u root -p lottery_system < sql/draw-async.sql
mysql -u root -p lottery_system < sql/init-admin.sql
```

### 3. 启动项目

默认端口：

```text
8080
```

命令：

```bash
mvn spring-boot:run
```

如果你需要使用独立端口，例如 E2E 测试使用 `18080`：

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"
```

## 管理员账号

### 本地开发

`sql/init-admin.sql` 创建的是开发账号，仅适合本机：

```text
用户名：admin
密码：12345678
邮箱：admin@lottery.com
手机号：13800000000
```

### 云服务器

服务器上不要执行 `init-admin.sql`。首次部署后用 `deploy/bootstrap-admin.sh` 创建管理员，密码由你自己提供，只以 SHA-256 摘要写入数据库：

```bash
ADMIN_NAME=admin \
ADMIN_EMAIL=you@example.com \
ADMIN_PHONE=13800000000 \
ADMIN_PASSWORD='你的密码' \
  ./deploy/bootstrap-admin.sh
```

脚本只在「数据库中还没有任何管理员」时插入。已经存在管理员时直接跳过，不会重置密码。密码规则与后端一致：6-12 位字母或数字。

## Docker Compose 部署（云服务器）

对外只暴露应用端口 `26000`，MySQL、Redis、RabbitMQ 只在容器内部网络里，不映射到宿主机。

### 1. 一条命令部署

仓库是 monorepo，脚本只检出 `project/lottery-system`，并把它固定映射到 `/www/bite/lottery-system`：

```bash
bash <(curl -fsSL https://raw.githubusercontent.com/wunameor/bite-code/master/project/lottery-system/deploy/install-server.sh)
```

脚本会依次完成：

1. 稀疏检出仓库到 `/var/lib/lottery-system/repo`，不下载其他子项目；
2. 创建软链接 `/www/bite/lottery-system` 指向 `project/lottery-system`；
3. 生成 `.env`（随机密码 + `JWT_SECRET`，权限 600；已存在时绝不覆盖）；
4. 构建镜像、启动 MySQL / Redis / RabbitMQ / app，并等待健康检查；
5. 首次部署时交互创建管理员，并导入演示数据。

目录关系：

```text
/www/bite/lottery-system        -> /var/lib/lottery-system/repo/project/lottery-system
/var/lib/lottery-system/repo    # 稀疏检出的 monorepo，只下载 lottery-system 子目录
```

`/www/bite/lottery-system` 是软链接，真正的代码在 `/var/lib/lottery-system/repo`。缓存目录可以删掉重建，脚本会先备份再恢复 `.env`；只是重建期间软链接会短暂失效。

不想直接执行远程脚本时，可以先下载检查：

```bash
curl -fsSL https://raw.githubusercontent.com/wunameor/bite-code/master/project/lottery-system/deploy/install-server.sh -o /tmp/install-server.sh
less /tmp/install-server.sh
bash /tmp/install-server.sh
```

服务器访问 GitHub 较慢时，改走 Gitee 镜像（脚本会从 Gitee 检出代码）：

```bash
REPO_URL=https://gitee.com/wunameor/bite-code.git \
  bash <(curl -fsSL https://gitee.com/wunameor/bite-code/raw/master/project/lottery-system/deploy/install-server.sh)
```

脚本首次生成的 `.env` 已经在 `/www/bite/lottery-system/.env`，需要调整端口或邮件配置时直接编辑它：

```text
MYSQL_ROOT_PASSWORD   MySQL root 密码（自动生成）
DB_PASSWORD           应用连接 MySQL 的密码（自动生成）
RABBITMQ_PASSWORD     RabbitMQ 密码（自动生成）
JWT_SECRET            JWT 密钥（自动生成，Base64，解码后 32 字节）
APP_PORT              公网端口，默认 26000
MAIL_USERNAME         可选，中奖邮件发件邮箱
MAIL_PASSWORD         可选，邮箱授权码
```

`JWT_SECRET` 为空时容器会启动失败，避免使用仓库里的默认密钥。

### 2. 手动构建和启动（可选）

`install-server.sh` 已经自动完成构建和启动。需要单独操作时，在 `/www/bite/lottery-system` 执行：

```bash
docker compose build app
docker compose up -d
```

Maven 构建在容器内完成，不需要在服务器装 JDK，也不需要上传 jar。依赖下载走 `deploy/maven-settings.xml` 里的阿里云镜像，Maven 仓库使用 BuildKit 缓存挂载，第二次构建不会重复下载。

### 3. 初始化管理员和演示数据（可选）

首次部署由 `deploy-server.sh` 交互完成。单独执行时：

```bash
ADMIN_EMAIL=you@example.com ADMIN_PHONE=13800000000 ./deploy/bootstrap-admin.sh
./deploy/reset-demo-data.sh --yes
```

访问：

```text
http://<服务器IP>:26000/blogin.html
```

### 4. 常用命令

以下命令都在 `/www/bite/lottery-system` 执行：

```bash
docker compose ps                       # 查看状态和健康检查
docker compose logs -f app              # 应用日志
docker compose restart app              # 只重启应用
```

拉取代码、重新构建并上线：

```bash
cd /www/bite/lottery-system
./deploy/install-server.sh
```

`install-server.sh` 会更新 `/var/lib/lottery-system/repo` 缓存，再调用 `deploy-server.sh`。已有 `.env`、管理员和业务数据不会被覆盖；需要重置演示数据时：

```bash
./deploy/install-server.sh --reset-demo
```

也可以直接在软链接目录里操作 Git，效果相同：

```bash
cd /www/bite/lottery-system
git pull --ff-only
./deploy/deploy-server.sh
```

## 定期重置演示数据

一条命令即可把系统恢复到演示初始状态：

```bash
./deploy/reset-demo-data.sh --yes
```

这条命令会：

1. 检查存在管理员，缺失就中止（不会创建默认密码账号）。
2. 停止 `app` 容器，避免重置期间写入。
3. 删除普通用户，重建业务表，导入 `sql/demo-data.sql`。
4. 清理 Redis 活动详情缓存和 RabbitMQ 里残留的抽奖消息。
5. 执行 `sql/verify-demo-data.sql` 校验，通过后重新启动 `app`。

管理员的 id、姓名、邮箱、手机号密文、密码摘要、更新时间全部保留；重置不会再出现 `12345678` 这类默认密码。校验失败时 `app` 保持停止，方便排查。

每天凌晨 4 点自动重置：

```cron
0 4 * * * cd /www/bite/lottery-system && ./deploy/reset-demo-data.sh --yes >> /var/log/lottery-reset.log 2>&1
```

运行脚本的账号需要在 `docker` 组里。

### 演示数据内容

```text
普通用户    20 人（id 9001-9020，手机号以项目 AES key 加密存储）
奖品        6 个，图片为项目内相对路径 /pic/demo/*.svg
活动 201    进行中，6 个奖项全部待抽取，可直接现场演示
活动 202    已结束，13 条中奖记录，用于演示中奖名单和分享链接
活动 203    进行中，二等奖已抽完、三等奖待抽取，用于演示中途状态
```

奖品图片放在：

```text
src/main/resources/static/pic/demo
```

数据库只保存相对 URL，图片随项目一起打包，不依赖 `uploads/` 目录，删除项目目录不会留下其他环境的文件。

## 前端页面入口

静态页面位于：

```text
src/main/resources/static
```

常用入口：

```text
http://localhost:8080/blogin.html
http://localhost:8080/admin.html
http://localhost:8080/register.html
http://localhost:8080/user-list.html
http://localhost:8080/prizes-list.html
http://localhost:8080/create-prizes.html
http://localhost:8080/activities-list.html
http://localhost:8080/create-activity.html
http://localhost:8080/draw.html
http://localhost:8080/profile.html
http://localhost:8080/forgot-password.html
```

说明：

- `blogin.html` 是唯一登录入口，只允许管理员账号登录。
- `login.html` 仅保留为兼容跳转页，访问后会自动跳转到 `blogin.html`。
- 普通用户由管理员在后台创建，只作为活动参与人员，不提供前台登录入口。
- `admin.html` 右上角显示当前登录昵称，并通过下拉框进入个人信息和退出登录。
- `forgot-password.html` 通过邮箱验证码重置密码。

## 主要接口

所有业务接口返回统一结构：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

### 用户接口

```text
POST /user/email-code
POST /user/register
POST /user/login
POST /user/reset-password
GET  /user/list
GET  /user/profile
PUT  /user/profile
```

说明：

- `/user/login`、`/user/email-code` 和 `/user/reset-password` 是公开接口。
- `/user/reset-password` 使用 `RESET_PASSWORD` 场景的邮箱验证码。
- `/winning-records/show` 也是公开接口，用于中奖名单分享链接，只读且无需登录。
- `/user/register` 和 `/user/list` 仅管理员可访问。
- `/user/profile` 查询和修改当前登录用户资料，登录用户即可访问。
- `/user/profile` 返回的邮箱和手机号是脱敏值；前端提交空值或带 `*` 的值表示不修改。

### 奖品接口

```text
POST /prize/create
GET  /prize/list
```

说明：

- `/prize/create` 使用 `multipart/form-data`。
- 奖品接口仅管理员可访问。

### 活动接口

```text
POST /activity/create
GET  /activity/list
GET  /activity/detail
```

说明：

- 活动接口仅管理员可访问。
- `/activity/detail` 默认优先读取 Redis 中的活动详情快照。
- 抽奖页正常打开不传 `fresh`；只有前端状态异常、需要恢复时才传 `fresh=true` 回源数据库。
- 抽奖提交后会重建活动详情快照并写回 Redis，后续查询继续命中缓存。

### 抽奖接口

```text
POST /draw-prize
GET  /draw-prize/status?requestId=xxx
```

抽奖流程：

```text
1. 前端第一次点击只开始滚动，不提交抽奖请求。
2. 前端第二次点击停止滚动，当前显示的人作为中奖人提交。
3. 后端检查奖品剩余名额：已中奖人数 + 未完成任务人数 + 本次人数 <= 奖品数量。
4. 后端写 draw_request(PENDING)，返回 requestId。
5. 后端发送抽奖命令消息到 RabbitMQ。
6. MQ 消费者执行抽奖事务。
7. 前端轮询 /draw-prize/status，确认后再继续下一次。
```

抽奖约束：

- 一等奖、二等奖、三等奖必须按顺序抽取。
- 一个用户在一个活动内最多中一个奖品。
- 同一奖品支持分多次抽取，每次抽一个人；奖品未抽满前保持 `PENDING`。
- 两次有效点击至少间隔 500ms；请求在途期间按钮保持禁用，避免并发任务。
- 抽奖页字号和奖品图按原设计的约 67% 展示，中奖名单一行一个人名。

### 中奖记录接口

```text
POST /winning-records/show
```

说明：

- 公开接口，用于中奖名单分享链接，不要求登录；只读查询。
- 可按 `activityId` 查询，可选 `prizeId`。

## 测试命令

跑全量测试，排除依赖 Redis 的 `EmailCodeServiceTest`：

```bash
mvn -q clean test "-Dtest=!EmailCodeServiceTest"
```

只跑抽奖相关测试：

```bash
mvn -q test "-Dtest=DrawPrizeServiceImplTest,DrawRequestServiceImplTest,DrawStateUpdaterChainTest"
```

只跑邮箱验证码校验测试：

```bash
mvn -q "-Dtest=EmailCodeServiceTest#verify" test
```

注意：

- `EmailCodeServiceTest#send` 可能会真实发送邮件。
- 不确定邮箱配置时，不要随意运行会发送邮件的测试。

## 常见问题

### 端口被占用

`8080` 上如果已有旧实例，请先停止，再启动新实例。

### RabbitMQ 连不上

检查：

```text
RABBITMQ_HOST
RABBITMQ_PORT
RABBITMQ_USERNAME
RABBITMQ_PASSWORD
RABBITMQ_VHOST
```

本地测试时可以通过 SSH 端口转发到：

```text
127.0.0.1:5672
```

### Redis 连不上

检查：

```text
spring.data.redis.host
spring.data.redis.port
```

如果通过 SSH 转发，可连接到：

```text
127.0.0.1:6379
```

### 补偿任务一直在打印 SQL

抽奖异步补偿任务默认用于处理 `PENDING` / `PROCESSING` 超时任务。

当前本地配置默认关闭补偿扫描：

```yaml
lottery:
  draw:
    compensation:
      enabled: false
```

如果需要验证异步投递失败后的补偿逻辑，请临时改为：

```yaml
enabled: true
```

或启动时覆盖：

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--lottery.draw.compensation.enabled=true"
```

### 抽奖页面没有显示最终结果

抽奖是异步任务。提交 `/draw-prize` 后需要轮询：

```text
GET /draw-prize/status?requestId=xxx
```

只有状态为 `SUCCESS` 时才表示抽奖完成。

## 更多文档

项目结构和包职责说明：

```text
docs/project-structure.md
```

AI 修改项目规则：

```text
AGENTS.md
```
