#!/usr/bin/env bash
# 一键部署：准备 .env、构建镜像、启动服务、创建管理员、按需导入演示数据。
#
# 用法（在项目根目录执行）：
#   ./deploy/deploy-server.sh                 # 首次部署或日常更新
#   ./deploy/deploy-server.sh --reset-demo    # 顺带重置演示数据
#   ./deploy/deploy-server.sh --no-demo       # 不导入演示数据
#   ./deploy/deploy-server.sh --yes           # 非交互；管理员信息从环境变量读取
#
# 非交互示例：
#   ADMIN_EMAIL=you@example.com ADMIN_PHONE=13800000000 ADMIN_PASSWORD='YourPass123' \
#     ./deploy/deploy-server.sh --yes
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

RESET_DEMO=0
NO_DEMO=0
ASSUME_YES=0

usage() {
    cat <<'EOF'
用法: ./deploy/deploy-server.sh [选项]

选项:
  --reset-demo   部署完成后重置演示数据（会删除普通用户和业务数据，管理员保留）
  --no-demo      不导入演示数据
  --yes, -y      非交互模式；创建管理员时从 ADMIN_EMAIL/ADMIN_PHONE/ADMIN_PASSWORD 读取
  -h, --help     显示帮助

可用环境变量:
  ADMIN_NAME      管理员用户名，默认 admin
  ADMIN_EMAIL     管理员邮箱
  ADMIN_PHONE     管理员手机号
  ADMIN_PASSWORD  管理员密码（6-12 位字母数字）
EOF
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --reset-demo) RESET_DEMO=1; shift ;;
        --no-demo) NO_DEMO=1; shift ;;
        --yes|-y) ASSUME_YES=1; shift ;;
        -h|--help) usage; exit 0 ;;
        *) echo "未知参数：$1" >&2; usage >&2; exit 1 ;;
    esac
done

if [[ "$RESET_DEMO" == "1" && "$NO_DEMO" == "1" ]]; then
    echo "--reset-demo 和 --no-demo 不能同时使用" >&2
    exit 1
fi

require_cmd() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "缺少命令：$1" >&2
        exit 1
    fi
}

require_cmd docker
if ! docker compose version >/dev/null 2>&1; then
    echo "需要 Docker Compose v2（docker compose）" >&2
    exit 1
fi
if ! docker info >/dev/null 2>&1; then
    echo "Docker 守护进程不可用，请先启动 Docker" >&2
    exit 1
fi

random_hex() {
    if command -v openssl >/dev/null 2>&1; then
        openssl rand -hex 24
    else
        od -An -N24 -tx1 /dev/urandom | tr -d ' \n'
    fi
}

random_jwt() {
    if command -v openssl >/dev/null 2>&1; then
        openssl rand -base64 32
    else
        head -c 32 /dev/urandom | base64 | tr -d '\n'
    fi
}

# 首次部署生成独立的 .env；已存在时绝不覆盖
if [[ ! -f .env ]]; then
    if [[ ! -f .env.example ]]; then
        echo "缺少 .env.example，无法生成 .env" >&2
        exit 1
    fi
    cp .env.example .env
    sed -i 's/\r$//' .env
    sed -i "s|^MYSQL_ROOT_PASSWORD=.*|MYSQL_ROOT_PASSWORD=$(random_hex)|" .env
    sed -i "s|^DB_PASSWORD=.*|DB_PASSWORD=$(random_hex)|" .env
    sed -i "s|^RABBITMQ_PASSWORD=.*|RABBITMQ_PASSWORD=$(random_hex)|" .env
    sed -i "s|^JWT_SECRET=.*|JWT_SECRET=$(random_jwt)|" .env
    chmod 600 .env
    echo "[deploy] 已生成 .env：随机密码和 JWT_SECRET 已写入，权限 600"
else
    chmod 600 .env 2>/dev/null || true
    echo "[deploy] 使用已有 .env"
fi

# 防止把未修改的模板当成正式配置启动
if grep -qE 'change-me|replace-with-openssl' .env; then
    echo ".env 仍是 .env.example 的占位值，请先修改；或删除 .env 让脚本重新生成" >&2
    exit 1
fi

for key in MYSQL_ROOT_PASSWORD DB_PASSWORD RABBITMQ_PASSWORD JWT_SECRET; do
    if ! grep -qE "^${key}=.+" .env; then
        echo ".env 缺少必填项：$key" >&2
        exit 1
    fi
done

echo "[deploy] 构建应用镜像"
docker compose build app

echo "[deploy] 启动服务"
docker compose up -d

wait_healthy() {
    local service="$1"
    local timeout="${2:-300}"
    local elapsed=0
    local container_id status

    echo "[deploy] 等待 $service 就绪（最多 ${timeout}s）"
    while (( elapsed < timeout )); do
        container_id="$(docker compose ps -q "$service" 2>/dev/null || true)"
        if [[ -n "$container_id" ]]; then
            status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container_id" 2>/dev/null || true)"
            if [[ "$status" == "healthy" ]]; then
                return 0
            fi
            if [[ "$status" == "exited" || "$status" == "dead" ]]; then
                break
            fi
        fi
        sleep 3
        elapsed=$((elapsed + 3))
    done

    echo "[deploy] $service 未在 ${timeout}s 内就绪，最近日志：" >&2
    docker compose logs --tail 50 "$service" >&2 || true
    exit 1
}

wait_healthy mysql 180
wait_healthy app 300

# 所有 SQL 通过容器内 root 账号执行，密码不落在宿主机命令行
mysql_exec() {
    docker compose exec -T mysql sh -c \
        'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql -N -B -uroot -D "$MYSQL_DATABASE"'
}

echo "[deploy] 确保表结构存在"
mysql_exec < sql/schema.sql

ADMIN_COUNT="$(mysql_exec <<'SQL'
SELECT COUNT(*) FROM `user` WHERE `identity` = '0';
SQL
)"
FIRST_DEPLOY=0

if [[ "$ADMIN_COUNT" -lt 1 ]]; then
    FIRST_DEPLOY=1
    ADMIN_NAME="${ADMIN_NAME:-admin}"
    ADMIN_EMAIL="${ADMIN_EMAIL:-}"
    ADMIN_PHONE="${ADMIN_PHONE:-}"
    ADMIN_PASSWORD="${ADMIN_PASSWORD:-}"

    if [[ -z "$ADMIN_EMAIL" || -z "$ADMIN_PHONE" || -z "$ADMIN_PASSWORD" ]]; then
        if [[ "$ASSUME_YES" == "1" || ! -t 0 ]]; then
            echo "非交互模式需要设置 ADMIN_EMAIL、ADMIN_PHONE、ADMIN_PASSWORD" >&2
            exit 1
        fi
    fi
    if [[ -z "$ADMIN_EMAIL" ]]; then
        read -r -p "管理员邮箱（小写）: " ADMIN_EMAIL
    fi
    if [[ -z "$ADMIN_PHONE" ]]; then
        read -r -p "管理员手机号: " ADMIN_PHONE
    fi
    if [[ -z "$ADMIN_PASSWORD" ]]; then
        read -r -s -p "管理员密码（6-12 位字母数字）: " ADMIN_PASSWORD
        echo
        read -r -s -p "再次输入密码: " ADMIN_PASSWORD_CONFIRM
        echo
        if [[ "$ADMIN_PASSWORD" != "$ADMIN_PASSWORD_CONFIRM" ]]; then
            echo "两次输入的密码不一致" >&2
            exit 1
        fi
    fi

    export ADMIN_NAME ADMIN_EMAIL ADMIN_PHONE ADMIN_PASSWORD
    echo "[deploy] 创建管理员"
    bash deploy/bootstrap-admin.sh
else
    echo "[deploy] 已存在管理员（$ADMIN_COUNT 个），跳过创建"
fi

if [[ "$NO_DEMO" == "1" ]]; then
    echo "[deploy] 按 --no-demo 跳过演示数据"
elif [[ "$RESET_DEMO" == "1" || "$FIRST_DEPLOY" == "1" ]]; then
    echo "[deploy] 导入演示数据"
    bash deploy/reset-demo-data.sh --yes
    wait_healthy app 180
else
    echo "[deploy] 已有业务数据，跳过演示数据重置；需要重置请加 --reset-demo"
fi

APP_PORT_VALUE="$(grep -E '^APP_PORT=' .env | tail -1 | cut -d= -f2- | tr -d '"' | tr -d "'")"
APP_PORT_VALUE="${APP_PORT_VALUE:-26000}"
SERVER_IP="$(hostname -I 2>/dev/null | awk '{print $1}' || true)"

echo
echo "[deploy] 完成"
echo "  访问地址：http://${SERVER_IP:-<服务器IP>}:${APP_PORT_VALUE}/blogin.html"
echo "  配置文件：$ROOT_DIR/.env"
echo "  查看状态：docker compose ps"
echo "  查看日志：docker compose logs -f app"
