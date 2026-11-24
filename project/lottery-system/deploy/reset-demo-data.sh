#!/usr/bin/env bash
# 重置演示数据：保留所有管理员账号，重建业务表并导入 sql/demo-data.sql。
#
# 手动执行：
#   ./deploy/reset-demo-data.sh --yes
#
# 定时执行（每天 04:00）：
#   0 4 * * * cd /opt/lottery-system && ./deploy/reset-demo-data.sh --yes >> /var/log/lottery-reset.log 2>&1
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

APP_STOPPED=0
RESET_OK=0

# 只有成功完成重置才重新启动 app；失败时保持停止状态，方便排查现场
cleanup() {
    if [[ "$APP_STOPPED" == "1" && "$RESET_OK" == "1" ]]; then
        echo "[reset] 启动应用容器"
        docker compose start app
    elif [[ "$APP_STOPPED" == "1" ]]; then
        echo "[reset] 重置失败，应用保持停止状态；修复后请重新执行本脚本"
    fi
}
trap cleanup EXIT

if [[ "${1:-}" != "--yes" && "${RESET_DEMO_CONFIRM:-}" != "yes" ]]; then
    echo "该操作会删除所有业务数据和普通用户（管理员会保留）。"
    echo "确认执行请加 --yes，或设置 RESET_DEMO_CONFIRM=yes。"
    exit 1
fi

if ! docker compose ps --status running --services | grep -qx mysql; then
    echo "MySQL 容器未运行，请先执行 docker compose up -d" >&2
    exit 1
fi

# 所有 SQL 都通过容器内 root 账号执行，密码不落到宿主机命令行
mysql_exec() {
    docker compose exec -T mysql sh -c \
        'exec mysql -N -B -uroot -p"$MYSQL_ROOT_PASSWORD" -D "$MYSQL_DATABASE"'
}

echo "[reset] 检查管理员账号"
ADMIN_COUNT="$(mysql_exec <<'SQL'
SELECT COUNT(*) FROM `user` WHERE `identity` = '0';
SQL
)"
if [[ "$ADMIN_COUNT" -lt 1 ]]; then
    echo "数据库中不存在管理员，已中止（不会创建默认密码账号）。" >&2
    echo "请先执行：ADMIN_EMAIL=... ADMIN_PHONE=... ADMIN_PASSWORD=... ./deploy/bootstrap-admin.sh" >&2
    exit 1
fi
echo "[reset] 管理员数量：$ADMIN_COUNT（重置期间保持不变）"

# 演示用户占用 9001-9020，如果管理员落在该区间会与固定 id 冲突
CONFLICT="$(mysql_exec <<'SQL'
SELECT COUNT(*) FROM `user` WHERE `identity` = '0' AND `id` BETWEEN 9001 AND 9020;
SQL
)"
if [[ "$CONFLICT" -gt 0 ]]; then
    echo "检测到管理员 id 落在演示用户区间 9001-9020，已中止重置。" >&2
    exit 1
fi

if docker compose ps --status running --services | grep -qx app; then
    echo "[reset] 停止应用容器，避免重置期间写入"
    docker compose stop app
    APP_STOPPED=1
fi

echo "[reset] 删除普通用户和业务表"
mysql_exec < sql/reset-demo-data.sql

echo "[reset] 重建表结构"
mysql_exec < sql/schema.sql

echo "[reset] 导入演示数据"
mysql_exec < sql/demo-data.sql

echo "[reset] 清理活动详情缓存"
docker compose exec -T redis sh -c '
redis-cli --scan --pattern "lottery:activity:detail:v3:*" | while read -r key; do
    redis-cli DEL "$key" >/dev/null
done
' >/dev/null

echo "[reset] 清理 RabbitMQ 中残留的抽奖消息"
docker compose exec -T rabbitmq sh -c '
for q in lottery.draw.command.queue lottery.draw.notify.queue lottery.draw.command.dlq lottery.draw.notify.dlq; do
    rabbitmqctl purge_queue "$q" >/dev/null 2>&1 || true
done
' >/dev/null

echo "[reset] 校验演示数据"
VIOLATIONS="$(mysql_exec < sql/verify-demo-data.sql)"
if [[ -n "$VIOLATIONS" ]]; then
    echo "演示数据校验失败：" >&2
    echo "$VIOLATIONS" >&2
    exit 1
fi

RESET_OK=1
echo "[reset] 完成：普通用户 20 个、奖品 6 个、活动 3 个、中奖记录 15 条"
