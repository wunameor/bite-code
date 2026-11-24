#!/usr/bin/env bash
# 首次部署时创建管理员：仅在不存在任何管理员时插入，不会覆盖现有账号和密码。
#
# 用法：
#   ADMIN_NAME=admin ADMIN_EMAIL=you@example.com ADMIN_PHONE=13800000000 \
#     ADMIN_PASSWORD='YourPass123' ./deploy/bootstrap-admin.sh
#
# 也可以在终端交互输入密码：
#   ADMIN_EMAIL=you@example.com ADMIN_PHONE=13800000000 ./deploy/bootstrap-admin.sh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if ! docker compose ps --status running --services | grep -qx mysql; then
    echo "MySQL 容器未运行，请先执行 docker compose up -d" >&2
    exit 1
fi

ADMIN_NAME="${ADMIN_NAME:-admin}"
ADMIN_EMAIL="${ADMIN_EMAIL:-}"
ADMIN_PHONE="${ADMIN_PHONE:-}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:-}"

if [[ -z "$ADMIN_EMAIL" ]]; then
    read -r -p "管理员邮箱（小写）: " ADMIN_EMAIL
fi
if [[ -z "$ADMIN_PHONE" ]]; then
    read -r -p "管理员手机号: " ADMIN_PHONE
fi
if [[ -z "$ADMIN_PASSWORD" ]]; then
    read -r -s -p "管理员密码（6-12 位字母数字）: " ADMIN_PASSWORD
    echo
fi

ADMIN_EMAIL="$(printf '%s' "$ADMIN_EMAIL" | tr '[:upper:]' '[:lower:]')"

if [[ ! "$ADMIN_EMAIL" =~ ^[a-z0-9]+([._-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+\.)+[a-z0-9]+$ ]]; then
    echo "邮箱格式不正确，需要与后端一致的小写邮箱。" >&2
    exit 1
fi
if [[ ! "$ADMIN_PHONE" =~ ^1[3-9][0-9]{9}$ ]]; then
    echo "手机号格式不正确，需要 1 开头的 11 位数字。" >&2
    exit 1
fi
if [[ ! "$ADMIN_PASSWORD" =~ ^[0-9A-Za-z]{6,12}$ ]]; then
    echo "密码格式不正确，需要 6-12 位字母或数字。" >&2
    exit 1
fi
if [[ "$ADMIN_NAME" == *"'"* || "$ADMIN_NAME" == *"\\"* || ${#ADMIN_NAME} -gt 32 ]]; then
    echo "管理员名称不能包含单引号或反斜杠，且不超过 32 个字符。" >&2
    exit 1
fi

PASSWORD_HASH="$(printf '%s' "$ADMIN_PASSWORD" | sha256sum | awk '{print $1}')"

# 密码只以 SHA-256 形式进入 SQL；手机号由 MySQL 按项目 AES key 生成密文。
RESULT="$(
docker compose exec -T mysql sh -c \
    'exec mysql -N -B -uroot -p"$MYSQL_ROOT_PASSWORD" -D "$MYSQL_DATABASE"' <<SQL
INSERT INTO \`user\` (\`user_name\`, \`email\`, \`phone_number\`, \`password\`, \`identity\`)
SELECT '${ADMIN_NAME}', '${ADMIN_EMAIL}',
       LOWER(HEX(AES_ENCRYPT('${ADMIN_PHONE}', '1234567890abcdef'))),
       '${PASSWORD_HASH}', '0'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM \`user\` WHERE \`identity\` = '0');
SELECT ROW_COUNT();
SQL
)"

if [[ "$RESULT" == "1" ]]; then
    echo "管理员已创建：$ADMIN_EMAIL"
else
    echo "已存在管理员，未做任何修改（不会重置密码）。"
fi
