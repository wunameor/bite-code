# AGENTS.md

本文件是 `D:\learn\bite\bite-code` 仓库级 AI 规则入口。

本仓库是多项目学习/实验仓库，包含多个不同技术方向。这里只写通用规则；具体项目规则优先读取子项目自己的 `AGENTS.md`。

## 基本原则

- 先定位任务属于哪个子项目，再读取该子项目的 README、AGENTS、docs 和代码。
- 不跨项目修改文件，除非用户明确要求。
- 不删除、覆盖或回退不认识的用户改动。
- 不编造文件、命令、API、依赖、测试结果或外部事实。
- 不确定时先问，不要直接实施有破坏性的改动。
- 回答语言跟随用户。
- 代码、命令、路径、配置项保持原样。

## 子项目规则优先级

如果当前目录存在更近的 `AGENTS.md`，优先按更近的文档执行。

例如：

```text
D:\learn\bite\bite-code\AGENTS.md
D:\learn\bite\bite-code\project\lottery-system\AGENTS.md
```

修改 `lottery-system` 时，应以：

```text
project/lottery-system/AGENTS.md
```

作为项目规则主入口。

## 当前主要子项目

```text
project/lottery-system       # Spring Boot 抽奖系统
RabbitMQ                     # RabbitMQ 示例
Redis                        # Redis 示例
Java118                      # Java 学习和测试练习
python_basic                 # Python 基础练习
LangChain-LangGraph          # LangChain / LangGraph 示例
```

## 修改 Java/Spring 项目时

优先读取：

```text
项目 README.md
项目 AGENTS.md
docs/project-structure.md，如果存在
相关源码
相关测试
```

改完后至少执行：

```bash
# 在对应 Maven 项目根目录
mvn -q -DskipTests compile
```

如果影响主流程，再跑相关测试。涉及外部服务时，不要假设 MySQL、Redis、RabbitMQ、邮箱一定可用；先确认环境。

## 敏感信息规则

- 不提交密钥、token、密码、邮箱授权码、数据库连接串等敏感信息。
- 配置示例使用占位符或环境变量。
- 文档中不要记录真实凭据。

## 文档维护规则

- `README.md` 面向用户：项目是什么、怎么启动、怎么配置、怎么用。
- `AGENTS.md` 面向 AI：边界、规则、禁止事项、测试方式。
- `docs/project-structure.md` 面向结构：目录、包职责、主流程。

三者避免重复维护同一细节。
