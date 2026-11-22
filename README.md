# bite-code

这是一个学习和实验仓库，包含多个技术方向的示例、练习和项目。

## 主要内容

```text
project/lottery-system       # Spring Boot 抽奖系统
RabbitMQ                     # RabbitMQ 学习和示例
Redis                        # Redis 学习和示例
Java118                      # Java 基础、JavaEE、测试练习
python_basic                 # Python 基础练习
LangChain-LangGraph          # LangChain / LangGraph 示例
```

## 当前重点项目

### lottery-system

目录：

```text
project/lottery-system
```

说明：

- Spring Boot 抽奖系统。
- 包含用户登录、奖品管理、活动管理、异步抽奖、中奖记录、MQ 通知和补偿任务。

文档：

```text
project/lottery-system/README.md
project/lottery-system/AGENTS.md
project/lottery-system/docs/project-structure.md
```

快速进入项目：

```bash
cd project/lottery-system
mvn spring-boot:run
```

测试：

```bash
mvn -q clean test "-Dtest=!EmailCodeServiceTest"
```

## 其他目录说明

### RabbitMQ

RabbitMQ 相关学习和示例目录。

```text
RabbitMQ/quick-start
RabbitMQ/spring-boot-rabbitmq
RabbitMQ/rabbit-extensions
RabbitMQ/rabbit-communication
```

### Redis

Redis 相关学习和示例目录。

```text
Redis/basic/redis-spring-boot-demo
```

### Java118

Java 基础、JavaEE、Web 测试、博客测试等练习目录。

### python_basic

Python 基础练习目录。

### LangChain-LangGraph

LangChain / LangGraph 示例和练习目录。

## 给 AI 的入口

本仓库根目录的 `AGENTS.md` 只描述仓库级通用规则。

具体项目规则优先读取子项目自己的 `AGENTS.md`，例如：

```text
project/lottery-system/AGENTS.md
```
